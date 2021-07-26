package com.lng.lngattendancesystem.Camera.LauxandCameraService.Registration;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;
import com.luxand.FSDK;

import java.util.List;

public class RegisterFaceActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 1;
    public static float sDensity = 1.0f;
    public static String TAG = "Test";
    public static int conf;

    public static String name;
    private final String database = "Memory70.dat";
    public UserSession userSession;
    ProgressDialog progressDialog;
    private boolean mIsFailed = false;
    private FrameLayout mLayout;
    private Preview mPreview;
    private ProcessImageAndRegister mDraw;
    private boolean wasStopped = false;

    public static void alert(final Context context, final Runnable callback, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        if (callback != null) {
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    callback.run();
                }
            });
        }
        dialog.show();
    }

    public void showErrorAndClose(String error, int code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(error + ": " + code)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                })
                .show();
    }

    private void resetTrackerParameters() {
        int[] errpos = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;KeepFaceImages=false;", errpos);
        if (errpos[0] != 0) {
            showErrorAndClose("Error setting tracker parameters, position", errpos[0]);
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(RegisterFaceActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        sDensity = getResources().getDisplayMetrics().scaledDensity;
        userSession = new UserSession(RegisterFaceActivity.this);
        conf = getIntent().getExtras().getInt("type");


        name = userSession.getEmpName();
        progressDialog = Util.getProcessDialog(RegisterFaceActivity.this);
        int res = FSDK.ActivateLibrary(ConstantValues.LICENSE_KEY_7_2);
        if (res != FSDK.FSDKE_OK) {
            mIsFailed = true;
            showErrorAndClose("FaceSDK activation failed", res);
        } else {
            FSDK.Initialize();
            // Hide the window title (it is done in manifest too)
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            // Lock orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            mLayout = new FrameLayout(this);
            LayoutParams params = new LayoutParams
                    (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            mLayout.setLayoutParams(params);
            setContentView(mLayout);

            checkCameraPermissionsAndOpenCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:
                openCamera();
                break;
            default:
                break;
        }
    }

    private void checkCameraPermissionsAndOpenCamera() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                final Runnable onCloseAlert = new Runnable() {
                    @Override
                    public void run() {
                        ActivityCompat.requestPermissions(RegisterFaceActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSION_REQUEST_CODE);
                    }
                };

                alert(this, onCloseAlert, "The application processes frames from camera.");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            }
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        // Camera layer and drawing layer
        View background = new View(this);
        background.setBackgroundColor(Color.BLACK);
        mDraw = new ProcessImageAndRegister(RegisterFaceActivity.this);
        mPreview = new Preview(this, mDraw);
        //mPreview.setBackgroundColor(Color.GREEN);
        //mDraw.setBackgroundColor(Color.RED);
        mDraw.mTracker = new FSDK.HTracker();
        String templatePath = this.getApplicationInfo().dataDir + "/" + database;
        if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {
            int res = FSDK.CreateTracker(mDraw.mTracker);
            if (FSDK.FSDKE_OK != res) {
                showErrorAndClose("Error creating tracker", res);
            }
        }

        resetTrackerParameters();

        this.getWindow().setBackgroundDrawable(new ColorDrawable()); //black background

        mLayout.setVisibility(View.VISIBLE);
        addContentView(background, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addContentView(mPreview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)); //creates RegisterFaceActivity contents
        addContentView(mDraw, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View buttons = inflater.inflate(R.layout.round_background, null);
        addContentView(buttons, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

     /*

     try {
            String faceId = SplashActivity.databaseHandler.getPersistedFaceIDByEmpId("" + userSession.getEmpId());
            if (userSession.getPersistedFaceId() == null || userSession.getPersistedFaceId().equalsIgnoreCase("null")) {
                if (faceId != null) {
                    if (purgeEmployeeDetails(Integer.parseInt(faceId))) {
                        Toast.makeText(this, "Employee details updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Unable to remove existing employee details..! Try again", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    /**
     * Function to delete employee details from dat file and database
     */

    /*
    public boolean purgeEmployeeDetails(int faceID) {
        Log.i(TAG, "purgeEmployeeDetails: called");
        boolean status = false;
        int s = FSDK.SetName(mDraw.mTracker, faceID, "");
        status = FSDK.FSDKE_OK == s;
        if (status == false)
            return false;
        int d = FSDK.PurgeID(mDraw.mTracker, faceID);
        if (FSDK.FSDKE_OK == d) {
            return SplashActivity.databaseHandler.deleteEmployee(userSession.getEmpId());
        } else {
            return false;
        }
    }*/

    @Override
    protected void onStop() {
        if (mDraw != null || mPreview != null) {
            mPreview.setVisibility(View.GONE); // to destroy surface
            mLayout.setVisibility(View.GONE);
            mLayout.removeAllViews();
            mPreview.releaseCallbacks();
            mPreview = null;
            mDraw = null;
            wasStopped = true;
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (wasStopped && mDraw == null) {
            checkCameraPermissionsAndOpenCamera();

            wasStopped = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDraw != null) {
            pauseProcessingFrames();
            String templatePath = this.getApplicationInfo().dataDir + "/" + database;
            FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, templatePath);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsFailed)
            return;
        resumeProcessingFrames();
    }

    private void pauseProcessingFrames() {
        if (mDraw != null) {
            mDraw.mStopping = 1;

            // It is essential to limit wait time, because mStopped will not be set to 0, if no frames are feeded to mDraw
            for (int i = 0; i < 100; ++i) {
                if (mDraw.mStopped != 0) break;
                try {
                    Thread.sleep(10);
                } catch (Exception ex) {
                }
            }
        }
    }

    private void resumeProcessingFrames() {
        if (mDraw != null) {
            mDraw.mStopped = 0;
            mDraw.mStopping = 0;
        }
    }

}

class Preview extends SurfaceView implements SurfaceHolder.Callback {
    Context mContext;
    SurfaceHolder mHolder;
    Camera mCamera;
    ProcessImageAndRegister mDraw;
    boolean mFinished;
    boolean mIsCameraOpen = false;

    boolean mIsPreviewStarted = false;

    Preview(Context context, ProcessImageAndRegister draw) {
        super(context);
        mContext = context;
        mDraw = draw;

        //Install a SurfaceHolder.Callback so we get notified when the underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    //SurfaceView callback
    public void surfaceCreated(SurfaceHolder holder) {
        if (mIsCameraOpen) return; // surfaceCreated can be called several times
        mIsCameraOpen = true;

        mFinished = false;

        // Find the ID of the camera
        int cameraId = 0;
        boolean frontCameraFound = false;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, cameraInfo);
            //if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                frontCameraFound = true;
            }
        }

        if (frontCameraFound) {
            mCamera = Camera.open(cameraId);
        } else {
            mCamera = Camera.open();
        }

        try {
            mCamera.setPreviewDisplay(holder);

            // Preview callback used whenever new viewfinder frame is available
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if ((mDraw == null) || mFinished)
                        return;

                    if (mDraw.mYUVData == null) {
                        // Initialize the draw-on-top companion
                        Camera.Parameters params = camera.getParameters();
                        mDraw.mImageWidth = params.getPreviewSize().width;
                        mDraw.mImageHeight = params.getPreviewSize().height;
                        mDraw.mRGBData = new byte[3 * mDraw.mImageWidth * mDraw.mImageHeight];
                        mDraw.mYUVData = new byte[data.length];
                    }

                    // Pass YUV data to draw-on-top companion
                    System.arraycopy(data, 0, mDraw.mYUVData, 0, data.length);
                    mDraw.invalidate();
                }
            });
        } catch (Exception exception) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage("Cannot open camera")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .show();
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }


    public void releaseCallbacks() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
        }
        if (mHolder != null) {
            mHolder.removeCallback(this);
        }
        mDraw = null;
        mHolder = null;
    }

    //SurfaceView callback
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        mFinished = true;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        mIsCameraOpen = false;
        mIsPreviewStarted = false;
    }

    //SurfaceView callback, configuring camera
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        if (mCamera == null) return;

        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();

        //Keep uncommented to work correctly on phones:
        //This is an undocumented although widely known feature
        /**/
        if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            parameters.set("orientation", "portrait");
            mCamera.setDisplayOrientation(90); // For Android 2.2 and above
            mDraw.rotated = true;
        } else {
            parameters.set("orientation", "landscape");
            mCamera.setDisplayOrientation(0); // For Android 2.2 and above
        }
        /**/

        // choose preview size closer to 640x480 for optimal performance
        List<Camera.Size> supportedSizes = parameters.getSupportedPreviewSizes();
        int width = 0;
        int height = 0;
        for (Camera.Size s : supportedSizes) {
            if ((width - 640) * (width - 640) + (height - 480) * (height - 480) >
                    (s.width - 640) * (s.width - 640) + (s.height - 480) * (s.height - 480)) {
                width = s.width;
                height = s.height;
            }
        }

        //try to set preferred parameters
        try {
            if (width * height > 0) {
                parameters.setPreviewSize(width, height);

            }
            //parameters.setPreviewFrameRate(10);
            parameters.setSceneMode(Camera.Parameters.SCENE_MODE_PORTRAIT);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

            /**
             * Setting perticulr height and wodth of camera
             */
            //mCamera.setParameters(parameters);
        } catch (Exception ex) {
        }

        if (!mIsPreviewStarted) {
            mCamera.startPreview();
            mIsPreviewStarted = true;
        }

        parameters = mCamera.getParameters();
        Camera.Size previewSize = parameters.getPreviewSize();
        //makeResizeForCameraAspect(1.0f / ((1.0f * previewSize.width) / previewSize.height));
    }

    private void makeResizeForCameraAspect(float cameraAspectRatio) {
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();
        int matchParentWidth = this.getWidth();
        int newHeight = (int) (matchParentWidth / cameraAspectRatio);
        if (newHeight != layoutParams.height) {
            layoutParams.height = newHeight;
            layoutParams.width = matchParentWidth;
            this.setLayoutParams(layoutParams);
            this.invalidate();
        }
    }
}



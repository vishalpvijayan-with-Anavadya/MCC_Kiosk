package com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.lng.lngattendancesystem.Activities.CustomerActivities.TimerActivity;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.Models.GetDateTimeFromSerevr.GetServerDateTimeResponce;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.SerialPortCommunication.UsbService;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;
import com.luxand.FSDK;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

public class MarkAttendanceActivity extends AppCompatActivity {

    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    public static float sDensity = 1.0f;
    public static String TAG = "Mark";
    public static int conf;
    public static int EmgConf;
    public static long timeMilliSeconds;
    public static String PACKAGE_NAME;
    public static UsbService usbService;
    private final String database = "Memory70.dat";
    public UserSession userSession;
    /*************************************************************
     *FEVO Configuration
     */
    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    //Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    toastIconSucc("Device is connected");
                    userSession.setFevoDeviceStatus(true);
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(false);

                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(false);

                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(false);

                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(false);

                    break;
                case UsbService.ACTION_USB_ATTACHED: // USB NOT SUPPORTED
                    userSession.setFevoDeviceStatus(false);

                    Toast.makeText(context, "ACTION_USB_ATTACHED", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    ProgressDialog progressDialog;
    Context mContext;
    int _WAITINGTIME = 1500;
    Thread intraction;
    boolean isStop = false;


    /**
     * Created by Nagaraj 11-08-2020
     *
     * @param context
     */

    int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mIsFailed = false;
    private FrameLayout mLayout;
    private Preview mPreview;
    private ProcessAndMarkAttenance mDraw;
    private boolean wasStopped = false;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private String mLastUpdateTime;
    private ProcessAndMarkAttenance.MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
            /*
           String data = "S.MOD,1\r\n";
            if (usbService != null) { // if UsbService was correctly binded, Send data
                usbService.write(data.getBytes());
                Log.i(TAG, data + " Command Sent");
            }*/
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

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


    /************************************************************/

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
                Intent intent = new Intent(MarkAttendanceActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        sDensity = getResources().getDisplayMetrics().scaledDensity;
        userSession = new UserSession(MarkAttendanceActivity.this);
        conf = getIntent().getExtras().getInt("type");
        EmgConf = getIntent().getExtras().getInt("EMG");
        progressDialog = Util.getProcessDialog(MarkAttendanceActivity.this);
        int res = FSDK.ActivateLibrary(ConstantValues.LICENSE_KEY_7_2);
        mContext = MarkAttendanceActivity.this;

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);


        checkpermistion();


        createLocationCallback();

        createLocationRequest();

        buildLocationSettingsRequest();

        startLocationUpdates();


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
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams
                    (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mLayout.setLayoutParams(params);
            setContentView(mLayout);

            checkCameraPermissionsAndOpenCamera();


            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    getTimefromSereverApi();
                }
            }, 1000, (1000 * 60));
        }

        Calendar calendar = Calendar.getInstance();
        timeMilliSeconds = calendar.getTimeInMillis();
        intraction = new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                try {
                    Thread.sleep(1000);
                    if (calendar.getTimeInMillis() - timeMilliSeconds > (_WAITINGTIME * 1000)) {
                        Intent intent = new Intent(MarkAttendanceActivity.this, TimerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        if (!isStop) {
                            run();
                        } else {
                            Log.i("Thread", "TESTSTATES : stoped");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        intraction.start();


    }


    @Override
    public void onUserInteraction() {
        Calendar calendar = Calendar.getInstance();
        timeMilliSeconds = calendar.getTimeInMillis();
        Log.i("Time", "onUserInteraction: " + timeMilliSeconds + "  " + calendar.getTimeInMillis());
    }


    private void checkCameraPermissionsAndOpenCamera() {
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                final Runnable onCloseAlert = new Runnable() {
                    @Override
                    public void run() {
                        ActivityCompat.requestPermissions(MarkAttendanceActivity.this,
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
        mDraw = new ProcessAndMarkAttenance(MarkAttendanceActivity.this);
        mHandler = new ProcessAndMarkAttenance.MyHandler(this, mDraw.collectData);
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
        addContentView(background, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addContentView(mPreview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)); //creates MarkAttendanceActivity contents
        addContentView(mDraw, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View buttons = inflater.inflate(R.layout.round_background, null);
        addContentView(buttons, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    @Override


    protected void onStart() {
        super.onStart();
        Log.d("TAG", "TESTSTATES onStart() ");
        if (wasStopped && mDraw == null) {
            checkCameraPermissionsAndOpenCamera();
            wasStopped = false;
        }
        Calendar calendar = Calendar.getInstance();
        timeMilliSeconds = calendar.getTimeInMillis();
        intraction = new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                try {
                    Thread.sleep(1000);
                    if (calendar.getTimeInMillis() - timeMilliSeconds > (_WAITINGTIME * 1000)) {
                        Intent intent = new Intent(MarkAttendanceActivity.this, TimerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        if (!isStop) {
                            run();
                        } else {
                            Log.i("Thread", "TESTSTATES : stoped");
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        intraction.start();



    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            setFilters();  // Start listening notifications from UsbService
            startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mIsFailed)
            return;
        resumeProcessingFrames();
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("TAG", "TESTSTATES onPause() ");
        try {
            unregisterReceiver(mUsbReceiver);
            unbindService(usbConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mDraw != null) {
            pauseProcessingFrames();
            String templatePath = this.getApplicationInfo().dataDir + "/" + database;
            FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, templatePath);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("TAG", "TESTSTATES onRestart() ");
    }

    @Override
    protected void onStop() {
        Log.d("TAG", "TESTSTATES onStop() ");
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
        isStop = true;
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

    private void getTimefromSereverApi() {
        Log.i(TAG, "getTimefromSereverApi: Called");
        try {
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<GetServerDateTimeResponce> shiftdeteles = apiClient.getDateTimeFromSerever();
            shiftdeteles.enqueue(new Callback<GetServerDateTimeResponce>() {
                @Override
                public void onResponse(Call<GetServerDateTimeResponce> call, retrofit2.Response<GetServerDateTimeResponce> response) {
                    if (response.isSuccessful()) {
                        Log.i(TAG, "getTimefromSereverApi: success");
                        try {
                            // removeProgressDialog();
                            GetServerDateTimeResponce getServerDateTimeResponce = response.body();
                            Log.i("TAG", "called" + getServerDateTimeResponce.getCurrentDate());
                            userSession.setServerDateTime(getServerDateTimeResponce.getCurrentDate());
                            Log.i(TAG, "getTimefromSereverApi: success" + userSession.getServerDateAndTime());
                        } catch (Exception e) {
                            e.printStackTrace();
                            // removeProgressDialog();
                            //toastIconError(mContext.getResources().getString(R.string.No_Response_from_server_500));
                        }
                    } else {
                        //removeProgressDialog();
                        //toastIconError(mContext.getResources().getString(R.string.No_Response_from_server_500));
                    }
                }

                @Override
                public void onFailure(Call<GetServerDateTimeResponce> call, Throwable t) {
                    t.printStackTrace();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void removeProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });

    }

    public void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    if (!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                }
            }
        });

    }


    private void toastIconError(final String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void toastIconSucc(final String msg) {

        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

    }


    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            //updateUI();
            updateLocationUI();
        }
    }


    private void updateLocationUI() {
        try {
            if (mCurrentLocation != null) {
                String locationAaddress = Util.getLocationLatLongAddress(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), MarkAttendanceActivity.this);
                // set lat long

                if (String.valueOf(mCurrentLocation.getLatitude()) != null && String.valueOf(mCurrentLocation.getLongitude()) != null) {
                    userSession.setLattitude(String.valueOf(mCurrentLocation.getLatitude()));
                    userSession.setLongitude(String.valueOf(mCurrentLocation.getLongitude()));
                }

                if (locationAaddress != null) {

                    userSession.setLocationAddress(locationAaddress);
                } else {
                    userSession.setLocationAddress("No Location Data");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkpermistion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("TAG", "onRequestPermissionsResult" + requestCode);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                int granted = 0;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        granted++;
                    }
                }
                if (granted != 2) {
                    showMessage("Please provide the requried permission ");
                }
                startLocationUpdates();
                return;


            case CAMERA_PERMISSION_REQUEST_CODE:
                openCamera();
                break;
            default:
                break;


        }

    }


    public void showMessage(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        try {
                            Uri uri = Uri.fromParts("package", MarkAttendanceActivity.PACKAGE_NAME, null);
                            intent.setData(uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            Uri uri = Uri.fromParts("package", "com.lng.demoluxand", null);
                            intent.setData(uri);
                            startActivity(intent);
                            e.printStackTrace();
                        }


                    }
                })
                .setCancelable(false) // cancel with button only
                .show();
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates: ");
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.d("TAG", "Lat  settings are satisfied.");
                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(MarkAttendanceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MarkAttendanceActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        // updateUI();
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " + "location settings ");

                                Log.d("TAG", "Lat   Location settings are not satisfied. Attempting to upgrade ");

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(MarkAttendanceActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(MarkAttendanceActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        // updateUI();
                        updateLocationUI();
                    }
                });
    }

    /**************************************************/

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {


        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "startService: " + !UsbService.SERVICE_CONNECTED);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

}

class Preview extends SurfaceView implements SurfaceHolder.Callback {
    Context mContext;
    SurfaceHolder mHolder;
    Camera mCamera;
    ProcessAndMarkAttenance mDraw;
    boolean mFinished;
    boolean mIsCameraOpen = false;

    boolean mIsPreviewStarted = false;

    Preview(Context context, ProcessAndMarkAttenance draw) {
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






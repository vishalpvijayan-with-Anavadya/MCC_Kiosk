package com.lng.lngattendancesystem.Camera.LauxandCameraService.Registration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.Activities.CustomerActivities.CustomerDashBoard;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.Models.RegStatusUpdate.Regresponce;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;
import com.luxand.FSDK;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;

class ProcessImageAndRegister extends View {
    final int MAX_FACES = 5;
    final FaceRectangle[] mFacePositions = new FaceRectangle[MAX_FACES];
    final long[] mIDs = new long[MAX_FACES];
    final Lock faceLock = new ReentrantLock();
    public FSDK.HTracker mTracker;
    public UserSession userSession;
    int mTouchedIndex;
    long mTouchedID;
    int mStopping;
    int mStopped;
    boolean isRegisterShowing = false;
    boolean isMarking = false;
    boolean isAlreadyRegistered;
    Context mContext;
    Paint mPaintGreen, mPaintBlue, mPaintBlueTransparent;
    byte[] mYUVData;
    byte[] mRGBData;
    int mImageWidth, mImageHeight;
    boolean first_frame_saved;
    boolean rotated;
    ProgressDialog progressDialog;

    public ProcessImageAndRegister(Context context) {
        super(context);
        mTouchedIndex = -1;
        mStopping = 0;
        mStopped = 0;
        userSession = new UserSession(context);
        progressDialog = Util.getProcessDialog(context);
        rotated = false;
        mContext = context;
        mPaintGreen = new Paint();
        mPaintGreen.setStyle(Paint.Style.FILL);
        mPaintGreen.setColor(Color.GREEN);
        mPaintGreen.setTextSize(18 * RegisterFaceActivity.sDensity);
        mPaintGreen.setTextAlign(Paint.Align.CENTER);
        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.WHITE);
        mPaintBlue.setTextSize(18 * RegisterFaceActivity.sDensity);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintBlueTransparent = new Paint();
        mPaintBlueTransparent.setStyle(Paint.Style.STROKE);
        mPaintBlueTransparent.setStrokeWidth(2);
        mPaintBlueTransparent.setColor(Color.BLUE);
        mPaintBlueTransparent.setTextSize(25);
        mYUVData = null;
        mRGBData = null;

        first_frame_saved = false;
    }

    static public void decodeYUV420SP(byte[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int yp = 0;
        for (int j = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;

                rgb[3 * yp] = (byte) ((r >> 10) & 0xff);
                rgb[3 * yp + 1] = (byte) ((g >> 10) & 0xff);
                rgb[3 * yp + 2] = (byte) ((b >> 10) & 0xff);
                ++yp;
            }
        }
    }

    int GetFaceFrame(FSDK.FSDK_Features Features, FaceRectangle fr) {
        if (Features == null || fr == null)
            return FSDK.FSDKE_INVALID_ARGUMENT;

        float u1 = Features.features[0].x;
        float v1 = Features.features[0].y;
        float u2 = Features.features[1].x;
        float v2 = Features.features[1].y;
        float xc = (u1 + u2) / 2;
        float yc = (v1 + v2) / 2;
        int w = (int) Math.pow((u2 - u1) * (u2 - u1) + (v2 - v1) * (v2 - v1), 0.5);

        fr.x1 = (int) (xc - w * 1.6 * 0.9);
        fr.y1 = (int) (yc - w * 1.1 * 0.9);
        fr.x2 = (int) (xc + w * 1.6 * 0.9);
        fr.y2 = (int) (yc + w * 2.1 * 0.9);
        if (fr.x2 - fr.x1 > fr.y2 - fr.y1) {
            fr.x2 = fr.x1 + fr.y2 - fr.y1;
        } else {
            fr.y2 = fr.y1 + fr.x2 - fr.x1;
        }
        return 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mStopping == 1) {
            mStopped = 1;
            super.onDraw(canvas);
            return;
        }

        if (mYUVData == null || mTouchedIndex != -1) {
            super.onDraw(canvas);
            return; //nothing to process or name is being entered now
        }

        /*
        int canvasWidth = canvas.getWidth();
         */
        int canvasWidth = canvas.getWidth();
        //int canvasHeight = canvas.getHeight();

        // Convert from YUV to RGB
        decodeYUV420SP(mRGBData, mYUVData, mImageWidth, mImageHeight);

        // Load image to FaceSDK
        FSDK.HImage Image = new FSDK.HImage();
        FSDK.FSDK_IMAGEMODE imagemode = new FSDK.FSDK_IMAGEMODE();
        imagemode.mode = FSDK.FSDK_IMAGEMODE.FSDK_IMAGE_COLOR_24BIT;
        FSDK.LoadImageFromBuffer(Image, mRGBData, mImageWidth, mImageHeight, mImageWidth * 3, imagemode);
        FSDK.MirrorImage(Image, false);
        FSDK.HImage RotatedImage = new FSDK.HImage();
        FSDK.CreateEmptyImage(RotatedImage);

        //it is necessary to work with local variables (onDraw called not the time when mImageWidth,... being reassigned, so swapping mImageWidth and mImageHeight may be not safe)
        int ImageWidth = mImageWidth;
        //int ImageHeight = mImageHeight;
        if (rotated) {
            ImageWidth = mImageHeight;
            //ImageHeight = mImageWidth;
            FSDK.RotateImage90(Image, -1, RotatedImage);
        } else {
            FSDK.CopyImage(Image, RotatedImage);
        }
        FSDK.FreeImage(Image);
        long[] IDs = new long[MAX_FACES];
        long[] face_count = new long[1];

        FSDK.FeedFrame(mTracker, 0, RotatedImage, face_count, IDs);
        FSDK.FreeImage(RotatedImage);

        faceLock.lock();

        for (int i = 0; i < MAX_FACES; ++i) {
            mFacePositions[i] = new FaceRectangle();
            mFacePositions[i].x1 = 0;
            mFacePositions[i].y1 = 0;
            mFacePositions[i].x2 = 0;
            mFacePositions[i].y2 = 0;
            mIDs[i] = IDs[i];
        }

        float ratio = (canvasWidth * 1.0f) / ImageWidth;
        for (int i = 0; i < (int) face_count[0]; ++i) {
            FSDK.FSDK_Features Eyes = new FSDK.FSDK_Features();
            FSDK.GetTrackerEyes(mTracker, 0, mIDs[i], Eyes);

            GetFaceFrame(Eyes, mFacePositions[i]);
            mFacePositions[i].x1 *= ratio;
            mFacePositions[i].y1 *= ratio;
            mFacePositions[i].x2 *= ratio;
            mFacePositions[i].y2 *= ratio;
        }

        faceLock.unlock();

        int shift = (int) (22 * RegisterFaceActivity.sDensity);

        // Mark and name faces

        /**
         * face_count no of faces on frame
         */
        for (int i = 0; i < face_count[0]; ++i) {

            canvas.drawRect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2, mPaintBlueTransparent);
            boolean named = false;
            if (IDs[i] != -1) {
                String[] names = new String[1];
                FSDK.GetAllNames(mTracker, IDs[i], names, 1024);
                if (names[0] != null && names[0].length() > 0) {
                   // canvas.drawText(names[0], (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                    canvas.drawText("", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                    named = true;
                    isAlreadyRegistered = named;
                    if (!isRegisterShowing) {
                        if (!isMarking) {
                            isMarking = true;
                        }
                    }
                }
            }

            if (RegisterFaceActivity.conf == -1) {
                if (!named) {
                    canvas.drawText("Click here to Register face", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintGreen);
                }
            }
        }

        super.onDraw(canvas);
    }


    private void toastIconError(final String msg) {

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(mContext);
                toast.setDuration(Toast.LENGTH_LONG);
                //inflate view
                View custom_view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.toast_icon_text, null);
                ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
                //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
                ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));
                toast.setView(custom_view);
                toast.show();

            }
        });


    }

    private void toastIconSucc(final String msg) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(mContext);
                toast.setDuration(Toast.LENGTH_LONG);
                //inflate view
                View custom_view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.toast_icon_text, null);
                ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
                //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
                ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.usetv));
                toast.setView(custom_view);
                toast.show();

            }
        });


    }


    public String convertDateAndTimeTodisplay() {
        String setMarkedAttDateTime = new SimpleDateFormat("dd-MMM-YYYY  h:mm a", Locale.getDefault()).format(new Date());
        return setMarkedAttDateTime.toUpperCase();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//NOTE: the method can be implemented in Preview class
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                int x = (int) event.getX();
                int y = (int) event.getY();

                faceLock.lock();
                FaceRectangle[] rects = new FaceRectangle[MAX_FACES];
                long[] IDs = new long[MAX_FACES];
                for (int i = 0; i < MAX_FACES; ++i) {
                    rects[i] = new FaceRectangle();
                    rects[i].x1 = mFacePositions[i].x1;
                    rects[i].y1 = mFacePositions[i].y1;
                    rects[i].x2 = mFacePositions[i].x2;
                    rects[i].y2 = mFacePositions[i].y2;
                    IDs[i] = mIDs[i];
                }
                faceLock.unlock();

                for (int i = 0; i < MAX_FACES; ++i) {
                    if (rects[i] != null && rects[i].x1 <= x && x <= rects[i].x2 && rects[i].y1 <= y && y <= rects[i].y2 + 30) {
                        mTouchedID = IDs[i];
                        mTouchedIndex = i;

                        if (!isAlreadyRegistered) {

                            FSDK.LockID(mTracker, mTouchedID);
                            String userName = String.valueOf(userSession.getEmpId());
                            FSDK.SetName(mTracker, mTouchedID, userName);
                            if (userName.length() <= 0)
                                FSDK.PurgeID(mTracker, mTouchedID);
                            FSDK.UnlockID(mTracker, mTouchedID);
                            mTouchedIndex = -1;
                            /**
                             * On Touch add emploee details into dataase and update to server
                             * Registration
                             */
                            showProgressDialog();

                            registerEmployee(String.valueOf(mTouchedID));
                            // Call register API
                        } else {
                            toastIconError(ConstantValues.REGISTRATION_ERROR);
                            Intent intent = new Intent(getContext(), CustomerDashBoard.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getContext().startActivity(intent);
                        }
                        break;
                    }
                }
        }
        return true;
    }

    public void showProgressDialog() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
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

    public void removeProgressDialog() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
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


    public void registerEmployee(String faceId) {

        //Toast.makeText(mContext, "ID :"+faceId, Toast.LENGTH_SHORT).show();
        Log.i("Test", "registerEmployee: "+faceId);
        if (!SplashActivity.databaseHandler.isEmployeeExist(String.valueOf(userSession.getEmpId()))) {
            Log.i("Test", "registerEmployee: not Exist");
            boolean updatedStatus = SplashActivity.databaseHandler.insertEmprecords(String.valueOf(userSession.getEmpId()), userSession.getEmpName(),
                    faceId, userSession.getShiftType(),userSession.getEmpCode());

         //   Toast.makeText(mContext, updatedStatus +" : "+SplashActivity.databaseHandler.isEmployeeExist(String.valueOf(userSession.getEmpId())), Toast.LENGTH_SHORT).show();
            Log.i("Test", "registerEmployee "+updatedStatus);
            Log.i("Test", "isEmployeeExist "+SplashActivity.databaseHandler.isEmployeeExist(String.valueOf(userSession.getEmpId())));
            if (updatedStatus) {
                userSession.setPersistedFaceId(faceId);
                getCalltoUpdateRegStatus();
            } else {
                progressDialog.dismiss();
                toastIconError("Employee data is not Inserted");
            }
        } else {
            Log.i("Test", "registerEmployee: Already Exist");
            getCalltoUpdateRegStatus();
            toastIconError("Already Exist");
        }
    }

    private void getCalltoUpdateRegStatus() {
        int empId = userSession.getEmpId();
        String persistedFaceId = userSession.getPersistedFaceId();
        String empPic = userSession.getEmpPic();
        JsonObject jsonObject = getJsonObjects(empId, persistedFaceId, empPic);
        ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
        final Call<Regresponce> regResponceCall = apiClient.getRestatus(jsonObject);
        regResponceCall.enqueue(new Callback<Regresponce>() {
            @Override
            public void onResponse(Call<Regresponce> call, retrofit2.Response<Regresponce> response) {
                removeProgressDialog();
                if (response.isSuccessful()) {
                    try {
                        Regresponce regresponce = response.body();
                        if (!regresponce.getError()) {
                          /*  String filename = userSession.getCustomerCode() + "-" + userSession.getKioskNumber();
                            if (Util.backup(mContext, filename)) {
                                toastIconSucc("Backup created as " + filename);
                            }*/
                            showRegisteredDialog();
                        } else {
                            toastIconError(regresponce.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        toastIconError(mContext.getResources().getString(R.string.No_Response_from_server_500));
                    }
                } else {
                    toastIconError(mContext.getResources().getString(R.string.No_Response_from_server_500));
                }
            }

            @Override
            public void onFailure(Call<Regresponce> call, Throwable t) {
                removeProgressDialog();
                toastIconError(mContext.getResources().getString(R.string.No_Response_from_server_500));
            }
        });


    }

    public void showRegisteredDialog() {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View dialogView = inflater.inflate(R.layout.dialog_warning, null);
            TextView title = dialogView.findViewById(R.id.content);
            title.setText("You have successfully registered");
            builder.setCancelable(false)
                    .setView(dialogView);
            final android.app.AlertDialog alertDialog = builder.create();
            dialogView.findViewById(R.id.bt_close).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), CustomerDashBoard.class);
                    intent.putExtra("Backup", true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getContext().startActivity(intent);
                }
            });
            alertDialog.show();
        }catch (Exception e){

        }

    }


    public JsonObject getJsonObjects(int empId, String pesitedFaceId, String empPic) {
        try {
            JSONObject custDietailes = new JSONObject();
            custDietailes.put("empId", empId);
            custDietailes.put("empPresistedFaceId", pesitedFaceId);
            //custDietailes.put("employeePic", empPic);
            custDietailes.put("employeePic", "");
            JsonObject inputData = (JsonObject) new JsonParser().parse(custDietailes.toString());
            return inputData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

class FaceRectangle {
    public int x1, y1, x2, y2;
}


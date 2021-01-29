package com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.Activities.CustomerActivities.CustomerDashBoard;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.Models.AttendenceDetails;
import com.lng.lngattendancesystem.Models.GetDateTimeFromSerevr.GetServerDateTimeResponce;
import com.lng.lngattendancesystem.Models.MarkINModels.AttendanceMarkInReponse;
import com.lng.lngattendancesystem.Models.MarkInModule.MarkedAttendanceStatusResponce;
import com.lng.lngattendancesystem.Models.MarkOutModule.MarkAttendanceOutStatus;
import com.lng.lngattendancesystem.Models.MarkOutResponseModels.AttendanceMarkOutReponse;
import com.lng.lngattendancesystem.Models.NewAttendanceModel.SynchSuccessedData;
import com.lng.lngattendancesystem.Models.NewLogicMarkAtt.MarkAttendanceResponseNew;
import com.lng.lngattendancesystem.Models.NewLogicMarkAtt.SynchResponse.SynchedMainResponse;
import com.lng.lngattendancesystem.Models.ShiftDetails.MainShiftdetalsRespose;
import com.lng.lngattendancesystem.Models.ShiftDetails.ShiftData;
import com.lng.lngattendancesystem.Models.Status;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.SerialPortCommunication.CollectData;
import com.lng.lngattendancesystem.SerialPortCommunication.UsbService;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.DateTimeUtil;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;
import com.luxand.FSDK;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;
import static com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark.MarkAttendanceActivity.timeMilliSeconds;
import static com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark.MarkAttendanceActivity.usbService;


public class ProcessAndMarkAttenance extends View {
    public static final String TAG = "Mark";
    public static boolean isSynchingRunning = false;
    public static boolean isMarking = false;
    public static boolean isNotRegistered = false;

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
    int counter = 0;
    boolean isRegisterShowing = false;
    Context mContext;
    Paint mPaintGreen, mPaintBlue, mPaintBlueTransparent;
    byte[] mYUVData;
    byte[] mRGBData;
    int mImageWidth, mImageHeight;
    boolean first_frame_saved;
    boolean rotated;
    ProgressDialog progressDialog;
    String markedParistedFaceId1, empIDbyPersistedFace;
    double tempratureValue;
    String recognisedEmployeeID;
    boolean isBeepPlaying = false;
    boolean isErrorShowing = false;


    CollectData collectData = new CollectData() {
        @Override
        public void onDataReceived(String data) {

            if (userSession.getThermalDeviceMode() == 1) {
                removeProgressDialog();
                Log.i(TAG, "onDataReceived: " + data);
                if (data.startsWith(ConstantValues.FEVO_ERROR)) {
                    Toast.makeText(mContext, "Try Again.", Toast.LENGTH_SHORT).show();
                    removeProgressDialog();
                    isMarking = false;
                }
                if (data.contains(",") || data.startsWith("S.MOD,") || data.startsWith("1") ||
                        data.startsWith("F.T,") || data.startsWith("F.D") || data.startsWith(" ")) {
                    return;
                } else {
                    try {
                        double value = (Double.parseDouble(data) / 10.0);
                        double temp = (value * 1.8) + 32;
                        float config = (userSession.getThreshold());
                        toastIconSucc("Temp " + temp);
                        if (temp > 80 && temp < config) {
                            tempratureValue = temp;
                            showProgressDialog();
                            //  getDateTimeFromServer(recognisedEmployeeID);
                            startMarking(recognisedEmployeeID);
                        } else {
                            tempratureValue = 0;
                            showErrorMessage();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isMarking = false;
                    }
                }
            }
        }
    };

    public ProcessAndMarkAttenance(Context context) {
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
        mPaintGreen.setTextSize(18 * MarkAttendanceActivity.sDensity);
        mPaintGreen.setTextAlign(Paint.Align.CENTER);
        mPaintBlue = new Paint();
        mPaintBlue.setStyle(Paint.Style.FILL);
        mPaintBlue.setColor(Color.WHITE);
        mPaintBlue.setTextSize(18 * MarkAttendanceActivity.sDensity);
        mPaintBlue.setTextAlign(Paint.Align.CENTER);

        mPaintBlueTransparent = new Paint();
        mPaintBlueTransparent.setStyle(Paint.Style.STROKE);
        mPaintBlueTransparent.setStrokeWidth(5);
        mPaintBlueTransparent.setColor(Color.BLUE);
        mPaintBlueTransparent.setTextSize(25);
        mYUVData = null;
        mRGBData = null;

        first_frame_saved = false;
        isMarking = false;
        if (MarkAttendanceActivity.EmgConf == 4) {
            userSession.setEmergencyOut(true);
            Log.d("TAG", "TESTCASE from Emergeny");
        } else {
            Log.d("TAG", "TESTCASE im not  Emergeny");
            userSession.setEmergencyOut(false);
        }
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

        int shift = (int) (22 * MarkAttendanceActivity.sDensity);

        // Mark and name faces

        /**
         * face_count no of faces on frame
         */

        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < face_count[0]; ++i) {
            canvas.drawRect(mFacePositions[i].x1, mFacePositions[i].y1, mFacePositions[i].x2, mFacePositions[i].y2, mPaintBlueTransparent);
            boolean named = false;
            if (MarkAttendanceActivity.conf == 1) {
                Log.d("TAG", "TESTCASE conf" + MarkAttendanceActivity.conf);
                if (IDs[i] != -1) {
                    String[] names = new String[1];
                    FSDK.GetAllNames(mTracker, IDs[i], names, 1024);
                    if (names[0] != null && names[0].length() > 0) {
                        Log.d("TAG", "TESTCASE names " + names[0]);
                        canvas.drawText("", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                        //canvas.drawText("", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintBlue);
                        calendar = Calendar.getInstance();
                        timeMilliSeconds = calendar.getTimeInMillis();
                        named = true;
                        Log.d("TAG", "TESTCASE isMarking First" + isMarking);
                        if (!isRegisterShowing) {
                            if (!isMarking) {
                                isMarking = true;
                                Log.d("TAG", "TESTNEWFLOW Step1: Detected Face");
                                processAttendance(names[0]);
                                Log.d("TAG", "TESTEMPID Step1: Detected Face" + names[0]);
                            }
                        }
                    } else {
                        if (!isNotRegistered) {
                            isNotRegistered = true;
                            Log.d("TAG", "TESTCASE isNotRegistered " + isNotRegistered);
                            // showWarning(ConstantValues.UNREGISTERED_USER);
                            showEmergencyOutAndUnRegWarning("UNREG", ConstantValues.UNREGISTERED_USER);
                        }
                    }
                }
            }
            if (MarkAttendanceActivity.conf == -1) {
                if (!named) {
                    canvas.drawText("Click here to Register face", (mFacePositions[i].x1 + mFacePositions[i].x2) / 2, mFacePositions[i].y2 + shift, mPaintGreen);
                }
            }
        }

        super.onDraw(canvas);
    }


    /**
     * Start attendance from camera
     *
     * @param empID
     */


    public void processAttendance(String empID) {

        Calendar calendar = Calendar.getInstance();
        timeMilliSeconds = calendar.getTimeInMillis();
        Log.i(TAG, "processAttendance:empID   :" + empID);

        if (isMobileDataEnabled() || isWifiConnected()) {

            recognisedEmployeeID = empID;

            if (userSession.getThermalDeviceStatus()) {
                if (userSession.isFevoDeviceConnected()) {
                    Log.i("Test", "ThermalDeviceMode(): " + userSession.getThermalDeviceMode());
                    if (userSession.getThermalDeviceMode() == 1) {
                        CheckTemperature(empID);
                    } else {
                        showWarning(ConstantValues.DEVICE_MODE_MESSAGE);
                    }
                } else {
                    // showWarning(ConstantValues.DEVICE_NOT_ONNECTED);
                    showProgressDialog();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {


                    /*        if (Util.isConnected(mContext)) {
                                //showProgressDialog();

                                //getDateTimeFromServer(empID);
                                Log.d("TAG", "TESTNEWFLOW Step2 : Start Marking Attendance");
                                startMarking(empID);

                            } else {
                                isMarking = true;
                                removeProgressDialog();
                                showErrorToast(ConstantValues.NO_INTERNET_CONNECTION_AVAILABLE, mContext);
                            }*/


                            if (isMobileDataEnabled() || isWifiConnected()) {
                                //showProgressDialog();
                                //getDateTimeFromServer(empID);
                                Log.d("TAG", "TESTNEWFLOW Step2 : Start Marking Attendance");
                                startMarking(empID);

                            } else {
                                isMarking = true;
                                removeProgressDialog();
                                //showErrorToast(ConstantValues.NO_INTERNET_CONNECTION_AVAILABLE, mContext);
                                toastIconError(ConstantValues.NO_CONNECTION);
                            }


                        }
                    }).start();

                }
            } else {
                showProgressDialog();
                //getDateTimeFromServer(empID);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * isMobileDataEnabled() || isWifiConnected()
                         */

                   /*
                        if (Util.isConnected(mContext)) {
                            //showProgressDialog();
                            *//**
                         *  Modified by Nagaraj on 31-12-2020
                         *  time Api was Called First now changed it start Mark Attendance First
                         */
                            /*
                            //getDateTimeFromServer(empID);
                            Log.d("TAG", "TESTNEWFLOW Step2 : Start Marking Attendance");
                            startMarking(empID);

                        } else {
                            removeProgressDialog();
                            showErrorToast(ConstantValues.NO_INTERNET_CONNECTION_AVAILABLE, mContext);
                        }
                        */


                        if (isMobileDataEnabled() || isWifiConnected()) {
                            //showProgressDialog();
                            //getDateTimeFromServer(empID);
                            Log.d("TAG", "TESTNEWFLOW Step2 : Start Marking Attendance");
                            startMarking(empID);

                        } else {
                            isMarking = true;
                            removeProgressDialog();
                            //showErrorToast(ConstantValues.NO_INTERNET_CONNECTION_AVAILABLE, mContext);
                            toastIconError(ConstantValues.NO_CONNECTION);
                        }


                    }
                }).start();

            }
        } else {
            toastIconError(ConstantValues.NO_CONNECTION);
        }

    }

    public void CheckTemperature(String empID) {
        String data = "F.T\r\n";
        if (usbService != null) { // if UsbService was correctly binded, Send data
            usbService.write(data.getBytes());
            Log.i(TAG, data + " Command Sent");
            Toast.makeText(mContext, "Please take a your head near to the device.", Toast.LENGTH_SHORT).show();
            showProgressDialog();
        }
    }


    public void showErrorToast(final String msg, final Context mContext) {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {//NOTE: the method can be implemented in Preview class
        try {
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
                            /**
                             *  // On touch it should not go for registration so bellow code is commented
                             FSDK.LockID(mTracker, mTouchedID);
                             String userName = MarkAttendanceActivity.name;
                             FSDK.SetName(mTracker, mTouchedID, userName);
                             if (userName.length() <= 0)
                             FSDK.PurgeID(mTracker, mTouchedID);
                             FSDK.UnlockID(mTracker, mTouchedID);

                             */
                            mTouchedIndex = -1;
                            /**
                             * On Touch add employee details into database and update to server
                             * Registration
                             */
                            break;
                        }
                    }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void showProgressDialog() {
        Calendar calendar = Calendar.getInstance();
        timeMilliSeconds = calendar.getTimeInMillis();
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
        try {
            Calendar calendar = Calendar.getInstance();
            timeMilliSeconds = calendar.getTimeInMillis();
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
        } catch (Exception e) {

        }


    }

    public void showSuccessDialog1(String msg, String employeeName) {
        try {
            removeProgressDialog();
            makeAnbeep();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View dialogView = inflater.inflate(R.layout.marked, null);
            TextView value = dialogView.findViewById(R.id.msg);
            TextView name = dialogView.findViewById(R.id.name);
            name.setText("Hi, " + employeeName);
            value.setText(msg);
            builder.setCancelable(false)
                    .setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (alertDialog.isShowing()) {
                        isMarking = false;
                        Calendar calendar = Calendar.getInstance();
                        timeMilliSeconds = calendar.getTimeInMillis();
                        alertDialog.dismiss();
                    }
                }
            }, 2000); //change 5000 with a specific time you want
        } catch (Exception e) {

        }

    }


    public void showNewSuccessDialog(String empID, String msg, AttendenceDetails attendenceDetails, String attendanceOnlineOrOff) {
        try {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Calendar calendar = Calendar.getInstance();
                    timeMilliSeconds = calendar.getTimeInMillis();
                    removeProgressDialog();

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    View dialogView = inflater.inflate(R.layout.marked_dailog, null);
                    TextView emp_id = dialogView.findViewById(R.id.emp_id);
                    TextView emp_name = dialogView.findViewById(R.id.emp_name);
                    TextView emp_month_date = dialogView.findViewById(R.id.month_date);
                    TextView emp_time = dialogView.findViewById(R.id.emp_time);
                    Button type = dialogView.findViewById(R.id.type);
                    if (attendanceOnlineOrOff.equalsIgnoreCase(ConstantValues.ONLINE)) {
                        if (userSession.isEmergencyOut()) {
                            userSession.setEmergencyOut(false);
                        }
                        type.setTextColor(ContextCompat.getColor(mContext, R.color.green_500));
                        type.setText(msg);
                    } else {
                        if (userSession.isEmergencyOut()) {
                            userSession.setEmergencyOut(false);
                        }
                        type.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondary));
                        type.setText("Attendance Marked");
                    }

       /*     if (mode == 1) {
                type.setText("Office In");
                type.setTextColor(ContextCompat.getColor(mContext, R.color.green_500));
            } else if (mode == 2) {
                type.setTextColor(ContextCompat.getColor(mContext, R.color.green_500));
                type.setText("Office Out");
            } else {
                type.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondary));
                type.setText("Marked");

            }*/


                    String getempCode = SplashActivity.databaseHandler.getEmpCOde(empID);
                    Log.d(TAG, "TESTCODE getempCode: " + getempCode);

                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("E dd MMM ,h:mm a");
                    String strDate[] = formatter.format(date).split(",");
                    ImageView emplIcon = dialogView.findViewById(R.id.emp_face);
                    emp_month_date.setText(strDate[0]);
                    emp_time.setText(strDate[1]);
                    emp_id.setText(getempCode);
                    //emp_name.setText(employeeName);
                    ImageView done;
                    AnimatedVectorDrawable vd;
                    AnimatedVectorDrawableCompat avd;
                    done = dialogView.findViewById(R.id.done);
                    done.setImageDrawable(mContext.getDrawable(R.drawable.avd_done));
                    builder.setCancelable(false)
                            .setView(dialogView);
                    final android.app.AlertDialog alertDialog = builder.create();
                    Drawable drawable = done.getDrawable();
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                    if (drawable instanceof AnimatedVectorDrawableCompat) {
                        avd = (AnimatedVectorDrawableCompat) drawable;
                        avd.start();
                    } else if (drawable instanceof AnimatedVectorDrawable) {
                        vd = (AnimatedVectorDrawable) drawable;
                        vd.start();
                    }
                    alertDialog.show();
                    makeAnbeep();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (alertDialog.isShowing()) {
                                isMarking = false;
                                Calendar calendar = Calendar.getInstance();
                                timeMilliSeconds = calendar.getTimeInMillis();
                                alertDialog.dismiss();
                            }
                        }
                    }, 4000); //change 5000 with a specific time you want
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // unused For New Attendance Logic
    public void showSuccessDialog(int mode, String empId, String msg, String employeeName) {
        try {
            Calendar calendar = Calendar.getInstance();
            timeMilliSeconds = calendar.getTimeInMillis();
            Log.i(TAG, "showSuccessDialog: " + mode);
            Log.i(TAG, "showSuccessDialog: " + msg);
            removeProgressDialog();

            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View dialogView = inflater.inflate(R.layout.marked_dailog, null);
            TextView emp_id = dialogView.findViewById(R.id.emp_id);
            TextView emp_name = dialogView.findViewById(R.id.emp_name);
            TextView emp_month_date = dialogView.findViewById(R.id.month_date);
            TextView emp_time = dialogView.findViewById(R.id.emp_time);
            Button type = dialogView.findViewById(R.id.type);
            if (mode == 1) {
                type.setText("Office In");
                type.setTextColor(ContextCompat.getColor(mContext, R.color.green_500));
            } else if (mode == 2) {
                type.setTextColor(ContextCompat.getColor(mContext, R.color.green_500));
                type.setText("Office Out");
            } else {
                type.setTextColor(ContextCompat.getColor(mContext, R.color.colorSecondary));
                type.setText("Marked");

            }
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("E dd MMM ,h:mm a");
            String strDate[] = formatter.format(date).split(",");
            ImageView emplIcon = dialogView.findViewById(R.id.emp_face);
            emp_month_date.setText(strDate[0]);
            emp_time.setText(strDate[1]);
            emp_id.setText(userSession.getEmpCode());
            emp_name.setText(employeeName);
            ImageView done;
            AnimatedVectorDrawable vd;
            AnimatedVectorDrawableCompat avd;
            done = dialogView.findViewById(R.id.done);
            done.setImageDrawable(mContext.getDrawable(R.drawable.avd_done));
            builder.setCancelable(false)
                    .setView(dialogView);
            final android.app.AlertDialog alertDialog = builder.create();
            Drawable drawable = done.getDrawable();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            if (drawable instanceof AnimatedVectorDrawableCompat) {
                avd = (AnimatedVectorDrawableCompat) drawable;
                avd.start();
            } else if (drawable instanceof AnimatedVectorDrawable) {
                vd = (AnimatedVectorDrawable) drawable;
                vd.start();
            }
            alertDialog.show();
            makeAnbeep();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (alertDialog.isShowing()) {
                        isMarking = false;
                        Calendar calendar = Calendar.getInstance();
                        timeMilliSeconds = calendar.getTimeInMillis();
                        alertDialog.dismiss();
                    }
                }
            }, 4000); //change 5000 with a specific time you want
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void showWarning(String msg) {
        try {
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    timeMilliSeconds = calendar.getTimeInMillis();
                    removeProgressDialog();
                    makeWarningbeep();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    LayoutInflater inflater = LayoutInflater.from(mContext);
                    View dialogView = inflater.inflate(R.layout.warning, null);
                    TextView value = dialogView.findViewById(R.id.msg);
                    value.setText(msg);
                    builder.setCancelable(false).setView(dialogView);
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (alertDialog.isShowing()) {
                                    userSession.setEmergencyOut(false);
                                    isMarking = false;
                                    alertDialog.dismiss();

                                }
                            } catch (Exception e) {
                                isMarking = false;
                            }
                        }
                    }, 3000); //change 5000 with a specific time you want


                }
            });

        } catch (Exception e) {
            isMarking = false;
            e.printStackTrace();
        }
    }


    public void showEmergencyOutAndUnRegWarning(String type, String msg) {
        try {
            Calendar calendar = Calendar.getInstance();
            timeMilliSeconds = calendar.getTimeInMillis();
            removeProgressDialog();
            makeWarningbeep();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View dialogView = inflater.inflate(R.layout.warning, null);
            TextView value = dialogView.findViewById(R.id.msg);
            value.setText(msg);
            builder.setCancelable(false).setView(dialogView);
            final AlertDialog alertDialog = builder.create();
            alertDialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (alertDialog.isShowing()) {
                            if (type.equalsIgnoreCase("EMG")) {
                                isMarking = false;
                                Log.d("TAG", "TESTCASE isMarking Once EMG CLosed" + isMarking);
                                Intent intent = new Intent(mContext, CustomerDashBoard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                                ((CustomerDashBoard) mContext).finish();
                                alertDialog.dismiss();

                            } else {
                                isNotRegistered = false;
                          /*      Intent intent = new Intent(mContext, CustomerDashBoard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(intent);
                                ((CustomerDashBoard) mContext).finish();*/
                                alertDialog.dismiss();
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 3000); //change 5000 with a specific time you want
        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private void makeAnbeep() {
        MediaPlayer mp = MediaPlayer.create(mContext, R.raw.beep_06);
        mp.start();

    }

    private void makeWarningbeep() {
        MediaPlayer mp = MediaPlayer.create(mContext, R.raw.warning_beep);
        mp.start();

    }

    private void showErrorMessage() {
        if (!isErrorShowing) {
            isErrorShowing = true;
            MediaPlayer mp = MediaPlayer.create(mContext, R.raw.error);
            mp.start();
            try {
                removeProgressDialog();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View dialogView = inflater.inflate(R.layout.warning, null);
                TextView value = dialogView.findViewById(R.id.msg);
                value.setText(ConstantValues.NOT_PERMISSABLE_TEMPERATURE_MESSAGE);
                builder.setCancelable(false)
                        .setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (alertDialog.isShowing()) {
                                isMarking = false;
                                isErrorShowing = false;
                                Calendar calendar = Calendar.getInstance();
                                timeMilliSeconds = calendar.getTimeInMillis();
                                alertDialog.dismiss();
                            }
                        } catch (Exception e) {
                            isMarking = false;
                            isErrorShowing = false;
                        }
                    }
                }, 4000); //change 5000 with a specific time you want
            } catch (Exception e) {
                isMarking = false;
                isErrorShowing = false;
            }
        }
    }


    private void getCallNewEmergencyOut(String empID) {
        try {
            JSONObject details = new JSONObject();
            details.put("empId", empID);
            details.put("attendanceDateTime", "");
            details.put("latLong", userSession.getLattitude() + "," + userSession.getLogitude());
            details.put("address", userSession.getLocationAddress());
            details.put("attendanceMode", ConstantValues.TAB);
            details.put("empTemp", tempratureValue);
            details.put("InOrOut", "");
            details.put("attendanceId", 0);
            details.put("custId", userSession.getCustId());

            JsonObject inputData = (JsonObject) new JsonParser().parse(details.toString());
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MainShiftdetalsRespose> mainAttendanceResponse = apiClient.getShiftDetails(inputData);
            mainAttendanceResponse.enqueue(new Callback<MainShiftdetalsRespose>() {
                @Override
                public void onResponse(Call<MainShiftdetalsRespose> call, Response<MainShiftdetalsRespose> response) {
                    if (response.isSuccessful()) {
                        showNewSuccessDialog(empID, "msg", null, "online");
                    } else {
                        Log.d("TAG", "TESTCASE Step2 : Start Marking Attendance");
                        //getDateTimeFromServer(empID);
                        getLocalDateTime(empID);
                    }
                }

                @Override
                public void onFailure(Call<MainShiftdetalsRespose> call, Throwable t) {
                    t.printStackTrace();
                    // getDateTimeFromServer(empID);
                    getLocalDateTime(empID);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "TESTCASE Step3 : Failed Attendance so getting Local Time");
            // getDateTimeFromServer(empID);
            getLocalDateTime(empID);
        }


    }


    private void getCallEmergencyOut(String attendanceDate, String type, String empId) {
        try {
            JSONObject details = new JSONObject();
            details.put("refEmpId", Integer.parseInt(empIDbyPersistedFace));
            details.put("empAttendanceDate", attendanceDate);
            details.put("empAttendanceOutDatetime", "");
            details.put("empAttendanceOutConfidence", "0.0");
            details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
            details.put("empAttendanceOutLocation", userSession.getLocationAddress());
            details.put("empAttendanceOutMode", ConstantValues.TAB);

            Log.i(TAG, "getCallEmergencyOut: " + details.toString());
            // callSignOutApi(details, empId);
            markOutWithBreakShift(details, attendanceDate, empId, false, 0);

        } catch (Exception e) {
            saveFailedAttendanceDetails(empId);
        }
    }

    /**
     * Step 3: Call Mark in api to server
     *
     * @param jsonObject
     * @param attandanceDate
     * @param empId
     */
    public void callSignInApi(final JSONObject jsonObject, final String attandanceDate, final String empId) {

        Log.i(TAG, "callSignInApi: shift called ");
        try {
            JsonObject inputData = (JsonObject) new JsonParser().parse(jsonObject.toString());
            final String name = SplashActivity.databaseHandler.getEmployeeNameByEmpID(empId);

            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MarkedAttendanceStatusResponce> attendanceStatus = apiClient.getAttendanceInstatus(inputData);
            attendanceStatus.enqueue(new Callback<MarkedAttendanceStatusResponce>() {
                @Override
                public void onResponse(Call<MarkedAttendanceStatusResponce> call,
                                       retrofit2.Response<MarkedAttendanceStatusResponce> response) {
                    if (response.isSuccessful()) {
                        try {
                            MarkedAttendanceStatusResponce markedAttendanceStatusResponce = response.body();
                            if (!markedAttendanceStatusResponce.getError()) {
                                showSuccessDialog(1, empId, mContext.getResources().getString(R.string.IN_MARKED_FOR_THE_DAY), name);
                                startSyncingThread();
                            } else {
                                showWarning(markedAttendanceStatusResponce.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: response is not success");
                            saveFailedAttendanceDetails(empId);
                        }
                    } else {
                        Log.i(TAG, "onResponse: response is not success");
                        saveFailedAttendanceDetails(empId);
                    }
                }

                @Override
                public void onFailure(Call<MarkedAttendanceStatusResponce> call, Throwable t) {
                    t.printStackTrace();
                    Log.i(TAG, "onResponse: onFailure is not success");
                    saveFailedAttendanceDetails(empId);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            saveFailedAttendanceDetails(empId);
        }

    }


    public void callSynchSignInApi(final JSONObject jsonObject, final String empId) {
        try {

            JsonObject inputData = (JsonObject) new JsonParser().parse(jsonObject.toString());
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MarkedAttendanceStatusResponce> attendanceStatus = apiClient.getAttendanceInstatus(inputData);
            attendanceStatus.enqueue(new Callback<MarkedAttendanceStatusResponce>() {
                @Override
                public void onResponse(Call<MarkedAttendanceStatusResponce> call,
                                       retrofit2.Response<MarkedAttendanceStatusResponce> response) {
                    if (response.isSuccessful()) {
                        try {
                            MarkedAttendanceStatusResponce markedAttendanceStatusResponce = response.body();

                            if (!markedAttendanceStatusResponce.getError()) {
                                SplashActivity.databaseHandler.deletesynchRecord(empId);
                                Log.i(TAG, "sync: in sysch");

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<MarkedAttendanceStatusResponce> call, Throwable t) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Step 3: Call Mark Out api to server
     *
     * @param jsonObject
     * @param empId
     */
    private void callSignOutApi(final JSONObject jsonObject, final String empId) {
        Log.i(TAG, "callSignOutApi: shift called ");
        JsonObject inputData = (JsonObject) new JsonParser().parse(jsonObject.toString());
        Log.i(TAG, "callSignOutApi: called");
        try {
            final String name = SplashActivity.databaseHandler.getEmployeeNameByEmpID(empId);
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MarkAttendanceOutStatus> attendanceOutStatus = apiClient.getMarkOutStatus(inputData);
            attendanceOutStatus.enqueue(new Callback<MarkAttendanceOutStatus>() {
                @Override
                public void onResponse(Call<MarkAttendanceOutStatus> call, retrofit2.Response<MarkAttendanceOutStatus> response) {
                    if (response.isSuccessful()) {
                        try {
                            MarkAttendanceOutStatus markAttendanceOutStatus = response.body();
                            if (!markAttendanceOutStatus.getError()) {
                                showSuccessDialog(2, empId, mContext.getResources().getString(R.string.OUT_MARKED_FOR_THE_DAY), name);
                                userSession.setEmergencyOut(false);
                                startSyncingThread();
                            } else {
                                showWarning(markAttendanceOutStatus.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: 1");
                            saveFailedAttendanceDetails(empId);
                        }

                    } else {
                        Log.i(TAG, "onResponse: 2");
                        saveFailedAttendanceDetails(empId);
                    }
                }

                @Override
                public void onFailure(Call<MarkAttendanceOutStatus> call, Throwable t) {
                    t.printStackTrace();
                    Log.i(TAG, "onResponse: 3");
                    saveFailedAttendanceDetails(empId);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "onResponse: 4");
            saveFailedAttendanceDetails(empId);
        }


    }

    private void callSynchSignOutApi(final JSONObject jsonObject, final String empId) {
        JsonObject inputData = (JsonObject) new JsonParser().parse(jsonObject.toString());

        try {
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MarkAttendanceOutStatus> attendanceOutStatus = apiClient.getMarkOutStatus(inputData);
            attendanceOutStatus.enqueue(new Callback<MarkAttendanceOutStatus>() {
                @Override
                public void onResponse(Call<MarkAttendanceOutStatus> call, retrofit2.Response<MarkAttendanceOutStatus> response) {
                    if (response.isSuccessful()) {
                        try {
                            MarkAttendanceOutStatus markAttendanceOutStatus = response.body();
                            if (!markAttendanceOutStatus.getError()) {
                                SplashActivity.databaseHandler.deletesynchRecord(empId);
                            }
                            Log.i(TAG, "sync: Out sysch");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<MarkAttendanceOutStatus> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void toastIconError(final String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        isMarking = false;
    }

    private void toastIconSucc(final String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }


    private String getAttendanceDateShiftWise(String shiftType) {
        if (shiftType.trim().equalsIgnoreCase(ConstantValues.DAY_ONE_DAY_TWO)) {
            String getTimeType = getTimeTipe(userSession.getServerDateAndTime());
            String ampmValue = getTimeType.trim().substring(getTimeType.length() - 2);
            if (ampmValue.equalsIgnoreCase("AM")) {
                Date date = getParesdedDateObject(userSession.getServerDateAndTime());
                Date PriviousDateObject = getPriviousDate(date);
                String priviousDate = getPriviousconverteddateObject(PriviousDateObject);
                return priviousDate;
            } else {
                String parsedDate = getParsedDateTime(userSession.getServerDateAndTime());
                String returnCurrentdate = getonlydate(parsedDate);
                return returnCurrentdate;
            }
        } else {
            String parsedDate = getParsedDateTime(userSession.getServerDateAndTime());
            String dateObject = getonlydate(parsedDate);
            return dateObject;
        }
    }

    private String getAttendanceDateShiftWise(String shiftType, String attendanceTime) {
        if (shiftType.trim().equalsIgnoreCase(ConstantValues.DAY_ONE_DAY_TWO)) {
            String getTimeType = getTimeTipe(attendanceTime);
            String ampmValue = getTimeType.trim().substring(getTimeType.length() - 2);
            if (ampmValue.equalsIgnoreCase("AM")) {
                Date date = getParesdedDateObject(attendanceTime);
                Date PriviousDateObject = getPriviousDate(date);
                String priviousDate = getPriviousconverteddateObject(PriviousDateObject);
                return priviousDate;
            } else {
                String parsedDate = getParsedDateTime(attendanceTime);
                String returnCurrentdate = getonlydate(parsedDate);
                return returnCurrentdate;
            }
        } else {
            String parsedDate = getParsedDateTime(attendanceTime);
            String dateObject = getonlydate(parsedDate);
            return dateObject;
        }
    }

    private String getTimeTipe(String timeType) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date date1 = df.parse(timeType);
            String pattern = "yyyy-MM-dd h:mm a";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            String date = sf.format(date1);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            toastIconError("Something went wrong while converting date");
        }
        return null;


    }

    private Date getParesdedDateObject(String dateTime) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date parsedDateObject = df.parse(dateTime);
            return parsedDateObject;
        } catch (Exception e) {
            e.printStackTrace();
            toastIconError("Something went wrong while converting date");
        }
        return null;


    }

    private Date getPriviousDate(Date date) {
        int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:a");
        String currentdate = dateFormat.format(date.getTime());
        StringTokenizer tk = new StringTokenizer(currentdate);
        String todayDate = tk.nextToken();
        String currentTime = tk.nextToken();
        long gg = date.getTime() - MILLIS_IN_DAY;
        Date newDate = new Date(gg);
        return newDate;
    }

    private String getPriviousconverteddateObject(Date PriviousDateObject) {
        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            String date = sf.format(PriviousDateObject);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            toastIconError("Something went wrong while converting date");

        }
        return null;
    }

    private String getParsedDateTime(String getServerDateFrosession) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date date1 = df.parse(getServerDateFrosession);
            String pattern = "yyyy-MM-dd h:mm a";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            String date = sf.format(date1);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            toastIconError("Something went wrong while converting date");
        }
        return null;

    }

    private String getonlydate(String parsedDate) {
        try {
            String format = "yyyy-MM-dd HH:mm a";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date getDateObject = df.parse(parsedDate);
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            String date = sf.format(getDateObject);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
            toastIconError("Something went wrong while converting date");
        }
        return null;
    }

    public boolean isMobileDataEnabled() {
        boolean mobileDataEnabled = false; // Assume disabled
    ConnectivityManager cm1 = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
        Class cmClass = Class.forName(cm1.getClass().getName());
        Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
        method.setAccessible(true); // Make the method callable
        // get the setting for "mobile data"
        mobileDataEnabled = (Boolean) method.invoke(cm1);
    } catch (Exception e) {
        // Some problem accessible private API
    }
        return mobileDataEnabled;
}

    public boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi.isConnected();
        }
        return false;
    }


    /***
     * New Attendance procedure
     */
    /**
     * Step 1: Call Server API for getting  time
     */
    private void getDateTimeFromServer(final String empId) {
        try {
            Log.d("TAG", "TESTATT getDateTimeFromServer");
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<GetServerDateTimeResponce> shiftdeteles = apiClient.getDateTimeFromSerever();
            shiftdeteles.enqueue(new Callback<GetServerDateTimeResponce>() {
                @Override
                public void onResponse(Call<GetServerDateTimeResponce> call, retrofit2.Response<GetServerDateTimeResponce> response) {
                    if (response.isSuccessful()) {
                        try {
                            GetServerDateTimeResponce getServerDateTimeResponce = response.body();
                            userSession.setServerDateTime(getServerDateTimeResponce.getCurrentDate());
                            userSession.setAttendanceDate(Util.getonlydate(getServerDateTimeResponce.getCurrentDate()));
                            //startMarking(empId);
                            saveNewFailedAttendanceDetails(empId);
                        } catch (Exception e) {
                            getDateTimeFromGoogleServer(empId);
                        }
                    } else {
                        getDateTimeFromGoogleServer(empId);
                    }
                }

                @Override
                public void onFailure(Call<GetServerDateTimeResponce> call, Throwable t) {
                    getDateTimeFromGoogleServer(empId);
                }
            });
        } catch (Exception e) {
            getDateTimeFromGoogleServer(empId);
        }

    }

    /**
     * Step 1.1 : If not time from server then get from google.com
     */
    private void getDateTimeFromGoogleServer(final String empId) {
        Log.d("TAG", "TESTATT getDateTimeFromGoogleServer");
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL("https://google.com/");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(1000);
                        conn.setReadTimeout(1000);
                        conn.setDoOutput(false);
                        int responseCode = conn.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {
                            final String convertedDate = Util.getConvertTimeZone(conn.getHeaderField("Date"));
                            userSession.setServerDateTime(convertedDate);
                            userSession.setAttendanceDate(Util.getonlydate(convertedDate));
                            saveNewFailedAttendanceDetails(empId);
                            //startMarking(empId);
                        } else {
                            getLocalDateTime(empId);
                        }
                    } catch (Exception e) {
                        getLocalDateTime(empId);
                    }
                }
            }).start();
        } catch (Exception e) {
            getLocalDateTime(empId);
        }
    }

    /**
     * Step 1.2 :  If no time from even google then try to get from location
     */
    private void getLocalDateTime(String empId) {

        Log.d("TAG", "TESTATT getLocalDateTime");

        removeProgressDialog();
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locMan = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            long networkTime = locMan.getLastKnownLocation(LocationManager.NETWORK_PROVIDER).getTime();
            try {
                String NetworkTimeDate = Util.parseLongDateToString(networkTime);
                userSession.setServerDateTime(NetworkTimeDate);
                userSession.setAttendanceDate(Util.getonlydate(NetworkTimeDate));
                Log.d("TAG", "TESTATT getLocalDateTime Date" + Util.getonlydate(NetworkTimeDate));

                Log.d("TAG", "TESTATT getLocalDateTime dateTime" + NetworkTimeDate);
                saveNewFailedAttendanceDetails(empId);
                //startMarking(empId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ActivityCompat.requestPermissions(((Activity) mContext), new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA}, 2);
        }
    }

    /**
     * Step 1.3
     *
     * @param empID
     */
 /*   public void startMarking(String empID) {
        empIDbyPersistedFace = empID;
        markedParistedFaceId1 = SplashActivity.databaseHandler.getPersistedFaceIDByEmpId(empIDbyPersistedFace);
        String customerId = SplashActivity.databaseHandler.getCustomerId();
        getShiftDetailsApi(empID, customerId);
    }
    */

    /***
     *  Modified By Nagaraj on 31-12-2020
     *
     * @param empID
     */
    public void startMarking(String empID) {
        empIDbyPersistedFace = empID;
        markedParistedFaceId1 = SplashActivity.databaseHandler.getPersistedFaceIDByEmpId(empIDbyPersistedFace);
        String customerId = SplashActivity.databaseHandler.getCustomerId();
        //getShiftDetailsApi(empID, customerId);
        getFailedAttendanceToSynchFirst(empID);
    }

    private void getFailedAttendanceToSynchFirst(String empID) {
        // get Failed Attendance For this employee

        if (SplashActivity.databaseHandler.getAllFailedAttendanceOfAllEmployee() != null) {

            ArrayList<AttendenceDetails> attendanceDetailsFromDb = SplashActivity.databaseHandler.getAllFailedAttendanceOfAllEmployee();

            if (attendanceDetailsFromDb != null && attendanceDetailsFromDb.size() > 0) {
                // if Exists then Synch it first then if call suc then delete
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        if (isMobileDataEnabled() || isWifiConnected()) {
                            Log.d("TAG", "TESTATTFLOW Step2: Exixts data Synch called");
                            callSynchApi(attendanceDetailsFromDb, empID);
                        } else {
                            isMarking = true;
                            removeProgressDialog();
                            toastIconError(ConstantValues.NO_CONNECTION);
                        }
                    }
                }).start();


            } else {
                // proceed to Normal Attendance
                if (userSession.isEmergencyOut()) {
                    getCallNewEmergencyOut(empID);
                    return;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isMobileDataEnabled() || isWifiConnected()) {
                            callMarkAttendanceApi(empID);
                        } else {
                            isMarking = true;
                            removeProgressDialog();
                            //showErrorToast(ConstantValues.NO_INTERNET_CONNECTION_AVAILABLE, mContext);
                            toastIconError(ConstantValues.NO_CONNECTION);
                        }

                    }
                }).start();


            }
        } else {
            if (userSession.isEmergencyOut()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (isMobileDataEnabled() || isWifiConnected()) {
                            getCallNewEmergencyOut(empID);
                        } else {
                            isMarking = true;
                            removeProgressDialog();
                            //showErrorToast(ConstantValues.NO_INTERNET_CONNECTION_AVAILABLE, mContext);
                            toastIconError(ConstantValues.NO_CONNECTION);
                        }

                    }
                }).start();


                return;
            }


            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (isMobileDataEnabled() || isWifiConnected()) {
                        Log.d("TAG", "TESTATTFLOW Step1: Defult Start Marking Attendance");
                        callMarkAttendanceApi(empID);

                    } else {
                        isMarking = true;
                        removeProgressDialog();
                        //showErrorToast(ConstantValues.NO_INTERNET_CONNECTION_AVAILABLE, mContext);
                        toastIconError(ConstantValues.NO_CONNECTION);
                    }

                }
            }).start();


        }
    }


    private void callMarkAttendanceApi(String empId) {
        try {
            Log.d("TAG", "TESTEMPID Step2: Mark" + empId);


            JSONObject details = new JSONObject();
            details.put("attendanceId", 0);
            details.put("empId", empId);
            details.put("custId", userSession.getCustId());
            details.put("attendanceDateTime", "");
            details.put("latLong", userSession.getLattitude() + "," + userSession.getLogitude());
            details.put("address", userSession.getLocationAddress());
            details.put("attendanceMode", ConstantValues.TAB);
            details.put("empTemp", tempratureValue);
            details.put("InOrOut", "");

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(details);

            //JsonObject inputData = (JsonObject) new JsonParser().parse(details.toString());
            JsonArray inputData = (JsonArray) new JsonParser().parse(jsonArray.toString());
            Log.d(TAG, "TESTCASE INPUT: " + inputData);

            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MarkAttendanceResponseNew> mainAttendanceResponse = apiClient.getMarkAttendanceNew(inputData);
            mainAttendanceResponse.enqueue(new Callback<MarkAttendanceResponseNew>() {
                @Override
                public void onResponse(Call<MarkAttendanceResponseNew> call, Response<MarkAttendanceResponseNew> response) {
                    if (response.isSuccessful()) {
                        MarkAttendanceResponseNew markAttendanceResponseNew = response.body();
                        String msg = markAttendanceResponseNew.getStatus().getMessage();
                        if (!markAttendanceResponseNew.getStatus().getError() && markAttendanceResponseNew.getStatus().getCode() == 200) {
                            showNewSuccessDialog(empId, msg, null, "online");
                        } else {
                            //showNewSuccessDialog(msg, null, "online");
                            showWarning(msg);
                        }
                    } else {
                        Log.d("TAG", "TESTCASE Step2 : Start Marking Attendance");
                        // getDateTimeFromServer(empId);
                        // getDateTimeFromGoogleServer(empId);
                        getLocalDateTime(empId);
                    }
                }

                @Override
                public void onFailure(Call<MarkAttendanceResponseNew> call, Throwable t) {
                    t.printStackTrace();
                    // getDateTimeFromServer(empId);
                    //getDateTimeFromGoogleServer(empId);
                    getLocalDateTime(empId);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "TESTCASE Step3 : Failed Attendance so getting Local Time");
            //getDateTimeFromServer(empId);
            //getDateTimeFromGoogleServer(empId);
            getLocalDateTime(empId);
        }

    }

    private void callSynchApi(List<AttendenceDetails> attendenceDetails, String empID) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < attendenceDetails.size(); i++) {

                JSONObject details = new JSONObject();
                details.put("attendanceId", attendenceDetails.get(i).getAttendanceID());
                details.put("empId", attendenceDetails.get(i).getEmpId());
                details.put("custId", attendenceDetails.get(i).getCustId());
                details.put("attendanceDateTime", attendenceDetails.get(i).getAttendanceDateTime());
                details.put("latLong", attendenceDetails.get(i).getLatLong());
                details.put("address", attendenceDetails.get(i).getAddress());
                details.put("attendanceMode", attendenceDetails.get(i).getAttendanceMode());
                details.put("empTemp", attendenceDetails.get(i).getEmpTemp());
                details.put("InOrOut", "");
                jsonArray.put(details);

            }
            //JsonObject inputData = (JsonObject) new JsonParser().parse(details.toString());
            JsonArray inputData = (JsonArray) new JsonParser().parse(jsonArray.toString());
            Log.d(TAG, "TESTCASE INPUT1: " + inputData);

            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<SynchedMainResponse> synchSuccessedDataCall = apiClient.getSynchedData(inputData);

            synchSuccessedDataCall.enqueue(new Callback<SynchedMainResponse>() {
                @Override
                public void onResponse(Call<SynchedMainResponse> call, Response<SynchedMainResponse> response) {
                    if (response.isSuccessful()) {

                        SynchedMainResponse synchedMainResponse = response.body();

                        List<SynchSuccessedData> synchedList = new ArrayList<>();


                        try {
                            if (!synchedMainResponse.getStatus().getError() && synchedMainResponse.getStatus().getCode() == 200) {

                                if (synchedMainResponse.getOfflineAttendanceIds() != null && synchedMainResponse.getOfflineAttendanceIds().size() > 0) {

                                    for (int i = 0; i < synchedMainResponse.getOfflineAttendanceIds().size(); i++) {

                                        for (int j = 0; j < attendenceDetails.size(); j++) {

                                            if (synchedMainResponse.getOfflineAttendanceIds().get(i).getAttendanceId()
                                                    == attendenceDetails.get(j).getAttendanceID()) {
                                                // Delete The Attendance ID From The Local Table
                                                SynchSuccessedData synchSuccessedData = new SynchSuccessedData();
                                                synchSuccessedData.setAttendanceDate(attendenceDetails.get(i).getAttendanceDate());
                                                synchedList.add(synchSuccessedData);
                                            }

                                        }
                                    }
                                    deleteSynchedData(synchedList);

                                    Log.d("TAG", "TESTDEBUG ");

                                    if (userSession.isEmergencyOut()) {
                                        getCallNewEmergencyOut(empID);
                                    }
                                    callMarkAttendanceApi(empID);

                                } else {

                                    Log.d("TAG", "TESTDEBUG ");
                                    // If Synched Failed send Actual Attendance for the Employee
                                    if (userSession.isEmergencyOut()) {
                                        getCallNewEmergencyOut(empID);
                                    }
                                    callMarkAttendanceApi(empID);


                                }
                            } else {
                                Log.d("TAG", "TESTDEBUG ");
                                // If Synched Failed send Actual Attendance for the Employee
                                if (userSession.isEmergencyOut()) {
                                    getCallNewEmergencyOut(empID);
                                }
                                callMarkAttendanceApi(empID);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d("TAG", "TESTDEBUG ");
                            // If Synched Failed send Actual Attendance for the Employee

                            if (userSession.isEmergencyOut()) {
                                getCallNewEmergencyOut(empID);
                            }
                            callMarkAttendanceApi(empID);

                        }


                    } else {
                        Log.d("TAG", "TESTDEBUG ");
                        // If Synched Failed send Actual Attendance for the Employee
                        if (userSession.isEmergencyOut()) {
                            getCallNewEmergencyOut(empID);
                        }
                        callMarkAttendanceApi(empID);
                    }
                }

                @Override
                public void onFailure(Call<SynchedMainResponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("TAG", "TESTDEBUG ");
                    // If Synched Failed send Actual Attendance for the Employee

                    if (userSession.isEmergencyOut()) {
                        getCallNewEmergencyOut(empID);
                    }
                    callMarkAttendanceApi(empID);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("TAG", "TESTDEBUG ");
            // If Synched Failed send Actual Attendance for the Employee
            if (userSession.isEmergencyOut()) {
                getCallNewEmergencyOut(empID);
            }
            callMarkAttendanceApi(empID);
        }


    }


    private void deleteSynchedData(List<SynchSuccessedData> synchedData) {
        for (int i = 0; i < synchedData.size(); i++) {
            try {
                SynchSuccessedData details = synchedData.get(i);
                Date attendanceDate = Util.parseStringToDate(details.getAttendanceDate());
                Date twoDayBeforeDate = Util.last2DayDate(userSession.getServerDateAndTime());
                if (attendanceDate.before(twoDayBeforeDate)) {
                    boolean status = SplashActivity.databaseHandler.deleteAttendance(synchedData.get(i).getAttendanceDate());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void saveNewFailedAttendanceDetails(String empId) {
        Log.d("TAG", "TESTATT saveNewFailedAttendanceDetails");

        ArrayList<AttendenceDetails> attendanceDetailsFromDb = SplashActivity.databaseHandler.getAllFailedAttendanceOfThisEmployee(empId);
        if (attendanceDetailsFromDb != null) {
            if (attendanceDetailsFromDb.size() < 6) {

                Date dbDate = Util.getDateObjectForValidationIn(attendanceDetailsFromDb.get(attendanceDetailsFromDb.size() - 1).getAttendanceDateTime());


                Date currentDate = Util.getDateObjectForValidationIn(userSession.getServerDateAndTime());


                long timeInOneHourDeffrences = Util.getTimeInOnehourDefrence(currentDate, dbDate);

                if (timeInOneHourDeffrences != -1 && timeInOneHourDeffrences >= ConstantValues.MARK_DURATION) {

                    AttendenceDetails attendenceDetails = new AttendenceDetails();
                    attendenceDetails.setEmpId(Integer.parseInt(empId));
                    attendenceDetails.setAttendanceDate(userSession.getAttendanceDate());
                    attendenceDetails.setAttendanceDateTime(userSession.getServerDateAndTime());
                    attendenceDetails.setAttendanceMode(ConstantValues.TAB);
                    attendenceDetails.setLatLong(userSession.getLattitude() + "," + userSession.getLogitude());
                    attendenceDetails.setAddress(userSession.getLocationAddress());
                    attendenceDetails.setEmpTemp(tempratureValue);
                    attendenceDetails.setEmergency("0");
                    attendenceDetails.setCustId(String.valueOf(userSession.getCustId()));
                    boolean status = SplashActivity.databaseHandler.savedNewFailedAttendanceDetails(attendenceDetails);

                    if (status) {
                        showNewSuccessDialog(empId, "msg", attendenceDetails, ConstantValues.OFFLINE);
                    } else {
                        removeProgressDialog();
                        showWarning(ConstantValues.DB_INSERTION_ERROR);
                    }
                } else {
                    removeProgressDialog();
                    Date addOnehour = Util.getAddOneHour(dbDate);
                    String oneHourTimeAdded = (Util.getTimeAdd(addOnehour));
                    showWarning("Attendance can be marked post " + oneHourTimeAdded);
                }
            } else {
                removeProgressDialog();
                showWarning(ConstantValues.MORE_THEN_SIX_RECORDS);

            }
        } else {
            Log.d("TAG", "TESTATT saveNewFailedAttendanceDetails insert First Time ");

            /**
             *  Insert Failed Records For the First Time
             */
            AttendenceDetails attendenceDetails = new AttendenceDetails();
            attendenceDetails.setEmpId(Integer.parseInt(empId));
            attendenceDetails.setAttendanceDate(userSession.getAttendanceDate());
            attendenceDetails.setAttendanceDateTime(userSession.getServerDateAndTime());
            attendenceDetails.setAttendanceMode(ConstantValues.TAB);
            attendenceDetails.setLatLong(userSession.getLattitude() + "," + userSession.getLogitude());
            attendenceDetails.setAddress(userSession.getLocationAddress());
            attendenceDetails.setEmpTemp(tempratureValue);
            attendenceDetails.setEmergency("0");
            attendenceDetails.setCustId(String.valueOf(userSession.getCustId()));
            boolean status = SplashActivity.databaseHandler.savedNewFailedAttendanceDetails(attendenceDetails);

            if (status) {
                Log.d("TAG", "TESTATT saveNewFailedAttendanceDetails Show succ ");
                removeProgressDialog();
                showNewSuccessDialog(empId, "msg", attendenceDetails, ConstantValues.OFFLINE);
            } else {
                Log.d("TAG", "TESTATT saveNewFailedAttendanceDetails db Error");
                removeProgressDialog();
                showWarning(ConstantValues.DB_INSERTION_ERROR);
            }

        }


    }


    /**
     * Step 2 collect shift details
     *
     * @param empId
     * @param customerId
     */


    private void getShiftDetailsApi(final String empId, String customerId) {
        try {
            Log.i(TAG, "getShiftDetailesApi: shift called ");
            JSONObject empDetails = new JSONObject();
            empDetails.put("empId", empId);
            empDetails.put("refCustId", customerId);
            JsonObject inputData = (JsonObject) new JsonParser().parse(empDetails.toString());
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MainShiftdetalsRespose> mainShiftdetalsResposeCall = apiClient.getShiftDetails(inputData);
            mainShiftdetalsResposeCall.enqueue(new Callback<MainShiftdetalsRespose>() {
                @Override
                public void onResponse(Call<MainShiftdetalsRespose> call, retrofit2.Response<MainShiftdetalsRespose> response) {
                    if (response.isSuccessful()) {
                        try {
                            Log.i(TAG, "onResponse: shiift response came");
                            MainShiftdetalsRespose mainShiftdetalsRespose = response.body();
                            ShiftData shiftData = mainShiftdetalsRespose.getShiftData();
                            Status status = mainShiftdetalsRespose.getStatus();
                            if (status.getError()) {
                                if (status.getMessage().equalsIgnoreCase(ConstantValues.EMPLOYEE_NOT_IN_SERVICE)) {
                                    removeEmployeeDetails(empId);
                                } else {
                                    // Check AppStatusis True or false
                                 /*   if (userSession.getAppStatus()) {
                                        saveFailedAttendanceDetails(empId);
                                    } else {
                                        showWarning(mContext.getString(R.string.att_Not_Aut_marked));
                                    }*/
                                    saveFailedAttendanceDetails(empId);
                                }
                            } else {
                                userSession.setAppStatus(shiftData.isEmpAppSetupStatus());
                                userSession.setDeviceAllow(shiftData.getAttDeviceAllowed());
                                // check device Enabled or not
                                /**
                                 *  Modified By Nagaraj on26-12-2020
                                 *  when We Allow to user For Mark Only In Kiosk for K
                                 */

                                if (userSession.getDeviceAllow().equalsIgnoreCase(ConstantValues.K)) {

                                    if (shiftData.getIsBreakShift()) {
                                        Log.i(TAG, "onResponse: break shift emp");
                                        if (userSession.isEmergencyOut()) {
                                            showEmergencyOutAndUnRegWarning("EMG", mContext.getString(R.string.att_Not_Aut_Emergeny_in_Break));
                                            return;
                                        } else {
                                            breakShiftBreakHandler(empId, shiftData.getNoOfBreakShift());
                                        }
                                    } else {
                                        if (shiftData.getShiftType().equalsIgnoreCase(ConstantValues.DAY_ONE_DAY_TWO)) {
                                            String attendanceDate = getAttendanceDateShiftWise(ConstantValues.DAY_ONE_DAY_TWO).trim();
                                            if (userSession.isEmergencyOut()) {
                                                getCallEmergencyOut(attendanceDate, ConstantValues.DAY_ONE_DAY_TWO, empId);
                                                return;
                                            }
                                            Date ServerParesdTime = DateTimeUtil.getTime(userSession.getServerDateAndTime());
                                            Date outPermisiobleTime = DateTimeUtil.getTime(userSession.getServerDateAndTime().substring(0,
                                                    userSession.getServerDateAndTime().indexOf('T')) + "T" + shiftData.getOutPermissibleTime());
                                            if (ServerParesdTime.before(outPermisiobleTime)) {
                                                JSONObject details = new JSONObject();
                                                details.put("refEmpId", empId);
                                                details.put("empAttendanceDate", attendanceDate);
                                                details.put("empAttendanceInDatetime", "");
                                                details.put("empAttendanceInConfidence", "0.0");
                                                details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                details.put("empAttendanceInLocation", userSession.getLocationAddress());
                                                details.put("empAttendanceInMode", ConstantValues.TAB);
                                                if (tempratureValue == 0) {
                                                    details.put("empTemp", "NA");
                                                } else {
                                                    details.put("empTemp", tempratureValue);
                                                }
                                                //  callSignInApi(details, attendanceDate, empId);
                                                markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                            }
                                            if (ServerParesdTime.after(outPermisiobleTime)) {
                                                JSONObject details = new JSONObject();
                                                details.put("refEmpId", empId);
                                                details.put("empAttendanceDate", attendanceDate);
                                                details.put("empAttendanceOutDatetime", "");
                                                details.put("empAttendanceOutConfidence", "0.0");
                                                details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                                details.put("empAttendanceOutMode", ConstantValues.TAB);
                                                // callSignOutApi(details, empId);
                                                markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                            }
                                        } else {
                                            String attendanceDate = getAttendanceDateShiftWise(ConstantValues.DAY_ONE_DAY_ONE).trim();
                                            if (userSession.isEmergencyOut()) {
                                                getCallEmergencyOut(attendanceDate, ConstantValues.DAY_ONE_DAY_ONE, empId);
                                                return;
                                            }
                                            Date ServerParesdTime = DateTimeUtil.getTime(userSession.getServerDateAndTime());
                                            Date outPermisiobleTime = DateTimeUtil.getTime(userSession.getServerDateAndTime().substring(0,
                                                    userSession.getServerDateAndTime().indexOf('T')) + "T" + shiftData.getOutPermissibleTime());
                                            if (ServerParesdTime.before(outPermisiobleTime)) {
                                                JSONObject details = new JSONObject();
                                                details.put("refEmpId", Integer.parseInt(empId));
                                                details.put("empAttendanceDate", attendanceDate);
                                                details.put("empAttendanceInDatetime", "");
                                                details.put("empAttendanceInConfidence", "0.0");
                                                details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                details.put("empAttendanceInLocation", userSession.getLocationAddress());
                                                details.put("empAttendanceInMode", ConstantValues.TAB);
                                                if (tempratureValue == 0) {
                                                    details.put("empTemp", "NA");
                                                } else {
                                                    details.put("empTemp", tempratureValue);
                                                }
                                                //callSignInApi(details, attendanceDate, empId);
                                                markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                            }
                                            if (ServerParesdTime.after(outPermisiobleTime)) {
                                                JSONObject details = new JSONObject();
                                                details.put("refEmpId", Integer.parseInt(empId));
                                                details.put("empAttendanceDate", attendanceDate);
                                                details.put("empAttendanceOutDatetime", "");
                                                details.put("empAttendanceOutConfidence", "0.0");
                                                details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                                details.put("empAttendanceOutMode", ConstantValues.TAB);
                                                //callSignOutApi(details, empId);
                                                markOutWithBreakShift(details, attendanceDate, empId, false, 0);
                                            }

                                        }
                                    }

                                } else {
                                    /**
                                     *  Modified By Nagaraj on26-12-2020
                                     *  when We Allow to user For Mark Attendance for B Mean Bot
                                     *  Mobile And Kisok
                                     */


                                    if (userSession.getDeviceAllow().equalsIgnoreCase(ConstantValues.B)) {
                                        //showWarning(mContext.getString(R.string.att_Not_Aut_marked));

                                        if (shiftData.getIsBreakShift()) {
                                            Log.i(TAG, "onResponse: break shift emp");
                                            if (userSession.isEmergencyOut()) {
                                                showEmergencyOutAndUnRegWarning("EMG", mContext.getString(R.string.att_Not_Aut_Emergeny_in_Break));
                                                return;
                                            } else {
                                                breakShiftBreakHandler(empId, shiftData.getNoOfBreakShift());
                                            }
                                        } else {
                                            if (shiftData.getShiftType().equalsIgnoreCase(ConstantValues.DAY_ONE_DAY_TWO)) {
                                                String attendanceDate = getAttendanceDateShiftWise(ConstantValues.DAY_ONE_DAY_TWO).trim();
                                                if (userSession.isEmergencyOut()) {
                                                    getCallEmergencyOut(attendanceDate, ConstantValues.DAY_ONE_DAY_TWO, empId);
                                                    return;
                                                }
                                                Date ServerParesdTime = DateTimeUtil.getTime(userSession.getServerDateAndTime());
                                                Date outPermisiobleTime = DateTimeUtil.getTime(userSession.getServerDateAndTime().substring(0,
                                                        userSession.getServerDateAndTime().indexOf('T')) + "T" + shiftData.getOutPermissibleTime());
                                                if (ServerParesdTime.before(outPermisiobleTime)) {
                                                    JSONObject details = new JSONObject();
                                                    details.put("refEmpId", empId);
                                                    details.put("empAttendanceDate", attendanceDate);
                                                    details.put("empAttendanceInDatetime", "");
                                                    details.put("empAttendanceInConfidence", "0.0");
                                                    details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                    details.put("empAttendanceInLocation", userSession.getLocationAddress());
                                                    details.put("empAttendanceInMode", ConstantValues.TAB);
                                                    if (tempratureValue == 0) {
                                                        details.put("empTemp", "NA");
                                                    } else {
                                                        details.put("empTemp", tempratureValue);
                                                    }
                                                    //  callSignInApi(details, attendanceDate, empId);
                                                    markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                                }
                                                if (ServerParesdTime.after(outPermisiobleTime)) {
                                                    JSONObject details = new JSONObject();
                                                    details.put("refEmpId", empId);
                                                    details.put("empAttendanceDate", attendanceDate);
                                                    details.put("empAttendanceOutDatetime", "");
                                                    details.put("empAttendanceOutConfidence", "0.0");
                                                    details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                    details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                                    details.put("empAttendanceOutMode", ConstantValues.TAB);
                                                    // callSignOutApi(details, empId);
                                                    markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                                }
                                            } else {
                                                String attendanceDate = getAttendanceDateShiftWise(ConstantValues.DAY_ONE_DAY_ONE).trim();
                                                if (userSession.isEmergencyOut()) {
                                                    getCallEmergencyOut(attendanceDate, ConstantValues.DAY_ONE_DAY_ONE, empId);
                                                    return;
                                                }
                                                Date ServerParesdTime = DateTimeUtil.getTime(userSession.getServerDateAndTime());
                                                Date outPermisiobleTime = DateTimeUtil.getTime(userSession.getServerDateAndTime().substring(0,
                                                        userSession.getServerDateAndTime().indexOf('T')) + "T" + shiftData.getOutPermissibleTime());
                                                if (ServerParesdTime.before(outPermisiobleTime)) {
                                                    JSONObject details = new JSONObject();
                                                    details.put("refEmpId", Integer.parseInt(empId));
                                                    details.put("empAttendanceDate", attendanceDate);
                                                    details.put("empAttendanceInDatetime", "");
                                                    details.put("empAttendanceInConfidence", "0.0");
                                                    details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                    details.put("empAttendanceInLocation", userSession.getLocationAddress());
                                                    details.put("empAttendanceInMode", ConstantValues.TAB);
                                                    if (tempratureValue == 0) {
                                                        details.put("empTemp", "NA");
                                                    } else {
                                                        details.put("empTemp", tempratureValue);
                                                    }
                                                    //callSignInApi(details, attendanceDate, empId);
                                                    markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                                }
                                                if (ServerParesdTime.after(outPermisiobleTime)) {
                                                    JSONObject details = new JSONObject();
                                                    details.put("refEmpId", Integer.parseInt(empId));
                                                    details.put("empAttendanceDate", attendanceDate);
                                                    details.put("empAttendanceOutDatetime", "");
                                                    details.put("empAttendanceOutConfidence", "0.0");
                                                    details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                    details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                                    details.put("empAttendanceOutMode", ConstantValues.TAB);
                                                    //callSignOutApi(details, empId);
                                                    markOutWithBreakShift(details, attendanceDate, empId, false, 0);
                                                }

                                            }
                                        }

                                    } else {
                                        /**
                                         *  Modified By Nagaraj on26-12-2020
                                         *  when We Allow to user For Mark Attendance Other Then B Mean Bot
                                         *  Mobile And Kisok
                                         */

                                        if (shiftData.getIsBreakShift()) {
                                            Log.i(TAG, "onResponse: break shift emp");
                                            if (userSession.isEmergencyOut()) {
                                                showEmergencyOutAndUnRegWarning("EMG", mContext.getString(R.string.att_Not_Aut_Emergeny_in_Break));
                                                return;
                                            } else {
                                                breakShiftBreakHandler(empId, shiftData.getNoOfBreakShift());
                                            }
                                        } else {
                                            if (shiftData.getShiftType().equalsIgnoreCase(ConstantValues.DAY_ONE_DAY_TWO)) {
                                                String attendanceDate = getAttendanceDateShiftWise(ConstantValues.DAY_ONE_DAY_TWO).trim();
                                                if (userSession.isEmergencyOut()) {
                                                    getCallEmergencyOut(attendanceDate, ConstantValues.DAY_ONE_DAY_TWO, empId);
                                                    return;
                                                }
                                                Date ServerParesdTime = DateTimeUtil.getTime(userSession.getServerDateAndTime());
                                                Date outPermisiobleTime = DateTimeUtil.getTime(userSession.getServerDateAndTime().substring(0,
                                                        userSession.getServerDateAndTime().indexOf('T')) + "T" + shiftData.getOutPermissibleTime());
                                                if (ServerParesdTime.before(outPermisiobleTime)) {
                                                    JSONObject details = new JSONObject();
                                                    details.put("refEmpId", empId);
                                                    details.put("empAttendanceDate", attendanceDate);
                                                    details.put("empAttendanceInDatetime", "");
                                                    details.put("empAttendanceInConfidence", "0.0");
                                                    details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                    details.put("empAttendanceInLocation", userSession.getLocationAddress());
                                                    details.put("empAttendanceInMode", ConstantValues.TAB);
                                                    if (tempratureValue == 0) {
                                                        details.put("empTemp", "NA");
                                                    } else {
                                                        details.put("empTemp", tempratureValue);
                                                    }
                                                    //  callSignInApi(details, attendanceDate, empId);
                                                    markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                                }
                                                if (ServerParesdTime.after(outPermisiobleTime)) {
                                                    JSONObject details = new JSONObject();
                                                    details.put("refEmpId", empId);
                                                    details.put("empAttendanceDate", attendanceDate);
                                                    details.put("empAttendanceOutDatetime", "");
                                                    details.put("empAttendanceOutConfidence", "0.0");
                                                    details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                    details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                                    details.put("empAttendanceOutMode", ConstantValues.TAB);
                                                    // callSignOutApi(details, empId);
                                                    markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                                }
                                            } else {
                                                String attendanceDate = getAttendanceDateShiftWise(ConstantValues.DAY_ONE_DAY_ONE).trim();
                                                if (userSession.isEmergencyOut()) {
                                                    getCallEmergencyOut(attendanceDate, ConstantValues.DAY_ONE_DAY_ONE, empId);
                                                    return;
                                                }
                                                Date ServerParesdTime = DateTimeUtil.getTime(userSession.getServerDateAndTime());
                                                Date outPermisiobleTime = DateTimeUtil.getTime(userSession.getServerDateAndTime().substring(0,
                                                        userSession.getServerDateAndTime().indexOf('T')) + "T" + shiftData.getOutPermissibleTime());
                                                if (ServerParesdTime.before(outPermisiobleTime)) {
                                                    JSONObject details = new JSONObject();
                                                    details.put("refEmpId", Integer.parseInt(empId));
                                                    details.put("empAttendanceDate", attendanceDate);
                                                    details.put("empAttendanceInDatetime", "");
                                                    details.put("empAttendanceInConfidence", "0.0");
                                                    details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                    details.put("empAttendanceInLocation", userSession.getLocationAddress());
                                                    details.put("empAttendanceInMode", ConstantValues.TAB);
                                                    if (tempratureValue == 0) {
                                                        details.put("empTemp", "NA");
                                                    } else {
                                                        details.put("empTemp", tempratureValue);
                                                    }
                                                    //callSignInApi(details, attendanceDate, empId);
                                                    markInWithBreakShift(details, attendanceDate, empId, false, 0);
                                                }
                                                if (ServerParesdTime.after(outPermisiobleTime)) {
                                                    JSONObject details = new JSONObject();
                                                    details.put("refEmpId", Integer.parseInt(empId));
                                                    details.put("empAttendanceDate", attendanceDate);
                                                    details.put("empAttendanceOutDatetime", "");
                                                    details.put("empAttendanceOutConfidence", "0.0");
                                                    details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                                    details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                                    details.put("empAttendanceOutMode", ConstantValues.TAB);
                                                    //callSignOutApi(details, empId);
                                                    markOutWithBreakShift(details, attendanceDate, empId, false, 0);
                                                }

                                            }
                                        }
                                    }
                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            // Check AppStatusis True or false
                            if (userSession.getAppStatus()) {
                                saveFailedAttendanceDetails(empId);
                            } else {
                                showWarning(mContext.getString(R.string.att_Not_Aut_marked));
                            }
                        }
                    } else {
                        // Check AppStatusis True or false
                        if (userSession.getAppStatus()) {
                            saveFailedAttendanceDetails(empId);
                        } else {
                            showWarning(mContext.getString(R.string.att_Not_Aut_marked));
                        }
                    }
                }

                @Override
                public void onFailure(Call<MainShiftdetalsRespose> call, Throwable t) {
                    // Check AppStatusis True or false
                    if (userSession.getAppStatus()) {
                        saveFailedAttendanceDetails(empId);
                    } else {
                        showWarning(mContext.getString(R.string.att_Not_Aut_marked));
                    }
                }
            });

        } catch (Exception e) {
            // Check AppStatusis True or false
            if (userSession.getAppStatus()) {
                saveFailedAttendanceDetails(empId);
            } else {
                showWarning(mContext.getString(R.string.att_Not_Aut_marked));
            }
        }

    }

    public void saveFailedAttendanceDetails(String empId) {
        Log.i(TAG, "saveFailedAttendanceDetails: " + empId);
        if (empId == null || empId.isEmpty())
            showWarning(ConstantValues.SORRY_TRY_AGAIN);
        String emergency;
        if (userSession.isEmergencyOut()) {
            emergency = "1";
        } else {
            emergency = "0";
        }
        String attendanceDate = Util.getonlydate(userSession.getServerDateAndTime());
        if (attendanceDate == null || userSession.getServerDateAndTime() == null)
            showWarning(ConstantValues.SORRY_TRY_AGAIN);
        String name = SplashActivity.databaseHandler.getEmployeeNameByEmpID(empId);
        if (SplashActivity.databaseHandler.isAttendanceExist(empId)) {
            showWarning(ConstantValues.ATTENDACNE_ALLREADY_MARKED_FOR_THE_DAY);
        } else {
            boolean status = SplashActivity.databaseHandler.saveFailedAttendanceDetails(empId, attendanceDate, userSession.getServerDateAndTime(), emergency, String.valueOf(tempratureValue));
            if (status) {
                showSuccessDialog(3, empId, ConstantValues.ATTENDACNE_MARKED_FOR_THE_DAY, name);
            } else {
                showWarning(ConstantValues.SORRY_TRY_AGAIN);
            }
        }
        userSession.setEmergencyOut(false);
    }

    /**
     * When server is working after stoped privisously
     */
    public void startSyncingThread() {
        if (!isSynchingRunning) {
            isSynchingRunning = true;
            Thread synchFailedDetails = new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "sync started run: ");
                    ArrayList<AttendenceDetails> attendenceDetail = SplashActivity.databaseHandler.getAllFailedAttendanceDetails();
                    String customerId = SplashActivity.databaseHandler.getCustomerId();
                    if (attendenceDetail != null && attendenceDetail.size() > 0) {
                        for (int i = 0; i < attendenceDetail.size(); i++) {
                            if (attendenceDetail.get(i).getEmergency() != null && attendenceDetail.get(i).getEmergency().equalsIgnoreCase("1")) {
                                try {
                                    JSONObject details = new JSONObject();
                                    details.put("refEmpId", attendenceDetail.get(i).getRefEmpId());
                                    details.put("empAttendanceDate", attendenceDetail.get(i).getEmpAttendanceDate());
                                    details.put("empAttendanceOutDatetime", "");
                                    details.put("empAttendanceOutConfidence", "0.0");
                                    details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                    details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                    //callSynchSignOutApi(details, String.valueOf(attendenceDetail.get(i).getRefEmpId()));
                                    callSynchmarkOutWithBreakShift(details, String.valueOf(attendenceDetail.get(i).getRefEmpId()), false, 0, attendenceDetail.get(i).getEmpAttendanceDate());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                getShiftForSynch(attendenceDetail.get(i), customerId);
                            }


                        }
                        isSynchingRunning = false;
                    }
                    isSynchingRunning = false;
                }
            });
            synchFailedDetails.start();
        }
    }


    public static class MyHandler extends Handler {
        private final WeakReference<MarkAttendanceActivity> mActivity;
        CollectData cdRef;

        public MyHandler(MarkAttendanceActivity activity, CollectData cdRef) {
            mActivity = new WeakReference<>(activity);
            this.cdRef = cdRef;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    //mActivity.get().removeProgressDialog();
                    String data = (String) msg.obj;
                    Log.i(TAG, "ReceivedMessage: '" + data + "'");
                    if (data != null && !data.isEmpty() && !data.trim().isEmpty()) {
                        cdRef.onDataReceived(data);
                    }
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public void removeEmployeeDetails(final String empId) {
        try {
            removeProgressDialog();
            int faceID = Integer.parseInt(SplashActivity.databaseHandler.getPersistedFaceIDByEmpId(empId));
            boolean status = false;
            int s = FSDK.SetName(mTracker, faceID, "");
            status = FSDK.FSDKE_OK == s;
            if (status == false) {
                showWarning(ConstantValues.SORRY_TRY_AGAIN);
            }
            int d = FSDK.PurgeID(mTracker, faceID);
            if (FSDK.FSDKE_OK == d) {
                boolean deleted = SplashActivity.databaseHandler.deleteEmployee(Integer.parseInt(empId));
                if (deleted) {
                    showWarning(ConstantValues.EMPLOYEE_NOT_FOUND);
                } else {
                    showWarning(ConstantValues.SORRY_TRY_AGAIN);
                }

            } else {
                showWarning(ConstantValues.SORRY_TRY_AGAIN);
            }
            SplashActivity.databaseHandler.deleteEmployee(Integer.parseInt(empId));
            SplashActivity.databaseHandler.deletesynchRecord(empId);
        } catch (Exception e) {
            showWarning(ConstantValues.SORRY_TRY_AGAIN);
        }
    }


    /**
     * With break shift apis Non break shift and break shift api
     */

    public void markInWithBreakShift(final JSONObject jsonObject, final String attandanceDate, final String empId, boolean isBreakShift, int noOfShift) {
        Calendar calendar = Calendar.getInstance();
        timeMilliSeconds = calendar.getTimeInMillis();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            JsonArray inputData = (JsonArray) new JsonParser().parse(jsonArray.toString());
            final String name = SplashActivity.databaseHandler.getEmployeeNameByEmpID(empId);
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);

            Call<AttendanceMarkInReponse> attendanceMarkInReponseCall = apiClient.markInWithBreakShift(inputData);
            attendanceMarkInReponseCall.enqueue(new Callback<AttendanceMarkInReponse>() {
                @Override
                public void onResponse(Call<AttendanceMarkInReponse> call, Response<AttendanceMarkInReponse> response) {
                    if (response.isSuccessful()) {
                        try {
                            AttendanceMarkInReponse attendanceMarkInReponse = response.body();
                            if (!attendanceMarkInReponse.getStatus().getError()) {
                                if (isBreakShift)
                                    synchDetails(empId, 1, noOfShift, attandanceDate);
                                showSuccessDialog(1, empId, mContext.getResources().getString(R.string.IN_MARKED_FOR_THE_DAY), name);
                                startSyncingThread();
                            } else {
                                showWarning(attendanceMarkInReponse.getStatus().getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: response is not success");
                            saveFailedAttendanceDetails(empId);
                        }
                    } else {
                        Log.i(TAG, "onResponse: response is not success");
                        saveFailedAttendanceDetails(empId);
                    }

                }

                @Override
                public void onFailure(Call<AttendanceMarkInReponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.i(TAG, "onResponse: onFailure is not success");
                    saveFailedAttendanceDetails(empId);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            saveFailedAttendanceDetails(empId);
        }

    }

    public void markOutWithBreakShift(final JSONObject jsonObject, final String attandanceDate, final String empId, boolean isBreakShift, int noOfShift) {
        Calendar calendar = Calendar.getInstance();
        timeMilliSeconds = calendar.getTimeInMillis();
        try {
            Log.i(TAG, "markOutWithBreakShift: ");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            Log.i(TAG, "markOutWithBreakShift: " + jsonArray.toString());
            JsonArray inputData = (JsonArray) new JsonParser().parse(jsonArray.toString());

            final String name = SplashActivity.databaseHandler.getEmployeeNameByEmpID(empId);
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<AttendanceMarkOutReponse> attendanceMarkOutReponseCall = apiClient.markOutWithBreakShift(inputData);

            attendanceMarkOutReponseCall.enqueue(new Callback<AttendanceMarkOutReponse>() {
                @Override
                public void onResponse(Call<AttendanceMarkOutReponse> call, Response<AttendanceMarkOutReponse> response) {
                    if (response.isSuccessful()) {
                        try {
                            AttendanceMarkOutReponse attendanceMarkOutReponse = response.body();
                            if (!attendanceMarkOutReponse.getStatus().getError()) {
                                if (isBreakShift)
                                    synchDetails(empId, 2, noOfShift, attandanceDate);
                                showSuccessDialog(2, empId, mContext.getResources().getString(R.string.OUT_MARKED_FOR_THE_DAY), name);
                                userSession.setEmergencyOut(false);
                                startSyncingThread();
                            } else {
                                showWarning(attendanceMarkOutReponse.getStatus().getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "onResponse: 1");
                            saveFailedAttendanceDetails(empId);
                        }
                    } else {
                        Log.i(TAG, "onResponse: 2");
                        saveFailedAttendanceDetails(empId);
                    }
                }

                @Override
                public void onFailure(Call<AttendanceMarkOutReponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.i(TAG, "onResponse: 3");
                    saveFailedAttendanceDetails(empId);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "onResponse: 4");
            saveFailedAttendanceDetails(empId);
        }


    }

    public void breakShiftBreakHandler(String empId, int noOfShift) {
        Log.i(TAG, "breakShiftBreakHandler: " + empId + " " + noOfShift);
        String attendanceDate = Util.getonlydate(userSession.getServerDateAndTime());
        AttendenceDetails attendenceDetails = SplashActivity.databaseHandler.breakShiftAttendanceDetails(empId, attendanceDate);
        Log.i(TAG, "breakShiftBreakHandler: " + attendenceDetails);
        if (attendenceDetails == null) {
            Log.i(TAG, "breakShiftBreakHandler: attendenceDetails null");
            markIn(empId, attendanceDate, noOfShift);
        } else {
            Log.i(TAG, "breakShiftBreakHandler: attendenceDetails not null");
            if (attendenceDetails.getInSync() == noOfShift && attendenceDetails.getOutSync() == noOfShift) {
                showWarning(ConstantValues.ATTENDACNE_ALLREADY_MARKED_FOR_THE_DAY);
                Log.i(TAG, "breakShiftBreakHandler: attendenceDetails not null");
            } else if (attendenceDetails.getInSync() == noOfShift) {
                markOut(empId, attendanceDate, noOfShift);
                Log.i(TAG, "breakShiftBreakHandler: attendenceDetails getInSync() == noOfShift");
            } else if (attendenceDetails.getInSync() == attendenceDetails.getOutSync()) {
                markIn(empId, attendanceDate, noOfShift);
                Log.i(TAG, "breakShiftBreakHandler: attendenceDetails noOfShift");
            } else if (attendenceDetails.getInSync() > attendenceDetails.getOutSync()) {
                removeProgressDialog();
                showAttendanceType(empId, attendanceDate, noOfShift);
                Log.i(TAG, "breakShiftBreakHandler: attendenceDetails getInSync() > at");
            }
        }
    }

    public void markIn(String empId, String attendanceDate, int noOfShift) {
        Log.i(TAG, "markIn: ");
        try {
            JSONObject details = new JSONObject();
            details.put("refEmpId", empId);
            details.put("empAttendanceDate", attendanceDate);
            details.put("empAttendanceInDatetime", "");
            details.put("empAttendanceInConfidence", "0.0");
            details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
            details.put("empAttendanceInLocation", userSession.getLocationAddress());
            details.put("empAttendanceInLocation", userSession.getLocationAddress());
            details.put("empAttendanceInMode", ConstantValues.TAB);

            if (tempratureValue == 0) {
                details.put("empTemp", "NA");
            } else {
                details.put("empTemp", tempratureValue);
            }
            markInWithBreakShift(details, attendanceDate, empId, true, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void markOut(String empId, String attendanceDate, int noOfShift) {
        Log.i(TAG, "markOut: ");
        try {
            JSONObject details = new JSONObject();
            details.put("refEmpId", empId);
            details.put("empAttendanceDate", attendanceDate);
            details.put("empAttendanceOutDatetime", "");
            details.put("empAttendanceOutConfidence", "0.0");
            details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
            details.put("empAttendanceOutLocation", userSession.getLocationAddress());
            details.put("empAttendanceOutMode", ConstantValues.TAB);
            markOutWithBreakShift(details, attendanceDate, empId, true, noOfShift);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void synchDetails(String empId, int type, int noOfShift, String attendanceDate) {
        AttendenceDetails attendenceDetails = SplashActivity.databaseHandler.breakShiftAttendanceDetails(empId, attendanceDate);
        if (attendenceDetails == null) {
            SplashActivity.databaseHandler.insertbreakshiftdetaials(empId, attendanceDate, 1, 0);
        } else {
            if (type == 1) {
                SplashActivity.databaseHandler.updateDBInsync(attendenceDetails.getInSync() + 1, attendanceDate, empId);
            } else {
                SplashActivity.databaseHandler.updateDBOutSync(attendenceDetails.getOutSync() + 1, attendanceDate, empId);
            }
        }
    }

    private void showAttendanceType(String empid, String attendanceDate, int noOfshift) {
        Calendar calendar = Calendar.getInstance();
        timeMilliSeconds = calendar.getTimeInMillis();
        final Dialog dialog = new Dialog(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        final View dialogView = inflater.inflate(R.layout.show_att_type, null);

        TextView value = dialogView.findViewById(R.id.msg);

        dialog.setCancelable(false);
        dialog.setContentView(dialogView);


        dialogView.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProgressDialog();
                isMarking = false;
                dialog.dismiss();
            }
        });
        (dialogView.findViewById(R.id.privious_out)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showProgressDialog();
                markOut(empid, attendanceDate, noOfshift);

            }
        });
        (dialogView.findViewById(R.id.next_in)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                markIn(empid, attendanceDate, noOfshift);
            }
        });
        dialog.show();
    }

    /**
     * Offline attendance synching
     *
     * @param attendenceDetails
     * @param customerId
     */
    private void getShiftForSynch(final AttendenceDetails attendenceDetails, String customerId) {
        try {
            JSONObject empDetails = new JSONObject();
            empDetails.put("empId", attendenceDetails.getRefEmpId());
            empDetails.put("refCustId", customerId);

            JsonObject inputData = (JsonObject) new JsonParser().parse(empDetails.toString());

            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MainShiftdetalsRespose> mainShiftdetalsResposeCall = apiClient.getShiftDetails(inputData);
            mainShiftdetalsResposeCall.enqueue(new Callback<MainShiftdetalsRespose>() {
                @Override
                public void onResponse(Call<MainShiftdetalsRespose> call, retrofit2.Response<MainShiftdetalsRespose> response) {
                    if (response.isSuccessful()) {
                        try {
                            MainShiftdetalsRespose mainShiftdetalsRespose = response.body();
                            ShiftData shiftData = mainShiftdetalsRespose.getShiftData();
                            Status status = mainShiftdetalsRespose.getStatus();
                            if (!status.getError()) {
                                if (shiftData.getIsBreakShift()) {
                                    Log.i(TAG, "onResponse: break shift emp");
                                    synchbreakShiftBreakHandler(attendenceDetails, shiftData.getNoOfBreakShift());
                                } else {
                                    if (shiftData.getShiftType().equalsIgnoreCase(ConstantValues.DAY_ONE_DAY_TWO)) {
                                        String attendanceDate = getAttendanceDateShiftWise(ConstantValues.DAY_ONE_DAY_TWO, attendenceDetails.getEmpAttendanceDateTime()).trim();
                                        Date ServerParesdTime = DateTimeUtil.getTime(attendenceDetails.getEmpAttendanceDateTime());
                                        Date outPermisiobleTime = DateTimeUtil.getTime(attendenceDetails.getEmpAttendanceDateTime().substring(0,
                                                attendenceDetails.getEmpAttendanceDateTime().indexOf('T')) + "T" + shiftData.getOutPermissibleTime());
                                        if (ServerParesdTime.before(outPermisiobleTime)) {
                                            JSONObject details = new JSONObject();
                                            details.put("refEmpId", attendenceDetails.getRefEmpId());
                                            details.put("empAttendanceDate", attendenceDetails.getEmpAttendanceDate());
                                            details.put("empAttendanceInDatetime", attendenceDetails.getEmpAttendanceDateTime());
                                            details.put("empAttendanceInConfidence", "0.0");
                                            details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                            details.put("empAttendanceInLocation", userSession.getLocationAddress());

                                            //callSynchSignInApi(details, String.valueOf(attendenceDetails.getRefEmpId()));
                                            callSynchmarkInWithBreakShift(details, String.valueOf(attendenceDetails.getRefEmpId()), false, 0, attendenceDetails.getEmpAttendanceDate());
                                        }
                                        if (ServerParesdTime.after(outPermisiobleTime)) {

                                            JSONObject details = new JSONObject();
                                            details.put("refEmpId", attendenceDetails.getRefEmpId());
                                            details.put("empAttendanceDate", attendanceDate);
                                            details.put("empAttendanceOutDatetime", "");
                                            details.put("empAttendanceOutConfidence", "0.0");
                                            details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                            details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                            //callSynchSignOutApi(details, String.valueOf(attendenceDetails.getRefEmpId()));
                                            callSynchmarkOutWithBreakShift(details, String.valueOf(attendenceDetails.getRefEmpId()), false, 0, attendenceDetails.getEmpAttendanceDate());
                                        }


                                    } else {
                                        String attendanceDate = getAttendanceDateShiftWise(ConstantValues.DAY_ONE_DAY_ONE, attendenceDetails.getEmpAttendanceDateTime()).trim();
                                        Date ServerParesdTime = DateTimeUtil.getTime(attendenceDetails.getEmpAttendanceDateTime());
                                        Date outPermisiobleTime = DateTimeUtil.getTime(attendenceDetails.getEmpAttendanceDateTime().substring(0,
                                                attendenceDetails.getEmpAttendanceDateTime().indexOf('T')) + "T" + shiftData.getOutPermissibleTime());
                                        if (ServerParesdTime.before(outPermisiobleTime)) {
                                            JSONObject details = new JSONObject();
                                            details.put("refEmpId", Integer.parseInt(String.valueOf(attendenceDetails.getRefEmpId())));
                                            details.put("empAttendanceDate", attendanceDate);
                                            details.put("empAttendanceInDatetime", attendenceDetails.getEmpAttendanceDateTime());
                                            details.put("empAttendanceInConfidence", "0.0");
                                            details.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                            details.put("empAttendanceInLocation", userSession.getLocationAddress());
                                            if (tempratureValue == 0) {
                                                details.put("empTemp", "NA");
                                            } else {
                                                details.put("empTemp", tempratureValue);
                                            }
                                            //callSynchSignInApi(details, String.valueOf(attendenceDetails.getRefEmpId()));
                                            callSynchmarkInWithBreakShift(details, String.valueOf(attendenceDetails.getRefEmpId()), false, 0, attendanceDate);
                                        }
                                        if (ServerParesdTime.after(outPermisiobleTime)) {

                                            JSONObject details = new JSONObject();
                                            details.put("refEmpId", attendenceDetails.getRefEmpId());
                                            details.put("empAttendanceDate", attendanceDate);
                                            details.put("empAttendanceOutDatetime", attendenceDetails.getEmpAttendanceDateTime());
                                            details.put("empAttendanceOutConfidence", "0.0");
                                            details.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                                            details.put("empAttendanceOutLocation", userSession.getLocationAddress());
                                            //callSynchSignOutApi(details, String.valueOf(attendenceDetails.getRefEmpId()));
                                            callSynchmarkOutWithBreakShift(details, String.valueOf(attendenceDetails.getRefEmpId()), false, 0, attendanceDate);
                                        }

                                    }
                                }

                            } else {
                                if (status.getMessage().equalsIgnoreCase(ConstantValues.EMPLOYEE_NOT_IN_SERVICE)) {
                                    removeEmployeeDetails(String.valueOf(attendenceDetails.getRefEmpId()));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }

                @Override
                public void onFailure(Call<MainShiftdetalsRespose> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void synchbreakShiftBreakHandler(AttendenceDetails details, int noOfShift) {
        try {
            String empId = String.valueOf(details.getRefEmpId());
            Log.i(TAG, "synchbreakShiftBreakHandler: " + empId + " " + noOfShift);
            String attendanceDate = Util.getonlydate(userSession.getServerDateAndTime());
            AttendenceDetails attendenceDetails = SplashActivity.databaseHandler.breakShiftAttendanceDetails(empId, attendanceDate);
            Log.i(TAG, "synchbreakShiftBreakHandler: " + attendenceDetails);
            if (attendenceDetails == null) {
                Log.i(TAG, "synchbreakShiftBreakHandler: attendenceDetails null");
                JSONObject atdetails = new JSONObject();
                atdetails.put("refEmpId", details.getRefEmpId());
                atdetails.put("empAttendanceDate", details.getEmpAttendanceDate());
                atdetails.put("empAttendanceInDatetime", details.getEmpAttendanceDateTime());
                atdetails.put("empAttendanceInConfidence", "0.0");
                atdetails.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                atdetails.put("empAttendanceInLocation", userSession.getLocationAddress());
                callSynchmarkInWithBreakShift(atdetails, empId, true, noOfShift, details.getEmpAttendanceDate());
            } else {
                Log.i(TAG, "synchbreakShiftBreakHandler: attendenceDetails not null");
                if (attendenceDetails.getInSync() == noOfShift && attendenceDetails.getOutSync() == noOfShift) {
                    Log.i(TAG, "already marked for the day");
                    return;
                } else if (attendenceDetails.getInSync() == noOfShift) {
                    Log.i(TAG, "already marked for the day" + (attendenceDetails.getInSync() == noOfShift));
                    JSONObject atdetails = new JSONObject();
                    atdetails.put("refEmpId", attendenceDetails.getRefEmpId());
                    atdetails.put("empAttendanceDate", attendanceDate);
                    atdetails.put("empAttendanceOutDatetime", "");
                    atdetails.put("empAttendanceOutConfidence", "0.0");
                    atdetails.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                    atdetails.put("empAttendanceOutLocation", userSession.getLocationAddress());
                    callSynchmarkOutWithBreakShift(atdetails, empId, true, noOfShift, details.getEmpAttendanceDate());
                } else if (attendenceDetails.getInSync() == attendenceDetails.getOutSync()) {
                    Log.i(TAG, "synchbreakShiftBreakHandler: attendenceDetails not null" + (attendenceDetails.getInSync() == attendenceDetails.getOutSync()));
                    JSONObject atdetails = new JSONObject();
                    atdetails.put("refEmpId", details.getRefEmpId());
                    atdetails.put("empAttendanceDate", details.getEmpAttendanceDate());
                    atdetails.put("empAttendanceInDatetime", details.getEmpAttendanceDateTime());
                    atdetails.put("empAttendanceInConfidence", "0.0");
                    atdetails.put("empAttendanceInLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                    atdetails.put("empAttendanceInLocation", userSession.getLocationAddress());
                    callSynchmarkInWithBreakShift(atdetails, empId, true, noOfShift, details.getEmpAttendanceDate());
                } else if (attendenceDetails.getInSync() > attendenceDetails.getOutSync()) {
                    Log.i(TAG, "synchbreakShiftBreakHandler: attendenceDetails not null" + (attendenceDetails.getInSync() > attendenceDetails.getOutSync()));
                    JSONObject atdetails = new JSONObject();
                    atdetails.put("refEmpId", attendenceDetails.getRefEmpId());
                    atdetails.put("empAttendanceDate", attendanceDate);
                    atdetails.put("empAttendanceOutDatetime", "");
                    atdetails.put("empAttendanceOutConfidence", "0.0");
                    atdetails.put("empAttendanceOutLatLong", userSession.getLattitude() + "," + userSession.getLogitude());
                    atdetails.put("empAttendanceOutLocation", userSession.getLocationAddress());
                    callSynchmarkOutWithBreakShift(atdetails, empId, true, noOfShift, details.getEmpAttendanceDate());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callSynchmarkInWithBreakShift(final JSONObject jsonObject, final String empId, boolean isBreakShift, int noOfShift, String attandanceDate) {
        Log.i(TAG, "synchbreakShiftBreakHandler: callSynchmarkInWithBreakShift");
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            JsonArray inputData = (JsonArray) new JsonParser().parse(jsonArray.toString());
            final String name = SplashActivity.databaseHandler.getEmployeeNameByEmpID(empId);
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<AttendanceMarkInReponse> attendanceMarkInReponseCall = apiClient.markInWithBreakShift(inputData);
            attendanceMarkInReponseCall.enqueue(new Callback<AttendanceMarkInReponse>() {
                @Override
                public void onResponse(Call<AttendanceMarkInReponse> call, Response<AttendanceMarkInReponse> response) {
                    if (response.isSuccessful()) {
                        try {
                            AttendanceMarkInReponse attendanceMarkInReponse = response.body();
                            if (!attendanceMarkInReponse.getStatus().getError()) {
                                Log.d("TAG", "TESTFLOW getError false ");
                                if (isBreakShift) {
                                    synchDetails(empId, 1, noOfShift, attandanceDate);
                                }
                                boolean res = SplashActivity.databaseHandler.deletesynchRecord(empId);
                                Log.d("TAG", "TESTFLOW DElete Status " + res);
                            } else {
                                Log.d("TAG", "TESTFLOW getError true " + attendanceMarkInReponse.getStatus().getMessage());
                            }
                        } catch (Exception e) {
                            Log.d("TAG", "TESTFLOW Exception inner ex ");
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("TAG", "TESTFLOW Failed response ");
                    }

                }

                @Override
                public void onFailure(Call<AttendanceMarkInReponse> call, Throwable t) {
                    Log.d("TAG", "TESTFLOW Throwable t " + t);
                }
            });

        } catch (Exception e) {
            Log.d("TAG", "TESTFLOW Exception e " + e);
            e.printStackTrace();
        }

    }

    private void callSynchmarkOutWithBreakShift(final JSONObject jsonObject, final String empId, boolean isBreakShift, int noOfShift, String attandanceDate) {
        Log.i(TAG, "synchbreakShiftBreakHandler: callSynchmarkOutWithBreakShift");

        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            JsonArray inputData = (JsonArray) new JsonParser().parse(jsonArray.toString());

            final String name = SplashActivity.databaseHandler.getEmployeeNameByEmpID(empId);
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<AttendanceMarkOutReponse> attendanceMarkOutReponseCall = apiClient.markOutWithBreakShift(inputData);

            attendanceMarkOutReponseCall.enqueue(new Callback<AttendanceMarkOutReponse>() {
                @Override
                public void onResponse(Call<AttendanceMarkOutReponse> call, Response<AttendanceMarkOutReponse> response) {
                    if (response.isSuccessful()) {
                        try {
                            AttendanceMarkOutReponse attendanceMarkOutReponse = response.body();
                            if (!attendanceMarkOutReponse.getStatus().getError()) {
                                SplashActivity.databaseHandler.deletesynchRecord(empId);
                                if (isBreakShift)
                                    synchDetails(empId, 2, noOfShift, attandanceDate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    }
                }

                @Override
                public void onFailure(Call<AttendanceMarkOutReponse> call, Throwable t) {
                    t.printStackTrace();
                    Log.i(TAG, "onResponse: 3");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "onResponse: 4");
        }


    }


}

class FaceRectangle {
    public int x1, y1, x2, y2;
}


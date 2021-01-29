package com.lng.lngattendancesystem.Activities.FaceRegisterActivities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.Activities.CustomerActivities.CustomerDashBoard;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.Camera.LauxandCameraService.Registration.RegisterFaceActivity;
import com.lng.lngattendancesystem.Models.RegStatusUpdate.Regresponce;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.getCustomerAllDitailes;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.UserSession;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class FaceRegister extends AppCompatActivity {
    static final int CAPTURE_IMAGE_REQUEST = 1;
    private static final int REQUEST_WRITE_PERMISSION = 20;
    private final int MY_PERMISSIONS_REQUEST_USE_CAMERA = 0x00AF;
    AppCompatButton register;
    UserSession userSession;
    ProgressDialog progressDialog;
    ImageView image, back_arrow;
    Bundle extras;
    View view;
    byte[] imageInByte;
    String KEY, VALUE;
    private TextView customerName, branchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_register);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(FaceRegister.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        userSession = new UserSession(FaceRegister.this);
        progressDialog = new ProgressDialog(FaceRegister.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.progressbarmsg));
        progressDialog.setCancelable(false);
        initUicomponent();

    }

    private void initUicomponent() {
        register = findViewById(R.id.continue_id);
        image = findViewById(R.id.profile_view);
        back_arrow = findViewById(R.id.back);
        customerName = findViewById(R.id.tv_company_name);
        branchName = findViewById(R.id.tv_customer_code);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registereEmployeeFace();
                //OpenCameratoTakepicture();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registereEmployeeFace();
                //OpenCameratoTakepicture();
                // DvalidateFace();
            }
        });
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
        changeCustomerDetailes();
    }

    private void validateFace() {
        if (extras == null) {
            toastIconError(getString(R.string.frf_captureFace));
            return;
        } else {
            progressDialog.show();
            sentBytetoAzure(imageInByte);
        }
    }

    public void changeCustomerDetailes() {
        if (SplashActivity.databaseHandler == null)
            return;
        List<getCustomerAllDitailes> cutsomerDetalesList = SplashActivity.databaseHandler.getCustomerAllrecords();

        if (cutsomerDetalesList != null) {
            customerName.setText(cutsomerDetalesList.get(0).getCustomerName());
            branchName.setText(userSession.getBranchCode());
        } else {
            toastIconError("Sorry Customer Detailes not found!");
            return;

        }

    }


    public void sentBytetoAzure(final byte[] imageInByte) {
        final String brCode = SplashActivity.databaseHandler.getEmployeeGroupFacelist().toLowerCase();
        final byte[] byteImage = imageInByte;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    MediaType type = MediaType.parse("application/octet-stream; charset=utf-8");
                    RequestBody body = RequestBody.create(type, imageInByte);
                    KEY = getString(R.string.AZURE_SUBSCRIPTION_KEY).trim();
                    VALUE = getString(R.string.AZURE_SUBSCRIPTION_VALUE).trim();
                    Request request = new Request.Builder()
                            .url(getString(R.string.AZURE_BASE_URL) + brCode + getString(R.string.AZURE_DESTINATION_URL))
                            .addHeader(KEY, VALUE)
                            .post(body).build();
                    Response response = client.newCall(request).execute();
                    String responce = response.body().string();
                    if (responce == null) {
                        toastIconError(getString(R.string.could_not_recognise));
                        return;
                    }
                    if (response.isSuccessful()) {
                        MediaType type1 = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body1 = RequestBody.create(type1, "");
                        Request request1 = new Request.Builder()
                                .url(getString(R.string.AZURE_BASE_URL) + brCode + "/train")
                                .addHeader(KEY, VALUE)
                                .post(body1).build();
                        Response response1 = client.newCall(request1).execute();
                        if (!response1.isSuccessful()) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            JSONObject jsonObject = new JSONObject(response1.body().string());
                            String msg = jsonObject.getJSONObject("error").getString("message");
                            toastIconError(getString(R.string.face_list_notfound));
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(responce);
                            final String userParsistedFaceId = jsonObject.getString("persistedFaceId");
                            Log.i("TAG", "userParsistedFaceId" + userParsistedFaceId);
                            userSession.setPersistedFaceId(userParsistedFaceId);
                            userSession.setEmpPic(getConverTobase64(byteImage));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!SplashActivity.databaseHandler.isEmployeeExist(String.valueOf(userSession.getEmpId()))) {
                                        boolean updatedStatus = SplashActivity.databaseHandler.insertEmprecords(String.valueOf(userSession.getEmpId()), userSession.getEmpName(),
                                                userParsistedFaceId, userSession.getShiftType(),userSession.getEmpCode());
                                        if (updatedStatus) {
                                            progressDialog.dismiss();
                                            getCalltoUpdateRegStatus();
                                        } else {
                                            progressDialog.dismiss();
                                            toastIconError("Employee data is not Inserted");
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        getCalltoUpdateRegStatus();
                                        toastIconError("Already Exist");
                                    }
                                }
                            });


                        } catch (Exception e) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();

                            }
                            toastIconError(e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();

                        }
                        JSONObject mainObject = new JSONObject(responce);
                        String msg = mainObject.getJSONObject("error").getString("message");
                        toastIconError(msg);
                    }
                } catch (Exception e) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    toastIconError(getString(R.string.No_Response_from_server_500));
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private String getConverTobase64(byte[] byteImage) {
        String encEmpImage = Base64.encodeToString(byteImage, Base64.NO_WRAP);
        return encEmpImage;
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
                if (response.isSuccessful()) {
                    try {
                        Regresponce regresponce = response.body();
                        if (!regresponce.getError()) {
                            showCustomDialog();
                        } else {
                            toastIconError(regresponce.getMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        toastIconError(getString(R.string.No_Response_from_server_500));
                    }
                } else {
                    toastIconError(getString(R.string.No_Response_from_server_500));
                }
            }

            @Override
            public void onFailure(Call<Regresponce> call, Throwable t) {
                toastIconError(getString(R.string.No_Response_from_server_500));
            }
        });


    }

    private void showCustomDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(FaceRegister.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.dialog_warning);
                dialog.setCancelable(false);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), CustomerDashBoard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });

    }


    private void registereEmployeeFace() {
        Intent intent = new Intent(FaceRegister.this, RegisterFaceActivity.class);
        intent.putExtra("type", -1);
        startActivity(intent);
    }


    private void OpenCameratoTakepicture() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FaceRegister.this,
                    new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_USE_CAMERA);
        } else {
            takePicture();
        }
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        extras = data.getExtras();
        if (extras == null) {
            toastIconError(getString(R.string.could_not_recognise));
            return;
        }
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        Bitmap icon = imageBitmap;
        Bitmap resized = getResizedBitmap(icon, 300, 300);
        Bitmap tempbimap = cropImge(resized);
        // image.setImageBitmap(tempbimap);
        // register.setBackgroundResource(R.drawable.btn_rounded_primary);
        //register.setEnabled(true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        imageInByte = convertToByte(tempbimap);
        validateFace();
    }

    private void toastIconError(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);

                //inflate view
                View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
                ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
                //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
                ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));
                toast.setView(custom_view);
                toast.show();
            }
        });

    }

    private Bitmap cropImge(Bitmap resizedbitmap) {
        final Bitmap tempBitmap = Bitmap.createBitmap(resizedbitmap.getWidth(), resizedbitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(tempBitmap);
        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        if (!faceDetector.isOperational()) {
            toastIconError(getString(R.string.could_not_recognise));


        }
        Frame frame = new Frame.Builder().setBitmap(resizedbitmap).build(); //create frame and pass bitmap
        int size = 300;
        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;
        SparseArray<Face> sparseArray = faceDetector.detect(frame);
        for (int i = 0; i < sparseArray.size(); i++) {
            Face face = sparseArray.valueAt(i);
            left = face.getPosition().x;
            top = face.getPosition().y;
            right = face.getPosition().x + face.getWidth();
            bottom = face.getPosition().y + face.getHeight();
            Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
            Rect dst = new Rect(0, 0, size, size);
            canvas.drawBitmap(resizedbitmap, src, dst, null);
        }
        return tempBitmap;

    }

    public Bitmap getResizedBitmap(Bitmap image, int bitmapWidth, int bitmapHeight) {
        return Bitmap.createScaledBitmap(image, bitmapWidth, bitmapHeight, true);
    }


    public byte[] convertToByte(Bitmap convertImg) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        convertImg.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public JsonObject getJsonObjects(int empId, String pesitedFaceId, String empPic) {
        try {
            JSONObject custDietailes = new JSONObject();
            custDietailes.put("empId", empId);
            custDietailes.put("empPresistedFaceId", pesitedFaceId);
            custDietailes.put("employeePic", empPic);
            JsonObject inputData = (JsonObject) new JsonParser().parse(custDietailes.toString());
            return inputData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

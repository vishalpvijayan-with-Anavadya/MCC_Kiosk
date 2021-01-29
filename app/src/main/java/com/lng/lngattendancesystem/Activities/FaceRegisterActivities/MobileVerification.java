package com.lng.lngattendancesystem.Activities.FaceRegisterActivities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.Activities.CustomerActivities.CustomerDashBoard;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.ConnectivityReceiver;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.MainActivity;
import com.lng.lngattendancesystem.Models.FaceRegistration.MainRegresponce;
import com.lng.lngattendancesystem.Models.Status;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.getCustomerAllDitailes;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.EmployeeConfirmation;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileVerification extends AppCompatActivity {
    private Button btn_verify;
    private EditText mobile_no;
    private UserSession userSession;
    private ProgressDialog progressDialog;
    private long TIME_OUT = 1000;
    private BroadcastReceiver broadcastReceiver = new ConnectivityReceiver();

    private static boolean validatePhoneNumber(String phoneNo) {

        //validate phone numbers of format "1234567890"
        if (phoneNo.matches("\\d{10}"))
            return true;
            //validating phone number with -, . or spaces

        else if (phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}")) return true;

            //validating phone number with extension length from 3 to 5
        else //return false if nothing matches the input
            if (phoneNo.matches("\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}")) return true;
                //validating phone number where area code is in braces ()
            else return phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(MobileVerification.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        userSession = new UserSession(MobileVerification.this);
        progressDialog = new ProgressDialog(MobileVerification.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.progressbarmsg));
        progressDialog.setCancelable(false);
        initUiComponent();
    }

    private void initUiComponent() {
        btn_verify = findViewById(R.id.btn_verify);
        mobile_no = findViewById(R.id.mobile_no);
        // registerForBroadcaseReceier();
        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDesable(v);
                hideKeyboard(v);
                if (!validatePhoneNumber(mobile_no.getText().toString()) && mobile_no.getText().toString().length() != 10) {
                    toastIconError(getString(R.string.mobile));
                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (Util.isConnected(MobileVerification.this)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.show();
                                        getCallEmployeeApi(mobile_no.getText().toString());
                                    }
                                });


                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        toastIconError(getString(R.string.Internet_CONNECTION));
                                    }
                                });


                            }
                        }
                    }).start();


                }

            }
        });
    }

    private void getCallEmployeeApi(final String mobileno) {
        if (SplashActivity.databaseHandler == null)
            return;
        List<getCustomerAllDitailes> cutsomerDetalesList = SplashActivity.databaseHandler.getCustomerAllrecords();
        final String custId;
        String BranchId;
        if (cutsomerDetalesList == null) {
            Intent intent = new Intent(MobileVerification.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }
        custId = cutsomerDetalesList.get(0).getCustomerId();
        BranchId = cutsomerDetalesList.get(0).getBranchId();
        JsonObject jsonObject = getJsonObjectss(custId, BranchId, mobileno);
        ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
        Call<MainRegresponce> mainCustRegResponceCall = apiClient.getEmployeeDetails(jsonObject);
        mainCustRegResponceCall.enqueue(new Callback<MainRegresponce>() {
            @Override
            public void onResponse(Call<MainRegresponce> call, Response<MainRegresponce> response) {
                if (response.isSuccessful()) {
                    try {
                        MainRegresponce mainRegresponce = response.body();
                        Status status = mainRegresponce.getStatus();
                        if (!status.getError()) {
                            _removeProgressDialog();
                            userSession.setMobileNO(mobileno);

                            String userParsistedFaceId = mainRegresponce.getData1().getEmpPresistedFaceId();

                            if (mainRegresponce != null) {

                                userSession.setEmpId(mainRegresponce.getData1().getEmpId());
                                userSession.setEmpName(mainRegresponce.getData1().getEmpName());
                                userSession.setShifttype(mainRegresponce.getData1().getShiftType());
                                userSession.setPersistedFaceId(userParsistedFaceId);
                                userSession.setEmpCode(mainRegresponce.getData1().getEmpCode());


                                if (SplashActivity.databaseHandler.isEmployeeExist(String.valueOf(userSession.getEmpId()))) {

                                    if (userParsistedFaceId == null || userParsistedFaceId.equalsIgnoreCase("null")) {
                                        showConfirmDialog();
                                    } else {

                                        progressDialog.dismiss();
                                        toastIconError("You are already a Registered user!");
                                        Intent intent = new Intent(MobileVerification.this, CustomerDashBoard.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    showConfirmDialog();
                                }


                            }
                        } else {
                            _removeProgressDialog();
                            toastIconError(status.getMessage());
                        }

                    } catch (Exception e) {
                        _removeProgressDialog();
                        toastIconError(getString(R.string.No_Response_from_server_500));
                        e.printStackTrace();
                    }

                } else {
                    _removeProgressDialog();
                    toastIconError(getString(R.string.No_Response_from_server_500));
                }
            }

            @Override
            public void onFailure(Call<MainRegresponce> call, Throwable t) {
                _removeProgressDialog();
                toastIconError(getString(R.string.No_Response_from_server_500));
            }
        });


    }

    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) MobileVerification.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showConfirmDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        EmployeeConfirmation newFragment = new EmployeeConfirmation(userSession.getEmpName(), String.valueOf(userSession.getMobileNo()));
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
    }

    private void toastIconError(String msg) {
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


    public JsonObject getJsonObjectss(String custId, String brId, String mobileno) {
        try {
            JSONObject empDitales = new JSONObject();
            empDitales.put("refBrId", brId);
            empDitales.put("refCustId", custId);
            empDitales.put("empMobile", mobileno);
            //empDitales.put("blkId", userSession.getBlockId());
            JsonObject inputData = (JsonObject) new JsonParser().parse(empDitales.toString());
            return inputData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void _removeProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
    }


    public void onClickDesable(final View v) {
        v.setEnabled(false);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, TIME_OUT);
    }


}

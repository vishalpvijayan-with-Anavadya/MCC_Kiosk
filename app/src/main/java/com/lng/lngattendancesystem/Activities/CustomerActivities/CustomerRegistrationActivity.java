package com.lng.lngattendancesystem.Activities.CustomerActivities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.BroadCastReciever.ConnectivityReceiver;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.Models.CustomerRegModule.MainCustRegResponce;
import com.lng.lngattendancesystem.Models.Status;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.UtilActivity.NoInternetActivity;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.UserSession;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRegistrationActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    String enteredCostumercode, enteredBranchCode, enteredKioskNumber;
    ProgressDialog progressDialog;
    UserSession userSession;
    private Button bt_verify;
    private EditText custCode, brCode, kioskNumber;

    private BroadcastReceiver broadcastReceiver = new ConnectivityReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(CustomerRegistrationActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        setContentView(R.layout.activity_customer_registration);
        registerForBroadcastReceiver();
        initUiComponents();
    }

    private void initUiComponents() {
        bt_verify = findViewById(R.id.btn_verify);
        custCode = findViewById(R.id.cust_code);
        brCode = findViewById(R.id.br_code);
        kioskNumber = findViewById(R.id.kiosk_number);
        userSession = new UserSession(CustomerRegistrationActivity.this);
        progressDialog = new ProgressDialog(CustomerRegistrationActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.progressbarmsg));
        progressDialog.setCancelable(false);
        bt_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValidateCustDetailes();
            }
        });


    }

    public void getValidateCustDetailes() {

        enteredCostumercode = custCode.getText().toString().trim();
        enteredBranchCode = brCode.getText().toString().trim();
        enteredKioskNumber = kioskNumber.getText().toString().trim();
        if (enteredCostumercode.isEmpty() && enteredBranchCode.isEmpty() && enteredKioskNumber.isEmpty()) {
            toastIconError(getString(R.string.erf_ccbrcode));
        } else if (enteredCostumercode.isEmpty() || enteredCostumercode.length() != 6) {
            toastIconError(getString(R.string.erf_cc));
        } else if (enteredBranchCode.isEmpty() || enteredBranchCode.length() != 8) {
            toastIconError(getString(R.string.erf_branchcode));
        } else if (enteredKioskNumber.isEmpty() || (Integer.parseInt(enteredKioskNumber)) <= 0 || (Integer.parseInt(enteredKioskNumber)) > 9999) {
            toastIconError(getString(R.string.INVALID_KIOSK_NUMBER));
        } else {
            progressDialog.show();
            getCallCustomerRegApi();
        }

    }


    public void getCallCustomerRegApi() {
        try {
            /**
             * {
             *     "custCode": "CDV001",
             *     "brCode": "CDV00101",
             *     "kioskNo":8,
             *     "kioskCode":"ABC3201",
             *     "deviceOveride":false
             * }
             */
            JSONObject inputDetails = new JSONObject();
            inputDetails.put("custCode", enteredCostumercode);
            inputDetails.put("brCode", enteredBranchCode);
            inputDetails.put("kioskNo", enteredKioskNumber);
            inputDetails.put("kioskCode", userSession.getAndroidDeviceID());
            inputDetails.put("deviceOveride", false);
            JsonObject inputData = (JsonObject) new JsonParser().parse(inputDetails.toString());
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<MainCustRegResponce> mainCustRegResponceCall = apiClient.getCustRegister(inputData);
            mainCustRegResponceCall.enqueue(new Callback<MainCustRegResponce>() {
                @Override
                public void onResponse(Call<MainCustRegResponce> call, Response<MainCustRegResponce> response) {
                    if (response.isSuccessful()) {
                        try {
                            MainCustRegResponce mainCustRegResponce = response.body();
                            Status status = mainCustRegResponce.getStatus();
                            if (!status.getError()) {
                                _removeProgressDialog();
                                userSession.setOTP(mainCustRegResponce.getOtpDto().getOtp());
                                userSession.setCustId(mainCustRegResponce.getCustId());
                                userSession.setBrID(mainCustRegResponce.getBrId());
                                userSession.setBranchcode(enteredBranchCode);
                                userSession.setCustomerCode(enteredCostumercode);
                                userSession.setKeyKioskNumber(enteredKioskNumber);
                                Intent intent = new Intent(CustomerRegistrationActivity.this, CustomerOtpActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                _removeProgressDialog();
                                toastIconError(status.getMessage());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            _removeProgressDialog();
                            toastIconError(getString(R.string.No_Response_from_server_500));
                        }

                    } else {
                        _removeProgressDialog();
                        toastIconError(getString(R.string.No_Response_from_server_500));
                    }
                }

                @Override
                public void onFailure(Call<MainCustRegResponce> call, Throwable t) {
                    _removeProgressDialog();
                    t.printStackTrace();
                    toastIconError(getString(R.string.No_Response_from_server_500));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void toastIconError(String msg) {
        Toast toast = new Toast(CustomerRegistrationActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
        //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));
        toast.setView(custom_view);
        toast.show();

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


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            startActivity(new Intent(CustomerRegistrationActivity.this, NoInternetActivity.class));

        }

    }

    private void registerForBroadcastReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_MANAGE_NETWORK_USAGE);
        this.registerReceiver(broadcastReceiver, filter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        LngAttendance.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {
        }
    }


}

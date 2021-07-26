package com.lng.lngattendancesystem.Activities.FaceRegisterActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.goodiebag.pinview.Pinview;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.Activities.CustomerActivities.BranchBlockActivity;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.Tools;
import com.lng.lngattendancesystem.Utilities.UserSession;

import org.json.JSONObject;

public class OtpActivity extends AppCompatActivity {
    Button bt_continue;
    LinearLayout otplinearlayout;
    TextView tv_resentotpid, tv_mobileno, tv_coundown;
    ProgressDialog progressDialog;
    int otpp, userOtp, resentotp;
    Pinview pinview;
    UserSession userSession;
    ImageView back;
    ApiInterface apiInterface;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(OtpActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        intUicomponent();
        initToolbar();
    }

    private void intUicomponent() {
        tv_mobileno = findViewById(R.id.tv_mob);
        pinview = findViewById(R.id.otp_view);
        bt_continue = findViewById(R.id.continue_id);
        userSession = new UserSession(OtpActivity.this);
        //tv_resentotpid = (TextView) findViewById(R.id.tv_resentotpid);
        //  tv_mobileno.setText(getString(R.string.otpmheader) + " +91" + " " + "7204208262");
        tv_mobileno.setText(getString(R.string.otpmheader) + userSession.getMobileNo() + userSession.getOTP());
        progressDialog = new ProgressDialog(OtpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.progressbarmsg));
        progressDialog.setCancelable(false);

        Toast.makeText(OtpActivity.this, "Your OTP is : " + userSession.getOTP(), Toast.LENGTH_LONG).show();

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(OtpActivity.this, BranchBlockActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();*/
            }
        });

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(OtpActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                bt_continue.setBackgroundResource(R.drawable.btn_rounded_primary);
                bt_continue.setEnabled(true);
            }
        });

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (pinview.getValue().length() != 4 || Integer.parseInt(pinview.getValue()) != userSession.getOTP()) {
                        toastIconError("Please Enter valid OTP !");
                        return;
                    }
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                Intent intent = new Intent(OtpActivity.this, FaceRegister.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                    toastIconError(getString(R.string.exeptionMsg) + e.getMessage());

                }


            }

        });


    }


    private void toastIconError(String msg) {
        Toast toast = new Toast(OtpActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
        //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));
        toast.setView(custom_view);
        toast.show();
    }

    public JsonObject getJsonObjects(String custId, String brId, String mobileno) {
        try {
            JSONObject empDitales = new JSONObject();
            empDitales.put("refBrId", custId);
            empDitales.put("refCustId", brId);
            empDitales.put("empMobile", mobileno);


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

    private void initToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        // toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("OTP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OtpActivity.this, BranchBlockActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }


}

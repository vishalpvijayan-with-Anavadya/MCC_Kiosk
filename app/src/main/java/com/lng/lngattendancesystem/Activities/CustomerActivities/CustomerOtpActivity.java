package com.lng.lngattendancesystem.Activities.CustomerActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.goodiebag.pinview.Pinview;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.MainActivity;
import com.lng.lngattendancesystem.Models.CustomerBranchDetailsResponseModels.CustomerBranchDetailsResponse;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.InsertCustomerDitales;
import com.lng.lngattendancesystem.UtilActivity.RestoreActivity;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.Tools;
import com.lng.lngattendancesystem.Utilities.UserSession;

import org.json.JSONObject;

import java.io.File;
import java.io.FilenameFilter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerOtpActivity extends AppCompatActivity {
    Button bt_continue;
    TextView tv_resentotpid, tv_mobileno, tv_coundown;
    ProgressDialog progressDialog;
    Pinview pinview;
    UserSession userSession;
    private CountDownTimer countDownTimer;
    private String[] mFileList;
    private String[] dbFileList;
    private String[] mmFileList;
    private String mChosenFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_otp);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(CustomerOtpActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        intiUiComponents();
        initToolbar();
        LoadDBFile();
        LoadMMFile();
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
                /*Intent intent = new Intent(CustomerOtpActivity.this, BranchBlockActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();*/
            }
        });
    }

    private void intiUiComponents() {
        tv_mobileno = findViewById(R.id.tv_mob);
        pinview = findViewById(R.id.otp_view);
        bt_continue = findViewById(R.id.continue_id);
        userSession = new UserSession(CustomerOtpActivity.this);
        //tv_resentotpid = (TextView) findViewById(R.id.tv_resentotpid);

        tv_mobileno.setText(getString(R.string.otpmheader));
        progressDialog = new ProgressDialog(CustomerOtpActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.progressbarmsg));
        progressDialog.setCancelable(false);

       //Toast.makeText(CustomerOtpActivity.this, "" + userSession.getOTP(), Toast.LENGTH_SHORT).show();

        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
            @Override
            public void onDataEntered(Pinview pinview, boolean fromUser) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(CustomerOtpActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
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
                /*    Log.d("TAG", "TESTFILES Exist dbFile " + dbFileList.length);
                    Log.d("TAG", "TESTFILES Exist mmFile " + mmFileList.length);*/

                    if (dbFileList != null && dbFileList.length > 0
                            && mmFileList != null && mmFileList.length > 0) {

                        Log.d("TAG", "TESTFILES  Exist files ");
                        Intent intent = new Intent(CustomerOtpActivity.this, RestoreActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                        //Log.d("TAG", "TESTFILES Exist Files " + dbFileList.length);
                    } else {

                       // Log.d("TAG", "TESTFILES not Exist files ");
                        Intent intent = new Intent(CustomerOtpActivity.this, CustomerDashBoard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    toastIconError(getString(R.string.exeptionMsg) + e.getMessage());
                }


            }

        });

    }

    private void getCustomerDietailesApi() {
        JsonObject jsonObject = getJsonObjects();
        ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);

        Call<CustomerBranchDetailsResponse> customerBranchDetailsResponseCall = apiClient.getcustDetalies(jsonObject);
        customerBranchDetailsResponseCall.enqueue(new Callback<CustomerBranchDetailsResponse>() {
            @Override
            public void onResponse(Call<CustomerBranchDetailsResponse> call, Response<CustomerBranchDetailsResponse> response) {
                if (response.isSuccessful()) {
                    try {
                        CustomerBranchDetailsResponse customerBranchDetailsResponse = response.body();
                        if (customerBranchDetailsResponse.getStatus().getError()) {
                            _removeProgressDialog();
                            toastIconError(customerBranchDetailsResponse.getStatus().getMessage());
                        } else {
                            InsertCustomerDitales insertCustomerDitales;
                            for (int i = 0; i < customerBranchDetailsResponse.getCustBranchDetails().size(); i++) {
                                String custId = String.valueOf(customerBranchDetailsResponse.getCustBranchDetails().get(i).getCustId());
                                String brId = String.valueOf(customerBranchDetailsResponse.getCustBranchDetails().get(i).getBrId());
                                byte[] convertwdImg = convertedImg(customerBranchDetailsResponse.getCustBranchDetails().get(i).getCustLogoFile());
                                String customerName = customerBranchDetailsResponse.getCustBranchDetails().get(i).getCustName();
                                String branchName = customerBranchDetailsResponse.getCustBranchDetails().get(i).getBrName();
                                String brCode = customerBranchDetailsResponse.getCustBranchDetails().get(i).getBrCode();
                                userSession.setBrID(customerBranchDetailsResponse.getCustBranchDetails().get(i).getBrId());
                                userSession.setCustId(customerBranchDetailsResponse.getCustBranchDetails().get(i).getCustId());
                                insertCustomerDitales = new InsertCustomerDitales(custId, brId, convertwdImg,
                                        customerName, branchName, brCode);
                                boolean result = SplashActivity.databaseHandler.addCustomerDitailes(insertCustomerDitales);
                                if (result) {
                                    Intent intent = new Intent(CustomerOtpActivity.this, CustomerDashBoard.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(CustomerOtpActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    userSession.setRegiCompleeted(true);
                                    startActivity(intent);
                                    finish();
                                }

                            }

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    _removeProgressDialog();
                    toastIconError(getString(R.string.No_Response_from_server_500));

                }
            }

            @Override
            public void onFailure(Call<CustomerBranchDetailsResponse> call, Throwable t) {
                _removeProgressDialog();
                toastIconError(getString(R.string.No_Response_from_server_500));
            }
        });

    }


    private void toastIconError(String msg) {
        Toast toast = new Toast(CustomerOtpActivity.this);
        toast.setDuration(Toast.LENGTH_LONG);

        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
        //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.red_600));
        toast.setView(custom_view);
        toast.show();
    }

    public byte[] convertedImg(String customerLogo) {
        byte[] data = Base64.decode(customerLogo, Base64.DEFAULT);
        return data;
    }

    public JsonObject getJsonObjects() {
        try {
            JSONObject custDietailes = new JSONObject();
            custDietailes.put("custCode", userSession.getCustomerCode());
            custDietailes.put("brCode", userSession.getBranchCode());
            custDietailes.put("kioskNo", userSession.getKioskNumber());
            custDietailes.put("kioskCode", userSession.getAndroidDeviceID());


            JsonObject inputData = (JsonObject) new JsonParser().parse(custDietailes.toString());
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



    private void LoadDBFile() {
        try {
            File filePath = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//" + ConstantValues.DB_FILE_BACKUP_FOLDER);

            if (filePath.exists()) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.contains(ConstantValues.DB_TYPE);

                    }
                };
                dbFileList = filePath.list(filter);
                //Log.d("TAG", "TESTFILES Exist LoadDBFile" + dbFileList.length);
            } else {
                //  dbFileList = new String[]{ConstantValues.NO_BACKUP_FILE_MSG};
                //Log.d("TAG", "TESTFILES no LoadDBFile");
            }
        } catch (Exception e) {

        }
    }


    private void LoadMMFile() {
        try {
            File filePath = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//" + ConstantValues.DAT_FILE_BACKUP_FOLDER);

            if (filePath.exists()) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.contains(ConstantValues.DAT_TYPE);

                    }
                };
                mmFileList = filePath.list(filter);

                //Log.d("TAG", "TESTFILES Exist LoadDBFile" + mmFileList.length);
            } else {
                // mmFileList = new String[]{ConstantValues.NO_BACKUP_FILE_MSG};

               // Log.d("TAG", "TESTFILES no LoadMMFile");
            }

        } catch (Exception e) {

        }

    }


}

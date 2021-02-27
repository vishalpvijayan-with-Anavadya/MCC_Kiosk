package com.lng.lngattendancesystem.Activities.CustomerActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.Adapters.EmployeeListAdapter;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark.ProcessAndMarkAttenance;
import com.lng.lngattendancesystem.Models.RestoreEmployeeDetales.EmployeeDetail;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;
import com.luxand.FSDK;

import java.util.ArrayList;

public class PergeActivity extends AppCompatActivity {
    public static final String TAG = "Test";
    public static String name;
    private final String database = "Memory70.dat";
    ImageView back_arrow;
    ProgressDialog progressDialog;
    UserSession userSession;
    LinearLayout no_records_found;
    ArrayList<EmployeeDetail> availableEmployees;
    EmployeeListAdapter employeeListAdapter;
    private TextView tvNoRecords;
    private RecyclerView employeeListView;
    private ProcessAndMarkAttenance mDraw;
    private EditText serchItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perge);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(PergeActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        init();
    }


    private void init() {
        progressDialog = Util.getProcessDialog(PergeActivity.this);
        userSession = new UserSession(PergeActivity.this);
        back_arrow = findViewById(R.id.back);
        employeeListView = findViewById(R.id.my_team);
        employeeListView.setLayoutManager(new LinearLayoutManager(this));
        employeeListView.setHasFixedSize(true);
        tvNoRecords = findViewById(R.id.tv_no_records);
        no_records_found = findViewById(R.id.no_leave_found);
        serchItem = findViewById(R.id.serchItem);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        int res = FSDK.ActivateLibrary(ConstantValues.LICENSE_KEY_7_2);
        if (res != FSDK.FSDKE_OK) {
            Toast.makeText(this, "FaceSDK activation failed  :" + res, Toast.LENGTH_SHORT).show();
        } else {
            FSDK.Initialize();
            fileConfig();
        }
        listEmployees();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        };

      serchItem.addTextChangedListener(textWatcher);
    }


    private void filter(String text) {
        Log.d(TAG, "Textfilter: text "+text);
        //new array list that will hold the filtered data
        ArrayList<EmployeeDetail> newArrayList=new ArrayList<>();

         for(EmployeeDetail detail:availableEmployees){

             if(detail.getEmpName().toLowerCase().contains(text)
                     || String.valueOf(detail.getEmpId()).contains(text) ){
                 newArrayList.add(detail);
             }
         }

        //calling a method of the adapter class and passing the filtered list
        filterList(newArrayList);
    }

    public void filterList(ArrayList<EmployeeDetail> filterdNames) {
        Log.d(TAG, "Textfilter:  newArraList ");
        employeeListAdapter = new EmployeeListAdapter(PergeActivity.this, filterdNames, this);
        employeeListView.setAdapter(employeeListAdapter);
        employeeListView.setVisibility(View.VISIBLE);
        employeeListAdapter.notifyDataSetChanged();
    }


    private void listEmployees() {
        try {
            availableEmployees = SplashActivity.databaseHandler.getAllEmployeeDetails();

            if (availableEmployees != null && availableEmployees.size() > 0) {

                employeeListAdapter = new EmployeeListAdapter(PergeActivity.this, availableEmployees, this);
                employeeListView.setAdapter(employeeListAdapter);
                employeeListView.setVisibility(View.VISIBLE);
                no_records_found.setVisibility(View.GONE);
            } else {
                showNoRecords();
            }
        } catch (Exception e) {
            showNoRecords();
        }

    }

    private void showNoRecords() {
        employeeListView.setVisibility(View.GONE);
        no_records_found.setVisibility(View.VISIBLE);
    }

    private void fileConfig() {

        mDraw = new ProcessAndMarkAttenance(this);
        mDraw.mTracker = new FSDK.HTracker();
        String templatePath = this.getApplicationInfo().dataDir + "/" + database;
        if (FSDK.FSDKE_OK != FSDK.LoadTrackerMemoryFromFile(mDraw.mTracker, templatePath)) {
            int res = FSDK.CreateTracker(mDraw.mTracker);
            if (FSDK.FSDKE_OK != res) {
                Toast.makeText(this, "Error creating tracker :" + res, Toast.LENGTH_SHORT).show();
            }
        }
        resetTrackerParameters();
    }

    private void resetTrackerParameters() {
        int[] errpos = new int[1];
        FSDK.SetTrackerMultipleParameters(mDraw.mTracker, "ContinuousVideoFeed=true;FacialFeatureJitterSuppression=0;RecognitionPrecision=1;Threshold=0.996;Threshold2=0.9995;ThresholdFeed=0.97;MemoryLimit=2000;HandleArbitraryRotations=false;DetermineFaceRotationAngle=false;InternalResizeWidth=70;FaceDetectionThreshold=3;", errpos);
        if (errpos[0] != 0) {
            Toast.makeText(this, "Error setting tracker parameters, position", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDraw != null) {
            String templatePath = this.getApplicationInfo().dataDir + "/" + database;
            FSDK.SaveTrackerMemoryToFile(mDraw.mTracker, templatePath);
        }
    }


    public void purgeEmployeeDetails(int faceID, int empId) {

        AlertDialog.Builder builder = new AlertDialog.Builder(PergeActivity.this);
        builder.setTitle("Are you sure!");
        builder.setMessage("Do you want to remove employee!");
        builder.setPositiveButton(R.string.proceed_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(PergeActivity.this, "Face is :" + faceID, Toast.LENGTH_SHORT).show();
                boolean status = false;
                int s = FSDK.SetName(mDraw.mTracker, faceID, "");
                status = FSDK.FSDKE_OK == s || s == -18;
                if (status == false) {
                    Toast.makeText(PergeActivity.this, "Failed 1", Toast.LENGTH_SHORT).show();

                }
                int d = FSDK.PurgeID(mDraw.mTracker, faceID);
                Log.d("DeleteEmployee", FSDK.FSDKE_OK +", "+ d+", "+faceID+", "+s);
                if (FSDK.FSDKE_OK == d || d == -18) {
                    //Toast.makeText(PergeActivity.this, "Second Success", Toast.LENGTH_SHORT).show();
                    boolean deleted = SplashActivity.databaseHandler.deleteEmployee(empId);
                    if (deleted) {
                        Toast.makeText(PergeActivity.this, "Employee Deleted from database", Toast.LENGTH_SHORT).show();
                        listEmployees();
                    } else {
                        Toast.makeText(PergeActivity.this, "Unable to Delete employee from database", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(PergeActivity.this, "Failed 2", Toast.LENGTH_SHORT).show();

                }

            }
        });
        builder.setNegativeButton(R.string.cancel_text, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

}

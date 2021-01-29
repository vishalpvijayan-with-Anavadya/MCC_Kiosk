package com.lng.lngattendancesystem.UtilActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.MainActivity;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;

public class LcationAndAppPermistionActivity extends AppCompatActivity {
    View view;
    Button bt_allow;
    int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lcation_and_app_permistion);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(LcationAndAppPermistionActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        intUiComponents();
    }

    private void intUiComponents() {
        bt_allow = findViewById(R.id.bt_allow);
      /*  AppCompatActivity activity = (AppCompatActivity) getApplicationContext();
        activity.getSupportActionBar().setTitle("Dashboard");
*/
        bt_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkpermistion();
            }
        });
    }

    private void checkpermistion() {
        if (ContextCompat.checkSelfPermission(LcationAndAppPermistionActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(LcationAndAppPermistionActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(LcationAndAppPermistionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            ActivityCompat.requestPermissions(LcationAndAppPermistionActivity.this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                int granted = 0;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        granted++;
                    }
                }
                if (granted == 2) {    // contacts-related task you need to do.
                    Intent intent = new Intent(LcationAndAppPermistionActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    try {
                        Uri uri = Uri.fromParts("package", SplashActivity.PACKAGE_NAME, null);
                        intent.setData(uri);
                        startActivity(intent);
                    } catch (Exception e) {
                        Uri uri = Uri.fromParts("package", "com.lng.lngattendancev1", null);
                        intent.setData(uri);
                        startActivity(intent);
                        e.printStackTrace();
                    }
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {

    }
}

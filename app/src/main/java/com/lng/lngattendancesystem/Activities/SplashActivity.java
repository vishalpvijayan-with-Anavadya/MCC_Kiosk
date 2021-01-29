package com.lng.lngattendancesystem.Activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lng.lngattendancesystem.Activities.CustomerActivities.CustomerDashBoard;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.MainActivity;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.SqliteDatabse.DatabaseHandler;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.UserSession;

public class SplashActivity extends AppCompatActivity {

    public static DatabaseHandler databaseHandler;
    public static String PACKAGE_NAME;
    private static int splashTimeOut = 3000;
    TextView appversion;
    UserSession userSession;

    private ImageView logo;

    public static void closeDBInstance() {
        try {
            if (databaseHandler != null)
                databaseHandler.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(SplashActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        logo = findViewById(R.id.logo);
        appversion = findViewById(R.id.appversion);


        userSession = new UserSession(SplashActivity.this);
        databaseHandler = new DatabaseHandler(SplashActivity.this);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        String appVersion = getAppVersion();
        this.appversion.setText("Version " + appVersion);
        getAndroidDeviceID();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SplashActivity.databaseHandler.geCustomerId() != null) {
                    Intent intent = new Intent(SplashActivity.this, CustomerDashBoard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }, splashTimeOut);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mysplashanimation);
        logo.startAnimation(myanim);
        appversion.startAnimation(myanim);
    }

    public String getAppVersion() {
        PackageManager manager = getApplicationContext().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = info.versionName;
        return version;
    }

    public void getAndroidDeviceID() {
        try {
            String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            userSession.setAndroidDeviceID(androidId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);


    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}

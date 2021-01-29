package com.lng.lngattendancesystem.Activities.CustomerActivities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark.MarkAttendanceActivity;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.getCustomerAllDitailes;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;

import java.util.List;

public class TimerActivity extends AppCompatActivity implements SensorEventListener {
    ImageView punch;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(TimerActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        punch = findViewById(R.id.punch);
        punch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimerActivity.this, MarkAttendanceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("type", 1);
                startActivity(intent);
                finish();
            }
        });
        changeCustomerDetailes();
    }

    public void changeCustomerDetailes() {
        if (SplashActivity.databaseHandler == null)
            return;
        List<getCustomerAllDitailes> cutsomerDetalesList = SplashActivity.databaseHandler.getCustomerAllrecords();
        byte[] custImg = null;
        if (cutsomerDetalesList != null) {
            custImg = cutsomerDetalesList.get(0).getCustomerLogo();
            if (custImg != null) {
                Bitmap customerImag = BitmapFactory.decodeByteArray(custImg, 0, custImg.length);
                punch.setImageBitmap(customerImag);


            }
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.values[0] == 0) {
            Intent intent1 = new Intent(TimerActivity.this, MarkAttendanceActivity.class);
            intent1.putExtra("type", 1);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent1);
        } else {

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            mSensorManager.registerListener(this, mSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            mSensorManager.unregisterListener(this);
        } catch (Exception e) {

        }
    }
}

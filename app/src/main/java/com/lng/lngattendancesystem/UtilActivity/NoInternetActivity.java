package com.lng.lngattendancesystem.UtilActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.ConnectivityReceiver;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;


public class NoInternetActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    String myValue;
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    private Button retry;
    private BroadcastReceiver broadcastReceiver = new ConnectivityReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(NoInternetActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        initComponent();
    }


    private void initComponent() {
        progress_bar = findViewById(R.id.progress_bar);
        lyt_no_connection = findViewById(R.id.lyt_no_connection);
        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);
        retry = findViewById(R.id.btn_internet_retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_bar.setVisibility(View.VISIBLE);
                lyt_no_connection.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress_bar.setVisibility(View.GONE);
                        lyt_no_connection.setVisibility(View.VISIBLE);
                        if (ConnectivityReceiver.isConnected()) {
                            finish();
                        }
                    }
                }, 1000);
            }
        });
        lyt_no_connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progress_bar.setVisibility(View.VISIBLE);
                lyt_no_connection.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress_bar.setVisibility(View.GONE);
                        lyt_no_connection.setVisibility(View.VISIBLE);
                        if (ConnectivityReceiver.isConnected()) {
                            finish();
                        }
                    }
                }, 1000);
            }
        });
        registerForBroadcaseReceier();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isConnected) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LngAttendance.getInstance().setConnectivityListener(this);
    }

    private void registerForBroadcaseReceier() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_MANAGE_NETWORK_USAGE);
        this.registerReceiver(broadcastReceiver, filter);
    }


    @Override
    public void onBackPressed() {
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
    protected void onRestart() {
        super.onRestart();
        try {
            registerForBroadcaseReceier();
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

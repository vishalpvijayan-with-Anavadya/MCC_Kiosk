package com.lng.lngattendancesystem;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.lng.lngattendancesystem.Activities.CustomerActivities.CustomerRegistrationActivity;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.ConnectivityReceiver;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;
import com.lng.lngattendancesystem.Fragments.HomeFragments;
import com.lng.lngattendancesystem.UtilActivity.LcationAndAppPermistionActivity;
import com.lng.lngattendancesystem.UtilActivity.NoInternetActivity;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.UserSession;

public class MainActivity extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    UserSession userSession;
    NavigationView nav_view;
    boolean doubleBackToExitPressedOnce = false;
    int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private BroadcastReceiver broadcastReceiver = new ConnectivityReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Intent intent = new Intent(MainActivity.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        userSession = new UserSession(MainActivity.this);
        checkpermistion();
        registerForBroadcaseReceier();
        initToolbar();
        initNavigationMenu();
        initialFragTransaction();
    }

    private void initialFragTransaction() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentcantainer, new HomeFragments())
                .commit();
    }

    /**
     * Permission
     */
    private void checkpermistion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Home");
    }

    private void initNavigationMenu() {
        nav_view = findViewById(R.id.nav_view);
        Menu nav_Menu = nav_view.getMenu();
        nav_view.setItemIconTintList(null);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(final MenuItem item) {
                actionBar.setTitle(item.getTitle());
                drawer.closeDrawers();
                switch (item.getItemId()) {
                    case R.id.register_id:
                        actionBar.setTitle(item.getTitle());
                        Intent intent = new Intent(MainActivity.this, CustomerRegistrationActivity.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        //drawer.openDrawer(GravityCompat.START);
    }


    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            startActivity(new Intent(MainActivity.this, NoInternetActivity.class));

        }

    }

    private void registerForBroadcaseReceier() {
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
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
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
                if (granted != 4) {
                    Intent intent = new Intent(MainActivity.this, LcationAndAppPermistionActivity.class);
                    startActivity(intent);
                    finish();
                }
                return;
            }

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

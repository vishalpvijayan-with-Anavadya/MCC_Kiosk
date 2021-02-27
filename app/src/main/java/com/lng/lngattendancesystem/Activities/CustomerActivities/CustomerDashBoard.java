package com.lng.lngattendancesystem.Activities.CustomerActivities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lng.lngattendancesystem.Activities.QRActivity;
import com.lng.lngattendancesystem.Activities.SplashActivity;
import com.lng.lngattendancesystem.BroadCastReciever.ConnectivityReceiver;
import com.lng.lngattendancesystem.BroadCastReciever.LngAttendance;

import com.lng.lngattendancesystem.BuildConfig;
import com.lng.lngattendancesystem.CallBackInterfaces.ReceiveDeviceConnectionStatus;
import com.lng.lngattendancesystem.Camera.LauxandCameraService.AttendenceMark.MarkAttendanceActivity;
import com.lng.lngattendancesystem.Fragments.Customer_Home_Fragment;
import com.lng.lngattendancesystem.MainActivity;
import com.lng.lngattendancesystem.Models.ClearAttRecords.ClearAttResponce;
import com.lng.lngattendancesystem.Models.ConfigurationResponseModels.BeaconMapDetailslist;
import com.lng.lngattendancesystem.Models.ConfigurationResponseModels.ConfigurationDetails;
import com.lng.lngattendancesystem.R;
import com.lng.lngattendancesystem.Retrofit2.ApiClient;
import com.lng.lngattendancesystem.Retrofit2.ApiInterface;
import com.lng.lngattendancesystem.SerialPortCommunication.UsbService;
import com.lng.lngattendancesystem.SqliteDatabse.DatabaseHandler;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.InsertCustomerDitales;
import com.lng.lngattendancesystem.SqliteDatabse.SqliteModule.SqliteInsertModule.getCustomerAllDitailes;
import com.lng.lngattendancesystem.UtilActivity.NoInternetActivity;
import com.lng.lngattendancesystem.Utilities.ConstantValues;
import com.lng.lngattendancesystem.Utilities.CrashReport.ReportCrashResultActivity;
import com.lng.lngattendancesystem.Utilities.DateTimeUtil;
import com.lng.lngattendancesystem.Utilities.UserSession;
import com.lng.lngattendancesystem.Utilities.Util;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerDashBoard extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener, ReceiveDeviceConnectionStatus {
    private static final String FACIAL_Recognition = "FACIAL_Recognition";
    private static final String QR_Code = "QR_Code";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private static final String FTYPE = ".bak";
    private static final String NotFoundMsg = "There is no backup file(s)";
    public static String TAG = "Test";
    public static String PACKAGE_NAME;
    public static List<BeaconMapDetailslist> deviceBecondsList = new ArrayList<>();
    /**
     * Created by Nagaraj 11-08-2020
     *
     * @param context
     */
    public static List<ScanResult> WIFI_AVAILABLE_SCAN_LIST;
    private final String mStorageFolder = "Mydb";
    private final String mStorageFolder1 = "LNGFRDatBackup";
    int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    boolean doubleBackToExitPressedOnce = false;
    UsbService usbService;
    String responce = "";
    private WifiManager wifiManager;


    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                CustomerDashBoard.WIFI_AVAILABLE_SCAN_LIST = wifiManager.getScanResults();
                Log.i(TAG, "onReceive: " + WIFI_AVAILABLE_SCAN_LIST.size());
            }
        }
    };


    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

            //usbService = null;
        }
    };
    private Toolbar toolbar;
    private ActionBar actionBar;
    private NavigationView nav_view;
    private UserSession userSession;

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    //Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    toastIconSucc("Device is connected");
                    userSession.setFevoDeviceStatus(true);
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    toastIconError("USB Permission not granted");

                    userSession.setFevoDeviceStatus(false);

                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    toastIconError("No USB connected");

                    userSession.setFevoDeviceStatus(false);

                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    toastIconError("USB disconnected");
                    userSession.setFevoDeviceStatus(false);

                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    toastIconError("USB device not supported");
                    userSession.setFevoDeviceStatus(false);

                    break;
                case UsbService.ACTION_USB_ATTACHED: // USB NOT SUPPORTED
                    userSession.setFevoDeviceStatus(false);

                    toastIconError("ACTION_USB_ATTACHED");
                    break;
            }
        }
    };
    private ImageView customerLogo;
    private TextView branchName, customername;
    private BroadcastReceiver broadcastReceiver = new ConnectivityReceiver();
    private ProgressDialog progressDialog;
    private String latestVersion, currentVersion;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private BottomSheetBehavior mBehavior;
    private Boolean mRequestingLocationUpdates;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private String mLastUpdateTime;
    private File mPath;

    /**
     * Hold file list.
     */
    private String[] mFileList;
    private String[] dbFileList;
    private String[] mmFileList;
    private String mChosenFile;

    public static String GetCurrentTime(String inputformat) {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat simpleFormatter = new SimpleDateFormat(inputformat);
        String time = simpleFormatter.format(currentDate);
        return time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_dash_board);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                String data = LngAttendance.getInstance().reportUncaughtException(t, e);
                Toast.makeText(CustomerDashBoard.this, "" + data, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(CustomerDashBoard.this, ReportCrashResultActivity.class);
                intent.putExtra("data", data);
                startActivity(intent);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        userSession = new UserSession(CustomerDashBoard.this);
        progressDialog = Util.getProcessDialog(CustomerDashBoard.this);
        mHandler = new MyHandler(this);
        try {
            boolean status = getIntent().getExtras().getBoolean("Backup");
            Log.i(TAG, "onCreate: " + status);
            if (status) {
                SplashActivity.closeDBInstance();
                backup();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            try {
                registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
                wifiManager.startScan();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        //Check update and display update dialog
        //new GetLatestVersion().execute();
        //changeIcon(1);
        getConfiguration();
        listFiles();

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);


        checkpermistion();


        createLocationCallback();

        createLocationRequest();

        buildLocationSettingsRequest();

        startLocationUpdates();


    }

    public void changeRecordsStatus(boolean isUpdated) {
        if (isUpdated) {
            try {
                Log.d("TAg", "CHECK isUpdated");
                Menu menu = nav_view.getMenu();
                MenuItem menuItem = menu.getItem(3);
                menuItem.setIcon(R.drawable.ic_check_circle);
                menuItem.setEnabled(false);
                nav_view.invalidate();
            } catch (Exception e) {
                Log.d("TAg", "CHECK e");
                e.printStackTrace();
            }
        } else {
            Menu menu = nav_view.getMenu();
            MenuItem menuItem = menu.getItem(3);
            menuItem.setIcon(R.drawable.processing);
            menuItem.setEnabled(false);
            nav_view.invalidate();
            Log.d("TAg", "CHECK notup");
        }

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
                Fragment selectedfrag = null;
                switch (item.getItemId()) {
                    case R.id.nav_policies:
                        //actionBar.setTitle(item.getTitle());
                        Intent reg = new Intent(CustomerDashBoard.this, PoliciesActivitt.class);
                        reg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(reg);
                        finish();
                        break;

                    case R.id.nav_faq:
                        //actionBar.setTitle(item.getTitle());
                        Intent policies = new Intent(CustomerDashBoard.this, FaqActivity.class);
                        policies.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(policies);
                        finish();
                        break;
                    case R.id.mail:
                        String n = null;
                        n.toLowerCase();
                        break;
                }
                return true;
            }
        });
        //drawer.openDrawer(GravityCompat.START);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("Home");
    }

    public void changeIcon() {
        /*   try {*/
        Toast.makeText(usbService, "change icon" + userSession.isFevoDeviceConnected(), Toast.LENGTH_SHORT).show();
        MenuItem menuItem = toolBarMenu.getItem(0);
        if (userSession.isFevoDeviceConnected()) {
            //menuItem.setIcon(R.drawable.ic_check_circle);
            menuItem.setTitle("Connected");
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_check_circle));
        } else {
            //menuItem.setIcon(R.drawable.ic_error_outline_black_24dp);
            menuItem.setTitle("Disconnected");
            menuItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_error_outline_black_24dp));
        }
        invalidateOptionsMenu();
     /*   } catch (Exception e) {
        e.printStackTrace();
        }*/


    }

    Menu toolBarMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        toolBarMenu = menu;
        return true;
    }

    public void dismissProgressDailog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });

    }

    public void showProgressDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    if (!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.qr_generate:
                Intent intent = new Intent(CustomerDashBoard.this, QRActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            case R.id.emergency_Out:
                userSession.setEmergencyOut(true);
                Intent intent1 = new Intent(CustomerDashBoard.this, MarkAttendanceActivity.class);
                intent1.putExtra("type", 1);
                intent1.putExtra("EMG", 4);
                startActivity(intent1);
                break;

            case R.id.clearAtt:
                clearAttendance();
                break;

            case R.id.backup:
                backup();
                //onCreateDialog(1);
                break;


            case R.id.remove_employee:
                Intent intent2 = new Intent(CustomerDashBoard.this, Authentication.class);
                startActivity(intent2);
                break;

            case R.id.restore:
                restore();

                /*
                LoadFileList(1);
                onCreateDialog(2);*/
                break;
            case R.id.restore_dat_file:
                LoadFileList(2);
                onCreateDialog(3);
                /*   String filename = "CDV001-20";
               boolean status= Util.restore(CustomerDashBoard.this,filename);
                Toast.makeText(this, "Statu : "+status, Toast.LENGTH_SHORT).show();*/
                break;
            case R.id.backup_dat_file:
                String filename1 = userSession.getCustomerCode() + "-" + userSession.getKioskNumber() + GetCurrentTime("dd-MM-yyyy HH:mm:ss");
                if (com.lng.lngattendancesystem.Utilities.Util.backup(CustomerDashBoard.this, filename1)) {
                    toastIconSucc("Backup created as " + filename1);
                }
                break;

            case R.id.configuration:
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerDashBoard.this);
                builder.setTitle("Thermal Configuration");
                builder.setCancelable(false);
                View viewInflated = LayoutInflater.from(CustomerDashBoard.this).inflate(R.layout.frequency_input_value, null, false);
                final EditText input = viewInflated.findViewById(R.id.input);
                final ToggleButton thermalDeviceStatus = viewInflated.findViewById(R.id.fevo_device_status);
                final ToggleButton setMode = viewInflated.findViewById(R.id.set_manual_mode);
                Log.i(TAG, "onOptionsItemSelected: " + userSession.getThermalDeviceMode());
                if (userSession.getThermalDeviceMode() == 1) {
                    setMode.setChecked(true);
                    setMode.setEnabled(false);
                } else {
                    setMode.setEnabled(true);
                }

                setMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        String data = "S.MOD,1\r\n";
                        if (usbService != null) { // if UsbService was correctly binded, Send data
                            usbService.write(data.getBytes());
                            Log.i(TAG, data + " Command Sent");
                            toastIconSucc("Mode updated.");
                        } else {
                            toastIconError("Device not connected. Please try again.");
                        }

                    }
                });
                thermalDeviceStatus.setChecked(userSession.getThermalDeviceStatus());
                input.setText(String.valueOf(userSession.getThreshold()));
                builder.setView(viewInflated);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (thermalDeviceStatus.isChecked()) {

                            if (input.getText().toString().isEmpty()) {
                                toastIconError("Please provide proper temperature value");
                            } else {
                                float timeout = Float.parseFloat(input.getText().toString());
                                if (timeout > 80 && timeout <= 100) {
                                    userSession.setThreshold(timeout);
                                    dialog.dismiss();
                                } else {
                                    toastIconError("temperature value should be within 80 to 100 minutes");
                                }
                            }
                        }
                        userSession.setThermalDeviceStatus(true);

                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                break;


        }


        return super.onOptionsItemSelected(item);
    }

    private void clearAttendance() {
        showProgressDialog();
        try {
            SplashActivity.databaseHandler.deleteEmpAttendanceRecords();
        } catch (Exception e) {

        }
        JsonObject jsonObject = getJsonObjectss();
        ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ClearAttResponce> clearAttendanceRecords = apiClient.getClearAttendanceRecords(jsonObject);
        clearAttendanceRecords.enqueue(new Callback<ClearAttResponce>() {
            @Override
            public void onResponse(Call<ClearAttResponce> call, Response<ClearAttResponce> response) {
                if (response.isSuccessful()) {
                    ClearAttResponce clearAtt = response.body();
                    if (!clearAtt.getError()) {
                        dismissProgressDailog();
                        toastIconSucc(ConstantValues.ATTENDANCE_CLEARED);
                    } else {
                        dismissProgressDailog();
                        toastIconError(clearAtt.getMessage());
                    }

                } else {
                    dismissProgressDailog();
                    toastIconError(getString(R.string.No_Response_from_server_500));
                }

            }

            @Override
            public void onFailure(Call<ClearAttResponce> call, Throwable t) {
                dismissProgressDailog();
                toastIconError(getString(R.string.No_Response_from_server_500));
            }
        });
    }

    public void changeCustomerDetailes() {
        if (SplashActivity.databaseHandler == null)
            return;
        List<getCustomerAllDitailes> cutsomerDetalesList = SplashActivity.databaseHandler.getCustomerAllrecords();
        byte[] custImg = null;
        String customerName = null;
        String BranchName = null;
        if (cutsomerDetalesList != null) {
            custImg = cutsomerDetalesList.get(0).getCustomerLogo();
            customerName = cutsomerDetalesList.get(0).getCustomerName();
            BranchName = cutsomerDetalesList.get(0).getBranchName();
            if (custImg != null) {
                Bitmap customerImag = BitmapFactory.decodeByteArray(custImg, 0, custImg.length);
                View headerLayout = nav_view.getHeaderView(0);
                customerLogo = headerLayout.findViewById(R.id.customerlogo);
                customerLogo.setImageBitmap(customerImag);
            }
            if (customerName != null) {
                View headerLayout = nav_view.getHeaderView(0);
                customername = headerLayout.findViewById(R.id.cutsomername);
                customername.setText(customerName);
            }

            if (BranchName != null) {
                View headerLayout = nav_view.getHeaderView(0);
                branchName = headerLayout.findViewById(R.id.branchname);

                if (BranchName.equalsIgnoreCase(customerName)) {
                    branchName.setText(null);
                } else {
                    branchName.setText(BranchName);
                }
            }

        } else {
            toastIconError("Sorry Customer Detailes not found!");
            Intent intent = new Intent(CustomerDashBoard.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            userSession.setRegiCompleeted(false);
            startActivity(intent);
            finish();
            return;

        }

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

    private void toastIconSucc(final String msg) {
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.toast_icon_text, null);
        ((TextView) custom_view.findViewById(R.id.message)).setText(msg);
        //((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        ((CardView) custom_view.findViewById(R.id.parent_view)).setCardBackgroundColor(getResources().getColor(R.color.usetv));
        toast.setView(custom_view);
        toast.show();
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
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (!isConnected) {
            startActivity(new Intent(CustomerDashBoard.this, NoInternetActivity.class));

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
        setFilters();
        startService(UsbService.class, usbConnection, null); //
        //new GetLatestVersion().execute();
        invalidateOptionsMenu();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(broadcastReceiver);
            unregisterReceiver(mWifiScanReceiver);
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

    private void getConfiguration() {
        showProgressDialog();
        try {
            JSONObject customerDetails = new JSONObject();
            customerDetails.put("custId", userSession.getCustId());
            customerDetails.put("brId", userSession.getBrID());
            customerDetails.put("kioskNo", userSession.getKioskNumber());
            customerDetails.put("kioskCode", userSession.getAndroidDeviceID());
            Log.i(TAG, "getConfiguration: " + customerDetails.toString());
            JsonObject inputData = (JsonObject) new JsonParser().parse(customerDetails.toString());
            ApiInterface apiClient = ApiClient.getApiClient().create(ApiInterface.class);
            Call<ConfigurationDetails> configurationDetailsCall = apiClient.getCustomerConfiguration(inputData);
            configurationDetailsCall.enqueue(new Callback<ConfigurationDetails>() {
                @Override
                public void onResponse(Call<ConfigurationDetails> call, Response<ConfigurationDetails> response) {
                    try {
                        if (response.isSuccessful()) {
                            ConfigurationDetails configurationDetails = response.body();
                            /**
                             * IF FR is true then by default QR is available
                             *
                             */
                            if (!configurationDetails.getStatus().getError()) {
                                dismissProgressDailog();
                                try {
                                    String custId = String.valueOf(configurationDetails.getCustId());
                                    String brId = String.valueOf(configurationDetails.getBrId());
                                    byte[] convertwdImg = convertedImg(configurationDetails.getCustLogoFile());
                                    String customerName = configurationDetails.getCustName();
                                    String branchName = configurationDetails.getBrName();
                                    String brCode = configurationDetails.getBrCode();
                                    userSession.setBrID(configurationDetails.getBrId());
                                    userSession.setCustId(configurationDetails.getCustId());
                                    InsertCustomerDitales insertCustomerDitales = new InsertCustomerDitales(custId, brId, convertwdImg,
                                            customerName, branchName, brCode);
                                    boolean result = SplashActivity.databaseHandler.addCustomerDitailes(insertCustomerDitales);
                                    enableFR_QR();

                                    if (configurationDetails.getEmpBeaconsList().getBeaconMapDetailslist() != null && configurationDetails.getEmpBeaconsList().getBeaconMapDetailslist().size() > 0) {
                                        deviceBecondsList = configurationDetails.getEmpBeaconsList().getBeaconMapDetailslist();
                                        Log.i(TAG, "onResponse: " + deviceBecondsList.size());
                                    } else {
                                        deviceBecondsList = null;
                                    }
                                    if (userSession.getAndroidDeviceID().equals(configurationDetails.getKioskCode())) {
                                        chekWIFI_Range();
                                    } else {
                                        showError(ConstantValues.UPDATE_KIOSK_MESSAGE);
                                    }


                                    /*     if (configurationDetails.getCustLogoFile() != null && !configurationDetails.getCustLogoFile().isEmpty()) {
                                        byte[] convertwdImg = Util.convertedImg(configurationDetails.getCustLogoFile());
                                        SplashActivity.databaseHandler.updateCustomerLogo(convertwdImg);*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    enableFR_QR();
                                    // showBottomSheetDialog(getString(R.string.exeptionMsg));
                                }


                           /*     if (configurationDetails.getConfigList() != null && configurationDetails.getConfigList().size() > 0) {
                                    boolean isFRFound = false;
                                    boolean isQRFound = false;
                                    for (int i = 0; i < configurationDetails.getConfigList().size(); i++) {
                                        if (configurationDetails.getConfigList().get(i).getConfig().equalsIgnoreCase(FACIAL_Recognition) &&
                                                configurationDetails.getConfigList().get(i).getStatusFlag()) {
                                            isFRFound = true;
                                        }
                                        if (configurationDetails.getConfigList().get(i).getConfig().equalsIgnoreCase(QR_Code) &&
                                                configurationDetails.getConfigList().get(i).getStatusFlag()) {
                                            isQRFound = true;
                                        }
                                    }
                                    dismissProgressDailog();
                                    enableFR_QR();

                                    *//**
                                 *  Modified by Nagaraj 01-12-2020
                                 *  Resion: WE reMove
                                 *//*



                                 *//*
                                    if (isFRFound) {
                                        dismissProgressDailog();
                                        enableFR_QR();
                                    } else if (isQRFound) {
                                        dismissProgressDailog();
                                        enableQR();
                                    } else {
                                        dismissProgressDailog();
                                        showBottomSheetDialog("Configuration details not available for customer");

                                        Intent intent = new Intent(CustomerDashBoard.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }*//*



                                 *//*      if (configurationDetails.getEmpBeaconsList().getBeaconMapDetailslist() != null && configurationDetails.getEmpBeaconsList().getBeaconMapDetailslist().size() > 0) {
                                        deviceBecondsList = configurationDetails.getEmpBeaconsList().getBeaconMapDetailslist();
                                        Log.i(TAG, "onResponse: " + deviceBecondsList.size());
                                    } else {
                                        deviceBecondsList = null;
                                    }
                                    if (userSession.getAndroidDeviceID().equals(configurationDetails.getKioskCode())) {
                                        chekWIFI_Range();
                                    } else {
                                        showError(ConstantValues.UPDATE_KIOSK_MESSAGE);
                                    }*//*
                                } else {
                                    dismissProgressDailog();
                                    showBottomSheetDialog("Configuration details not available for customer");
                                }*/
                            } else {
                                enableFR_QR();
                                dismissProgressDailog();
                                // showBottomSheetDialog(configurationDetails.getStatus().getMessage());
                            }

                        } else {
                            dismissProgressDailog();
                            //showBottomSheetDialog("Unable to get customer configuration details..!");
                            enableFR_QR();
                            /**
                             *  Created By Nagaraj 03-12-2020 we need to run Offline
                             */

                        }
                    } catch (Exception e) {
                        dismissProgressDailog();
                        enableFR_QR();
                        /**
                         *  Created By Nagaraj 03-12-2020 we need to run Offline
                         */
                        // showBottomSheetDialog(e.getMessage());


                    }
                }

                @Override
                public void onFailure(Call<ConfigurationDetails> call, Throwable t) {
                    dismissProgressDailog();

                    enableFR_QR();
                    /**
                     *  Created By Nagaraj 03-12-2020 we need to run Offline
                     */
                    // showBottomSheetDialog(t.getMessage());
                }
            });
        } catch (Exception e) {
            dismissProgressDailog();
            enableFR_QR();
            /**
             *  Created By Nagaraj 03-12-2020 we need to run Offline
             */
            //showBottomSheetDialog(e.getMessage());
        }

    }

    public byte[] convertedImg(String customerLogo) {
        byte[] data = Base64.decode(customerLogo, Base64.DEFAULT);
        return data;
    }

    public void enableFR_QR() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentcantainer, new Customer_Home_Fragment(this))
                .commit();
        registerForBroadcaseReceier();
        initToolbar();
        initNavigationMenu();
        changeCustomerDetailes();
    }

    private void showBottomSheetDialog(final String errorMsg) {
        try {
            if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            if (mBottomSheetDialog != null && mBottomSheetDialog.isShowing()) {
                return;
            }
            final View view = getLayoutInflater().inflate(R.layout.sheet_basic, null);
            mBottomSheetDialog = new BottomSheetDialog(CustomerDashBoard.this);
            TextView tvErrorMsg = view.findViewById(R.id.tv_msg);
            tvErrorMsg.setText(errorMsg);
            view.findViewById(R.id.btn_retry).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog();
                    mBottomSheetDialog.dismiss();
                    getConfiguration();

                }
            });

            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            mBottomSheetDialog.show();
            mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    mBottomSheetDialog = null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void enableQR() {
        Intent intent = new Intent(CustomerDashBoard.this, QRActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showMsg(String msg) {
        try {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        toolBarMenu = menu;
        MenuItem settingsItem = menu.findItem(R.id.status);

        if (userSession.isFevoDeviceConnected()) {
            // set your desired icon here based on a flag if you like
            settingsItem.setIcon(getResources().getDrawable(R.drawable.ic_check_circle));
        } else {
            settingsItem.setIcon(getResources().getDrawable(R.drawable.ic_error_outline_black_24dp));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void showUpdateDialogBox(String msg) {
        try {
            final Dialog dialog = new Dialog(CustomerDashBoard.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_upadte, null);
            TextView leavemsg = dialogView.findViewById(R.id.latestvesrion);
            leavemsg.setText("Latest Version " + msg);
            dialog.setContentView(dialogView);
            dialog.setCancelable(false);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.findViewById(R.id.bt_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.findViewById(R.id.bt_update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lng.lngattendance")));

                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public JsonObject getJsonObjectss() {
        try {
            JSONObject empDitales = new JSONObject();
            empDitales.put("brId", userSession.getBrID());
            empDitales.put("custId", userSession.getCustId());
            empDitales.put("dates", DateTimeUtil.getonlydate(userSession.getServerDateAndTime()));
            //empDitales.put("blkId", userSession.getBlockId());
            JsonObject inputData = (JsonObject) new JsonParser().parse(empDitales.toString());
            return inputData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void checkpermistion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("TAG", "onRequestPermissionsResult" + requestCode);
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                int granted = 0;
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        granted++;
                    }
                }
                if (granted != 2) {
                    showMessage("Please provide the required permission ");
                }
                startLocationUpdates();
                return;
        }

    }

    public void showMessage(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        try {
                            Uri uri = Uri.fromParts("package", MarkAttendanceActivity.PACKAGE_NAME, null);
                            intent.setData(uri);
                            startActivity(intent);
                        } catch (Exception e) {
                            Uri uri = Uri.fromParts("package", "com.lng.demoluxand", null);
                            intent.setData(uri);
                            startActivity(intent);
                            e.printStackTrace();
                        }


                    }
                })
                .setCancelable(false) // cancel with button only
                .show();
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        Log.i(TAG, "startLocationUpdates: ");
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.d("TAG", "Lat  settings are satisfied.");
                        //noinspection MissingPermission
                        if (ActivityCompat.checkSelfPermission(CustomerDashBoard.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CustomerDashBoard.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        // updateUI();
                        updateLocationUI();
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " + "location settings ");

                                Log.d("TAG", "Lat   Location settings are not satisfied. Attempting to upgrade ");

                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(CustomerDashBoard.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);
                                Toast.makeText(CustomerDashBoard.this, errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        // updateUI();
                        updateLocationUI();
                    }
                });
    }

    private void updateLocationUI() {
        try {
            if (mCurrentLocation != null) {
                String locationAaddress = com.lng.lngattendancesystem.Utilities.Util.getLocationLatLongAddress(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), CustomerDashBoard.this);
                // set lat long
                if (String.valueOf(mCurrentLocation.getLatitude()) != null && String.valueOf(mCurrentLocation.getLongitude()) != null) {

                    userSession.setLattitude(String.valueOf(mCurrentLocation.getLatitude()));
                    userSession.setLongitude(String.valueOf(mCurrentLocation.getLongitude()));
                }
                if (locationAaddress != null) {

                    userSession.setLocationAddress(locationAaddress);
                } else {
                    userSession.setLocationAddress("No Location Data");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUESTING_LOCATION_UPDATES);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
            }
            //updateUI();
            updateLocationUI();
        }
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (id) {
            case 1:
                builder.setTitle("Backup your current setting")
                        .setMessage("Do you want to backup your current settings")
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {


                                        try {


                                            File sd = Environment
                                                    .getExternalStorageDirectory();

                                            if (sd.canWrite()) {
                                                File sdPath = new File(sd + "//"
                                                        + "Mydb");
                                                if (!sdPath.exists())
                                                    sdPath.mkdir();

                                                String backupDBFile = "AttendanceDb" +
                                                        GetCurrentTime("dd-MM-yyyy HH:mm:ss")
                                                        + FTYPE;

                                                backupDBFile = backupDBFile
                                                        .replaceAll(":", "-");
                                                File backupDB = new File(sdPath,
                                                        backupDBFile);
                                                File currentDB = getApplicationContext()
                                                        .getDatabasePath(DatabaseHandler.DATABASE_NAME);

                                                @SuppressWarnings("resource")
                                                FileChannel src = new FileInputStream(
                                                        currentDB).getChannel();
                                                @SuppressWarnings("resource")
                                                FileChannel dst = new FileOutputStream(
                                                        backupDB).getChannel();

                                                dst.transferFrom(src, 0, src.size());
                                                src.close();
                                                dst.close();
                                                Toast.makeText(CustomerDashBoard.this, "App back up in "
                                                        + backupDBFile, Toast.LENGTH_SHORT).show();

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Log.e(TAG,
                                                    "Exception in onCreateDialog - BACKUP_DATABASE "
                                                            + e.toString());
                                            Toast.makeText(CustomerDashBoard.this, "Failed to backup app settings.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }).setNegativeButton("NO", null);
                break;
            case 2:
                builder.setTitle("Choose your backup file");
                if (mFileList == null) {
                    Log.e(TAG, "Showing file picker before loading the file list");
                    dialog = builder.create();
                    return dialog;
                }
                builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChosenFile = mFileList[which];

                        // Dont fail to Restart all stopped threads again later

                        try {

                            Log.v(TAG, "Stopping all running thread.");

                            File currentDB = getApplicationContext()
                                    .getDatabasePath(DatabaseHandler.DATABASE_NAME);
                            File sd = Environment.getExternalStorageDirectory();

                            if (NotFoundMsg.equalsIgnoreCase(mChosenFile))
                                return;

                            mChosenFile = mStorageFolder + "//" + mChosenFile;

                            File backupDB = new File(sd, mChosenFile);

                            @SuppressWarnings("resource")
                            FileChannel src = new FileInputStream(backupDB)
                                    .getChannel();
                            @SuppressWarnings("resource")
                            FileChannel dst = new FileOutputStream(currentDB)
                                    .getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();


                            ShowToast("App restore completed successfully");
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,
                                    "Exception in onCreateDialog - RESTORE_DATABASE "
                                            + e.toString());
                            ShowToast("Data restore aborted,please re-try");
                        }
                    }
                });

                break;
            case 3:
                builder.setTitle("Choose your backup file");
                if (mFileList == null) {
                    Log.e(TAG, "Showing file picker before loading the file list");
                    dialog = builder.create();
                    return dialog;
                }
                builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mChosenFile = mFileList[which];


                        try {
                            String filename = mChosenFile;
                            boolean status = Util.restore(CustomerDashBoard.this, filename);
                            ShowToast("Status : " + status);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG,
                                    "Exception in onCreateDialog - RESTORE_DATABASE "
                                            + e.toString());
                            ShowToast("Data restore aborted,please re-try");
                        }
                    }
                });

                break;

        }

        dialog = builder.show();
        return dialog;
    }

    private void backup() {
        try {
            String filename1 = userSession.getCustomerCode() + "-" + userSession.getKioskNumber() + "-" + GetCurrentTime("dd-MM-yyyy");
            if (com.lng.lngattendancesystem.Utilities.Util.backup(CustomerDashBoard.this, filename1)) {
                File sd = Environment
                        .getExternalStorageDirectory();
                if (sd.canWrite()) {
                    File sdPath = new File(sd + "//"
                            + ConstantValues.DB_FILE_BACKUP_FOLDER);
                    if (!sdPath.exists())
                        sdPath.mkdir();

                    String backupDBFile = filename1 + ConstantValues.DB_TYPE;


                    File backupDB = new File(sdPath,
                            backupDBFile);
                    File currentDB = getApplicationContext()
                            .getDatabasePath(DatabaseHandler.DATABASE_NAME);

                    @SuppressWarnings("resource")
                    FileChannel src = new FileInputStream(
                            currentDB).getChannel();
                    @SuppressWarnings("resource")
                    FileChannel dst = new FileOutputStream(
                            backupDB).getChannel();

                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    toastIconSucc("Backup created successfully.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void restore() {
        LoadDBFile();
        LoadMMFile();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose your backup file");
        builder.setItems(mmFileList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mChosenFile = mmFileList[which].replace(".dat", "");
                boolean found = false;
                for (int i = 0; i < dbFileList.length; i++) {
                    if (dbFileList[i].replace(".bak", "").equalsIgnoreCase(mChosenFile)) {
                        found = true;
                        break;
                    }
                }


                if (found) {
                    try {
                        String filename = mmFileList[which];
                        boolean status = Util.restore(CustomerDashBoard.this, filename);

                        if (status) {
                            mChosenFile += ".bak";
                            File currentDB = getApplicationContext()
                                    .getDatabasePath(DatabaseHandler.DATABASE_NAME);
                            File sd = Environment.getExternalStorageDirectory();

                            if (NotFoundMsg.equalsIgnoreCase(mChosenFile))
                                return;

                            mChosenFile = ConstantValues.DB_FILE_BACKUP_FOLDER + "//" + mChosenFile;

                            File backupDB = new File(sd, mChosenFile);

                            @SuppressWarnings("resource")
                            FileChannel src = new FileInputStream(backupDB)
                                    .getChannel();
                            @SuppressWarnings("resource")
                            FileChannel dst = new FileOutputStream(currentDB)
                                    .getChannel();

                            dst.transferFrom(src, 0, src.size());
                            src.close();
                            dst.close();
                            toastIconSucc("App restore completed successfully");
                        } else {
                            toastIconError("Sorry..! Something went wrong try again.");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG,
                                "Exception in onCreateDialog - RESTORE_DATABASE "
                                        + e.toString());
                        toastIconError("Data restore aborted,please re-try");
                    }

                } else {
                    toastIconError("Sorry..! Backup files not found");
                }
            }
        });
        builder.show();
    }


    private void listFiles() {
        try {
            File presentDBFileFolder = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//" + ConstantValues.DB_FILE_BACKUP_FOLDER);


            File presentDatFileFolder = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//" + ConstantValues.DAT_FILE_BACKUP_FOLDER);

            if (presentDatFileFolder.isDirectory()) {

                File[] presentDatFileList = presentDatFileFolder.listFiles();

                Arrays.sort(presentDatFileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return Long.compare(o1.lastModified(), o2.lastModified());
                    }
                });

                for (int i = 5; i < presentDatFileList.length; i++) {
                    presentDatFileList[i].delete();
                }

            }
            if (presentDBFileFolder.isDirectory()) {
                File[] presentDBFileList = presentDBFileFolder.listFiles();

                Arrays.sort(presentDBFileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return Long.compare(o1.lastModified(), o2.lastModified());
                    }
                });

                for (int i = 5; i < presentDBFileList.length; i++) {
                    presentDBFileList[i].delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void ShowToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void LoadFileList(final int type) {
        if (type == 1) {
            mPath = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//" + mStorageFolder);
        } else {
            mPath = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//" + mStorageFolder1);
        }


        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (type == 1)
                        return filename.contains(FTYPE);
                    return filename.contains(".dat");

                }
            };
            mFileList = mPath.list(filter);
        } else {
            mFileList = new String[]{NotFoundMsg};
        }
    }

    private void LoadDBFile() {
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
        } else {
            dbFileList = new String[]{NotFoundMsg};
        }
    }

    private void LoadMMFile() {
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
        } else {
            mmFileList = new String[]{NotFoundMsg};
        }
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    public void chekWIFI_Range() {
        try {
            if (deviceBecondsList != null && deviceBecondsList.size() > 0) {
                if (wifiManager != null && wifiManager.isWifiEnabled()) {
                    boolean isBeconeFound = false;
                    if (CustomerDashBoard.WIFI_AVAILABLE_SCAN_LIST != null && CustomerDashBoard.WIFI_AVAILABLE_SCAN_LIST.size() > 0) {
                        for (int i = 0; i < deviceBecondsList.size(); i++) {
                            for (int j = 0; j < CustomerDashBoard.WIFI_AVAILABLE_SCAN_LIST.size(); j++) {
                                if (deviceBecondsList.get(i).getBeaconCode().equalsIgnoreCase(CustomerDashBoard.WIFI_AVAILABLE_SCAN_LIST.get(j).SSID)) {
                                    isBeconeFound = true;
                                    break;
                                }
                            }
                            if (isBeconeFound) {
                                break;
                            }
                        }
                        if (!isBeconeFound) {
                            showError("Kiosk seems to be out of Office Range.");
                        }
                    }
                } else {
                    showError("Please Enable WIFI..!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(CustomerDashBoard.this);
        LayoutInflater inflater = LayoutInflater.from(CustomerDashBoard.this);
        View dialogView = inflater.inflate(R.layout.warning, null);
        TextView value = dialogView.findViewById(R.id.msg);
        value.setText(msg);
        builder.setCancelable(false)
                .setView(dialogView);
        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void OnDeviceStatusChange(boolean status) {
        invalidateOptionsMenu();
    }

    private class MyHandler extends Handler {

        Context context;

        public MyHandler(Context context) {
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    Log.i("Test", "Received1: " + data);
                    responce += (data).trim();
                    Log.i("Test", "Received2: " + responce);
                    if (responce != null && !responce.isEmpty() && !responce.trim().isEmpty() && (responce.equalsIgnoreCase("S.MOD,1") || responce.equalsIgnoreCase("F.MOD,1"))) {
                        //Toast.makeText(context, "Device is fine. It is Manual mode", Toast.LENGTH_SHORT).show();
                        userSession.setThermalMode(1);
                        responce = "";
                        Log.i("Test", "Received2: setting thermal mode 1");
                    }
                    Log.i("Test", "handleMessage: leng: " + responce.length() + (responce.length() >= 7 &&
                            !responce.equalsIgnoreCase("S.MOD,1") && !responce.equalsIgnoreCase("F.MOD,1")));
                    if (responce.length() >= 7 && !responce.equalsIgnoreCase("S.MOD,1")) {
                        responce = "";
                        userSession.setThermalMode(-1);
                        Log.i("Test", "Received2: setting thermal mode -1");

                    }

                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(context, "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(context, "DSR_CHANGE", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public class GetLatestVersion extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                String pkgname = getPackageName();
                //latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" + pkgname+ "&hl=en")
                latestVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=com.lng.lngattendance&hl=en_IN&gl=US")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return latestVersion;
        }


        @Override
        protected void onPostExecute(String s) {
            currentVersion = BuildConfig.VERSION_NAME;

            String versionName = null;
            if (latestVersion != null) {
                if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                    showUpdateDialogBox(latestVersion);
                }
            }
        }
    }


}

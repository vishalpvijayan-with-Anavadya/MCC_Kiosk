package com.lng.lngattendancesystem.BroadCastReciever;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.widget.Toast;

import com.lng.lngattendancesystem.CallBackInterfaces.ReceiveDeviceConnectionStatus;
import com.lng.lngattendancesystem.SerialPortCommunication.UsbService;
import com.lng.lngattendancesystem.Utilities.CrashReport.Helper;
import com.lng.lngattendancesystem.Utilities.UserSession;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Set;

public class LngAttendance extends Application {
    private static LngAttendance mInstance;
    public UsbService usbService;
    private MyHandler mHandler;
    public static final String CHANNEL_ID = "exampleServiceChannel";

    /**
     * Hold version employeeID.
     */
    String VersionName;
    /**
     * Hold package employeeID.
     */
    String PackageName;
    /**
     * Hold FilePath.
     */
    String FilePath;
    /**
     * Hold phone model.
     */
    String PhoneModel;
    /**
     * Hold version of android.
     */
    String AndroidVersion;
    /**
     * Hold board details.
     */
    String Board;
    /**
     * Hold brand employeeID.
     */
    String Brand;
    /**
     * Design of device.
     */
    String Device;
    /**
     * Display type.
     */
    String Display;
    // String CPU_ABI;
    /**
     * Finger print type.
     */
    String FingerPrint;
    /**
     * Host employeeID.
     */
    String Host;
    /**
     * Device Id.
     */
    String ID;
    /**
     * Manufacturer employeeID.
     */
    String Manufacturer;
    /**
     * Model employeeID of device.
     */
    String Model;
    /**
     * Name of the product.
     */
    String Product;
    /**
     * Tag describing the build.
     */
    String Tags;
    /**
     * Build time.
     */
    long Time;
    /**
     * The type of build.
     */
    String Type;
    /**
     *
     */
    String User;

    /**
     * OS Info
     */

    String OS_VERSION;
    private Context context;

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
    public UserSession userSession;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    //Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    //Toast.makeText(context, "FEVO Device is connected", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(true);

                    req();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    //Toast.makeText(context, "FEVO USB Permission not granted", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(false);


                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    // Toast.makeText(context, "FEVO No USB connected", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(false);


                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    //Toast.makeText(context, "FEVO USB disconnected", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(false);


                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    //Toast.makeText(context, "FEVO USB device not supported", Toast.LENGTH_SHORT).show();
                    userSession.setFevoDeviceStatus(false);
                    break;
                case UsbService.ACTION_USB_ATTACHED: // USB NOT SUPPORTED
                    userSession.setFevoDeviceStatus(false);
                    //Toast.makeText(context, "FEVO ACTION_USB_ATTACHED", Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };


    String responce = "";

    public static synchronized LngAttendance getInstance() {
        return mInstance;
    }

    private void req() {
        String data = "F.MOD\r\n";
        if (usbService != null) { // if UsbService was correctly binded, Send data
            usbService.write(data.getBytes());
            Log.i("Command", data + " Command Sent");
            responce = "";
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userSession = new UserSession(this);
        mInstance = this;
        mHandler = new MyHandler(this);
        context = getApplicationContext();
        onResume();
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
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

    void onResume() {
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
        setFilters();  // Start listening notifications from UsbService
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
                        //  Toast.makeText(context, "Device is fine. It is Manual mode", Toast.LENGTH_SHORT).show();
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


    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * @return total internal memory size.
     */
    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /***
     * Get Android Version employeeID
     */
    private String getVersionName() {
        StringBuilder builder = new StringBuilder();
        builder.append("android : ").append(Build.VERSION.RELEASE);

        Field[] fields = Build.VERSION_CODES.class.getFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            int fieldValue = -1;

            try {
                fieldValue = field.getInt(new Object());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (fieldValue == Build.VERSION.SDK_INT) {
                builder.append(" : ").append(fieldName).append(" : ");
                builder.append("sdk=").append(fieldValue);
            }
        }
        return new String(builder);
    }


    /**
     * Recolt informations.
     *
     * @param context
     */
    void RecoltInformations(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            VersionName = pi.versionName;
            // Package employeeID
            PackageName = pi.packageName;
            // Files dir for storing the stack traces
            FilePath = context.getFilesDir().getAbsolutePath();
            // Device model
            PhoneModel = Build.MODEL;
            // Android version
            AndroidVersion = Build.VERSION.RELEASE;
            // OS VERSION
            OS_VERSION = getVersionName();
            Board = Build.BOARD;
            Brand = Build.BRAND;
            // CPU_ABI = android.os.Build.;
            Device = Build.DEVICE;
            Display = Build.DISPLAY;
            FingerPrint = Build.FINGERPRINT;
            Host = Build.HOST;
            ID = Build.ID;
            // Manufacturer = android.os.Build.;
            Model = Build.MODEL;
            Product = Build.PRODUCT;
            Tags = Build.TAGS;
            Time = Build.TIME;
            Type = Build.TYPE;
            User = Build.USER;
        } catch (PackageManager.NameNotFoundException e) {
            Log.v("VM", e.getMessage());
        }
    }

    /**
     * Create information string with all available details.
     *
     * @return information string.
     */
    public String CreateInformationString() {
        String ReturnVal = "";
        ReturnVal += "Version : " + VersionName;
        ReturnVal += "\r\n";
        ReturnVal += "Package : " + PackageName;
        ReturnVal += "\r\n";
        ReturnVal += "FilePath : " + FilePath;
        ReturnVal += "\r\n";
        ReturnVal += "Phone Model" + PhoneModel;
        ReturnVal += "\r\n";
        ReturnVal += "Android Version : " + AndroidVersion;
        ReturnVal += "\r\n";
        ReturnVal += "OS Version : " + OS_VERSION;
        ReturnVal += "\r\n";
        ReturnVal += "Board : " + Board;
        ReturnVal += "\r\n";
        ReturnVal += "Brand : " + Brand;
        ReturnVal += "\r\n";
        ReturnVal += "Device : " + Device;
        ReturnVal += "\r\n";
        ReturnVal += "Display : " + Display;
        ReturnVal += "\r\n";
        ReturnVal += "Finger Print : " + FingerPrint;
        ReturnVal += "\r\n";
        ReturnVal += "Host : " + Host;
        ReturnVal += "\r\n";
        ReturnVal += "ID : " + ID;
        ReturnVal += "\r\n";
        ReturnVal += "Model : " + Model;
        ReturnVal += "\r\n";
        ReturnVal += "Product : " + Product;
        ReturnVal += "\r\n";
        ReturnVal += "Tags : " + Tags;
        ReturnVal += "\r\n";
        ReturnVal += "Time : " + Time;
        ReturnVal += "\r\n";
        ReturnVal += "Type : " + Type;
        ReturnVal += "\r\n";
        ReturnVal += "User : " + User;
        ReturnVal += "\r\n";
        ReturnVal += "Total Internal memory : " + getTotalInternalMemorySize();
        ReturnVal += "\r\n";
        ReturnVal += "Available Internal memory : "
                + getAvailableInternalMemorySize();
        ReturnVal += "\r\n";
        return ReturnVal;
    }

    /**
     * Generate report.
     */
    public String reportUncaughtException(Thread thread, Throwable exception) {
        RecoltInformations(context);
        String Report = "";
        Date CurDate = new Date();
        Report += "Error Report collected on : " + CurDate.toString();
        Report += "\r\n";
        Report += "\r\n";
        Report += "Informations :";
        Report += "\r\n";
        Report += "==============";
        Report += "\r\n";

        Report += CreateInformationString();
        Report += "\r\n";
        Report += "\r\n";
        Report += "Shared prefference Data :";
        Report += "\r\n";
        Report += "==============";
        Report += collectCustomerEmpDetails();

        Report += "\n\n";
        Report += "\n\n";
        Report += "Stack : \n";
        Report += "======= \r\n";
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        exception.printStackTrace(printWriter);
        String stacktrace = result.toString();
        Report += stacktrace;

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        Throwable cause = exception.getCause();
        if (cause != null) {
            Report += "\r\n";
            Report += "Cause : \r\n";
            Report += "======= \r\n";
        }

        while (cause != null) {
            cause.printStackTrace(printWriter);
            Report += result.toString();
            cause = cause.getCause();
        }

        printWriter.close();
        Report += "\r\n****  End of current Report ***";
        new Helper().SaveAsFile(Report, context);

        return Report;
        /*
        Intent intent = new Intent(context, ReportCrashResultActivity.class);
        intent.putExtra("STACKTRACE", Report);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // required when
        // starting from

        // Application

        context.startActivity(intent);
        Process.killProcess(Process.myPid());
        System.exit(0);

        startActivity(new Intent(context, ReportCrashResultActivity.class));
        finish();
        android.os.Process.killProcess(android.os.Process.myPid());*/
    }


    private String collectCustomerEmpDetails() {
        UserSession userSession = new UserSession(context);
        return userSession.collectAllDetails();
    }
}

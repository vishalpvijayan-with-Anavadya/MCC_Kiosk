package com.lng.lngattendancesystem.Utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.lng.lngattendancesystem.R;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class Util {


    public static Date last2DayDate(String serverDateTIme) {
        try {
            Date expectedDate = Util.getDateIsbefore(serverDateTIme);
            Date twoDayBeforeDate = Util.get2DaysBeforeDay(expectedDate);
            return twoDayBeforeDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // return Date
    public static Date getDateIsbefore(String serverTime) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date parsedDate = df.parse(serverTime);
            return parsedDate;
        } catch (Exception e) {
            return null;
        }

    }


    public static String getPresentDateTime() {
        try {

            DateFormat df = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
            Date dateobj = new Date();
            return df.format(dateobj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getTimeInOnehourDefrence(Date currentDate, Date dbDate) {
        try {
            long diff = currentDate.getTime() - dbDate.getTime();
            long diffSeconds = diff / 1000;
            long diffMinutes = diff / (60 * 1000);
            long diffHours = diff / (60 * 60 * 1000);
            System.out.println("Time in seconds: " + diffSeconds + " seconds.");
            System.out.println("Time in minutes: " + diffMinutes + " minutes.");
            System.out.println("Time in hours: " + diffHours + " hours.");
            return diffHours;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    // return Date

    public static Date getDateObjectForValidation(String serverTime) {
        try {
            String format = "dd-MM-yyyy";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date parsedDate = df.parse(serverTime);
            return parsedDate;
        } catch (Exception e) {
            return null;
        }

    }

    public static Date getDateObjectForValidationIn(String serverTime) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date parsedDate = df.parse(serverTime);
            return parsedDate;
        } catch (Exception e) {
            return null;
        }

    }


    public static Date parseStringToDate(String date) {
        try {
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            Date parsedaDate = dateFormat.parse(date);
            return parsedaDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static Date getAddOneHour(Date date) {
        int MILLIS_IN_DAY = 1 * 1000 * 60 * 60;
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        long dateLongValue = date.getTime() + MILLIS_IN_DAY;
        Date newDate = new Date(dateLongValue);
        return newDate;
    }


    public static Date get2DaysBeforeDay(Date date) {
        int MILLIS_IN_DAY = 2 * 1000 * 60 * 60 * 24;
        //SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        long dateLongValue = date.getTime() - MILLIS_IN_DAY;
        Date newDate = new Date(dateLongValue);
        return newDate;
    }

    public static ProgressDialog getProgressDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);

        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static String getonlydate(String parsedDate) {
        try {
            String format = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date getDateObject = df.parse(parsedDate);
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            String date = sf.format(getDateObject);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1000); // mTimeout is in seconds
                urlc.connect();
                return urlc.getResponseCode() == 200;
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }

        return false;

    }

    public static boolean backup(Context context, String filename) {
        String external = context.getApplicationInfo().dataDir + "/" + ConstantValues.FACE_DETAILS_FILE;
        String templatePath = Environment.getExternalStorageDirectory() + "/" + ConstantValues.DAT_FILE_BACKUP_FOLDER + "/" + filename + ConstantValues.DAT_TYPE;
        try {
            File s = new File(external);
            File d = new File(templatePath);
            return copyFile(s, d);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static byte[] convertedImg(String customerLogo) {
        byte[] data = Base64.decode(customerLogo, Base64.DEFAULT);
        return data;
    }

    private static boolean copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        return false;
    }


    public static String getLocationLatLongAddress(double latitude, double longitude, Context ctx) {
        int count = 0;
        do {
            count++;
            try {
                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(ctx, Locale.getDefault());
                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                // Perticuarly extract details from location details
/*
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();*/
                return address;

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (count < 3);
        return null;
    }

    public static ProgressDialog getProcessDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(context.getString(R.string.progressbarmsg));
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public static void appendLog(String text) {
        try {
            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Facetek/logs.txt";
            Log.i("Path", "appendLog: " + fullPath);
            File logFile = new File(fullPath);
            if (!logFile.getParentFile().exists())
                logFile.getParentFile().mkdirs();
            if (!logFile.exists())
                logFile.createNewFile();

            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(mydate + ": " + text);
            buf.newLine();
            buf.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static byte[] ConverBitmapToByte(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    public static String parseLongDateToString(Long serverDateTime) {
        try {
            String dateString = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date(serverDateTime));
            return dateString;
        } catch (Exception e) {
            throw e;
        }
    }

    public static String getConvertTimeZone(String serverDateTime) throws Exception {
        try {
            String format = "EEE, d MMM yyyy HH:mm:ss Z";
            SimpleDateFormat df = new SimpleDateFormat(format);
            Date parsedDate = df.parse(String.valueOf(serverDateTime));
            String pattern = "yyyy-MM-dd'T'HH:mm:ss";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            String date = sf.format(parsedDate);
            return date;
        } catch (Exception e) {
            throw e;
        }
    }

    public static Date getPriviousDate() {
        Date date = new Date();
        int MILLIS_IN_DAY = (1000 * 60 * 60 * 24) * 5;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentdate = dateFormat.format(date.getTime());
        Log.i("Test", "getPriviousDate:  " + currentdate);
        long gg = date.getTime() - MILLIS_IN_DAY;
        Date newDate = new Date(gg);
        String privi = dateFormat.format(newDate.getTime());
        Log.i("Test", "privi:  " + privi);

        return newDate;
    }

    public static Date formatStrDate(String strDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            return dateFormat.parse(strDate);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean restore1(Context context, String filename) {
        String external = Environment.getExternalStorageDirectory() + "/LNGFRDatBackup/" + filename;
        String templatePath = context.getApplicationInfo().dataDir + "/" + ConstantValues.FACE_DETAILS_FILE;
        try {
            File s = new File(external);
            File d = new File(templatePath);
            return copyFile(s, d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean restore(Context context, String filename) {
        String external = Environment.getExternalStorageDirectory() + "/" + ConstantValues.DAT_FILE_BACKUP_FOLDER + "/" + filename;
        String templatePath = context.getApplicationInfo().dataDir + "/" + ConstantValues.FACE_DETAILS_FILE;
        try {
            File s = new File(external);
            File d = new File(templatePath);
            return copyFile(s, d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public  static String getTimeAdd(Date serverDateTime) {

        try {
            String pattern = "h:mm a";
            SimpleDateFormat sf = new SimpleDateFormat(pattern);
            String date = sf.format(serverDateTime);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}

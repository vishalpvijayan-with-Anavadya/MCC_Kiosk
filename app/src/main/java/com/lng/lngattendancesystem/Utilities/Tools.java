package com.lng.lngattendancesystem.Utilities;

import android.app.Activity;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import com.lng.lngattendancesystem.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Tools {

    public static void setSystemBarColor(Activity act) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = act.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(act.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static String getFormattedDateShort(Long dateTime) {
        SimpleDateFormat newFormat = new SimpleDateFormat("dd-MMM-yyyy");
        return newFormat.format(new Date(dateTime));
    }


}

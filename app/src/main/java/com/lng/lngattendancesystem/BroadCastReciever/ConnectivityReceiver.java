package com.lng.lngattendancesystem.BroadCastReciever;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;
    public static boolean isInternetConnected = false;

    public ConnectivityReceiver() {
        super();
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) LngAttendance.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isInternetConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isInternetConnected;
    }

    @Override
    public void onReceive(Context context, Intent arg1) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            if (isConnected) isInternetConnected = true;
            // connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
package com.nebrasapps.otlb;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDexApplication;

import com.nebrasapps.otlb.storage.SharedData;


/**
 * ============================================================================
 *                                       🤓
 *              Copyright (c) 12/01/2018 - NebrasApps All Rights Reserved
 *                    www.nebrasapps.com - Hi@nebrasapps.com
 * ============================================================================
 */


public class MainApp extends MultiDexApplication {
    private SharedData sharedData;

    @Override
    public void onCreate()
    {
        super.onCreate();
        sharedData = new SharedData(this);

    }

    public static boolean isNetworkAvailable(final Context context) {
        try {
            ConnectivityManager connection = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo Wifi = connection
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (Wifi != null && Wifi.isConnectedOrConnecting())
                return true;

            NetworkInfo Mobile = connection
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (Mobile != null && Mobile.isConnectedOrConnecting())
                return true;

            NetworkInfo activeNet = connection.getActiveNetworkInfo();
            if (activeNet != null && activeNet.isConnectedOrConnecting())
                return true;



            return false;
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

}

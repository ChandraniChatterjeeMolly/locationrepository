package com.example.chandranichatterjee.myapplicationloc.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class LocUtil {

    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}

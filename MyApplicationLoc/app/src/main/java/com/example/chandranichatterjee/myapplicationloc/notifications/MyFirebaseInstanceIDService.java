package com.example.chandranichatterjee.myapplicationloc.notifications;

import com.google.firebase.iid.FirebaseInstanceIdService;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.edit().putString("firebase token", refreshedToken).apply();
    }
}

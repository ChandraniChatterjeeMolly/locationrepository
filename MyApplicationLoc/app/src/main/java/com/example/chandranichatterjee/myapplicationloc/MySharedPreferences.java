package com.example.chandranichatterjee.myapplicationloc;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private final SharedPreferences preferences;
    public String FIRST_LOGIN = "firstLogin";

    public MySharedPreferences(Context context) {
        preferences = context.getSharedPreferences("pref.xml",Context.MODE_PRIVATE);
    }

    public boolean isFirstLogin(){
        return preferences.getBoolean(FIRST_LOGIN, true);
    }

    public void setFirstLogin(boolean isFirstLogin){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_LOGIN, isFirstLogin);
        editor.apply();
    }
}

package com.example.chandranichatterjee.myapplicationloc;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private final SharedPreferences preferences;
    public String FIRST_LOGIN = "firstLogin";
    public String Firebase_Token = "firebase token";

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

    public String getFirebase_Token() {
        return preferences.getString(Firebase_Token,null);
    }

    public void setFirebase_Token(String firebase_Token) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Firebase_Token, firebase_Token);
        editor.apply();
    }
}


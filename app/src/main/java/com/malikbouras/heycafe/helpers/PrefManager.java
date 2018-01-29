package com.malikbouras.heycafe.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 */
public class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "pref";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String SERVER_TOKEN = "server_token";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    // shared pref mode
    private int PRIVATE_MODE = 0;

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, false);
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public String getServerToken() {
        return pref.getString(SERVER_TOKEN, null);
    }

    public void setServerToken(String token) {
        editor.putString(SERVER_TOKEN, token);
        editor.commit();
    }

}

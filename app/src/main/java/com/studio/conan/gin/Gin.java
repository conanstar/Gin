package com.studio.conan.gin;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class Gin extends Application {

    private static final String PREFS_NAME = "gin";
    private static Gin sInstance;
    private SharedPreferences mPrefs;

    private static final String PREF_KEY_LOGIN = "flag";  // login flag

    public static final int LOGIN_TYPE_NOT_LOGIN    = 0;
    public static final int LOGIN_TYPE_STAFF        = 1;
    public static final int LOGIN_TYPE_EMPLOYEE     = 2;

    public static Gin getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        sInstance.initInstance();
    }

    private void initInstance() {
        Context context = getApplicationContext();
        mPrefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    public boolean cleanPreferences() {
        SharedPreferences.Editor edit = mPrefs.edit();
        return edit.clear().commit();
    }

    public int getLoginType() {
        return mPrefs.getInt(PREF_KEY_LOGIN, 0);
    }

    public void setLoginType(int loginType) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putInt(PREF_KEY_LOGIN, loginType);
        edit.apply();
    }
}

package com.studio.conan.gin;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class Gin extends Application {

    private static final String PREFS_NAME = "gin";
    private static Gin sInstance;
    private SharedPreferences mPrefs;

    private static final String PREF_KEY_LOGIN = "flag";    // login flag
    private static final String PREF_KEY_PSK = "psk";       // PSK code
    private static final String PREF_KEY_PSID = "psid";     // PS ID
    private static final String PREF_KEY_SN = "sn";         // Serial number

    public static final int LOGIN_TYPE_NOT_LOGIN    = 0;
    public static final int LOGIN_TYPE_STAFF        = 1;
    //public static final int LOGIN_TYPE_EMPLOYEE     = 2;

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

    public String getPSK() {
        return  mPrefs.getString(PREF_KEY_PSK, null);
    }

    public void setPSK(String psk) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREF_KEY_PSK, psk);
        edit.apply();
    }

    public String getPSID() {
        return mPrefs.getString(PREF_KEY_PSID, null);
    }

    public String getSN() {
        return mPrefs.getString(PREF_KEY_SN, null);
    }

    public void setPSIDAndSN(String psid, String sn) {
        SharedPreferences.Editor edit = mPrefs.edit();
        edit.putString(PREF_KEY_PSID, psid);
        edit.putString(PREF_KEY_SN, sn);
        edit.apply();
    }
}

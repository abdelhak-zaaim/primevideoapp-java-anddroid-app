package com.cinefilmz.tv.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.cinefilmz.tv.BuildConfig;

public class PrefManager {

    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    private static Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;
    public static String pushRID = "0";
    // Shared preferences file name
    private static final String PREF_NAME = BuildConfig.APPLICATION_ID;

    private static PrefManager sInstance;

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String LOGIN_ID = "LOGIN";

    public static final String NIGHT_MODE = "NIGHT_MODE";
    private boolean isNightModeEnabled = false;

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static final PrefManager getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PrefManager(context.getApplicationContext());
        }
        return sInstance;
    }

    public void setLoginId(String id) {
        editor.putString(LOGIN_ID, id);
        editor.commit();
    }

    public String getLoginId() {
        return pref.getString(LOGIN_ID, "0");
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setBool(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
    }

    public boolean getBool(String key) {
        return pref.getBoolean(key, true);
    }

    public void setValue(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getValue(String key) {
        return pref.getString(key, "0");
    }

    //rtl
    public static void forceRTLIfSupported(Window window, Activity activity) {
        /*if (activity.getResources().getString(R.string.isRTL).equals("true")){}*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Log.e("forceRTLIfSupported", "SelectedLanguageId => " + LocaleUtils.getSelectedLanguageId());
            if ("ar".equals(LocaleUtils.getSelectedLanguageId())) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            } else {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
        } else {
            window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        }
    }

    public static boolean isNightModeEnabled() {
        return pref.getBoolean(NIGHT_MODE, false);
    }

    public void setIsNightModeEnabled(boolean isNightModeEnabled) {
        this.isNightModeEnabled = isNightModeEnabled;
        editor.putBoolean(NIGHT_MODE, isNightModeEnabled);
        editor.commit();
    }

}
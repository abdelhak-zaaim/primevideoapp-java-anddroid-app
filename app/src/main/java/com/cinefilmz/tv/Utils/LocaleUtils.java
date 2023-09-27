package com.cinefilmz.tv.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.SecureRandom;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class LocaleUtils {

    public static final String ENGLISH = "en";
    public static final String ARABIC = "ar";
    public static final String FRENCH = "fr";
    public static final String HINDI = "hi";
    public static final String SWAHILI = "sw";
    public static final String TAMIL = "ta";
    public static final String TELUGU = "te";
    public static final String ZULU = "zu";
    public static final String PASHTO = "ps";

    public static void initialize(Context context, @LocaleDef String defaultLanguage) {
        setLocale(context, defaultLanguage);
    }

    public static boolean setLocale(Context context, @LocaleDef String language) {
        return updateResources(context, language);
    }

    private static boolean updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        context.createConfigurationContext(configuration);
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return true;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ENGLISH, ARABIC, FRENCH, HINDI, SWAHILI, TAMIL, TELUGU, ZULU, PASHTO})
    public @interface LocaleDef {
        String[] SUPPORTED_LOCALES = {ENGLISH, ARABIC, FRENCH, HINDI, SWAHILI, TAMIL, TELUGU, ZULU, PASHTO};
    }

    private static SharedPreferences getDefaultSharedPreference(Context context) {
        if (PreferenceManager.getDefaultSharedPreferences(MyApp.getInstance().getApplicationContext()) != null)
            return PreferenceManager.getDefaultSharedPreferences(MyApp.getInstance().getApplicationContext());
        else
            return null;
    }

    public static void setSelectedLanguageId(String id) {
        final SharedPreferences prefs = getDefaultSharedPreference(MyApp.getInstance().getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("app_language_id", id);
        editor.apply();
    }

    public static String getSelectedLanguageId() {
        return getDefaultSharedPreference(MyApp.getInstance().getApplicationContext())
                .getString("app_language_id", "en");
    }

    public static byte[] generateKey(String password) throws Exception {
        byte[] keyStart = password.getBytes("UTF-8");

        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        @SuppressLint("DeletedProvider") SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(keyStart);
        kgen.init(128, sr);
        SecretKey skey = kgen.generateKey();
        return skey.getEncoded();
    }

    public static byte[] encodeFile(byte[] key, byte[] fileData) throws Exception {

        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(fileData);

        return encrypted;
    }

    public static byte[] decodeFile(byte[] key, byte[] fileData) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);

        return decrypted;
    }

}
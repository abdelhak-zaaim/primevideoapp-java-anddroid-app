package com.cinefilmz.tv.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.onesignal.OneSignal;
import com.orhanobut.hawk.Hawk;

import java.io.File;

public class MyApp extends Application {

    private static MyApp mInstance = null;
    private static final String TAG = MyApp.class.getSimpleName();

    private static AppOpenManager appOpenManager;

    PrefManager prefManager;
    public static SimpleCache simpleCache = null;
    public static LeastRecentlyUsedCacheEvictor leastRecentlyUsedCacheEvictor = null;
    public static ExoDatabaseProvider exoDatabaseProvider = null;
    public static Long exoPlayerCacheSize = (long) (90 * 1024 * 1024);

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        prefManager = new PrefManager(this);

        // Initialize the Google Mobile Ads SDK
        MobileAds.initialize(getApplicationContext());

        /* App Open Ads Init */
        //appOpenManager = new AppOpenManager(this);

        // Initialize the Audience Network SDK (Facebook ads)
        AudienceNetworkAds.initialize(this);
        //Initialize the Hawk for secure content
        Hawk.init(getApplicationContext()).build();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Enable verbose OneSignal logging to debug issues if needed.
            OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
            // OneSignal Initialization
            OneSignal.initWithContext(this);
            OneSignal.setAppId(Constant.ONESIGNAL_APP_ID);
            // promptForPushNotifications will show the native Android notification permission prompt.
            OneSignal.promptForPushNotifications();
        }
        // Initialize the Firebase
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        UsedCacheEvictor();

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                Log.e(TAG, "onActivityCreated => " + activity.getLocalClassName());
                /* Below code will restrict screen capture */
                Log.e(TAG, "setScreenCapture => " + Constant.setScreenCapture);
                if (Constant.setScreenCapture) {
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.e(TAG, "onActivityStarted => " + activity.getLocalClassName());
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Log.e(TAG, "onActivityResumed => " + activity.getLocalClassName());
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Log.e(TAG, "onActivityPaused => " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.e(TAG, "onActivityStopped => " + activity.getLocalClassName());
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
                Log.e(TAG, "onActivitySaveInstanceState => " + activity.getLocalClassName());
            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.e(TAG, "onActivityDestroyed => " + activity.getLocalClassName());
            }
        });

    }

    public void initAppLanguage(Context context) {
        LocaleUtils.initialize(context, LocaleUtils.getSelectedLanguageId());
    }

    public Context getContext() {
        return mInstance.getContext();
    }

    public static synchronized MyApp getInstance() {
        if (mInstance == null) {
            mInstance = new MyApp();
        }
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

    public void UsedCacheEvictor() {
        if (leastRecentlyUsedCacheEvictor == null) {
            leastRecentlyUsedCacheEvictor = new LeastRecentlyUsedCacheEvictor(exoPlayerCacheSize);
        }

        if (exoDatabaseProvider != null) {
            exoDatabaseProvider = new ExoDatabaseProvider(this);
        }

        if (simpleCache == null) {
            simpleCache = new SimpleCache(getCacheDir(), leastRecentlyUsedCacheEvictor, exoDatabaseProvider);
            if (simpleCache.getCacheSpace() >= 400207768) {
                CacheClear();
            }
            Log.i("TAG", "onCreate: " + simpleCache.getCacheSpace());
        }
    }

    public void CacheClear() {
        try {
            File dir = getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
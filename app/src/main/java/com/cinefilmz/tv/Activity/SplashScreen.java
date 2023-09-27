package com.cinefilmz.tv.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cinefilmz.tv.Model.GeneralSettings.GeneralSettings;
import com.cinefilmz.tv.Utils.MyApp;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.ConnectivityReceiver;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.google.android.material.snackbar.Snackbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    private PrefManager prefManager;
    private boolean isConnected;
    private Intent splashIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.HideNavigation(this);
        MyApp.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_splash);
        PrefManager.forceRTLIfSupported(getWindow(), this);
// Check if the user has granted the POST_NOTIFICATIONS permission.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Request the permission from the user.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1212);
        }

        init();
        printKeyHash();
    }

    private void init() {
        prefManager = new PrefManager(SplashScreen.this);
        checkConnection();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyApp.getInstance().setConnectivityListener(this);

        isConnected = ConnectivityReceiver.isConnected();
        if (isConnected) {

            generalSetting();
        } else {
            showSnack(isConnected);
        }
    }

    private void generalSetting() {









        Call<GeneralSettings> call = BaseURL.getVideoAPI().general_setting();
        call.enqueue(new Callback<GeneralSettings>() {
            @Override
            public void onResponse(Call<GeneralSettings> call, Response<GeneralSettings> response) {




                try {
                    Log.e("general_setting", "Exception => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        for (int i = 0; i < response.body().getResult().size(); i++) {
                            prefManager.setValue(response.body().getResult().get(i).getKey(), response.body().getResult().get(i).getValue());
                            Log.e("" + response.body().getResult().get(i).getKey(), " => " + response.body().getResult().get(i).getValue());
                        }
                        //check first time or not
                        if (prefManager.isFirstTimeLaunch()) {
                            splashIntent = new Intent(SplashScreen.this, WelcomeActivity.class);
                        } else {
                            //login or not
                            splashIntent = new Intent(SplashScreen.this, MainActivity.class);
                        }
                        if (splashIntent != null) {
                            startActivity(splashIntent);
                        }
                        finish();
                    }
                } catch (Exception e) {
                    Log.e("general_setting", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<GeneralSettings> call, Throwable t) {
                Log.e("general_setting", "onFailure => " + t.getMessage());
            }
        });
    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        if (isConnected) {

          //  onStart();
        } else {
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                    "" + getResources().getString(R.string.sorry_not_connected_to_internet), Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.show();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getApplication().getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(),
                        Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
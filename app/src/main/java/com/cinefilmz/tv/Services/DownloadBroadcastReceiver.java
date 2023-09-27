package com.cinefilmz.tv.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cinefilmz.tv.Utils.Functions;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String getAction = intent.getAction();
            Log.e("<<<<=== getAction", "===>>>> " + getAction);
            switch (getAction) {
                case "startshowservice":
                    setShowEpiData(context, intent);
                    return;

                case "startservice":
                    setVideoData(context, intent);
                    return;
            }
        } catch (Exception e) {
            Log.e("onReceive", "Exception ==>>> " + e.getMessage());
        }
    }

    private void setVideoData(Context context, Intent intent) {
        // Post the UI updating code to our Handler
        if (intent != null) {
            if (Functions.isMyServiceRunning(context, SecureDownloadService.class)) {
                Log.e("<<<<=== setVideoData", "Service Running ===>>>>");

                boolean downloadCompleted = intent.getBooleanExtra("downloadComplete", false);
                Log.e("setVideoData", "currentProgress ==>>> " + intent.getParcelableExtra("downloadProgress"));
                Log.e("setVideoData", "downloadCompleted ==DBR==>>>>" + downloadCompleted);
            } else {
                Log.e("<<<<=== setVideoData", "Service Stop ===>>>>");
            }
        }
    }

    private void setShowEpiData(Context context, Intent intent) {
        // Post the UI updating code to our Handler
        Log.e("<<<<=== setShowEpiData", "Service Status ===>>>> " + Functions.isMyServiceRunning(context, SecureDownloadService.class));

        boolean downloadCompleted = intent.getBooleanExtra("downloadComplete", false);

        Log.e("setShowEpiData", "currentProgress ==>>> " + intent.getParcelableExtra("showDownloadProgress"));
        Log.e("setShowEpiData", "downloadCompleted ==DBR==>>>>" + downloadCompleted);
    }

}
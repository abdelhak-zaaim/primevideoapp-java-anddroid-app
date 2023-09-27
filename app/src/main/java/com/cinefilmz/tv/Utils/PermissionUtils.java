package com.cinefilmz.tv.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.cinefilmz.tv.Interface.FragmentCallBack;
import com.cinefilmz.tv.R;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {
    Activity activity;
    ActivityResultLauncher<String[]> mPermissionResult;

    public PermissionUtils(Activity activity, ActivityResultLauncher<String[]> mPermissionResult) {
        this.activity = activity;
        this.mPermissionResult = mPermissionResult;
    }

    /*Storage Permission*/
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void takeStoragePermission() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.MANAGE_EXTERNAL_STORAGE};
        }
        mPermissionResult.launch(permissions);
    }

    public void showStoragePermissionDailog(String message) {
        List<String> permissionStatusList = new ArrayList<>();
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        }
        for (String keyStr : permissions) {
            permissionStatusList.add(Functions.getPermissionStatus(activity, keyStr));
        }

        Log.e("permissionStatusList", "" + permissionStatusList);
        if (permissionStatusList.contains("denied")) {
            Functions.showDoubleButtonAlert(activity, activity.getString(R.string.permission_alert), message,
                    activity.getString(R.string.cancel_), activity.getString(R.string.permission), false, new FragmentCallBack() {
                        @Override
                        public void onResponse(Bundle bundle) {
                            if (bundle.getBoolean("isShow", false)) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                    takeStoragePermission();
                                }
                            }
                        }
                    });
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            takeStoragePermission();
        }

    }

    public boolean isStoragePermissionGranted() {
        int readExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED);
        } else {
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED);
        }
    }
    /*Storage Permission*/

    /*Storage & Camera Permission*/
    public void takeStorageCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            mPermissionResult.launch(permissions);
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            mPermissionResult.launch(permissions);
        }
    }

    public void showStorageCameraPermissionDailog(String message) {
        List<String> permissionStatusList = new ArrayList<>();
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        } else {
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        }

        for (String keyStr : permissions) {
            permissionStatusList.add(Functions.getPermissionStatus(activity, keyStr));
        }

        Log.e("permissionStatusList", "" + permissionStatusList);
        if (permissionStatusList.contains("denied")) {
            Functions.showDoubleButtonAlert(activity, activity.getString(R.string.permission_alert), message,
                    activity.getString(R.string.cancel_), activity.getString(R.string.permission), false, new FragmentCallBack() {
                        @Override
                        public void onResponse(Bundle bundle) {
                            if (bundle.getBoolean("isShow", false)) {
                                takeStorageCameraPermission();
                            }
                        }
                    });
            return;
        }
        takeStorageCameraPermission();

    }

    public boolean isStorageCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            return (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED && cameraPermission == PackageManager.PERMISSION_GRANTED);
        } else {
            int readExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int cameraPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
            return (readExternalStoragePermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED && cameraPermission == PackageManager.PERMISSION_GRANTED);
        }
    }
    /*Storage & Camera Permission*/

    /*Read Phone State Permission*/
    public void takeReadPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            String[] permissions = {Manifest.permission.READ_PHONE_STATE};
            mPermissionResult.launch(permissions);
        } else {
            String[] permissions = {Manifest.permission.READ_PHONE_STATE};
            mPermissionResult.launch(permissions);
        }
    }

    public void showReadPhonePermissionDailog(String message) {
        List<String> permissionStatusList = new ArrayList<>();
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        } else {
            permissions = new String[]{Manifest.permission.READ_PHONE_STATE};
        }
        for (String keyStr : permissions) {
            permissionStatusList.add(Functions.getPermissionStatus(activity, keyStr));
        }

        Log.e("permissionStatusList", "" + permissionStatusList);
        if (permissionStatusList.contains("denied")) {
            Functions.showDoubleButtonAlert(activity, activity.getString(R.string.permission_alert), message,
                    activity.getString(R.string.cancel_), activity.getString(R.string.permission), false, new FragmentCallBack() {
                        @Override
                        public void onResponse(Bundle bundle) {
                            if (bundle.getBoolean("isShow", false)) {
                                takeReadPhonePermission();
                            }
                        }
                    });
            return;
        }
        takeReadPhonePermission();

    }

    public boolean isReadPhonePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            int readPhonePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
            return (readPhonePermission == PackageManager.PERMISSION_GRANTED);
        } else {
            int readPhonePermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
            return (readPhonePermission == PackageManager.PERMISSION_GRANTED);
        }
    }
    /*Read Phone State Permission*/

}
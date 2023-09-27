package com.cinefilmz.tv.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cinefilmz.tv.Interface.FragmentCallBack;
import com.cinefilmz.tv.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class Functions {

    // change the color of status bar into black
    public static void blackStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();

        int flags = view.getSystemUiVisibility();
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
        activity.getWindow().setStatusBarColor(Color.BLACK);
    }

    public static void PrintHashKey(Context context) {
        try {
            final PackageInfo info = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                final MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String hashKey = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                Log.d("Functions", "KeyHash : " + hashKey);
            }
        } catch (Exception e) {
            Log.e("Functions", "error:", e);
        }
    }

    // change the color of status bar into white
    public static void whiteStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        int flags = view.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
        activity.getWindow().setStatusBarColor(Color.WHITE);
    }

    // close the keybord
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // open the keyboard
    public static void showKeyboard(Activity activity) {
        View view = activity.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    // change string value to integer
    public static int parseInterger(String value) {
        if (value != null && !value.equals("")) {
            return Integer.parseInt(value);
        } else
            return 0;
    }

    // format the count value
    public static String getSuffix(String value) {
        try {
            if (value != null && (!value.equals("") && !value.equalsIgnoreCase("null"))) {
                long count = Long.parseLong(value);
                if (count < 1000)
                    return "" + count;
                int exp = (int) (Math.log(count) / Math.log(1000));
                return String.format(Locale.ENGLISH, "%.1f %c",
                        count / Math.pow(1000, exp),
                        "kMBTPE".charAt(exp - 1));
            } else {
                return "0";
            }
        } catch (Exception e) {
            return value;
        }
    }

    // return  the rundom string of given length
    public static String getRandomString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz" + "0123456789";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }

        return sb.toString();
    }

    public static String removeSpecialChar(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "");
    }

    // show loader of simple messages
    public static void showAlert(Context context, String title, String Message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(Message)
                .setNegativeButton("" + context.getResources().getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static void showDoubleButtonAlert(Context context, String title, String message, String negTitle,
                                             String posTitle, boolean isCancelable, FragmentCallBack callBack) {
        final Dialog dialog = new Dialog(context);
        dialog.setCancelable(isCancelable);
        dialog.setContentView(R.layout.show_double_button_new_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView tvtitle, tvMessage, tvPositive, tvNegative;
        tvtitle = dialog.findViewById(R.id.tvtitle);
        tvMessage = dialog.findViewById(R.id.tvMessage);
        tvNegative = dialog.findViewById(R.id.tvNegative);
        tvPositive = dialog.findViewById(R.id.tvPositive);


        tvtitle.setText(title);
        tvMessage.setText(message);
        tvNegative.setText(negTitle);
        tvPositive.setText(posTitle);

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isShow", false);
                callBack.onResponse(bundle);
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putBoolean("isShow", true);
                callBack.onResponse(bundle);
            }
        });
        dialog.show();
    }

    public static boolean checkTimeDiffernce(Calendar current_cal, String date) {
        try {
            Calendar date_cal = Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;

            Log.d("Functions", "Tag : " + difference);

            if (difference < 0) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }

    public static String changeDateTodayYesterday(Context context, String date) {
        try {
            Calendar current_cal = Calendar.getInstance();

            Calendar date_cal = Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;

            if (difference < 86400) {
                if (current_cal.get(Calendar.DAY_OF_YEAR) - date_cal.get(Calendar.DAY_OF_YEAR) == 0) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
                    return sdf.format(d);
                } else
                    return context.getString(R.string.yesterday);
            } else if (difference < 172800) {
                return context.getString(R.string.yesterday);
            } else
                return (difference / 86400) + context.getString(R.string.day_ago);

        } catch (Exception e) {
            return date;
        }
    }

    // return the random string of 16 char
    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz" + "1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 16) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static String getStringSizeOfFile(long size) {

        DecimalFormat df = new DecimalFormat("00");

        float sizeKb = 1024.0f;
        float sizeMb = sizeKb * sizeKb;
        float sizeGb = sizeMb * sizeKb;
        float sizeTerra = sizeGb * sizeKb;

        if (size < sizeMb) {
            Log.e("Size of File", "==>>>>>> " + df.format(size / sizeKb) + " Kb");
            return df.format(size / sizeKb) + " Kb";
        } else if (size < sizeGb) {
            Log.e("Size of File", "==>>>>>> " + df.format(size / sizeMb) + " Mb");
            return df.format(size / sizeMb) + " Mb";
        } else if (size < sizeTerra) {
            Log.e("Size of File", "==>>>>>> " + df.format(size / sizeGb) + " Gb");
            return df.format(size / sizeGb) + " Gb";
        }

        return "";
    }

    public static String formatDuration(final long millis) {
        Log.e("formatDuration", "millis ==>>>>>> " + millis);
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / (1000 * 60)) % 60;
        long hours = millis / (1000 * 60 * 60);

        StringBuilder b = new StringBuilder();
        b.append(hours == 0 ? "" : hours < 10 ? String.valueOf("0" + hours) : String.valueOf(hours));
        if (hours != 0)
            b.append("h ");
        b.append(minutes == 0 ? "00" : minutes < 10 ? String.valueOf("0" + minutes) : String.valueOf(minutes));
        b.append("min ");
        b.append(seconds == 0 ? "00" : seconds < 10 ? String.valueOf("0" + seconds) : String.valueOf(seconds));
        b.append("s");

        Log.e("final", "Duration ==>>>>>> " + b);
        return b.toString();
    }

    public static String formatDurationInMinute(final long millis) {
        Log.e("formatDurationInMinute", "millis ==>>>>>> " + millis);
        long minutes = (millis / (1000 * 60)) % 60;

        StringBuilder b = new StringBuilder();
        b.append(minutes == 0 ? "00" : minutes < 10 ? "0" + minutes : String.valueOf(minutes));
        b.append(" min");

        Log.e("formatDurationInMinute", "Duration ==>>>>>> " + b);
        return b.toString();
    }

    public static long milliseconds(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh mm ss");
        try {
            Date mDate = sdf.parse(date);
            long timeInMilliseconds = mDate.getTime();
            Log.e("milliseconds", "Date in milli ::==>>> " + timeInMilliseconds);
            return timeInMilliseconds;
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }

    public static Boolean isConnectedToInternet(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            Log.e("Functions", "Exception : " + e.getMessage());
            return false;
        }
    }

    //check rational permission status
    public static String getPermissionStatus(Activity activity, String androidPermissionName) {
        Log.e("androidPermissionName", "" + androidPermissionName);
        if (ContextCompat.checkSelfPermission(activity, androidPermissionName) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, androidPermissionName)) {
                return "blocked";
            }
            return "denied";
        }
        return "granted";
    }

    //show permission setting screen
    public static void showPermissionSetting(Context context, String message) {
        showDoubleButtonAlert(context, context.getString(R.string.permission_alert), message,
                context.getString(R.string.cancel_), context.getString(R.string.settings), false, new FragmentCallBack() {
                    @Override
                    public void onResponse(Bundle bundle) {
                        if (bundle.getBoolean("isShow", false)) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    }
                });
    }

    //    check app is exist or not
    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static Dialog determinant_dialog;
    public static ProgressBar determinant_progress;

    public static void showDeterminentLoader(Context context, boolean outside_touch, boolean cancleable) {

        determinant_dialog = new Dialog(context);
        determinant_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        determinant_dialog.setContentView(R.layout.item_determinant_progress_layout);
        determinant_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.d_round_white_background));

        determinant_progress = determinant_dialog.findViewById(R.id.pbar);

        if (!outside_touch)
            determinant_dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            determinant_dialog.setCancelable(false);

        determinant_dialog.show();

    }

    public static void showLoadingProgress(int progress) {
        if (determinant_progress != null) {
            determinant_progress.setProgress(progress);
        }
    }

    public static void cancelDeterminentLoader() {
        if (determinant_dialog != null) {
            determinant_progress = null;
            determinant_dialog.cancel();
        }
    }

    public static File commonDocumentDirPath(String FolderName) {
        File dir = null;
        Log.e("=>FolderName", "" + FolderName);
        Log.e("=>is Android R", "" + (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + FolderName);
        } else {
            dir = new File(Environment.getExternalStorageDirectory() + "/" + FolderName);
        }

        // Make sure the path directory exists.
        if (!dir.exists()) {
            // Make it, if it doesn't exit
            boolean success = dir.mkdirs();
            if (!success) {
                dir = null;
            }
        }
        Log.e("==dir=>", "" + dir.getAbsolutePath());
        return dir;
    }

    public static String bitmapToBase64(Bitmap imagebitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    public static String getAppFolder(Context context) {
        return context.getExternalFilesDir(null).getPath() + "/";
    }

    public static String getAppFolder(Activity activity) {
        return activity.getExternalFilesDir(null).getPath() + "/";
    }

    public static String getCacheFolder(Activity activity) {
        return activity.getExternalCacheDir().getPath() + "/";
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
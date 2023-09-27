package com.cinefilmz.tv.PushNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.cinefilmz.tv.Activity.SplashScreen;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationExtenderExample extends Service implements OSRemoteNotificationReceivedHandler {

    public static final int NOTIFICATION_ID = 118;
    String title, message, bigpicture, url;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent osNotificationReceivedEvent) {

        title = osNotificationReceivedEvent.getNotification().getTitle();
        message = osNotificationReceivedEvent.getNotification().getBody();
        bigpicture = osNotificationReceivedEvent.getNotification().getBigPicture();
        Log.e("aaa - noti", "" + osNotificationReceivedEvent.getNotification().getAdditionalData().toString());
        try {
            PrefManager.pushRID = osNotificationReceivedEvent.getNotification().getAdditionalData().getString("radio_id");
            Log.e("onNotifiProcessing", "url => " + osNotificationReceivedEvent.getNotification().getLaunchURL());
            url = osNotificationReceivedEvent.getNotification().getLaunchURL();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("onNotifiProcessing", "Exception => " + e);
        }
        sendNotification(context);
    }

    private void sendNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        if (!PrefManager.pushRID.equals("0")) {
            intent = new Intent(context, SplashScreen.class);
            intent.putExtra("ispushnoti", true);
        } else if (url != null && !url.equals("false") && !url.trim().isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(context, SplashScreen.class);
        }

        NotificationChannel mChannel;
        String NOTIFICATION_CHANNEL_ID = "onlineradio_push";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Online Radio Channel";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            mChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        PendingIntent contentIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSound(uri)
                .setAutoCancel(true)
                .setChannelId(NOTIFICATION_CHANNEL_ID)
                .setLights(Color.RED, 800, 800)
                .setContentText(message);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));

        mBuilder.setContentTitle(title);
        mBuilder.setTicker(message);

        if (bigpicture != null) {
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)).setSummaryText(message));
        } else {
            mBuilder.setContentText(message);
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
        }
        return R.mipmap.ic_launcher_round;
    }

    private int getColour() {
        return 0xee2c7a;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

}
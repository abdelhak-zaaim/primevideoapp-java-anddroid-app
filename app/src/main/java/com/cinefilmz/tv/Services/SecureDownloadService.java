package com.cinefilmz.tv.Services;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import com.cinefilmz.tv.Activity.MainActivity;
import com.cinefilmz.tv.Model.DownloadShowModel.EpisodeItem;
import com.cinefilmz.tv.Model.DownloadShowModel.SessionItem;
import com.cinefilmz.tv.Model.DownloadVideoItem;
import com.cinefilmz.tv.Model.SectionDetailModel.Result;
import com.cinefilmz.tv.Model.SectionDetailModel.Session;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Interface.DownloadShowListener;
import com.cinefilmz.tv.Interface.DownloadVideoListener;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.ConnectivityReceiver;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class SecureDownloadService extends Service implements DownloadVideoListener, DownloadShowListener {

    private final IBinder mBinder = new LocalBinder();

    private static String TAG = SecureDownloadService.class.getSimpleName();
    private static int VIDEO_CHANNEL_ID = 111;
    private static int SHOW_CHANNEL_ID = 999;

    private PrefManager prefManager;
    private Result sectionDetailList;

    private List<Session> sessionList;
    private List<com.cinefilmz.tv.Model.VideoSeasonModel.Result> episodeList;
    private List<EpisodeItem> downloadedEpiList = new ArrayList<>();

    private String mainKeyAlias = "", downloadFrom = "", seasonName = "";
    private int timeInMillisec, sessionPos;
    private File mTargetFile = null;
    private boolean downloaded = false, mAllowRebind;

    public SecureDownloadService() {
        super();
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    public class LocalBinder extends Binder {
        public SecureDownloadService getService() {
            return SecureDownloadService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefManager = new PrefManager(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("onStartCommand", "intent ==>>> " + intent);
        Log.e("onStartCommand", "flags ==>>> " + flags);
        Log.e("onStartCommand", "startId ==>>> " + startId);

        if (intent != null && (intent.getAction().equals(Constant.VIDEO_DOWNLOAD_SERVICE) || intent.getAction().equals(Constant.SHOW_DOWNLOAD_SERVICE))) {
            downloadFrom = intent.getStringExtra("from");
            Log.e("onStartCommand", "downloadFrom ==>>> " + downloadFrom);

            if (downloadFrom.equalsIgnoreCase("MovieDetails")) {
                sectionDetailList = new Result();
                sectionDetailList = (Result) intent.getSerializableExtra("detailList");
                mainKeyAlias = intent.getStringExtra("secureKey");
                timeInMillisec = intent.getIntExtra("videoDuration", 0);

                Log.e("onStartCommand", "videoDuration ==>>> " + timeInMillisec);
                if (TextUtils.isEmpty(mainKeyAlias)) {
                    mainKeyAlias = "4J95qN8RxBP8hTpk";
                }
                Log.e("onStartCommand", "mainKeyAlias ==>>> " + mainKeyAlias);

                setVideoDownload();
            }
            if (downloadFrom.equalsIgnoreCase("ShowDetails")) {
                sectionDetailList = new Result();
                sectionDetailList = (Result) intent.getSerializableExtra("detailList");
                sessionPos = intent.getIntExtra("sessionPos", 0);
                sessionList = new ArrayList<>();
                sessionList = (List<Session>) intent.getSerializableExtra("sessionList");
                seasonName = intent.getStringExtra("seasonName");
                episodeList = new ArrayList<>();
                episodeList = (List<com.cinefilmz.tv.Model.VideoSeasonModel.Result>) intent.getSerializableExtra("episodeList");
                Log.e("onStartCommand", "sessionPos ==>>> " + sessionPos);
                Log.e("onStartCommand", "sessionList ==>>> " + sessionList.get(sessionPos).getName());
                Log.e("onStartCommand", "seasonName ==>>> " + seasonName);
                Log.e("onStartCommand", "episodeList ==>>> " + episodeList.size());

                setShowDownload();
            }

        } else {
            stopForeground(true);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    /* Single Video Download */
    private void setVideoDownload() {
        if (sectionDetailList != null) {
            try {
                if (ConnectivityReceiver.isConnected()) {
                    setVideoNotification();

                    mTargetFile = new File(Functions.getAppFolder(this) + "downloads" + "/", "" +
                            sectionDetailList.getName().replace(" ", "_").replace(".", "").replaceAll("[^\\.A-Za-z0-9_]", "")
                            + "_" + sectionDetailList.getId() + "" + prefManager.getLoginId() + "." + sectionDetailList.getVideoExtension());
                    Log.e("Download", "Path ==>>> " + mTargetFile);

                    if (!mTargetFile.exists()) {
                        mTargetFile.getParentFile().mkdirs();

                        if (!TextUtils.isEmpty(sectionDetailList.getVideo320())) {
                            new SecureVideoDownload(SecureDownloadService.this, sectionDetailList.getId(), mTargetFile, mainKeyAlias, SecureDownloadService.this)
                                    .execute("" + sectionDetailList.getVideo320());
                        } else {
                            Toasty.warning(this, "" + getResources().getString(R.string.invalid_url), Toasty.LENGTH_SHORT).show();
                        }

                    } else {
                        if (mTargetFile.length() > 0) {
                            try {
                                Uri imageUri = FileProvider.getUriForFile(SecureDownloadService.this,
                                        SecureDownloadService.this.getPackageName() + ".provider", mTargetFile);
                                ContentResolver contentResolver = SecureDownloadService.this.getContentResolver();
                                int deletefile = contentResolver.delete(imageUri, null, null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mTargetFile.delete();

                            mTargetFile = new File(Functions.getAppFolder(this) + "downloads" + "/", "" +
                                    sectionDetailList.getName().replace(" ", "_").replace(".", "").replaceAll("[^\\.A-Za-z0-9_]", "")
                                    + "_" + sectionDetailList.getId() + "" + prefManager.getLoginId() + "." + sectionDetailList.getVideoExtension());
                            Log.e("Download", "NEW Path ==>>> " + mTargetFile);
                            mTargetFile.getParentFile().mkdirs();

                            if (!TextUtils.isEmpty(sectionDetailList.getVideo320())) {
                                new SecureVideoDownload(SecureDownloadService.this, sectionDetailList.getId(), mTargetFile, mainKeyAlias, SecureDownloadService.this)
                                        .execute("" + sectionDetailList.getVideo320());
                            } else {
                                Toasty.warning(this, "" + getResources().getString(R.string.invalid_url), Toasty.LENGTH_SHORT).show();
                            }
                        } else {
                            Toasty.info(this, "" + getResources().getString(R.string.file_already_downloaded), Toasty.LENGTH_SHORT).show();
                            stopForeground(true);
                            stopSelf();
                        }
                    }

                } else {
                    Utility.showSnackBar((Activity) getApplicationContext(), "NoInternet", "");
                }
            } catch (Exception e) {
                Toasty.error(this, "Url/path not correct", Toasty.LENGTH_SHORT).show();
                stopForeground(true);
                stopSelf();
                e.printStackTrace();
            }

        } else {
            Toasty.error(this, "Url/path not correct", Toasty.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        }
    }

    private void setVideoNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Log.e("VIDEO SERVICE DATA", "Name =>>>>" + sectionDetailList.getName() + "; secureKey =>>>> " + mainKeyAlias);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("" + VIDEO_CHANNEL_ID,
                    "" + getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationBuilder = new NotificationCompat.Builder(this, "" + VIDEO_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(sectionDetailList.getName())
                .setContentText("" + getResources().getString(R.string.downloading))
                .setProgress(100, 0, false)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(VIDEO_CHANNEL_ID, notificationBuilder.build());
        startForeground(VIDEO_CHANNEL_ID, notificationBuilder.build());
    }

    private void onVideoDownloadComplete(boolean downloadComplete) {
        Log.e(TAG, "downloadComplete : ============================================> " + downloadComplete);
        Intent intent = new Intent(Constant.VIDEO_DOWNLOAD_SERVICE);
        intent.putExtra("downloadComplete", downloadComplete);
        sendBroadcast(intent);

        notificationBuilder.setSmallIcon(R.drawable.ic_file_download);
        notificationBuilder.setContentText("" + getResources().getString(R.string.file_has_been_downloaded));
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setOngoing(false);
        notificationManager.notify(VIDEO_CHANNEL_ID, notificationBuilder.build());

        Log.e(TAG, "mTargetFile ==>>Q>> " + mTargetFile.getPath());
        Log.e(TAG, "mainKeyAlias ==>>Q>> " + mainKeyAlias);

        DownloadVideoItem downloadVideoItem = new DownloadVideoItem(sectionDetailList.getId(), "" + sectionDetailList.getName(),
                "" + sectionDetailList.getVideoType(), "" + sectionDetailList.getTypeId(), "" + sectionDetailList.getUpcomingType(),
                "" + sectionDetailList.getReleaseYear(), "" + sectionDetailList.getAgeRestriction(),
                "" + sectionDetailList.getMaxVideoQuality(),
                "" + sectionDetailList.getReleaseTag(), Uri.fromFile(mTargetFile).getPath(),
                "" + sectionDetailList.getLandscape(), true, mTargetFile.length(),
                sectionDetailList.getVideoDuration() != null ? sectionDetailList.getVideoDuration() : timeInMillisec,
                sectionDetailList.getIsBookmark(), 1,
                sectionDetailList.getStopTime(), sectionDetailList.getId(), sectionDetailList.getId(),
                "" + mainKeyAlias, null);

        if (downloadFrom.equalsIgnoreCase("KidsDetails")) {

            List<DownloadVideoItem> myKidsVideoList = Hawk.get(Constant.hawkKIDSVIDEOList + prefManager.getLoginId());
            if (myKidsVideoList == null) {
                myKidsVideoList = new ArrayList<>();
            }
            checkKidsVideoInHawk(myKidsVideoList, "" + sectionDetailList.getId());
            myKidsVideoList.add(downloadVideoItem);
            Hawk.put(Constant.hawkKIDSVIDEOList + prefManager.getLoginId(), myKidsVideoList);

        } else {

            List<DownloadVideoItem> myVideoList = Hawk.get(Constant.hawkVIDEOList + prefManager.getLoginId());
            if (myVideoList == null) {
                myVideoList = new ArrayList<>();
            }
            checkVideoInHawk(myVideoList, "" + sectionDetailList.getId());
            myVideoList.add(downloadVideoItem);
            Hawk.put(Constant.hawkVIDEOList + prefManager.getLoginId(), myVideoList);
        }

        if (!mTargetFile.exists()) {
            Toasty.info(this, "" + getResources().getString(R.string.something_went_wrong), Toasty.LENGTH_SHORT).show();
        } else {
            Utility.addRemoveDownload(getApplicationContext(), "" + sectionDetailList.getId(),
                    "" + sectionDetailList.getVideoType(), "" + sectionDetailList.getTypeId(), "0");
            Toasty.success(this, "" + getResources().getString(R.string.file_has_been_downloaded), Toasty.LENGTH_SHORT).show();
        }

        this.stopForeground(false);
        this.stopSelf();
    }

    private void checkVideoInHawk(List<DownloadVideoItem> myVideoList, String videoID) {
        Log.e("checkVideoInHawk", "UserID ===> " + prefManager.getLoginId());
        Log.e("checkVideoInHawk", "videoID ===> " + videoID);
        for (int i = 0; i < myVideoList.size(); i++) {
            Log.e("Hawk", "itemID ==> " + myVideoList.get(i).getId());
            if (("" + myVideoList.get(i).getId()).equalsIgnoreCase(videoID)) {
                Log.e("myVideoList", "=======================> i = " + i);
                myVideoList.remove(myVideoList.get(i));
                Hawk.put(Constant.hawkVIDEOList + prefManager.getLoginId(), myVideoList);
            }
        }
    }

    private void checkKidsVideoInHawk(List<DownloadVideoItem> myKidsVideoList, String kidsVideoID) {
        Log.e("checkKidsVideoInHawk", "UserID ===> " + prefManager.getLoginId());
        Log.e("checkKidsVideoInHawk", "kidsVideoID ===> " + kidsVideoID);
        for (int i = 0; i < myKidsVideoList.size(); i++) {
            Log.e("Hawk", "itemID ==> " + myKidsVideoList.get(i).getId());
            if (("" + myKidsVideoList.get(i).getId()).equalsIgnoreCase(kidsVideoID)) {
                Log.e("myKidsVideoList", "=======================> i = " + i);
                myKidsVideoList.remove(myKidsVideoList.get(i));
                Hawk.put(Constant.hawkKIDSVIDEOList + prefManager.getLoginId(), myKidsVideoList);
            }
        }
    }

    private void updateVideoNotification(boolean downloadComplete, VideoDownloadProgress downloadProgress) {
        notificationBuilder.setProgress(100, downloadProgress.progress, false);
        notificationBuilder.setContentText("" + downloadProgress.progress + "%");
        notificationManager.notify(VIDEO_CHANNEL_ID, notificationBuilder.build());

        Intent intent = new Intent(Constant.VIDEO_DOWNLOAD_SERVICE);
        intent.putExtra("downloadComplete", downloadComplete);
        intent.putExtra("downloadProgress", downloadProgress);
        sendBroadcast(intent);
    }

    @Override
    public void onProgressUpdate(VideoDownloadProgress downloadProgress) {
        Log.e(TAG, "<<==::::== progress : " + downloadProgress.progress + " ==::::==>>");
        if (downloadProgress.progress < 100) {
            updateVideoNotification(false, downloadProgress);
        } else {
            updateVideoNotification(true, downloadProgress);
        }
    }

    @Override
    public void onStartDownload(String url) {
    }

    @Override
    public void OnDownloadCompleted() {
        downloaded = true;
        Log.e("DownloadCompleted", "::::==>>>>>> " + downloaded);
        onVideoDownloadComplete(downloaded);
    }

    @Override
    public void onCancelDownload() {
        this.stopForeground(false);
        this.stopSelf();
    }
    /* Single Video Download */

    /* Multiple Video (Show Episodes) Download */
    private void setShowDownload() {
        if (sectionDetailList != null && episodeList != null) {
            try {
                if (ConnectivityReceiver.isConnected()) {
                    setShowNotification();

                    new SecureShowDownload(SecureDownloadService.this, sectionDetailList,
                            seasonName, episodeList, SecureDownloadService.this).execute();
                } else {
                    Utility.showSnackBar((Activity) getApplicationContext(), "NoInternet", "");
                }
            } catch (Exception e) {
                Toasty.error(this, "Url/path not correct", Toasty.LENGTH_SHORT).show();
                stopForeground(true);
                stopSelf();
                e.printStackTrace();
            }

        } else {
            Toasty.error(this, "Url/path not correct", Toasty.LENGTH_SHORT).show();
            stopForeground(true);
            stopSelf();
        }
    }

    private void setShowNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Log.e("SHOW SERVICE DATA", "Name =>>>>" + sectionDetailList.getName() + "; seasonName =>>>> " + seasonName);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("" + SHOW_CHANNEL_ID,
                    "" + getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationBuilder = new NotificationCompat.Builder(this, "" + SHOW_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(sectionDetailList.getName())
                .setContentText("" + getResources().getString(R.string.downloading))
                .setProgress(100, 0, false)
                .setContentIntent(pendingIntent)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notificationManager.notify(SHOW_CHANNEL_ID, notificationBuilder.build());
        startForeground(SHOW_CHANNEL_ID, notificationBuilder.build());
    }

    private void onShowDownloadComplete(boolean downloadComplete) {
        Log.e(TAG, "downloadComplete : ============================================> " + downloadComplete);
        Intent intent = new Intent(Constant.SHOW_DOWNLOAD_SERVICE);
        intent.putExtra("downloadComplete", downloadComplete);
        sendBroadcast(intent);

        notificationBuilder.setSmallIcon(R.drawable.ic_file_download);
        notificationBuilder.setContentText("" + getResources().getString(R.string.file_has_been_downloaded));
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setOngoing(false);
        notificationManager.notify(SHOW_CHANNEL_ID, notificationBuilder.build());

        List<EpisodeItem> myEpisodeList = Hawk.get(Constant.hawkEPISODEList + prefManager.getLoginId()
                + "" + sessionList.get(sessionPos).getId() + "" + sectionDetailList.getId());
        if (myEpisodeList == null) {
            myEpisodeList = new ArrayList<>();
        }

        long totalFileSize = 0L, totalDuration = 0L;
        for (int i = 0; i < myEpisodeList.size(); i++) {
            /* Get Total File size from Download items */
            totalFileSize += myEpisodeList.get(i).getFileSize();

            /* Get Total Duration from Download items */
            totalDuration += Long.parseLong(myEpisodeList.get(i).getVideoDuration());
        }
        Log.e(TAG, "totalFileSize :===> " + totalFileSize);
        Log.e(TAG, "totalDuration :===> " + totalDuration);

        SessionItem sessionItem = new SessionItem(sessionList.get(sessionPos).getId(), sectionDetailList.getId(), sessionPos,
                "" + sessionList.get(sessionPos).getName(), sessionList.get(sessionPos).getStatus(),
                "" + sessionList.get(sessionPos).getCreatedAt(), "" + sessionList.get(sessionPos).getUpdatedAt(),
                myEpisodeList);

        List<SessionItem> mySessionList = Hawk.get(Constant.hawkSEASONList + prefManager.getLoginId() + "" + sectionDetailList.getId());
        if (mySessionList == null) {
            mySessionList = new ArrayList<>();
        }
        Utility.checkSeasonInHawk(mySessionList, "" + prefManager.getLoginId(), "" + sectionDetailList.getId(),
                "" + sessionList.get(sessionPos).getId());
        mySessionList.add(sessionItem);
        Hawk.put(Constant.hawkSEASONList + prefManager.getLoginId() + "" + sectionDetailList.getId(), mySessionList);

        Log.e(TAG, "mySessionList ==>>>> " + mySessionList.size());
        Log.e(TAG, "downloadedEpiList ==>>>> " + downloadedEpiList.size());

        DownloadVideoItem downloadVideoItem = new DownloadVideoItem(sectionDetailList.getId(), "" + sectionDetailList.getName(),
                "" + sectionDetailList.getVideoType(), "" + sectionDetailList.getTypeId(), "" + sectionDetailList.getUpcomingType(),
                "" + sectionDetailList.getReleaseYear(), "" + sectionDetailList.getAgeRestriction(),
                "" + sectionDetailList.getMaxVideoQuality(), "" + sectionDetailList.getReleaseTag(),
                "" + Functions.getAppFolder(getApplicationContext()) + "downloads" + "/" + sectionDetailList.getName() + "/" + seasonName,
                "" + sectionDetailList.getLandscape(), true, totalFileSize, totalDuration, sectionDetailList.getIsBookmark(),
                1, sectionDetailList.getStopTime(), sectionDetailList.getId(), sectionDetailList.getId(), "",
                mySessionList);

        List<DownloadVideoItem> myShowList = Hawk.get(Constant.hawkSHOWList + prefManager.getLoginId());
        if (myShowList == null) {
            myShowList = new ArrayList<>();
        }
        Log.e(TAG, "showID ==>>>> " + sectionDetailList.getId());
        Log.e(TAG, "sessionID ==>>>> " + sessionList.get(sessionPos).getId());

        Utility.checkShowInHawk(myShowList, "" + prefManager.getLoginId(), "" + sectionDetailList.getId());

        myShowList.add(downloadVideoItem);
        Hawk.put(Constant.hawkSHOWList + prefManager.getLoginId(), myShowList);

        Toasty.success(this, "" + getResources().getString(R.string.file_has_been_downloaded), Toasty.LENGTH_SHORT).show();
        Utility.addRemoveDownload(getApplicationContext(), "" + sessionList.get(sessionPos).getId(),
                "" + sectionDetailList.getVideoType(), "" + sectionDetailList.getTypeId(), "" + sectionDetailList.getId());

        this.stopForeground(false);
        this.stopSelf();
    }

    private void onEpiDownloadComplete(int position, long fileSize, String epiVideoPath, String secretKey) {
        Log.e("onEpiDownloadComplete", "position ==> " + position);

        EpisodeItem episodeItem = new EpisodeItem(episodeList.get(position).getId(), episodeList.get(position).getShowId(),
                episodeList.get(position).getSessionId(), "" + episodeList.get(position).getDescription(),
                "" + episodeList.get(position).getThumbnail(), "" + episodeList.get(position).getLandscape(),
                "" + episodeList.get(position).getVideo320(), "" + epiVideoPath,
                "" + episodeList.get(position).getVideoType(), "" + episodeList.get(position).getUpcomingType(),
                "" + episodeList.get(position).getVideo320(), "" + episodeList.get(position).getVideoExtension(),
                "" + episodeList.get(position).getVideoDuration(), episodeList.get(position).getView(), episodeList.get(position).getDownload(),
                episodeList.get(position).getStatus(), "" + episodeList.get(position).getIsTitle(), fileSize, episodeList.get(position).getIsPremium(),
                "" + episodeList.get(position).getCreatedAt(), "" + episodeList.get(position).getUpdatedAt(),
                1, episodeList.get(position).getRentBuy(), episodeList.get(position).getIsRent(),
                episodeList.get(position).getRentPrice(), episodeList.get(position).getIsBuy(), episodeList.get(position).getId(),
                episodeList.get(position).getId(), "" + secretKey);

        List<EpisodeItem> myEpisodeList = Hawk.get(Constant.hawkEPISODEList + prefManager.getLoginId()
                + "" + episodeList.get(position).getSessionId() + "" + sectionDetailList.getId());
        if (myEpisodeList == null) {
            myEpisodeList = new ArrayList<>();
        }
        Log.e(TAG, "episodeID ==>>>> " + episodeList.get(position).getId());
        Log.e(TAG, "showID ==>>>> " + episodeList.get(position).getShowId());
        Log.e(TAG, "sessionID ==>>>> " + episodeList.get(position).getSessionId());
        Utility.checkEpisodeInHawk(myEpisodeList, "" + prefManager.getLoginId(), "" + episodeList.get(position).getId(),
                "" + sectionDetailList.getId(), "" + episodeList.get(position).getSessionId());

        myEpisodeList.add(episodeItem);
        Hawk.put(Constant.hawkEPISODEList + prefManager.getLoginId() + "" + episodeList.get(position).getSessionId()
                + "" + sectionDetailList.getId(), myEpisodeList);

        Log.e("==============", "==============> " + secretKey);
        Log.e(TAG, "fileSize ==> " + fileSize);
        Log.e("myEpisodeList", "Size ==> " + myEpisodeList.size());
        Log.e("==============", "==============> " + secretKey);
    }

    private void updateShowNotification(boolean downloadComplete, ShowDownloadProgress showDownloadProgress) {
        notificationBuilder.setProgress(100, showDownloadProgress.progress, false);
        notificationBuilder.setContentText("" + showDownloadProgress.progress + "%");
        notificationManager.notify(SHOW_CHANNEL_ID, notificationBuilder.build());

        Intent intent = new Intent(Constant.SHOW_DOWNLOAD_SERVICE);
        intent.putExtra("downloadComplete", downloadComplete);
        intent.putExtra("showDownloadProgress", showDownloadProgress);
        sendBroadcast(intent);
    }

    @Override
    public void onDShowProgress(ShowDownloadProgress showDownloadProgress) {
        Log.e(TAG, "<<==::::== progress : " + showDownloadProgress.progress + " ==::::==>>");
        if (showDownloadProgress.progress < 100) {
            updateShowNotification(false, showDownloadProgress);
        } else {
            Log.e("<<=======", "================== position : " + showDownloadProgress.position + "====================>>");
            updateShowNotification(true, showDownloadProgress);
            Log.e("<<=======", "================== secretKey : " + showDownloadProgress.secretKey + "====================>>");

            //Store Episode data in List
            onEpiDownloadComplete(showDownloadProgress.position, showDownloadProgress.fileSize,
                    "" + showDownloadProgress.epiFilePath, "" + showDownloadProgress.secretKey);
        }
    }

    @Override
    public void onDShowStart(String url) {
        Log.e("onDShowStart", "url ::::==>>>>>> " + url);
    }

    @Override
    public void onDShowCompleted() {
        downloaded = true;
        Log.e("onDShowCompleted", "::::==>>>>>> " + downloaded);
        onShowDownloadComplete(downloaded);
    }

    @Override
    public void onDShowCancel() {
        this.stopForeground(true);
        this.stopSelf();
    }
    /* Multiple Video (Show Episodes) Download */

    @Override
    public void onDestroy() {
        if (downloaded) {
            Toasty.success(this, getResources().getString(R.string.file_has_been_downloaded), Toast.LENGTH_SHORT).show();
        }
        super.onDestroy();
    }

}
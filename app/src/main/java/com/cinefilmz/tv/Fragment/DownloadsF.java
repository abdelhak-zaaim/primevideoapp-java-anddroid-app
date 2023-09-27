package com.cinefilmz.tv.Fragment;

import static android.content.Context.WIFI_SERVICE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Adapter.DownloadedAdapter;
import com.cinefilmz.tv.Model.DownloadShowModel.EpisodeItem;
import com.cinefilmz.tv.Model.DownloadShowModel.SessionItem;
import com.cinefilmz.tv.Model.DownloadVideoItem;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.WebService.AndroidWebServer;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DownloadsF extends Fragment implements View.OnClickListener, OnItemClick, DownloadedAdapter.DownloadListener {

    private PrefManager prefManager;
    private View root;
    private static final String TAG = DownloadsF.class.getSimpleName();

    // INSTANCE OF ANDROID WEB SERVER
    private AndroidWebServer androidWebServer;
    private BroadcastReceiver broadcastReceiverNetworkState;
    private static boolean isStarted = false;

    private ShimmerFrameLayout shimmer;
    private LinearLayout lyAllDetails, lyNoData, lyAboutVideos;
    private TextView txtMainTitle, txtTotalVideos, txtTotalMinutes, txtTotalSize;
    private RecyclerView rvAllDownload;

    private DownloadedAdapter downloadedAdapter;
    private List<DownloadVideoItem> downloadVideoItemArrayList = new ArrayList<>();

    private static final int DEFAULT_PORT = 8589;

    public DownloadsF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_downloads, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();
        initBroadcastReceiverNetworkStateChanged();

        return root;
    }

    private void init() {
        try {
            prefManager = new PrefManager(getActivity());

            shimmer = root.findViewById(R.id.shimmer);
            lyAboutVideos = root.findViewById(R.id.lyAboutVideos);
            lyAllDetails = root.findViewById(R.id.lyAllDetails);
            lyNoData = root.findViewById(R.id.lyNoData);

            txtMainTitle = root.findViewById(R.id.txtMainTitle);
            txtTotalVideos = root.findViewById(R.id.txtTotalVideos);
            txtTotalMinutes = root.findViewById(R.id.txtTotalMinutes);
            txtTotalSize = root.findViewById(R.id.txtTotalSize);

            rvAllDownload = root.findViewById(R.id.rvAllDownload);

            lyAboutVideos.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onResume() {
        AllDownload();
        stopAndroidWebServer();
        isStarted = false;
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    /* Set Downloaded Videos */
    private void AllDownload() {
        long addTotalDuration = 0L, addTotalFileSize = 0L;

        downloadVideoItemArrayList.clear();

        /* Video */
        List<DownloadVideoItem> myDownloadsList = Hawk.get(Constant.hawkVIDEOList + "" + prefManager.getLoginId());
        if (myDownloadsList == null) {
            myDownloadsList = new ArrayList<>();
        }
        Log.e(TAG, "myDownloadsList size :==> " + myDownloadsList.size());
        if (myDownloadsList.size() > 0) {
            for (int i = 0; i < myDownloadsList.size(); i++) {
                downloadVideoItemArrayList.add(myDownloadsList.get(i));

                /* Get Total Duration & Total size of File from Download items */
                addTotalDuration += myDownloadsList.get(i).getDuration();
                Log.e(TAG, "addTotalDuration :=> " + addTotalDuration);

                /* Get Total File size from Download items */
                addTotalFileSize += myDownloadsList.get(i).getSize();
                Log.e(TAG, "addTotalFileSize :==> " + addTotalFileSize);
            }
        }

        /* Kids Videos */
        List<DownloadVideoItem> kidsDownloadsList = Hawk.get(Constant.hawkKIDSVIDEOList + prefManager.getLoginId());
        if (kidsDownloadsList == null) {
            kidsDownloadsList = new ArrayList<>();
        }
        Log.e(TAG, "kidsDownloadsList size :==> " + kidsDownloadsList.size());
        if (kidsDownloadsList.size() > 0) {
            for (int i = 0; i < kidsDownloadsList.size(); i++) {
                downloadVideoItemArrayList.add(kidsDownloadsList.get(i));

                /* Get Total Duration & Total size of File from Download items */
                addTotalDuration += kidsDownloadsList.get(i).getDuration();
                Log.e(TAG, "addTotalDuration :=> " + addTotalDuration);

                /* Get Total File size from Download items */
                addTotalFileSize += kidsDownloadsList.get(i).getSize();
                Log.e(TAG, "addTotalFileSize :==> " + addTotalFileSize);
            }
        }

        /* Show */
        List<DownloadVideoItem> showEpiDownloadList = Hawk.get(Constant.hawkSHOWList + prefManager.getLoginId());
        if (showEpiDownloadList == null) {
            showEpiDownloadList = new ArrayList<>();
        }
        List<SessionItem> seasonDownloadList;
        List<EpisodeItem> episodeDownloadList;
        Log.e(TAG, "showEpiDownloadList size :==> " + showEpiDownloadList.size());
        if (showEpiDownloadList.size() > 0) {
            for (int i = 0; i < showEpiDownloadList.size(); i++) {
                downloadVideoItemArrayList.add(showEpiDownloadList.get(i));
                seasonDownloadList = showEpiDownloadList.get(i).getSessionItems();
                Log.e(TAG, "seasonDownloadList size :==> " + seasonDownloadList.size());

                for (int j = 0; j < seasonDownloadList.size(); j++) {

                    episodeDownloadList = seasonDownloadList.get(j).getEpisodeItems();
                    Log.e(TAG, "episodeDownloadList size :==> " + episodeDownloadList.size());

                    for (int k = 0; k < episodeDownloadList.size(); k++) {

                        /* Get Total Duration & Total size of File from Download items */
                        addTotalDuration += Long.parseLong(episodeDownloadList.get(k).getVideoDuration());
                        Log.e(TAG, "addTotalDuration :=> " + addTotalDuration);

                        /* Get Total File size from Download items */
                        addTotalFileSize += episodeDownloadList.get(k).getFileSize();
                        Log.e(TAG, "addTotalFileSize :==> " + addTotalFileSize);
                    }
                }
            }
        }

        if (downloadVideoItemArrayList.size() > 0) {

            txtTotalVideos.setText(downloadVideoItemArrayList.size() + " "
                    + (downloadVideoItemArrayList.size() > 1 ? getResources().getString(R.string.videos) : getResources().getString(R.string.video_)));
            txtTotalMinutes.setText("" + (addTotalDuration != 0L ? Functions.formatDuration(addTotalDuration) : "0 min"));
            txtTotalSize.setText("" + (addTotalFileSize != 0L ? Functions.getStringSizeOfFile(addTotalFileSize) : "0 byte"));

            DownloadedAdapter.adapterSize = downloadVideoItemArrayList.size();
            Log.e("==>", "adapterSize : " + DownloadedAdapter.adapterSize);
            Log.e("==>", "downloadItemArrayList : " + downloadVideoItemArrayList.size());
            downloadedAdapter = new DownloadedAdapter(getActivity(), downloadVideoItemArrayList, DownloadsF.this,
                    DownloadsF.this);
            rvAllDownload.setLayoutManager(new GridLayoutManager(getActivity(), 1));
            rvAllDownload.setAdapter(downloadedAdapter);
            downloadedAdapter.notifyDataSetChanged();

            lyAllDetails.setVisibility(View.VISIBLE);
            lyNoData.setVisibility(View.GONE);
        } else {
            txtTotalVideos.setText("0 " + getResources().getString(R.string.video_));
            txtTotalMinutes.setText("0 min");
            txtTotalSize.setText("0 byte");
            lyAllDetails.setVisibility(View.GONE);
            lyNoData.setVisibility(View.VISIBLE);
        }

        // Create and add a callback
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.e("onMove", "pos => " + viewHolder.getAbsoluteAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.e("onSwiped", "pos => " + viewHolder.getAbsoluteAdapterPosition());
                if (!downloadVideoItemArrayList.get(viewHolder.getAbsoluteAdapterPosition()).getVideoType().equalsIgnoreCase("2")) {
                    showDeleteDialog(viewHolder.getAbsoluteAdapterPosition());
                }
            }

            // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Log.e("onChildDraw", "pos => " + viewHolder.getAbsoluteAdapterPosition());
                Log.e("onChildDraw", "VideoType => " + downloadVideoItemArrayList.get(viewHolder.getAbsoluteAdapterPosition()).getVideoType());

                if (!downloadVideoItemArrayList.get(viewHolder.getAbsoluteAdapterPosition()).getVideoType().equalsIgnoreCase("2")) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red))
                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.red))
                                .addSwipeRightActionIcon(R.drawable.ic_delete)
                                .create()
                                .decorate();
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvAllDownload);
    }

    @Override
    public void longClick(String itemID, String clickType, int position) {
        Log.e("itemClick", "itemID ==> " + itemID);
        Log.e("itemClick", "clickType ==> " + clickType);
        Log.e("itemClick", "position ==> " + position);

        showDeleteDialog(position);
    }

    @Override
    public void itemClick(String itemID, String clickType, int position) {
        Log.e("itemClick", "itemID ==> " + itemID);
        Log.e("itemClick", "clickType ==> " + clickType);
        Log.e("itemClick", "position ==> " + position);

        if (clickType.equalsIgnoreCase("Details")
                && downloadVideoItemArrayList.get(position).getVideoType().equalsIgnoreCase("2")) {
            Utility.openDetails(getActivity(),
                    "" + downloadVideoItemArrayList.get(position).getId(),
                    "" + downloadVideoItemArrayList.get(position).getVideoType(),
                    "" + downloadVideoItemArrayList.get(position).getTypeId(),
                    "" + downloadVideoItemArrayList.get(position).getUpcomingType());

        } else if (clickType.equalsIgnoreCase("Delete")) {
            showDownloadMoreDialog(position);
        }
    }

    private void showDeleteDialog(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.delete_confirmation_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadedAdapter.notifyItemChanged(position);
            }
        });

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        LinearLayout lyClickDelete = bottomSheetDialog.findViewById(R.id.lyClickDelete);
        LinearLayout lyClickCancel = bottomSheetDialog.findViewById(R.id.lyClickCancel);

        lyClickDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }

                Log.e(TAG, "position ===> " + position);
                Log.e(TAG, "VideoType ===> " + downloadVideoItemArrayList.get(position).getVideoType());
                if (!downloadVideoItemArrayList.get(position).getVideoType().equalsIgnoreCase("2")) {
                    Utility.removeDownloadFromStorage(getActivity(), Constant.hawkVIDEOList,
                            "" + downloadVideoItemArrayList.get(position).getId());
                }

                Utility.addRemoveDownload(getActivity(), "" + downloadVideoItemArrayList.get(position).getId(),
                        "" + downloadVideoItemArrayList.get(position).getVideoType(),
                        "" + downloadVideoItemArrayList.get(position).getTypeId(), "0");
                if (DownloadedAdapter.adapterSize != 0) {
                    DownloadedAdapter.adapterSize = DownloadedAdapter.adapterSize - 1;
                    if (DownloadedAdapter.adapterSize == 0) {
                        lyAllDetails.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    lyAllDetails.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                downloadedAdapter.notifyItemRemoved(position);
                AllDownload();
                Utility.showSnackBar(getActivity(), "DeleteDone", "");
            }
        });

        lyClickCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }

                downloadedAdapter.notifyItemChanged(position);
            }
        });

    }

    private void showDownloadMoreDialog(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.download_more_icon_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        TextView txtVideoTitle = bottomSheetDialog.findViewById(R.id.txtVideoTitle);
        TextView txtCommentIcon = bottomSheetDialog.findViewById(R.id.txtCommentIcon);
        TextView txtAgeLimit = bottomSheetDialog.findViewById(R.id.txtAgeLimit);
        TextView txtReleaseYear = bottomSheetDialog.findViewById(R.id.txtReleaseYear);
        TextView txtWatchResumeIcon = bottomSheetDialog.findViewById(R.id.txtWatchResumeIcon);
        TextView txtWatchResume = bottomSheetDialog.findViewById(R.id.txtWatchResume);
        TextView txtAddDeleteDownloadIcon = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownloadIcon);
        TextView txtAddDeleteDownload = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownload);
        TextView txtVideoQuality = bottomSheetDialog.findViewById(R.id.txtVideoQuality);
        TextView txtAudioLanguage = bottomSheetDialog.findViewById(R.id.txtAudioLanguage);
        LinearLayout lyVideoQuality = bottomSheetDialog.findViewById(R.id.lyVideoQuality);
        LinearLayout lyAudioLanguage = bottomSheetDialog.findViewById(R.id.lyAudioLanguage);
        LinearLayout lyWatchResume = bottomSheetDialog.findViewById(R.id.lyWatchResume);
        LinearLayout lyAddDeleteDownload = bottomSheetDialog.findViewById(R.id.lyAddDeleteDownload);
        LinearLayout lyViewDetails = bottomSheetDialog.findViewById(R.id.lyViewDetails);

        lyAudioLanguage.setVisibility(View.GONE);
        txtVideoTitle.setText("" + downloadVideoItemArrayList.get(position).getTitle());
        if (!TextUtils.isEmpty(downloadVideoItemArrayList.get(position).getReleaseYear())) {
            txtReleaseYear.setVisibility(View.VISIBLE);
            txtReleaseYear.setText("" + downloadVideoItemArrayList.get(position).getReleaseYear());
        } else {
            txtReleaseYear.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(downloadVideoItemArrayList.get(position).getAgeRestriction())) {
            txtAgeLimit.setVisibility(View.VISIBLE);
            txtAgeLimit.setText("" + downloadVideoItemArrayList.get(position).getAgeRestriction());
        } else {
            txtAgeLimit.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(downloadVideoItemArrayList.get(position).getMaxVideoQuality())) {
            lyVideoQuality.setVisibility(View.VISIBLE);
            txtVideoQuality.setText("" + downloadVideoItemArrayList.get(position).getMaxVideoQuality());
        } else {
            lyVideoQuality.setVisibility(View.GONE);
        }
        txtWatchResumeIcon.setBackground(getResources().getDrawable(R.drawable.ic_play2));
        txtWatchResume.setText("" + getResources().getString(R.string.watch_now));

        if (!downloadVideoItemArrayList.get(position).getVideoType().equalsIgnoreCase("2")) {
            if (downloadVideoItemArrayList.get(position).getIsDownloaded() == 1) {
                lyAddDeleteDownload.setVisibility(View.VISIBLE);
                txtAddDeleteDownloadIcon.setBackground(getResources().getDrawable(R.drawable.ic_delete));
                txtAddDeleteDownload.setText("" + getResources().getString(R.string.delete_download));
            } else {
                lyAddDeleteDownload.setVisibility(View.GONE);
            }
        } else {
            lyAddDeleteDownload.setVisibility(View.GONE);
        }

        lyWatchResume.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            Utility.playVideoOnClick(getActivity(), "Download", "Download", 0,
                    "" + downloadVideoItemArrayList.get(position).getPath(), "" + downloadVideoItemArrayList.get(position).getPath(),
                    "" + downloadVideoItemArrayList.get(position).getId(), "",
                    "" + downloadVideoItemArrayList.get(position).getImage(),
                    "" + downloadVideoItemArrayList.get(position).getTitle(),
                    "" + downloadVideoItemArrayList.get(position).getVideoType(),
                    "" + downloadVideoItemArrayList.get(position).getSecretKey());
        });

        lyAddDeleteDownload.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }

            Log.e(TAG, "position ===> " + position);
            Log.e(TAG, "VideoType ===> " + downloadVideoItemArrayList.get(position).getVideoType());
            if (!downloadVideoItemArrayList.get(position).getVideoType().equalsIgnoreCase("2")) {
                Utility.removeDownloadFromStorage(getActivity(), Constant.hawkVIDEOList,
                        "" + downloadVideoItemArrayList.get(position).getId());
            }

            Utility.addRemoveDownload(getActivity(), "" + downloadVideoItemArrayList.get(position).getId(),
                    "" + downloadVideoItemArrayList.get(position).getVideoType(),
                    "" + downloadVideoItemArrayList.get(position).getTypeId(), "0");
            if (DownloadedAdapter.adapterSize != 0) {
                DownloadedAdapter.adapterSize = DownloadedAdapter.adapterSize - 1;
                if (DownloadedAdapter.adapterSize == 0) {
                    lyAllDetails.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
            } else {
                lyAllDetails.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
            }
            downloadedAdapter.notifyItemRemoved(position);
            AllDownload();
            Utility.showSnackBar(getActivity(), "DeleteDone", "");
        });

        lyViewDetails.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            Utility.openDetails(getActivity(), "" + downloadVideoItemArrayList.get(position).getId(),
                    "" + downloadVideoItemArrayList.get(position).getVideoType(),
                    "" + downloadVideoItemArrayList.get(position).getTypeId(),
                    "" + downloadVideoItemArrayList.get(position).getUpcomingType());
        });

    }

    private String getIpAccess() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":";
    }

    private int getPortFromEditText() {
        return DEFAULT_PORT;
    }

    public boolean isConnectedInWifi() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiManager.isWifiEnabled() && networkInfo.getTypeName().equals("WIFI")) {
            return true;
        }
        return false;
    }

    private boolean startAndroidWebServer(String url) {
        if (!isStarted) {
            int port = getPortFromEditText();
            try {
                if (port == 0) {
                    throw new Exception();
                }
                androidWebServer = new AndroidWebServer(port, url);
                androidWebServer.start();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean stopAndroidWebServer() {
        if (isStarted && androidWebServer != null) {
            androidWebServer.stop();
            return true;
        }
        return false;
    }

    private void initBroadcastReceiverNetworkStateChanged() {
        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        broadcastReceiverNetworkState = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            }
        };
        getActivity().registerReceiver(broadcastReceiverNetworkState, filters);
    }

    @Override
    public void OnPlay(DownloadVideoItem downloadVideoItem) {
        Log.e("downloadItem", "path ==> " + downloadVideoItem.getPath());
        Log.e("downloadItem", "isConnectedInWifi ==> " + isConnectedInWifi());
        if (isConnectedInWifi()) {
            if (!isStarted && startAndroidWebServer(downloadVideoItem.getPath())) {
                Utility.playVideoOnClick(getActivity(), "Download", "Download", 0,
                        "" + downloadVideoItem.getPath(), "" + downloadVideoItem.getPath(),
                        "" + downloadVideoItem.getId(), "",
                        "" + downloadVideoItem.getImage(),
                        "" + downloadVideoItem.getTitle(),
                        "" + downloadVideoItem.getVideoType(),
                        "" + downloadVideoItem.getSecretKey());
                isStarted = true;
            } else if (stopAndroidWebServer()) {
                isStarted = false;
            }
        } else {
            Utility.playVideoOnClick(getActivity(), "Download", "Download", 0,
                    "" + downloadVideoItem.getPath(), "" + downloadVideoItem.getPath(),
                    "" + downloadVideoItem.getId(), "",
                    "" + downloadVideoItem.getImage(),
                    "" + downloadVideoItem.getTitle(),
                    "" + downloadVideoItem.getVideoType(),
                    "" + downloadVideoItem.getSecretKey());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        Utility.shimmerHide(shimmer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (broadcastReceiverNetworkState != null) {
            getActivity().unregisterReceiver(broadcastReceiverNetworkState);
        }
        Log.e("onDestroy", "called");
        Utility.shimmerHide(shimmer);
    }

}
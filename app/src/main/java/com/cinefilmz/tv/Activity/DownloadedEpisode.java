package com.cinefilmz.tv.Activity;

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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Adapter.DownloadEpiAdapter;
import com.cinefilmz.tv.Adapter.TvShowSeasonAdapter;
import com.cinefilmz.tv.Model.DownloadShowModel.EpisodeItem;
import com.cinefilmz.tv.Model.DownloadShowModel.SessionItem;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.WebService.AndroidWebServer;
import com.cinefilmz.tv.players.PlayerActivity;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.Interface.OnSingleItemClick;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.orhanobut.hawk.Hawk;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DownloadedEpisode extends AppCompatActivity implements View.OnClickListener, OnItemClick, OnSingleItemClick {

    private PrefManager prefManager;
    private static final String TAG = DownloadedEpisode.class.getSimpleName();

    // INSTANCE OF ANDROID WEB SERVER
    private AndroidWebServer androidWebServer;
    private BroadcastReceiver broadcastReceiverNetworkState;
    private static boolean isStarted = false;

    private ShimmerFrameLayout shimmer;
    private LinearLayout lyNoData;
    private TextView txtMainTitle, txtTotalVideos, txtTotalMinutes, txtTotalSize;
    private RecyclerView rvAllSeason, rvAllDownloadEpi;

    private TvShowSeasonAdapter tvShowSeasonAdapter;
    private List<SessionItem> seasonList;
    private DownloadEpiAdapter downloadEpiAdapter;
    private List<EpisodeItem> episodeItemList;
    private String showTitle = "", showID = "", videoType = "", typeID = "";
    private int showPosition, mSeasonClickPos = 0;

    private static final int DEFAULT_PORT = 8589;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_download_epi);
        PrefManager.forceRTLIfSupported(getWindow(), DownloadedEpisode.this);
        Log.e(TAG, "onCreate");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            this.showTitle = bundle.getString("showTitle");
            this.showID = bundle.getString("showID");
            this.videoType = bundle.getString("videoType");
            this.typeID = bundle.getString("typeID");
            Log.e("showTitle", "===>> " + showTitle);
            Log.e("showID", "===>> " + showID);
            Log.e("videoType", "===>> " + videoType);
            Log.e("typeID", "===>> " + typeID);
        }

        init();
        txtMainTitle.setText("" + showTitle);
        setAllSeason();
        initBroadcastReceiverNetworkStateChanged();
    }

    private void init() {
        try {
            prefManager = new PrefManager(DownloadedEpisode.this);

            shimmer = findViewById(R.id.shimmer);

            txtMainTitle = findViewById(R.id.txtMainTitle);
            txtTotalVideos = findViewById(R.id.txtTotalVideos);
            txtTotalMinutes = findViewById(R.id.txtTotalMinutes);
            txtTotalSize = findViewById(R.id.txtTotalSize);

            rvAllSeason = findViewById(R.id.rvAllSeason);
            rvAllDownloadEpi = findViewById(R.id.rvAllDownloadEpi);
            lyNoData = findViewById(R.id.lyNoData);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    /* Set All Seasons */
    private void setAllSeason() {
        seasonList = new ArrayList<>();
        seasonList = Hawk.get(Constant.hawkSEASONList + prefManager.getLoginId() + "" + showID);
        Log.e("seasonList", "size ==>>> " + seasonList.size());

        Collections.sort(seasonList, new Comparator<SessionItem>() {
            @Override
            public int compare(SessionItem o1, SessionItem o2) {
                if (o1.getSessionPosition() != null && o2.getSessionPosition() != null) {
                    return o1.getSessionPosition() - o2.getSessionPosition();
                } else {
                    return 0;
                }
            }
        });

        if (seasonList != null && seasonList.size() > 0) {
            rvAllSeason.setVisibility(View.VISIBLE);
            Log.e("seasonList", "size ==>>>==>>> " + seasonList.size());

            tvShowSeasonAdapter = new TvShowSeasonAdapter(DownloadedEpisode.this, new ArrayList<>(), seasonList, "DSeason",
                    DownloadedEpisode.this);
            rvAllSeason.setLayoutManager(new LinearLayoutManager(DownloadedEpisode.this, LinearLayoutManager.HORIZONTAL, false));
            rvAllSeason.setAdapter(tvShowSeasonAdapter);
            tvShowSeasonAdapter.notifyDataSetChanged();

            mSeasonClickPos = TvShowSeasonAdapter.mSelectedPos;
            Log.e("mSeasonClickPos", "==>>> " + mSeasonClickPos);
            if (mSeasonClickPos > (seasonList.size() - 1)) {
                mSeasonClickPos = seasonList.size() - 1;
                TvShowSeasonAdapter.mSelectedPos = mSeasonClickPos;
            }
            Log.e("mSeasonClickPos", "==final==>>> " + mSeasonClickPos);
            AllDownloadedEpisode(mSeasonClickPos);
        } else {
            rvAllSeason.setVisibility(View.GONE);
        }
    }

    /* Set All Episodes */
    private void AllDownloadedEpisode(int newSPos) {
        Log.e(TAG, "newSPos :==> " + newSPos);

        long addTotalDuration = 0L, addTotalFileSize = 0L;

        if (episodeItemList == null) {
            episodeItemList = new ArrayList<>();
        }
        episodeItemList = Hawk.get(Constant.hawkEPISODEList + prefManager.getLoginId() + "" + seasonList.get(newSPos).getId() + "" + showID);
        Log.e(TAG, "episodeItemList :==> " + episodeItemList.size());

        if (episodeItemList.size() > 0) {
            rvAllDownloadEpi.setVisibility(View.VISIBLE);
            lyNoData.setVisibility(View.GONE);

            txtTotalVideos.setText(episodeItemList.size() + " " +
                    (episodeItemList.size() > 1 ? getResources().getString(R.string.episodes) : getResources().getString(R.string.episode)));

            for (int i = 0; i < episodeItemList.size(); i++) {
                /* Get Total Duration of File from Download items */
                addTotalDuration += Long.parseLong(episodeItemList.get(i).getVideoDuration());
                Log.e(TAG, "addTotalDuration :=> " + addTotalDuration);

                /* Get Total File size from Download items */
                addTotalFileSize += episodeItemList.get(i).getFileSize();
                Log.e(TAG, "addTotalFileSize :==> " + addTotalFileSize);
            }
            txtTotalMinutes.setText("" + (addTotalDuration != 0L ? Functions.formatDuration(addTotalDuration) : "0 min"));
            txtTotalSize.setText("" + (addTotalFileSize != 0L ? Functions.getStringSizeOfFile(addTotalFileSize) : "0 byte"));

            DownloadEpiAdapter.adapterSize = episodeItemList.size();
            Log.e("==>", "adapterSize : " + DownloadEpiAdapter.adapterSize);
            downloadEpiAdapter = new DownloadEpiAdapter(DownloadedEpisode.this, episodeItemList, "Episode", DownloadedEpisode.this);
            rvAllDownloadEpi.setLayoutManager(new GridLayoutManager(DownloadedEpisode.this, 1));
            rvAllDownloadEpi.setAdapter(downloadEpiAdapter);
            downloadEpiAdapter.notifyDataSetChanged();
            setSwipeToDelete();
        } else {
            txtTotalVideos.setText("0 " + getResources().getString(R.string.episode));
            txtTotalMinutes.setText("0 min");
            txtTotalSize.setText("0 byte");
            rvAllDownloadEpi.setVisibility(View.GONE);
            lyNoData.setVisibility(View.VISIBLE);
        }

    }

    private void setSwipeToDelete() {
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
                Log.e(TAG, "mSelectedPos ===> " + tvShowSeasonAdapter.mSelectedPos);
                Log.e(TAG, "adapterSize ===> " + DownloadEpiAdapter.adapterSize);
                showDeleteDialog(viewHolder.getAbsoluteAdapterPosition());
            }

            // You must use @RecyclerViewSwipeDecorator inside the onChildDraw method
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Log.e("onChildDraw", "pos => " + viewHolder.getAbsoluteAdapterPosition());

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive)
                            .addSwipeLeftBackgroundColor(ContextCompat.getColor(DownloadedEpisode.this, R.color.red))
                            .addSwipeLeftActionIcon(R.drawable.ic_delete)
                            .addSwipeRightBackgroundColor(ContextCompat.getColor(DownloadedEpisode.this, R.color.red))
                            .addSwipeRightActionIcon(R.drawable.ic_delete)
                            .create()
                            .decorate();
                    super.onChildDraw(c, recyclerView, viewHolder, dX / 4, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvAllDownloadEpi);
    }

    private void showDeleteDialog(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DownloadedEpisode.this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.delete_confirmation_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadEpiAdapter.notifyItemChanged(position);
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
                Log.e(TAG, "mSelectedPos ===> " + tvShowSeasonAdapter.mSelectedPos);
                Log.e(TAG, "adapterSize ===> " + DownloadEpiAdapter.adapterSize);
                Utility.removeEpisodeFromStorage(DownloadedEpisode.this, Constant.hawkSHOWList, "" + showID,
                        "" + seasonList.get(tvShowSeasonAdapter.mSelectedPos).getId(),
                        "" + episodeItemList.get(position).getId());

                if (DownloadEpiAdapter.adapterSize != 0) {
                    DownloadEpiAdapter.adapterSize = DownloadEpiAdapter.adapterSize - 1;
                    if (DownloadEpiAdapter.adapterSize == 0) {
                        rvAllDownloadEpi.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                    }
                } else {
                    rvAllDownloadEpi.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
                Log.e(TAG, "episodeItemList ===> " + episodeItemList.size());
                Log.e(TAG, "final adapterSize ===> " + DownloadEpiAdapter.adapterSize);
                Log.e(TAG, "mSeasonClickPos ===> " + mSeasonClickPos);

                if (DownloadEpiAdapter.adapterSize == 0) {
                    Utility.addRemoveDownload(DownloadedEpisode.this, "" + seasonList.get(mSeasonClickPos).getId(),
                            "" + videoType, "" + typeID, "" + showID);
                }
                AllDownloadedEpisode(mSeasonClickPos);
                downloadEpiAdapter.notifyItemRemoved(position);
                Utility.showSnackBar(DownloadedEpisode.this, "DeleteDone", "");
            }
        });

        lyClickCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }

                downloadEpiAdapter.notifyItemChanged(position);
            }
        });

    }

    private String getIpAccess() {
        WifiManager wifiManager = (WifiManager) DownloadedEpisode.this.getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        return "http://" + formatedIpAddress + ":";
    }

    private int getPortFromEditText() {
        return DEFAULT_PORT;
    }

    public boolean isConnectedInWifi() {
        WifiManager wifiManager = (WifiManager) DownloadedEpisode.this.getApplicationContext().getSystemService(WIFI_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = ((ConnectivityManager) DownloadedEpisode.this.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
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
        DownloadedEpisode.this.registerReceiver(broadcastReceiverNetworkState, filters);
    }

    @Override
    public void longClick(String itemID, String clickType, int position) {
        Log.e("onSingleClick", "position ==> " + position);
        Log.e("onSingleClick", "clickType ==> " + clickType);
        Log.e("onSingleClick", "itemID ==> " + itemID);
    }

    @Override
    public void itemClick(String itemID, String clickType, int position) {
        Log.e("onSingleClick", "position ==> " + position);
        Log.e("onSingleClick", "clickType ==> " + clickType);
        Log.e("onSingleClick", "itemID ==> " + itemID);

        if (mSeasonClickPos != position) {
            mSeasonClickPos = position;
            AllDownloadedEpisode(position);
        }

    }

    @Override
    public void onSingleClick(int position, String itemId) {
        Log.e("onSingleClick", "position ==> " + position);
        Log.e("onSingleClick", "itemId ==> " + itemId);

        if (isConnectedInWifi()) {
            if (!isStarted && startAndroidWebServer(episodeItemList.get(position).getVideoPath())) {
                Intent intent1 = new Intent(DownloadedEpisode.this, PlayerActivity.class);
                intent1.putExtra("videoFrom", "EpisodeDownload");
                intent1.putExtra("position", position);
                intent1.putExtra("episodeList", (Serializable) episodeItemList);
                intent1.putExtra("secretKey", "" + episodeItemList.get(position).getSecretKey());
                DownloadedEpisode.this.startActivity(intent1);
                isStarted = true;
            } else if (stopAndroidWebServer()) {
                isStarted = false;
            }
        } else {
            Intent intent = new Intent(DownloadedEpisode.this, PlayerActivity.class);
            intent.putExtra("videoFrom", "EpisodeDownload");
            intent.putExtra("position", position);
            intent.putExtra("episodeList", (Serializable) episodeItemList);
            intent.putExtra("secretKey", "" + episodeItemList.get(position).getSecretKey());
            DownloadedEpisode.this.startActivity(intent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        Utility.shimmerHide(shimmer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "called");
        Utility.shimmerHide(shimmer);
    }

}
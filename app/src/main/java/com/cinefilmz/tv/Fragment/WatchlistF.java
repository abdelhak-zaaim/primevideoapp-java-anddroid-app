package com.cinefilmz.tv.Fragment;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinefilmz.tv.Adapter.WatchlistAdapter;
import com.cinefilmz.tv.Model.VideoModel.Result;
import com.cinefilmz.tv.Model.VideoModel.VideoModel;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WatchlistF extends Fragment implements View.OnClickListener, OnItemClick {

    private PrefManager prefManager;
    private View root;
    private static final String TAG = WatchlistF.class.getSimpleName();

    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout lyWatchlist, lyEmptyWatchlist;
    private TextView txtTotalVideos;
    private RecyclerView rvWatchlist;

    private WatchlistAdapter watchlistAdapter;

    private List<Result> watchlistList;

    private String filterBySort = "", filterByContent = "";

    public WatchlistF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();
        AllWatchlist();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        AllWatchlist();
                    }
                }, getResources().getInteger(R.integer.swipeRefreshDelay));
            }
        });

        return root;
    }

    private void init() {
        try {
            prefManager = new PrefManager(getActivity());

            shimmer = root.findViewById(R.id.shimmer);
            swipeRefresh = root.findViewById(R.id.swipeRefresh);

            lyWatchlist = root.findViewById(R.id.lyWatchlist);
            lyEmptyWatchlist = root.findViewById(R.id.lyEmptyWatchlist);
            rvWatchlist = root.findViewById(R.id.rvWatchlist);

            txtTotalVideos = root.findViewById(R.id.txtTotalVideos);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /* get_bookmark_video API */
    private void AllWatchlist() {
        Utility.shimmerShow(shimmer);
        Call<VideoModel> call = BaseURL.getVideoAPI().get_bookmark_video("" + prefManager.getLoginId());
        call.enqueue(new Callback<VideoModel>() {
            @Override
            public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                try {
                    watchlistList = new ArrayList<>();
                    Log.e("get_bookmark_video", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult() != null) {
                            if (response.body().getResult().size() > 0) {
                                watchlistList = response.body().getResult();
                                Log.e("watchlistList", "size =>>> " + watchlistList.size());

                                if (watchlistList.size() == 1) {
                                    txtTotalVideos.setText(watchlistList.size() + " "
                                            + (watchlistList.size() == 1 ? getResources().getString(R.string.video_) : getResources().getString(R.string.video_)));
                                } else {
                                    txtTotalVideos.setText(watchlistList.size() + " " + getResources().getString(R.string.videos));
                                }

                                watchlistAdapter = new WatchlistAdapter(getActivity(), watchlistList, "Watchlist", WatchlistF.this);
                                rvWatchlist.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                                rvWatchlist.setAdapter(watchlistAdapter);
                                watchlistAdapter.notifyDataSetChanged();

                                lyWatchlist.setVisibility(View.VISIBLE);
                                lyEmptyWatchlist.setVisibility(View.GONE);
                            } else {
                                lyWatchlist.setVisibility(View.GONE);
                                lyEmptyWatchlist.setVisibility(View.VISIBLE);
                            }
                        } else {
                            lyWatchlist.setVisibility(View.GONE);
                            lyEmptyWatchlist.setVisibility(View.VISIBLE);
                        }

                    } else {
                        lyWatchlist.setVisibility(View.GONE);
                        lyEmptyWatchlist.setVisibility(View.VISIBLE);
                        Log.e("get_bookmark_video", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    lyWatchlist.setVisibility(View.GONE);
                    lyEmptyWatchlist.setVisibility(View.VISIBLE);
                    Log.e("get_bookmark_video", "Exception => " + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<VideoModel> call, Throwable t) {
                lyWatchlist.setVisibility(View.GONE);
                lyEmptyWatchlist.setVisibility(View.VISIBLE);
                Utility.shimmerHide(shimmer);
                Log.e("get_bookmark_video", "onFailure => " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void itemClick(String itemID, String clickType, int position) {
        Log.e(TAG, "itemID ==>> " + itemID);
        Log.e(TAG, "clickType ==>> " + clickType);
        Log.e(TAG, "position ==>> " + position);

        Utility.openDetails(getActivity(), "" + watchlistList.get(position).getId(),
                "" + watchlistList.get(position).getVideoType(), "" + watchlistList.get(position).getTypeId(),
                "" + watchlistList.get(position).getUpcomingType());
    }

    @Override
    public void longClick(String itemID, String clickType, int position) {
        Log.e(TAG, "itemID ==>> " + itemID);
        Log.e(TAG, "clickType ==>> " + clickType);
        Log.e(TAG, "position ==>> " + position);
        Log.e(TAG, "itemID ==>> " + itemID);

        if (clickType.equalsIgnoreCase("Video")) {

        } else if (clickType.equalsIgnoreCase("Show")) {

        }
        showLongPressedWatchlistDialog(position);
    }

    private void showLongPressedWatchlistDialog(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.long_pressed_watchlist_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        TextView txtVideoTitle = bottomSheetDialog.findViewById(R.id.txtVideoTitle);
        TextView txtDuration = bottomSheetDialog.findViewById(R.id.txtDuration);
        TextView txtCommentIcon = bottomSheetDialog.findViewById(R.id.txtCommentIcon);
        TextView txtAgeLimit = bottomSheetDialog.findViewById(R.id.txtAgeLimit);
        TextView txtWatchResumeIcon = bottomSheetDialog.findViewById(R.id.txtWatchResumeIcon);
        TextView txtWatchResume = bottomSheetDialog.findViewById(R.id.txtWatchResume);
        TextView txtAddRemoveWatchlistIcon = bottomSheetDialog.findViewById(R.id.txtAddRemoveWatchlistIcon);
        TextView txtAddRemoveWatchlist = bottomSheetDialog.findViewById(R.id.txtAddRemoveWatchlist);
        TextView txtAddDeleteDownloadIcon = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownloadIcon);
        TextView txtAddDeleteDownload = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownload);
        LinearLayout lyWatchResume = bottomSheetDialog.findViewById(R.id.lyWatchResume);
        LinearLayout lyWatchTrailer = bottomSheetDialog.findViewById(R.id.lyWatchTrailer);
        LinearLayout lyStartOver = bottomSheetDialog.findViewById(R.id.lyStartOver);
        LinearLayout lyAddRemoveWatchlist = bottomSheetDialog.findViewById(R.id.lyAddRemoveWatchlist);
        LinearLayout lyAddDeleteDownload = bottomSheetDialog.findViewById(R.id.lyAddDeleteDownload);
        LinearLayout lyVideoShare = bottomSheetDialog.findViewById(R.id.lyVideoShare);
        LinearLayout lyViewDetails = bottomSheetDialog.findViewById(R.id.lyViewDetails);

        /* Prime & Rent TAG init */
        LinearLayout lyPrimeTag = bottomSheetDialog.findViewById(R.id.lyPrimeTag);
        LinearLayout lyRentTag = bottomSheetDialog.findViewById(R.id.lyRentTag);
        TextView txtPrimeTag = bottomSheetDialog.findViewById(R.id.txtPrimeTag);
        TextView txtCurrencySymbol = bottomSheetDialog.findViewById(R.id.txtCurrencySymbol);
        TextView txtRentTag = bottomSheetDialog.findViewById(R.id.txtRentTag);

        txtVideoTitle.setText("" + watchlistList.get(position).getName());
        if (!TextUtils.isEmpty(watchlistList.get(position).getAgeRestriction())) {
            txtAgeLimit.setVisibility(View.VISIBLE);
            txtAgeLimit.setText("" + watchlistList.get(position).getAgeRestriction());
        } else {
            txtAgeLimit.setVisibility(View.GONE);
        }
        if (watchlistList.get(position).getIsPremium() == 1) {
            lyPrimeTag.setVisibility(View.VISIBLE);
        } else {
            lyPrimeTag.setVisibility(View.GONE);
        }
        if (watchlistList.get(position).getIsRent() == 1) {
            txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
            lyRentTag.setVisibility(View.VISIBLE);
        } else {
            lyRentTag.setVisibility(View.GONE);
        }

        if (watchlistList.get(position).getVideoType() != 2) {
            lyWatchResume.setVisibility(View.VISIBLE);

            if (watchlistList.get(position).getVideoDuration() > 0) {
                txtDuration.setVisibility(View.VISIBLE);
                txtDuration.setText("" + Utility.covertToText(Long.parseLong("" + watchlistList.get(position).getVideoDuration())));
            } else {
                txtDuration.setVisibility(View.GONE);
            }

            if (watchlistList.get(position).getStopTime() > 0) {
                lyWatchTrailer.setVisibility(View.GONE);
                lyStartOver.setVisibility(View.VISIBLE);
                txtWatchResume.setText("" + getResources().getString(R.string.resume));
            } else {
                lyWatchTrailer.setVisibility(View.VISIBLE);
                lyStartOver.setVisibility(View.GONE);
                txtWatchResume.setText("" + getResources().getString(R.string.watch_now));
            }
            if (watchlistList.get(position).getTypeId() == 5) {
                lyAddDeleteDownload.setVisibility(View.GONE);
            } else {
                if (watchlistList.get(position).getIsDownloaded() == 1) {
                    lyAddDeleteDownload.setVisibility(View.VISIBLE);
                    txtAddDeleteDownloadIcon.setBackground(getResources().getDrawable(R.drawable.ic_delete));
                    txtAddDeleteDownload.setText("" + getResources().getString(R.string.delete_download));
                } else {
                    lyAddDeleteDownload.setVisibility(View.GONE);
                }
            }
        } else {
            lyWatchTrailer.setVisibility(View.GONE);
            lyStartOver.setVisibility(View.GONE);
            lyWatchResume.setVisibility(View.GONE);
            txtDuration.setVisibility(View.GONE);
            lyAddDeleteDownload.setVisibility(View.GONE);
        }

        if (watchlistList.get(position).getIsBookmark() == 1) {
            txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_watchlist_remove));
            txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.remove_from_watchlist));
        } else {
            txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_add_icon));
            txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.add_to_watchlist));
        }

        lyWatchResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (Utility.checkLoginUser(getActivity())) {
                    if (watchlistList != null && watchlistList.size() > 0) {
                        if (watchlistList.get(position).getStopTime() > 0) {
                            Utility.playVideoOnClick(getActivity(), "Continue",
                                    "" + watchlistList.get(position).getVideoUploadType(),
                                    watchlistList.get(position).getStopTime(),
                                    "" + watchlistList.get(position).getVideo320(),
                                    "" + watchlistList.get(position).getVideo320(),
                                    "" + watchlistList.get(position).getId(),
                                    "" + watchlistList.get(position).getCategoryId(),
                                    "" + watchlistList.get(position).getLandscape(),
                                    "" + watchlistList.get(position).getName(),
                                    "" + watchlistList.get(position).getVideoType(), "");
                        } else {
                            if (watchlistList.get(position).getIsPremium() == 1) {
                                if (watchlistList.get(position).getIsBuy() == 1 ||
                                        watchlistList.get(position).getRentBuy() == 1) {
                                    Utility.playVideoOnClick(getActivity(), "Movie",
                                            "" + watchlistList.get(position).getVideoUploadType(),
                                            0, "" + watchlistList.get(position).getVideo320(),
                                            "" + watchlistList.get(position).getVideo320(),
                                            "" + watchlistList.get(position).getId(),
                                            "" + watchlistList.get(position).getCategoryId(),
                                            "" + watchlistList.get(position).getLandscape(),
                                            "" + watchlistList.get(position).getName(),
                                            "" + watchlistList.get(position).getVideoType(), "");
                                } else {
                                    Utility.openSubscription(getActivity());
                                }
                            } else if (watchlistList.get(position).getIsBuy() == 0 && watchlistList.get(position).getIsRent() == 1
                                    && watchlistList.get(position).getRentBuy() == 0) {
                                Utility.paymentForRent(getActivity(), "" + watchlistList.get(position).getId(),
                                        "" + watchlistList.get(position).getName(),
                                        "" + watchlistList.get(position).getRentPrice(),
                                        "" + watchlistList.get(position).getTypeId(),
                                        "" + watchlistList.get(position).getVideoType());
                            } else {
                                Utility.playVideoOnClick(getActivity(), "Movie",
                                        "" + watchlistList.get(position).getVideoUploadType(), 0,
                                        "" + watchlistList.get(position).getVideo320(),
                                        "" + watchlistList.get(position).getVideo320(),
                                        "" + watchlistList.get(position).getId(),
                                        "" + watchlistList.get(position).getCategoryId(),
                                        "" + watchlistList.get(position).getLandscape(),
                                        "" + watchlistList.get(position).getName(),
                                        "" + watchlistList.get(position).getVideoType(), "");
                            }
                        }
                    }
                }
            }
        });

        lyVideoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                showShareWithDialog(position);
            }
        });

        lyViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                Utility.openDetails(getActivity(), "" + watchlistList.get(position).getId(), "" + watchlistList.get(position).getVideoType(),
                        "" + watchlistList.get(position).getTypeId(),
                        "" + watchlistList.get(position).getUpcomingType());
            }
        });

        lyAddRemoveWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }

                if (Utility.checkLoginUser(getActivity())) {
                    Utility.addRemoveFromWatchlist(getActivity(), "" + watchlistList.get(position).getId(),
                            "" + watchlistList.get(position).getVideoType(), "" + watchlistList.get(position).getTypeId());

                    watchlistList.remove(position);
                    if (watchlistList.size() == 0) {
                        txtTotalVideos.setText("0 " + getResources().getString(R.string.video_));
                        lyWatchlist.setVisibility(View.GONE);
                        lyEmptyWatchlist.setVisibility(View.VISIBLE);
                    } else {
                        txtTotalVideos.setText(watchlistList.size() + " "
                                + (watchlistList.size() == 1 ? getResources().getString(R.string.video_) : getResources().getString(R.string.video_)));
                    }

                    watchlistAdapter.notifyItemRemoved(position);
                    watchlistAdapter.notifyDataSetChanged();
                    Utility.showSnackBar(getActivity(), "WatchlistRemove", "");
                }
            }
        });

        lyAddDeleteDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (Utility.checkLoginUser(getActivity())) {
                    if (watchlistList.get(position).getIsDownloaded() == 1) {
                        Utility.removeDownloadFromStorage(getActivity(), Constant.hawkVIDEOList,
                                "" + watchlistList.get(position).getId());
                        Utility.addRemoveDownload(getActivity(), "" + watchlistList.get(position).getId(),
                                "" + watchlistList.get(position).getVideoType(), "" + watchlistList.get(position).getTypeId(), "0");
                        Utility.showSnackBar(getActivity(), "DeleteDone", "");

                        if (watchlistList.get(position).getIsDownloaded() == 1) {
                            watchlistList.get(position).setIsDownloaded(0);
                        } else {
                            watchlistList.get(position).setIsDownloaded(1);
                        }
                    }
                }
            }
        });

    }

    private void showShareWithDialog(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.share_video_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        TextView txtVideoTitle = bottomSheetDialog.findViewById(R.id.txtVideoTitle);
        TextView txtCommentIcon = bottomSheetDialog.findViewById(R.id.txtCommentIcon);
        TextView txtAgeLimit = bottomSheetDialog.findViewById(R.id.txtAgeLimit);
        LinearLayout lyShareWithSMS = bottomSheetDialog.findViewById(R.id.lyShareWithSMS);
        LinearLayout lyShareWithInsta = bottomSheetDialog.findViewById(R.id.lyShareWithInsta);
        LinearLayout lyCopyLink = bottomSheetDialog.findViewById(R.id.lyCopyLink);
        LinearLayout lyShareWithMore = bottomSheetDialog.findViewById(R.id.lyShareWithMore);

        txtVideoTitle.setText("" + watchlistList.get(position).getName());
        if (!TextUtils.isEmpty("" + watchlistList.get(position).getAgeRestriction())) {
            txtAgeLimit.setVisibility(View.VISIBLE);
            txtAgeLimit.setText("" + watchlistList.get(position).getAgeRestriction());
        } else {
            txtAgeLimit.setVisibility(View.GONE);
        }

        String shareMessage = "Hey I'm watching " + watchlistList.get(position).getName() + ". Check it out now on "
                + getResources().getString(R.string.app_name) + "! \n"
                + "https://play.google.com/store/apps/details?id=" + getActivity().getPackageName() + "\n";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "" + shareMessage);
        shareIntent.setType("text/plain");

        lyShareWithMore.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            try {
                startActivity(Intent.createChooser(shareIntent, "" + getString(R.string.share_with)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("ShareOnMore", "ActivityNotF => " + ex.getMessage());
            }
        });

        lyShareWithInsta.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            try {
                shareIntent.setPackage("com.instagram.android");
                startActivity(Intent.createChooser(shareIntent, "" + getString(R.string.share_with)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("ShareOnMore", "ActivityNotF => " + ex.getMessage());
            }
        });

        lyShareWithSMS.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            try {
                shareIntent.setPackage("com.google.android.apps.messaging");
                startActivity(Intent.createChooser(shareIntent, "" + getString(R.string.share_with)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("ShareOnMore", "ActivityNotF => " + ex.getMessage());
            }
        });

        lyCopyLink.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Link", "" + shareMessage);
            clipboard.setPrimaryClip(clip);
            Utility.showSnackBar(getActivity(), "LinkCopy", "");
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        Utility.shimmerHide(shimmer);
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
        Log.e("onDestroy", "called");
        Utility.shimmerHide(shimmer);
    }

}
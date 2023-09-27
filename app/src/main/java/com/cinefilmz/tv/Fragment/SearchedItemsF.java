package com.cinefilmz.tv.Fragment;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinefilmz.tv.Adapter.SearchItemsAdapter;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Interface.GetSetCallBack;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.Model.SearchModel.Tvshow;
import com.cinefilmz.tv.Model.SearchModel.Video;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class SearchedItemsF extends Fragment implements View.OnClickListener, OnItemClick, GetSetCallBack {

    private PrefManager prefManager;
    private View root;
    private static final String TAG = SearchedItemsF.class.getSimpleName();

    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout lySearchedItems, lyNoData;
    private RecyclerView rvSearchedItems;

    private SearchItemsAdapter searchItemsAdapter;

    private List<Video> searchVideoList;
    private List<Tvshow> searchTvShowList;

    private String searchText = "";
    private Bundle getSetBundle;
    private int tabPos;

    public SearchedItemsF() {
        Log.e("Constructor", "Called");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "Called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_searchitems, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        if (tabPos == 0) {
                            setVideos();
                        } else if (tabPos == 1) {
                            setShows();
                        }
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
            lySearchedItems = root.findViewById(R.id.lySearchedItems);
            lyNoData = root.findViewById(R.id.lyNoData);
            rvSearchedItems = root.findViewById(R.id.rvSearchedItems);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void setBundle(Bundle bundle) {
        this.getSetBundle = bundle;

        Log.e("setBundle ", " searchText ==> " + this.getSetBundle.getString("searchText"));
        if (this.getSetBundle != null) {
            if (!TextUtils.isEmpty(this.getSetBundle.getString("searchText"))) {
                searchText = "" + this.getSetBundle.getString("searchText");
                tabPos = this.getSetBundle.getInt("tabPos");
                Log.e("searchText ", "==> " + searchText);
                Log.e("tabPos ", "==> " + tabPos);

                this.searchVideoList = new ArrayList<>();
                if (this.getSetBundle.getSerializable("videoList") != null) {
                    this.searchVideoList = (List<Video>) this.getSetBundle.getSerializable("videoList");
                    Log.e("searchVideoList", " =>=> " + searchVideoList.size());

                    if (tabPos == 0) {
                        setVideos();
                    }
                    if (tabPos == 1) {
                        setShows();
                    }
                }

                this.searchTvShowList = new ArrayList<>();
                if (this.getSetBundle.getSerializable("showList") != null) {
                    this.searchTvShowList = (List<Tvshow>) this.getSetBundle.getSerializable("showList");
                    Log.e("searchTvShowList", " =>=> " + searchTvShowList.size());

                    if (tabPos == 0) {
                        setVideos();
                    }
                    if (tabPos == 1) {
                        setShows();
                    }
                }

            }
        }
    }

    @Override
    public Bundle getBundle() {
        return this.getSetBundle;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setVideos() {
        if (searchVideoList != null) {
            if (searchVideoList.size() > 0) {
                Log.e("searchVideoList", "size =>>> " + searchVideoList.size());

                searchItemsAdapter = new SearchItemsAdapter(getActivity(), searchVideoList, SearchedItemsF.this, "Video", "" + prefManager.getValue("currency_code"));
                rvSearchedItems.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                rvSearchedItems.setAdapter(searchItemsAdapter);
                searchItemsAdapter.notifyDataSetChanged();

                lySearchedItems.setVisibility(View.VISIBLE);
                lyNoData.setVisibility(View.GONE);
            } else {
                lySearchedItems.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
            }
        } else {
            lySearchedItems.setVisibility(View.GONE);
            lyNoData.setVisibility(View.VISIBLE);
        }
    }

    private void setShows() {
        if (searchTvShowList != null) {
            if (searchTvShowList.size() > 0) {
                Log.e("searchTvShowList", "size =>>> " + searchTvShowList.size());

                searchItemsAdapter = new SearchItemsAdapter(getActivity(), searchTvShowList, SearchedItemsF.this, "Shows", "Search", "" + prefManager.getValue("currency_code"));
                rvSearchedItems.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                rvSearchedItems.setAdapter(searchItemsAdapter);
                searchItemsAdapter.notifyDataSetChanged();

                lySearchedItems.setVisibility(View.VISIBLE);
                lyNoData.setVisibility(View.GONE);
            } else {
                lySearchedItems.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
            }
        } else {
            lySearchedItems.setVisibility(View.GONE);
            lyNoData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void longClick(String itemID, String clickType, int position) {
        Functions.hideSoftKeyboard(getActivity());
        Log.e(TAG, "itemID ==>> " + itemID);
        Log.e(TAG, "clickType ==>> " + clickType);
        Log.e(TAG, "position ==>> " + position);

        if (itemID.equalsIgnoreCase("Video")) {
            showLongPressedVideoDialog(position);
        } else if (itemID.equalsIgnoreCase("Show")) {
            showLongPressedShowDialog(position);
        }
    }

    @Override
    public void itemClick(String itemID, String clickType, int position) {
        Log.e(TAG, "itemID ==>> " + itemID);
        Log.e(TAG, "clickType ==>> " + clickType);
        Log.e(TAG, "position ==>> " + position);

        if (itemID.equalsIgnoreCase("Video")) {
            Utility.openDetails(getActivity(), "" + searchVideoList.get(position).getId(),
                    "" + searchVideoList.get(position).getVideoType(), "" + searchVideoList.get(position).getTypeId(),
                    "" + searchVideoList.get(position).getUpcomingType());
        } else if (itemID.equalsIgnoreCase("Show")) {
            Utility.openDetails(getActivity(), "" + searchTvShowList.get(position).getId(),
                    "" + searchTvShowList.get(position).getVideoType(), "" + searchTvShowList.get(position).getTypeId(),
                    "" + searchTvShowList.get(position).getUpcomingType());
        }
    }

    private void showLongPressedVideoDialog(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.long_pressed_video_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        TextView txtVideoTitle = bottomSheetDialog.findViewById(R.id.txtVideoTitle);
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
        LinearLayout lyHideThisVideo = bottomSheetDialog.findViewById(R.id.lyHideThisVideo);

        if (searchVideoList != null) {
            if (searchVideoList.size() > 0) {
                txtVideoTitle.setText("" + searchVideoList.get(position).getName());
                txtAgeLimit.setText("" + searchVideoList.get(position).getAgeRestriction());
                if (Integer.parseInt("" + searchVideoList.get(position).getStopTime()) > 0) {
                    lyStartOver.setVisibility(View.VISIBLE);
                    txtWatchResume.setText("" + getResources().getString(R.string.resume));
                    txtWatchResumeIcon.setBackground(getResources().getDrawable(R.drawable.ic_resume));
                } else {
                    lyStartOver.setVisibility(View.GONE);
                    txtWatchResumeIcon.setBackground(getResources().getDrawable(R.drawable.ic_play2));
                    txtWatchResume.setText("" + getResources().getString(R.string.watch_now));
                }

                if (searchVideoList.get(position).getIsDownloaded() == 1) {
                    lyAddDeleteDownload.setVisibility(View.VISIBLE);
                    txtAddDeleteDownloadIcon.setBackground(getResources().getDrawable(R.drawable.ic_delete));
                    txtAddDeleteDownload.setText("" + getResources().getString(R.string.delete_download));
                } else {
                    lyAddDeleteDownload.setVisibility(View.GONE);
                }

                if (searchVideoList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        lyWatchResume.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            if (Utility.checkLoginUser(getActivity())) {
                if (searchVideoList != null) {
                    if (searchVideoList.size() > 0) {
                        if (searchVideoList.get(position).getStopTime() > 0) {
                            Utility.playVideoOnClick(getActivity(), "Continue", searchVideoList.get(position).getVideoUploadType(),
                                    searchVideoList.get(position).getStopTime(), "" + searchVideoList.get(position).getVideo320(),
                                    "" + searchVideoList.get(position).getVideo320(), "" + searchVideoList.get(position).getId(),
                                    "" + searchVideoList.get(position).getCategoryId(),
                                    "" + searchVideoList.get(position).getLandscape(),
                                    "" + searchVideoList.get(position).getName(), "" + searchVideoList.get(position).getVideoType(),
                                    "");
                        } else {
                            if (searchVideoList.get(position).getIsPremium() == 1) {
                                if (searchVideoList.get(position).getIsBuy() == 1 ||
                                        searchVideoList.get(position).getRentBuy() == 1) {
                                    Utility.playVideoOnClick(getActivity(), "Movie", searchVideoList.get(position).getVideoUploadType(),
                                            0, "" + searchVideoList.get(position).getVideo320(),
                                            "" + searchVideoList.get(position).getVideo320(), "" + searchVideoList.get(position).getId(),
                                            "" + searchVideoList.get(position).getCategoryId(),
                                            "" + searchVideoList.get(position).getLandscape(),
                                            "" + searchVideoList.get(position).getName(), "" + searchVideoList.get(position).getVideoType(),
                                            "");
                                } else {
                                    Utility.openSubscription(getActivity());
                                }

                            } else if (searchVideoList.get(position).getIsBuy() == 0 && searchVideoList.get(position).getIsRent() == 1
                                    && searchVideoList.get(position).getRentBuy() == 0) {
                                Utility.paymentForRent(getActivity(), "" + searchVideoList.get(position).getId(),
                                        "" + searchVideoList.get(position).getName(),
                                        "" + searchVideoList.get(position).getRentPrice(),
                                        "" + searchVideoList.get(position).getTypeId(),
                                        "" + searchVideoList.get(position).getVideoType());
                            } else {
                                Utility.playVideoOnClick(getActivity(), "Movie", searchVideoList.get(position).getVideoUploadType(),
                                        0, "" + searchVideoList.get(position).getVideo320(),
                                        "" + searchVideoList.get(position).getVideo320(), "" + searchVideoList.get(position).getId(),
                                        "" + searchVideoList.get(position).getCategoryId(),
                                        "" + searchVideoList.get(position).getLandscape(),
                                        "" + searchVideoList.get(position).getName(), "" + searchVideoList.get(position).getVideoType(),
                                        "");
                            }
                        }
                    }
                }
            }
        });

        lyWatchTrailer.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            if (searchVideoList != null) {
                if (searchVideoList.size() > 0) {
                    Utility.playVideoOnClick(getActivity(), "Trailer", "" + searchVideoList.get(position).getTrailerType(),
                            0, "" + searchVideoList.get(position).getTrailerUrl(),
                            "" + searchVideoList.get(position).getTrailerUrl(),
                            "" + searchVideoList.get(position).getId(), "" + searchVideoList.get(position).getCategoryId(),
                            "" + searchVideoList.get(position).getLandscape(), "" + searchVideoList.get(position).getName(),
                            "" + searchVideoList.get(position).getVideoType(), "");
                }
            }
        });

        lyStartOver.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            if (Utility.checkLoginUser(getActivity())) {
                if (searchVideoList != null) {
                    if (searchVideoList.size() > 0) {
                        Utility.playVideoOnClick(getActivity(), "Movie", "" + searchVideoList.get(position).getVideoUploadType(),
                                0, "" + searchVideoList.get(position).getVideo320(),
                                "" + searchVideoList.get(position).getVideo320(), "" + searchVideoList.get(position).getId(),
                                "" + searchVideoList.get(position).getCategoryId(), "" + searchVideoList.get(position).getLandscape(),
                                "" + searchVideoList.get(position).getName(),
                                "" + searchVideoList.get(position).getVideoType(), "");
                    }
                }
            }
        });

        lyAddRemoveWatchlist.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            if (Utility.checkLoginUser(getActivity())) {
                if (searchVideoList != null) {
                    if (searchVideoList.size() > 0) {
                        if (searchVideoList.get(position).getIsBookmark() == 1) {
                            Utility.showSnackBar(getActivity(), "WatchlistRemove", "");
                            searchVideoList.get(position).setIsBookmark(0);
                            txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.add_to_watchlist));
                            txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_add_icon));
                        } else {
                            Utility.showSnackBar(getActivity(), "WatchlistAdd", "");
                            searchVideoList.get(position).setIsBookmark(1);
                            txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.remove_from_watchlist));
                            txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_watchlist_remove));
                        }
                        Utility.addRemoveFromWatchlist(getActivity(), "" + searchVideoList.get(position).getId(),
                                "" + searchVideoList.get(position).getVideoType(), "" + searchVideoList.get(position).getTypeId());
                    }
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
                    if (searchVideoList != null) {
                        if (searchVideoList.size() > 0) {
                            if (searchVideoList.get(position).getIsDownloaded() == 1) {
                                Utility.addRemoveDownload(getActivity(), "" + searchVideoList.get(position).getId(),
                                        "" + searchVideoList.get(position).getVideoType(), "" + searchVideoList.get(position).getTypeId(),
                                        "0");
                                Utility.showSnackBar(getActivity(), "DeleteDone", "");
                                searchVideoList.get(position).setIsDownloaded(0);
                                txtAddDeleteDownload.setText("" + getResources().getString(R.string.download));
                                txtAddDeleteDownloadIcon.setBackground(getResources().getDrawable(R.drawable.ic_downloads));
                                Utility.removeDownloadFromStorage(getActivity(), Constant.hawkVIDEOList,
                                        "" + searchVideoList.get(position).getId());
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
                if (searchVideoList != null) {
                    if (searchVideoList.size() > 0) {
                        Utility.showShareWithDialog(getActivity(), "" + searchVideoList.get(position).getName(),
                                "" + searchVideoList.get(position).getAgeRestriction());
                    }
                }
            }
        });

        lyViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (searchVideoList != null) {
                    if (searchVideoList.size() > 0) {
                        Utility.openDetails(getActivity(), "" + searchVideoList.get(position).getId(), "" + searchVideoList.get(position).getVideoType(),
                                "" + searchVideoList.get(position).getTypeId(), "" + searchVideoList.get(position).getUpcomingType());
                    }
                }
            }
        });

    }

    private void showLongPressedShowDialog(int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.long_pressed_video_dialog);
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
        LinearLayout lyHideThisVideo = bottomSheetDialog.findViewById(R.id.lyHideThisVideo);
        /* Prime & Rent TAG init */
        LinearLayout lyPrimeTag = bottomSheetDialog.findViewById(R.id.lyPrimeTag);
        LinearLayout lyRentTag = bottomSheetDialog.findViewById(R.id.lyRentTag);
        TextView txtPrimeTag = bottomSheetDialog.findViewById(R.id.txtPrimeTag);
        TextView txtCurrencySymbol = bottomSheetDialog.findViewById(R.id.txtCurrencySymbol);
        TextView txtRentTag = bottomSheetDialog.findViewById(R.id.txtRentTag);

        txtAgeLimit.setVisibility(View.GONE);
        txtDuration.setVisibility(View.GONE);
        lyWatchResume.setVisibility(View.GONE);
        lyWatchTrailer.setVisibility(View.GONE);
        lyStartOver.setVisibility(View.GONE);
        lyAddDeleteDownload.setVisibility(View.GONE);

        if (searchTvShowList != null) {
            if (searchTvShowList.size() > 0) {
                txtVideoTitle.setText("" + searchTvShowList.get(position).getName());

                if (searchTvShowList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (searchTvShowList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (searchTvShowList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        lyAddRemoveWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (Utility.checkLoginUser(getActivity())) {
                    if (searchTvShowList != null) {
                        if (searchTvShowList.size() > 0) {
                            if (searchTvShowList.get(position).getIsBookmark() == 1) {
                                Utility.showSnackBar(getActivity(), "WatchlistRemove", "");
                                searchTvShowList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                Utility.showSnackBar(getActivity(), "WatchlistAdd", "");
                                searchTvShowList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            Utility.addRemoveFromWatchlist(getActivity(), "" + searchTvShowList.get(position).getId(),
                                    "" + searchTvShowList.get(position).getVideoType(), "" + searchTvShowList.get(position).getTypeId());
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
                if (searchTvShowList != null) {
                    if (searchTvShowList.size() > 0) {
                        Utility.showShareWithDialog(getActivity(), "" + searchTvShowList.get(position).getName(), "");
                    }
                }
            }
        });

        lyViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (searchTvShowList != null) {
                    if (searchTvShowList.size() > 0) {
                        Utility.openDetails(getActivity(), "" + searchTvShowList.get(position).getId(), "" + searchTvShowList.get(position).getVideoType(),
                                "" + searchTvShowList.get(position).getTypeId(),
                                "" + searchTvShowList.get(position).getUpcomingType());
                    }
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Functions.hideSoftKeyboard(getActivity());
        Log.e("onPause", "called");
        Utility.shimmerHide(shimmer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Functions.hideSoftKeyboard(getActivity());
        Log.e("onDestroy", "called");
        Utility.shimmerHide(shimmer);
    }

}
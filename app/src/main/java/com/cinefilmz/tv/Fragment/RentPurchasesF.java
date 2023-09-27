package com.cinefilmz.tv.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinefilmz.tv.Adapter.RentPurchaseAdapter;
import com.cinefilmz.tv.Model.RentProductModel.RentProductModel;
import com.cinefilmz.tv.Model.RentProductModel.Tvshow;
import com.cinefilmz.tv.Model.RentProductModel.Video;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RentPurchasesF extends Fragment implements View.OnClickListener, OnItemClick {

    private PrefManager prefManager;
    private View root;
    private static final String TAG = RentPurchasesF.class.getSimpleName();

    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout lyPurchase, lyPurchasedVideo, lyPurchasedShow, lyEmptyPurchase;
    private TextView txtTotalVideo, txtTotalShow;
    private RecyclerView rvPurchasedVideo, rvPurchasedShow;

    private RentPurchaseAdapter rentPurchaseAdapter;
    private List<Video> purchageVideoList;
    private List<Tvshow> purchageShowList;

    public RentPurchasesF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_rent_purchases, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();
        GetPurchagedProducts();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        GetPurchagedProducts();
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

            lyPurchase = root.findViewById(R.id.lyPurchase);
            lyPurchasedVideo = root.findViewById(R.id.lyPurchasedVideo);
            lyPurchasedShow = root.findViewById(R.id.lyPurchasedShow);
            lyEmptyPurchase = root.findViewById(R.id.lyEmptyPurchase);
            rvPurchasedVideo = root.findViewById(R.id.rvPurchasedVideo);
            rvPurchasedShow = root.findViewById(R.id.rvPurchasedShow);
            txtTotalVideo = root.findViewById(R.id.txtTotalVideo);
            txtTotalShow = root.findViewById(R.id.txtTotalShow);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /* user_rent_video_list API */
    private void GetPurchagedProducts() {
        Utility.shimmerShow(shimmer);
        Call<RentProductModel> call = BaseURL.getVideoAPI().user_rent_video_list("" + prefManager.getLoginId());
        call.enqueue(new Callback<RentProductModel>() {
            @Override
            public void onResponse(Call<RentProductModel> call, Response<RentProductModel> response) {
                Utility.shimmerHide(shimmer);
                purchageVideoList = new ArrayList<>();
                purchageShowList = new ArrayList<>();
                try {
                    Log.e("user_rent_video_list", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        lyPurchase.setVisibility(View.VISIBLE);

                        if (response.body().getVideo() != null) {
                            if (response.body().getVideo().size() > 0) {
                                purchageVideoList = response.body().getVideo();
                                Log.e("purchageVideoList", "size => " + purchageVideoList.size());

                                Utility.removeNullObjects(purchageVideoList);
                                Log.e("purchageVideoList", "final size => " + purchageVideoList.size());

                                if (purchageVideoList.size() > 0) {
                                    lyPurchasedVideo.setVisibility(View.VISIBLE);
                                    txtTotalVideo.setText("(" + purchageVideoList.size() + " " + getResources().getString(R.string.videos) + ")");
                                    setVideos();
                                } else {
                                    lyPurchasedVideo.setVisibility(View.GONE);
                                }

                            } else {
                                lyPurchasedVideo.setVisibility(View.GONE);
                            }
                        } else {
                            lyPurchasedVideo.setVisibility(View.GONE);
                        }

                        if (response.body().getTvshow() != null) {
                            if (response.body().getTvshow().size() > 0) {
                                purchageShowList = response.body().getTvshow();
                                Log.e("purchageShowList", "size => " + purchageShowList.size());

                                Utility.removeNullObjects(purchageShowList);
                                Log.e("purchageShowList", "final size => " + purchageShowList.size());

                                if (purchageShowList.size() > 0) {
                                    lyPurchasedShow.setVisibility(View.VISIBLE);
                                    txtTotalShow.setText("(" + purchageShowList.size() + " " + getResources().getString(R.string._shows) + ")");
                                    setShows();
                                } else {
                                    lyPurchasedShow.setVisibility(View.GONE);
                                }

                            } else {
                                lyPurchasedShow.setVisibility(View.GONE);
                            }
                        } else {
                            lyPurchasedShow.setVisibility(View.GONE);
                        }

                        if (purchageVideoList.size() == 0 && purchageShowList.size() == 0) {
                            lyPurchase.setVisibility(View.GONE);
                            lyEmptyPurchase.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.e("user_rent_video_list", "Message => " + response.body().getMessage());
                        lyPurchase.setVisibility(View.GONE);
                        lyEmptyPurchase.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    lyPurchase.setVisibility(View.GONE);
                    lyEmptyPurchase.setVisibility(View.VISIBLE);
                    Log.e("user_rent_video_list", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<RentProductModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                lyPurchase.setVisibility(View.GONE);
                lyEmptyPurchase.setVisibility(View.VISIBLE);
                Log.e("user_rent_video_list", "onFailure => " + t.getMessage());
            }
        });
    }

    private void setVideos() {
        if (purchageVideoList != null) {
            if (purchageVideoList.size() > 0) {
                Log.e("purchageVideoList", "size =>>> " + purchageVideoList.size());

                rentPurchaseAdapter = new RentPurchaseAdapter(getActivity(), purchageVideoList, RentPurchasesF.this,
                        "Video", "" + prefManager.getValue("currency_code"));
                if (purchageVideoList.size() == 1) {
                    rvPurchasedVideo.setLayoutManager(new GridLayoutManager(getActivity(), 1,
                            LinearLayoutManager.HORIZONTAL, false));
                } else if (purchageVideoList.size() > 1) {
                    rvPurchasedVideo.setLayoutManager(new GridLayoutManager(getActivity(), 2,
                            LinearLayoutManager.HORIZONTAL, false));
                }
                rvPurchasedVideo.setAdapter(rentPurchaseAdapter);
                rentPurchaseAdapter.notifyDataSetChanged();

                lyPurchasedVideo.setVisibility(View.VISIBLE);
            } else {
                lyPurchasedVideo.setVisibility(View.GONE);
            }
        } else {
            lyPurchasedVideo.setVisibility(View.GONE);
        }
    }

    private void setShows() {
        if (purchageShowList != null) {
            if (purchageShowList.size() > 0) {
                Log.e("purchageShowList", "size =>>> " + purchageShowList.size());

                rentPurchaseAdapter = new RentPurchaseAdapter(getActivity(), purchageShowList, RentPurchasesF.this,
                        "Shows", "Search", "" + prefManager.getValue("currency_code"));
                if (purchageShowList.size() == 1) {
                    rvPurchasedShow.setLayoutManager(new GridLayoutManager(getActivity(), 1,
                            LinearLayoutManager.HORIZONTAL, false));
                } else if (purchageShowList.size() > 1) {
                    rvPurchasedShow.setLayoutManager(new GridLayoutManager(getActivity(), 2,
                            LinearLayoutManager.HORIZONTAL, false));
                }
                rvPurchasedShow.setAdapter(rentPurchaseAdapter);
                rentPurchaseAdapter.notifyDataSetChanged();

                lyPurchasedShow.setVisibility(View.VISIBLE);
            } else {
                lyPurchasedShow.setVisibility(View.GONE);
            }
        } else {
            lyPurchasedShow.setVisibility(View.GONE);
        }
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

        if (clickType.equalsIgnoreCase("Video")) {
            Utility.openDetails(getActivity(), "" + purchageVideoList.get(position).getId(),
                    "" + purchageVideoList.get(position).getVideoType(),
                    "" + purchageVideoList.get(position).getTypeId(),
                    "" + purchageVideoList.get(position).getUpcomingType());

        } else if (clickType.equalsIgnoreCase("Show")) {
            Utility.openDetails(getActivity(), "" + purchageShowList.get(position).getId(),
                    "" + purchageShowList.get(position).getVideoType(),
                    "" + purchageShowList.get(position).getTypeId(),
                    "" + purchageShowList.get(position).getUpcomingType());
        }
    }

    @Override
    public void longClick(String itemID, String clickType, int position) {
        Log.e(TAG, "itemID ==>> " + itemID);
        Log.e(TAG, "clickType ==>> " + clickType);
        Log.e(TAG, "position ==>> " + position);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        Utility.shimmerHide(shimmer);
        Utility.ProgressbarHide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "called");
        Utility.shimmerHide(shimmer);
        Utility.ProgressbarHide();
    }

}
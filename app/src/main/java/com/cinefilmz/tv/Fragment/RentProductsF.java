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

import com.cinefilmz.tv.Adapter.RentShowAdapter;
import com.cinefilmz.tv.Adapter.RentVideoAdapter;
import com.cinefilmz.tv.Model.RentProductModel.RentProductModel;
import com.cinefilmz.tv.Model.RentProductModel.Tvshow;
import com.cinefilmz.tv.Model.RentProductModel.Video;
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

public class RentProductsF extends Fragment implements View.OnClickListener {

    private PrefManager prefManager;
    private static final String TAG = RentProductsF.class.getSimpleName();
    private View root;

    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvRentVideo, rvRentShow;
    private TextView txtTotalVideo, txtTotalShow, txtCurrencySymbolVideo, txtCurrencySymbolShow;
    private LinearLayout lyRentVideo, lyRentShow, lyNoData;

    private RentVideoAdapter rentVideoAdapter;
    private RentShowAdapter rentShowAdapter;
    private List<Video> rentVideoList;
    private List<Tvshow> rentShowList;

    private boolean isVideoSelected = true;

    public RentProductsF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_rent_products, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();
        GetRentProducts();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        Log.e("=>isVideoSelected", "" + isVideoSelected);
                        GetRentProducts();
//                        if (isVideoSelected) {
//                            RentVideos();
//                        } else {
//                            RentShows();
//                        }
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

            lyRentVideo = root.findViewById(R.id.lyRentVideo);
            lyRentShow = root.findViewById(R.id.lyRentShow);
            lyNoData = root.findViewById(R.id.lyNoData);
            txtTotalVideo = root.findViewById(R.id.txtTotalVideo);
            txtTotalShow = root.findViewById(R.id.txtTotalShow);
            txtCurrencySymbolVideo = root.findViewById(R.id.txtCurrencySymbolVideo);
            txtCurrencySymbolShow = root.findViewById(R.id.txtCurrencySymbolShow);

            rvRentVideo = root.findViewById(R.id.rvRentVideo);
            rvRentShow = root.findViewById(R.id.rvRentShow);
        } catch (Exception e) {
            Log.e(TAG, "init Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    /* rent_video_list API */
    private void GetRentProducts() {
        Utility.shimmerShow(shimmer);
        Call<RentProductModel> call = BaseURL.getVideoAPI().rent_video_list("" + prefManager.getLoginId());
        call.enqueue(new Callback<RentProductModel>() {
            @Override
            public void onResponse(Call<RentProductModel> call, Response<RentProductModel> response) {
                rentVideoList = new ArrayList<>();
                rentShowList = new ArrayList<>();
                try {
                    Log.e("rent_video_list", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getVideo() != null) {
                            if (response.body().getVideo().size() > 0) {
                                rentVideoList = response.body().getVideo();
                                Log.e("rentVideoList", "size => " + rentVideoList.size());

                                Utility.removeNullObjects(rentVideoList);
                                Log.e("rentVideoList", "final size => " + rentVideoList.size());

                                if (rentVideoList.size() > 0) {
                                    lyRentVideo.setVisibility(View.VISIBLE);
                                    txtTotalVideo.setText("(" + rentVideoList.size() + " " + getResources().getString(R.string.videos) + ")");
                                    RentVideos();
                                } else {
                                    lyRentVideo.setVisibility(View.GONE);
                                }
                            } else {
                                lyRentVideo.setVisibility(View.GONE);
                            }
                        } else {
                            lyRentVideo.setVisibility(View.GONE);
                        }

                        if (response.body().getTvshow() != null) {
                            if (response.body().getTvshow().size() > 0) {
                                rentShowList = response.body().getTvshow();
                                Log.e("rentShowList", "size => " + rentShowList.size());

                                Utility.removeNullObjects(rentShowList);
                                Log.e("rentShowList", "final size => " + rentShowList.size());

                                if (rentShowList.size() > 0) {
                                    lyRentShow.setVisibility(View.VISIBLE);
                                    txtTotalShow.setText("(" + rentShowList.size() + " " + getResources().getString(R.string._shows) + ")");
                                    RentShows();
                                } else {
                                    lyRentShow.setVisibility(View.GONE);
                                }
                            } else {
                                lyRentShow.setVisibility(View.GONE);
                            }
                        } else {
                            lyRentShow.setVisibility(View.GONE);
                        }

                    } else {
                        Log.e("rent_video_list", "Message => " + response.body().getMessage());
                        lyRentVideo.setVisibility(View.GONE);
                        lyRentShow.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    lyRentVideo.setVisibility(View.GONE);
                    lyRentShow.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                    Log.e("rent_video_list", "Exception => " + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<RentProductModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                lyRentVideo.setVisibility(View.GONE);
                lyRentShow.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
                Log.e("rent_video_list", "onFailure => " + t.getMessage());
            }
        });
    }

    private void RentVideos() {
        if (rentVideoList.size() > 0) {
            txtCurrencySymbolVideo.setText("" + prefManager.getValue("currency_code"));

            rvRentVideo.setAdapter(null);
            rentVideoAdapter = new RentVideoAdapter(getActivity(), rentVideoList, "" + prefManager.getValue("currency_code"),
                    "landscape");
            if (rentVideoList.size() == 1) {
                rvRentVideo.setLayoutManager(new GridLayoutManager(getActivity(), 1,
                        LinearLayoutManager.HORIZONTAL, false));
            } else if (rentVideoList.size() > 1 && rentVideoList.size() < 7) {
                rvRentVideo.setLayoutManager(new GridLayoutManager(getActivity(), 2,
                        LinearLayoutManager.HORIZONTAL, false));
            } else if (rentVideoList.size() > 6) {
                rvRentVideo.setLayoutManager(new GridLayoutManager(getActivity(), 3,
                        LinearLayoutManager.HORIZONTAL, false));
            }
            rvRentVideo.setAdapter(rentVideoAdapter);
            rentVideoAdapter.notifyDataSetChanged();

            lyRentVideo.setVisibility(View.VISIBLE);
        } else {
            lyRentVideo.setVisibility(View.GONE);
        }
    }

    private void RentShows() {
        if (rentShowList.size() > 0) {
            txtCurrencySymbolShow.setText("" + prefManager.getValue("currency_code"));

            rvRentShow.setAdapter(null);
            rentShowAdapter = new RentShowAdapter(getActivity(), rentShowList, "" + prefManager.getValue("currency_code"),
                    "landscape");
            if (rentShowList.size() == 1) {
                rvRentShow.setLayoutManager(new GridLayoutManager(getActivity(), 1,
                        LinearLayoutManager.HORIZONTAL, false));
            } else if (rentShowList.size() > 1 && rentShowList.size() < 7) {
                rvRentShow.setLayoutManager(new GridLayoutManager(getActivity(), 2,
                        LinearLayoutManager.HORIZONTAL, false));
            } else if (rentShowList.size() > 6) {
                rvRentShow.setLayoutManager(new GridLayoutManager(getActivity(), 3,
                        LinearLayoutManager.HORIZONTAL, false));
            }
            rvRentShow.setAdapter(rentShowAdapter);
            rentShowAdapter.notifyDataSetChanged();

            lyRentShow.setVisibility(View.VISIBLE);
        } else {
            lyRentShow.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        Log.e("onDestroy", "called");
        Utility.shimmerHide(shimmer);
    }

}
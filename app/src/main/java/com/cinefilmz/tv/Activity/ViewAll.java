package com.cinefilmz.tv.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinefilmz.tv.Adapter.AllVideosAdapter;
import com.cinefilmz.tv.Model.VideoModel.Result;
import com.cinefilmz.tv.Model.VideoModel.VideoModel;
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

public class ViewAll extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;

    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmer;
    private LinearLayout lyNoData;
    private TextView txtMainTitle, txtTotalVideos;
    private RecyclerView rvViewAll;

    private AllVideosAdapter allVideosAdapter;
    private List<Result> allVideoList;

    private String viewAllType = "", mainTitle = "", itemID = "", videoType = "", layoutType = "", typeID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewall);
        PrefManager.forceRTLIfSupported(getWindow(), ViewAll.this);

        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            viewAllType = bundle.getString("viewAllType");
            itemID = bundle.getString("itemID");
            mainTitle = bundle.getString("itemTitle");
            videoType = bundle.getString("videoType");
            layoutType = bundle.getString("layoutType");
            typeID = bundle.getString("typeID");

            txtMainTitle.setText("" + mainTitle);

            Log.e("==>viewAllType", "" + viewAllType);
            Log.e("==>itemID", "" + itemID);
            Log.e("==>mainTitle", "" + mainTitle);
            Log.e("==>videoType", "" + videoType);
            Log.e("==>layoutType", "" + layoutType);
            Log.e("==>typeID", "" + typeID);

            if (viewAllType.equalsIgnoreCase("ByBrowse")) {
                VideosByBrowse();
            } else if (viewAllType.equalsIgnoreCase("ByGenres")) {
                VideosByCategory();
            } else if (viewAllType.equalsIgnoreCase("ByLanguage")) {
                VideosByLanguage();
            }
        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        if (videoType.equalsIgnoreCase("ByGenres")) {
                            VideosByCategory();
                        } else if (videoType.equalsIgnoreCase("ByLanguage")) {
                            VideosByLanguage();
                        }
                    }
                }, getResources().getInteger(R.integer.swipeRefreshDelay));
            }
        });

    }

    private void init() {
        try {
            prefManager = new PrefManager(ViewAll.this);

            shimmer = findViewById(R.id.shimmer);
            swipeRefresh = findViewById(R.id.swipeRefresh);
            txtMainTitle = findViewById(R.id.txtMainTitle);
            txtTotalVideos = findViewById(R.id.txtTotalVideos);

            rvViewAll = findViewById(R.id.rvViewAll);
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

    private void VideosByBrowse() {
        allVideoList = new ArrayList<>();
        allVideosAdapter = new AllVideosAdapter(ViewAll.this, allVideoList, "ViewAll", "" + videoType, "" + layoutType, "" + typeID);
        rvViewAll.setLayoutManager(new GridLayoutManager(ViewAll.this, 2));
        rvViewAll.setAdapter(allVideosAdapter);
        allVideosAdapter.notifyDataSetChanged();
    }

    /* video_by_category API */
    private void VideosByCategory() {
        Utility.shimmerShow(shimmer);
        Call<VideoModel> call = BaseURL.getVideoAPI().video_by_category("" + prefManager.getLoginId(), "" + typeID, "" + itemID);
        call.enqueue(new Callback<VideoModel>() {
            @Override
            public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    Log.e("video_by_category", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            allVideoList = new ArrayList<>();
                            allVideoList = response.body().getResult();
                            Log.e("allVideoList", "size => " + allVideoList.size());

                            txtTotalVideos.setText(allVideoList.size() + " " + getResources().getString(R.string.videos));

                            allVideosAdapter = new AllVideosAdapter(ViewAll.this, allVideoList, "ViewAll", "" + videoType,
                                    "" + layoutType, "" + typeID);
                            rvViewAll.setLayoutManager(new GridLayoutManager(ViewAll.this, 2));
                            rvViewAll.setAdapter(allVideosAdapter);
                            allVideosAdapter.notifyDataSetChanged();

                            rvViewAll.setVisibility(View.VISIBLE);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvViewAll.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                        }

                    } else {
                        rvViewAll.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    rvViewAll.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                    Log.e("video_by_category", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<VideoModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                rvViewAll.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
                Log.e("video_by_category", "onFailure => " + t.getMessage());
            }
        });
    }

    /* video_by_language API */
    private void VideosByLanguage() {
        Utility.shimmerShow(shimmer);
        Call<VideoModel> call = BaseURL.getVideoAPI().video_by_language("" + prefManager.getLoginId(), "" + typeID, "" + itemID);
        call.enqueue(new Callback<VideoModel>() {
            @Override
            public void onResponse(Call<VideoModel> call, Response<VideoModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    Log.e("video_by_language", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            allVideoList = new ArrayList<>();
                            allVideoList = response.body().getResult();
                            Log.e("allVideoList", "size => " + allVideoList.size());

                            txtTotalVideos.setText(allVideoList.size() + " " + getResources().getString(R.string.videos));

                            allVideosAdapter = new AllVideosAdapter(ViewAll.this, allVideoList, "ViewAll", "" + videoType,
                                    "" + layoutType, "" + typeID);
                            rvViewAll.setLayoutManager(new GridLayoutManager(ViewAll.this, 2));
                            rvViewAll.setAdapter(allVideosAdapter);
                            allVideosAdapter.notifyDataSetChanged();

                            rvViewAll.setVisibility(View.VISIBLE);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvViewAll.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                        }

                    } else {
                        rvViewAll.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    rvViewAll.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                    Log.e("video_by_language", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<VideoModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                rvViewAll.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
                Log.e("video_by_language", "onFailure => " + t.getMessage());
            }
        });
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
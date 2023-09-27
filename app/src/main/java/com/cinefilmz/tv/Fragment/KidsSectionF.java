package com.cinefilmz.tv.Fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinefilmz.tv.Adapter.BannerAdapter;
import com.cinefilmz.tv.Adapter.ContinueWatchAdapter;
import com.cinefilmz.tv.Adapter.SectionKidsAdapter;
import com.cinefilmz.tv.Model.SectionBannerModel.Result;
import com.cinefilmz.tv.Model.SectionBannerModel.SectionBannerModel;
import com.cinefilmz.tv.Model.SectionListModel.ContinueWatching;
import com.cinefilmz.tv.Model.SectionListModel.Datum;
import com.cinefilmz.tv.Model.SectionListModel.SectionListModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdView;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.List;

import pl.pzienowicz.autoscrollviewpager.AutoScrollViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KidsSectionF extends Fragment implements View.OnClickListener {

    private PrefManager prefManager;
    private View root;

    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmer;
    private AutoScrollViewPager mViewPager;
    private DotsIndicator dotsIndicator;
    private LinearLayout lyBanner, lyDataDynamic, lyContinueWatch, lyContinueWatchTitle, lyBannerAds, lyAdView, lyFbAdView;
    private RecyclerView rvContinueWatch;
    private TextView txtContinueWatchMore;

    private BannerAdapter bannerAdapter;
    private SectionKidsAdapter sectionKidsAdapter;
    private ContinueWatchAdapter continueWatchAdapter;

    private List<Result> bannerList;
    private List<com.cinefilmz.tv.Model.SectionListModel.Result> sectionList;
    private List<Datum> sectionDataList;
    private List<ContinueWatching> continueWatchingList;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    public KidsSectionF() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_kidssectionf, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();
        AdInit();
        DisplayBanner();
        KidsSectionData();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);

                        DisplayBanner();
                        lyDataDynamic.removeAllViews();
                        KidsSectionData();
                    }
                }, getResources().getInteger(R.integer.swipeRefreshDelay));
            }
        });

        return root;
    }

    private void init() {
        try {
            prefManager = new PrefManager(getActivity());

            swipeRefresh = root.findViewById(R.id.swipeRefresh);
            shimmer = root.findViewById(R.id.shimmer);

            lyBannerAds = root.findViewById(R.id.lyBannerAds);
            lyFbAdView = root.findViewById(R.id.lyFbAdView);
            lyAdView = root.findViewById(R.id.lyAdView);

            lyBanner = root.findViewById(R.id.lyBanner);
            mViewPager = root.findViewById(R.id.viewPager);
            dotsIndicator = root.findViewById(R.id.dotsIndicator);

            txtContinueWatchMore = root.findViewById(R.id.txtContinueWatchMore);
            rvContinueWatch = root.findViewById(R.id.rvContinueWatch);
            lyContinueWatch = root.findViewById(R.id.lyContinueWatch);
            lyContinueWatchTitle = root.findViewById(R.id.lyContinueWatchTitle);
            lyDataDynamic = root.findViewById(R.id.lyDataDynamic);

            lyContinueWatchTitle.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));

        lyBannerAds.setVisibility(View.VISIBLE);
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("1")) {
            lyAdView.setVisibility(View.VISIBLE);
            lyFbAdView.setVisibility(View.GONE);
            Utility.Admob(getActivity(), mAdView, "" + prefManager.getValue("banner_adid"), lyAdView);
        } else if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("1")) {
            lyAdView.setVisibility(View.GONE);
            lyFbAdView.setVisibility(View.VISIBLE);
            Utility.FacebookBannerAd(getActivity(), fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyBannerAds.setVisibility(View.GONE);
            lyAdView.setVisibility(View.GONE);
            lyFbAdView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.startAutoScroll(getResources().getInteger(R.integer.bannerInterval));
    }

    /* get_banner API */
    private void DisplayBanner() {
        Utility.shimmerShow(shimmer);
        Call<SectionBannerModel> call = BaseURL.getVideoAPI().get_banner("" + prefManager.getLoginId(), "1", "1");
        call.enqueue(new Callback<SectionBannerModel>() {
            @Override
            public void onResponse(Call<SectionBannerModel> call, Response<SectionBannerModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    Log.e("get_banner", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            bannerList = new ArrayList<>();
                            bannerList = response.body().getResult();
                            Log.e("bannerList", "" + bannerList.size());
                            SetBanner();

                            lyBanner.setVisibility(View.VISIBLE);
                        } else {
                            lyBanner.setVisibility(View.GONE);
                        }

                    } else {
                        lyBanner.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("get_banner", "Exception => " + e);
                    lyBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<SectionBannerModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e("get_banner", "onFailure => " + t.getMessage());
                lyBanner.setVisibility(View.GONE);
            }
        });
    }

    private void SetBanner() {
        bannerAdapter = new BannerAdapter(getActivity(), bannerList, "Kids");
        mViewPager.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        dotsIndicator.setViewPager(mViewPager);
        mViewPager.setInterval(getResources().getInteger(R.integer.bannerInterval));
        mViewPager.setCycle(true);
        mViewPager.setStopScrollWhenTouch(true);
        mViewPager.setScrollDurationFactor(5);
    }

    /* section_list API */
    private void KidsSectionData() {
        Utility.shimmerShow(shimmer);
        Call<SectionListModel> call = BaseURL.getVideoAPI().section_list("" + prefManager.getLoginId(), "1", "1");
        call.enqueue(new Callback<SectionListModel>() {
            @Override
            public void onResponse(Call<SectionListModel> call, Response<SectionListModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    Log.e("section_list", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getContinueWatching() != null) {
                            if (response.body().getContinueWatching().size() > 0) {
                                continueWatchingList = new ArrayList<>();
                                continueWatchingList = response.body().getContinueWatching();
                                Log.e("continueWatchingList", "size => " + continueWatchingList.size());

                                Utility.removeNullObjects(continueWatchingList);
                                Log.e("continueWatchingList", "final size => " + continueWatchingList.size());

                                if (continueWatchingList.size() > 0) {
                                    lyContinueWatch.setVisibility(View.VISIBLE);
                                    SetContinueWatch();
                                } else {
                                    lyContinueWatch.setVisibility(View.GONE);
                                }
                            } else {
                                lyContinueWatch.setVisibility(View.GONE);
                            }
                        } else {
                            lyContinueWatch.setVisibility(View.GONE);
                        }

                        if (response.body().getResult() != null) {
                            if (response.body().getResult().size() > 0) {
                                sectionList = new ArrayList<>();
                                sectionList = response.body().getResult();
                                Log.e("sectionList", "size => " + sectionList.size());

                                Utility.removeNullObjects(sectionList);
                                Log.e("sectionList", "final size => " + sectionList.size());

                                if (sectionList.size() > 0) {
                                    lyDataDynamic.setVisibility(View.VISIBLE);
                                    SetDataDynamic();
                                } else {
                                    lyDataDynamic.setVisibility(View.GONE);
                                }
                            } else {
                                lyDataDynamic.setVisibility(View.GONE);
                            }
                        } else {
                            lyDataDynamic.setVisibility(View.GONE);
                        }

                    } else {
                        lyDataDynamic.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    lyDataDynamic.setVisibility(View.GONE);
                    Log.e("section_list", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SectionListModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                lyDataDynamic.setVisibility(View.GONE);
                Log.e("section_list", "onFailure => " + t.getMessage());
            }
        });
    }

    private void SetContinueWatch() {
        continueWatchAdapter = new ContinueWatchAdapter(getActivity(), continueWatchingList, "Kids");
        rvContinueWatch.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        rvContinueWatch.setAdapter(continueWatchAdapter);
        continueWatchAdapter.notifyDataSetChanged();
    }

    /*================= Set Data Dynamic START =================*/
    private void SetDataDynamic() {
        try {
            for (int i = 0; i < sectionList.size(); i++) {
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "font/public_bold.ttf");

                LinearLayout.LayoutParams paramMain = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramMain.setMargins(0, (int) getResources().getDimension(R.dimen.margin12), 0, 0);

                LinearLayout lyMain = new LinearLayout(getActivity());
                lyMain.setOrientation(LinearLayout.VERTICAL);
                lyMain.setLayoutParams(paramMain);

                /* *********************** Title START *********************** */
                LinearLayout.LayoutParams paramTitleMain = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramTitleMain.setMargins((int) getResources().getDimension(R.dimen.titleMarginSmall), 0, 0, 0);

                LinearLayout lyTitleDynamic = new LinearLayout(getActivity());
                lyTitleDynamic.setOrientation(LinearLayout.HORIZONTAL);
                lyTitleDynamic.setLayoutParams(paramTitleMain);

                LinearLayout.LayoutParams paramTitle = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView txtTitleDynamic = new TextView(getActivity());
                txtTitleDynamic.setLayoutParams(paramTitle);
                txtTitleDynamic.setTextColor(getResources().getColor(R.color.text_white));
                txtTitleDynamic.setText("" + sectionList.get(i).getTitle());
                txtTitleDynamic.setTypeface(face);
                txtTitleDynamic.setGravity(Gravity.CENTER_VERTICAL);
                txtTitleDynamic.setTextSize(16f);

                lyTitleDynamic.addView(txtTitleDynamic);
                /* *********************** Title END *********************** */

                sectionDataList = new ArrayList<>();
                sectionDataList = sectionList.get(i).getData();

                /* *********************** RecycleView START *********************** */
                LinearLayout.LayoutParams paramRecycleview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramRecycleview.setMargins(0, (int) getResources().getDimension(R.dimen.margin1), 0, 8);

                RecyclerView rvDataDynamic = new RecyclerView(getActivity());
                rvDataDynamic.setLayoutParams(paramRecycleview);

                Log.e("==>VideoType", "" + sectionList.get(i).getVideoType());
                Log.e("==>TypeId", "" + sectionList.get(i).getTypeId());
                Log.e("==>ScreenLayout", "" + sectionList.get(i).getScreenLayout());
                sectionKidsAdapter = new SectionKidsAdapter(getActivity(), sectionDataList, "Kids", "" + sectionList.get(i).getVideoType(),
                        "" + sectionList.get(i).getScreenLayout(), "" + sectionList.get(i).getTypeId());
                rvDataDynamic.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                rvDataDynamic.setAdapter(sectionKidsAdapter);
                sectionKidsAdapter.notifyDataSetChanged();
                lyDataDynamic.setVisibility(View.VISIBLE);
                /* *********************** RecycleView END *********************** */

                if (sectionDataList != null) {
                    if (sectionDataList.size() > 0) {
                        Log.e("sectionDataList", "size => " + sectionDataList.size());

                        lyMain.addView(lyTitleDynamic);
                        lyMain.addView(rvDataDynamic);

                        lyDataDynamic.addView(lyMain);
                    }
                }

            }
        } catch (Exception e) {
            Log.e("SetDataDynamic", "Exception => " + e);
        }
    }
    /*================= Set Data Dynamic END =================*/

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        Utility.shimmerHide(shimmer);
        mViewPager.stopAutoScroll();
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "called");
        Utility.shimmerHide(shimmer);
        mViewPager.stopAutoScroll();
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

}
package com.cinefilmz.tv.Fragment;

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

import com.cinefilmz.tv.Adapter.ChannelBannerAdapter;
import com.cinefilmz.tv.Adapter.SectionChannelAdapter;
import com.cinefilmz.tv.Model.SectionChannelModel.Result;
import com.cinefilmz.tv.Model.SectionChannelModel.Datum;
import com.cinefilmz.tv.Model.SectionChannelModel.LiveUrl;
import com.cinefilmz.tv.Model.SectionChannelModel.SectionChannelModel;
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

public class ChannelsF extends Fragment implements View.OnClickListener {

    public ChannelsF() {
    }

    private PrefManager prefManager;
    private View root;

    private SwipeRefreshLayout swipeRefresh;
    private ShimmerFrameLayout shimmer;
    private AutoScrollViewPager mViewPager;
    private DotsIndicator dotsIndicator;
    private LinearLayout lyBanner, lyVideosByChannels, lyBannerAds, lyAdView, lyFbAdView;

    private ChannelBannerAdapter channelBannerAdapter;
    private SectionChannelAdapter sectionChannelAdapter;

    private List<LiveUrl> channelBannerList;
    private List<Result> sectionChannelList;
    private List<Datum> sectionDataList;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_channels, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();
        AdInit();
        ChannelSectionData();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        lyVideosByChannels.removeAllViews();
                        ChannelSectionData();
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
            lyVideosByChannels = root.findViewById(R.id.lyVideosByChannels);
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
        switch (v.getId()) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.startAutoScroll(getResources().getInteger(R.integer.bannerInterval));
    }

    /* channel_section_list API */
    private void ChannelSectionData() {
        Utility.shimmerShow(shimmer);
        Call<SectionChannelModel> call = BaseURL.getVideoAPI().channel_section_list("" + prefManager.getLoginId());
        call.enqueue(new Callback<SectionChannelModel>() {
            @Override
            public void onResponse(Call<SectionChannelModel> call, Response<SectionChannelModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    channelBannerList = new ArrayList<>();
                    sectionChannelList = new ArrayList<>();

                    Log.e("channel_section_list", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getLiveUrl().size() > 0) {
                            lyBanner.setVisibility(View.VISIBLE);

                            channelBannerList = response.body().getLiveUrl();
                            Log.e("channelBannerList", "==>>>> " + channelBannerList.size());

                            SetBanner();
                        } else {
                            lyBanner.setVisibility(View.GONE);
                        }

                        if (response.body().getResult().size() > 0) {
                            sectionChannelList = response.body().getResult();
                            Log.e("sectionChannelList", "size => " + sectionChannelList.size());

                            VideosByChannelDynamic();

                            lyVideosByChannels.setVisibility(View.VISIBLE);
                        } else {
                            lyVideosByChannels.setVisibility(View.GONE);
                        }

                    } else {
                        lyVideosByChannels.setVisibility(View.GONE);
                        lyBanner.setVisibility(View.GONE);
                        Log.e("channel_section_list", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    lyVideosByChannels.setVisibility(View.GONE);
                    lyBanner.setVisibility(View.GONE);
                    Log.e("channel_section_list", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SectionChannelModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                lyVideosByChannels.setVisibility(View.GONE);
                lyBanner.setVisibility(View.GONE);
                Log.e("channel_section_list", "onFailure => " + t.getMessage());
            }
        });
    }

    private void SetBanner() {
        channelBannerAdapter = new ChannelBannerAdapter(getActivity(), channelBannerList, "LiveChannel");
        mViewPager.setAdapter(channelBannerAdapter);
        channelBannerAdapter.notifyDataSetChanged();
        dotsIndicator.setViewPager(mViewPager);
        mViewPager.setInterval(getResources().getInteger(R.integer.bannerInterval));
        mViewPager.setCycle(true);
        mViewPager.setStopScrollWhenTouch(true);
        mViewPager.setScrollDurationFactor(5);
    }

    /*================= Videos by Channel Dynamic START =================*/
    private void VideosByChannelDynamic() {
        try {
            for (int i = 0; i < sectionChannelList.size(); i++) {
                Typeface faceTitle = Typeface.createFromAsset(getActivity().getAssets(), "font/public_round_bold.ttf");
                Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "font/public_bold.ttf");

                LinearLayout.LayoutParams paramMain = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout lyMain = new LinearLayout(getActivity());
                lyMain.setOrientation(LinearLayout.VERTICAL);
                lyMain.setLayoutParams(paramMain);

                /* *********************** Title START *********************** */
                LinearLayout.LayoutParams paramTitleMain = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramTitleMain.setMargins((int) getResources().getDimension(R.dimen.titleMarginSmall), (int) getResources().getDimension(R.dimen.contentMarginTop), (int) getResources().getDimension(R.dimen.titleMarginEnd), 0);

                LinearLayout lyChannelTitleDynamic = new LinearLayout(getActivity());
                lyChannelTitleDynamic.setOrientation(LinearLayout.HORIZONTAL);
                lyChannelTitleDynamic.setLayoutParams(paramTitleMain);

                LinearLayout.LayoutParams paramChannelTitle = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView txtChannelTitle = new TextView(getActivity());
                txtChannelTitle.setLayoutParams(paramChannelTitle);
                txtChannelTitle.setTextColor(getResources().getColor(R.color.text_other));
                txtChannelTitle.setText("" + sectionChannelList.get(i).getChannelName());
                txtChannelTitle.setTypeface(faceTitle);
                txtChannelTitle.setGravity(Gravity.CENTER_VERTICAL);
                txtChannelTitle.setTextSize(10f);

                lyChannelTitleDynamic.addView(txtChannelTitle);
                /* *********************** Title END *********************** */

                /* *********************** Cat-Title START *********************** */
                LinearLayout.LayoutParams paramCatTitleMain = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramCatTitleMain.setMargins((int) getResources().getDimension(R.dimen.titleMarginSmall), 0, (int) getResources().getDimension(R.dimen.titleMarginEnd), 0);

                LinearLayout lyChannelSubTitleDynamic = new LinearLayout(getActivity());
                lyChannelSubTitleDynamic.setOrientation(LinearLayout.HORIZONTAL);
                lyChannelSubTitleDynamic.setLayoutParams(paramCatTitleMain);

                LinearLayout.LayoutParams paramCatTitle = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                TextView txtCatTitle = new TextView(getActivity());
                txtCatTitle.setLayoutParams(paramCatTitle);
                txtCatTitle.setTextColor(getResources().getColor(R.color.text_white));
                txtCatTitle.setText("" + sectionChannelList.get(i).getTitle());
                txtCatTitle.setTypeface(face);
                txtCatTitle.setGravity(Gravity.CENTER_VERTICAL);
                txtCatTitle.setTextSize(16f);

                lyChannelSubTitleDynamic.addView(txtCatTitle);
                /* *********************** Cat-Title START *********************** */

                sectionDataList = new ArrayList<>();
                sectionDataList = sectionChannelList.get(i).getData();

                /* *********************** RecyclerView START *********************** */
                LinearLayout.LayoutParams paramRecycleview = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                RecyclerView rvVideosByChannels = new RecyclerView(getActivity());
                rvVideosByChannels.setLayoutParams(paramRecycleview);

                sectionChannelAdapter = new SectionChannelAdapter(getActivity(), sectionDataList, "Channel", "" + sectionChannelList.get(i).getVideoType(),
                        "" + sectionChannelList.get(i).getScreenLayout());
                rvVideosByChannels.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                rvVideosByChannels.setAdapter(sectionChannelAdapter);
                sectionChannelAdapter.notifyDataSetChanged();
                lyVideosByChannels.setVisibility(View.VISIBLE);
                /* *********************** RecyclerView START *********************** */

                if (sectionDataList != null) {
                    if (sectionDataList.size() > 0) {
                        Log.e("sectionDataList", "size => " + sectionDataList.size());

                        lyMain.addView(lyChannelTitleDynamic);
                        lyMain.addView(lyChannelSubTitleDynamic);
                        lyMain.addView(rvVideosByChannels);

                        lyVideosByChannels.addView(lyMain);
                    }
                }
            }
        } catch (Exception e) {
            Log.e("VideosByChannelDynamic", "Exception => " + e);
        }
    }
    /*================= Videos by Channels Dynamic END =================*/

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        Utility.shimmerHide(shimmer);
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
            fbAdView = null;
        }
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
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
            fbAdView = null;
        }
        mViewPager.stopAutoScroll();
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

}
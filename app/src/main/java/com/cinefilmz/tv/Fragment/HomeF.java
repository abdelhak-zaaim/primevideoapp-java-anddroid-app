package com.cinefilmz.tv.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cinefilmz.tv.Model.SectionTypeModel.Result;
import com.cinefilmz.tv.Model.SectionTypeModel.SectionTypeModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeF extends Fragment {

    private static final String TAG = HomeF.class.getSimpleName();
    private PrefManager prefManager;
    private View root;

    private ShimmerFrameLayout shimmer;
    private TabLayout tabLayout;
    private ViewPager tabViewpager;
    private LinearLayout lyNoData, lyBannerAds, lyAdView, lyFbAdView;

    private List<Result> sectionTypeList;

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;

    public HomeF() {
        Log.e(TAG, "Constructor called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());
        Log.e(TAG, "onCreateView called");

        prefManager = new PrefManager(getActivity());

        shimmer = root.findViewById(R.id.shimmer);
        tabLayout = root.findViewById(R.id.tabLayout);
        tabViewpager = root.findViewById(R.id.tabViewpager);
        lyNoData = root.findViewById(R.id.lyNoData);

        lyBannerAds = root.findViewById(R.id.lyBannerAds);
        lyFbAdView = root.findViewById(R.id.lyFbAdView);
        lyAdView = root.findViewById(R.id.lyAdView);

        AdInit();
        GetViewType();

        return root;
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

    /* get_type API */
    private void GetViewType() {
        Utility.shimmerShow(shimmer);

        Call<SectionTypeModel> call = BaseURL.getVideoAPI().get_type();
        call.enqueue(new Callback<SectionTypeModel>() {
            @Override
            public void onResponse(Call<SectionTypeModel> call, Response<SectionTypeModel> response) {
                try {
                    Log.e("get_type", "status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            sectionTypeList = new ArrayList<>();
                            sectionTypeList = response.body().getResult();
                            Log.e("sectionTypeList", "" + sectionTypeList.size());

                            SetTabs();

                            tabViewpager.setVisibility(View.VISIBLE);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            Utility.shimmerHide(shimmer);
                            tabViewpager.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.e("get_type", "message => " + response.body().getMessage());
                        Utility.shimmerHide(shimmer);
                        tabViewpager.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e("get_type", "Exception => " + e);
                    Utility.shimmerHide(shimmer);
                    tabViewpager.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SectionTypeModel> call, Throwable t) {
                Log.e("get_type", "onFailure => " + t.getMessage());
                Utility.shimmerHide(shimmer);
                tabViewpager.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void SetTabs() {
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        for (int i = 0; i <= sectionTypeList.size(); i++) {
            if (i == 0) {
                tabLayout.addTab(tabLayout.newTab().setText("All"));
            } else {
                tabLayout.addTab(tabLayout.newTab().setText(sectionTypeList.get(i - 1).getName()));
            }

            if (i == (sectionTypeList.size())) {
                Utility.shimmerHide(shimmer);
            }

        }
        setupViewPager(tabViewpager);
        tabLayout.setupWithViewPager(tabViewpager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Log.e("mNumOfTabs", "==========================> " + tabLayout.getTabCount());
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        int mNumOfTabs;

        public ViewPagerAdapter(FragmentManager manager, int tabCount) {
            super(manager);
            this.mNumOfTabs = tabCount;
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("=>mNumOfTabs", "" + mNumOfTabs);
            Log.e("=>position", "" + position);

            if (position == 0) {
                return HomeSectionF.addFragment("0", "1");
            } else {
                Log.e("=>(position-1)", "" + (position - 1));
                Log.e("=:=>type", "" + sectionTypeList.get(position - 1).getName());
                return HomeSectionF.addFragment("" + sectionTypeList.get(position - 1).getId(), "2");
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "All";
            } else {
                return "" + sectionTypeList.get(position - 1).getName();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause called");
        Utility.shimmerHide(shimmer);
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
            fbAdView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy called");
        Utility.shimmerHide(shimmer);
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
            fbAdView = null;
        }
    }

}
package com.cinefilmz.tv.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.cinefilmz.tv.Fragment.SubscriptionF;
import com.cinefilmz.tv.Model.SubscriptionModel.Result;
import com.cinefilmz.tv.Model.SubscriptionModel.SubscriptionModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.jinqiu.view.scaleviewpager.ScaleViewPager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Subscription extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private static final String TAG = Subscription.class.getSimpleName();

    private ShimmerFrameLayout shimmer;
    private LinearLayout lyToolbar, lyBack, lySubscriptionVP;
    private TextView txtToolbarTitle;
    private ScaleViewPager vpSubscription;

    private ViewPagerAdapter viewPagerAdapter;

    private List<Result> subscriptionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        PrefManager.forceRTLIfSupported(getWindow(), this);

        init();
        txtToolbarTitle.setText("" + getResources().getString(R.string.subscription));
        GetSubscription();

    }

    private void init() {
        try {
            prefManager = new PrefManager(this);

            shimmer = findViewById(R.id.shimmer);
            lyToolbar = findViewById(R.id.lyToolbar);
            lyBack = findViewById(R.id.lyBack);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            lySubscriptionVP = findViewById(R.id.lySubscriptionVP);
            vpSubscription = findViewById(R.id.vpSubscription);

            lyBack.setOnClickListener(this);
        } catch (Exception e) {
            Log.e(TAG, "init Exception => " + e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void GetSubscription() {
        Utility.shimmerShow(shimmer);
        Call<SubscriptionModel> call = BaseURL.getVideoAPI().get_package("" + prefManager.getLoginId());
        call.enqueue(new Callback<SubscriptionModel>() {
            @Override
            public void onResponse(Call<SubscriptionModel> call, Response<SubscriptionModel> response) {
                try {
                    Log.e("get_package", "status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            subscriptionList = new ArrayList<>();
                            subscriptionList = response.body().getResult();
                            Log.e("subscriptionList", "" + subscriptionList.size());

                            SetSubscriptionView();
                        }

                    } else {
                        Log.e("get_package", "message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("get_package", "Exception => " + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<SubscriptionModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e("get_package", "onFailure => " + t.getMessage());
            }
        });
    }

    private void SetSubscriptionView() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vpSubscription.setAdapter(viewPagerAdapter);
        vpSubscription.setCoverWidth(5f);
        vpSubscription.setCurrentItem(1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack:
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                overridePendingTransition(R.anim.enter, R.anim.exit);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
        overridePendingTransition(R.anim.enter, R.anim.exit);
        super.onBackPressed();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Log.e("=>position", "" + position);
            return SubscriptionF.addFragment(position, subscriptionList.get(position).getIsBuy());
        }

        @Override
        public int getCount() {
            return subscriptionList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }

    @Override
    protected void onPause() {
        Utility.shimmerHide(shimmer);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Utility.shimmerHide(shimmer);
        super.onDestroy();
    }

}
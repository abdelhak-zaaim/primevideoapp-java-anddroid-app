package com.cinefilmz.tv.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;

    private ViewPager viewPager;
    private DotsIndicator dotsIndicator;
    private TextView btnNext, btnSkip;
    private LinearLayout lyNext;
    private WelcomeAdapter welcomeAdapter;
    private int[] layouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.HideNavigation(this);
        setContentView(R.layout.activity_welcome);
        PrefManager.forceRTLIfSupported(getWindow(), this);

        Init();

        layouts = new int[]{R.layout.welcome_slide1, R.layout.welcome_slide2, R.layout.welcome_slide3, R.layout.welcome_slide4};
        welcomeAdapter = new WelcomeAdapter(WelcomeActivity.this, layouts);
        viewPager.setAdapter(welcomeAdapter);
        dotsIndicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void Init() {
        try {
            prefManager = new PrefManager(WelcomeActivity.this);

            viewPager = findViewById(R.id.viewPager);
            dotsIndicator = findViewById(R.id.dotsIndicator);

            lyNext = findViewById(R.id.lyNext);
            btnNext = findViewById(R.id.btnNext);
            lyNext.setOnClickListener(this);

            btnSkip = findViewById(R.id.btnSkip);
            btnSkip.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lyNext:
                int current = getItem(+1);

                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
                break;

            case R.id.btnSkip:
                launchHomeScreen();
                break;
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.get_started));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public static class WelcomeAdapter extends PagerAdapter {
        private int[] layouts;
        private Context context;

        public WelcomeAdapter(Context context, int[] layouts) {
            this.context = context;
            this.layouts = layouts;
            Log.e("layout size", "" + layouts.length);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
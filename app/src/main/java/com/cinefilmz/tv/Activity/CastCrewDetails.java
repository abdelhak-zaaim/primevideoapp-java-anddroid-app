package com.cinefilmz.tv.Activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinefilmz.tv.Model.CastDetailModel.CastDetailModel;
import com.cinefilmz.tv.Model.CastDetailModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CastCrewDetails extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    private PrefManager prefManager;
    private static final String TAG = CastCrewDetails.class.getSimpleName();

    private ShimmerFrameLayout shimmer;
    private AppBarLayout appBarLayout;
    private SwipeRefreshLayout swipeRefresh;
    private ImageView ivCastCrew;
    private LinearLayout lyInterestingDesc;
    private TextView txtPersonName, txtPersonDesc, txtMoreLess, txtSourceOfDetails, txtInterestingDesc;

    private List<Result> castDetailList;

    private String castID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_castcrewdetails);
        PrefManager.forceRTLIfSupported(getWindow(), CastCrewDetails.this);

        init();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            castID = "" + bundle.getString("itemID");
            Log.e("castID", "==>>> " + castID);
        }
        CastDetailData();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        CastDetailData();
                    }
                }, getResources().getInteger(R.integer.swipeRefreshDelay));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        Log.e("verticalOffset", "==>> " + verticalOffset);
        swipeRefresh.setEnabled(verticalOffset == 0);
    }

    private void init() {
        try {
            prefManager = new PrefManager(CastCrewDetails.this);

            shimmer = findViewById(R.id.shimmer);
            appBarLayout = findViewById(R.id.appBarLayout);
            swipeRefresh = findViewById(R.id.swipeRefresh);
            ivCastCrew = findViewById(R.id.ivCastCrew);

            lyInterestingDesc = findViewById(R.id.lyInterestingDesc);

            txtPersonName = findViewById(R.id.txtPersonName);
            txtPersonDesc = findViewById(R.id.txtPersonDesc);
            txtSourceOfDetails = findViewById(R.id.txtSourceOfDetails);
            txtInterestingDesc = findViewById(R.id.txtInterestingDesc);
            lyInterestingDesc = findViewById(R.id.lyInterestingDesc);
            txtMoreLess = findViewById(R.id.txtMoreLess);

            txtMoreLess.setOnClickListener(this);
        } catch (Exception e) {
            Log.e(TAG, "init Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtMoreLess:
                Log.e("lineCount", "" + txtPersonDesc.getLineCount());
                Log.e("maxLines", "" + txtPersonDesc.getMaxLines());
                if (txtMoreLess.getText().toString().equalsIgnoreCase("" + getResources().getString(R.string.more))) {
                    ObjectAnimator animation = ObjectAnimator.ofInt(txtPersonDesc, "maxLines", 50);
                    animation.setDuration(100).start();
                    txtMoreLess.setText("" + getResources().getString(R.string.less));
                } else {
                    ObjectAnimator animation = ObjectAnimator.ofInt(txtPersonDesc, "maxLines", 4);
                    animation.setDuration(100).start();
                    txtMoreLess.setText("" + getResources().getString(R.string.more));
                }
                break;
        }
    }

    /* cast_detail API */
    private void CastDetailData() {
        Utility.shimmerShow(shimmer);
        Call<CastDetailModel> call = BaseURL.getVideoAPI().cast_detail("" + castID);
        call.enqueue(new Callback<CastDetailModel>() {
            @Override
            public void onResponse(Call<CastDetailModel> call, Response<CastDetailModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    castDetailList = new ArrayList<>();

                    Log.e(TAG, "cast_detail Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult() != null) {
                            castDetailList = response.body().getResult();
                            Log.e("castDetailList", "size => " + castDetailList.size());

                            if (!TextUtils.isEmpty(castDetailList.get(0).getImage())) {
                                Picasso.get().load(castDetailList.get(0).getImage()).into(ivCastCrew);
                            }
                            txtPersonName.setText("" + castDetailList.get(0).getName());
                            if (!TextUtils.isEmpty(castDetailList.get(0).getPersonalInfo())) {
                                txtPersonDesc.setVisibility(View.VISIBLE);
                                txtPersonDesc.setText("" + Html.fromHtml(castDetailList.get(0).getPersonalInfo()));
                            } else {
                                txtPersonDesc.setVisibility(View.GONE);
                            }
                            lyInterestingDesc.setVisibility(View.GONE);

                        } else {
                            Log.e(TAG, "cast_detail Message => " + response.body().getMessage());
                        }

                    } else {
                        Log.e(TAG, "cast_detail Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "cast_detail Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<CastDetailModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e(TAG, "cast_detail onFailure => " + t.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.shimmerHide(shimmer);
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        Utility.shimmerHide(shimmer);
        super.onDestroy();
    }

}
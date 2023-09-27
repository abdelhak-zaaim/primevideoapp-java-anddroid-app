package com.cinefilmz.tv.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Adapter.AvatarAdapter;
import com.cinefilmz.tv.Interface.OnSingleItemClick;
import com.cinefilmz.tv.Model.AvatarModel.AvatarModel;
import com.cinefilmz.tv.Model.AvatarModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvatarForProfile extends AppCompatActivity implements View.OnClickListener, OnSingleItemClick {

    private PrefManager prefManager;
    private static final String TAG = AvatarForProfile.class.getSimpleName();

    private ShimmerFrameLayout shimmer;
    private LinearLayout lyToolbarTitle, lyNoData;
    private TextView txtToolbarTitle;
    private RecyclerView rvAllAvatar;

    private List<Result> avatarList;
    private AvatarAdapter avatarAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_for_profile);
        PrefManager.forceRTLIfSupported(getWindow(), this);

        init();
        txtToolbarTitle.setText("" + getResources().getString(R.string.change_profile_image));
    }

    private void init() {
        try {
            prefManager = new PrefManager(AvatarForProfile.this);

            shimmer = findViewById(R.id.shimmer);
            lyToolbarTitle = findViewById(R.id.lyToolbarTitle);
            lyToolbarTitle.setVisibility(View.VISIBLE);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            rvAllAvatar = findViewById(R.id.rvAllAvatar);
            lyNoData = findViewById(R.id.lyNoData);
        } catch (Exception e) {
            Log.e(TAG, "init Exception => " + e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetAvatar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private void GetAvatar() {
        Utility.shimmerShow(shimmer);

        Call<AvatarModel> call = BaseURL.getVideoAPI().get_avatar();
        call.enqueue(new Callback<AvatarModel>() {
            @Override
            public void onResponse(Call<AvatarModel> call, Response<AvatarModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    Log.e("get_avatar", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            avatarList = new ArrayList<>();
                            avatarList = response.body().getResult();
                            Log.e("avatarList", "" + avatarList.size());

                            avatarAdapter = new AvatarAdapter(AvatarForProfile.this, avatarList, "Avatar", AvatarForProfile.this);
                            rvAllAvatar.setLayoutManager(new GridLayoutManager(AvatarForProfile.this, 4));
                            rvAllAvatar.setAdapter(avatarAdapter);
                            avatarAdapter.notifyDataSetChanged();

                            rvAllAvatar.setVisibility(View.VISIBLE);
                            lyNoData.setVisibility(View.GONE);
                        } else {
                            rvAllAvatar.setVisibility(View.GONE);
                            lyNoData.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.e("get_avatar", "Message => " + response.body().getMessage());
                        rvAllAvatar.setVisibility(View.GONE);
                        lyNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e("get_avatar", "Exception => " + e);
                    rvAllAvatar.setVisibility(View.GONE);
                    lyNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<AvatarModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e("get_avatar", "onFailure => " + t.getMessage());
                rvAllAvatar.setVisibility(View.GONE);
                lyNoData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onSingleClick(int position, String itemId) {
        Log.e("onSingleClick", "position => " + position);
        Log.e("onSingleClick", "itemId => " + itemId);

        Constant.isSelectPic = false;
        Intent intent = new Intent(AvatarForProfile.this, EditProfile.class);
        intent.putExtra("Avatar", "" + avatarList.get(position).getImage());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "Called!");
        Utility.shimmerHide(shimmer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "Called!");
        Utility.shimmerHide(shimmer);
    }

}
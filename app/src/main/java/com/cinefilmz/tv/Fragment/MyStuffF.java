package com.cinefilmz.tv.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cinefilmz.tv.Activity.AboutPrivacyTerms;
import com.cinefilmz.tv.Activity.AppSettings;
import com.cinefilmz.tv.Activity.EditProfile;
import com.cinefilmz.tv.Adapter.AllProfileAdapter;
import com.cinefilmz.tv.Adapter.TabPagerAdapter;
import com.cinefilmz.tv.Model.ProfileModel.ProfileModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyStuffF extends Fragment implements View.OnClickListener {

    public MyStuffF() {
    }

    private PrefManager prefManager;
    private View root, viewDropDownUp;

    private AppBarLayout appBarLayout;
    private ShimmerFrameLayout shimmer;
    private TabLayout tabLayout;
    private ViewPager tabViewpager;
    private LinearLayout lyUserDropDown, lySettings;
    private RoundedImageView ivUser;
    private TextView txtUserName;
    private RecyclerView rvAllProfile;

    private AllProfileAdapter allProfileAdapter;

    private PopupWindow popupWindow = null;
    private boolean isDialogOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_mystuff, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());
        prefManager = new PrefManager(getActivity());

        init();
        setupViewPager(tabViewpager);
        tabLayout.setupWithViewPager(tabViewpager);

        return root;
    }

    private void init() {
        try {
            appBarLayout = root.findViewById(R.id.appBarLayout);
            shimmer = root.findViewById(R.id.shimmer);
            tabLayout = root.findViewById(R.id.tabLayout);
            tabViewpager = root.findViewById(R.id.tabViewpager);

            lySettings = root.findViewById(R.id.lySettings);
            lyUserDropDown = root.findViewById(R.id.lyUserDropDown);
            viewDropDownUp = root.findViewById(R.id.viewDropDownUp);
            ivUser = root.findViewById(R.id.ivUser);
            txtUserName = root.findViewById(R.id.txtUserName);

            lyUserDropDown.setOnClickListener(this);
            lySettings.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Utility.checkLoginUser(getActivity())) {
            GetProfile();
        }
    }

    /* get_profile API */
    private void GetProfile() {
        Call<ProfileModel> call = BaseURL.getVideoAPI().get_profile("" + prefManager.getLoginId());
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                try {
                    Log.e("get_profile", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult() != null) {
                            txtUserName.setText("" + response.body().getResult().get(0).getName());
                            if (!TextUtils.isEmpty(response.body().getResult().get(0).getImage())) {
                                Picasso.get().load(response.body().getResult().get(0).getImage()).placeholder(R.drawable.ic_user).into(ivUser);
                            }

                            Utility.storeUserCred(getActivity(),
                                    "" + response.body().getResult().get(0).getId(),
                                    "" + response.body().getResult().get(0).getEmail(),
                                    "" + response.body().getResult().get(0).getName(),
                                    "" + response.body().getResult().get(0).getMobile(),
                                    "" + response.body().getResult().get(0).getType(),
                                    "" + response.body().getResult().get(0).getImage());
                        }

                    } else {
                        Log.e("get_profile", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("get_profile", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                Log.e("get_profile", "onFailure => " + t.getMessage());
            }
        });
    }

    /* Set Downloads, Watchlist & Purchases */
    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter adapter = new TabPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DownloadsF(), "" + getResources().getString(R.string.downloads));
        adapter.addFragment(new WatchlistF(), "" + getResources().getString(R.string.watchlist));
        adapter.addFragment(new RentPurchasesF(), "" + getResources().getString(R.string.purchases));
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyUserDropDown:
                if (popupWindow != null) {
                    isDialogOpen = false;
                    popupWindow.dismiss();
                    popupWindow = null;
                } else {
                    openDropDown();
                }
                setDropDownUp();
                break;

            case R.id.lySettings:
                startActivity(new Intent(getActivity(), AppSettings.class));
                break;
        }
    }

    private void openDropDown() {
        isDialogOpen = true;
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_related_dialog, null);

        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popupWindow.setAnimationStyle(R.style.SheetDialog);
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 0);

        final LinearLayout lyCreateProfile, lyManageProfile, lyLearnMore;
        final RelativeLayout rlDialog;
        rlDialog = popupView.findViewById(R.id.rlDialog);
        lyCreateProfile = popupView.findViewById(R.id.lyCreateProfile);
        lyManageProfile = popupView.findViewById(R.id.lyManageProfile);
        lyLearnMore = popupView.findViewById(R.id.lyLearnMore);
        rvAllProfile = popupView.findViewById(R.id.rvAllProfile);
        rvAllProfile.setVisibility(View.GONE);
        //GetAllProfile();

//        lyCreateProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                isDialogOpen = false;
//                setDropDownUp();
//                popupWindow.dismiss();
//                popupWindow = null;
//
//                Constant.isSelectPic = false;
//                startActivity(new Intent(getActivity(), CreateProfile.class));
//            }
//        });

        lyManageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogOpen = false;
                setDropDownUp();
                popupWindow.dismiss();
                popupWindow = null;
                Constant.isSelectPic = false;
                startActivity(new Intent(getActivity(), EditProfile.class));
            }
        });

        lyLearnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogOpen = false;
                setDropDownUp();
                popupWindow.dismiss();
                popupWindow = null;

                Intent intent = new Intent(getActivity(), AboutPrivacyTerms.class);
                intent.putExtra("mainTitle", "" + getResources().getString(R.string.learn_more_about_profiles));
                intent.putExtra("loadUrl", "" + prefManager.getValue("website"));
                startActivity(intent);
            }
        });

        rlDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDialogOpen = false;
                setDropDownUp();
                popupWindow.dismiss();
                popupWindow = null;
            }
        });

    }

    private void GetAllProfile() {
        allProfileAdapter = new AllProfileAdapter(getActivity(), "MyStuff");
        rvAllProfile.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvAllProfile.setAdapter(allProfileAdapter);
        allProfileAdapter.notifyDataSetChanged();
    }

    private void setDropDownUp() {
        if (isDialogOpen) {
            viewDropDownUp.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_dropup));
            lySettings.setVisibility(View.INVISIBLE);
        } else {
            viewDropDownUp.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_dropdown));
            lySettings.setVisibility(View.VISIBLE);
        }
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
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

}
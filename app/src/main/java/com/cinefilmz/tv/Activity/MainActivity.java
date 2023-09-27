package com.cinefilmz.tv.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cinefilmz.tv.PushNotification.Config;
import com.cinefilmz.tv.PushNotification.NotificationUtils;
import com.cinefilmz.tv.Fragment.ChannelsF;
import com.cinefilmz.tv.Fragment.FindF;
import com.cinefilmz.tv.Fragment.HomeF;
import com.cinefilmz.tv.Fragment.MyStuffF;
import com.cinefilmz.tv.Fragment.RentProductsF;
import com.cinefilmz.tv.Model.ProfileModel.ProfileModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BottomNavigationView bottomNavigationView;
    public static LinearLayout lyInfoView;
    public static TextView txtInfoIcon, txtInfoView;

    private String backStateName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PrefManager.forceRTLIfSupported(getWindow(), this);
        prefManager = new PrefManager(this);

        init();
        PushInit();

        if (bottomNavigationView != null) {
            // Set action to perform when any menu-item is selected.
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    // Write code to perform some actions.
                    Log.e("item", "" + item);
                    selectFragment(item);
                    return false;
                }
            });
        }

        bottomNavigationView.getMenu().findItem(R.id.bottomHome).setVisible(true);
        bottomNavigationView.getMenu().findItem(R.id.bottomFind).setVisible(true);
        bottomNavigationView.getMenu().findItem(R.id.bottomRent).setVisible(true);
        bottomNavigationView.getMenu().findItem(R.id.bottomChannels).setVisible(true);
        bottomNavigationView.getMenu().findItem(R.id.bottomMyStuff).setVisible(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Log.e("pushFragment", "==>>> " + bundle.getString("pushFragment"));
            if (bundle.getString("pushFragment").equalsIgnoreCase("MyStuff")) {
                pushFragment(new MyStuffF());
                bottomNavigationView.setSelectedItemId(R.id.bottomMyStuff);
            } else {
                pushFragment(new HomeF());
                bottomNavigationView.setSelectedItemId(R.id.bottomHome);
                //pushFragment(new KidsSectionF());
            }
        } else {
            pushFragment(new HomeF());
            //pushFragment(new KidsSectionF());
        }

        /* Welcome User */
        if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
            GetProfile();
        }

    }

    private void init() {
        try {
            bottomNavigationView = findViewById(R.id.navigation);
            bottomNavigationView.setItemIconTintList(null);

            lyInfoView = findViewById(R.id.lyInfoView);
            txtInfoIcon = findViewById(R.id.txtInfoIcon);
            txtInfoView = findViewById(R.id.txtInfoView);
        } catch (Exception e) {
            Log.e(TAG, "Exception => " + e);
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
                            Log.e("profileData", "" + response.body().getResult().get(0).getId());

                            Utility.showSnackBar(MainActivity.this, "Welcome", "" + response.body().getResult().get(0).getName());

                            Utility.storeUserCred(MainActivity.this,
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_search:
                // search action
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void selectFragment(MenuItem item) {
        item.setChecked(true);
        switch (item.getItemId()) {
            case R.id.bottomHome:
                pushFragment(new HomeF());
//                pushFragment(new KidsSectionF());
                break;

            case R.id.bottomFind:
                pushFragment(new FindF());
                break;

            case R.id.bottomChannels:
                pushFragment(new ChannelsF());
                break;

            case R.id.bottomRent:
                if (Utility.checkLoginUser(MainActivity.this)) {
                    pushFragment(new RentProductsF());
                }
                break;

            case R.id.bottomMyStuff:
                if (Utility.checkLoginUser(MainActivity.this)) {
                    pushFragment(new MyStuffF());
                }
                break;
        }
    }

    protected void pushFragment(Fragment fragment) {
        if (fragment == null)
            return;

        backStateName = fragment.getClass().getSimpleName();
        Log.e("backStateName =>", "" + backStateName);
        String fragmentTag = backStateName;

        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.rootLayout, fragment, fragmentTag);
            ft.setReorderingAllowed(true);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    public void PushInit() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Objects.equals(intent.getAction(), Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (Objects.equals(intent.getAction(), Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.e("message ==>", "" + message);
                    //Toasty.info(getApplicationContext(), "Push notification : " + message, Toasty.LENGTH_LONG).show();
                }
            }
        };
        displayFirebaseRegId();
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        Log.e(TAG, "Firebase reg id : " + regId);
        if (!TextUtils.isEmpty(regId)) {
            Log.e(TAG, "Firebase reg id : " + regId);
        } else {
            Log.e(TAG, "Firebase Reg Id is not received yet!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        Log.e(TAG, "backStateName :=> " + backStateName);
        if (bottomNavigationView.getSelectedItemId() == R.id.bottomHome) {
            Utility.doubleButtonAlertDialog(MainActivity.this, "" + getResources().getString(R.string.do_you_want_to_exit), false, true);
        } else {
            Log.e("Home navigation", "");
            bottomNavigationView.setSelectedItemId(R.id.bottomHome);
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
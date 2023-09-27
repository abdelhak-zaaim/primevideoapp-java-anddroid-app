package com.cinefilmz.tv.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.BuildConfig;
import com.cinefilmz.tv.Model.PagesModel.PagesModel;
import com.cinefilmz.tv.Model.ProfileModel.ProfileModel;
import com.cinefilmz.tv.Model.SuccessModel.SuccessModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.LocaleUtils;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.onesignal.OneSignal;

import java.io.File;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppSettings extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;

    private LinearLayout lyAccountDetails, lySubscription, lyChangePassword, lyCPassType1, lyCPassType2, lyCurrentVisible, lyNewVisible,
            lyConfirmVisible, lyChangePassBtn, lyChangeLanguage, lyPushNotifications, lyClearCache, lyClearSearchHistory,
            lySignedInAs, lyAboutUs, lyPrivacyPolicy, lyTermsConditions, lyRateUs, lyShareApp, lyRefundPolicy, lyDeleteAccount;
    private ImageView ivCurrentVisible, ivNewVisible, ivConfirmVisible;
    private TextView txtCPassUp, txtCPassDown, txtCurrentLanguage, txtSignedUserName, txtSignInOut, txtAppVersion;
    private EditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private SwitchCompat switchPushNoti;

    private String strCurrentPass = "", strNewPass = "", strConfirmPass = "", strPrivacyPolicy = "", strTermsCondition = "",
            strRefundPolicy = "", strAboutUs = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);
        PrefManager.forceRTLIfSupported(getWindow(), AppSettings.this);

        init();
        txtAppVersion.setText(getResources().getString(R.string.version) + " " + BuildConfig.VERSION_NAME);
        txtCurrentLanguage.setText("" + Utility.getAppLanguage("" + LocaleUtils.getSelectedLanguageId()));

        if (prefManager.getBool("PUSH")) {
            switchPushNoti.setChecked(true);
        } else {
            switchPushNoti.setChecked(false);
        }

        switchPushNoti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    OneSignal.disablePush(false);
                } else {
                    OneSignal.disablePush(true);
                }
                prefManager.setBool("PUSH", isChecked);
            }
        });
    }

    private void init() {
        try {
            prefManager = new PrefManager(AppSettings.this);

            lyAccountDetails = findViewById(R.id.lyAccountDetails);
            lySubscription = findViewById(R.id.lySubscription);
            lyChangePassword = findViewById(R.id.lyChangePassword);
            lyCPassType1 = findViewById(R.id.lyCPassType1);
            lyCPassType2 = findViewById(R.id.lyCPassType2);
            lyCurrentVisible = findViewById(R.id.lyCurrentVisible);
            lyNewVisible = findViewById(R.id.lyNewVisible);
            lyConfirmVisible = findViewById(R.id.lyConfirmVisible);
            lyChangePassBtn = findViewById(R.id.lyChangePassBtn);
            lyChangeLanguage = findViewById(R.id.lyChangeLanguage);
            lyPushNotifications = findViewById(R.id.lyPushNotifications);
            lyClearCache = findViewById(R.id.lyClearCache);
            lyClearSearchHistory = findViewById(R.id.lyClearSearchHistory);
            lySignedInAs = findViewById(R.id.lySignedInAs);
            lyAboutUs = findViewById(R.id.lyAboutUs);
            lyPrivacyPolicy = findViewById(R.id.lyPrivacyPolicy);
            lyTermsConditions = findViewById(R.id.lyTermsConditions);
            lyRateUs = findViewById(R.id.lyRateUs);
            lyShareApp = findViewById(R.id.lyShareApp);
            lyRefundPolicy = findViewById(R.id.lyRefundPolicy);
            lyDeleteAccount = findViewById(R.id.lyDeleteAccount);

            switchPushNoti = findViewById(R.id.switchPushNoti);

            ivCurrentVisible = findViewById(R.id.ivCurrentVisible);
            ivNewVisible = findViewById(R.id.ivNewVisible);
            ivConfirmVisible = findViewById(R.id.ivConfirmVisible);

            txtCPassUp = findViewById(R.id.txtCPassUp);
            txtCPassDown = findViewById(R.id.txtCPassDown);
            txtCurrentLanguage = findViewById(R.id.txtCurrentLanguage);
            txtSignedUserName = findViewById(R.id.txtSignedUserName);
            txtSignInOut = findViewById(R.id.txtSignInOut);
            txtAppVersion = findViewById(R.id.txtAppVersion);

            etCurrentPassword = findViewById(R.id.etCurrentPassword);
            etNewPassword = findViewById(R.id.etNewPassword);
            etConfirmPassword = findViewById(R.id.etConfirmPassword);

            lyAccountDetails.setOnClickListener(this);
            lySubscription.setOnClickListener(this);
            lyChangePassword.setOnClickListener(this);
            lyCurrentVisible.setOnClickListener(this);
            lyNewVisible.setOnClickListener(this);
            lyConfirmVisible.setOnClickListener(this);
            lyChangePassBtn.setOnClickListener(this);
            lyChangeLanguage.setOnClickListener(this);
            lyPushNotifications.setOnClickListener(this);
            lyClearCache.setOnClickListener(this);
            lyClearSearchHistory.setOnClickListener(this);
            lySignedInAs.setOnClickListener(this);
            lyAboutUs.setOnClickListener(this);
            lyPrivacyPolicy.setOnClickListener(this);
            lyTermsConditions.setOnClickListener(this);
            lyRateUs.setOnClickListener(this);
            lyShareApp.setOnClickListener(this);
            lyRefundPolicy.setOnClickListener(this);
            lyDeleteAccount.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GetPages();
        if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
            lyDeleteAccount.setVisibility(View.VISIBLE);
            GetProfile();
        } else {
            lyDeleteAccount.setVisibility(View.GONE);
            txtSignedUserName.setText("" + getResources().getString(R.string.not_sign_in_desc));
            txtSignInOut.setText("" + getResources().getString(R.string.sign_in));
        }
    }

    private void GetProfile() {
        Call<ProfileModel> call = BaseURL.getVideoAPI().get_profile("" + prefManager.getLoginId());
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                try {
                    Log.e("get_profile", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult() != null) {
                            if (response.body().getResult().get(0).getType() == 3) {
                                txtSignedUserName.setText(getResources().getString(R.string.signed_in_as) + " " + response.body().getResult().get(0).getMobile());
                            } else {
                                txtSignedUserName.setText(getResources().getString(R.string.signed_in_as) + " " + response.body().getResult().get(0).getName());
                            }
                            txtSignInOut.setText("" + getResources().getString(R.string.sign_out));

                            Utility.storeUserCred(AppSettings.this,
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

    private void GetPages() {
        Call<PagesModel> call = BaseURL.getVideoAPI().get_pages();
        call.enqueue(new Callback<PagesModel>() {
            @Override
            public void onResponse(Call<PagesModel> call, Response<PagesModel> response) {
                try {
                    Log.e("get_pages", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult() != null) {
                            for (int i = 0; i < response.body().getResult().size(); i++) {
                                if (response.body().getResult().get(i).getPageName() == "about-us") {
                                    strAboutUs = "" + response.body().getResult().get(i).getPageName();
                                }
                                if (response.body().getResult().get(i).getPageName() == "privacy-policy") {
                                    strPrivacyPolicy = "" + response.body().getResult().get(i).getPageName();
                                }
                                if (response.body().getResult().get(i).getPageName() == "terms-and-conditions") {
                                    strTermsCondition = "" + response.body().getResult().get(i).getPageName();
                                }
                                if (response.body().getResult().get(i).getPageName() == "refund-policy") {
                                    strRefundPolicy = "" + response.body().getResult().get(i).getPageName();
                                }
                            }
                            Log.e("strAboutUs", "=====> " + strAboutUs);
                            Log.e("strPrivacyPolicy", "=====> " + strPrivacyPolicy);
                            Log.e("strTermsCondition", "=====> " + strTermsCondition);
                            Log.e("strRefundPolicy", "=====> " + strRefundPolicy);
                        }

                    } else {
                        Log.e("get_pages", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("get_pages", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<PagesModel> call, Throwable t) {
                Log.e("get_pages", "onFailure => " + t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyAccountDetails:
                if (Utility.checkLoginUser(AppSettings.this)) {
                    Constant.isSelectPic = false;
                    startActivity(new Intent(AppSettings.this, EditProfile.class));
                }
                break;

            case R.id.lySubscription:
                if (Utility.checkLoginUser(AppSettings.this)) {
                    startActivity(new Intent(AppSettings.this, Subscription.class));
                }
                break;

            case R.id.lyChangePassword:
                if (Utility.checkLoginUser(AppSettings.this)) {
                    if (txtCPassDown.getVisibility() == View.VISIBLE) {
                        ObjectAnimator animation;
//                        if (profileList.get(0).getType().equalsIgnoreCase("2")) {
//                            lyCPassType1.setVisibility(View.GONE);
//                            animation = ObjectAnimator.ofArgb(lyCPassType2, "visibility", View.VISIBLE);
//                        } else {
                        lyCPassType2.setVisibility(View.GONE);
                        animation = ObjectAnimator.ofArgb(lyCPassType1, "visibility", View.VISIBLE);
//                        }
                        animation.setDuration(200).start();

                        txtCPassDown.setVisibility(View.GONE);
                        txtCPassUp.setVisibility(View.VISIBLE);

                    } else {
                        ObjectAnimator animation;
//                        if (profileList.get(0).getType().equalsIgnoreCase("2")) {
//                            lyCPassType1.setVisibility(View.GONE);
//                            animation = ObjectAnimator.ofArgb(lyCPassType2, "visibility", View.GONE);
//                        } else {
                        lyCPassType2.setVisibility(View.GONE);
                        animation = ObjectAnimator.ofArgb(lyCPassType1, "visibility", View.GONE);
//                        }
                        animation.setDuration(200).start();

                        txtCPassDown.setVisibility(View.VISIBLE);
                        txtCPassUp.setVisibility(View.GONE);
                    }
                }
                break;

            case R.id.lyCurrentVisible:
                if (TextUtils.isEmpty(etCurrentPassword.getText().toString())) {
                    Toasty.warning(AppSettings.this, "" + getResources().getString(R.string.enter_current_password), Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (etCurrentPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    ivCurrentVisible.setImageResource(R.drawable.ic_pass_invisible);
                    etCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    ivCurrentVisible.setImageResource(R.drawable.ic_pass_visible);
                    etCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;

            case R.id.lyNewVisible:
                if (TextUtils.isEmpty(etNewPassword.getText().toString())) {
                    Toasty.warning(AppSettings.this, "" + getResources().getString(R.string.enter_new_password), Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (etNewPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    ivNewVisible.setImageResource(R.drawable.ic_pass_invisible);
                    etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    ivNewVisible.setImageResource(R.drawable.ic_pass_visible);
                    etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;

            case R.id.lyConfirmVisible:
                if (TextUtils.isEmpty(etConfirmPassword.getText().toString())) {
                    Toasty.warning(AppSettings.this, "" + getResources().getString(R.string.enter_confirm_password), Toasty.LENGTH_SHORT).show();
                    return;
                }

                if (etConfirmPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                    ivConfirmVisible.setImageResource(R.drawable.ic_pass_invisible);
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    ivConfirmVisible.setImageResource(R.drawable.ic_pass_visible);
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;

            case R.id.lyChangePassBtn:
                ValidateAndChangePassword();
                break;

            case R.id.lyChangeLanguage:
                if (Utility.checkLoginUser(AppSettings.this)) {
                    startActivity(new Intent(AppSettings.this, AppLanguage.class));
                }
                break;

            case R.id.lyPushNotifications:
                if (switchPushNoti.isChecked()) {
                    switchPushNoti.setChecked(false);
                } else {
                    switchPushNoti.setChecked(true);
                }
                break;

            case R.id.lyClearCache:
                ClearCache();
                break;

            case R.id.lyClearSearchHistory:
                break;

            case R.id.lySignedInAs:
                if (Utility.checkLoginUser(AppSettings.this)) {
                    Utility.logoutConfirmDialog(AppSettings.this);
                }
                break;

            case R.id.lyDeleteAccount:
                Utility.deleteConfirmDialog(AppSettings.this);
                break;

            case R.id.lyRateUs:
                RateApp();
                break;

            case R.id.lyShareApp:
                ShareApp();
                break;

            case R.id.lyAboutUs:
                Intent intent = new Intent(AppSettings.this, AboutPrivacyTerms.class);
                intent.putExtra("mainTitle", "" + getResources().getString(R.string.about_us));
                intent.putExtra("loadUrl", "" + strAboutUs);
                startActivity(intent);
                break;

            case R.id.lyPrivacyPolicy:
                Intent intent1 = new Intent(AppSettings.this, AboutPrivacyTerms.class);
                intent1.putExtra("mainTitle", "" + getResources().getString(R.string.privacy_policy));
                intent1.putExtra("loadUrl", "" + strPrivacyPolicy);
                startActivity(intent1);
                break;

            case R.id.lyTermsConditions:
                Intent intent2 = new Intent(AppSettings.this, AboutPrivacyTerms.class);
                intent2.putExtra("mainTitle", "" + getResources().getString(R.string.terms_and_conditions));
                intent2.putExtra("loadUrl", "" + strTermsCondition);
                startActivity(intent2);
                break;

            case R.id.lyRefundPolicy:
                Intent intent3 = new Intent(AppSettings.this, AboutPrivacyTerms.class);
                intent3.putExtra("mainTitle", "" + getResources().getString(R.string.refund_policy));
                intent3.putExtra("loadUrl", "" + strRefundPolicy);
                startActivity(intent3);
                break;
        }
    }

    private void ValidateAndChangePassword() {
        strCurrentPass = "" + etCurrentPassword.getText().toString().trim();
        strNewPass = "" + etNewPassword.getText().toString().trim();
        strConfirmPass = "" + etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(strCurrentPass)) {
            Toasty.warning(AppSettings.this, "" + getResources().getString(R.string.enter_current_password), Toasty.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(strNewPass)) {
            Toasty.warning(AppSettings.this, "" + getResources().getString(R.string.enter_new_password), Toasty.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(strConfirmPass)) {
            Toasty.warning(AppSettings.this, "" + getResources().getString(R.string.enter_confirm_password), Toasty.LENGTH_SHORT).show();
            return;
        }
        if (!strNewPass.equalsIgnoreCase(strConfirmPass)) {
            Toasty.error(AppSettings.this, "" + getResources().getString(R.string.password_not_matched_toast),
                    Toast.LENGTH_LONG).show();
            return;
        }

        //ChangePassword();
    }

    private void ChangePassword() {
        Utility.ProgressBarShow(AppSettings.this);

        Call<SuccessModel> call = BaseURL.getVideoAPI().change_password("" + prefManager.getLoginId(),
                "" + strConfirmPass);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                Utility.ProgressbarHide();
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Utility.exitAlertDialog(AppSettings.this, "" + response.body().getMessage(), true, false);
                    } else {
                        Utility.exitAlertDialog(AppSettings.this, "" + response.body().getMessage(), false, false);
                    }
                } catch (Exception e) {
                    Log.e("change_password", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("change_password", "onFailure => " + t.getMessage());
                Utility.ProgressbarHide();
                Utility.exitAlertDialog(AppSettings.this, "" + t.getMessage(), false, false);
            }
        });
    }

    private void ClearCache() {
        File file = new File(Functions.getCacheFolder(AppSettings.this));
        Log.e("cache File =>", "" + file.getPath());
        if (file.isDirectory()) {
            String[] children = file.list();
            for (String aChildren : children) {
                new File(file, aChildren).delete();
            }
            Toasty.success(AppSettings.this, getResources().getString(R.string.clear_cached_data_toast), Toasty.LENGTH_SHORT).show();
        }
    }

    private void RateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AppSettings.this.getPackageName())));
        } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + AppSettings.this.getPackageName())));
        }
    }

    private void ShareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + getResources().getString(R.string.app_name));
            String shareMessage = "\nLet me recommend you this application\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + AppSettings.this.getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
            Log.e("shareApp", "Exception => " + e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.ProgressbarHide();
        Log.e("onPause", "called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.ProgressbarHide();
        Log.e("onDestroy", "called");
    }

}
package com.cinefilmz.tv.OTPLogin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.cinefilmz.tv.Activity.MainActivity;
import com.cinefilmz.tv.Model.LoginRegiModel.LoginRegiModel;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Model.ProfileModel.ProfileModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.LocaleUtils;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.gne.www.lib.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import cn.iwgang.countdownview.CountdownView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPVerification extends AppCompatActivity implements CountdownView.OnCountdownEndListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    private PrefManager prefManager;

    private LinearLayout lyResendOTP, lyBack, lyConfirm;
    private TextView txtPhoneNumber;
    private CountdownView countdownTimer;
    private PinView otpView;

    private String entryFrom = "", strMobile, strEmail = "", strUserName = "", strDeviceToken = "", verificationId, strOTP = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private long countTime = 90L /*seconds*/, timerCount = 90000L /*milliseconds*/; //set same value

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.HideNavigation(OTPVerification.this);
        setContentView(R.layout.activity_otp_verification);
        PrefManager.forceRTLIfSupported(getWindow(), this);

        // below line is for getting instance of our FirebaseAuth.
        mAuth = FirebaseAuth.getInstance();
        // Turn off phone auth app verification.
        mAuth.setLanguageCode(LocaleUtils.getSelectedLanguageId());

        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            entryFrom = bundle.getString("entryFrom");
            strMobile = bundle.getString("mobile");
            if (!TextUtils.isEmpty(bundle.getString("emailAddress"))) {
                strEmail = bundle.getString("emailAddress");
            } else {
                strEmail = "" + prefManager.getValue("Email");
            }
            if (!TextUtils.isEmpty(bundle.getString("userName"))) {
                strUserName = bundle.getString("userName");
            } else {
                strUserName = "" + prefManager.getValue("Username");
            }
            Log.e("entryFrom", "" + entryFrom);
            Log.e("mobile", "" + strMobile);
            Log.e("emailAddress", "" + strEmail);
            Log.e("userName", "" + strUserName);

            txtPhoneNumber.setText("" + strMobile);

            sendVerificationCode(strMobile);
            lyResendOTP.setVisibility(View.VISIBLE);
            countdownTimer.setVisibility(View.VISIBLE);
            countdownTimer.start(timerCount);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        strDeviceToken = Utility.getToken(OTPVerification.this);
        Log.e("Devicetoken =>", "" + strDeviceToken);
    }

    private void init() {
        try {
            prefManager = new PrefManager(OTPVerification.this);

            otpView = findViewById(R.id.otpView);

            countdownTimer = findViewById(R.id.countdownTimer);
            countdownTimer.setOnCountdownEndListener(this);

            txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
            lyBack = findViewById(R.id.lyBack);
            lyResendOTP = findViewById(R.id.lyResendOTP);
            lyConfirm = findViewById(R.id.lyConfirm);

            lyBack.setOnClickListener(this);
            lyResendOTP.setOnClickListener(null);
            lyConfirm.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyConfirm:
                strOTP = "" + otpView.getText().trim();
                Log.e("strOTP", "======> " + strOTP);
                if (TextUtils.isEmpty(strOTP)) {
                    Toasty.warning(OTPVerification.this, "" + getResources().getString(R.string.enter_otp), Toasty.LENGTH_SHORT).show();
                    return;
                }
                Functions.hideSoftKeyboard(OTPVerification.this);
                Utility.ProgressBarShow(OTPVerification.this);
                lyResendOTP.setOnClickListener(null);
                verifyCode(strOTP);
                break;

            case R.id.lyResendOTP:
                Log.e("countdownTimer", "" + countdownTimer.getRemainTime());
                countdownTimer.start(timerCount);
                resendVerificationCode(strMobile, mResendToken);
                break;

            case R.id.lyBack:
                finish();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.ProgressbarHide();
        countdownTimer.pause();
        Log.e("onPause", "remainTime => " + countdownTimer.getRemainTime());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "remainTime => " + countdownTimer.getRemainTime());
        if (countdownTimer.getRemainTime() > 0) {
            countdownTimer.restart();
        }
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        Log.e("signInWithCredential", "SmsCode => " + credential.getSmsCode());
        Log.e("signInWithCredential", "Provider => " + credential.getProvider());
        Log.e("signInWithCredential", "SignInMethod => " + credential.getSignInMethod());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.e("AuthResult", "Credential => " + task.getResult().getCredential());
                            Log.e("AuthResult", "User => " + task.getResult().getUser());
                            Log.e("AuthResult", "AdditionalUserInfo => " + task.getResult().getAdditionalUserInfo());

                            if (entryFrom.equalsIgnoreCase("Payment")) {
                                UpdateMissingProfile();
                            } else {
                                SignInMobile();
                            }
                        } else {
                            Toasty.error(OTPVerification.this, "" + task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                            Utility.ProgressbarHide();
                        }
                    }
                });
    }

    private void verifyCode(String code) {
        Log.e("=> verifyCode", "" + code);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void sendVerificationCode(String number) {
        Utility.ProgressBarShow(OTPVerification.this);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(number)       // Phone number to verify
                .setTimeout(countTime, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Utility.ProgressbarHide();
    }

    @Override
    public void onEnd(CountdownView cv) {
        countdownTimer.stop();
        countdownTimer.allShowZero();
        lyResendOTP.setVisibility(View.VISIBLE);
        lyResendOTP.setOnClickListener(this);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        lyResendOTP.setOnClickListener(null);
        Utility.ProgressBarShow(OTPVerification.this);
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)       // Phone number to verify
                .setTimeout(countTime, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(this)                 // (optional) Activity for callback binding
                // If no activity is passed, reCAPTCHA verification can not be used.
                .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        Utility.ProgressbarHide();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            mResendToken = forceResendingToken;
            Log.e("verificationId", "" + verificationId);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            final String code = phoneAuthCredential.getSmsCode();
            Log.e("SmsCode", "" + code);
            if (code != null) {
                otpView.setText("" + code);
                Utility.ProgressBarShow(OTPVerification.this);
                verifyCode(code);
                lyResendOTP.setOnClickListener(null);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.e("VerificationFailed", "" + e);
            Toasty.error(OTPVerification.this, "" + e.getMessage(), Toasty.LENGTH_LONG).show();
            lyResendOTP.setVisibility(View.VISIBLE);
            lyResendOTP.setOnClickListener(OTPVerification.this::onClick);
            Utility.ProgressbarHide();
            OTPVerification.this.finish();
        }
    };

    /* login API */
    private void SignInMobile() {
        if (!((Activity) OTPVerification.this).isFinishing()) {
            Utility.ProgressBarShow(OTPVerification.this);
        }

        Call<LoginRegiModel> call = BaseURL.getVideoAPI().loginWithOTP("" + Constant.typeOTP, "" + strMobile);
        call.enqueue(new Callback<LoginRegiModel>() {
            @Override
            public void onResponse(Call<LoginRegiModel> call, Response<LoginRegiModel> response) {
                Utility.ProgressbarHide();
                try {
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("loginwithotp", "Message => " + response.body().getMessage());

                        prefManager.setFirstTimeLaunch(false);
                        Utility.storeUserCred(OTPVerification.this,
                                "" + response.body().getResult().get(0).getId(),
                                "" + response.body().getResult().get(0).getEmail(),
                                "" + response.body().getResult().get(0).getName(),
                                "" + response.body().getResult().get(0).getMobile(),
                                "" + response.body().getResult().get(0).getType(),
                                "" + response.body().getResult().get(0).getImage());

                        Intent intent = new Intent(OTPVerification.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    } else {
                        Utility.exitAlertDialog(OTPVerification.this, "" + response.body().getMessage(), false, true);
                    }

                } catch (Exception e) {
                    Log.e("loginwithotp", "Exception => " + e);
                    lyResendOTP.setVisibility(View.VISIBLE);
                    lyResendOTP.setOnClickListener(OTPVerification.this);
                }
            }

            @Override
            public void onFailure(Call<LoginRegiModel> call, Throwable t) {
                Utility.ProgressbarHide();
                Utility.exitAlertDialog(OTPVerification.this, "" + t.getMessage(), false, true);
                Log.e("loginwithotp", "onFailure => " + t.getMessage());
            }
        });
    }

    /* updateprofile API */
    private void UpdateMissingProfile() {
        if (!((Activity) OTPVerification.this).isFinishing()) {
            Utility.ProgressBarShow(OTPVerification.this);
        }
        Log.e("TAG", "UserID ===============> " + prefManager.getLoginId());
        Log.e("TAG", "UserName ===============> " + strUserName);
        Log.e("TAG", "Email ===============> " + strEmail);
        Log.e("TAG", "userMobile ===============> " + strMobile);
        Call<ProfileModel> call = BaseURL.getVideoAPI().updateMissingData("" + prefManager.getLoginId(), "" + strUserName,
                "" + strMobile, "" + strEmail);
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                Utility.ProgressbarHide();
                try {
                    Log.e("updateMobileEmail", "status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("updateMobile", "strEmail => " + strEmail);
                        Log.e("updateMobile", "strMobile => " + strMobile);

                        Utility.storeUserCred(OTPVerification.this,
                                "" + response.body().getResult().get(0).getId(),
                                "" + response.body().getResult().get(0).getEmail(),
                                "" + response.body().getResult().get(0).getName(),
                                "" + response.body().getResult().get(0).getMobile(),
                                "" + response.body().getResult().get(0).getType(),
                                "" + response.body().getResult().get(0).getImage());

                        Toasty.success(OTPVerification.this, "" + getResources().getString(R.string.verification_success), Toasty.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Utility.exitAlertDialog(OTPVerification.this, "" + response.body().getMessage(), false, true);
                    }
                } catch (Exception e) {
                    Log.e("updateMobileEmail", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                Log.e("updateMobileEmail", "onFailure => " + t.getMessage());
                Utility.ProgressbarHide();
                Utility.exitAlertDialog(OTPVerification.this, "" + t.getMessage(), false, true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.ProgressbarHide();
        countdownTimer.stop();
        countdownTimer.allShowZero();
    }

}
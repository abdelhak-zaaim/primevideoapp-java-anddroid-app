package com.cinefilmz.tv.Activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cinefilmz.tv.Model.LoginRegiModel.LoginRegiModel;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Utils.PermissionUtils;
import com.cinefilmz.tv.Model.SuccessModel.SuccessModel;
import com.cinefilmz.tv.OTPLogin.OTPVerification;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.hbb20.CountryCodePicker;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private PermissionUtils takePermissionUtils;

    private LinearLayout lyWithMobile, lyFacebook, lyGmail;
    private TextView txtUserAgreement;
    private EditText etMobileNumber;
    private CountryCodePicker countryCodePicker;

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;

    private static final String EMAIL = "email";
    private static final String PROFILE = "public_profile";

    private String strFirstname = "", strEmail = "", strMobileNumber = "", strCoutryCode = "", strType = "", strDeviceToken = "",
            fbName = "", fbEmail = "", storeImageName = "", loginWith = "";
    private MultipartBody.Part body;
    private File imageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.HideNavigation(LoginActivity.this);
        setContentView(R.layout.activity_login);
        PrefManager.forceRTLIfSupported(getWindow(), this);
        takePermissionUtils = new PermissionUtils(this, mPermissionResult);
        prefManager = new PrefManager(this);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        callbackManager = CallbackManager.Factory.create();

        init();
        setHTMLTexts();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e("loginResult1", "Token::" + loginResult.getAccessToken());
                Log.e("loginResult", "" + loginResult.getAccessToken().getToken());
                AccessToken accessToken = loginResult.getAccessToken();
                Log.e("loginResult3", "" + accessToken);
                useLoginInformation(accessToken);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                Log.e("exception", "" + exception.getMessage());
            }
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void onStart() {
        super.onStart();
        strDeviceToken = Utility.getToken(LoginActivity.this);
        Log.e("Devicetoken =>", "" + strDeviceToken);
    }

    private void init() {
        try {
            prefManager = new PrefManager(this);

            lyWithMobile = findViewById(R.id.lyWithMobile);
            lyGmail = findViewById(R.id.lyGmail);
            lyFacebook = findViewById(R.id.lyFacebook);
            txtUserAgreement = findViewById(R.id.txtUserAgreement);

            etMobileNumber = findViewById(R.id.etMobileNumber);
            countryCodePicker = findViewById(R.id.countryCodePicker);
            countryCodePicker.setCountryForNameCode("IN");

            lyWithMobile.setOnClickListener(this);
            lyGmail.setOnClickListener(this);
            lyFacebook.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void setHTMLTexts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txtUserAgreement.setText(Html.fromHtml("<p>By continuing , I understand and agree with <a href="
                    + prefManager.getValue("privacy-policy") + ">Privacy Policy</a> and <a href="
                    + prefManager.getValue("terms-and-conditions") + ">Terms and Conditions</a> of " + getResources().getString(R.string.app_name) + ".</p>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtUserAgreement.setText(Html.fromHtml("<p>By continuing , I understand and agree with <a href="
                    + prefManager.getValue("privacy-policy") + ">Privacy Policy</a> and <a href="
                    + prefManager.getValue("terms-and-conditions") + ">Terms and Conditions</a> of " + getResources().getString(R.string.app_name) + ".</p>"));
        }
        txtUserAgreement.setClickable(true);
        txtUserAgreement.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyWithMobile:
                ValidateAndLogin();
                break;

            case R.id.lyGmail:
                loginWith = "Google";
                Log.e("gMail", "loginWith => " + loginWith);
                if (takePermissionUtils.isStoragePermissionGranted()||true) {
                    LoginManager.getInstance().logOut();
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, 101);
                } else {
                    takePermissionUtils.showStoragePermissionDailog("" + getResources().getString(R.string.we_need_storage_permission_for_save_video));
                }
                break;

            case R.id.lyFacebook:
                loginWith = "Facebook";
                Log.e("fb", "loginWith => " + loginWith);
                if (takePermissionUtils.isStoragePermissionGranted()) {
                    mGoogleSignInClient.signOut();
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(EMAIL, PROFILE));
                } else {
                    takePermissionUtils.showStoragePermissionDailog("" + getResources().getString(R.string.we_need_storage_permission_for_save_video));
                }
                break;
        }
    }

    private void ValidateAndLogin() {
        strMobileNumber = "" + etMobileNumber.getText().toString().trim();
        strCoutryCode = "" + countryCodePicker.getSelectedCountryCode();
        Log.e("coutryCode", "" + strCoutryCode);

        if (TextUtils.isEmpty(strMobileNumber)) {
            Toasty.warning(LoginActivity.this, "" + getResources().getString(R.string.enter_your_mobile_number), Toasty.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(strCoutryCode)) {
            Toasty.warning(LoginActivity.this, "" + getResources().getString(R.string.select_your_coutry_code), Toasty.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(LoginActivity.this, OTPVerification.class);
        intent.putExtra("entryFrom", "Login");
        intent.putExtra("mobile", "+" + strCoutryCode + strMobileNumber);
        startActivity(intent);
    }

    private final ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    boolean allPermissionClear = true;
                    List<String> blockPermissionCheck = new ArrayList<>();
                    for (String key : result.keySet()) {
                        if (!(result.get(key))) {
                            allPermissionClear = false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(LoginActivity.this, key));
                        }
                    }
                    Log.e("blockPermissionCheck", "" + blockPermissionCheck);
                    Log.e("allPermissionClear", "" + allPermissionClear);
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(LoginActivity.this, "" + getResources().getString(R.string.we_need_storage_permission_for_save_video));
                    } else if (allPermissionClear) {
                        if (loginWith.equalsIgnoreCase("Google")) {
                            LoginManager.getInstance().logOut();
                            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                            startActivityForResult(signInIntent, 101);
                        } else {
                            mGoogleSignInClient.signOut();
                            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(EMAIL, PROFILE));
                        }
                    }
                }
            });

    private void useLoginInformation(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            //OnCompleted is invoked once the GraphRequest is successful
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                if (object != null) {

                    Log.e("object", "==> " + object.toString());
                    String f_name = object.optString("first_name");
                    String l_name = object.optString("last_name");
                    fbEmail = object.optString("email");
                    String id = object.optString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    Log.e("Firstname", "" + f_name);
                    Log.e("last_name", "" + l_name);
                    Log.e("fbEmail", "" + fbEmail);
                    Log.e("id", "" + id);
                    Log.e("image_url", "" + image_url);

                    fbEmail = object.optString("email");
                    fbName = f_name + l_name;

                    if (fbEmail.length() == 0) {
                        fbEmail = fbName.trim() + "@facebook.com";
                    }
                    Log.e("name", "" + fbName);
                    Log.e("email", "" + fbEmail);

                    strEmail = fbEmail;
                    strFirstname = fbName;
                    strType = "" + Constant.typeFacebook;

                    storeImageName = (getResources().getString(R.string.app_name) + "" + fbName.replaceAll("[, ; & _]", "")).toLowerCase();
                    if (image_url != null) {
                        downloadAndSave(LoginActivity.this, image_url, "" + storeImageName);
                    } else {
                        SignInSocial();
                    }

                }
            }
        });
        // We set parameters to the GraphRequest using a Bundle.
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.e("getDisplayName", "" + account.getDisplayName());
            Log.e("getEmail", "" + account.getEmail());
            Log.e("getIdToken", "" + account.getIdToken());
            Log.e("getPhotoUrl", "" + account.getPhotoUrl());

            strFirstname = "" + account.getDisplayName();
            strEmail = "" + account.getEmail();
            strType = "" + Constant.typeGmail;

            storeImageName = (getResources().getString(R.string.app_name) + "" + strFirstname.replaceAll("[, ; & _]", "")).toLowerCase();
            if (account.getPhotoUrl() != null) {
                downloadAndSave(LoginActivity.this, "" + account.getPhotoUrl(), "" + storeImageName);
            } else {
                SignInSocial();
            }

        } catch (ApiException e) {
            Log.e("ApiException", "" + e.getStatusCode());
        }
    }

    private void downloadAndSave(Activity activity, String download_url, String imageName) {
        String downloadDirectory = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            downloadDirectory = Functions.getAppFolder(activity);
        } else {
            downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Image/";
        }

        File file = new File(downloadDirectory);
        if (!(file.exists())) {
            Log.e("downloadAndSave", "Image directory created again");
            file.mkdirs();
        }

        Functions.showDeterminentLoader(activity, false, false);
        PRDownloader.initialize(activity.getApplicationContext());
        DownloadRequest prDownloader = PRDownloader.download(download_url, downloadDirectory, imageName + ".jpeg")
                .build()
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                        Functions.showLoadingProgress(prog);
                    }
                });

        String finalDownloadDirectory = downloadDirectory;
        prDownloader.start(new OnDownloadListener() {
            @Override
            public void onDownloadComplete() {
                Functions.cancelDeterminentLoader();
                Log.e("=>downloadDirectory", "" + finalDownloadDirectory);
                Log.e("=>imageName", "" + imageName);
                scanFile(finalDownloadDirectory, imageName + ".jpeg");
            }

            @Override
            public void onError(Error error) {
                Functions.cancelDeterminentLoader();
            }
        });
    }

    private void downloadAEImage(Activity activity, String path, String imageName) {

        ContentValues valuesimage;
        valuesimage = new ContentValues();
        valuesimage.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM
                + Functions.getAppFolder(LoginActivity.this) + "Image/");
        valuesimage.put(MediaStore.MediaColumns.TITLE, imageName);
        valuesimage.put(MediaStore.MediaColumns.DISPLAY_NAME, imageName);
        valuesimage.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        valuesimage.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        valuesimage.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis());
        valuesimage.put(MediaStore.MediaColumns.IS_PENDING, 1);
        ContentResolver resolver = activity.getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        Uri uriSavedImage = resolver.insert(collection, valuesimage);

        ParcelFileDescriptor pfd;
        try {
            pfd = activity.getContentResolver().openFileDescriptor(uriSavedImage, "w");

            FileOutputStream out = new FileOutputStream(pfd.getFileDescriptor());
            imageFile = new File(path + imageName);
            FileInputStream in = new FileInputStream(imageFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();
            pfd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        valuesimage.clear();
        valuesimage.put(MediaStore.MediaColumns.IS_PENDING, 0);
        activity.getContentResolver().update(uriSavedImage, valuesimage, null, null);

        if (imageFile != null) {
            Log.e("==>imageFile", "" + imageFile.getPath());

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

            Log.e("body", "==> " + body);

            SignInSocial();
        }
    }

    private void scanFile(String downloadDirectory, String imageName) {
        imageFile = new File(downloadDirectory + imageName);

        Log.e("=>imageFile", "" + imageFile.getPath());

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        body = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        Log.e("body", "=> " + body);

        SignInSocial();
    }

    /* login API */
    private void SignInSocial() {
        if (!((Activity) LoginActivity.this).isFinishing()) {
            Utility.ProgressBarShow(LoginActivity.this);
        }
        Call<LoginRegiModel> call = BaseURL.getVideoAPI().login("" + strType, "" + strEmail, "" + strFirstname);
        call.enqueue(new Callback<LoginRegiModel>() {
            @Override
            public void onResponse(Call<LoginRegiModel> call, Response<LoginRegiModel> response) {
                Utility.ProgressbarHide();
                try {
                    Log.e("SignInSocial", "status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("email==>", "" + response.body().getResult().get(0).getEmail());

                        prefManager.setFirstTimeLaunch(false);
                        Utility.storeUserCred(LoginActivity.this,
                                "" + response.body().getResult().get(0).getId(),
                                "" + response.body().getResult().get(0).getEmail(),
                                "" + response.body().getResult().get(0).getName(),
                                "" + response.body().getResult().get(0).getMobile(),
                                "" + response.body().getResult().get(0).getType(),
                                "" + response.body().getResult().get(0).getImage());

                        if (!TextUtils.isEmpty(imageFile.getPath()) && body != null) {
                            UpdateProfileImg();
                        }

                    } else {
                        Utility.exitAlertDialog(LoginActivity.this, "" + response.body().getMessage(), false, false);
                    }
                } catch (Exception e) {
                    Log.e("SignInSocial", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<LoginRegiModel> call, Throwable t) {
                Log.e("SignInSocial", "onFailure => " + t.getMessage());
                Utility.ProgressbarHide();
                Utility.exitAlertDialog(LoginActivity.this, "" + t.getMessage(), false, false);
            }
        });
    }

    /* image_upload API */
    private void UpdateProfileImg() {

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "" + prefManager.getLoginId());

        Call<SuccessModel> call = BaseURL.getVideoAPI().image_upload(userId, body);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    Log.e("image_upload", "Status => " + response.body().getStatus());
                    Log.e("image_upload", "Message => " + response.body().getMessage());

                    /* Remove Image From storage */
                    Utility.removeDownloadImage(LoginActivity.this, "" + imageFile.getPath());

                    // after validation checking, log in user a/c
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e("image_upload", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("image_upload", "onFailure => " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.ProgressbarHide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPermissionResult.unregister();
        Utility.ProgressbarHide();
    }

}
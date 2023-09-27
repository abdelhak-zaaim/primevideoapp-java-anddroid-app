package com.cinefilmz.tv.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Utils.PermissionUtils;
import com.cinefilmz.tv.Model.ProfileModel.ProfileModel;
import com.cinefilmz.tv.Model.ProfileModel.Result;
import com.cinefilmz.tv.Model.SuccessModel.SuccessModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.ScalingUtilities;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.OnProgressListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private PermissionUtils takePermissionUtils;
    private static final String TAG = EditProfile.class.getSimpleName();

    private LinearLayout lyToolbarTitle, lySave, lyClearText, lyDeleteProfile;
    private RoundedImageView ivProfileImg;
    private TextView txtToolbarTitle, txtChangePic, txtClearTextIcon, txtProfileNote, txtSave;
    private EditText etName;

    private List<Result> profileData;
    private String strUsername = "", fileProfile = "", avatarUrl = "";
    private File avatarImageFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        PrefManager.forceRTLIfSupported(getWindow(), this);
        takePermissionUtils = new PermissionUtils(this, mPermissionResult);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (!TextUtils.isEmpty(bundle.getString("Avatar"))) {
                avatarUrl = bundle.getString("Avatar");
                Log.e("avatarUrl", "==> " + avatarUrl);
            }
        }

        init();
        txtToolbarTitle.setText("" + getResources().getString(R.string.edit_profile));

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e("etName", "Before text => " + s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("etName", "Before text => " + s.toString());
                strUsername = "" + etName.getText().toString().trim();

                if (s.length() > 0) {
                    if (!TextUtils.isEmpty(strUsername)) {
                        txtSave.setTextColor(getResources().getColor(R.color.text_black));
                        lySave.setBackground(getResources().getDrawable(R.drawable.round_bg_r5_primary));
                    } else {
                        txtSave.setTextColor(getResources().getColor(R.color.disableText));
                        lySave.setBackground(getResources().getDrawable(R.drawable.round_bg_r5_disable));
                    }
                    lyClearText.setVisibility(View.VISIBLE);
                } else {
                    lyClearText.setVisibility(View.INVISIBLE);
                    txtSave.setTextColor(getResources().getColor(R.color.disableText));
                    lySave.setBackground(getResources().getDrawable(R.drawable.round_bg_r5_disable));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e("etName", "Before text => " + s.toString());
            }
        });

        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                strUsername = "" + etName.getText().toString().trim();

                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.e("strUsername", "" + strUsername);
                    if (!TextUtils.isEmpty(strUsername)) {
                        txtSave.setTextColor(getResources().getColor(R.color.text_black));
                        lySave.setBackground(getResources().getDrawable(R.drawable.round_bg_r5_primary));
                    } else {
                        txtSave.setTextColor(getResources().getColor(R.color.disableText));
                        lySave.setBackground(getResources().getDrawable(R.drawable.round_bg_r5_disable));
                    }
                }
                return false;
            }
        });

    }

    private void init() {
        try {
            prefManager = new PrefManager(this);

            lyToolbarTitle = findViewById(R.id.lyToolbarTitle);
            lyToolbarTitle.setVisibility(View.VISIBLE);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            lySave = findViewById(R.id.lySave);
            lyClearText = findViewById(R.id.lyClearText);
            lyDeleteProfile = findViewById(R.id.lyDeleteProfile);

            ivProfileImg = findViewById(R.id.ivProfileImg);
            etName = findViewById(R.id.etName);
            txtChangePic = findViewById(R.id.txtChangePic);
            txtClearTextIcon = findViewById(R.id.txtClearTextIcon);
            txtProfileNote = findViewById(R.id.txtProfileNote);
            txtSave = findViewById(R.id.txtSave);

            ivProfileImg.setOnClickListener(this);
            txtChangePic.setOnClickListener(this);
            lySave.setOnClickListener(this);
            lyClearText.setOnClickListener(this);
            lyDeleteProfile.setOnClickListener(this);
        } catch (Exception e) {
            Log.e(TAG, "init Exception => " + e);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Utility.checkLoginUser(EditProfile.this) && !Constant.isSelectPic) {
            GetProfile();
        }
    }

    /* get_profile API */
    private void GetProfile() {
        Utility.ProgressBarShow(EditProfile.this);
        Call<ProfileModel> call = BaseURL.getVideoAPI().get_profile("" + prefManager.getLoginId());
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                try {
                    Log.e("get_profile", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult() != null) {
                            profileData = new ArrayList<>();
                            profileData = response.body().getResult();
                            Log.e("profileData", "" + profileData.get(0).getId());

                            etName.setText("" + response.body().getResult().get(0).getName());
                            strUsername = "" + response.body().getResult().get(0).getName();

                            Log.e("profile", "avatarUrl ==>> " + avatarUrl);
                            if (!TextUtils.isEmpty(avatarUrl)) {
                                if (avatarUrl != null) {
                                    Picasso.get().load("" + avatarUrl).into(ivProfileImg);
                                    downloadAvatarAndUpdate();
                                }
                            } else {
                                if (!TextUtils.isEmpty(response.body().getResult().get(0).getImage())) {
                                    Picasso.get().load(response.body().getResult().get(0).getImage()).placeholder(R.drawable.ic_user).into(ivProfileImg);
                                }
                            }

                            Utility.storeUserCred(EditProfile.this,
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
                Utility.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                Log.e("get_profile", "onFailure => " + t.getMessage());
                Utility.ProgressbarHide();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivProfileImg:
            case R.id.txtChangePic:
                if (Utility.checkLoginUser(EditProfile.this)) {
                    if (takePermissionUtils.isStorageCameraPermissionGranted()) {
                        Constant.isSelectPic = true;
                        selectImageOptions();
                    } else {
                        takePermissionUtils.showStorageCameraPermissionDailog(getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                    }
                }
                break;

            case R.id.lyClearText:
                Log.e("strUsername =>", "" + strUsername);
                if (strUsername.length() > 0) {
                    etName.setText("");
                    strUsername = "";
                }
                break;

            case R.id.lyDeleteProfile:
                break;

            case R.id.lySave:
                ValidateAndUpdate();
                break;
        }
    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {

                    boolean allPermissionClear = true;
                    List<String> blockPermissionCheck = new ArrayList<>();
                    for (String key : result.keySet()) {
                        if (!(result.get(key))) {
                            allPermissionClear = false;
                            blockPermissionCheck.add(Functions.getPermissionStatus(EditProfile.this, key));
                        }
                    }
                    Log.e("blockPermissionCheck", "" + blockPermissionCheck);
                    Log.e("allPermissionClear", "" + allPermissionClear);
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(EditProfile.this, getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                    } else if (allPermissionClear) {
                        Constant.isSelectPic = true;
                        selectImageOptions();
                    }

                }
            });

    /* NEW take a picture form camera or pick the image from gallery */
    private void selectImageOptions() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.add_pic_selcetions_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        LinearLayout lyClickNegative = bottomSheetDialog.findViewById(R.id.lyClickNegative);
        LinearLayout lyByCamera = bottomSheetDialog.findViewById(R.id.lyByCamera);
        LinearLayout lyByGallery = bottomSheetDialog.findViewById(R.id.lyByGallery);
        LinearLayout lyByAvatar = bottomSheetDialog.findViewById(R.id.lyByAvatar);

        lyClickNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                Constant.isSelectPic = false;
            }
        });

        lyByCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }

                openCameraIntent();
            }
        });

        lyByGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                resultCallbackForGallery.launch(intent);
            }
        });

        lyByAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                startActivity(new Intent(EditProfile.this, AvatarForProfile.class));
                finish();
            }
        });

    }

    ActivityResultLauncher<Intent> resultCallbackForGallery = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Uri selectedImage = data.getData();
                        beginCrop(selectedImage);
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Constant.isSelectPic = false;
                    }
                }
            });

    ActivityResultLauncher<Intent> resultCallbackForCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.e("=>resultCode", "" + result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.e("=>resultData", "" + result.getData());
                        Matrix matrix = new Matrix();
                        try {
                            Log.e("imageFilePath", "" + imageFilePath);
                            android.media.ExifInterface exif = new android.media.ExifInterface(imageFilePath);
                            int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, 1);
                            switch (orientation) {
                                case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.postRotate(90);
                                    break;
                                case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.postRotate(180);
                                    break;
                                case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.postRotate(270);
                                    break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Uri selectedImage = (Uri.fromFile(new File(imageFilePath)));
                        beginCrop(selectedImage);
                    } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                        Constant.isSelectPic = false;
                    }
                }
            });

    // below three method is related with taking the picture from camera
    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (Exception ex) {
                Log.e("photoFile", "Exception => " + ex);
            }
            if (photoFile != null) {
                Constant.isSelectPic = true;
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                resultCallbackForCamera.launch(pictureIntent);
            }
        }
    }

    // create a temp image file
    String imageFilePath;

    private File createImageFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        imageFilePath = image.getAbsolutePath();
        Log.e("imageFilePath", "" + imageFilePath);
        return image;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        if (!TextUtils.isEmpty(imageFilePath)) {
            outState.putString("imageFilePath", imageFilePath);
            outState.putBoolean("isSelected", Constant.isSelectPic);
            Log.e("onSave", "imageFilePath => " + imageFilePath);
            Log.e("onSave", "isSelectPic => " + Constant.isSelectPic);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // get the file url
        imageFilePath = savedInstanceState.getString("imageFilePath");
        Constant.isSelectPic = savedInstanceState.getBoolean("isSelected");
        Log.e("onRestore", "isSelectPic => " + Constant.isSelectPic);
    }

    ActivityResultLauncher<Intent> resultCallbackForCrop = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        CropImage.ActivityResult cropResult = CropImage.getActivityResult(data);

                        Picasso.get().load(cropResult.getUri()).into(ivProfileImg);
                        Log.e("fileProfile", "" + fileProfile);
                        // get the image uri after the image crope and resize it
                        decodeFile(getRealPathFromURI(cropResult.getUri()), Constant.PROFILE_IMAGE_SIZE, Constant.PROFILE_IMAGE_SIZE);
                    }
                }
            });

    private void beginCrop(Uri source) {
        Intent intent = CropImage.activity(source).setCropShape(CropImageView.CropShape.OVAL)
                .setAspectRatio(1, 1).getIntent(EditProfile.this);
        resultCallbackForCrop.launch(intent);
    }

    private String decodeFile(String path, int desiredWidth, int desiredHeight) {
        Log.e("path", "==> " + path);
        Log.e("desiredWidth", "==> " + desiredWidth);
        Log.e("desiredHeight", "==> " + desiredHeight);

        String strMyImagePath = null;

        Bitmap scaledBitmap = null;
        if (path.contains(".png")) {
            Log.e("png path ==>", "" + strMyImagePath);
            try {
                // Part 1: Decode image
                Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, desiredWidth, desiredHeight, ScalingUtilities.ScalingLogic.FIT);

                assert unscaledBitmap != null;
                if (!(unscaledBitmap.getWidth() <= desiredWidth && unscaledBitmap.getHeight() <= desiredHeight)) {
                    // Part 2: Scale image
                    scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, desiredWidth, desiredHeight, ScalingUtilities.ScalingLogic.FIT);
                } else if (unscaledBitmap.getWidth() <= desiredWidth && unscaledBitmap.getHeight() <= desiredHeight) {
                    fileProfile = path;
                    return path;
                } else {
                    unscaledBitmap.recycle();
                    return path;
                }

                // Store to tmp file
                String extr = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    extr = Functions.getAppFolder(EditProfile.this);
                } else {
                    extr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Image/";
                }
                Log.e("jpeg", "ExternalStorageDirectory => " + extr);
                File mFolder = new File(extr);
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                    Log.e("jpeg", "mFolder.mkdir() => " + mFolder.mkdir());
                }

                String s = "tmp.png";
                File f = new File(mFolder.getAbsolutePath(), s);

                strMyImagePath = f.getAbsolutePath();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, 75, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.e("png", "FileNotFoundException => " + e);
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("png Exception ==>", "" + e);
                    e.printStackTrace();
                }

                scaledBitmap.recycle();
            } catch (Exception e) {
                Log.e("Upload pic in PNG", "Exception => " + e);
            }

        } else {
            Log.e("jpeg path ==>", "" + strMyImagePath);
            try {
                // Part 1: Decode image
                Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, desiredWidth, desiredHeight, ScalingUtilities.ScalingLogic.FIT);

                if (!(unscaledBitmap.getWidth() <= desiredWidth && unscaledBitmap.getHeight() <= desiredHeight)) {
                    // Part 2: Scale image
                    scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, desiredWidth, desiredHeight, ScalingUtilities.ScalingLogic.FIT);
                } else if (unscaledBitmap.getWidth() <= desiredWidth && unscaledBitmap.getHeight() <= desiredHeight) {
                    fileProfile = path;
                    return path;
                } else {
                    unscaledBitmap.recycle();
                    return path;
                }

                String extr = "";
                // Store to tmp file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    extr = Functions.getAppFolder(EditProfile.this);
                } else {
                    extr = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Image/";
                }
                Log.e("jpeg", "ExternalStorageDirectory => " + extr);
                File mFolder = new File(extr);
                if (!mFolder.exists()) {
                    mFolder.mkdir();
                    Log.e("jpeg", "mFolder.mkdir() => " + mFolder.mkdir());
                }

                String s = "tmp.jpeg";
                File f = new File(mFolder.getAbsolutePath(), s);

                strMyImagePath = f.getAbsolutePath();
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(f);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.e("jpeg", "FileNotFoundException ==> " + e);
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("jpeg", "Exception => " + e);
                    e.printStackTrace();
                }

                scaledBitmap.recycle();
            } catch (Exception e) {
                Log.e("Upload pic in JPEG", "Exception => " + e);
            }

        }
        if (strMyImagePath == null) {
            return path;
        }
        Log.e("strMyImagePath", "(Final) => " + strMyImagePath);
        fileProfile = strMyImagePath;
        return strMyImagePath;
    }

    private String getRealPathFromURI(Uri contentURI) {
        @SuppressLint("Recycle") Cursor cursor = EditProfile.this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    private void ValidateAndUpdate() {
        strUsername = "" + etName.getText().toString().trim();

        if (TextUtils.isEmpty(strUsername)) {
            Toasty.warning(this, "" + getResources().getString(R.string.enter_name), Toasty.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(fileProfile)) {
            UpdateProfile();
        } else {
            UpdateProfileImg(fileProfile);
        }
    }

    /* Download Avatar Image START */
    private void downloadAvatarAndUpdate() {
        String downloadDirectory, imageName = prefManager.getLoginId() + "_" + System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            downloadDirectory = Functions.getAppFolder(EditProfile.this);
        } else {
            downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Image/";
        }

        File file = new File(downloadDirectory);
        if (!(file.exists())) {
            Log.e("downloadAndSave", "Image directory created again");
            file.mkdirs();
        }

        Functions.showDeterminentLoader(EditProfile.this, false, false);
        PRDownloader.initialize(getApplicationContext());
        DownloadRequest prDownloader = PRDownloader.download(avatarUrl, downloadDirectory, imageName + ".jpeg")
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
                + Functions.getAppFolder(EditProfile.this) + "Image/");
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
            avatarImageFile = new File(path + imageName);
            FileInputStream in = new FileInputStream(avatarImageFile);

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

        if (avatarImageFile != null) {
            Log.e("==>avatarImageFile", "" + avatarImageFile.getPath());

            UpdateProfileImg("" + avatarImageFile.getPath());
        }
    }

    private void scanFile(String downloadDirectory, String imageName) {
        avatarImageFile = new File(downloadDirectory + imageName);
        Log.e("=>avatarImageFile", "" + avatarImageFile.getPath());

        UpdateProfileImg("" + avatarImageFile.getPath());
    }
    /* Download Avatar Image END */

    /* update_profile API */
    private void UpdateProfile() {
        Log.e("strUsername", "===>> " + strUsername);
        Utility.ProgressBarShow(EditProfile.this);
        Call<ProfileModel> call = BaseURL.getVideoAPI().update_profile("" + prefManager.getLoginId(), "" + strUsername);
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(Call<ProfileModel> call, Response<ProfileModel> response) {
                try {
                    Log.e("update_profile", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult() != null) {
                            GetProfile();
                        }

                    } else {
                        Log.e("update_profile", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("update_profile", "Exception => " + e);
                }
                Utility.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<ProfileModel> call, Throwable t) {
                Log.e("update_profile", "onFailure => " + t.getMessage());
                Utility.ProgressbarHide();
            }
        });
    }

    /* image_upload API */
    private void UpdateProfileImg(String filePath) {
        Log.e("Entered Name", "===> " + etName.getText().toString());
        if (!TextUtils.isEmpty(etName.getText().toString())) {
            strUsername = "" + etName.getText().toString().trim();
        }
        File file = new File(filePath);
        Log.e("<file>-->", file + "");

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        RequestBody userId = RequestBody.create(MediaType.parse("text/plain"), "" + prefManager.getLoginId());

        Utility.ProgressBarShow(EditProfile.this);
        Call<SuccessModel> call = BaseURL.getVideoAPI().image_upload(userId, body);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    Log.e("image_upload", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (avatarImageFile != null && !TextUtils.isEmpty(avatarUrl)) {
                            /* Remove Image From storage */
                            Utility.removeDownloadImage(EditProfile.this, "" + avatarImageFile.getPath());
                            avatarUrl = "";
                            avatarImageFile = null;
                        }

                        UpdateProfile();
                    } else {
                        Log.e("image_upload", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("image_upload", "Exception => " + e);
                }
                Utility.ProgressbarHide();
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e("image_upload", "onFailure => " + t.getMessage());
                Utility.ProgressbarHide();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.ProgressbarHide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.ProgressbarHide();
    }

}
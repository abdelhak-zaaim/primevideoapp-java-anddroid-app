package com.cinefilmz.tv.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Utils.PermissionUtils;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.ScalingUtilities;
import com.cinefilmz.tv.Utils.Utility;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CreateProfile extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private PermissionUtils takePermissionUtils;
    private static final String TAG = CreateProfile.class.getSimpleName();

    private LinearLayout lyToolbarTitle, lySave, lyClearText;
    private RoundedImageView ivProfileImg;
    private TextView txtToolbarTitle, txtChangePic, txtClearTextIcon, txtKidsLearnMore, txtSave;
    private EditText etName;
    private SwitchCompat switchKidsOrNot;

    private String strUsername = "", fileProfile = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        PrefManager.forceRTLIfSupported(getWindow(), this);
        takePermissionUtils = new PermissionUtils(this, mPermissionResult);

        init();
        txtToolbarTitle.setText("" + getResources().getString(R.string.new_));

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
            prefManager = new PrefManager(CreateProfile.this);

            lyToolbarTitle = findViewById(R.id.lyToolbarTitle);
            lyToolbarTitle.setVisibility(View.VISIBLE);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);

            lySave = findViewById(R.id.lySave);
            lyClearText = findViewById(R.id.lyClearText);

            ivProfileImg = findViewById(R.id.ivProfileImg);
            etName = findViewById(R.id.etName);
            txtChangePic = findViewById(R.id.txtChangePic);
            txtClearTextIcon = findViewById(R.id.txtClearTextIcon);
            txtKidsLearnMore = findViewById(R.id.txtKidsLearnMore);
            txtSave = findViewById(R.id.txtSave);
            switchKidsOrNot = findViewById(R.id.switchKidsOrNot);

            ivProfileImg.setOnClickListener(this);
            txtChangePic.setOnClickListener(this);
            txtKidsLearnMore.setOnClickListener(this);
            lySave.setOnClickListener(this);
            lyClearText.setOnClickListener(this);
        } catch (Exception e) {
            Log.e(TAG, "init Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivProfileImg:
            case R.id.txtChangePic:
                if (Utility.checkLoginUser(CreateProfile.this)) {
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

            case R.id.txtKidsLearnMore:
                break;

            case R.id.lySave:
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
                            blockPermissionCheck.add(Functions.getPermissionStatus(CreateProfile.this, key));
                        }
                    }
                    Log.e("blockPermissionCheck", "" + blockPermissionCheck);
                    Log.e("allPermissionClear", "" + allPermissionClear);
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(CreateProfile.this, getString(R.string.we_need_storage_and_camera_permission_for_upload_profile_pic));
                    } else if (allPermissionClear) {
                        Constant.isSelectPic = true;
                        selectImageOptions();
                    }

                }
            });

    /* take a picture from camera or pick the image from gallery or choose avatar from list */
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
                startActivity(new Intent(CreateProfile.this, AvatarForProfile.class));
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
                .setAspectRatio(1, 1).getIntent(CreateProfile.this);
        resultCallbackForCrop.launch(intent);
    }

    private String decodeFile(String path, int desiredWidth, int desiredHeight) {
        Log.e("path", "==> " + path);
        Log.e("desiredWidth", "==> " + desiredWidth);
        Log.e("desiredHeight", "==> " + desiredHeight);

        String strMyImagePath = null;

        if (path.contains(".png")) {
            Bitmap scaledBitmap = null;
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
                    //AddProfileImg(path);
                    return path;
                } else {
                    unscaledBitmap.recycle();
                    return path;
                }

                // Store to tmp file
                String extr = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    extr = Functions.getAppFolder(CreateProfile.this);
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

            if (strMyImagePath == null) {
                return path;
            }
            Log.e("strMyImagePath", "(Final) => " + strMyImagePath);
            fileProfile = strMyImagePath;
            //AddProfileImg(strMyImagePath);

        } else {
            Bitmap scaledBitmap = null;
            Log.e("jpeg path ==>", "" + strMyImagePath);
            try {
                // Part 1: Decode image
                Bitmap unscaledBitmap = ScalingUtilities.decodeFile(path, desiredWidth, desiredHeight, ScalingUtilities.ScalingLogic.FIT);

                if (!(unscaledBitmap.getWidth() <= desiredWidth && unscaledBitmap.getHeight() <= desiredHeight)) {
                    // Part 2: Scale image
                    scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, desiredWidth, desiredHeight, ScalingUtilities.ScalingLogic.FIT);
                } else if (unscaledBitmap.getWidth() <= desiredWidth && unscaledBitmap.getHeight() <= desiredHeight) {
                    fileProfile = path;
                    //AddProfileImg(path);
                    return path;
                } else {
                    unscaledBitmap.recycle();
                    return path;
                }

                String extr = "";
                // Store to tmp file
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    extr = Functions.getAppFolder(CreateProfile.this);
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

            if (strMyImagePath == null) {
                return path;
            }
            Log.e("strMyImagePath", "(Final) => " + strMyImagePath);
            fileProfile = strMyImagePath;
            //AddProfileImg(strMyImagePath);
        }
        return strMyImagePath;
    }

    private String getRealPathFromURI(Uri contentURI) {
        @SuppressLint("Recycle") Cursor cursor = CreateProfile.this.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
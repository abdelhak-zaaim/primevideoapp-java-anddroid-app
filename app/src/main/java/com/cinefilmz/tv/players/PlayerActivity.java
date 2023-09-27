package com.cinefilmz.tv.players;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.app.MediaRouteButton;
import androidx.multidex.MultiDex;

import com.cinefilmz.tv.Utils.MyApp;
import com.cinefilmz.tv.Activity.MainActivity;
import com.cinefilmz.tv.Model.DownloadShowModel.EpisodeItem;
import com.cinefilmz.tv.Model.SuccessModel.SuccessModel;
import com.cinefilmz.tv.Model.VideoSeasonModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.CustomDataSourceFactory;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.TrackSelectionDialog;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SubtitleView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private static final String TAG = PlayerActivity.class.getSimpleName();
    private ProgressBar progressBar;
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 111;
    private Dialog dialog;

    private RoundedImageView ivNextEpiPoster;
    private ImageView ivThumb, imgBack, serverIv, imgFull, aspectRatioIv, ivExoReplay, externalPlayerIv, imgSubtitle, imgAudio;
    private RelativeLayout contentDetails, exoplayerLayout, lPlay, rlNextEpisode;
    private SimpleExoPlayer player;
    private CacheDataSourceFactory cacheDataSourceFactory;
    private PlayerView simpleExoPlayerView;
    private PlayerControlView castControlView;
    private SubtitleView subtitleView;
    private LinearLayout volumnControlLayout, brightnessControlLayout, exoRewind, exoForward, seekbarLayout, lyPlayCancel, lyPlayNext;
    private VerticalSeekBar volumnSeekbar, brightnessSeekbar;
    private TextView liveTv, txtPlayNext, txtPlayCancel, exoPosition, exoDuration;
    private View playerLayout;

    private DefaultTrackSelector trackSelector;
    private MediaSourceFactory mediaSourceFactory = null;
    private ProgressiveMediaSource progressiveMediaSource;
    private HlsMediaSource hlsMediaSource;
    private DataSource.Factory dataSourceFactory;
    private Uri contentVideoUri = null;
    private MediaRouteButton mediaRouteButton;
    private AudioManager mAudioManager;
    private ImaAdsLoader adsLoader;
    private ContentResolver cResolverBritness;
    private Window mWindow;

    private List<EpisodeItem> episodeItemList;
    private List<Result> tvShowEpisodeList;

    private String videoFrom = "", vType = "", itemID = "", catID = "", URL, title, typeID = "", thumbnailImg, secretKey = "";
    private boolean isPlaying, isFullScr = true, isFinish = false;
    private int playerHeight, aspectClickCount = 1, epiPosition = 0, brightness;
    private long resumePosition = 0L;
    private long playerCurrentPosition = 0L;
    private long mediaDuration = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.HideNavigation(this);
        setContentView(R.layout.activity_player);
        PrefManager.forceRTLIfSupported(getWindow(), this);
        MultiDex.install(this);
        prefManager = new PrefManager(this);

        // Create an AdsLoader.
        adsLoader = new ImaAdsLoader.Builder(PlayerActivity.this).build();

        initViews();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoFrom = bundle.getString("videoFrom");
            secretKey = bundle.getString("secretKey");
            Log.e("videoFrom", "==>>> " + videoFrom);
            Log.e("secretKey", "==>>> " + secretKey);

            if (videoFrom.equalsIgnoreCase("EpisodeDownload")) {
                episodeItemList = new ArrayList<>();
                episodeItemList = (List<EpisodeItem>) bundle.getSerializable("episodeList");
                epiPosition = bundle.getInt("position");

                Log.e(TAG, "epiPosition ==> " + epiPosition);
                Log.e(TAG, "episodeItemList ==> " + episodeItemList.size());

                if (!TextUtils.isEmpty(episodeItemList.get(epiPosition).getLandscape())) {
                    Picasso.get().load(episodeItemList.get(epiPosition).getLandscape()).placeholder(R.drawable.no_image_land).into(ivThumb);
                } else if (!TextUtils.isEmpty(episodeItemList.get(epiPosition).getThumbnail())) {
                    Picasso.get().load(episodeItemList.get(epiPosition).getThumbnail()).placeholder(R.drawable.no_image_land).into(ivThumb);
                } else {
                    Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(ivThumb);
                }

            } else if (videoFrom.equalsIgnoreCase("TvShow")) {
                itemID = bundle.getString("itemID");
                title = bundle.getString("title");
                vType = bundle.getString("vType");
                typeID = bundle.getString("typeID");
                tvShowEpisodeList = new ArrayList<>();
                tvShowEpisodeList = (List<Result>) bundle.getSerializable("episodeList");
                epiPosition = bundle.getInt("position");

                Log.e("resumeFrom", "==>>> " + bundle.getInt("resumeFrom"));
                if (bundle.getInt("resumeFrom") != 0 && bundle.getInt("resumeFrom") > 0) {
                    resumePosition = bundle.getInt("resumeFrom");
                    Log.e("resumePosition", "==>>> " + resumePosition);
                }
                Log.e("itemID", "==>>> " + itemID);
                Log.e("title", "==>>> " + title);
                Log.e("vType", "==>>> " + vType);
                Log.e("typeID", "==>>> " + typeID);
                Log.e(TAG, "epiPosition ==> " + epiPosition);
                Log.e(TAG, "tvShowEpisodeList ==> " + tvShowEpisodeList.size());

                if (!TextUtils.isEmpty(tvShowEpisodeList.get(epiPosition).getLandscape())) {
                    Picasso.get().load(tvShowEpisodeList.get(epiPosition).getLandscape()).into(ivThumb);
                } else if (!TextUtils.isEmpty(tvShowEpisodeList.get(epiPosition).getThumbnail())) {
                    Picasso.get().load(tvShowEpisodeList.get(epiPosition).getThumbnail()).into(ivThumb);
                } else {
                    Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(ivThumb);
                }

            } else {
                itemID = bundle.getString("itemID");
                URL = bundle.getString("url");
                thumbnailImg = bundle.getString("image");
                title = bundle.getString("title");
                vType = bundle.getString("vType");
                catID = bundle.getString("catId");

                if (videoFrom != null && !TextUtils.isEmpty(videoFrom)) {
                    if (videoFrom.equalsIgnoreCase("Continue")) {
                        resumePosition = bundle.getInt("resumeFrom");
                        Log.e("resumePosition", "==>>> " + resumePosition);
                    }
                }

                Log.e("URL", "==>>> " + URL);
                Log.e("itemID", "==>>> " + itemID);
                Log.e("thumbnailImg", "==>>> " + thumbnailImg);
                Log.e("title", "==>>> " + title);
                Log.e("vType", "==>>> " + vType);
                Log.e("secretKey", "==>>> " + secretKey);
                Log.e("catID", "==>>> " + catID);

                if (!TextUtils.isEmpty(thumbnailImg)) {
                    Picasso.get().load(thumbnailImg).placeholder(R.drawable.no_image_land).into(ivThumb);
                } else {
                    Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(ivThumb);
                }
            }
        }

        try {
            if (videoFrom.equalsIgnoreCase("Continue")) {
                releasePlayer();
                setPlayerFullScreen();
                progressBar.setVisibility(VISIBLE);
                lPlay.setVisibility(VISIBLE);
                imgFull.setVisibility(GONE);
                initVideoPlayer(URL, "" + videoFrom);

            } else if (videoFrom.equalsIgnoreCase("EpisodeDownload") || videoFrom.equalsIgnoreCase("TvShow")) {
                if (Util.SDK_INT <= 23 || player == null) {
                    releasePlayer();
                    initEpisodePlayer(epiPosition);
                    lPlay.setVisibility(VISIBLE);
                    if (simpleExoPlayerView != null) {
                        simpleExoPlayerView.onResume();
                    }
                }
            } else if (videoFrom.equalsIgnoreCase("LiveChannel")) {
                if (Util.SDK_INT <= 23 || player == null) {
                    releasePlayer();
                    initLiveStreaming(URL, "LiveChannel");
                    lPlay.setVisibility(VISIBLE);
                    if (simpleExoPlayerView != null) {
                        simpleExoPlayerView.onResume();
                    }
                }
            } else {
                if (Util.SDK_INT <= 23 || player == null) {
                    releasePlayer();
                    initVideoPlayer(URL, "" + videoFrom);
                    lPlay.setVisibility(VISIBLE);
                    if (simpleExoPlayerView != null) {
                        simpleExoPlayerView.onResume();
                    }
                }
            }
        } catch (NullPointerException e) {
            Log.e("onCreate", "NullPointerException => " + e);
            if (videoFrom.equalsIgnoreCase("Continue")) {
                releasePlayer();
                setPlayerFullScreen();
                progressBar.setVisibility(VISIBLE);
                lPlay.setVisibility(VISIBLE);
                imgFull.setVisibility(GONE);
                initVideoPlayer(URL, "" + videoFrom);

            } else if (videoFrom.equalsIgnoreCase("EpisodeDownload") || videoFrom.equalsIgnoreCase("TvShow")) {
                if (Util.SDK_INT <= 23 || player == null) {
                    releasePlayer();
                    initEpisodePlayer(epiPosition);
                    lPlay.setVisibility(VISIBLE);
                    if (simpleExoPlayerView != null) {
                        simpleExoPlayerView.onResume();
                    }
                }
            } else if (videoFrom.equalsIgnoreCase("LiveChannel")) {
                if (Util.SDK_INT <= 23 || player == null) {
                    releasePlayer();
                    initLiveStreaming(URL, "LiveChannel");
                    lPlay.setVisibility(VISIBLE);
                    if (simpleExoPlayerView != null) {
                        simpleExoPlayerView.onResume();
                    }
                }
            } else {
                if (Util.SDK_INT <= 23 || player == null) {
                    releasePlayer();
                    initVideoPlayer(URL, "" + videoFrom);
                    lPlay.setVisibility(VISIBLE);
                    if (simpleExoPlayerView != null) {
                        simpleExoPlayerView.onResume();
                    }
                }
            }
        }

        /* Volume And Britness Initialize */
        mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        // Get the content resolver
        cResolverBritness = getContentResolver();
        // Get the current window
        mWindow = getWindow();
        //Set the seekbar range between 0 and 255
        brightnessSeekbar.setMax(255);
        brightnessSeekbar.setKeyProgressIncrement(1);
        checkAppSettingsPermission();

        mediaRouteButton.setVisibility(VISIBLE);
        playerHeight = lPlay.getLayoutParams().height;
        progressBar.setMax(100); // 100 maximum value for the progress value
        progressBar.setProgress(50);

        //CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mediaRouteButton);

        volumnSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, AudioManager.ADJUST_SAME);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        brightnessSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    //Set the minimal brightness level
                    if (i <= 20) {
                        brightness = 20;
                    } else {
                        brightness = i;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e("seekBar", "progress ===>>> " + seekBar.getProgress());
                updateBrightness(brightness);
            }
        });

        simpleExoPlayerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if (visibility == 0) {
                    imgBack.setVisibility(VISIBLE);
                    imgFull.setVisibility(VISIBLE);
                    imgSubtitle.setVisibility(GONE);
                    volumnControlLayout.setVisibility(VISIBLE);
                } else {
                    imgBack.setVisibility(GONE);
                    imgFull.setVisibility(GONE);
                    imgSubtitle.setVisibility(GONE);
                    volumnControlLayout.setVisibility(GONE);
                }
            }
        });

    }

    private void initViews() {
        try {
            ivThumb = findViewById(R.id.ivThumb);
            ivExoReplay = findViewById(R.id.ivExoReplay);
            imgBack = findViewById(R.id.imgBack);
            imgAudio = findViewById(R.id.imgAudio);
            imgFull = findViewById(R.id.imgFullScr);
            imgSubtitle = findViewById(R.id.imgSubtitle);
            progressBar = findViewById(R.id.progressBar);
            lPlay = findViewById(R.id.play);
            simpleExoPlayerView = findViewById(R.id.videoView);
            exoplayerLayout = findViewById(R.id.exoPlayerLayout);
            subtitleView = findViewById(R.id.subtitle);
            playerLayout = findViewById(R.id.playerLayout);
            aspectRatioIv = findViewById(R.id.aspectRatioIv);
            externalPlayerIv = findViewById(R.id.externalPlayerIv);
            volumnControlLayout = findViewById(R.id.volumnLayout);
            volumnSeekbar = findViewById(R.id.volumnSeekbar);
            brightnessControlLayout = findViewById(R.id.brightnessLayout);
            brightnessSeekbar = findViewById(R.id.brightnessSeekbar);
            mediaRouteButton = findViewById(R.id.mediaRouteButton);
            castControlView = findViewById(R.id.castControlView);
            exoRewind = findViewById(R.id.rewindLayout);
            exoForward = findViewById(R.id.forwardLayout);
            seekbarLayout = findViewById(R.id.seekbarLayout);
            liveTv = findViewById(R.id.liveTv);
            exoPosition = findViewById(R.id.exo_position);
            exoDuration = findViewById(R.id.exo_duration);

            contentDetails = findViewById(R.id.content_details);
            rlNextEpisode = findViewById(R.id.rlNextEpisode);
            ivNextEpiPoster = findViewById(R.id.ivNextEpiPoster);
            lyPlayCancel = findViewById(R.id.lyPlayCancel);
            lyPlayNext = findViewById(R.id.lyPlayNext);
            txtPlayNext = findViewById(R.id.txtPlayNext);
            txtPlayCancel = findViewById(R.id.txtPlayCancel);
            serverIv = findViewById(R.id.imgServer);

            ivExoReplay.setOnClickListener(this);
            imgBack.setOnClickListener(this);
            imgAudio.setOnClickListener(this);
            imgFull.setOnClickListener(this);
            imgSubtitle.setOnClickListener(this);
            externalPlayerIv.setOnClickListener(this);
            aspectRatioIv.setOnClickListener(this);
            lyPlayCancel.setOnClickListener(this);
            lyPlayNext.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAudioManager != null) {
            volumnSeekbar.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            int currentVolumn = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            Log.e("==>currentVolume", "" + currentVolumn);
            volumnSeekbar.setProgress(currentVolumn);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check vpn connection
        if (Util.SDK_INT <= 23 || player != null) {
            if (simpleExoPlayerView != null) {
                simpleExoPlayerView.onResume();
            }
            if (videoFrom.equalsIgnoreCase("Continue")) {
                try {
                    if (resumePosition > 0) {
                        Log.e("resumePosition", "==>>>> " + resumePosition);
                        player.seekTo(resumePosition);
                        player.setPlayWhenReady(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (dialog != null) {
            if (dialog.isShowing()) {
                player.setPlayWhenReady(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBack:
                Log.e("isFullScr", "" + isFullScr);
                if (isFullScr) {
                    controlFullScreenPlayer();
                } else {
                    finish();
                }
                break;

            case R.id.lyPlayNext:
                Log.e("epiPosition", "onClick =======> " + epiPosition);
                releasePlayer();
                initEpisodePlayer(epiPosition);
                lPlay.setVisibility(VISIBLE);
                rlNextEpisode.setVisibility(GONE);
                contentDetails.setVisibility(VISIBLE);
                break;

            case R.id.lyPlayCancel:
                finish();
                break;

            case R.id.ivExoReplay:
                player.seekTo(0);
                player.setPlayWhenReady(true);
                break;

            case R.id.imgSubtitle:
                break;

            case R.id.imgFullScr:
                controlFullScreenPlayer();
                break;

            case R.id.imgAudio:
                if (TrackSelectionDialog.willHaveContent(trackSelector)) {
                    MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(trackSelector.getCurrentMappedTrackInfo());
                    DefaultTrackSelector.Parameters parameters = trackSelector.getParameters();
                    TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForTrackSelector(trackSelector,/* onDismissListener= */ dismissedDialog -> {
                    });
                    trackSelectionDialog.show(getSupportFragmentManager(), null);
                }
                break;

            case R.id.externalPlayerIv:
                break;

            case R.id.aspectRatioIv:
                Log.e("aspectClickCount", "===>>> " + aspectClickCount);
                if (aspectClickCount == 1) {
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    aspectClickCount = 2;
                } else if (aspectClickCount == 2) {
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                    aspectClickCount = 3;
                } else if (aspectClickCount == 3) {
                    simpleExoPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                    player.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
                    aspectClickCount = 1;
                }
                break;
        }
    }

    private void checkAppSettingsPermission() {
        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(PlayerActivity.this);
        } else {
            permission = ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (permission) {
            //do your code
            initBrightness();
        } else {
            player.setPlayWhenReady(false);
            showAlertForSettings();
        }
    }

    private void showAlertForSettings() {
        dialog = new Dialog(PlayerActivity.this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.show_double_button_new_popup_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView tvtitle, tvMessage, tvPositive, tvNegative;
        tvtitle = dialog.findViewById(R.id.tvtitle);
        tvMessage = dialog.findViewById(R.id.tvMessage);
        tvNegative = dialog.findViewById(R.id.tvNegative);
        tvPositive = dialog.findViewById(R.id.tvPositive);

        tvtitle.setText("" + getResources().getString(R.string.settings));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvMessage.setText("" + Html.fromHtml(getResources().getString(R.string.get_permission_for_brightness_manage_desc), Html.FROM_HTML_MODE_LEGACY));
        } else {
            tvMessage.setText("" + Html.fromHtml(getResources().getString(R.string.get_permission_for_brightness_manage_desc)));
        }
        tvNegative.setText("" + getResources().getString(R.string.cancel));
        tvPositive.setText("" + getResources().getString(R.string.okay));

        tvNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                brightnessControlLayout.setVisibility(GONE);
                player.setPlayWhenReady(true);
            }
        });
        tvPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION);
                } else {
                    mPermissionResult.launch(new String[]{Manifest.permission.WRITE_SETTINGS});
                }
            }
        });
        dialog.show();
    }

    private void initBrightness() {
        if (cResolverBritness != null) {
            try {
                // To handle the auto
                Settings.System.putInt(cResolverBritness, Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                // Get the current system brightness
                brightness = Settings.System.getInt(cResolverBritness, Settings.System.SCREEN_BRIGHTNESS);
                Log.e("==>brightness", "" + brightness);
                brightnessSeekbar.setProgress(brightness);
            } catch (Settings.SettingNotFoundException e) {
                // Throw an error case it couldn't be retrieved
                Log.e("Error", "Cannot access system brightness");
                e.printStackTrace();
            }
        }
    }

    private void updateBrightness(int brightnessProgress) {
        // Set the system brightness using the brightness variable value
        Settings.System.putInt(cResolverBritness, Settings.System.SCREEN_BRIGHTNESS, brightnessProgress);
        // Get the current window attributes
        WindowManager.LayoutParams layoutpars = mWindow.getAttributes();
        // Set the brightness of this window
        Log.e("brightness", "===>> " + brightness);
        layoutpars.screenBrightness = brightness / 255f;
        Log.e("screenBrightness", "===>> " + layoutpars.screenBrightness);
        // Apply attribute changes to this window
        mWindow.setAttributes(layoutpars);
    }

    private final ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(PlayerActivity.this)) {
                    Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
                    brightnessControlLayout.setVisibility(VISIBLE);
                    //do your code
                    initBrightness();
                } else {
                    brightnessControlLayout.setVisibility(GONE);
                }
            } else {
                if (ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
                    brightnessControlLayout.setVisibility(VISIBLE);
                    //do your code
                    initBrightness();
                } else {
                    brightnessControlLayout.setVisibility(GONE);
                }
            }
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(PlayerActivity.this)) {
                    Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
                    brightnessControlLayout.setVisibility(VISIBLE);
                    //do your code
                    initBrightness();
                } else {
                    brightnessControlLayout.setVisibility(GONE);
                }
            } else {
                if (ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
                    brightnessControlLayout.setVisibility(VISIBLE);
                    //do your code
                    initBrightness();
                } else {
                    brightnessControlLayout.setVisibility(GONE);
                }
            }
        } else {
            brightnessControlLayout.setVisibility(GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.System.canWrite(PlayerActivity.this)) {
                    Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
                    brightnessControlLayout.setVisibility(VISIBLE);
                    //do your code
                    initBrightness();
                } else {
                    brightnessControlLayout.setVisibility(GONE);
                }
            } else {
                if (ContextCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("TAG", "MainActivity.CODE_WRITE_SETTINGS_PERMISSION success");
                    brightnessControlLayout.setVisibility(VISIBLE);
                    //do your code
                    initBrightness();
                } else {
                    brightnessControlLayout.setVisibility(GONE);
                }
            }
        } else {
            brightnessControlLayout.setVisibility(GONE);
        }
    }

    private void controlFullScreenPlayer() {
        Log.e("isFullScr", "" + isFullScr);
        if (isFullScr) {
            isFullScr = false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
            // reset the orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        } else {
            isFullScr = true;
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            // reset the orientation
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        simpleExoPlayerView.onResume();
    }

    private void setPlayerNormalScreen() {
        lPlay.setVisibility(GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //close embed link playing
        Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
        intent.putExtra("vType", vType);
        intent.putExtra("itemID", itemID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, playerHeight));
    }

    private void setPlayerFullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        lPlay.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    /* Set Player */
    private void initVideoPlayer(String url, String type) {
        setPlayerFullScreen();
        imgSubtitle.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        liveTv.setVisibility(GONE);
        exoDuration.setVisibility(VISIBLE);

        if (player != null) {
            player.stop();
            player.release();
        }

        playerLayout.setVisibility(VISIBLE);
        exoplayerLayout.setVisibility(VISIBLE);

        // Create the MediaItem to play, specifying the content URI and ad tag URI.
        Log.e("=>url", "" + url);
        Log.e("==>>>videoFrom", "" + videoFrom);

        trackSelector = new DefaultTrackSelector(PlayerActivity.this);
        cacheDataSourceFactory = new CacheDataSourceFactory(MyApp.simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(PlayerActivity.this, "" + getResources().getString(R.string.app_name))), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        if (videoFrom.equalsIgnoreCase("Download")) {
            File fileWithinMyDir = new File(url);
            fileWithinMyDir.setReadable(true, false);
            contentVideoUri = Uri.parse(fileWithinMyDir.getAbsolutePath());
            Log.e("=>contentVideoUri", "" + contentVideoUri);

            Log.e(TAG, "secretKey :==> " + secretKey);
            //decrypting and playing the Video
            dataSourceFactory = new CustomDataSourceFactory(PlayerActivity.this, cacheDataSourceFactory, secretKey);
            progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory).createMediaSource(MediaItem.fromUri(contentVideoUri));

            player = new SimpleExoPlayer.Builder(PlayerActivity.this).setTrackSelector(trackSelector).build();
            Log.e("PlayerActivity", "progressiveMediaSource :--> " + progressiveMediaSource);

            player.setMediaSource(progressiveMediaSource);
        } else {
            contentVideoUri = Uri.parse(url);
            Log.e(TAG, "contentVideoUri :==> " + contentVideoUri);

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
            renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
            player = new SimpleExoPlayer.Builder(PlayerActivity.this, renderersFactory).setTrackSelector(trackSelector).build();

            if (url.contains("m3u8")) {
                dataSourceFactory = new DefaultDataSourceFactory(this, getString(R.string.app_name));
                hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(contentVideoUri));

                player.setMediaSource(hlsMediaSource);
            } else {
                // Set up the factory for media sources, passing the ads loader and ad view providers.
                dataSourceFactory = new DefaultDataSourceFactory(PlayerActivity.this, Util.getUserAgent(this, getString(R.string.app_name)));
                progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory).createMediaSource(MediaItem.fromUri(contentVideoUri));

                player.setMediaSource(progressiveMediaSource);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MOVIE).build();
            player.setAudioAttributes(audioAttributes, true);
        }

        simpleExoPlayerView.setPlayer(player);

        if (videoFrom.equalsIgnoreCase("Download")) {
            player.prepare();
        } else if (videoFrom.equalsIgnoreCase("Continue")) {
            player.seekTo(resumePosition);
            player.prepare();
        } else {
            player.prepare();
        }

        Log.e("type", "==>>>> " + type);
        //add listener to player
        if (player != null) {
            player.addListener(playerListener);
        }

        player.setPlayWhenReady(true);
    }

    /* Set Player for Episodes */
    private void initEpisodePlayer(int position) {
        setPlayerFullScreen();
        imgSubtitle.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        liveTv.setVisibility(GONE);
        exoDuration.setVisibility(VISIBLE);

        if (player != null) {
            player.stop();
            player.release();
        }

        playerLayout.setVisibility(VISIBLE);
        exoplayerLayout.setVisibility(VISIBLE);

        // Create the MediaItem to play, specifying the content URI and ad tag URI.

        Uri contentUri = null;
        Log.e("==>>>videoFrom", "" + videoFrom);

        trackSelector = new DefaultTrackSelector(PlayerActivity.this);
        cacheDataSourceFactory = new CacheDataSourceFactory(MyApp.simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(PlayerActivity.this, "" + getResources().getString(R.string.app_name))), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        if (videoFrom.equalsIgnoreCase("EpisodeDownload") && !TextUtils.isEmpty(episodeItemList.get(position).getVideoPath())) {
            Log.e("=>VideoPath", "" + episodeItemList.get(position).getVideoPath());
            Log.e("=>Video", "" + episodeItemList.get(position).getVideo());
            File fileWithinMyDir = new File(episodeItemList.get(position).getVideoPath());
            fileWithinMyDir.setReadable(true, false);
            contentUri = Uri.parse(fileWithinMyDir.getAbsolutePath());
            Log.e("=>contentUri", "" + contentUri);

            Log.e(TAG, "secretKey :==> " + secretKey);
            //decrypting and playing the Video
            DataSource.Factory factory = new CustomDataSourceFactory(PlayerActivity.this, cacheDataSourceFactory, secretKey);
            progressiveMediaSource = new ProgressiveMediaSource.Factory(factory, extractorsFactory).createMediaSource(MediaItem.fromUri(contentUri));

            player = new SimpleExoPlayer.Builder(PlayerActivity.this).setTrackSelector(trackSelector).build();
            Log.e("PlayerActivity", "progressiveMediaSource :--> " + progressiveMediaSource);

            player.setMediaSource(progressiveMediaSource);
        } else {
            String epiURL = "";
            if (videoFrom.equalsIgnoreCase("EpisodeDownload") && TextUtils.isEmpty(episodeItemList.get(position).getVideoPath())) {
                Log.e("EpisodeDownload", "Video :=> " + episodeItemList.get(position).getVideo());
                epiURL = episodeItemList.get(position).getVideo();
            } else if (videoFrom.equalsIgnoreCase("TvShow")) {
                Log.e("TvShow", "Video :=> " + tvShowEpisodeList.get(position).getVideo320());
                epiURL = tvShowEpisodeList.get(position).getVideo320();
            }
            contentUri = Uri.parse(epiURL);
            Log.e(TAG, "contentUri :==> " + contentUri);

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
            renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
            player = new SimpleExoPlayer.Builder(PlayerActivity.this, renderersFactory).setTrackSelector(trackSelector).build();

            Log.e("===========", "epiURL===========> " + epiURL);
            // Set up the factory for media sources, passing the ads loader and ad view providers.
            if (epiURL.contains("m3u8")) {
                dataSourceFactory = new DefaultDataSourceFactory(this, getString(R.string.app_name));
                hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(contentUri));

                player.setMediaSource(hlsMediaSource);
            } else {
                dataSourceFactory = new DefaultDataSourceFactory(PlayerActivity.this, Util.getUserAgent(this, getString(R.string.app_name)));
                progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory).createMediaSource(MediaItem.fromUri(contentUri));

                player.setMediaSource(progressiveMediaSource);
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MOVIE).build();
            player.setAudioAttributes(audioAttributes, true);
        }
        simpleExoPlayerView.setPlayer(player);
        if (resumePosition > 0) {
            player.seekTo(resumePosition);
        }
        player.prepare();

        //add listener to player
        if (player != null) {
            player.addListener(playerListener);
        }

        player.setPlayWhenReady(true);
    }

    /* Set Live Streaming */
    private void initLiveStreaming(String url, String type) {
        setPlayerFullScreen();
        imgSubtitle.setVisibility(GONE);
        progressBar.setVisibility(VISIBLE);
        liveTv.setVisibility(VISIBLE);
        exoDuration.setVisibility(GONE);

        if (player != null) {
            player.stop();
            player.release();
        }

        playerLayout.setVisibility(VISIBLE);
        exoplayerLayout.setVisibility(VISIBLE);

        // Create the MediaItem to play, specifying the content URI and ad tag URI.
        Log.e("=>url", "" + url);

        Uri contentUri = Uri.parse(url);
        Log.e("==>>>videoFrom", "" + videoFrom);

        trackSelector = new DefaultTrackSelector(PlayerActivity.this);
        cacheDataSourceFactory = new CacheDataSourceFactory(MyApp.simpleCache, new DefaultHttpDataSourceFactory(Util.getUserAgent(PlayerActivity.this, "" + getResources().getString(R.string.app_name))), CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        Log.e(TAG, "contentUri :==> " + contentUri);

        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        renderersFactory.setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        player = new SimpleExoPlayer.Builder(PlayerActivity.this, renderersFactory).setTrackSelector(trackSelector).build();

        // Set up the factory for media sources, passing the ads loader and ad view providers.
        if (url.contains("m3u8")) {
            dataSourceFactory = new DefaultDataSourceFactory(this, getString(R.string.app_name));
            hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(contentUri));

            player.setMediaSource(hlsMediaSource);
        } else {
            dataSourceFactory = new DefaultDataSourceFactory(PlayerActivity.this, Util.getUserAgent(this, getString(R.string.app_name)));
            progressiveMediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory, extractorsFactory).createMediaSource(MediaItem.fromUri(contentUri));

            player.setMediaSource(progressiveMediaSource);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.CONTENT_TYPE_MOVIE).build();
            player.setAudioAttributes(audioAttributes, true);
        }

        simpleExoPlayerView.setPlayer(player);
        player.prepare();

        Log.e("type", "==>>>> " + type);
        //add listener to player
        if (player != null) {
            player.addListener(playerListener);
        }

        player.setPlayWhenReady(true);
    }

    private void setNextEpisodeUI() {
        if (epiPosition == (videoFrom.equalsIgnoreCase("EpisodeDownload") ? (episodeItemList.size() - 1) : (tvShowEpisodeList.size() - 1))) {
            epiPosition = 0;
            txtPlayNext.setText("" + getResources().getString(R.string.replay_all));
        } else {
            epiPosition++;
            txtPlayNext.setText("" + getResources().getString(R.string.next));
        }
        if (!TextUtils.isEmpty(videoFrom.equalsIgnoreCase("EpisodeDownload") ? episodeItemList.get(epiPosition).getLandscape() : tvShowEpisodeList.get(epiPosition).getLandscape())) {
            Picasso.get().load(videoFrom.equalsIgnoreCase("EpisodeDownload") ? episodeItemList.get(epiPosition).getLandscape() : tvShowEpisodeList.get(epiPosition).getLandscape()).placeholder(R.drawable.no_image_land).into(ivNextEpiPoster);
        } else if (!TextUtils.isEmpty(videoFrom.equalsIgnoreCase("EpisodeDownload") ? episodeItemList.get(epiPosition).getThumbnail() : tvShowEpisodeList.get(epiPosition).getThumbnail())) {
            Picasso.get().load(videoFrom.equalsIgnoreCase("EpisodeDownload") ? episodeItemList.get(epiPosition).getThumbnail() : tvShowEpisodeList.get(epiPosition).getThumbnail()).placeholder(R.drawable.no_image_land).into(ivNextEpiPoster);
        } else {
            Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(ivNextEpiPoster);
        }
        Log.e("epiPosition", "NEW ==> " + epiPosition);
        secretKey = "" + (videoFrom.equalsIgnoreCase("EpisodeDownload") ? episodeItemList.get(epiPosition).getSecretKey() : "");
        Log.e("secretKey", "NEW ==> " + secretKey);
    }

    private Handler handler = new Handler();
    private Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onTimelineChanged(Timeline timeline, int reason) {
            Log.e("WindowCount", "==>>> " + timeline.getWindowCount());
            Log.e("reason", "==>>> " + reason);
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Log.e("playbackState", "" + playbackState);
            Log.e("playWhenReady", "" + playWhenReady);
            ivExoReplay.setVisibility(GONE);
            if (playWhenReady && playbackState == Player.STATE_READY) {
                isFinish = false;
                isPlaying = true;
                progressBar.setVisibility(GONE);
                ivThumb.setVisibility(GONE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (player != null) {
                            playerCurrentPosition = player.getCurrentPosition();
                            mediaDuration = player.getDuration();
                        }
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);

            } else if (playbackState == Player.STATE_READY) {
                progressBar.setVisibility(GONE);
                ivThumb.setVisibility(GONE);
                isPlaying = false;
                Log.e(TAG, "playWhenReady ===>>> " + playWhenReady);

            } else if (playbackState == Player.STATE_BUFFERING) {
                isPlaying = false;
                //progressBar.setVisibility(VISIBLE);
                //ivThumb.setVisibility(VISIBLE);
            } else if (playbackState == Player.STATE_ENDED) {
                isFinish = true;
                isPlaying = false;
                Log.e("STATE_ENDED", "CurrentPosition => " + player.getCurrentPosition());
                Log.e(TAG, "isPlaying ===>>> " + player.isPlaying());
                Log.e(TAG, "getPlayWhenReady ===>>> " + player.getPlayWhenReady());

                if (videoFrom.equalsIgnoreCase("EpisodeDownload")) {
                    ivExoReplay.setVisibility(GONE);
                    contentDetails.setVisibility(GONE);
                    rlNextEpisode.setVisibility(VISIBLE);
                    if (!player.isPlaying() && !player.getPlayWhenReady()) {
                        setNextEpisodeUI();
                    }
                } else {
                    contentDetails.setVisibility(VISIBLE);
                    ivExoReplay.setVisibility(VISIBLE);
                    rlNextEpisode.setVisibility(GONE);
                }
                player.setPlayWhenReady(false);
                //---delete into continueWatching---//
                if (!videoFrom.equalsIgnoreCase("LiveChannel") && !videoFrom.equalsIgnoreCase("Trailer") && !videoFrom.equalsIgnoreCase("Download") && !videoFrom.equalsIgnoreCase("EpisodeDownload")) {
                    Utility.removeFromContinueWatch(PlayerActivity.this, "" + (videoFrom.equalsIgnoreCase("TvShow") ? ("" + tvShowEpisodeList.get(epiPosition).getId()) : itemID), "" + vType);
                }

            } else {
                // player paused in any state
                isPlaying = false;
                playerCurrentPosition = player.getCurrentPosition();
                mediaDuration = player.getDuration();
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            isPlaying = false;
            //progressBar.setVisibility(VISIBLE);
            ivThumb.setVisibility(VISIBLE);
        }
    };

    /* add_continue_watching API */
    private void AddToContinue() {
        if (!TextUtils.isEmpty((videoFrom.equalsIgnoreCase("TvShow") ? ("" + tvShowEpisodeList.get(epiPosition).getId()) : itemID))) {
            Call<SuccessModel> call = BaseURL.getVideoAPI().add_continue_watching("" + prefManager.getLoginId(), "" + (videoFrom.equalsIgnoreCase("TvShow") ? ("" + tvShowEpisodeList.get(epiPosition).getId()) : itemID), "" + vType, "" + playerCurrentPosition);
            call.enqueue(new Callback<SuccessModel>() {
                @Override
                public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                    try {
                        Log.e("add_continue_watching", "Status => " + response.body().getStatus());
                        if (response.code() == 200 && response.body().getStatus() == 200) {
                            Log.e("add_continue_watching", "Message => " + response.body().getMessage());
                        }
                    } catch (Exception e) {
                        Log.e("add_continue_watching", "Exception => " + e);
                    }
                }

                @Override
                public void onFailure(Call<SuccessModel> call, Throwable t) {
                    Log.e("add_continue_watching", "onFailure => " + t.getMessage());
                }
            });
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!videoFrom.equalsIgnoreCase("LiveChannel") && !videoFrom.equalsIgnoreCase("Trailer") && !videoFrom.equalsIgnoreCase("Download") && !videoFrom.equalsIgnoreCase("EpisodeDownload")) {
            Log.e("playerCurrentPosition", "==>>> " + playerCurrentPosition);
            if (playerCurrentPosition > 0 && !isFinish) {
                AddToContinue();
            }
        }

        if (isPlaying && player != null) {
            player.setPlayWhenReady(false);
            resumePosition = playerCurrentPosition;
            Log.e("resumePosition", "==>>> " + resumePosition);
        }
        if (Util.SDK_INT <= 23) {
            if (simpleExoPlayerView != null) {
                simpleExoPlayerView.onPause();
            }
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            if (Util.SDK_INT <= 23) {
                if (simpleExoPlayerView != null) {
                    simpleExoPlayerView.onPause();
                }
                releasePlayer();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        adsLoader.release();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        Log.e("isFullScr", "" + isFullScr);
        if (isFullScr) {
            controlFullScreenPlayer();
        } else {
            releasePlayer();
            super.onBackPressed();
        }
    }

    public void releasePlayer() {
        if (player != null) {
            adsLoader.setPlayer(null);
            player.setPlayWhenReady(false);
            player.stop();
            player.release();
            player = null;
            simpleExoPlayerView.setPlayer(null);
            //simpleExoPlayerView = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
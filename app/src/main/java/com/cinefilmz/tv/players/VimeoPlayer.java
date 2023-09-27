package com.cinefilmz.tv.players;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.cinefilmz.tv.Model.SuccessModel.SuccessModel;
import com.cinefilmz.tv.Model.VideoSeasonModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.example.vimeoplayer2.UniversalMediaController;
import com.example.vimeoplayer2.UniversalVideoView;
import com.example.vimeoplayer2.vimeoextractor.OnVimeoExtractionListener;
import com.example.vimeoplayer2.vimeoextractor.VimeoExtractor;
import com.example.vimeoplayer2.vimeoextractor.VimeoVideo;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VimeoPlayer extends AppCompatActivity implements View.OnClickListener, UniversalVideoView.VideoViewCallback {

    private PrefManager prefManager;
    private static final String TAG = VimeoPlayer.class.getSimpleName();

    private UniversalVideoView mVideoView;
    private UniversalMediaController mMediaController;

    private View mBottomLayout;
    private View mVideoLayout;

    private List<Result> tvShowEpisodeList;

    private String videoFrom = "", vType = "", itemID = "", catID = "", URL, title, typeID = "", thumbnailImg;
    private boolean isFullscreen, isPlaying, isFinish = false;
    private int epiPosition = 0, mSeekPosition = 0, cachedHeight, playerCurrentPosition = 0, mediaDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.HideNavigation(this);
        setContentView(R.layout.activity_vimeoplayer);
        PrefManager.forceRTLIfSupported(getWindow(), this);
        MultiDex.install(this);
        prefManager = new PrefManager(this);

        initViews();
        setVideoAreaSize();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoFrom = bundle.getString("videoFrom");
            Log.e("videoFrom", "==>>> " + videoFrom);

            if (videoFrom.equalsIgnoreCase("TvShow")) {
                itemID = bundle.getString("itemID");
                title = bundle.getString("title");
                vType = bundle.getString("vType");
                typeID = bundle.getString("typeID");
                tvShowEpisodeList = new ArrayList<>();
                tvShowEpisodeList = (List<Result>) bundle.getSerializable("episodeList");
                epiPosition = bundle.getInt("position");

                Log.e("resumeFrom", "==>>> " + bundle.getInt("resumeFrom"));
                if (bundle.getInt("resumeFrom") != 0 && bundle.getInt("resumeFrom") > 0) {
                    mSeekPosition = bundle.getInt("resumeFrom");
                    Log.e("mSeekPosition", "==>>> " + mSeekPosition);
                }
                Log.e("itemID", "==>>> " + itemID);
                Log.e("title", "==>>> " + title);
                Log.e("vType", "==>>> " + vType);
                Log.e("typeID", "==>>> " + typeID);
                Log.e(TAG, "epiPosition ==> " + epiPosition);
                Log.e(TAG, "tvShowEpisodeList ==> " + tvShowEpisodeList.size());

                initEpisodeVimeoPlayer(epiPosition);

            } else {
                itemID = bundle.getString("itemID");
                URL = bundle.getString("url");
                thumbnailImg = bundle.getString("image");
                title = bundle.getString("title");
                vType = bundle.getString("vType");
                catID = bundle.getString("catId");

                if (videoFrom != null && !TextUtils.isEmpty(videoFrom)) {
                    if (videoFrom.equalsIgnoreCase("Continue")) {
                        mSeekPosition = bundle.getInt("resumeFrom");
                        Log.e("mSeekPosition", "==>>> " + mSeekPosition);
                    }
                }

                Log.e("URL", "==>>> " + URL);
                Log.e("itemID", "==>>> " + itemID);
                Log.e("thumbnailImg", "==>>> " + thumbnailImg);
                Log.e("title", "==>>> " + title);
                Log.e("vType", "==>>> " + vType);
                Log.e("catID", "==>>> " + catID);

                initVimeoPlayer();
            }
        }
    }

    private void initViews() {
        try {
            mVideoLayout = findViewById(R.id.video_layout);
            mBottomLayout = findViewById(R.id.bottom_layout);
            mVideoView = findViewById(R.id.videoView);
            mMediaController = findViewById(R.id.media_controller);
            mVideoView.setMediaController(mMediaController);
            mVideoView.setVideoViewCallback(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check vpn connection
        if (mVideoView != null) {
            if (videoFrom.equalsIgnoreCase("Continue")) {
                try {
                    if (mSeekPosition > 0) {
                        Log.e("mSeekPosition", "==>>>> " + mSeekPosition);
                        mVideoView.seekTo(mSeekPosition);
                        mVideoView.resume();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    if (playerCurrentPosition > 0) {
                        mVideoView.seekTo(playerCurrentPosition);
                        mVideoView.resume();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initVimeoPlayer() {
        try {
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.d(TAG, "onCompletion ");
                    Utility.removeFromContinueWatch(VimeoPlayer.this, "" + itemID, "" + vType);
                }
            });

            VimeoExtractor.getInstance().fetchVideoWithURL(URL, "" + title, new OnVimeoExtractionListener() {
                @Override
                public void onSuccess(final VimeoVideo video) {
                    Log.e(TAG, "onSuccess Title ===> " + video.getTitle());
                    Log.e(TAG, "onSuccess Duration ===> " + video.getDuration());
                    String hdStream = null;
                    for (String key : video.getStreams().keySet()) {
                        hdStream = key;
                    }
                    final String hdStreamuRL = video.getStreams().get(hdStream);
                    if (hdStream != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Start the MediaController
                                mVideoView.setMediaController(mMediaController);
                                // Get the URL from String VideoURL
                                Uri video = Uri.parse(hdStreamuRL);

                                mVideoView.setVideoURI(video);
                                if (mSeekPosition > 0) {
                                    mVideoView.seekTo(mSeekPosition);
                                }
                                mVideoView.start();
                                mMediaController.setTitle("" + title);
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(TAG, "onFailure Throwable ===> " + throwable.getMessage());
                    //Error handling here
                }
            });

        } catch (Exception e) {
            Log.e("initVimeoPlayer", "Exception ======> " + e);
        }
    }

    private void initEpisodeVimeoPlayer(int epiPos) {
        Log.e(TAG, "epiPos =====> " + epiPos);
        Log.e(TAG, "videoFrom ========> " + videoFrom);
        try {
            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isFinish = true;
                    Log.d(TAG, "onCompletion ");
                    Utility.removeFromContinueWatch(VimeoPlayer.this, "" + tvShowEpisodeList.get(epiPosition).getId(),
                            "" + vType);
                    releasePlayer();
                    mSeekPosition = 0;
                    setNextEpisodeUI();
                }
            });

            VimeoExtractor.getInstance().fetchVideoWithURL(tvShowEpisodeList.get(epiPos).getVideo320(), "" + title,
                    new OnVimeoExtractionListener() {
                        @Override
                        public void onSuccess(final VimeoVideo video) {
                            Log.e(TAG, "onSuccess Title ===> " + video.getTitle());
                            Log.e(TAG, "onSuccess Duration ===> " + video.getDuration());
                            String hdStream = null;
                            for (String key : video.getStreams().keySet()) {
                                hdStream = key;
                            }
                            final String hdStreamuRL = video.getStreams().get(hdStream);
                            if (hdStream != null) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Start the MediaController
                                        mVideoView.setMediaController(mMediaController);
                                        // Get the URL from String VideoURL
                                        Uri video = Uri.parse(hdStreamuRL);

                                        mVideoView.setVideoURI(video);
                                        if (mSeekPosition > 0) {
                                            mVideoView.seekTo(mSeekPosition);
                                        }
                                        isFinish = false;
                                        mVideoView.start();
                                        mMediaController.setTitle("" + title);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e(TAG, "onFailure Throwable ===> " + throwable.getMessage());
                            //Error handling here
                        }
                    });

        } catch (Exception e) {
            Log.e("initVimeoPlayer", "Exception ======> " + e);
        }
    }

    private void setNextEpisodeUI() {
        Log.e("setNextEpisodeUI", "epiPosition ===OLD===> " + epiPosition);
        Log.e("setNextEpisodeUI", "tvShowEpisodeList ======> " + tvShowEpisodeList.size());
        if (epiPosition == tvShowEpisodeList.size() - 1) {
            epiPosition = 0;
        } else {
            epiPosition++;
        }
        Log.e("setNextEpisodeUI", "epiPosition ===NEW===> " + epiPosition);
        initEpisodeVimeoPlayer(epiPosition);
    }

    /* add_continue_watching API */
    private void AddToContinue() {
        if (!TextUtils.isEmpty((videoFrom.equalsIgnoreCase("TvShow")
                ? ("" + tvShowEpisodeList.get(epiPosition).getId()) : itemID))) {
            Call<SuccessModel> call = BaseURL.getVideoAPI().add_continue_watching("" + prefManager.getLoginId(),
                    "" + (videoFrom.equalsIgnoreCase("TvShow")
                            ? ("" + tvShowEpisodeList.get(epiPosition).getId()) : itemID),
                    "" + vType, "" + playerCurrentPosition);
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
        if (!videoFrom.equalsIgnoreCase("LiveChannel")
                && !videoFrom.equalsIgnoreCase("Trailer")
                && !videoFrom.equalsIgnoreCase("Download")
                && !videoFrom.equalsIgnoreCase("EpisodeDownload")) {
            if (mVideoView != null && mVideoView.mMediaPlayer != null) {
                if (mVideoView.mMediaPlayer.isPlaying() && mVideoView.mMediaPlayer.getCurrentPosition() > 0) {
                    playerCurrentPosition = mVideoView.mMediaPlayer.getCurrentPosition();
                    Log.e("playerCurrentPosition", "==>>> " + playerCurrentPosition);
                    AddToContinue();
                }
            }
        }

        if (mVideoView != null && isPlaying) {
            mSeekPosition = playerCurrentPosition;
            Log.e("mSeekPosition", "==>>> " + mSeekPosition);

            if (mVideoView.canPause()) {
                mVideoView.pause();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVideoView != null && isPlaying) {
            if (mVideoView.canPause()) {
                mVideoView.pause();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void setVideoAreaSize() {
        mVideoLayout.post(new Runnable() {
            @Override
            public void run() {
                int width = mVideoLayout.getWidth();
                cachedHeight = (int) (width * 405f / 720f);
                ViewGroup.LayoutParams videoLayoutParams = mVideoLayout.getLayoutParams();
                videoLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                videoLayoutParams.height = cachedHeight;
                mVideoLayout.setLayoutParams(videoLayoutParams);
            }
        });
    }

    @Override
    public void onScaleChange(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
        if (isFullscreen) {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.GONE);

        } else {
            ViewGroup.LayoutParams layoutParams = mVideoLayout.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoLayout.setLayoutParams(layoutParams);
            mBottomLayout.setVisibility(View.VISIBLE);
        }

        //switchTitleBar(!isFullscreen);
    }

    private void switchTitleBar(boolean show) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            if (show) {
                supportActionBar.show();
            } else {
                supportActionBar.hide();
            }
        }
    }

    @Override
    public void onPause(MediaPlayer mediaPlayer) {
        Log.e(TAG, "onPause UniversalVideoView callback");
        isPlaying = false;
        playerCurrentPosition = mediaPlayer.getCurrentPosition();
    }

    @Override
    public void onStart(MediaPlayer mediaPlayer) {
        Log.e(TAG, "onStart UniversalVideoView callback");
        mediaDuration = mediaPlayer.getDuration();
        isPlaying = true;
    }

    @Override
    public void onBufferingStart(MediaPlayer mediaPlayer) {
        Log.e(TAG, "onBufferingStart UniversalVideoView callback");
        isPlaying = false;
    }

    @Override
    public void onBufferingEnd(MediaPlayer mediaPlayer) {
        Log.e(TAG, "onBufferingEnd UniversalVideoView callback");
        isPlaying = false;
    }

    public void releasePlayer() {
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView.closePlayer();
        }
    }

    @Override
    public void onBackPressed() {
        if (this.isFullscreen) {
            mVideoView.setFullscreen(false);
        } else {
            super.onBackPressed();
        }
    }

}
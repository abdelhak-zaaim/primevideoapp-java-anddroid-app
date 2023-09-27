package com.cinefilmz.tv.players;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import com.cinefilmz.tv.Model.SuccessModel.SuccessModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YoutubeActivity extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private static final String TAG = YoutubeActivity.class.getSimpleName();
    private YouTubePlayerView youTubePlayerView;

    private String videoFrom = "", videoUrl = "", itemID = "", vType = "", typeID = "", finalID;
    private String[] Video_ID;
    private boolean isFullScr = true;
    private float resumePosition = 0, playerCurrentPosition = 0, mediaDuration = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.HideNavigation(this);
        setContentView(R.layout.activity_youtube);
        PrefManager.forceRTLIfSupported(getWindow(), this);
        MultiDex.install(this);
        prefManager = new PrefManager(this);

        youTubePlayerView = findViewById(R.id.youtube_view);
        // here we are adding observer to our youtubeplayerview.
        getLifecycle().addObserver(youTubePlayerView);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            videoFrom = bundle.getString("videoFrom");
            itemID = bundle.getString("itemID");
            videoUrl = bundle.getString("url");
            vType = bundle.getString("vType");
            resumePosition = bundle.getInt("resumeFrom");
            Log.e("videoFrom", "==>>> " + videoFrom);
            Log.e("itemID", "=====>>> " + itemID);
            Log.e("videoUrl", "===>>> " + videoUrl);
            Log.e("vType", "======>>> " + vType);
            Log.e("typeID", "=====>>> " + typeID);
            Log.e("resumePos", "==>>> " + resumePosition);

            if (!videoUrl.isEmpty()) {
                if (videoUrl.contains("watch")) {
                    Video_ID = videoUrl.split("v=");
                    Log.e("Video_ID[1]", "" + Video_ID[1]);

                    if (Video_ID[1].contains("&")) {
                        Video_ID = Video_ID[1].split("&");
                        finalID = Video_ID[0];
                        Log.e("finalID", "" + Video_ID[0]);
                    } else {
                        finalID = Video_ID[1];
                        Log.e("==>finalID", "" + finalID);
                    }

                } else if (videoUrl.contains("youtu.be")) {
                    Video_ID = videoUrl.split("//youtu.be/");
                    finalID = Video_ID[1];
                    Log.e("finalID", "==>>> " + Video_ID[1]);
                } else {
                    Toasty.error(YoutubeActivity.this, "" + getResources().getString(R.string.invalid_youtube_url), Toasty.LENGTH_LONG).show();
                }
            } else {
                Toasty.error(YoutubeActivity.this, "" + getResources().getString(R.string.invalid_youtube_url), Toasty.LENGTH_LONG).show();
            }
        }

        try {
            initVideoPlayer(videoUrl, "" + videoFrom);
        } catch (NullPointerException e) {
            Log.e("onCreate", "NullPointerException => " + e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
    }

    /* Set Player */
    private void initVideoPlayer(String url, String type) {
        // Create the MediaItem to play, specifying the content URI and ad tag URI.
        Log.e("==>>>URL", "" + url);
        Log.e("==>>>type", "" + type);
        Log.e("==>>>videoFrom", "" + videoFrom);
        if (isFullScr) {
            youTubePlayerView.enterFullScreen();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        AbstractYouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                Log.e("========= onReady", " =========");
                // using pre-made custom ui
                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer,
                        getLifecycle(),
                        finalID,
                        resumePosition
                );
            }

            @Override
            public void onCurrentSecond(@NonNull YouTubePlayer youTubePlayer, float second) {
                super.onCurrentSecond(youTubePlayer, second);
                playerCurrentPosition = second;
            }

            @Override
            public void onVideoDuration(@NonNull YouTubePlayer youTubePlayer, float duration) {
                super.onVideoDuration(youTubePlayer, duration);
                mediaDuration = duration;
            }
        };
        IFramePlayerOptions options = new IFramePlayerOptions.Builder().controls(1).build();
        youTubePlayerView.initialize(listener, true, options);

        youTubePlayerView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                isFullScr = true;
                youTubePlayerView.enterFullScreen();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }

            @Override
            public void onYouTubePlayerExitFullScreen() {
                isFullScr = false;
                youTubePlayerView.exitFullScreen();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        });
    }

    /* add_continue_watching API */
    private void AddToContinue() {
        if (!TextUtils.isEmpty(itemID)) {
            Call<SuccessModel> call = BaseURL.getVideoAPI().add_continue_watching("" + prefManager.getLoginId(),
                    "" + itemID, "" + vType, "" + Long.parseLong("" + playerCurrentPosition));
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
        Utility.ProgressbarHide();
        if (!videoFrom.equalsIgnoreCase("LiveChannel") && !videoFrom.equalsIgnoreCase("Trailer")
                && !videoFrom.equalsIgnoreCase("Download") && !videoFrom.equalsIgnoreCase("EpisodeDownload")) {
            Log.e("playerCurrentPosition", "==>>> " + playerCurrentPosition);
            if (playerCurrentPosition > 0 && playerCurrentPosition < mediaDuration) {
                AddToContinue();
            }
        }

        if (youTubePlayerView != null) {
            resumePosition = playerCurrentPosition;
            Log.e("resumePosition", "==>>> " + resumePosition);
        }
        releasePlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (youTubePlayerView != null) {
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.ProgressbarHide();
        releasePlayer();
    }

    @Override
    public void onBackPressed() {
        Log.e("isFullScr", "" + isFullScr);
        if (isFullScr) {
            youTubePlayerView.exitFullScreen();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            releasePlayer();
            super.onBackPressed();
        }
    }

    public void releasePlayer() {
        if (youTubePlayerView != null) {
            youTubePlayerView.release();
            youTubePlayerView = null;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

}
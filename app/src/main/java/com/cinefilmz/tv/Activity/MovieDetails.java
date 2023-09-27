package com.cinefilmz.tv.Activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cinefilmz.tv.Adapter.CastAndCrewAdapter;
import com.cinefilmz.tv.Adapter.SectionRelatedAdapter;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Utils.PermissionUtils;
import com.cinefilmz.tv.Model.SectionDetailModel.Cast;
import com.cinefilmz.tv.Model.SectionDetailModel.GetRelatedVideo;
import com.cinefilmz.tv.Model.SectionDetailModel.Language;
import com.cinefilmz.tv.Model.SectionDetailModel.MoreDetail;
import com.cinefilmz.tv.Model.SectionDetailModel.Result;
import com.cinefilmz.tv.Model.SectionDetailModel.SectionDetailModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Services.DownloadBroadcastReceiver;
import com.cinefilmz.tv.Services.SecureDownloadService;
import com.cinefilmz.tv.Services.VideoDownloadProgress;
import com.cinefilmz.tv.Utils.ConnectivityReceiver;
import com.cinefilmz.tv.Utils.Constant;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.ads.Ad;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetails extends AppCompatActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener {

    private PrefManager prefManager;
    private static final String TAG = MovieDetails.class.getSimpleName();
    private PermissionUtils takePermissionUtils;

    private ShimmerFrameLayout shimmer;
    private SwipeRefreshLayout swipeRefresh;
    private AppBarLayout appBarLayout;

    /* Tabs */
    private LinearLayout lyTab1, lyTab2, layoutVideoRelatedData, layoutTabMoreDetails;
    private TextView txtTab1, txtTab2;
    private View viewLine1, viewLine2;

    private ImageView ivPoster;
    private RoundedImageView ivPosterThumb;
    private CardView cvWatchNow, cvRentNow, cvWatchTrailer;
    private LinearLayout lyNewReleaseTag, lyFeatureBtns, lyWatchNow, lyWatchTrailer, lyTrailer, lyWatchlist, lyMore, lyOpenLASDialog, lyPrimeTag, lyRentNow, lyRentTag,
            lySubscribeNow, lyNativeAds, lyAdView, lyFbAdView, lyPlayButton, lyReleaseDate;
    private TextView txtNewReleaseTag, txtMainTitle, txtPrimeTag, txtPrimeTagDesc, txtWatchNow, txtRemainWatch, txtDescription,
            txtIMDbRatings, txtReleaseYear, txtDuration, txtAgeLimit, txtMaxVideoQuality, txtCommentIcon, txtAudioSubtitleCount,
            txtTrailerIcon, txtTrailer, txtWatchlistIcon, txtWatchlist, txtMoreIcon, txtMore, txtRentPrice, txtSubscribeNow, txtCurrencySymbol,
            txtReleaseDate;
    private RoundedProgressBar progressRemainWatch;
    private RelativeLayout rlDWithProgress;
    public static TextView txtDownloadStarted, txtDownloadPauseIcon, txtStartOverIcon, txtStartOver, txtDownloadIcon, txtDownload;
    public static CircularProgressBar circularDownloadProgressBar;
    public static LinearLayout lyDownload, lyDownloadStarted, lyStartOver;

    public static Result sectionDetailList;
    private List<Language> languageList;
    private List<GetRelatedVideo> relatedVideoList;
    private List<Cast> castList;
    private List<Cast> directorList;
    private List<MoreDetail> moreDetailsList;

    /* Episode & MoreDetails ****... */
    private LinearLayout lyDetailsDynamic, lyAlsoWatch, lyCastAndCrew, lyDirectors;
    private RecyclerView rvAlsoWatch, rvCastAndCrew;
    private TextView txtSourceOfDetails, txtDirectorName, txtDirectorDesc, txtInteresting, txtNoReviews;
    private RoundedImageView ivDirectors;

    private SectionRelatedAdapter sectionRelatedAdapter;
    private CastAndCrewAdapter castAndCrewAdapter;
    /* ...*****/

    private String itemID = "", videoType = "", typeID = "", upcomingType = "", adTYPE = "", mainKeyAlias = "", clickedTab = "";

    private com.facebook.ads.AdView fbAdView = null;
    private AdView mAdView = null;
    private NativeAdLayout fbNativeTemplate = null;
    private TemplateView nativeTemplate = null;
    private NativeBannerAd fbNativeBannerAd = null;
    private InterstitialAd mInterstitialAd = null;
    private RewardedAd mRewardedAd = null;
    private RewardedInterstitialAd mInterstitialVideoAd = null;
    private com.facebook.ads.InterstitialAd fbInterstitialAd = null;
    private RewardedVideoAd fbRewardedVideoAd = null;

    private DownloadBroadcastReceiver downloadBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moviedetails);
        PrefManager.forceRTLIfSupported(getWindow(), MovieDetails.this);
        takePermissionUtils = new PermissionUtils(MovieDetails.this, mPermissionResult);
        Log.e(TAG, "onCreate called");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            itemID = bundle.getString("ID");
            videoType = bundle.getString("videoType");
            typeID = bundle.getString("typeID");
            upcomingType = bundle.getString("upcomingType");
            Log.e("itemID =>", "" + itemID);
            Log.e("videoType =>", "" + videoType);
            Log.e("typeID =>", "" + typeID);
            Log.e("upcomingType =>", "" + upcomingType);
        }

        init();
        AdInit();

        swipeRefresh.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {
                swipeRefresh.setRefreshing(false);
                lyDetailsDynamic.removeAllViews();
                SectionDetailData();
            }, getResources().getInteger(R.integer.swipeRefreshDelay));
        });

        downloadBroadcastReceiver = new DownloadBroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                Log.e(TAG, "=====DBR===== currentID ======>>> " + Functions.isMyServiceRunning(context, SecureDownloadService.class));
                if (Functions.isMyServiceRunning(context, SecureDownloadService.class)) {
                    if (intent != null) {
                        VideoDownloadProgress videoDownloadProgress = intent.getParcelableExtra("downloadProgress");
                        boolean downloadCompleted = intent.getBooleanExtra("downloadComplete", false);
                        if (!downloadCompleted) {
                            if (videoDownloadProgress.videoId.equalsIgnoreCase(itemID)) {
                                // Post the UI updating code to our HandXler
                                // Post the UI updating code to our Handler
                                Log.e(TAG, "=====DBR===== downloadProgress ======>>> " + videoDownloadProgress);
                                Log.e(TAG, "=====DBR===== downloadCompleted ======>>>>" + downloadCompleted);
                                Log.e(TAG, "=====DBR===== currentProgress ======>>> " + videoDownloadProgress.progress);
                                Log.e(TAG, "=====DBR===== videoId ======>>> " + videoDownloadProgress.videoId);
                                Log.e(TAG, "=====DBR===== currentID ======>>> " + itemID);

                                updateDownloadUI(videoDownloadProgress, downloadCompleted);
                            } else {
                                Log.e(TAG, "=====DBR===== !downloadCompleted ======>>>>" + downloadCompleted);
                                lyDownload.setVisibility(View.VISIBLE);
                                lyDownloadStarted.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e(TAG, "=====DBR===== downloadCompleted ======>>>>" + downloadCompleted);
                            lyDownload.setVisibility(View.VISIBLE);
                            lyDownloadStarted.setVisibility(View.GONE);
                            if (videoDownloadProgress != null && videoDownloadProgress.videoId.equalsIgnoreCase(itemID)) {
                                updateDownloadUI(videoDownloadProgress, downloadCompleted);
                            }
                        }
                    } else {
                        lyDownload.setVisibility(View.VISIBLE);
                        lyDownloadStarted.setVisibility(View.GONE);
                    }
                } else {
                    lyDownload.setVisibility(View.VISIBLE);
                    lyDownloadStarted.setVisibility(View.GONE);
                }
            }
        };
    }

    private void init() {
        try {
            prefManager = new PrefManager(MovieDetails.this);

            shimmer = findViewById(R.id.shimmer);
            appBarLayout = findViewById(R.id.appBarLayout);
            swipeRefresh = findViewById(R.id.swipeRefresh);

            lyTab1 = findViewById(R.id.lyTab1);
            lyTab2 = findViewById(R.id.lyTab2);
            txtTab1 = findViewById(R.id.txtTab1);
            txtTab2 = findViewById(R.id.txtTab2);
            viewLine1 = findViewById(R.id.viewLine1);
            viewLine2 = findViewById(R.id.viewLine2);
            layoutVideoRelatedData = findViewById(R.id.layoutVideoRelatedData);
            layoutTabMoreDetails = findViewById(R.id.layoutTabMoreDetails);

            ivPoster = findViewById(R.id.ivPoster);
            ivPosterThumb = findViewById(R.id.ivPosterThumb);
            progressRemainWatch = findViewById(R.id.progressRemainWatch);

            cvWatchNow = findViewById(R.id.cvWatchNow);
            cvWatchTrailer = findViewById(R.id.cvWatchTrailer);
            cvRentNow = findViewById(R.id.cvRentNow);

            lyAdView = findViewById(R.id.lyAdView);
            lyFbAdView = findViewById(R.id.lyFbAdView);
            lyNativeAds = findViewById(R.id.lyNativeAds);
            nativeTemplate = findViewById(R.id.nativeTemplate);
            fbNativeTemplate = findViewById(R.id.fbNativeTemplate);

            lyPlayButton = findViewById(R.id.lyPlayButton);
            lyNewReleaseTag = findViewById(R.id.lyNewReleaseTag);
            lyWatchNow = findViewById(R.id.lyWatchNow);
            lyWatchTrailer = findViewById(R.id.lyWatchTrailer);
            lyFeatureBtns = findViewById(R.id.lyFeatureBtns);
            lyTrailer = findViewById(R.id.lyTrailer);
            lyStartOver = findViewById(R.id.lyStartOver);
            lyDownload = findViewById(R.id.lyDownload);
            lyDownloadStarted = findViewById(R.id.lyDownloadStarted);
            lyWatchlist = findViewById(R.id.lyWatchlist);
            lyMore = findViewById(R.id.lyMore);
            lyOpenLASDialog = findViewById(R.id.lyOpenLASDialog);
            lyReleaseDate = findViewById(R.id.lyReleaseDate);
            lyPrimeTag = findViewById(R.id.lyPrimeTag);
            lyRentNow = findViewById(R.id.lyRentNow);
            lyRentTag = findViewById(R.id.lyRentTag);
            lySubscribeNow = findViewById(R.id.lySubscribeNow);

            txtNewReleaseTag = findViewById(R.id.txtNewReleaseTag);
            txtMainTitle = findViewById(R.id.txtMainTitle);
            txtReleaseDate = findViewById(R.id.txtReleaseDate);
            txtPrimeTag = findViewById(R.id.txtPrimeTag);
            txtPrimeTagDesc = findViewById(R.id.txtPrimeTagDesc);
            txtWatchNow = findViewById(R.id.txtWatchNow);
            txtRemainWatch = findViewById(R.id.txtRemainWatch);
            txtDescription = findViewById(R.id.txtDescription);
            txtIMDbRatings = findViewById(R.id.txtIMDbRatings);
            txtReleaseYear = findViewById(R.id.txtReleaseYear);
            txtDuration = findViewById(R.id.txtDuration);
            txtAgeLimit = findViewById(R.id.txtAgeLimit);
            txtMaxVideoQuality = findViewById(R.id.txtMaxVideoQuality);
            txtCommentIcon = findViewById(R.id.txtCommentIcon);
            txtAudioSubtitleCount = findViewById(R.id.txtAudioSubtitleCount);
            txtTrailerIcon = findViewById(R.id.txtTrailerIcon);
            txtTrailer = findViewById(R.id.txtTrailer);
            txtStartOverIcon = findViewById(R.id.txtStartOverIcon);
            txtStartOver = findViewById(R.id.txtStartOver);
            txtDownloadIcon = findViewById(R.id.txtDownloadIcon);
            txtDownload = findViewById(R.id.txtDownload);
            txtDownloadStarted = findViewById(R.id.txtDownloadStarted);
            txtDownloadPauseIcon = findViewById(R.id.txtDownloadPauseIcon);
            txtWatchlistIcon = findViewById(R.id.txtWatchlistIcon);
            txtWatchlist = findViewById(R.id.txtWatchlist);
            txtMoreIcon = findViewById(R.id.txtMoreIcon);
            txtMore = findViewById(R.id.txtMore);
            txtRentPrice = findViewById(R.id.txtRentPrice);
            txtCurrencySymbol = findViewById(R.id.txtCurrencySymbol);
            txtSubscribeNow = findViewById(R.id.txtSubscribeNow);

            rlDWithProgress = findViewById(R.id.rlDWithProgress);
            circularDownloadProgressBar = findViewById(R.id.downloadProgress);

            /* Related & MoreDetails Init */
            lyDetailsDynamic = findViewById(R.id.lyDetailsDynamic);
            lyAlsoWatch = findViewById(R.id.lyAlsoWatch);
            lyCastAndCrew = findViewById(R.id.lyCastAndCrew);
            lyDirectors = findViewById(R.id.lyDirectors);
            rvAlsoWatch = findViewById(R.id.rvAlsoWatch);
            rvCastAndCrew = findViewById(R.id.rvCastAndCrew);
            ivDirectors = findViewById(R.id.ivDirectors);
            txtSourceOfDetails = findViewById(R.id.txtSourceOfDetails);
            txtDirectorName = findViewById(R.id.txtDirectorName);
            txtDirectorDesc = findViewById(R.id.txtDirectorDesc);

            lyDirectors.setOnClickListener(this);
            /* Related & MoreDetails Init */

            lyTab1.setOnClickListener(this);
            lyTab2.setOnClickListener(this);
            lyPlayButton.setOnClickListener(this);
            lyWatchNow.setOnClickListener(this);
            lyWatchTrailer.setOnClickListener(this);
            lySubscribeNow.setOnClickListener(this);
            lyRentNow.setOnClickListener(this);
            lyTrailer.setOnClickListener(this);
            lyStartOver.setOnClickListener(this);
            lyDownload.setOnClickListener(this);
            lyWatchlist.setOnClickListener(this);
            lyMore.setOnClickListener(this);
            lyOpenLASDialog.setOnClickListener(this);
            txtDescription.setOnClickListener(this);
            txtCommentIcon.setOnClickListener(this);
        } catch (Exception e) {
            Log.e(TAG, "init Exception => " + e);
        }
    }

    private void AdInit() {
        Log.e("banner_ad", "" + prefManager.getValue("banner_ad"));
        if (prefManager.getValue("banner_ad").equalsIgnoreCase("1")) {
            lyAdView.setVisibility(View.VISIBLE);
            Utility.Admob(MovieDetails.this, mAdView, "" + prefManager.getValue("banner_adid"), lyAdView);
        } else {
            lyAdView.setVisibility(View.GONE);
        }

        Log.e("fb_banner_status", "" + prefManager.getValue("fb_banner_status"));
        if (prefManager.getValue("fb_banner_status").equalsIgnoreCase("1")) {
            lyFbAdView.setVisibility(View.VISIBLE);
            Utility.FacebookBannerAd(MovieDetails.this, fbAdView, "" + prefManager.getValue("fb_banner_id"), lyFbAdView);
        } else {
            lyFbAdView.setVisibility(View.GONE);
        }

        Log.e("fb_native_status", "" + prefManager.getValue("fb_native_status"));
        Log.e("native_ad", "" + prefManager.getValue("native_ad"));

        if (prefManager.getValue("fb_native_status").equalsIgnoreCase("1")) {
            lyNativeAds.setVisibility(View.VISIBLE);
            nativeTemplate.setVisibility(View.VISIBLE);
            fbNativeTemplate.setVisibility(View.VISIBLE);
            Utility.FacebookNativeAdSmall(MovieDetails.this, fbNativeBannerAd, fbNativeTemplate, "" + prefManager.getValue("fb_native_id"));
        } else if (prefManager.getValue("native_ad").equalsIgnoreCase("1")) {
            lyNativeAds.setVisibility(View.VISIBLE);
            nativeTemplate.setVisibility(View.VISIBLE);
            fbNativeTemplate.setVisibility(View.GONE);
            Utility.NativeAds(MovieDetails.this, nativeTemplate, "" + prefManager.getValue("native_adid"));
        } else {
            lyNativeAds.setVisibility(View.GONE);
            nativeTemplate.setVisibility(View.GONE);
            fbNativeTemplate.setVisibility(View.GONE);
        }
    }

    private void FullScreenAdInit() {
        Log.e("reward_ad", "" + prefManager.getValue("reward_ad"));
        if (prefManager.getValue("reward_ad").equalsIgnoreCase("1")) {
            mRewardedAd = null;
            RewardedVideoAd();
        }

        Log.e("fb_rewardvideo_status", "" + prefManager.getValue("fb_rewardvideo_status"));
        if (prefManager.getValue("fb_rewardvideo_status").equalsIgnoreCase("1")) {
            fbRewardedVideoAd = null;
            FacebookRewardAd();
        }

        Log.e("interstital_ad", "" + prefManager.getValue("interstital_ad"));
        if (prefManager.getValue("interstital_ad").equalsIgnoreCase("1")) {
            mInterstitialAd = null;
            InterstitialAd();
        }

        Log.e("interstitalvideo_ad", "" + prefManager.getValue("interstitalvideo_ad"));
        if (prefManager.getValue("interstitalvideo_ad").equalsIgnoreCase("1")) {
            mInterstitialVideoAd = null;
            InterstitialVideoAd();
        }

        Log.e("fb_interstiatial_status", "" + prefManager.getValue("fb_interstiatial_status"));
        if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("1")) {
            fbInterstitialAd = null;
            FacebookInterstitialAd();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        swipeRefresh.setEnabled(verticalOffset == 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyTab1:
                setTabOnClick("Related");
                break;

            case R.id.lyTab2:
                setTabOnClick("More");
                break;

            case R.id.lyDirectors:
                if (directorList.size() > 0) {
                    Utility.pushCastCrewDetailsF(MovieDetails.this, "" + directorList.get(0).getId());
                }
                break;

            case R.id.lyWatchNow:
                if (ConnectivityReceiver.isConnected()) {
                    if (Utility.checkLoginUser(MovieDetails.this)) {
                        if (sectionDetailList != null) {
                            OpenPlayer("WATCHNOW");
                        }
                    }
                } else {
                    Utility.showSnackBar(MovieDetails.this, "NoInternet", "");
                }
                break;

            case R.id.lySubscribeNow:
                if (Utility.checkLoginUser(MovieDetails.this)) {
                    Utility.openSubscription(MovieDetails.this);
                }
                break;

            case R.id.lyRentNow:
                if (Utility.checkLoginUser(MovieDetails.this)) {
                    if (sectionDetailList != null) {
                        Utility.paymentForRent(MovieDetails.this, "" + sectionDetailList.getId(), "" + sectionDetailList.getName(),
                                "" + sectionDetailList.getRentPrice(), "" + sectionDetailList.getTypeId(),
                                "" + sectionDetailList.getVideoType());
                    }
                }
                break;

            case R.id.lyPlayButton:
            case R.id.lyWatchTrailer:
            case R.id.lyTrailer:
                ShowAdByClick("TRAILER");
                break;

            case R.id.lyStartOver:
                if (ConnectivityReceiver.isConnected()) {
                    if (Utility.checkLoginUser(MovieDetails.this)) {
                        if (sectionDetailList != null) {
                            OpenPlayer("STARTOVER");
                        }
                    }
                } else {
                    Utility.showSnackBar(MovieDetails.this, "NoInternet", "");
                }
                break;

            case R.id.lyDownload:
                if (ConnectivityReceiver.isConnected()) {
                    if (Utility.checkLoginUser(MovieDetails.this)) {
                        if (sectionDetailList != null) {
                            if (sectionDetailList.getVideoUploadType().equalsIgnoreCase("server_video")
                                    || sectionDetailList.getVideoUploadType().equalsIgnoreCase("external")) {

                                boolean isPrimiumUser = _checkSubsRentLogin();
                                Log.e("isPrimiumUser", " => " + isPrimiumUser);
                                if (!isPrimiumUser) return;

                                checkAndDownload();
                            } else {
                                Toasty.warning(MovieDetails.this, "" + getResources().getString(R.string.download_not_available),
                                        Toasty.LENGTH_SHORT).show();
                            }
                        }
                    }
                } else {
                    Utility.showSnackBar(MovieDetails.this, "NoInternet", "");
                }
                break;

            case R.id.lyWatchlist:
                if (Utility.checkLoginUser(MovieDetails.this)) {
                    if (sectionDetailList != null) {
                        if (sectionDetailList.getIsBookmark() == 1) {
                            txtWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_add_icon));
                            sectionDetailList.setIsBookmark(0);
                            Utility.showSnackBar(MovieDetails.this, "WatchlistRemove", "");
                        } else {
                            txtWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            sectionDetailList.setIsBookmark(1);
                            Utility.showSnackBar(MovieDetails.this, "WatchlistAdd", "");
                        }
                        Utility.addRemoveFromWatchlist(MovieDetails.this, "" + itemID, "" + videoType, "" + typeID);
                    }
                }
                break;

            case R.id.lyMore:
                Utility.videoMoreDialog(MovieDetails.this, sectionDetailList);
                break;

            case R.id.lyOpenLASDialog:
                Utility.showLASDialog(MovieDetails.this, languageList);
                break;

            case R.id.txtDescription:
                Log.e("lineCount", "" + txtDescription.getLineCount());
                Log.e("maxLines", "" + txtDescription.getMaxLines());
                if (txtDescription.getLineCount() >= 3) {
                    ObjectAnimator animation = ObjectAnimator.ofInt(txtDescription, "maxLines", 50);
                    animation.setDuration(200).start();
                } else {
                    ObjectAnimator animation = ObjectAnimator.ofInt(txtDescription, "maxLines", 3);
                    animation.setDuration(200).start();
                }
                break;

            case R.id.txtCommentIcon:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadBroadcastReceiver, new IntentFilter(Constant.VIDEO_DOWNLOAD_SERVICE));
        Log.e(TAG, "onResume Called");
        appBarLayout.addOnOffsetChangedListener(this);
        SectionDetailData();

        adTYPE = "";
        FullScreenAdInit();
    }

    /* section_detail API */
    private void SectionDetailData() {
        Utility.shimmerShow(shimmer);
        Call<SectionDetailModel> call = BaseURL.getVideoAPI().section_detail("" + typeID, "" + videoType,
                "" + upcomingType, "" + prefManager.getLoginId(), "" + itemID);
        call.enqueue(new Callback<SectionDetailModel>() {
            @Override
            public void onResponse(Call<SectionDetailModel> call, Response<SectionDetailModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    sectionDetailList = new Result();
                    relatedVideoList = new ArrayList<>();
                    castList = new ArrayList<>();
                    directorList = new ArrayList<>();
                    languageList = new ArrayList<>();
                    moreDetailsList = new ArrayList<>();

                    Log.e(TAG, "section_detail Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getLanguage() != null) {
                            if (response.body().getLanguage().size() > 0) {
                                languageList = response.body().getLanguage();
                                Log.e("languageList", "size => " + languageList.size());
                            }
                        }

                        if (response.body().getGetRelatedVideo() != null) {
                            if (response.body().getGetRelatedVideo().size() > 0) {
                                relatedVideoList = response.body().getGetRelatedVideo();
                                Log.e("relatedVideoList", "size => " + relatedVideoList.size());
                            }
                        }

                        if (response.body().getCast() != null) {
                            if (response.body().getCast().size() > 0) {
                                castList = response.body().getCast();
                                Log.e("castList", "size => " + castList.size());

                                for (int i = 0; i < castList.size(); i++) {
                                    if (castList.get(i).getType().equalsIgnoreCase("Director")) {
                                        Cast cast = new Cast();
                                        cast.setId(castList.get(i).getId());
                                        cast.setName(castList.get(i).getName());
                                        cast.setImage(castList.get(i).getImage());
                                        cast.setType(castList.get(i).getType());
                                        cast.setPersonalInfo(castList.get(i).getPersonalInfo());
                                        cast.setStatus(castList.get(i).getStatus());
                                        cast.setUpdatedAt(castList.get(i).getUpdatedAt());
                                        cast.setCreatedAt(castList.get(i).getCreatedAt());
                                        directorList.add(cast);
                                        Log.e("directorList", "==>> " + directorList.size());
                                    }
                                }
                            }
                        }

                        if (response.body().getMoreDetails() != null) {
                            if (response.body().getMoreDetails().size() > 0) {
                                moreDetailsList = response.body().getMoreDetails();
                                Log.e(TAG, "moreDetailsList size => " + moreDetailsList.size());
                            }
                        }

                        if (response.body().getResult() != null) {
                            sectionDetailList = response.body().getResult();
                            Log.e("sectionDetailList", "size => " + sectionDetailList);
                            Log.e("sectionDetailList", "Trailer URL => " + sectionDetailList.getTrailerUrl());
                            Log.e("sectionDetailList", "Video320 => " + sectionDetailList.getVideo320());

                            if (!TextUtils.isEmpty(sectionDetailList.getThumbnail())) {
                                Picasso.get().load(sectionDetailList.getThumbnail()).placeholder(R.drawable.no_image_port).into(ivPosterThumb);
                            } else {
                                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(ivPosterThumb);
                            }

                            if (!TextUtils.isEmpty(sectionDetailList.getLandscape())) {
                                Picasso.get().load(sectionDetailList.getLandscape()).placeholder(R.drawable.no_image_land).into(ivPoster);
                            } else if (!TextUtils.isEmpty(sectionDetailList.getThumbnail())) {
                                Picasso.get().load(sectionDetailList.getThumbnail()).placeholder(R.drawable.no_image_land).into(ivPoster);
                            } else {
                                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(ivPoster);
                            }
                            txtMainTitle.setText("" + sectionDetailList.getName());
                            txtDescription.setText("" + Html.fromHtml(sectionDetailList.getDescription()));
                            txtIMDbRatings.setText("" + sectionDetailList.getImdbRating());
                            txtAudioSubtitleCount.setText(getResources().getString(R.string.audio) + " (" + languageList.size() + "), " +
                                    getResources().getString(R.string.subtitles) + " (0)");

                            txtDuration.setVisibility(View.GONE);
                            if (sectionDetailList.getVideoDuration() != null) {
                                if (sectionDetailList.getVideoDuration() > 0) {
                                    txtDuration.setVisibility(View.VISIBLE);
                                    txtDuration.setText("" + Utility.covertToText(Long.parseLong("" + sectionDetailList.getVideoDuration())));
                                }
                            }
                            txtReleaseYear.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(sectionDetailList.getReleaseYear())) {
                                txtReleaseYear.setVisibility(View.VISIBLE);
                                txtReleaseYear.setText("" + sectionDetailList.getReleaseYear());
                            }
                            txtAgeLimit.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(sectionDetailList.getMaturityRating())) {
                                txtAgeLimit.setVisibility(View.VISIBLE);
                                txtAgeLimit.setText("" + sectionDetailList.getMaturityRating());
                            }
                            txtMaxVideoQuality.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(sectionDetailList.getMaxVideoQuality())) {
                                txtMaxVideoQuality.setVisibility(View.VISIBLE);
                                txtMaxVideoQuality.setText("" + sectionDetailList.getMaxVideoQuality());
                            }
                            lyNewReleaseTag.setVisibility(View.GONE);
                            if (!TextUtils.isEmpty(sectionDetailList.getReleaseTag())) {
                                lyNewReleaseTag.setVisibility(View.VISIBLE);
                                txtNewReleaseTag.setText("" + sectionDetailList.getReleaseTag());
                            }
                            if (sectionDetailList.getIsBookmark() == 1) {
                                txtWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            } else {
                                txtWatchlistIcon.setBackground(getResources().getDrawable(R.drawable.ic_add_icon));
                            }

                            /* For Upcoming SHOW/HIDE */
                            Log.e("videoType", "==============> " + videoType);
                            Log.e("releaseDate", "============> " + sectionDetailList.getReleaseDate());
                            if (!sectionDetailList.getVideoType().toString().equalsIgnoreCase("5")) {
                                cvWatchTrailer.setVisibility(View.GONE);
                                lyReleaseDate.setVisibility(View.GONE);
                                lyFeatureBtns.setVisibility(View.VISIBLE);
                                checkSubscriptionAndSet();
                            } else {
                                lyReleaseDate.setVisibility(View.VISIBLE);
                                txtReleaseDate.setText("" + Utility.dateFormatDDMMMYYYY(sectionDetailList.getReleaseDate()));
                                lyFeatureBtns.setVisibility(View.GONE);
                                cvWatchTrailer.setVisibility(View.VISIBLE);
                                cvWatchNow.setVisibility(View.GONE);
                                cvRentNow.setVisibility(View.GONE);
                            }
                            /* For Upcoming SHOW/HIDE */

                        } else {
                            Log.e(TAG, "section_detail Message => " + response.body().getMessage());
                        }

                        setTabOnClick("Related");
                        setTabRelatedData();

                    } else {
                        Log.e(TAG, "section_detail Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "section_detail Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SectionDetailModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e(TAG, "section_detail onFailure => " + t.getMessage());
            }
        });
    }

    private void checkSubscriptionAndSet() {
        if (sectionDetailList.getIsPremium() == 1) {
            //For Premium
            lyPrimeTag.setVisibility(View.VISIBLE);
            cvWatchNow.setVisibility(View.VISIBLE);
            lyWatchNow.setVisibility(View.GONE);
            lySubscribeNow.setVisibility(View.VISIBLE);
            txtSubscribeNow.setText(getResources().getString(R.string.watch_with) + " " + getResources().getString(R.string.app_name));
            //

            //For Rent
            if (sectionDetailList.getIsRent() == 1) {
                cvRentNow.setVisibility(View.VISIBLE);
                lyRentNow.setVisibility(View.VISIBLE);
                txtRentPrice.setText(getResources().getString(R.string.rent_movie_at_just) + " "
                        + prefManager.getValue("currency_code") + sectionDetailList.getRentPrice());
                lyRentTag.setVisibility(View.VISIBLE);
                txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
            }
            //

            /* Check Premium or Rent buy or not */
            if (sectionDetailList.getIsBuy() == 1 || sectionDetailList.getRentBuy() == 1) {
                if (sectionDetailList.getVideoUploadType().equalsIgnoreCase("server_video")) {
                    lyDownload.setVisibility(View.VISIBLE);
                } else {
                    lyDownload.setVisibility(View.GONE);
                }
                cvRentNow.setVisibility(View.GONE);
                cvWatchNow.setVisibility(View.VISIBLE);
                lySubscribeNow.setVisibility(View.GONE);
                lyWatchNow.setVisibility(View.VISIBLE);
                txtWatchNow.setText("" + getResources().getString(R.string.watch_now));
            } else {
                lyDownload.setVisibility(View.GONE);
            }

        } else if (sectionDetailList.getIsBuy() == 0 && sectionDetailList.getIsRent() == 1 && sectionDetailList.getRentBuy() == 0) {
            cvWatchNow.setVisibility(View.GONE);
            lyDownload.setVisibility(View.GONE);
            cvRentNow.setVisibility(View.VISIBLE);
            lyRentNow.setVisibility(View.VISIBLE);
            txtRentPrice.setText(getResources().getString(R.string.rent_movie_at_just) + " "
                    + prefManager.getValue("currency_code") + sectionDetailList.getRentPrice());
            lyRentTag.setVisibility(View.VISIBLE);
            txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
        } else {
            lyPrimeTag.setVisibility(View.GONE);
            lyRentTag.setVisibility(View.GONE);
            if (sectionDetailList.getVideoUploadType().equalsIgnoreCase("server_video")) {
                lyDownload.setVisibility(View.VISIBLE);
            } else {
                lyDownload.setVisibility(View.GONE);
            }
            cvWatchNow.setVisibility(View.VISIBLE);
            lyWatchNow.setVisibility(View.VISIBLE);
            txtWatchNow.setText("" + getResources().getString(R.string.watch_now));
            lySubscribeNow.setVisibility(View.GONE);
            cvRentNow.setVisibility(View.GONE);

            //For Rent
            if (sectionDetailList.getIsRent() == 1) {
                lyRentTag.setVisibility(View.VISIBLE);
                txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
            }
            //
        }

        lyDownloadStarted.setVisibility(View.GONE);
        Log.e(TAG, "===== IsDownloaded ===============> " + sectionDetailList.getIsDownloaded());
        if (sectionDetailList.getIsDownloaded() == 1) {
            txtDownloadIcon.setBackground(getResources().getDrawable(R.drawable.download_done));
            txtDownload.setText("" + getResources().getString(R.string.complete));
        } else {
            txtDownloadIcon.setBackground(getResources().getDrawable(R.drawable.ic_downloads));
            txtDownload.setText("" + getResources().getString(R.string.download));
        }

        if (sectionDetailList.getStopTime() > 0) {
            if (sectionDetailList.getVideoDuration() != null) {
                if (sectionDetailList.getVideoDuration() > 0 && sectionDetailList.getVideoDuration() != sectionDetailList.getStopTime() &&
                        sectionDetailList.getVideoDuration() > sectionDetailList.getStopTime()) {
                    txtWatchNow.setText("" + getResources().getString(R.string.continue_watching));
                    progressRemainWatch.setVisibility(View.VISIBLE);
                    progressRemainWatch.setProgressPercentage(Utility.getPercentage(sectionDetailList.getVideoDuration(),
                            sectionDetailList.getStopTime()), true);

                    txtRemainWatch.setVisibility(View.VISIBLE);
                    Log.e("VideoDuration", "===========> " + sectionDetailList.getVideoDuration());
                    Log.e("StopTime", "===========> " + sectionDetailList.getStopTime());
                    if ((Long.parseLong("" + sectionDetailList.getVideoDuration())
                            - Long.parseLong("" + sectionDetailList.getStopTime())) > 1000) {
                        txtRemainWatch.setText(Utility.remainTimeInMin(Math.abs(Long.parseLong("" + sectionDetailList.getVideoDuration())
                                - Long.parseLong("" + sectionDetailList.getStopTime())))
                                + " " + getResources().getString(R.string.left));
                    } else {
                        txtRemainWatch.setText("1 " + getResources().getString(R.string.left));
                    }

                    lyStartOver.setVisibility(View.VISIBLE);
                } else {
                    lyStartOver.setVisibility(View.GONE);
                }
                lyTrailer.setVisibility(View.GONE);
            }
        } else {
            progressRemainWatch.setVisibility(View.GONE);
            txtRemainWatch.setVisibility(View.GONE);
            lyStartOver.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(sectionDetailList.getTrailerUrl())) {
                lyTrailer.setVisibility(View.VISIBLE);
            } else {
                lyTrailer.setVisibility(View.GONE);
            }
        }
    }

    /* Set Related Data */
    private void setTabOnClick(String tabName) {
        clickedTab = "" + tabName;
        Log.e("tabName", "======> " + tabName);
        if (tabName.equalsIgnoreCase("Related")) {
            txtTab1.setSelected(true);
            viewLine1.setVisibility(View.VISIBLE);
            viewLine2.setVisibility(View.GONE);
            txtTab2.setSelected(false);

            layoutVideoRelatedData.setVisibility(View.VISIBLE);
            layoutTabMoreDetails.setVisibility(View.GONE);

        } else if (tabName.equalsIgnoreCase("More")) {
            txtTab1.setSelected(false);
            viewLine1.setVisibility(View.GONE);
            viewLine2.setVisibility(View.VISIBLE);
            txtTab2.setSelected(true);

            layoutVideoRelatedData.setVisibility(View.GONE);
            layoutTabMoreDetails.setVisibility(View.VISIBLE);
        }
    }

    private void setTabRelatedData() {
        lyDetailsDynamic.removeAllViews();
        txtTab1.setText("" + getResources().getString(R.string.related));
        txtTab2.setText("" + getResources().getString(R.string.more_details));

        /* Tab 1 START */
        if (directorList.size() > 0) {
            lyDirectors.setVisibility(View.VISIBLE);

            txtDirectorName.setText("" + directorList.get(0).getName());
            txtDirectorDesc.setText("" + directorList.get(0).getPersonalInfo());
            if (!TextUtils.isEmpty(directorList.get(0).getImage())) {
                Picasso.get().load(directorList.get(0).getImage()).placeholder(R.drawable.no_user).into(ivDirectors);
            } else {
                Picasso.get().load(R.drawable.no_user).placeholder(R.drawable.no_user).into(ivDirectors);
            }
        } else {
            lyDirectors.setVisibility(View.GONE);
        }
        CustomersAlsoWatched();
        CastAndCrew();
        if (directorList.size() > 0 || castList.size() > 0 || relatedVideoList.size() > 0) {
            lyTab1.setVisibility(View.VISIBLE);
        } else {
            lyTab1.setVisibility(View.GONE);
        }
        /* Tab 1 END */

        /* Tab 2 START */
        if (moreDetailsList.size() > 0) {
            lyTab2.setVisibility(View.VISIBLE);
            MoreDetailsDynamic();
        } else {
            lyTab2.setVisibility(View.GONE);
        }
        /* Tab 2 END */
    }

    /* Customer Watched Videos */
    private void CustomersAlsoWatched() {
        if (relatedVideoList.size() > 0) {
            Log.e("relatedVideoList =>", "" + relatedVideoList.size());
            sectionRelatedAdapter = new SectionRelatedAdapter(MovieDetails.this, relatedVideoList,
                    "" + videoType, "" + typeID);
            rvAlsoWatch.setLayoutManager(new LinearLayoutManager(MovieDetails.this, LinearLayoutManager.HORIZONTAL, false));
            rvAlsoWatch.setAdapter(sectionRelatedAdapter);
            sectionRelatedAdapter.notifyDataSetChanged();

            lyAlsoWatch.setVisibility(View.VISIBLE);
        } else {
            lyAlsoWatch.setVisibility(View.GONE);
        }
    }

    /* Cast & Crew */
    private void CastAndCrew() {
        if (castList.size() > 0) {
            Log.e("castList =>", "" + castList.size());
            castAndCrewAdapter = new CastAndCrewAdapter(MovieDetails.this, castList, "Details");
            rvCastAndCrew.setLayoutManager(new GridLayoutManager(MovieDetails.this, 3));
            rvCastAndCrew.setAdapter(castAndCrewAdapter);
            castAndCrewAdapter.notifyDataSetChanged();

            lyCastAndCrew.setVisibility(View.VISIBLE);
        } else {
            lyCastAndCrew.setVisibility(View.GONE);
        }
    }

    /*================= MoreDetails Dynamic START =================*/
    private void MoreDetailsDynamic() {
        try {
            for (int i = 0; i < this.moreDetailsList.size(); i++) {
                Typeface faceTitle = Typeface.createFromAsset(MovieDetails.this.getAssets(), "font/public_round_bold.ttf");
                Typeface faceDesc = Typeface.createFromAsset(MovieDetails.this.getAssets(), "font/public_medium.ttf");

                LinearLayout.LayoutParams paramMain = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                LinearLayout lyMain = new LinearLayout(MovieDetails.this);
                lyMain.setOrientation(LinearLayout.VERTICAL);
                lyMain.setLayoutParams(paramMain);

                LinearLayout.LayoutParams paramTitle = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                /* ************* Title START ************* */
                LinearLayout lyTitleDynamic = new LinearLayout(MovieDetails.this);
                lyTitleDynamic.setOrientation(LinearLayout.VERTICAL);
                lyTitleDynamic.setLayoutParams(paramMain);

                TextView txtTitle = new TextView(MovieDetails.this);
                txtTitle.setLayoutParams(paramTitle);
                txtTitle.setTextColor(getResources().getColor(R.color.text_white));
                txtTitle.setText("" + moreDetailsList.get(i).getTitle());
                txtTitle.setTypeface(faceTitle);
                txtTitle.setGravity(Gravity.CENTER_VERTICAL);
                txtTitle.setTextSize(13);

                lyTitleDynamic.addView(txtTitle);
                /* ************* Title END ************* */

                /* ************* Desc START ************* */
                LinearLayout.LayoutParams paramDesc = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramDesc.setMargins(0, 3, 0, 0);

                LinearLayout lyDescDynamic = new LinearLayout(MovieDetails.this);
                lyDescDynamic.setOrientation(LinearLayout.VERTICAL);
                lyDescDynamic.setLayoutParams(paramMain);

                TextView txtDesc = new TextView(MovieDetails.this);
                txtDesc.setLayoutParams(paramDesc);
                txtDesc.setTextColor(getResources().getColor(R.color.text_other));
                txtDesc.setText("" + moreDetailsList.get(i).getDescription());
                txtDesc.setTypeface(faceDesc);
                txtDesc.setGravity(Gravity.CENTER_VERTICAL);
                txtDesc.setTextSize(13);

                lyDescDynamic.addView(txtDesc);
                /* ************* Desc END ************* */

                /* ************* Bottom Line START ************* */
                LinearLayout.LayoutParams paramLine = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1);
                paramLine.setMargins(0, (int) getResources().getDimension(R.dimen.margin12), 0, (int) getResources().getDimension(R.dimen.margin12));

                LinearLayout lyLineDynamic = new LinearLayout(MovieDetails.this);
                lyLineDynamic.setOrientation(LinearLayout.VERTICAL);
                lyLineDynamic.setLayoutParams(paramMain);

                View bottomLine = new View(MovieDetails.this);
                bottomLine.setLayoutParams(paramLine);
                bottomLine.setBackgroundColor(getResources().getColor(R.color.primaryLight));

                lyLineDynamic.addView(bottomLine);
                /* ************* Bottom Line END ************* */

                lyMain.addView(lyTitleDynamic);
                lyMain.addView(lyDescDynamic);
                lyMain.addView(lyLineDynamic);

                lyDetailsDynamic.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty("" + moreDetailsList.get(i).getDescription())) {
                    lyDetailsDynamic.addView(lyMain);
                }
            }

        } catch (Exception e) {
            Log.e("MoreDetailsDynamic", "Exception => " + e);
        }
    }
    /*================= MoreDetails Dynamic END =================*/

    private void OpenPlayer(String playType) {
        Log.e("===> Video", "" + sectionDetailList.getVideo320());
        Log.e("===> Trailer URL", "" + sectionDetailList.getTrailerUrl());

        if (!playType.equalsIgnoreCase("TRAILER")) {
            boolean isPrimiumUser = _checkSubsRentLogin();
            Log.e("isPrimiumUser", " => " + isPrimiumUser);
            if (!isPrimiumUser) return;
        }

        if (playType.equalsIgnoreCase("STARTOVER")) {
            sectionDetailList.setStopTime(0);
        }

        if (playType.equalsIgnoreCase("WATCHNOW")) {
            if (!TextUtils.isEmpty("" + sectionDetailList.getVideo320())) {
                if (sectionDetailList.getStopTime() > 0) {
                    Utility.playVideoOnClick(MovieDetails.this, "Continue",
                            "" + sectionDetailList.getVideoUploadType(), sectionDetailList.getStopTime(),
                            "" + sectionDetailList.getVideo320(), "" + sectionDetailList.getVideo320(),
                            "" + sectionDetailList.getId(), "" + sectionDetailList.getCategoryId(),
                            "" + sectionDetailList.getLandscape(), "" + sectionDetailList.getName(),
                            "" + sectionDetailList.getVideoType(), "");
                } else {
                    Utility.playVideoOnClick(MovieDetails.this, "Movie",
                            "" + sectionDetailList.getVideoUploadType(), 0,
                            "" + sectionDetailList.getVideo320(), "" + sectionDetailList.getVideo320(),
                            "" + sectionDetailList.getId(), "" + sectionDetailList.getCategoryId(),
                            "" + sectionDetailList.getLandscape(), "" + sectionDetailList.getName(),
                            "" + sectionDetailList.getVideoType(), "");
                }
            } else {
                Utility.showSnackBar(MovieDetails.this, "NoVideo", "");
            }
        } else {
            if (!TextUtils.isEmpty("" + sectionDetailList.getTrailerUrl())) {
                Utility.playVideoOnClick(MovieDetails.this, "Trailer",
                        "" + sectionDetailList.getTrailerType(), 0,
                        "" + sectionDetailList.getTrailerUrl(), "" + sectionDetailList.getTrailerUrl(),
                        "" + sectionDetailList.getId(), "" + sectionDetailList.getCategoryId(),
                        "" + sectionDetailList.getLandscape(), "" + sectionDetailList.getName(),
                        "" + sectionDetailList.getVideoType(), "");
            } else {
                Utility.showSnackBar(MovieDetails.this, "NoTrailer", "");
            }
        }
    }

    public boolean _checkSubsRentLogin() {
        Log.e("videoType", " => " + videoType);
        if (sectionDetailList.getIsPremium() == 1 && sectionDetailList.getIsRent() == 1) {
            if (sectionDetailList.getIsBuy() == 1 || sectionDetailList.getRentBuy() == 1) {
                return true;
            } else {
                Utility.openSubscription(MovieDetails.this);
                return false;
            }
        } else if (sectionDetailList.getIsPremium() == 1) {
            if (sectionDetailList.getIsBuy() == 1) {
                return true;
            } else {
                Utility.openSubscription(MovieDetails.this);
                return false;
            }
        } else if (sectionDetailList.getIsRent() == 1) {
            if (sectionDetailList.getIsBuy() == 1 || sectionDetailList.getRentBuy() == 1) {
                return true;
            } else {
                Utility.paymentForRent(MovieDetails.this, "" + sectionDetailList.getId(), "" + sectionDetailList.getName(),
                        "" + sectionDetailList.getRentPrice(), "" + sectionDetailList.getTypeId(),
                        "" + sectionDetailList.getVideoType());
                return false;
            }
        } else {
            return true;
        }
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
                            blockPermissionCheck.add(Functions.getPermissionStatus(MovieDetails.this, key));
                        }
                    }
                    Log.e("blockPermissionCheck", "" + blockPermissionCheck);
                    Log.e("allPermissionClear", "" + allPermissionClear);
                    if (blockPermissionCheck.contains("blocked")) {
                        Functions.showPermissionSetting(MovieDetails.this, "" + getResources().getString(R.string.we_need_storage_permission_for_save_video));
                    } else if (allPermissionClear) {
                        if (sectionDetailList.getIsDownloaded() == 0) {
                            if (!TextUtils.isEmpty(sectionDetailList.getVideo320())) {
                                DownloadSource("" + Utility.fileExt(sectionDetailList.getVideo320()));
                            } else {
                                Toasty.warning(MovieDetails.this, "" + getResources().getString(R.string.invalid_url), Toasty.LENGTH_SHORT).show();
                            }
                        } else {
                            showDownloadCompleteDialog();
                        }
                    }
                }
            });

    /* =====***** Download Securely *****===== */
    private void checkAndDownload() {
        if (takePermissionUtils.isStoragePermissionGranted()) {
            if (sectionDetailList.getIsDownloaded() == 0) {
                if (!TextUtils.isEmpty(sectionDetailList.getVideo320())) {
                    DownloadSource("" + Utility.fileExt(sectionDetailList.getVideo320()));
                } else {
                    Toasty.warning(MovieDetails.this, "" + getResources().getString(R.string.invalid_url), Toasty.LENGTH_SHORT).show();
                }
            } else {
                showDownloadCompleteDialog();
            }
        } else {
            takePermissionUtils.showStoragePermissionDailog("" + getResources().getString(R.string.we_need_storage_permission_for_save_video));
        }
    }

    public void DownloadSource(String extension) {
        switch (extension) {
            case "mov": {
                DownloadNow();
            }
            case "webm": {
                DownloadNow();
            }
            case "mkv": {
                DownloadNow();
            }
            case "mp4": {
                DownloadNow();
            }
            break;
            case "m3u8":
                if (!Functions.isMyServiceRunning(MovieDetails.this, SecureDownloadService.class)) {
                    DownloadNow();
                } else {
                    Toasty.warning(MovieDetails.this, "Multi-download not supported with m3u8 files. please wait !",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private static void expandPanel(Context _context) {
        try {
            @SuppressLint("WrongConstant") Object sbservice = _context.getSystemService("statusbar");
            Class<?> statusbarManager;
            statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            if (Build.VERSION.SDK_INT >= 17) {
                showsb = statusbarManager.getMethod("expandNotificationsPanel");
            } else {
                showsb = statusbarManager.getMethod("expand");
            }
            showsb.invoke(sbservice);
        } catch (ClassNotFoundException _e) {
            _e.printStackTrace();
        } catch (NoSuchMethodException _e) {
            _e.printStackTrace();
        } catch (IllegalArgumentException _e) {
            _e.printStackTrace();
        } catch (IllegalAccessException _e) {
            _e.printStackTrace();
        } catch (InvocationTargetException _e) {
            _e.printStackTrace();
        }
    }

    public void DownloadNow() {
        if (ConnectivityReceiver.isConnected()) {
            if (!Functions.isMyServiceRunning(MovieDetails.this, SecureDownloadService.class)) {

                mainKeyAlias = "" + Functions.getRandomString();
                Log.e("mainKeyAlias", "===>>>> " + mainKeyAlias);

                Log.e("VideoDuration", "==>>> " + sectionDetailList.getVideoDuration());
                Intent mServiceIntent = new Intent(MovieDetails.this, SecureDownloadService.class);
                mServiceIntent.setAction(Constant.VIDEO_DOWNLOAD_SERVICE);
                mServiceIntent.putExtra("from", "MovieDetails");
                mServiceIntent.putExtra("detailList", sectionDetailList);
                mServiceIntent.putExtra("secureKey", mainKeyAlias);
                mServiceIntent.putExtra("videoDuration", sectionDetailList.getVideoDuration());

                expandPanel(MovieDetails.this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    MovieDetails.this.startForegroundService(mServiceIntent);
                } else {
                    MovieDetails.this.startService(mServiceIntent);
                }
            } else {
                Toasty.warning(MovieDetails.this, "" + getResources().getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
            }
        } else {
            Utility.showSnackBar(MovieDetails.this, "NoInternet", "");
        }
    }

    private void updateDownloadUI(VideoDownloadProgress videoDownloadProgress,
                                  boolean isCompleted) {
        if (!isCompleted) {
            Log.e("==DownloadUI==", "downldProgress ==>> " + videoDownloadProgress.progress);
            Log.e("isCompleted", "==>> " + isCompleted);

            lyDownload.setVisibility(View.GONE);
            lyDownloadStarted.setVisibility(View.VISIBLE);
            circularDownloadProgressBar.setProgress(Float.parseFloat("" + videoDownloadProgress.progress));
            txtDownloadStarted.setText("" + videoDownloadProgress.progress + " %");
        } else {
            lyDownload.setVisibility(View.VISIBLE);
            lyDownloadStarted.setVisibility(View.GONE);
            circularDownloadProgressBar.setProgress(0f);
            txtDownloadStarted.setText("0 %");
            if (videoDownloadProgress != null && sectionDetailList.getIsDownloaded() == 0) {
                sectionDetailList.setIsDownloaded(1);
                txtDownloadIcon.setBackground(getResources().getDrawable(R.drawable.download_done));
                txtDownload.setText("" + getResources().getString(R.string.complete));
            }
        }
    }
    /* =====***** Download Securely *****===== */

    /* ==================== Download REMOVE ==================== */
    private void showDownloadCompleteDialog() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MovieDetails.this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.download_complete_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        LinearLayout lyGoToDownload = bottomSheetDialog.findViewById(R.id.lyGoToDownload);
        LinearLayout lyDeleteDownload = bottomSheetDialog.findViewById(R.id.lyDeleteDownload);

        lyGoToDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                Intent intent = new Intent(MovieDetails.this, MainActivity.class);
                intent.putExtra("pushFragment", "MyStuff");
                startActivity(intent);
                finish();
            }
        });

        lyDeleteDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                Utility.removeDownloadFromStorage(MovieDetails.this, Constant.hawkVIDEOList,
                        "" + sectionDetailList.getId());
                Utility.addRemoveDownload(MovieDetails.this, "" + sectionDetailList.getId(),
                        "" + sectionDetailList.getVideoType(), "" + sectionDetailList.getTypeId(), "0");
                if (sectionDetailList.getIsDownloaded() == 1) {
                    sectionDetailList.setIsDownloaded(0);
                    txtDownloadIcon.setBackground(getResources().getDrawable(R.drawable.ic_downloads));
                    txtDownload.setText("" + getResources().getString(R.string.download));
                } else {
                    sectionDetailList.setIsDownloaded(1);
                    txtDownloadIcon.setBackground(getResources().getDrawable(R.drawable.download_done));
                    txtDownload.setText("" + getResources().getString(R.string.complete));
                }
                Utility.showSnackBar(MovieDetails.this, "DeleteDone", "");
            }
        });

    }
    /* ==================== Download REMOVE ==================== */

    /* Full Screen ads START */
    private void ShowAdByClick(String Type) {
        adTYPE = Type;
        Log.e("=>adTYPE", "" + adTYPE);

        if (prefManager.getValue("reward_ad").equalsIgnoreCase("1")) {
            if (mRewardedAd != null) {
                mRewardedAd.show(MovieDetails.this, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                    }
                });
            } else {
                Log.e(TAG, "mRewardedAd wasn't ready yet.");
                if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                    OpenPlayer("WATCHNOW");
                } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                    OpenPlayer("TRAILER");
                } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                    checkAndDownload();
                } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                    OpenPlayer("WATCHNOW");
                }
            }

        } else if (prefManager.getValue("fb_rewardvideo_status").equalsIgnoreCase("1")) {
            if (fbRewardedVideoAd != null && fbRewardedVideoAd.isAdLoaded()) {
                fbRewardedVideoAd.show();
            } else {
                Log.e(TAG, "fbRewardedVideoAd wasn't ready yet.");
                if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                    OpenPlayer("WATCHNOW");
                } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                    OpenPlayer("TRAILER");
                } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                    checkAndDownload();
                } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                    OpenPlayer("WATCHNOW");
                }
            }

        } else if (prefManager.getValue("fb_interstiatial_status").equalsIgnoreCase("1")) {
            if (fbInterstitialAd != null && fbInterstitialAd.isAdLoaded()) {
                fbInterstitialAd.show();
            } else {
                Log.e(TAG, "fbInterstitialAd wasn't ready yet.");
                if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                    OpenPlayer("WATCHNOW");
                } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                    OpenPlayer("TRAILER");
                } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                    checkAndDownload();
                } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                    OpenPlayer("WATCHNOW");
                }
            }

        } else if (prefManager.getValue("interstital_ad").equalsIgnoreCase("1")) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MovieDetails.this);
            } else {
                Log.e(TAG, "mInterstitialAd wasn't ready yet.");
                if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                    OpenPlayer("WATCHNOW");
                } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                    OpenPlayer("TRAILER");
                } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                    checkAndDownload();
                } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                    OpenPlayer("WATCHNOW");
                }
            }
        } else {
            if (mInterstitialVideoAd != null) {
                mInterstitialVideoAd.show(MovieDetails.this, new OnUserEarnedRewardListener() {
                    @Override
                    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                        Log.e("RewardItem amount =>", "" + rewardItem.getAmount());
                    }
                });
            } else {
                Log.e(TAG, "mInterstitialVideoAd wasn't ready yet.");
                if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                    OpenPlayer("WATCHNOW");
                } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                    OpenPlayer("TRAILER");
                } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                    checkAndDownload();
                } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                    OpenPlayer("WATCHNOW");
                }
            }
        }
    }

    private void InterstitialAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e(TAG, "InterstitialAd failed to show. => " + adError.getMessage());
                    mInterstitialAd = null;
                    if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                        OpenPlayer("WATCHNOW");
                    } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                        OpenPlayer("TRAILER");
                    } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                        checkAndDownload();
                    } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                        OpenPlayer("WATCHNOW");
                    }
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.e(TAG, "InterstitialAd ShowedFullScreen");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.e(TAG, "InterstitialAd DismissedFullScreen");
                    mInterstitialAd = null;
                    if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                        OpenPlayer("WATCHNOW");
                    } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                        OpenPlayer("TRAILER");
                    } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                        checkAndDownload();
                    } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                        OpenPlayer("WATCHNOW");
                    }
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e(TAG, "InterstitialAd onAdImpression.");
                }
            };

            mInterstitialAd.load(MovieDetails.this, "" + prefManager.getValue("interstital_adid"),
                    adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            mInterstitialAd = interstitialAd;
                            Log.e(TAG, "InterstitialAd onAdLoaded");
                            mInterstitialAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            // Handle the error
                            Log.e(TAG, "InterstitialAd loadAdError => " + loadAdError.getMessage());
                            mInterstitialAd = null;
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "InterstitialAd Exception => " + e);
        }
    }

    private void InterstitialVideoAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();
            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e(TAG, "mInterstitialVideoAd failed to show. => " + adError.getMessage());
                    if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                        OpenPlayer("WATCHNOW");
                    } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                        OpenPlayer("TRAILER");
                    } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                        checkAndDownload();
                    } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                        OpenPlayer("WATCHNOW");
                    }
                    mInterstitialVideoAd = null;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.e(TAG, "mInterstitialVideoAd ShowedFullScreen");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.e(TAG, "mInterstitialVideoAd DismissedFullScreen");
                    if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                        OpenPlayer("WATCHNOW");
                    } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                        OpenPlayer("TRAILER");
                    } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                        checkAndDownload();
                    } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                        OpenPlayer("WATCHNOW");
                    }
                    mInterstitialVideoAd = null;
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e(TAG, "mInterstitialVideoAd onAdImpression.");
                }
            };

            mInterstitialVideoAd.load(MovieDetails.this, "" + prefManager.getValue("interstital_adid"),
                    adRequest, new RewardedInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedInterstitialAd rewardedInterstitialAd) {
                            super.onAdLoaded(rewardedInterstitialAd);
                            mInterstitialVideoAd = rewardedInterstitialAd;
                            Log.e(TAG, "mInterstitialVideoAd onAdLoaded");
                            mInterstitialVideoAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            // Handle the error
                            Log.e(TAG, "mInterstitialVideoAd loadAdError => " + loadAdError.getMessage());
                            mInterstitialVideoAd = null;
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "mInterstitialVideoAd Exception => " + e);
        }
    }

    private void FacebookInterstitialAd() {
        try {
            fbInterstitialAd = new com.facebook.ads.InterstitialAd(MovieDetails.this,
                    "CAROUSEL_IMG_SQUARE_APP_INSTALL#" + prefManager.getValue("fb_interstiatial_id"));
            fbInterstitialAd.loadAd(fbInterstitialAd.buildLoadAdConfig()
                    .withAdListener(new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {
                            Log.e(TAG, "fbInterstitialAd displayed.");
                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                                OpenPlayer("WATCHNOW");
                            } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                                OpenPlayer("TRAILER");
                            } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                                checkAndDownload();
                            } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                                OpenPlayer("WATCHNOW");
                            }
                            fbInterstitialAd.destroy();
                            fbInterstitialAd = null;
                            Log.e(TAG, "fbInterstitialAd dismissed.");
                        }

                        @Override
                        public void onError(Ad ad, com.facebook.ads.AdError adError) {
                            Log.e(TAG, "fbInterstitialAd failed to load : " + adError.getErrorMessage());
                            if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                                OpenPlayer("WATCHNOW");
                            } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                                OpenPlayer("TRAILER");
                            } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                                checkAndDownload();
                            } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                                OpenPlayer("WATCHNOW");
                            }
                            fbInterstitialAd.destroy();
                            fbInterstitialAd = null;
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            Log.e(TAG, "fbInterstitialAd is loaded and ready to be displayed!");
                        }

                        @Override
                        public void onAdClicked(Ad ad) {
                            Log.e(TAG, "fbInterstitialAd clicked!");
                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {
                            Log.e(TAG, "fbInterstitialAd impression logged!");
                        }
                    })
                    .build());
        } catch (Exception e) {
            Log.e(TAG, "fbInterstitialAd Exception =>" + e);
        }
    }

    private void RewardedVideoAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder().build();

            FullScreenContentCallback fullScreenContentCallback = new FullScreenContentCallback() {
                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);
                    Log.e(TAG, "RewardedVideoAd failed to show. => " + adError.toString());
                    if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                        OpenPlayer("WATCHNOW");
                    } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                        OpenPlayer("TRAILER");
                    } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                        checkAndDownload();
                    } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                        OpenPlayer("WATCHNOW");
                    }
                    mRewardedAd = null;
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent();
                    Log.e(TAG, "RewardedVideoAd was shown.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();
                    Log.e(TAG, "RewardedVideoAd was dismissed.");
                    if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                        OpenPlayer("WATCHNOW");
                    } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                        OpenPlayer("TRAILER");
                    } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                        checkAndDownload();
                    } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                        OpenPlayer("WATCHNOW");
                    }
                    mRewardedAd = null;
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.e(TAG, "RewardedVideoAd onAdImpression.");
                }
            };
            //TEST reward_adid ==>  ca-app-pub-3940256099942544/5224354917 ==OR== + prefManager.getValue("reward_adid")
            mRewardedAd.load(MovieDetails.this, "" + prefManager.getValue("reward_adid"),
                    adRequest, new RewardedAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                            super.onAdLoaded(rewardedAd);
                            Log.e(TAG, "RewardedVideoAd onAdLoaded");
                            mRewardedAd = rewardedAd;
                            mRewardedAd.setFullScreenContentCallback(fullScreenContentCallback);
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.e(TAG, "RewardedVideoAd onAdFailedToLoad");
                            mRewardedAd = null;
                        }
                    });

        } catch (Exception e) {
            Log.e(TAG, "RewardedVideoAd Exception => " + e);
        }
    }

    private void FacebookRewardAd() {
        try {
            fbRewardedVideoAd = new RewardedVideoAd(MovieDetails.this,
                    "VID_HD_16_9_15S_APP_INSTALL#" + prefManager.getValue("fb_rewardvideo_id"));

            fbRewardedVideoAd.loadAd(fbRewardedVideoAd.buildLoadAdConfig().withAdListener(new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e(TAG, "FacebookRewardAd adError => " + adError.getErrorMessage());
                    if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                        OpenPlayer("WATCHNOW");
                    } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                        OpenPlayer("TRAILER");
                    } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                        checkAndDownload();
                    } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                        OpenPlayer("WATCHNOW");
                    }
                    fbRewardedVideoAd.destroy();
                    fbRewardedVideoAd = null;
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.e(TAG, "FacebookRewardAd is loaded and ready to be displayed!");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.e(TAG, "FacebookRewardAd clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.e(TAG, "FacebookRewardAd impression logged!");
                }

                @Override
                public void onRewardedVideoCompleted() {
                    Log.e(TAG, "Rewarded video completed!");
                }

                @Override
                public void onRewardedVideoClosed() {
                    Log.e(TAG, "FacebookRewardAd closed!");
                    if (adTYPE.equalsIgnoreCase("WATCHNOW")) {
                        OpenPlayer("WATCHNOW");
                    } else if (adTYPE.equalsIgnoreCase("TRAILER")) {
                        OpenPlayer("TRAILER");
                    } else if (adTYPE.equalsIgnoreCase("DOWNLOAD")) {
                        checkAndDownload();
                    } else if (adTYPE.equalsIgnoreCase("STARTOVER")) {
                        OpenPlayer("WATCHNOW");
                    }
                    fbRewardedVideoAd.destroy();
                    fbRewardedVideoAd = null;
                }
            }).build());

        } catch (Exception e) {
            Log.e(TAG, "FacebookRewardAd Exception => " + e.getMessage());
        }
    }
    /* Full Screen ads END */

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on scren orientation
        // changes
        if (outState == null) {
            outState.putString("clickedTab", clickedTab);
            Log.e("onSaveInsState", "clickedTab => " + clickedTab);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            clickedTab = savedInstanceState.getString("clickedTab");
            Log.e("onRestoreInsState", "clickedTab => " + clickedTab);
            setTabOnClick("" + clickedTab);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (downloadBroadcastReceiver != null) {
            unregisterReceiver(downloadBroadcastReceiver);
            downloadBroadcastReceiver = null;
        }
        Log.e(TAG, "onPause called");
        appBarLayout.removeOnOffsetChangedListener(this);
        Utility.shimmerHide(shimmer);
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
            fbAdView = null;
        }
        if (fbNativeBannerAd != null) {
            fbNativeBannerAd.destroy();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy called");
        Utility.shimmerHide(shimmer);
        if (mAdView != null) {
            mAdView.destroy();
        }
        if (fbAdView != null) {
            fbAdView.destroy();
            fbAdView = null;
        }
        if (fbNativeBannerAd != null) {
            fbNativeBannerAd.destroy();
        }
        if (mInterstitialAd != null) {
            mInterstitialAd = null;
        }
        if (mRewardedAd != null) {
            mRewardedAd = null;
        }
        if (fbRewardedVideoAd != null) {
            fbRewardedVideoAd.destroy();
            fbRewardedVideoAd = null;
        }
        if (fbNativeBannerAd != null) {
            fbNativeBannerAd.destroy();
        }
        if (fbInterstitialAd != null) {
            fbInterstitialAd.destroy();
            fbInterstitialAd = null;
        }
    }

}
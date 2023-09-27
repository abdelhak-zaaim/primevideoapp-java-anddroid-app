package com.cinefilmz.tv.Utils;

import static android.content.Context.CLIPBOARD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.androidadvance.topsnackbar.TSnackbar;
import com.cinefilmz.tv.Activity.AllPaymentActivity;
import com.cinefilmz.tv.Activity.CastCrewDetails;
import com.cinefilmz.tv.Activity.DownloadedEpisode;
import com.cinefilmz.tv.Activity.KidsDetails;
import com.cinefilmz.tv.Activity.LoginActivity;
import com.cinefilmz.tv.Activity.MovieDetails;
import com.cinefilmz.tv.Activity.SectionByType;
import com.cinefilmz.tv.Activity.Subscription;
import com.cinefilmz.tv.Activity.TVShowDetails;
import com.cinefilmz.tv.Activity.TabKids;
import com.cinefilmz.tv.Activity.TabMovies;
import com.cinefilmz.tv.Activity.TabTVShow;
import com.cinefilmz.tv.Activity.VideosByID;
import com.cinefilmz.tv.Activity.ViewAll;
import com.cinefilmz.tv.Fragment.KidsSectionF;
import com.cinefilmz.tv.Fragment.MyStuffF;
import com.cinefilmz.tv.Fragment.RentProductsF;
import com.cinefilmz.tv.Model.DownloadShowModel.EpisodeItem;
import com.cinefilmz.tv.Model.DownloadShowModel.SessionItem;
import com.cinefilmz.tv.Model.DownloadVideoItem;
import com.cinefilmz.tv.Model.ProfileModel.ProfileModel;
import com.cinefilmz.tv.Model.SectionDetailModel.GetRelatedVideo;
import com.cinefilmz.tv.Model.SectionDetailModel.Language;
import com.cinefilmz.tv.Model.SectionListModel.ContinueWatching;
import com.cinefilmz.tv.Model.SectionListModel.Datum;
import com.cinefilmz.tv.Model.VideoModel.Result;
import com.cinefilmz.tv.OTPLogin.OTPVerification;
import com.cinefilmz.tv.players.PlayerActivity;
import com.cinefilmz.tv.players.VimeoPlayer;
import com.cinefilmz.tv.players.YoutubeActivity;
import com.cinefilmz.tv.Model.SuccessModel.SuccessModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.login.LoginManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.hbb20.CountryCodePicker;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utility {

    Context context;
    public static ProgressDialog pDialog;
    private static final String TAG = Utility.class.getSimpleName();

    public Utility(Context context) {
        this.context = context;
    }

    /* Store User Credential */
    public static void storeUserCred(Activity context, String userID, String email,
                                     String userName, String mobileNumber, String userType, String userImage) {
        PrefManager prefManager = new PrefManager(context);
        prefManager.setLoginId("" + userID);
        prefManager.setValue("Email", "" + email);
        prefManager.setValue("Username", "" + userName);
        prefManager.setValue("Phone", "" + mobileNumber);
        prefManager.setValue("userType", "" + userType);
        prefManager.setValue("userImage", "" + userImage);
    }

    /* check Subscription Purchage status */
    public static boolean checkPrimeUser(Activity context, int isBuy) {
        if (isBuy == 1) {
            return true;
        } else {
            openSubscription(context);
            return false;
        }
    }

    public static void openSubscription(Activity context) {
        Intent intent = new Intent(context, Subscription.class);
        context.startActivity(intent);
        context.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
    }

    public static void paymentForRent(Activity context, String itemID, String itemTitle, String price,
                                      String typeID, String videoType) {
        PrefManager prefManager = new PrefManager(context);
        if (Utility.checkMissingData(context, "" + prefManager.getValue("userType"))) {
            Intent intent = new Intent(context, AllPaymentActivity.class);
            intent.putExtra("payType", "Rent");
            intent.putExtra("itemId", "" + itemID);
            intent.putExtra("itemTitle", "" + itemTitle);
            intent.putExtra("price", "" + price);
            intent.putExtra("typeId", "" + typeID);
            intent.putExtra("videoType", "" + videoType);
            context.startActivity(intent);
        } else {
            Utility.getMissingDataFromUser(context, "" + prefManager.getValue("userType"));
        }
    }

    public static void removeNullObjects(List<? extends Object> yourList) {
        yourList.removeAll(Collections.singleton(null));
    }

    public static int getScreenHeight(Activity activity) {
        int measuredHeight;
        Point size = new Point();
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wm.getDefaultDisplay().getSize(size);
            measuredHeight = size.y;
        } else {
            Display d = wm.getDefaultDisplay();
            measuredHeight = d.getHeight();
        }

        return measuredHeight;
    }

    public static String getAppLanguage(String langCode) {
        String currLanguage = "";
        if (langCode.equalsIgnoreCase("en")) {
            currLanguage = "English";
        } else if (langCode.equalsIgnoreCase("ar")) {
            currLanguage = "Arabic";
        } else if (langCode.equalsIgnoreCase("fr")) {
            currLanguage = "French";
        } else if (langCode.equalsIgnoreCase("hi")) {
            currLanguage = "Hindi";
        } else if (langCode.equalsIgnoreCase("sw")) {
            currLanguage = "Swahili";
        } else if (langCode.equalsIgnoreCase("ta")) {
            currLanguage = "Tamil";
        } else if (langCode.equalsIgnoreCase("te")) {
            currLanguage = "Telugu";
        } else if (langCode.equalsIgnoreCase("zu")) {
            currLanguage = "Zulu";
        }
        return "" + currLanguage;
    }

    /* ========= Ads integration START ========= */

    //Facebook Small NativeAd
    public static void FacebookNativeAdSmall(Activity activity, NativeBannerAd fbNativeBannerAd, NativeAdLayout fbNativeTemplate, String fbNativeID) {
        try {
            fbNativeBannerAd = new NativeBannerAd(activity, "IMG_16_9_APP_INSTALL#" + fbNativeID);

            NativeBannerAd finalFbNativeBannerAd = fbNativeBannerAd;
            fbNativeBannerAd.loadAd(fbNativeBannerAd.buildLoadAdConfig().withAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e("fbNativeBannerAd", "ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e("fbNativeBannerAd", "ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d("fbNativeBannerAd", "ad is loaded and ready to be displayed!");
                    if (finalFbNativeBannerAd == null || finalFbNativeBannerAd != ad) {
                        return;
                    }
                    // Inflate Native Banner Ad into Container
                    inflateFbSmallNativeAd(activity, finalFbNativeBannerAd, fbNativeTemplate);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d("fbNativeBannerAd", "ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d("fbNativeBannerAd", "ad impression logged!");
                }
            }).build());

        } catch (Exception e) {
            Log.e("fbNativeBannerAd", "Exception => " + e.getMessage());
        }
    }

    //Facebook Small NativeAd layout
    public static void inflateFbSmallNativeAd(Activity activity, NativeBannerAd nativeBannerAd, NativeAdLayout nativeTemplate) {
        nativeBannerAd.unregisterView();

        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        View adView = LayoutInflater.from(activity).inflate(R.layout.fbnative_s_adview, nativeTemplate, false);
        nativeTemplate.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.adChoicesContainer);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeBannerAd, nativeTemplate);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView txtNativeTitle = adView.findViewById(R.id.txtNativeTitle);
        TextView txtNativeAdSocialContext = adView.findViewById(R.id.txtNativeAdSocialContext);
        TextView nativeAdSponsoredLabel = adView.findViewById(R.id.nativeAdSponsoredLabel);
        MediaView nativeMediaView = adView.findViewById(R.id.nativeMediaView);
        Button nativeAdCallToAction = adView.findViewById(R.id.nativeAdCallToAction);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        txtNativeTitle.setText(nativeBannerAd.getAdvertiserName());
        txtNativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        nativeAdSponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(txtNativeTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeMediaView, clickableViews);
    }

    //Facebook Large NativeAd
    public static void FacebookNativeAdLarge(Activity activity, NativeAd fbNativeAd, NativeAdLayout fbNativeFullTemplate, String fbFullNativeID) {
        try {
            fbNativeAd = new NativeAd(activity, "IMG_16_9_APP_INSTALL#" + fbFullNativeID);

            NativeAd finalFbNativeAd = fbNativeAd;
            fbNativeAd.loadAd(fbNativeAd.buildLoadAdConfig().withAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e("fbNativeAd", "ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    // Native ad failed to load
                    Log.e("fbNativeAd", "ad failed to load :- " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Native ad is loaded and ready to be displayed
                    Log.d("fbNativeAd", "ad is loaded and ready to be displayed!");
                    if (finalFbNativeAd == null || finalFbNativeAd != ad) {
                        return;
                    }
                    // Inflate Native Banner Ad into Container
                    inflateFbLargeNativeAd(activity, finalFbNativeAd, fbNativeFullTemplate);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d("fbNativeAd", "ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d("fbNativeAd", "ad impression logged!");
                }
            }).build());

        } catch (Exception e) {
            Log.e("fbNative", "Exception => " + e.getMessage());
        }
    }

    //Facebook Large NativeAd layout
    public static void inflateFbLargeNativeAd(Activity activity, NativeAd nativeAd, NativeAdLayout nativeTemplate) {

        nativeAd.unregisterView();
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        View adView = LayoutInflater.from(activity).inflate(R.layout.fbnative_l_adview, nativeTemplate, false);
        nativeTemplate.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = adView.findViewById(R.id.adChoicesContainer);
        AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeTemplate);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.nativeAdIcon);
        TextView txtNativeTitle = adView.findViewById(R.id.txtNativeTitle);
        MediaView nativeMediaView = adView.findViewById(R.id.nativeMediaView);
        TextView txtNativeAdSocialContext = adView.findViewById(R.id.txtNativeAdSocialContext);
        TextView txtNativeAdBody = adView.findViewById(R.id.txtNativeAdBody);
        TextView nativeAdSponsoredLabel = adView.findViewById(R.id.nativeAdSponsoredLabel);
        Button nativeAdCallToAction = adView.findViewById(R.id.nativeAdCallToAction);

        // Set the Text.
        txtNativeTitle.setText(nativeAd.getAdvertiserName());
        txtNativeAdBody.setText(nativeAd.getAdBodyText());
        txtNativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        nativeAdSponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(txtNativeTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(adView, nativeMediaView, nativeAdIcon, clickableViews);
    }

    //Facebook BannerAds
    public static void FacebookBannerAd(Activity activity, com.facebook.ads.AdView fbAdView, String fbPlacementId, LinearLayout lyFbAdView) {
        try {
            fbAdView = new com.facebook.ads.AdView(activity, "IMG_16_9_APP_INSTALL#" + fbPlacementId,
                    com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            fbAdView.loadAd(fbAdView.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
                @Override
                public void onError(Ad ad, com.facebook.ads.AdError adError) {
                    Log.e("fb Banner", "Error=> " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.e("fb Banner", "Loaded=> " + ad.getPlacementId());
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.e("fb Banner", "AdClick=> " + ad.getPlacementId());
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.e("fb Banner", "LoggingImpression=> " + ad.getPlacementId());
                }
            }).build());

            lyFbAdView.addView(fbAdView);
        } catch (Exception e) {
            Log.e("Facebook BannerAds", "Exception => " + e.getMessage());
        }
    }

    //Admob BannerAds
    public static void Admob(Activity activity, AdView mAdView, String bannerAdId, LinearLayout lyAdView) {
        try {
            mAdView = new AdView(activity);
            mAdView.setAdSize(AdSize.SMART_BANNER);
            mAdView.setAdUnitId("" + bannerAdId);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e("onAdFailedToLoad =>", "" + loadAdError.toString());
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                }
            });
            mAdView.loadAd(adRequest);
            lyAdView.addView(mAdView);
        } catch (Exception e) {
            Log.e("Admob BannerAds", "Exception => " + e.getMessage());
        }
    }

    //Admob NativeAd
    public static void NativeAds(Activity activity, TemplateView nativeTemplate, String nativeAdId) {
        try {
            AdLoader adLoader = new AdLoader.Builder(activity, "" + nativeAdId)
                    .forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
                        private ColorDrawable background;

                        @Override
                        public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                            Log.e("NativeAd", "Advertiser => " + nativeAd.getAdvertiser());
                            NativeTemplateStyle styles = new
                                    NativeTemplateStyle.Builder().withMainBackgroundColor(background).build();

                            nativeTemplate.setStyles(styles);
                            nativeTemplate.setNativeAd(nativeAd);
                        }
                    })
                    .withAdListener(new AdListener() {
                        @Override
                        public void onAdFailedToLoad(LoadAdError adError) {
                            // Handle the failure by logging, altering the UI, and so on.
                            Log.e("NativeAd", "adError => " + adError.toString());
                        }

                        @Override
                        public void onAdClicked() {
                            // Log the click event or other custom behavior.
                        }
                    })
                    .withNativeAdOptions(new NativeAdOptions.Builder().build())
                    .build();
            adLoader.loadAd(new AdRequest.Builder().build());
        } catch (Exception e) {
            Log.e("NativeAds", "Exception => " + e);
        }
    }

    /* ========= Ads integration END ========= */


    public static long getFileSize(String url) {
        long file_size = 0L;

        try {
            URL myUrl = new URL(url);
            URLConnection urlConnection = myUrl.openConnection();
            urlConnection.connect();
            file_size = urlConnection.getContentLength();
            Log.e("==>>>>", "file_size => " + file_size);

            return file_size;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file_size;
    }

    /* Play Video on Click */
    public static void playVideoOnClick(Context context, String videoFrom, String videoUploadType, int stopTime, String serverVideo,
                                        String videoUrl, String itemID, String catID, String imgLandscape, String vTitle, String vType,
                                        String secretKey) {
        if (videoUploadType.equalsIgnoreCase("server_video")
                || videoUploadType.equalsIgnoreCase("Download")) {
            /* Normal Player */
            Intent intent = new Intent(context, PlayerActivity.class);
            intent.putExtra("videoFrom", "" + videoFrom);
            if (videoFrom.equalsIgnoreCase("Continue")) {
                intent.putExtra("resumeFrom", stopTime);
            }
            if (videoFrom.equalsIgnoreCase("Download")) {
                intent.putExtra("secretKey", "" + secretKey);
            } else {
                intent.putExtra("catId", "" + catID);
            }
            intent.putExtra("url", "" + serverVideo);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("image", "" + imgLandscape);
            intent.putExtra("title", "" + vTitle);
            intent.putExtra("vType", "" + vType);
            context.startActivity(intent);

        } else if (videoUploadType.equalsIgnoreCase("external")) {
            Intent intent;
            if (videoUrl.contains("youtube")) {
                /* Youtube Player */
                intent = new Intent(context, YoutubeActivity.class);
            } else if (videoUrl.contains("vimeo")) {
                /* Vimeo Player */
                intent = new Intent(context, VimeoPlayer.class);
            } else {
                /* Normal Player */
                intent = new Intent(context, PlayerActivity.class);
            }
            intent.putExtra("videoFrom", "" + videoFrom);
            if (videoFrom.equalsIgnoreCase("Continue")) {
                intent.putExtra("resumeFrom", stopTime);
            }
            intent.putExtra("catId", "" + catID);
            intent.putExtra("url", "" + videoUrl);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("image", "" + imgLandscape);
            intent.putExtra("title", "" + vTitle);
            intent.putExtra("vType", "" + vType);
            context.startActivity(intent);

        } else if (videoUploadType.equalsIgnoreCase("vimeo")) {
            /* Vimeo Player */
            Intent intent = new Intent(context, VimeoPlayer.class);
            intent.putExtra("videoFrom", "" + videoFrom);
            if (videoFrom.equalsIgnoreCase("Continue")) {
                intent.putExtra("resumeFrom", stopTime);
            }
            intent.putExtra("catId", "" + catID);
            intent.putExtra("url", "" + videoUrl);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("image", "" + imgLandscape);
            intent.putExtra("title", "" + vTitle);
            intent.putExtra("vType", "" + vType);
            context.startActivity(intent);

        } else if (videoUploadType.equalsIgnoreCase("youtube")) {
            /* YouTube Player */
            Intent intent = new Intent(context, YoutubeActivity.class);
            intent.putExtra("videoFrom", "" + videoFrom);
            if (videoFrom.equalsIgnoreCase("Continue")) {
                intent.putExtra("resumeFrom", stopTime);
            }
            intent.putExtra("url", "" + videoUrl);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("vType", "" + vType);
            context.startActivity(intent);
        }
    }

    /**
     * Push Activities & Fragments
     **/
    public static void pushSectionByTypeF(Context context, String typeID, String itemTitle, String itemType, String isHomePage) {
        try {
            Log.e("SectionByTypeF", "itemTitle => " + itemTitle);
            Log.e("SectionByTypeF", "typeID => " + typeID);
            Log.e("SectionByTypeF", "itemType => " + itemType);
            Log.e("SectionByTypeF", "isHomePage => " + isHomePage);
            Intent intent = new Intent(context, SectionByType.class);
            intent.putExtra("typeID", "" + typeID);
            intent.putExtra("itemTitle", "" + itemTitle);
            intent.putExtra("itemType", "" + itemType);
            intent.putExtra("isHomePage", "" + isHomePage);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushSectionByTypeF", "Exception => " + e);
        }
    }

    public static void pushMoviesF(Context context, String itemID, String itemTitle, String typeId) {
        try {
            Log.e("MoviesF", "itemTitle => " + itemTitle);
            Log.e("MoviesF", "itemID => " + itemID);
            Log.e("MoviesF", "typeId => " + typeId);
            Intent intent = new Intent(context, TabMovies.class);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("itemTitle", "" + itemTitle);
            intent.putExtra("typeId", "" + typeId);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushMoviesF", "Exception => " + e);
        }
    }

    public static void pushTVShowF(Context context, String itemID, String itemTitle, String typeId) {
        try {
            Log.e("TVShowF", "itemTitle => " + itemTitle);
            Log.e("TVShowF", "itemID => " + itemID);
            Log.e("TVShowF", "typeId => " + typeId);
            Intent intent = new Intent(context, TabTVShow.class);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("itemTitle", "" + itemTitle);
            intent.putExtra("typeId", "" + typeId);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushTVShowF", "Exception => " + e);
        }
    }

    public static void pushKidsF(Context context, String itemID, String videoType, String typeID) {
        try {
            Log.e("KidsF", "itemID => " + itemID);
            Log.e("KidsF", "videoType => " + videoType);
            Log.e("KidsF", "typeID => " + typeID);
            Intent intent = new Intent(context, TabKids.class);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("videoType", "" + videoType);
            intent.putExtra("typeID", "" + typeID);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushKidsF", "Exception => " + e);
        }
    }

    public static void pushKidsSectionF(Context context) {
        try {
            //set Fragmentclass Arguments
            Fragment kidsSectionF = new KidsSectionF();

            if (kidsSectionF == null)
                return;

            String backStateName = kidsSectionF.getClass().getSimpleName();
            Log.e("KidsSectionF", "backStateName => " + backStateName);
            String fragmentTag = backStateName;

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) {
                ft.replace(R.id.rootLayout, kidsSectionF, fragmentTag);
            } else {
                ft.replace(R.id.rootLayout, kidsSectionF, fragmentTag);
                ft.detach(kidsSectionF);
                ft.attach(kidsSectionF);
            }
            ft.setReorderingAllowed(true);
            ft.addToBackStack(backStateName);
            ft.commit();
        } catch (Exception e) {
            Log.e("pushKidsSectionF", "Exception => " + e);
        }
    }

    public static void pushMyStuffF(Context context) {
        try {
            //set Fragmentclass Arguments
            Fragment myStuffF = new MyStuffF();

            if (myStuffF == null)
                return;

            String backStateName = myStuffF.getClass().getSimpleName();
            Log.e("MyStuffF", "backStateName => " + backStateName);
            String fragmentTag = backStateName;

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) {
                ft.replace(R.id.rootLayout, myStuffF, fragmentTag);
            } else {
                ft.replace(R.id.rootLayout, myStuffF, fragmentTag);
                ft.detach(myStuffF);
                ft.attach(myStuffF);
            }
            ft.setReorderingAllowed(true);
            ft.addToBackStack(backStateName);
            ft.commit();
        } catch (Exception e) {
            Log.e("pushMyStuffF", "Exception => " + e);
        }
    }

    /* Detail Fragments */
    public static void openDetails(Context context, String itemID, String videoType, String typeID, String upcomingType) {
        try {
            Log.e("openDetails", "itemID => " + itemID);
            Log.e("openDetails", "videoType => " + videoType);
            Log.e("openDetails", "typeID => " + typeID);
            Log.e("openDetails", "upcomingType => " + upcomingType);

            Intent intent;
            if (videoType.equalsIgnoreCase("5")) {
                if (upcomingType.equalsIgnoreCase("2")) {
                    intent = new Intent(context, TVShowDetails.class);
                } else {
                    intent = new Intent(context, MovieDetails.class);
                }
            } else {
                if (videoType.equalsIgnoreCase("2")) {
                    intent = new Intent(context, TVShowDetails.class);
                } else {
                    intent = new Intent(context, MovieDetails.class);
                }
            }
            intent.putExtra("ID", "" + itemID);
            intent.putExtra("videoType", "" + videoType);
            intent.putExtra("typeID", "" + typeID);
            intent.putExtra("upcomingType", "" + upcomingType);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushMovieDetailsF", "Exception => " + e);
        }
    }

    public static void pushKidsDetailsF(Context context, String itemID, String videoType, String typeID) {
        try {
            Log.e("KidsDetailsF", "itemID => " + itemID);
            Log.e("KidsDetailsF", "videoType => " + videoType);
            Log.e("KidsDetailsF", "typeID => " + typeID);
            Intent intent = new Intent(context, KidsDetails.class);
            intent.putExtra("ID", "" + itemID);
            intent.putExtra("videoType", "" + videoType);
            intent.putExtra("typeID", "" + typeID);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushKidsDetailsF", "Exception => " + e);
        }
    }
    /* Detail Fragments */

    public static void pushCastCrewDetailsF(Context context, String itemID) {
        try {
            Log.e("CastCrewDetailsF", "itemID => " + itemID);
            Intent intent = new Intent(context, CastCrewDetails.class);
            intent.putExtra("itemID", "" + itemID);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushCastCrewDetailsF", "Exception => " + e);
        }
    }

    public static void pushViewAllF(Context context, String viewAllType, String itemID, String itemTitle, String videoType, String layoutType, String typeID) {
        try {
            Log.e("ViewAllF", "viewAllType => " + viewAllType);
            Log.e("ViewAllF", "itemID => " + itemID);
            Log.e("ViewAllF", "itemTitle => " + itemTitle);
            Log.e("ViewAllF", "videoType => " + videoType);
            Log.e("ViewAllF", "layoutType => " + layoutType);
            Log.e("ViewAllF", "typeID => " + typeID);
            Intent intent = new Intent(context, ViewAll.class);
            intent.putExtra("viewAllType", "" + viewAllType);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("itemTitle", "" + itemTitle);
            intent.putExtra("videoType", "" + videoType);
            intent.putExtra("layoutType", "" + layoutType);
            intent.putExtra("typeID", "" + typeID);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushViewAllF", "Exception => " + e);
        }
    }

    public static void pushVideosByIDF(Context context, String viewAllType, String itemID, String itemTitle, String videoType,
                                       String layoutType, String typeID) {
        try {
            Log.e("VideosByIDF", "viewAllType => " + viewAllType);
            Log.e("VideosByIDF", "itemID => " + itemID);
            Log.e("VideosByIDF", "itemTitle => " + itemTitle);
            Log.e("VideosByIDF", "videoType => " + videoType);
            Log.e("VideosByIDF", "layoutType => " + layoutType);
            Log.e("VideosByIDF", "typeID => " + typeID);
            Intent intent = new Intent(context, VideosByID.class);
            intent.putExtra("viewAllType", "" + viewAllType);
            intent.putExtra("itemID", "" + itemID);
            intent.putExtra("itemTitle", "" + itemTitle);
            intent.putExtra("videoType", "" + videoType);
            intent.putExtra("layoutType", "" + layoutType);
            intent.putExtra("typeID", "" + typeID);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushVideosByIDF", "Exception => " + e);
        }
    }

    public static void pushRentProductsF(Context context, String itemType) {
        try {
            Log.e("RentProductsF", "itemType => " + itemType);
            Bundle bundle = new Bundle();
            bundle.putString("itemType", "" + itemType);
            //set Fragmentclass Arguments
            Fragment rentProductsF = new RentProductsF();
            rentProductsF.setArguments(bundle);

            if (rentProductsF == null)
                return;

            String backStateName = rentProductsF.getClass().getSimpleName();
            Log.e("RentProductsF", "backStateName => " + backStateName);
            String fragmentTag = backStateName;

            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);

            if (!fragmentPopped && fragmentManager.findFragmentByTag(fragmentTag) == null) {
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.rootLayout, rentProductsF, fragmentTag);
                ft.setReorderingAllowed(true);
                ft.addToBackStack(backStateName);
                ft.commit();
            }
        } catch (Exception e) {
            Log.e("pushRentProductsF", "Exception => " + e);
        }
    }

    public static void pushDownloadEpiF(Context context, int showPosition, String showTitle, String showID, String videoType, String typeID) {
        try {
            Log.e("DownloadEpiF", "showPosition => " + showPosition);
            Log.e("DownloadEpiF", "showTitle => " + showTitle);
            Log.e("DownloadEpiF", "showID => " + showID);
            Log.e("DownloadEpiF", "videoType => " + videoType);
            Log.e("DownloadEpiF", "typeID => " + typeID);
            Intent intent = new Intent(context, DownloadedEpisode.class);
            intent.putExtra("showTitle", "" + showTitle);
            intent.putExtra("showID", "" + showID);
            intent.putExtra("videoType", "" + videoType);
            intent.putExtra("typeID", "" + typeID);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushDownloadEpiF", "Exception => " + e);
        }
    }

    /**
     * Back From Fragments
     **/
    public static void backFromFragments(Context context) {
        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

        if (fragmentManager != null) {
            if (fragmentManager.getBackStackEntryCount() > 1) {
                fragmentManager.popBackStack(fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getId(),
                        fragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } else {
            FragmentManager fragmentManager1 = ((FragmentActivity) context).getSupportFragmentManager();
            fragmentManager1.popBackStack();
        }
    }

    /**
     * Dialogs START
     **/

    public static void showLongPressedMovieDialog(Activity context, List<Datum> sectionDataList,
                                                  List<Result> allVideoList,
                                                  List<GetRelatedVideo> relatedVideoList, List<ContinueWatching> continueWatchingList, int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.long_pressed_video_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        TextView txtVideoTitle = bottomSheetDialog.findViewById(R.id.txtVideoTitle);
        TextView txtDuration = bottomSheetDialog.findViewById(R.id.txtDuration);
        TextView txtCommentIcon = bottomSheetDialog.findViewById(R.id.txtCommentIcon);
        TextView txtAgeLimit = bottomSheetDialog.findViewById(R.id.txtAgeLimit);
        TextView txtWatchResumeIcon = bottomSheetDialog.findViewById(R.id.txtWatchResumeIcon);
        TextView txtWatchResume = bottomSheetDialog.findViewById(R.id.txtWatchResume);
        TextView txtAddRemoveWatchlistIcon = bottomSheetDialog.findViewById(R.id.txtAddRemoveWatchlistIcon);
        TextView txtAddRemoveWatchlist = bottomSheetDialog.findViewById(R.id.txtAddRemoveWatchlist);
        TextView txtAddDeleteDownloadIcon = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownloadIcon);
        TextView txtAddDeleteDownload = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownload);
        LinearLayout lyWatchResume = bottomSheetDialog.findViewById(R.id.lyWatchResume);
        LinearLayout lyWatchTrailer = bottomSheetDialog.findViewById(R.id.lyWatchTrailer);
        LinearLayout lyStartOver = bottomSheetDialog.findViewById(R.id.lyStartOver);
        LinearLayout lyAddRemoveWatchlist = bottomSheetDialog.findViewById(R.id.lyAddRemoveWatchlist);
        LinearLayout lyAddDeleteDownload = bottomSheetDialog.findViewById(R.id.lyAddDeleteDownload);
        LinearLayout lyVideoShare = bottomSheetDialog.findViewById(R.id.lyVideoShare);
        LinearLayout lyViewDetails = bottomSheetDialog.findViewById(R.id.lyViewDetails);
        LinearLayout lyHideThisVideo = bottomSheetDialog.findViewById(R.id.lyHideThisVideo);

        /* Prime & Rent TAG init */
        LinearLayout lyPrimeTag = bottomSheetDialog.findViewById(R.id.lyPrimeTag);
        LinearLayout lyRentTag = bottomSheetDialog.findViewById(R.id.lyRentTag);
        TextView txtPrimeTag = bottomSheetDialog.findViewById(R.id.txtPrimeTag);
        TextView txtCurrencySymbol = bottomSheetDialog.findViewById(R.id.txtCurrencySymbol);
        TextView txtRentTag = bottomSheetDialog.findViewById(R.id.txtRentTag);

        PrefManager prefManager = new PrefManager(context);

        if (sectionDataList != null) {
            if (sectionDataList.size() > 0) {
                txtVideoTitle.setText("" + sectionDataList.get(position).getName());
                if (sectionDataList.get(position).getVideoDuration() > 0) {
                    txtDuration.setVisibility(View.VISIBLE);
                    txtDuration.setText("" + Utility.covertToText(Long.parseLong("" + sectionDataList.get(position).getVideoDuration())));
                } else {
                    txtDuration.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(sectionDataList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + sectionDataList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (sectionDataList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (sectionDataList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (sectionDataList.get(position).getStopTime() > 0) {
                    lyStartOver.setVisibility(View.VISIBLE);
                    txtWatchResume.setText("" + context.getResources().getString(R.string.resume));
                    txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_resume));
                } else {
                    lyStartOver.setVisibility(View.GONE);
                    txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_play2));
                    txtWatchResume.setText("" + context.getResources().getString(R.string.watch_now));
                }

                if (sectionDataList.get(position).getTypeId() == 5) {
                    lyAddDeleteDownload.setVisibility(View.GONE);
                } else {
                    if (sectionDataList.get(position).getIsDownloaded() == 1) {
                        txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_delete));
                        lyAddDeleteDownload.setVisibility(View.VISIBLE);
                        txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.delete_download));
                    } else {
                        lyAddDeleteDownload.setVisibility(View.GONE);
                    }
                }

                if (sectionDataList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        if (allVideoList != null) {
            if (allVideoList.size() > 0) {
                txtVideoTitle.setText("" + allVideoList.get(position).getName());
                if (allVideoList.get(position).getVideoDuration() > 0) {
                    txtDuration.setVisibility(View.VISIBLE);
                    txtDuration.setText("" + Utility.covertToText(Long.parseLong("" + allVideoList.get(position).getVideoDuration())));
                } else {
                    txtDuration.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(allVideoList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + allVideoList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (allVideoList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (allVideoList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (allVideoList.get(position).getStopTime() > 0) {
                    lyStartOver.setVisibility(View.VISIBLE);
                    txtWatchResume.setText("" + context.getResources().getString(R.string.resume));
                    txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_resume));
                } else {
                    lyStartOver.setVisibility(View.GONE);
                    txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_play2));
                    txtWatchResume.setText("" + context.getResources().getString(R.string.watch_now));
                }

                if (allVideoList.get(position).getVideoType() == 5) {
                    lyAddDeleteDownload.setVisibility(View.GONE);
                } else {
                    if (allVideoList.get(position).getIsDownloaded() == 1) {
                        txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_delete));
                        lyAddDeleteDownload.setVisibility(View.VISIBLE);
                        txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.delete_download));
                    } else {
                        lyAddDeleteDownload.setVisibility(View.GONE);
                    }
                }

                if (allVideoList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        if (relatedVideoList != null) {
            if (relatedVideoList.size() > 0) {
                txtVideoTitle.setText("" + relatedVideoList.get(position).getName());
                if (relatedVideoList.get(position).getVideoDuration() > 0) {
                    txtDuration.setVisibility(View.VISIBLE);
                    txtDuration.setText("" + Utility.covertToText(Long.parseLong("" + relatedVideoList.get(position).getVideoDuration())));
                } else {
                    txtDuration.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(relatedVideoList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + relatedVideoList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (relatedVideoList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (relatedVideoList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (relatedVideoList.get(position).getStopTime() > 0) {
                    lyStartOver.setVisibility(View.VISIBLE);
                    txtWatchResume.setText("" + context.getResources().getString(R.string.resume));
                    txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_resume));
                } else {
                    lyStartOver.setVisibility(View.GONE);
                    txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_play2));
                    txtWatchResume.setText("" + context.getResources().getString(R.string.watch_now));
                }

                if (relatedVideoList.get(position).getVideoType() == 5) {
                    lyAddDeleteDownload.setVisibility(View.GONE);
                } else {
                    if (relatedVideoList.get(position).getIsDownloaded() == 1) {
                        txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_delete));
                        lyAddDeleteDownload.setVisibility(View.VISIBLE);
                        txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.delete_download));
                    } else {
                        lyAddDeleteDownload.setVisibility(View.GONE);
                    }
                }

                if (relatedVideoList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        if (continueWatchingList != null) {
            if (continueWatchingList.size() > 0) {
                txtVideoTitle.setText("" + continueWatchingList.get(position).getName());
                if (continueWatchingList.get(position).getVideoDuration() > 0) {
                    txtDuration.setVisibility(View.VISIBLE);
                    txtDuration.setText("" + Utility.covertToText(Long.parseLong("" + continueWatchingList.get(position).getVideoDuration())));
                } else {
                    txtDuration.setVisibility(View.GONE);
                }
                if (!TextUtils.isEmpty(continueWatchingList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + continueWatchingList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (continueWatchingList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (continueWatchingList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (continueWatchingList.get(position).getStopTime() > 0) {
                    lyStartOver.setVisibility(View.VISIBLE);
                    txtWatchResume.setText("" + context.getResources().getString(R.string.resume));
                    txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_resume));
                } else {
                    lyStartOver.setVisibility(View.GONE);
                    txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_play2));
                    txtWatchResume.setText("" + context.getResources().getString(R.string.watch_now));
                }

                if (continueWatchingList.get(position).getVideoType() == 5) {
                    lyAddDeleteDownload.setVisibility(View.GONE);
                } else {
                    if (continueWatchingList.get(position).getIsDownloaded() == 1) {
                        txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_delete));
                        lyAddDeleteDownload.setVisibility(View.VISIBLE);
                        txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.delete_download));
                    } else {
                        lyAddDeleteDownload.setVisibility(View.GONE);
                    }
                }

                if (continueWatchingList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        lyWatchResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }

                if (checkLoginUser(context)) {

                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsPremium() == 1 && sectionDataList.get(position).getIsRent() == 1) {
                                if (sectionDataList.get(position).getIsBuy() == 1 ||
                                        sectionDataList.get(position).getRentBuy() == 1) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(), "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (sectionDataList.get(position).getIsPremium() == 1) {
                                if (sectionDataList.get(position).getIsBuy() == 1) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(), "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (sectionDataList.get(position).getIsRent() == 1) {
                                if (sectionDataList.get(position).getRentBuy() == 1) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(), "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    paymentForRent(context, "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getRentPrice(),
                                            "" + sectionDataList.get(position).getTypeId(),
                                            "" + sectionDataList.get(position).getVideoType());
                                }
                            } else {
                                if (sectionDataList.size() > 0) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(), "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                }
                            }
                        }
                    }

                    if (allVideoList != null) {
                        if (allVideoList.size() > 0) {
                            if (allVideoList.get(position).getIsPremium() == 1 && allVideoList.get(position).getIsRent() == 1) {
                                if (allVideoList.get(position).getIsBuy() == 1 ||
                                        allVideoList.get(position).getRentBuy() == 1) {
                                    if (allVideoList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                allVideoList.get(position).getVideoUploadType(),
                                                allVideoList.get(position).getStopTime(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getId(),
                                                "" + allVideoList.get(position).getCategoryId(),
                                                "" + allVideoList.get(position).getLandscape(),
                                                "" + allVideoList.get(position).getName(),
                                                "" + allVideoList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + allVideoList.get(position).getVideoUploadType(),
                                                0, "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getId(),
                                                "" + allVideoList.get(position).getCategoryId(),
                                                "" + allVideoList.get(position).getLandscape(),
                                                "" + allVideoList.get(position).getName(),
                                                "" + allVideoList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (allVideoList.get(position).getIsPremium() == 1) {
                                if (allVideoList.get(position).getIsBuy() == 1) {
                                    if (allVideoList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                allVideoList.get(position).getVideoUploadType(),
                                                allVideoList.get(position).getStopTime(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getId(),
                                                "" + allVideoList.get(position).getCategoryId(),
                                                "" + allVideoList.get(position).getLandscape(),
                                                "" + allVideoList.get(position).getName(),
                                                "" + allVideoList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + allVideoList.get(position).getVideoUploadType(),
                                                0, "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getId(),
                                                "" + allVideoList.get(position).getCategoryId(),
                                                "" + allVideoList.get(position).getLandscape(),
                                                "" + allVideoList.get(position).getName(),
                                                "" + allVideoList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (allVideoList.get(position).getIsRent() == 1) {
                                if (allVideoList.get(position).getRentBuy() == 1) {
                                    if (allVideoList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                allVideoList.get(position).getVideoUploadType(),
                                                allVideoList.get(position).getStopTime(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getId(),
                                                "" + allVideoList.get(position).getCategoryId(),
                                                "" + allVideoList.get(position).getLandscape(),
                                                "" + allVideoList.get(position).getName(),
                                                "" + allVideoList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + allVideoList.get(position).getVideoUploadType(),
                                                0, "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getId(),
                                                "" + allVideoList.get(position).getCategoryId(),
                                                "" + allVideoList.get(position).getLandscape(),
                                                "" + allVideoList.get(position).getName(),
                                                "" + allVideoList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    paymentForRent(context, "" + allVideoList.get(position).getId(),
                                            "" + allVideoList.get(position).getName(),
                                            "" + allVideoList.get(position).getRentPrice(),
                                            "" + allVideoList.get(position).getTypeId(),
                                            "" + allVideoList.get(position).getVideoType());
                                }
                            } else {
                                if (allVideoList.size() > 0) {
                                    if (allVideoList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                allVideoList.get(position).getVideoUploadType(),
                                                allVideoList.get(position).getStopTime(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getId(),
                                                "" + allVideoList.get(position).getCategoryId(),
                                                "" + allVideoList.get(position).getLandscape(),
                                                "" + allVideoList.get(position).getName(),
                                                "" + allVideoList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + allVideoList.get(position).getVideoUploadType(),
                                                0, "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getVideo320(),
                                                "" + allVideoList.get(position).getId(),
                                                "" + allVideoList.get(position).getCategoryId(),
                                                "" + allVideoList.get(position).getLandscape(),
                                                "" + allVideoList.get(position).getName(),
                                                "" + allVideoList.get(position).getVideoType(), "");
                                    }
                                }
                            }
                        }
                    }

                    if (relatedVideoList != null) {
                        if (relatedVideoList.size() > 0) {
                            if (relatedVideoList.get(position).getIsPremium() == 1 && relatedVideoList.get(position).getIsRent() == 1) {
                                if (relatedVideoList.get(position).getIsBuy() == 1 ||
                                        relatedVideoList.get(position).getRentBuy() == 1) {
                                    if (relatedVideoList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                relatedVideoList.get(position).getVideoUploadType(),
                                                relatedVideoList.get(position).getStopTime(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getId(),
                                                "" + relatedVideoList.get(position).getCategoryId(),
                                                "" + relatedVideoList.get(position).getLandscape(),
                                                "" + relatedVideoList.get(position).getName(),
                                                "" + relatedVideoList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + relatedVideoList.get(position).getVideoUploadType(),
                                                0, "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getId(),
                                                "" + relatedVideoList.get(position).getCategoryId(),
                                                "" + relatedVideoList.get(position).getLandscape(),
                                                "" + relatedVideoList.get(position).getName(),
                                                "" + relatedVideoList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (relatedVideoList.get(position).getIsPremium() == 1) {
                                if (relatedVideoList.get(position).getIsBuy() == 1) {
                                    if (relatedVideoList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                relatedVideoList.get(position).getVideoUploadType(),
                                                relatedVideoList.get(position).getStopTime(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getId(),
                                                "" + relatedVideoList.get(position).getCategoryId(),
                                                "" + relatedVideoList.get(position).getLandscape(),
                                                "" + relatedVideoList.get(position).getName(),
                                                "" + relatedVideoList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + relatedVideoList.get(position).getVideoUploadType(),
                                                0, "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getId(),
                                                "" + relatedVideoList.get(position).getCategoryId(),
                                                "" + relatedVideoList.get(position).getLandscape(),
                                                "" + relatedVideoList.get(position).getName(),
                                                "" + relatedVideoList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (relatedVideoList.get(position).getIsRent() == 1) {
                                if (relatedVideoList.get(position).getRentBuy() == 1) {
                                    if (relatedVideoList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                relatedVideoList.get(position).getVideoUploadType(),
                                                relatedVideoList.get(position).getStopTime(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getId(),
                                                "" + relatedVideoList.get(position).getCategoryId(),
                                                "" + relatedVideoList.get(position).getLandscape(),
                                                "" + relatedVideoList.get(position).getName(),
                                                "" + relatedVideoList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + relatedVideoList.get(position).getVideoUploadType(),
                                                0, "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getId(),
                                                "" + relatedVideoList.get(position).getCategoryId(),
                                                "" + relatedVideoList.get(position).getLandscape(),
                                                "" + relatedVideoList.get(position).getName(),
                                                "" + relatedVideoList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    paymentForRent(context, "" + relatedVideoList.get(position).getId(),
                                            "" + relatedVideoList.get(position).getName(),
                                            "" + relatedVideoList.get(position).getRentPrice(),
                                            "" + relatedVideoList.get(position).getTypeId(),
                                            "" + relatedVideoList.get(position).getVideoType());
                                }
                            } else {
                                if (relatedVideoList.size() > 0) {
                                    if (relatedVideoList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                relatedVideoList.get(position).getVideoUploadType(),
                                                relatedVideoList.get(position).getStopTime(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getId(),
                                                "" + relatedVideoList.get(position).getCategoryId(),
                                                "" + relatedVideoList.get(position).getLandscape(),
                                                "" + relatedVideoList.get(position).getName(),
                                                "" + relatedVideoList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + relatedVideoList.get(position).getVideoUploadType(),
                                                0, "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getVideo320(),
                                                "" + relatedVideoList.get(position).getId(),
                                                "" + relatedVideoList.get(position).getCategoryId(),
                                                "" + relatedVideoList.get(position).getLandscape(),
                                                "" + relatedVideoList.get(position).getName(),
                                                "" + relatedVideoList.get(position).getVideoType(), "");
                                    }
                                }
                            }
                        }
                    }

                    if (continueWatchingList != null) {
                        if (continueWatchingList.size() > 0) {
                            if (continueWatchingList.get(position).getIsPremium() == 1 && continueWatchingList.get(position).getIsRent() == 1) {
                                if (continueWatchingList.get(position).getIsBuy() == 1 ||
                                        continueWatchingList.get(position).getRentBuy() == 1) {
                                    if (continueWatchingList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                continueWatchingList.get(position).getStopTime(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                0, "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (continueWatchingList.get(position).getIsPremium() == 1) {
                                if (continueWatchingList.get(position).getIsBuy() == 1) {
                                    if (continueWatchingList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                continueWatchingList.get(position).getStopTime(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                0, "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (continueWatchingList.get(position).getIsRent() == 1) {
                                if (continueWatchingList.get(position).getRentBuy() == 1) {
                                    if (continueWatchingList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                continueWatchingList.get(position).getStopTime(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                0, "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    paymentForRent(context, "" + continueWatchingList.get(position).getId(),
                                            "" + continueWatchingList.get(position).getName(),
                                            "" + continueWatchingList.get(position).getRentPrice(),
                                            "" + continueWatchingList.get(position).getTypeId(),
                                            "" + continueWatchingList.get(position).getVideoType());
                                }
                            } else {
                                if (continueWatchingList.size() > 0) {
                                    if (continueWatchingList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                continueWatchingList.get(position).getStopTime(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                0, "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    }
                                }
                            }
                        }
                    }

                }

            }
        });

        lyWatchTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (sectionDataList != null) {
                    if (sectionDataList.size() > 0) {
                        playVideoOnClick(context, "Trailer", "" + sectionDataList.get(position).getTrailerType(),
                                0, "" + sectionDataList.get(position).getTrailerUrl(),
                                "" + sectionDataList.get(position).getTrailerUrl(),
                                "" + sectionDataList.get(position).getId(),
                                "" + sectionDataList.get(position).getCategoryId(),
                                "" + sectionDataList.get(position).getLandscape(),
                                "" + sectionDataList.get(position).getName(),
                                "" + sectionDataList.get(position).getVideoType(), "");
                    }
                }
                if (allVideoList != null) {
                    if (allVideoList.size() > 0) {
                        playVideoOnClick(context, "Trailer", "" + allVideoList.get(position).getTrailerType(),
                                0, "" + allVideoList.get(position).getTrailerUrl(),
                                "" + allVideoList.get(position).getTrailerUrl(),
                                "" + allVideoList.get(position).getId(),
                                "" + allVideoList.get(position).getCategoryId(),
                                "" + allVideoList.get(position).getLandscape(),
                                "" + allVideoList.get(position).getName(),
                                "" + allVideoList.get(position).getVideoType(), "");
                    }
                }
                if (relatedVideoList != null) {
                    if (relatedVideoList.size() > 0) {
                        playVideoOnClick(context, "Trailer", "" + relatedVideoList.get(position).getTrailerType(),
                                0, "" + relatedVideoList.get(position).getTrailerUrl(),
                                "" + relatedVideoList.get(position).getTrailerUrl(),
                                "" + relatedVideoList.get(position).getId(),
                                "" + relatedVideoList.get(position).getCategoryId(),
                                "" + relatedVideoList.get(position).getLandscape(),
                                "" + relatedVideoList.get(position).getName(),
                                "" + relatedVideoList.get(position).getVideoType(), "");
                    }
                }
                if (continueWatchingList != null) {
                    if (continueWatchingList.size() > 0) {
                        playVideoOnClick(context, "Trailer", "" + continueWatchingList.get(position).getTrailerType(),
                                0, "" + continueWatchingList.get(position).getTrailerUrl(),
                                "" + continueWatchingList.get(position).getTrailerUrl(),
                                "" + continueWatchingList.get(position).getId(),
                                "" + continueWatchingList.get(position).getCategoryId(),
                                "" + continueWatchingList.get(position).getLandscape(),
                                "" + continueWatchingList.get(position).getName(),
                                "" + continueWatchingList.get(position).getVideoType(), "");
                    }
                }
            }
        });

        lyStartOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (checkLoginUser(context)) {
                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsPremium() == 1 && sectionDataList.get(position).getIsRent() == 1) {
                                if (sectionDataList.get(position).getIsBuy() == 1 ||
                                        sectionDataList.get(position).getRentBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + sectionDataList.get(position).getVideoUploadType(),
                                            0, "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getCategoryId(),
                                            "" + sectionDataList.get(position).getLandscape(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getVideoType(), "");
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (sectionDataList.get(position).getIsPremium() == 1) {
                                if (sectionDataList.get(position).getIsBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + sectionDataList.get(position).getVideoUploadType(),
                                            0, "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getCategoryId(),
                                            "" + sectionDataList.get(position).getLandscape(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getVideoType(), "");
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (sectionDataList.get(position).getIsRent() == 1) {
                                if (sectionDataList.get(position).getRentBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + sectionDataList.get(position).getVideoUploadType(),
                                            0, "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getCategoryId(),
                                            "" + sectionDataList.get(position).getLandscape(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getVideoType(), "");
                                } else {
                                    paymentForRent(context, "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getRentPrice(),
                                            "" + sectionDataList.get(position).getTypeId(),
                                            "" + sectionDataList.get(position).getVideoType());
                                }
                            } else {
                                if (sectionDataList.size() > 0) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + sectionDataList.get(position).getVideoUploadType(),
                                            0, "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getCategoryId(),
                                            "" + sectionDataList.get(position).getLandscape(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getVideoType(), "");
                                }
                            }
                        }
                    }

                    if (allVideoList != null) {
                        if (allVideoList.size() > 0) {
                            if (allVideoList.get(position).getIsPremium() == 1 && allVideoList.get(position).getIsRent() == 1) {
                                if (allVideoList.get(position).getIsBuy() == 1 ||
                                        allVideoList.get(position).getRentBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + allVideoList.get(position).getVideoUploadType(),
                                            0, "" + allVideoList.get(position).getVideo320(),
                                            "" + allVideoList.get(position).getVideo320(),
                                            "" + allVideoList.get(position).getId(),
                                            "" + allVideoList.get(position).getCategoryId(),
                                            "" + allVideoList.get(position).getLandscape(),
                                            "" + allVideoList.get(position).getName(),
                                            "" + allVideoList.get(position).getVideoType(), "");
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (allVideoList.get(position).getIsPremium() == 1) {
                                if (allVideoList.get(position).getIsBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + allVideoList.get(position).getVideoUploadType(),
                                            0, "" + allVideoList.get(position).getVideo320(),
                                            "" + allVideoList.get(position).getVideo320(),
                                            "" + allVideoList.get(position).getId(),
                                            "" + allVideoList.get(position).getCategoryId(),
                                            "" + allVideoList.get(position).getLandscape(),
                                            "" + allVideoList.get(position).getName(),
                                            "" + allVideoList.get(position).getVideoType(), "");
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (allVideoList.get(position).getIsRent() == 1) {
                                if (allVideoList.get(position).getRentBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + allVideoList.get(position).getVideoUploadType(),
                                            0, "" + allVideoList.get(position).getVideo320(),
                                            "" + allVideoList.get(position).getVideo320(),
                                            "" + allVideoList.get(position).getId(),
                                            "" + allVideoList.get(position).getCategoryId(),
                                            "" + allVideoList.get(position).getLandscape(),
                                            "" + allVideoList.get(position).getName(),
                                            "" + allVideoList.get(position).getVideoType(), "");
                                } else {
                                    paymentForRent(context, "" + allVideoList.get(position).getId(),
                                            "" + allVideoList.get(position).getName(),
                                            "" + allVideoList.get(position).getRentPrice(),
                                            "" + allVideoList.get(position).getTypeId(),
                                            "" + allVideoList.get(position).getVideoType());
                                }
                            } else {
                                if (allVideoList.size() > 0) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + allVideoList.get(position).getVideoUploadType(),
                                            0, "" + allVideoList.get(position).getVideo320(),
                                            "" + allVideoList.get(position).getVideo320(),
                                            "" + allVideoList.get(position).getId(),
                                            "" + allVideoList.get(position).getCategoryId(),
                                            "" + allVideoList.get(position).getLandscape(),
                                            "" + allVideoList.get(position).getName(),
                                            "" + allVideoList.get(position).getVideoType(), "");
                                }
                            }
                        }
                    }

                    if (relatedVideoList != null) {
                        if (relatedVideoList.size() > 0) {
                            if (relatedVideoList.get(position).getIsPremium() == 1 && relatedVideoList.get(position).getIsRent() == 1) {
                                if (relatedVideoList.get(position).getIsBuy() == 1 ||
                                        relatedVideoList.get(position).getRentBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + relatedVideoList.get(position).getVideoUploadType(),
                                            0, "" + relatedVideoList.get(position).getVideo320(),
                                            "" + relatedVideoList.get(position).getVideo320(),
                                            "" + relatedVideoList.get(position).getId(),
                                            "" + relatedVideoList.get(position).getCategoryId(),
                                            "" + relatedVideoList.get(position).getLandscape(),
                                            "" + relatedVideoList.get(position).getName(),
                                            "" + relatedVideoList.get(position).getVideoType(), "");
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (relatedVideoList.get(position).getIsPremium() == 1) {
                                if (relatedVideoList.get(position).getIsBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + relatedVideoList.get(position).getVideoUploadType(),
                                            0, "" + relatedVideoList.get(position).getVideo320(),
                                            "" + relatedVideoList.get(position).getVideo320(),
                                            "" + relatedVideoList.get(position).getId(),
                                            "" + relatedVideoList.get(position).getCategoryId(),
                                            "" + relatedVideoList.get(position).getLandscape(),
                                            "" + relatedVideoList.get(position).getName(),
                                            "" + relatedVideoList.get(position).getVideoType(), "");
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (relatedVideoList.get(position).getIsRent() == 1) {
                                if (relatedVideoList.get(position).getRentBuy() == 1) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + relatedVideoList.get(position).getVideoUploadType(),
                                            0, "" + relatedVideoList.get(position).getVideo320(),
                                            "" + relatedVideoList.get(position).getVideo320(),
                                            "" + relatedVideoList.get(position).getId(),
                                            "" + relatedVideoList.get(position).getCategoryId(),
                                            "" + relatedVideoList.get(position).getLandscape(),
                                            "" + relatedVideoList.get(position).getName(),
                                            "" + relatedVideoList.get(position).getVideoType(), "");
                                } else {
                                    paymentForRent(context, "" + relatedVideoList.get(position).getId(),
                                            "" + relatedVideoList.get(position).getName(),
                                            "" + relatedVideoList.get(position).getRentPrice(),
                                            "" + relatedVideoList.get(position).getTypeId(),
                                            "" + relatedVideoList.get(position).getVideoType());
                                }
                            } else {
                                if (relatedVideoList.size() > 0) {
                                    playVideoOnClick(((Activity) context), "Movie",
                                            "" + relatedVideoList.get(position).getVideoUploadType(),
                                            0, "" + relatedVideoList.get(position).getVideo320(),
                                            "" + relatedVideoList.get(position).getVideo320(),
                                            "" + relatedVideoList.get(position).getId(),
                                            "" + relatedVideoList.get(position).getCategoryId(),
                                            "" + relatedVideoList.get(position).getLandscape(),
                                            "" + relatedVideoList.get(position).getName(),
                                            "" + relatedVideoList.get(position).getVideoType(), "");
                                }
                            }
                        }
                    }

                    if (continueWatchingList != null) {
                        if (continueWatchingList.size() > 0) {
                            if (continueWatchingList.get(position).getIsPremium() == 1 && continueWatchingList.get(position).getIsRent() == 1) {
                                if (continueWatchingList.get(position).getIsBuy() == 1 ||
                                        continueWatchingList.get(position).getRentBuy() == 1) {
                                    if (continueWatchingList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                continueWatchingList.get(position).getStopTime(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                0, "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (continueWatchingList.get(position).getIsPremium() == 1) {
                                if (continueWatchingList.get(position).getIsBuy() == 1) {
                                    if (continueWatchingList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                continueWatchingList.get(position).getStopTime(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + continueWatchingList.get(position).getVideoUploadType(),
                                                0, "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getVideo320(),
                                                "" + continueWatchingList.get(position).getId(),
                                                "" + continueWatchingList.get(position).getCategoryId(),
                                                "" + continueWatchingList.get(position).getLandscape(),
                                                "" + continueWatchingList.get(position).getName(),
                                                "" + continueWatchingList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (continueWatchingList.get(position).getIsRent() == 1) {
                                if (continueWatchingList.get(position).getRentBuy() == 1) {
                                    playVideoOnClick(context, "Movie",
                                            "" + continueWatchingList.get(position).getVideoUploadType(),
                                            0, "" + continueWatchingList.get(position).getVideo320(),
                                            "" + continueWatchingList.get(position).getVideo320(),
                                            "" + continueWatchingList.get(position).getId(),
                                            "" + continueWatchingList.get(position).getCategoryId(),
                                            "" + continueWatchingList.get(position).getLandscape(),
                                            "" + continueWatchingList.get(position).getName(),
                                            "" + continueWatchingList.get(position).getVideoType(), "");
                                } else {
                                    paymentForRent(context, "" + continueWatchingList.get(position).getId(),
                                            "" + continueWatchingList.get(position).getName(),
                                            "" + continueWatchingList.get(position).getRentPrice(),
                                            "" + continueWatchingList.get(position).getTypeId(),
                                            "" + continueWatchingList.get(position).getVideoType());
                                }
                            } else {
                                if (continueWatchingList.size() > 0) {
                                    playVideoOnClick(context, "Movie",
                                            "" + continueWatchingList.get(position).getVideoUploadType(),
                                            0, "" + continueWatchingList.get(position).getVideo320(),
                                            "" + continueWatchingList.get(position).getVideo320(),
                                            "" + continueWatchingList.get(position).getId(),
                                            "" + continueWatchingList.get(position).getCategoryId(),
                                            "" + continueWatchingList.get(position).getLandscape(),
                                            "" + continueWatchingList.get(position).getName(),
                                            "" + continueWatchingList.get(position).getVideoType(), "");
                                }
                            }
                        }
                    }
                }
            }
        });

        lyAddRemoveWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (checkLoginUser(context)) {
                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                sectionDataList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                sectionDataList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, "" + sectionDataList.get(position).getId(),
                                    "" + sectionDataList.get(position).getVideoType(),
                                    "" + sectionDataList.get(position).getTypeId());
                        }
                    }
                    if (allVideoList != null) {
                        if (allVideoList.size() > 0) {
                            if (allVideoList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                allVideoList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                allVideoList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, "" + allVideoList.get(position).getId(),
                                    "" + allVideoList.get(position).getVideoType(),
                                    "" + allVideoList.get(position).getTypeId());
                        }
                    }
                    if (relatedVideoList != null) {
                        if (relatedVideoList.size() > 0) {
                            if (relatedVideoList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                relatedVideoList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                relatedVideoList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, "" + relatedVideoList.get(position).getId(),
                                    "" + relatedVideoList.get(position).getVideoType(),
                                    "" + relatedVideoList.get(position).getTypeId());
                        }
                    }
                    if (continueWatchingList != null) {
                        if (continueWatchingList.size() > 0) {
                            if (continueWatchingList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                continueWatchingList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                continueWatchingList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, continueWatchingList.get(position).getVideoType() == 2
                                            ? ("" + continueWatchingList.get(position).getShowId())
                                            : ("" + continueWatchingList.get(position).getId()),
                                    "" + continueWatchingList.get(position).getVideoType(),
                                    "" + continueWatchingList.get(position).getTypeId());
                        }
                    }
                }
            }
        });

        lyAddDeleteDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (checkLoginUser(context)) {
                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsDownloaded() == 1) {
                                showSnackBar((Activity) context, "DeleteDone", "");
                                sectionDataList.get(position).setIsDownloaded(0);
                                txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.download));
                                txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_downloads));
                                removeDownloadFromStorage(context, Constant.hawkVIDEOList, "" + sectionDataList.get(position).getId());
                            }
                        }
                    }
                    if (allVideoList != null) {
                        if (allVideoList.size() > 0) {
                            if (allVideoList.get(position).getIsDownloaded() == 1) {
                                showSnackBar((Activity) context, "DeleteDone", "");
                                allVideoList.get(position).setIsDownloaded(0);
                                txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.download));
                                txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_downloads));
                                removeDownloadFromStorage(context, Constant.hawkVIDEOList, "" + allVideoList.get(position).getId());
                            }
                        }
                    }
                    if (relatedVideoList != null) {
                        if (relatedVideoList.size() > 0) {
                            if (relatedVideoList.get(position).getIsDownloaded() == 1) {
                                showSnackBar((Activity) context, "DeleteDone", "");
                                relatedVideoList.get(position).setIsDownloaded(0);
                                txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.download));
                                txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_downloads));
                                removeDownloadFromStorage(context, Constant.hawkVIDEOList, "" + relatedVideoList.get(position).getId());
                            }
                        }
                    }
                    if (continueWatchingList != null) {
                        if (continueWatchingList.size() > 0) {
                            if (continueWatchingList.get(position).getIsDownloaded() == 1) {
                                showSnackBar((Activity) context, "DeleteDone", "");
                                continueWatchingList.get(position).setIsDownloaded(0);
                                txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.download));
                                txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_downloads));
                                removeDownloadFromStorage(context, Constant.hawkVIDEOList, "" + continueWatchingList.get(position).getId());
                            }
                        }
                    }
                }
            }
        });

        lyVideoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (sectionDataList != null) {
                    if (sectionDataList.size() > 0) {
                        showShareWithDialog(context, "" + sectionDataList.get(position).getName(), "" + sectionDataList.get(position).getAgeRestriction());
                    }
                }
                if (allVideoList != null) {
                    if (allVideoList.size() > 0) {
                        showShareWithDialog(context, "" + allVideoList.get(position).getName(), "" + allVideoList.get(position).getAgeRestriction());
                    }
                }
                if (relatedVideoList != null) {
                    if (relatedVideoList.size() > 0) {
                        showShareWithDialog(context, "" + relatedVideoList.get(position).getName(), "" + relatedVideoList.get(position).getAgeRestriction());
                    }
                }
                if (continueWatchingList != null) {
                    if (continueWatchingList.size() > 0) {
                        showShareWithDialog(context, "" + continueWatchingList.get(position).getName(), "" + continueWatchingList.get(position).getAgeRestriction());
                    }
                }
            }
        });

        lyViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (sectionDataList != null) {
                    if (sectionDataList.size() > 0) {
                        openDetails(context, "" + sectionDataList.get(position).getId(), "" + sectionDataList.get(position).getVideoType(),
                                "" + sectionDataList.get(position).getTypeId(), "" + sectionDataList.get(position).getUpcomingType());
                    }
                }
                if (allVideoList != null) {
                    if (allVideoList.size() > 0) {
                        openDetails(context, "" + allVideoList.get(position).getId(), "" + allVideoList.get(position).getVideoType(),
                                "" + allVideoList.get(position).getTypeId(), "" + allVideoList.get(position).getUpcomingType());
                    }
                }
                if (relatedVideoList != null) {
                    if (relatedVideoList.size() > 0) {
                        openDetails(context, "" + relatedVideoList.get(position).getId(), "" + relatedVideoList.get(position).getVideoType(),
                                "" + relatedVideoList.get(position).getTypeId(), "" + relatedVideoList.get(position).getUpcomingType());
                    }
                }
                if (continueWatchingList != null) {
                    if (continueWatchingList.size() > 0) {
                        openDetails(context, continueWatchingList.get(position).getVideoType() == 2
                                        ? ("" + continueWatchingList.get(position).getShowId())
                                        : ("" + continueWatchingList.get(position).getId()),
                                "" + continueWatchingList.get(position).getVideoType(),
                                "" + continueWatchingList.get(position).getTypeId(),
                                "" + continueWatchingList.get(position).getUpcomingType());
                    }
                }
            }
        });

    }

    public static void showLongPressedTVShowDialog(Activity context, List<Datum> sectionDataList,
                                                   List<Result> allVideoList,
                                                   List<GetRelatedVideo> relatedVideoList, List<ContinueWatching> continueWatchingList,
                                                   int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.long_pressed_video_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        TextView txtVideoTitle = bottomSheetDialog.findViewById(R.id.txtVideoTitle);
        TextView txtDuration = bottomSheetDialog.findViewById(R.id.txtDuration);
        txtDuration.setVisibility(View.GONE);
        TextView txtCommentIcon = bottomSheetDialog.findViewById(R.id.txtCommentIcon);
        TextView txtAgeLimit = bottomSheetDialog.findViewById(R.id.txtAgeLimit);
        TextView txtWatchResumeIcon = bottomSheetDialog.findViewById(R.id.txtWatchResumeIcon);
        TextView txtWatchResume = bottomSheetDialog.findViewById(R.id.txtWatchResume);
        TextView txtAddRemoveWatchlistIcon = bottomSheetDialog.findViewById(R.id.txtAddRemoveWatchlistIcon);
        TextView txtAddRemoveWatchlist = bottomSheetDialog.findViewById(R.id.txtAddRemoveWatchlist);
        TextView txtAddDeleteDownloadIcon = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownloadIcon);
        TextView txtAddDeleteDownload = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownload);
        LinearLayout lyWatchResume = bottomSheetDialog.findViewById(R.id.lyWatchResume);
        lyWatchResume.setVisibility(View.GONE);
        LinearLayout lyWatchTrailer = bottomSheetDialog.findViewById(R.id.lyWatchTrailer);
        lyWatchTrailer.setVisibility(View.GONE);
        LinearLayout lyStartOver = bottomSheetDialog.findViewById(R.id.lyStartOver);
        lyStartOver.setVisibility(View.GONE);
        LinearLayout lyAddRemoveWatchlist = bottomSheetDialog.findViewById(R.id.lyAddRemoveWatchlist);
        LinearLayout lyAddDeleteDownload = bottomSheetDialog.findViewById(R.id.lyAddDeleteDownload);
        lyAddDeleteDownload.setVisibility(View.GONE);
        LinearLayout lyVideoShare = bottomSheetDialog.findViewById(R.id.lyVideoShare);
        LinearLayout lyViewDetails = bottomSheetDialog.findViewById(R.id.lyViewDetails);
        LinearLayout lyHideThisVideo = bottomSheetDialog.findViewById(R.id.lyHideThisVideo);

        /* Prime & Rent TAG init */
        LinearLayout lyPrimeTag = bottomSheetDialog.findViewById(R.id.lyPrimeTag);
        LinearLayout lyRentTag = bottomSheetDialog.findViewById(R.id.lyRentTag);
        TextView txtPrimeTag = bottomSheetDialog.findViewById(R.id.txtPrimeTag);
        TextView txtCurrencySymbol = bottomSheetDialog.findViewById(R.id.txtCurrencySymbol);
        TextView txtRentTag = bottomSheetDialog.findViewById(R.id.txtRentTag);

        PrefManager prefManager = new PrefManager(context);

        if (sectionDataList != null) {
            if (sectionDataList.size() > 0) {
                txtVideoTitle.setText("" + sectionDataList.get(position).getName());
                if (!TextUtils.isEmpty(sectionDataList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + sectionDataList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (sectionDataList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (sectionDataList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (sectionDataList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        if (allVideoList != null) {
            if (allVideoList.size() > 0) {
                txtVideoTitle.setText("" + allVideoList.get(position).getName());
                if (!TextUtils.isEmpty(allVideoList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + allVideoList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (allVideoList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (allVideoList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (allVideoList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        if (relatedVideoList != null) {
            if (relatedVideoList.size() > 0) {
                txtVideoTitle.setText("" + relatedVideoList.get(position).getName());
                if (!TextUtils.isEmpty(relatedVideoList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + relatedVideoList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (relatedVideoList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (relatedVideoList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (relatedVideoList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        if (continueWatchingList != null) {
            if (continueWatchingList.size() > 0) {
                txtVideoTitle.setText("" + continueWatchingList.get(position).getName());
                if (!TextUtils.isEmpty(continueWatchingList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + continueWatchingList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (continueWatchingList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (continueWatchingList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (continueWatchingList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        lyAddRemoveWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (checkLoginUser(context)) {
                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                sectionDataList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                sectionDataList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, "" + sectionDataList.get(position).getId(),
                                    "" + sectionDataList.get(position).getVideoType(), "" + sectionDataList.get(position).getTypeId());
                        }
                    }
                    if (allVideoList != null) {
                        if (allVideoList.size() > 0) {
                            if (allVideoList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                allVideoList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                allVideoList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, "" + allVideoList.get(position).getId(),
                                    "" + allVideoList.get(position).getVideoType(), "" + allVideoList.get(position).getTypeId());
                        }
                    }
                    if (relatedVideoList != null) {
                        if (relatedVideoList.size() > 0) {
                            if (relatedVideoList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                relatedVideoList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                relatedVideoList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, "" + relatedVideoList.get(position).getId(),
                                    "" + relatedVideoList.get(position).getVideoType(), "" + relatedVideoList.get(position).getTypeId());
                        }
                    }
                    if (continueWatchingList != null) {
                        if (continueWatchingList.size() > 0) {
                            if (continueWatchingList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                continueWatchingList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                continueWatchingList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, continueWatchingList.get(position).getVideoType() == 2
                                            ? ("" + continueWatchingList.get(position).getShowId())
                                            : ("" + continueWatchingList.get(position).getId()),
                                    "" + continueWatchingList.get(position).getVideoType(), "" + continueWatchingList.get(position).getTypeId());
                        }
                    }
                }
            }
        });

        lyVideoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (sectionDataList != null) {
                    if (sectionDataList.size() > 0) {
                        showShareWithDialog(context, "" + sectionDataList.get(position).getName(), "" + sectionDataList.get(position).getAgeRestriction());
                    }
                }
                if (allVideoList != null) {
                    if (allVideoList.size() > 0) {
                        showShareWithDialog(context, "" + allVideoList.get(position).getName(), "" + allVideoList.get(position).getAgeRestriction());
                    }
                }
                if (relatedVideoList != null) {
                    if (relatedVideoList.size() > 0) {
                        showShareWithDialog(context, "" + relatedVideoList.get(position).getName(), "" + relatedVideoList.get(position).getAgeRestriction());
                    }
                }
                if (continueWatchingList != null) {
                    if (continueWatchingList.size() > 0) {
                        showShareWithDialog(context, "" + continueWatchingList.get(position).getName(), "" + continueWatchingList.get(position).getAgeRestriction());
                    }
                }
            }
        });

        lyViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (sectionDataList != null) {
                    if (sectionDataList.size() > 0) {
                        openDetails(context, "" + sectionDataList.get(position).getId(), "" + sectionDataList.get(position).getVideoType(),
                                "" + sectionDataList.get(position).getTypeId(), "" + sectionDataList.get(position).getUpcomingType());
                    }
                }
                if (allVideoList != null) {
                    if (allVideoList.size() > 0) {
                        openDetails(context, "" + allVideoList.get(position).getId(), "" + allVideoList.get(position).getVideoType(),
                                "" + allVideoList.get(position).getTypeId(), "" + allVideoList.get(position).getUpcomingType());
                    }
                }
                if (relatedVideoList != null) {
                    if (relatedVideoList.size() > 0) {
                        openDetails(context, "" + relatedVideoList.get(position).getId(), "" + relatedVideoList.get(position).getVideoType(),
                                "" + relatedVideoList.get(position).getTypeId(), "" + relatedVideoList.get(position).getUpcomingType());
                    }
                }
                if (continueWatchingList != null) {
                    if (continueWatchingList.size() > 0) {
                        openDetails(context, continueWatchingList.get(position).getVideoType() == 2
                                        ? ("" + continueWatchingList.get(position).getShowId())
                                        : ("" + continueWatchingList.get(position).getId()),
                                "" + continueWatchingList.get(position).getVideoType(),
                                "" + continueWatchingList.get(position).getTypeId(),
                                "" + continueWatchingList.get(position).getUpcomingType());
                    }
                }
            }
        });

    }

    public static void showLongPressedChannelDialog(Activity context, List<com.cinefilmz.tv.Model.SectionChannelModel.Datum> sectionDataList, int position) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.long_pressed_video_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        TextView txtVideoTitle = bottomSheetDialog.findViewById(R.id.txtVideoTitle);
        TextView txtDuration = bottomSheetDialog.findViewById(R.id.txtDuration);
        TextView txtCommentIcon = bottomSheetDialog.findViewById(R.id.txtCommentIcon);
        TextView txtAgeLimit = bottomSheetDialog.findViewById(R.id.txtAgeLimit);
        TextView txtWatchResumeIcon = bottomSheetDialog.findViewById(R.id.txtWatchResumeIcon);
        TextView txtWatchResume = bottomSheetDialog.findViewById(R.id.txtWatchResume);
        TextView txtAddRemoveWatchlistIcon = bottomSheetDialog.findViewById(R.id.txtAddRemoveWatchlistIcon);
        TextView txtAddRemoveWatchlist = bottomSheetDialog.findViewById(R.id.txtAddRemoveWatchlist);
        TextView txtAddDeleteDownloadIcon = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownloadIcon);
        TextView txtAddDeleteDownload = bottomSheetDialog.findViewById(R.id.txtAddDeleteDownload);
        LinearLayout lyWatchResume = bottomSheetDialog.findViewById(R.id.lyWatchResume);
        LinearLayout lyWatchTrailer = bottomSheetDialog.findViewById(R.id.lyWatchTrailer);
        LinearLayout lyStartOver = bottomSheetDialog.findViewById(R.id.lyStartOver);
        LinearLayout lyAddRemoveWatchlist = bottomSheetDialog.findViewById(R.id.lyAddRemoveWatchlist);
        LinearLayout lyAddDeleteDownload = bottomSheetDialog.findViewById(R.id.lyAddDeleteDownload);
        LinearLayout lyVideoShare = bottomSheetDialog.findViewById(R.id.lyVideoShare);
        LinearLayout lyViewDetails = bottomSheetDialog.findViewById(R.id.lyViewDetails);
        LinearLayout lyHideThisVideo = bottomSheetDialog.findViewById(R.id.lyHideThisVideo);

        /* Prime & Rent TAG init */
        LinearLayout lyPrimeTag = bottomSheetDialog.findViewById(R.id.lyPrimeTag);
        LinearLayout lyRentTag = bottomSheetDialog.findViewById(R.id.lyRentTag);
        TextView txtPrimeTag = bottomSheetDialog.findViewById(R.id.txtPrimeTag);
        TextView txtCurrencySymbol = bottomSheetDialog.findViewById(R.id.txtCurrencySymbol);
        TextView txtRentTag = bottomSheetDialog.findViewById(R.id.txtRentTag);

        PrefManager prefManager = new PrefManager(context);

        if (sectionDataList != null) {
            if (sectionDataList.size() > 0) {
                txtVideoTitle.setText("" + sectionDataList.get(position).getName());
                if (!TextUtils.isEmpty(sectionDataList.get(position).getAgeRestriction())) {
                    txtAgeLimit.setVisibility(View.VISIBLE);
                    txtAgeLimit.setText("" + sectionDataList.get(position).getAgeRestriction());
                } else {
                    txtAgeLimit.setVisibility(View.GONE);
                }
                if (sectionDataList.get(position).getIsPremium() == 1) {
                    lyPrimeTag.setVisibility(View.VISIBLE);
                } else {
                    lyPrimeTag.setVisibility(View.GONE);
                }
                if (sectionDataList.get(position).getIsRent() == 1) {
                    txtCurrencySymbol.setText("" + prefManager.getValue("currency_code"));
                    lyRentTag.setVisibility(View.VISIBLE);
                } else {
                    lyRentTag.setVisibility(View.GONE);
                }

                if (sectionDataList.get(position).getVideoType() == 2) {
                    lyWatchResume.setVisibility(View.GONE);
                    lyStartOver.setVisibility(View.GONE);
                    lyWatchTrailer.setVisibility(View.GONE);
                } else {
                    lyWatchResume.setVisibility(View.VISIBLE);
                    lyStartOver.setVisibility(View.VISIBLE);
                    lyWatchTrailer.setVisibility(View.VISIBLE);
                    if (sectionDataList.get(position).getVideoDuration() > 0) {
                        txtDuration.setVisibility(View.VISIBLE);
                        txtDuration.setText("" + Utility.covertToText(Long.parseLong("" + sectionDataList.get(position).getVideoDuration())));
                    } else {
                        txtDuration.setVisibility(View.GONE);
                    }
                    if (sectionDataList.get(position).getStopTime() > 0) {
                        lyStartOver.setVisibility(View.VISIBLE);
                        txtWatchResume.setText("" + context.getResources().getString(R.string.resume));
                        txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_resume));
                    } else {
                        lyStartOver.setVisibility(View.GONE);
                        txtWatchResumeIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_play2));
                        txtWatchResume.setText("" + context.getResources().getString(R.string.watch_now));
                    }
                }

                if (sectionDataList.get(position).getTypeId() == 5) {
                    lyAddDeleteDownload.setVisibility(View.GONE);
                } else {
                    if (sectionDataList.get(position).getIsDownloaded() == 1) {
                        txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_delete));
                        lyAddDeleteDownload.setVisibility(View.VISIBLE);
                        txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.delete_download));
                    } else {
                        lyAddDeleteDownload.setVisibility(View.GONE);
                    }
                }

                if (sectionDataList.get(position).getIsBookmark() == 1) {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                } else {
                    txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                    txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                }
            }
        }

        lyVideoShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                showShareWithDialog(context, sectionDataList.get(position).getName(), sectionDataList.get(position).getAgeRestriction());
            }
        });

        lyWatchTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (sectionDataList != null) {
                    if (sectionDataList.size() > 0) {
                        playVideoOnClick(context, "Trailer", "" + sectionDataList.get(position).getTrailerType(),
                                0, "" + sectionDataList.get(position).getTrailerUrl(),
                                "" + sectionDataList.get(position).getTrailerUrl(),
                                "" + sectionDataList.get(position).getId(),
                                "" + sectionDataList.get(position).getCategoryId(),
                                "" + sectionDataList.get(position).getLandscape(),
                                "" + sectionDataList.get(position).getName(),
                                "" + sectionDataList.get(position).getVideoType(), "");
                    }
                }
            }
        });

        lyWatchResume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (checkLoginUser(context)) {
                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsPremium() == 1 && sectionDataList.get(position).getIsRent() == 1) {
                                if (sectionDataList.get(position).getIsBuy() == 1 ||
                                        sectionDataList.get(position).getRentBuy() == 1) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(), "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (sectionDataList.get(position).getIsPremium() == 1) {
                                if (sectionDataList.get(position).getIsBuy() == 1) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (sectionDataList.get(position).getIsRent() == 1) {
                                if (sectionDataList.get(position).getRentBuy() == 1) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    paymentForRent(context, "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getRentPrice(),
                                            "" + sectionDataList.get(position).getTypeId(),
                                            "" + sectionDataList.get(position).getVideoType());
                                }
                            } else {
                                if (sectionDataList.size() > 0) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(), "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(((Activity) context), "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        lyStartOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (checkLoginUser(context)) {
                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsPremium() == 1 && sectionDataList.get(position).getIsRent() == 1) {
                                if (sectionDataList.get(position).getIsBuy() == 1 ||
                                        sectionDataList.get(position).getRentBuy() == 1) {
                                    playVideoOnClick(context, "Movie",
                                            "" + sectionDataList.get(position).getVideoUploadType(),
                                            0, "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getCategoryId(),
                                            "" + sectionDataList.get(position).getLandscape(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getVideoType(), "");
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (sectionDataList.get(position).getIsPremium() == 1) {
                                if (sectionDataList.get(position).getIsBuy() == 1) {
                                    playVideoOnClick(context, "Movie",
                                            "" + sectionDataList.get(position).getVideoUploadType(),
                                            0, "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getCategoryId(),
                                            "" + sectionDataList.get(position).getLandscape(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getVideoType(), "");
                                } else {
                                    openSubscription(((Activity) context));
                                }

                            } else if (sectionDataList.get(position).getIsRent() == 1) {
                                if (sectionDataList.get(position).getRentBuy() == 1) {
                                    if (sectionDataList.get(position).getStopTime() > 0) {
                                        playVideoOnClick(context, "Continue",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                sectionDataList.get(position).getStopTime(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    } else {
                                        playVideoOnClick(context, "Movie",
                                                "" + sectionDataList.get(position).getVideoUploadType(),
                                                0, "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getVideo320(),
                                                "" + sectionDataList.get(position).getId(),
                                                "" + sectionDataList.get(position).getCategoryId(),
                                                "" + sectionDataList.get(position).getLandscape(),
                                                "" + sectionDataList.get(position).getName(),
                                                "" + sectionDataList.get(position).getVideoType(), "");
                                    }
                                } else {
                                    paymentForRent(context, "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getRentPrice(),
                                            "" + sectionDataList.get(position).getTypeId(),
                                            "" + sectionDataList.get(position).getVideoType());
                                }
                            } else {
                                if (sectionDataList.size() > 0) {
                                    playVideoOnClick(context, "Movie",
                                            "" + sectionDataList.get(position).getVideoUploadType(),
                                            0, "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getVideo320(),
                                            "" + sectionDataList.get(position).getId(),
                                            "" + sectionDataList.get(position).getCategoryId(),
                                            "" + sectionDataList.get(position).getLandscape(),
                                            "" + sectionDataList.get(position).getName(),
                                            "" + sectionDataList.get(position).getVideoType(), "");
                                }
                            }
                        }
                    }
                }
            }
        });

        lyAddRemoveWatchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (checkLoginUser(context)) {
                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsBookmark() == 1) {
                                showSnackBar((Activity) context, "WatchlistRemove", "");
                                sectionDataList.get(position).setIsBookmark(0);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.add_to_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_add_icon));
                            } else {
                                showSnackBar((Activity) context, "WatchlistAdd", "");
                                sectionDataList.get(position).setIsBookmark(1);
                                txtAddRemoveWatchlist.setText("" + context.getResources().getString(R.string.remove_from_watchlist));
                                txtAddRemoveWatchlistIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_watchlist_remove));
                            }
                            addRemoveFromWatchlist(context, "" + sectionDataList.get(position).getId(),
                                    "" + sectionDataList.get(position).getVideoType(),
                                    "" + sectionDataList.get(position).getTypeId());
                        }
                    }
                }
            }
        });

        lyAddDeleteDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                if (checkLoginUser(context)) {
                    if (sectionDataList != null) {
                        if (sectionDataList.size() > 0) {
                            if (sectionDataList.get(position).getIsDownloaded() == 1) {
                                showSnackBar((Activity) context, "DeleteDone", "");
                                sectionDataList.get(position).setIsDownloaded(0);
                                txtAddDeleteDownload.setText("" + context.getResources().getString(R.string.download));
                                txtAddDeleteDownloadIcon.setBackground(context.getResources().getDrawable(R.drawable.ic_downloads));
                                removeDownloadFromStorage(context, Constant.hawkVIDEOList, "" + sectionDataList.get(position).getId());
                            }
                        }
                    }
                }
            }
        });

        lyViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                openDetails(context, "" + sectionDataList.get(position).getId(), "" + sectionDataList.get(position).getVideoType(),
                        "" + sectionDataList.get(position).getTypeId(), "" + sectionDataList.get(position).getUpcomingType());
            }
        });

    }

    public static void showShareWithDialog(Context context, String vTitle, String ageLimit) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.share_video_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        TextView txtVideoTitle = bottomSheetDialog.findViewById(R.id.txtVideoTitle);
        TextView txtCommentIcon = bottomSheetDialog.findViewById(R.id.txtCommentIcon);
        TextView txtAgeLimit = bottomSheetDialog.findViewById(R.id.txtAgeLimit);
        LinearLayout lyShareWithSMS = bottomSheetDialog.findViewById(R.id.lyShareWithSMS);
        LinearLayout lyShareWithInsta = bottomSheetDialog.findViewById(R.id.lyShareWithInsta);
        LinearLayout lyCopyLink = bottomSheetDialog.findViewById(R.id.lyCopyLink);
        LinearLayout lyShareWithMore = bottomSheetDialog.findViewById(R.id.lyShareWithMore);

        txtVideoTitle.setText("" + vTitle);
        if (!TextUtils.isEmpty(ageLimit)) {
            txtAgeLimit.setVisibility(View.VISIBLE);
            txtAgeLimit.setText("" + ageLimit);
        } else {
            txtAgeLimit.setVisibility(View.GONE);
        }

        String shareMessage = "Hey I'm watching " + vTitle + ". Check it out now on " + context.getResources().getString(R.string.app_name) + "! \n"
                + "https://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "" + context.getResources().getString(R.string.app_name));
        shareIntent.putExtra(Intent.EXTRA_TEXT, "" + shareMessage);
        shareIntent.setType("text/plain");

        lyShareWithMore.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            try {
                context.startActivity(Intent.createChooser(shareIntent, "" + context.getString(R.string.share_with)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("ShareOnMore", "ActivityNotF => " + ex.getMessage());
            }
        });

        lyShareWithInsta.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            try {
                shareIntent.setPackage("com.instagram.android");
                context.startActivity(Intent.createChooser(shareIntent, "" + context.getString(R.string.share_with)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("ShareOnMore", "ActivityNotF => " + ex.getMessage());
            }
        });

        lyShareWithSMS.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            try {
                shareIntent.setPackage("com.google.android.apps.messaging");
                context.startActivity(Intent.createChooser(shareIntent, "" + context.getString(R.string.share_with)));
            } catch (android.content.ActivityNotFoundException ex) {
                Log.e("ShareOnMore", "ActivityNotF => " + ex.getMessage());
            }
        });

        lyCopyLink.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Link", "" + shareMessage);
            clipboard.setPrimaryClip(clip);
            showSnackBar((Activity) context, "LinkCopy", "");
        });

    }

    public static void showMoreDialog(Context context, com.cinefilmz.tv.Model.SectionDetailModel.Result sectionDetailList, int stopTime) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.more_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        final LinearLayout lyWatchParty, lyShare, lyTrailer;
        final RelativeLayout rlDialog;
        rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        lyWatchParty = bottomSheetDialog.findViewById(R.id.lyWatchParty);
        lyShare = bottomSheetDialog.findViewById(R.id.lyShare);
        lyTrailer = bottomSheetDialog.findViewById(R.id.lyTrailer);

        lyTrailer.setVisibility(View.GONE);
        if (stopTime > 0) {
            lyTrailer.setVisibility(View.VISIBLE);
        }

        lyTrailer.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            playVideoOnClick(context, "Trailer", "" + sectionDetailList.getTrailerType(),
                    0, "" + sectionDetailList.getTrailerUrl(),
                    "" + sectionDetailList.getTrailerUrl(),
                    "" + sectionDetailList.getId(),
                    "" + sectionDetailList.getCategoryId(),
                    "" + sectionDetailList.getLandscape(),
                    "" + sectionDetailList.getName(),
                    "" + sectionDetailList.getVideoType(), "");
        });

        lyShare.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            showShareWithDialog(context, sectionDetailList.getName(), sectionDetailList.getAgeRestriction());
        });

    }

    public static void videoMoreDialog(Context context, com.cinefilmz.tv.Model.SectionDetailModel.Result sectionDetailList) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.more_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        final LinearLayout lyWatchParty, lyShare, lyTrailer;
        final RelativeLayout rlDialog;
        rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        lyWatchParty = bottomSheetDialog.findViewById(R.id.lyWatchParty);
        lyShare = bottomSheetDialog.findViewById(R.id.lyShare);
        lyTrailer = bottomSheetDialog.findViewById(R.id.lyTrailer);

        lyTrailer.setVisibility(View.GONE);
        if (sectionDetailList.getStopTime() > 0 && !TextUtils.isEmpty(sectionDetailList.getTrailerUrl())) {
            lyTrailer.setVisibility(View.VISIBLE);
        }

        lyTrailer.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            playVideoOnClick(context, "Trailer", "" + sectionDetailList.getTrailerType(),
                    0, "" + sectionDetailList.getTrailerUrl(),
                    "" + sectionDetailList.getTrailerUrl(),
                    "" + sectionDetailList.getId(),
                    "" + sectionDetailList.getCategoryId(),
                    "" + sectionDetailList.getLandscape(),
                    "" + sectionDetailList.getName(),
                    "" + sectionDetailList.getVideoType(), "");
        });

        lyShare.setOnClickListener(v -> {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
            showShareWithDialog(context, sectionDetailList.getName(), sectionDetailList.getAgeRestriction());
        });

    }

    public static void showLASDialog(Context context, List<Language> languageList) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.lang_audio_subtitle_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        TextView txtSupportedAudio, txtSubtitles;
        RelativeLayout rlDialog;
        rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        txtSupportedAudio = bottomSheetDialog.findViewById(R.id.txtSupportedAudio);
        txtSubtitles = bottomSheetDialog.findViewById(R.id.txtSubtitles);

        String audioLanguages = "";
        if (languageList.size() > 0) {
            for (int i = 0; i < languageList.size(); i++) {
                if (i == 0) {
                    audioLanguages = languageList.get(i).getName();
                } else {
                    audioLanguages = audioLanguages + ", " + languageList.get(i).getName();
                }
            }
            txtSupportedAudio.setText("" + audioLanguages);
        } else {
            txtSupportedAudio.setText("-");
        }
        txtSubtitles.setText("-");

    }

    public static void logoutConfirmDialog(Activity activity) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.logout_confirm_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        LinearLayout lyClickPositive = bottomSheetDialog.findViewById(R.id.lyClickPositive);
        LinearLayout lyClickNegative = bottomSheetDialog.findViewById(R.id.lyClickNegative);

        lyClickPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                clearPrefManager(activity);
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finishAffinity();
            }
        });

        lyClickNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
            }
        });

    }

    public static void deleteConfirmDialog(Activity activity) {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.delete_acc_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        LinearLayout lyClickPositive = bottomSheetDialog.findViewById(R.id.lyClickPositive);
        LinearLayout lyClickNegative = bottomSheetDialog.findViewById(R.id.lyClickNegative);

        lyClickPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                clearPrefManager(activity);
                LoginManager.getInstance().logOut();
                mGoogleSignInClient.signOut();
                Intent intent = new Intent(activity, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.finishAffinity();
            }
        });

        lyClickNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
            }
        });

    }

    //Alert Dialog
    public static void exitAlertDialog(Activity activity, String message, boolean isSuccess, boolean isFinish) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(LayoutInflater.from(activity).inflate(R.layout.exit_logout_dialog, null, false));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        RoundedImageView rivDialog = dialog.findViewById(R.id.rivDialog);
        if (isSuccess) {
            rivDialog.setImageResource(R.drawable.ic_success);
        } else {
            rivDialog.setImageResource(R.drawable.ic_warn);
        }

        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtDescription = dialog.findViewById(R.id.txtDescription);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        Button btnPositive = dialog.findViewById(R.id.btnPositive);

        txtTitle.setText("" + activity.getResources().getString(R.string.app_name));
        txtDescription.setText("" + message);

        btnNegative.setVisibility(View.GONE);
        btnPositive.setText("" + activity.getResources().getString(R.string.okay));
        if (dialog != null) {
            dialog.findViewById(R.id.btnPositive).setOnClickListener(v -> {
                dialog.dismiss();
                if (isFinish) {
                    activity.finish();
                }
            });
        }

        dialog.show();
    }

    //Alert Dialog
    public static void doubleButtonAlertDialog(Activity activity, String message, boolean isSuccess, boolean isFinish) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(LayoutInflater.from(activity).inflate(R.layout.exit_logout_dialog, null, false));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setAttributes(layoutParams);
        dialog.setCanceledOnTouchOutside(false);

        RoundedImageView rivDialog = dialog.findViewById(R.id.rivDialog);
        if (isSuccess) {
            rivDialog.setImageResource(R.drawable.ic_success);
        } else {
            rivDialog.setImageResource(R.drawable.ic_warn);
        }

        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        TextView txtDescription = dialog.findViewById(R.id.txtDescription);
        Button btnNegative = dialog.findViewById(R.id.btnNegative);
        Button btnPositive = dialog.findViewById(R.id.btnPositive);

        txtTitle.setText("" + activity.getResources().getString(R.string.app_name));
        txtDescription.setText("" + message);
        btnNegative.setText("" + activity.getResources().getString(R.string.no));
        btnPositive.setText("" + activity.getResources().getString(R.string.yes));

        if (dialog != null) {
            btnPositive.setOnClickListener(v -> {
                dialog.dismiss();
                if (isFinish) {
                    activity.finish();
                }
            });

            btnNegative.setOnClickListener(v -> {
                dialog.dismiss();
            });
        }

        dialog.show();
    }

    /**
     * Dialogs END
     **/

    /* remove_continue_watching API */
    public static void removeFromContinueWatch(Context context, String videoID, String videoType) {
        PrefManager prefManager = new PrefManager(context);

        Log.e("userId", " => " + prefManager.getLoginId());
        Log.e("videoID", " => " + videoID);
        Log.e("videoType", " => " + videoType);
        if (!TextUtils.isEmpty(videoID)) {
            Call<SuccessModel> call = BaseURL.getVideoAPI().remove_continue_watching("" + prefManager.getLoginId(),
                    "" + videoID, "" + videoType);
            call.enqueue(new Callback<SuccessModel>() {
                @Override
                public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                    try {
                        Log.e("remove_continue_watch", "Status => " + response.body().getStatus());
                        if (response.code() == 200 && response.body().getStatus() == 200) {
                            Log.e("remove_continue_watch", "Message => " + response.body().getMessage());
                        }
                    } catch (Exception e) {
                        Log.e("remove_continue_watch", "Exception => " + e);
                    }
                }

                @Override
                public void onFailure(Call<SuccessModel> call, Throwable t) {
                    Log.e("remove_continue_watch", "onFailure => " + t.getMessage());
                }
            });
        }
    }

    /* add_remove_bookmark API */
    public static void addRemoveFromWatchlist(Context context, String itemID, String videoType, String typeID) {
        PrefManager prefManager = new PrefManager(context);

        Call<SuccessModel> call = BaseURL.getVideoAPI().add_remove_bookmark("" + prefManager.getLoginId(), "" + itemID,
                "" + videoType, "" + typeID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    Log.e(TAG, "add_remove_bookmark Status => " + response.body().getStatus());
                    Log.e(TAG, "add_remove_bookmark Message => " + response.body().getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "add_remove_bookmark Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e(TAG, "add_remove_bookmark onFailure => " + t.getMessage());
            }
        });
    }

    /* **//* Remove Downloads From Local Storage & Hawk *//* **/
    public static void removeDownloadFromStorage(Context context, String listType, String itemID) {
        PrefManager prefManager = new PrefManager(context);

        Log.e("Hawk", "listType ==> " + listType);
        Log.e("Hawk", "listType ==> " + Hawk.contains(listType + prefManager.getLoginId()));
        Log.e("ListData", "itemID ==> " + itemID);
        List<DownloadVideoItem> myDownloadsList = Hawk.get((listType + prefManager.getLoginId()));
        if (myDownloadsList == null) {
            myDownloadsList = new ArrayList<>();
        }
        Log.e("================", "myDownloadsList Size ================> " + myDownloadsList.size());
        for (int i = 0; i < myDownloadsList.size(); i++) {
            Log.e("Hawk", "itemID ==> " + myDownloadsList.get(i).getId());
            if (("" + myDownloadsList.get(i).getId()).equalsIgnoreCase(itemID)) {
                String path = myDownloadsList.get(i).getPath();
                Log.e("File", "path ==> " + path);

                myDownloadsList.remove(myDownloadsList.get(i));
                Hawk.put((listType + prefManager.getLoginId()), myDownloadsList);
                File file = new File(path);
                if (file.exists()) {
                    com.cinefilmz.tv.Utils.Log.log("EXISTR");
                    try {
                        Uri imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                        ContentResolver contentResolver = context.getContentResolver();
                        int deletefile = contentResolver.delete(imageUri, null, null);
                        Log.e(TAG, "deletefile ==> " + deletefile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    file.delete();
                }
            }
        }
    }

    public static void removeShowFromStorage(Context context, String listType, String itemID) {
        PrefManager prefManager = new PrefManager(context);

        Log.e("Stored", "listType ==> " + listType);
        Log.e("Hawk", "listType ==> " + Hawk.contains(listType + prefManager.getLoginId()));
        Log.e("ListData", "itemID ==> " + itemID);
        List<DownloadVideoItem> myDownloadsList = Hawk.get((listType + prefManager.getLoginId()));
        if (myDownloadsList == null) {
            myDownloadsList = new ArrayList<>();
        }
        Log.e("================", "myDownloadsList Size ================> " + myDownloadsList.size());
        for (int i = 0; i < myDownloadsList.size(); i++) {
            Log.e("Hawk", "itemID ==> " + myDownloadsList.get(i).getId());
            Log.e("====== equals", "itemID ========> " + (("" + myDownloadsList.get(i).getId()).equalsIgnoreCase(itemID)));
            if (("" + myDownloadsList.get(i).getId()).equalsIgnoreCase(itemID)) {
                Log.e("myDownloadsList", "getPath ==> " + myDownloadsList.get(i).getPath());
                String path = myDownloadsList.get(i).getPath();
                Log.e("File", "path ==> " + path);

                myDownloadsList.remove(myDownloadsList.get(i));
                Hawk.put((listType + prefManager.getLoginId()), myDownloadsList);
                File dirFolder = new File(path);
                Log.e("File", "dirFolder ==> " + dirFolder.getPath());
                Log.e("File", "exists ==> " + dirFolder.exists());
                Log.e("File", "isDirectory ==> " + dirFolder.isDirectory());
                if (dirFolder.exists() && dirFolder.isDirectory()) {
                    com.cinefilmz.tv.Utils.Log.log("EXISTR");
                    new DirectoryCleaner(dirFolder).clean();
                    deleteDir(dirFolder);
                }
            }
        }
    }

    public static void removeEpisodeFromStorage(Context context, String listType, String showID, String seasonID, String epiID) {
        PrefManager prefManager = new PrefManager(context);

        Log.e("Stored", "listType ==> " + listType);
        Log.e("Stored", "showID ==> " + showID);
        Log.e("Hawk", "listType ==> " + Hawk.contains(listType + prefManager.getLoginId()));
        Log.e("ListData", "seasonID ==> " + seasonID);
        Log.e("ListData", "epiID ==> " + epiID);

        List<DownloadVideoItem> myDownloadsList = Hawk.get((listType + prefManager.getLoginId()));
        if (myDownloadsList == null) {
            myDownloadsList = new ArrayList<>();
        }
        List<SessionItem> mySessionList = Hawk.get((Constant.hawkSEASONList + prefManager.getLoginId() + "" + showID));
        if (mySessionList == null) {
            mySessionList = new ArrayList<>();
        }
        List<EpisodeItem> myEpisodeList = Hawk.get((Constant.hawkEPISODEList + prefManager.getLoginId() + "" + seasonID + "" + showID));
        if (myEpisodeList == null) {
            myEpisodeList = new ArrayList<>();
        }
        Log.e("myDownloadsList", "Size ========> " + myDownloadsList.size());
        Log.e("myEpisodeList", "Size ========> " + myEpisodeList.size());
        Log.e("mySessionList", "Size ========> " + mySessionList.size());

        /* Main Download Loop */
        for (int i = 0; i < myDownloadsList.size(); i++) {
            if (("" + myDownloadsList.get(i).getId()).equalsIgnoreCase("" + showID)) {
                Log.e("Stored", "ShowID ========> " + myDownloadsList.get(i).getId());
                /* Season(Session) Loop */
                for (int j = 0; j < mySessionList.size(); j++) {

                    if (("" + mySessionList.get(j).getId()).equalsIgnoreCase("" + seasonID) &&
                            ("" + mySessionList.get(j).getShowId()).equalsIgnoreCase("" + showID)) {
                        Log.e("Stored", "SessionID ========> " + mySessionList.get(j).getId());
                        /* Episode Loop */
                        for (int k = 0; k < myEpisodeList.size(); k++) {

                            Log.e("Hawk", "epiID ==> " + myEpisodeList.get(k).getId());
                            if (("" + myEpisodeList.get(k).getId()).equalsIgnoreCase(epiID)
                                    && ("" + myEpisodeList.get(k).getShowId()).equalsIgnoreCase(showID)
                                    && ("" + myEpisodeList.get(k).getSessionId()).equalsIgnoreCase(seasonID)) {
                                Log.e("Stored", "EpisodeID ========> " + myEpisodeList.get(k).getId());
                                Log.e("Stored", "SessionID ========> " + myEpisodeList.get(k).getSessionId());
                                Log.e("Stored", "ShowID ========> " + myEpisodeList.get(k).getShowId());
                                Log.e("myEpisodeList", "=======================> k = " + k);
                                String dirPath = myDownloadsList.get(i).getPath();
                                String epiPath = myEpisodeList.get(k).getVideoPath();
                                Log.e("epiPath", "=====> " + epiPath);
                                Log.e("dirPath", "=====> " + dirPath);

                                Log.e("myEpisodeList", "====BEFORE=====> " + myEpisodeList.size());
                                myEpisodeList.remove(myEpisodeList.get(k));
                                Log.e("myEpisodeList", "====AFTER=====> " + myEpisodeList.size());
                                Hawk.put(Constant.hawkEPISODEList + prefManager.getLoginId() + seasonID + showID, myEpisodeList);
                                if (myEpisodeList.size() == 0) {
                                    Log.e("mySessionList", "====BEFORE=====> " + mySessionList.size());
                                    mySessionList.remove(mySessionList.get(j));
                                    Log.e("mySessionList", "====AFTER=====> " + mySessionList.size());
                                }
                                if (myEpisodeList.size() > 0) {
                                    mySessionList.get(j).setEpisodeItems(myEpisodeList);
                                }
                                Hawk.put(Constant.hawkSEASONList + prefManager.getLoginId() + showID, mySessionList);
                                if (mySessionList.size() == 0) {
                                    Log.e("myDownloadsList", "====BEFORE=====> " + myDownloadsList.size());
                                    myDownloadsList.remove(myDownloadsList.get(i));
                                    Log.e("myDownloadsList", "====AFTER=====> " + myDownloadsList.size());
                                }
                                if (mySessionList.size() > 0) {
                                    myDownloadsList.get(i).setSessionItems(mySessionList);
                                }

                                Hawk.put(Constant.hawkSHOWList + prefManager.getLoginId(), myDownloadsList);
                                Log.e("myDownloadsList", "====SIZE=====> " + myDownloadsList.size());

                                if (myEpisodeList.size() > 0) {
                                    File file = new File(epiPath);
                                    if (file.exists()) {
                                        com.cinefilmz.tv.Utils.Log.log("EXISTR");
                                        try {
                                            Uri imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName()
                                                    + ".provider", file);
                                            ContentResolver contentResolver = context.getContentResolver();
                                            int deletefile = contentResolver.delete(imageUri, null, null);
                                            Log.e(TAG, "deletefile ==> " + deletefile);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        file.delete();
                                    }
                                } else {
                                    if (mySessionList.size() == 0) {
                                        File dirFolder = new File(dirPath);
                                        Log.e("File", "dirFolder ==> " + dirFolder.getPath());
                                        Log.e("File", "exists ==> " + dirFolder.exists());
                                        Log.e("File", "isDirectory ==> " + dirFolder.isDirectory());
                                        if (dirFolder.exists() && dirFolder.isDirectory()) {
                                            com.cinefilmz.tv.Utils.Log.log("EXISTR");
                                            new DirectoryCleaner(dirFolder).clean();
                                            deleteDir(dirFolder);
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }

                }
            }
        }

    }

    public static void checkShowInHawk(List<DownloadVideoItem> myShowList, String userID, String showID) {
        Log.e("ShowAvailability", "UserID ===> " + userID);
        Log.e("ShowAvailability", "showID ===> " + showID);

        for (int i = 0; i < myShowList.size(); i++) {
            Log.e("Hawk", "itemID ==> " + myShowList.get(i).getId());

            if (("" + myShowList.get(i).getId()).equalsIgnoreCase(showID)) {
                Log.e("myShowList", "=======================> i = " + i);
                myShowList.remove(myShowList.get(i));
                Hawk.put(Constant.hawkSHOWList + userID, myShowList);
            }

        }
    }

    public static void checkSeasonInHawk(List<SessionItem> mySeasonList, String userID, String showID, String seasonID) {
        Log.e("checkSeasonInHawk", "UserID ===> " + userID);
        Log.e("checkSeasonInHawk", "showID ===> " + showID);
        Log.e("checkSeasonInHawk", "seasonID ===> " + seasonID);

        for (int i = 0; i < mySeasonList.size(); i++) {
            Log.e("Hawk", "itemID ==> " + mySeasonList.get(i).getId());
            if (("" + mySeasonList.get(i).getId()).equalsIgnoreCase(seasonID)
                    && ("" + mySeasonList.get(i).getShowId()).equalsIgnoreCase(showID)) {
                Log.e("mySeasonList", "=======================> i = " + i);
                Log.e("ShowID", "==============> " + showID);
                Log.e("ShowID", "==============> " + seasonID);
                mySeasonList.remove(mySeasonList.get(i));
                Hawk.put(Constant.hawkSEASONList + userID + "" + showID, mySeasonList);
            }
        }
    }

    public static void checkEpisodeInHawk(List<EpisodeItem> myEpisodeList, String userID, String episodeID, String showID, String seasonID) {
        Log.e("checkEpisodeInHawk", "UserID ===> " + userID);
        Log.e("checkEpisodeInHawk", "episodeID ===> " + episodeID);
        Log.e("checkEpisodeInHawk", "showID ===> " + showID);
        Log.e("checkEpisodeInHawk", "seasonID ===> " + seasonID);

        for (int i = 0; i < myEpisodeList.size(); i++) {
            Log.e("Hawk", "itemID ==> " + myEpisodeList.get(i).getId());
            if (("" + myEpisodeList.get(i).getId()).equalsIgnoreCase(episodeID)
                    && ("" + myEpisodeList.get(i).getShowId()).equalsIgnoreCase(showID)
                    && ("" + myEpisodeList.get(i).getSessionId()).equalsIgnoreCase(seasonID)) {
                Log.e("myEpisodeList", "=======================> i = " + i);
                myEpisodeList.remove(myEpisodeList.get(i));
                Hawk.put(Constant.hawkEPISODEList + userID + "" + seasonID + "" + showID, myEpisodeList);
            }
        }
    }

    // For to Delete the directory inside list of files and inner Directory
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public static void removeDownloadImage(Context context, String filePath) {
        File file = new File(filePath);
        Log.e("Image", "file ==> " + file.getAbsolutePath());
        if (file.exists()) {
            com.cinefilmz.tv.Utils.Log.log("EXISTR");
            try {
                Uri imageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
                ContentResolver contentResolver = context.getContentResolver();
                int deletefile = contentResolver.delete(imageUri, null, null);
                Log.e(TAG, "deletefile ==> " + deletefile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            file.delete();
        }
    }

    /* add_remove_download API */
    public static void addRemoveDownload(Context context, String itemID, String vType, String typeID, String otherID) {
        PrefManager prefManager = new PrefManager(context);

        Log.e(TAG, "itemID => " + itemID);
        Log.e(TAG, "VideoType => " + vType);
        Log.e(TAG, "TypeID => " + typeID);
        Log.e(TAG, "otherID => " + otherID);

        Call<SuccessModel> call = BaseURL.getVideoAPI().add_remove_download("" + prefManager.getLoginId(), "" + itemID,
                "" + vType, "" + typeID, "" + otherID);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                try {
                    Log.e(TAG, "add_remove_download Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e(TAG, "add_remove_download Message => " + response.body().getMessage());
                    } else {
                        Log.e(TAG, "add_remove_download Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "add_remove_download Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                Log.e(TAG, "add_remove_download onFailure => " + t.getMessage());
            }
        });
    }

    public static String getTotalEpisodeFileSize(List<SessionItem> sessionList, String showID) {
        Log.e(TAG, "showID :==> " + showID);
        long addTotalFileSize = 0L;

        List<EpisodeItem> episodeDownloadList;
        Log.e(TAG, "seasonDownloadList size :==> " + sessionList.size());
        for (int j = 0; j < sessionList.size(); j++) {
            episodeDownloadList = sessionList.get(j).getEpisodeItems();
            Log.e(TAG, "episodeDownloadList size :==> " + episodeDownloadList.size());
            for (int k = 0; k < episodeDownloadList.size(); k++) {
                if (("" + episodeDownloadList.get(k).getShowId()).equalsIgnoreCase("" + showID)) {
                    /* Get Total File size from Download items */
                    addTotalFileSize += episodeDownloadList.get(k).getFileSize();
                    Log.e(TAG, "addTotalFileSize :==> " + addTotalFileSize);
                }
            }
        }

        return Functions.getStringSizeOfFile(addTotalFileSize);
    }

    public static String getTotalEpisodeDuration(List<SessionItem> sessionList, String showID) {
        Log.e(TAG, "showID :==> " + showID);
        long addTotalDuration = 0L;

        List<EpisodeItem> episodeDownloadList;
        Log.e(TAG, "seasonDownloadList size :==> " + sessionList.size());

        for (int j = 0; j < sessionList.size(); j++) {
            episodeDownloadList = sessionList.get(j).getEpisodeItems();
            Log.e(TAG, "episodeDownloadList size :==> " + episodeDownloadList.size());
            for (int k = 0; k < episodeDownloadList.size(); k++) {
                if (("" + episodeDownloadList.get(k).getShowId()).equalsIgnoreCase("" + showID)) {
                    /* Get Total Duration & Total size of File from Download items */
                    addTotalDuration += Long.parseLong(episodeDownloadList.get(k).getVideoDuration());
                    Log.e(TAG, "addTotalDuration :=> " + addTotalDuration);
                }
            }
        }

        return Functions.formatDurationInMinute(addTotalDuration);
    }
    /* **//* Remove Downloads From Local Storage & Hawk *//* **/


    public static String fileExt(String url) {
        Log.e("url", "=> " + url);
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"));
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            Log.e("extension", "=> " + ext.toLowerCase());
            return ext.toLowerCase();
        }
    }

    //DateFormation :
    public static String DateFormat(String date) {
        String finaldate = "";
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormate", "Exception => " + e);
        }

        return finaldate;
    }

    //DateFormation :
    public static String dateFormat1(String date) {
        String finaldate = "";
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm a", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormat1", "Exception => " + e);
        }

        return finaldate;
    }

    //DateFormation :
    public static String dateFormat2(String date) {
        String finaldate = "";
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormate2", "Exception => " + e);
        }

        return finaldate;
    }

    //DateFormation :
    public static String dateFormatDDMMMYYYY(String date) {
        String finaldate = "";
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date input = inputFormat.parse(date);
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);
        } catch (Exception e) {
            Log.e("DateFormate3", "Exception => " + e);
        }

        return finaldate;
    }

    /* For Daily Login Challenge */
    public static String DateCheckWithToday(String savedDate) {
        String isToday = "", finaldate;
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy");
            Date input = inputFormat.parse(savedDate);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            assert input != null;
            finaldate = outputFormat.format(input);

            Date exitdate = outputFormat.parse(finaldate);
            Date currdate = new Date();

            Log.e("currdate", "" + currdate);
            Log.e("exitdate", "" + exitdate);

            long diff = currdate.getTime() - exitdate.getTime();
            long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            Log.e("=>days", "" + days);

            // test your condition
            if (days == 0) {
                isToday = "YES";
            } else {
                isToday = "NO";
            }
        } catch (Exception e) {
            Log.e("DateCheckWithToday", "Exception => " + e);
        }
        return isToday;
    }

    //Today Day :
    public static String getToday() {
        String Today = "";
        try {
            Date currentDate = Calendar.getInstance().getTime();

            Today = "" + currentDate.getDay();
            if (Today.equalsIgnoreCase("0")) {
                Today = "7";
            }
        } catch (Exception e) {
            Log.e("getToday", "Exception => " + e);
        }
        return Today;
    }

    //Date Comparition :
    public static boolean compareDate(String date) {
        boolean isToday = false;
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = inputFormat.parse(date);
            Date currentDate = Calendar.getInstance().getTime();

            Log.e("startDate =>", "" + startDate);
            Log.e("currentDate =>", "" + currentDate);
            if (startDate.compareTo(currentDate) == 0) {
                isToday = true;
            } else if (startDate.compareTo(currentDate) < 0) {
                isToday = true;
            } else {
                isToday = false;
            }

            Log.e("isToday =>", "" + isToday);
        } catch (Exception e) {
            Log.e("compareDate", "Exception => " + e);
        }
        return isToday;
    }

    //Date Differences :
    public static long getDifference(String date) {
        long remainingTime = 0;
        try {
            @SuppressLint("SimpleDateFormat")
            DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date startDate = inputFormat.parse(date);
            Date currentDate = Calendar.getInstance().getTime();

            Log.e("startDate =>", "" + startDate);
            Log.e("currentDate =>", "" + currentDate);

            //1 minute = 60 seconds
            //1 hour = 60 x 60 = 3600
            //1 day = 3600 x 24 = 86400
            //milliseconds
            long different = startDate.getTime() - currentDate.getTime();

            if (different > 0) {
                remainingTime = different;
            }
            Log.e("remainingTime =>", "" + remainingTime);

            //just for Information
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

        } catch (Exception e) {
            Log.e("getDifference", "Exception => " + e);
        }
        return remainingTime;
    }

    public static String covertTimeToText(String dataDate) {
        String convTime = null;
        String prefix = "";
        String suffix = "ago";

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date pasTime = dateFormat.parse(dataDate);

            Date nowTime = new Date();

            Log.e("==>pastTime", "" + pasTime.getTime());
            Log.e("==>nowTime", "" + nowTime.getTime());

            long dateDiff = nowTime.getTime() - pasTime.getTime();
            Log.e("==>dateDiff", "" + (dateDiff / 1000));

            long second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
            long minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
            long hour = TimeUnit.MILLISECONDS.toHours(dateDiff);
            long day = TimeUnit.MILLISECONDS.toDays(dateDiff);

            Log.e("==>second", "" + (second));
            Log.e("==>minute", "" + (minute));
            Log.e("==>hour", "" + (hour));
            Log.e("==>day", "" + (day));

            if (second < 60 && second > 0) {
                convTime = second + " sec " + suffix;
            } else if (minute < 60 && minute > 0) {
                convTime = minute + " min " + suffix;
            } else if (hour < 24 && hour > 0) {
                convTime = hour + " hr " + suffix;
            } else if (day >= 7) {
                if (day > 360) {
                    convTime = (day / 360) + " years " + suffix;
                } else if (day > 30) {
                    convTime = (day / 30) + " months " + suffix;
                } else {
                    convTime = (day / 7) + " week " + suffix;
                }
            } else if (day < 7) {
                if (day == 1) {
                    convTime = day + " day " + suffix;
                } else {
                    convTime = day + " days " + suffix;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("ConvTimeE", "" + e.getMessage());
        }

        return convTime;
    }

    public static String covertToText(long timeInMilli) {
        String convTime = "0";

        try {
            if (timeInMilli > 0) {
                Log.e("==>timeInMilli", "" + timeInMilli);

                int seconds = (int) (timeInMilli / 1000) % 60;
                int minutes = (int) ((timeInMilli / (1000 * 60)) % 60);
                int hours = (int) ((timeInMilli / (1000 * 60 * 60)) % 24);

                Log.e("==>seconds", "" + (seconds));
                Log.e("==>minutes", "" + (minutes));
                Log.e("==>hours", "" + (hours));

                if (hours > 0) {
                    if (minutes > 0 && seconds > 0) {
                        convTime = hours + " hr " + minutes + " min " + seconds + " sec";
                    } else if (minutes > 0 && seconds == 0) {
                        convTime = hours + " hr " + minutes + " min";
                    } else if (minutes == 0 && seconds > 0) {
                        convTime = hours + " hr " + seconds + " sec";
                    } else if (minutes == 0 && seconds == 0) {
                        convTime = hours + " hr";
                    }
                } else if (minutes > 0) {
                    if (seconds > 0) {
                        convTime = minutes + " min " + seconds + " sec";
                    } else if (minutes > 0 && seconds == 0) {
                        convTime = minutes + " min";
                    }
                } else if (seconds > 0) {
                    convTime = seconds + " sec";
                }
            } else {
                convTime = "0";
            }
        } catch (Exception e) {
            Log.e("covertToText", "Exception => " + e.getMessage());
        }

        return convTime;
    }

    public static String covertToColonText(long timeInMilli) {
        String convTime = "0";

        try {
            if (timeInMilli > 0) {
                Log.e("==>timeInMilli", "" + timeInMilli);

                int seconds = (int) (timeInMilli / 1000) % 60;
                int minutes = (int) ((timeInMilli / (1000 * 60)) % 60);
                int hours = (int) ((timeInMilli / (1000 * 60 * 60)) % 24);

                Log.e("==>seconds", "" + (seconds));
                Log.e("==>minutes", "" + (minutes));
                Log.e("==>hours", "" + (hours));

                if (hours > 0) {
                    if (minutes > 0 && seconds > 0) {
                        convTime = hours + ":" + minutes + ":" + seconds + " hr";
                    } else if (minutes > 0 && seconds == 0) {
                        convTime = hours + ":" + minutes + " hr";
                    } else if (minutes == 0 && seconds > 0) {
                        convTime = hours + ":00:" + seconds + " hr";
                    } else if (minutes == 0 && seconds == 0) {
                        convTime = hours + ":00 hr";
                    }
                } else if (minutes > 0) {
                    if (seconds > 0) {
                        convTime = minutes + ":" + seconds + " min";
                    } else if (minutes > 0 && seconds == 0) {
                        convTime = minutes + ":00 min";
                    }
                } else if (seconds > 0) {
                    convTime = "00:" + seconds + " sec";
                }
            } else {
                convTime = "0";
            }
        } catch (Exception e) {
            Log.e("covertToText", "Exception => " + e.getMessage());
        }

        return convTime;
    }

    public static String remainTimeInMin(long remainWatch) {
        String convTime = "0";

        try {
            Log.e("==>remainWatch", "" + (remainWatch / 1000));

            int seconds = (int) (remainWatch / 1000) % 60;
            int minutes = (int) ((remainWatch / (1000 * 60)) % 60);
            int hours = (int) ((remainWatch / (1000 * 60 * 60)) % 24);

            Log.e("second", " ==> " + (seconds));
            Log.e("minute", " ==> " + (minutes));
            Log.e("hour", " ==> " + (hours));

            if (hours > 0) {
                if (minutes > 0 && seconds > 0) {
                    convTime = hours + " hr " + minutes + " min " + seconds + " sec";
                } else if (minutes > 0 && seconds == 0) {
                    convTime = hours + " hr " + minutes + " min";
                } else if (minutes == 0 && seconds > 0) {
                    convTime = hours + " hr " + seconds + " sec";
                } else if (minutes == 0 && seconds == 0) {
                    convTime = hours + " hr";
                }
            } else if (minutes > 0) {
                if (seconds > 0) {
                    convTime = minutes + " min " + seconds + " sec";
                } else if (minutes > 0 && seconds == 0) {
                    convTime = minutes + " min";
                }
            } else if (seconds > 0) {
                convTime = seconds + " sec";
            }

        } catch (Exception e) {
            Log.e("ConvTimeE", "Exception ==> " + e.getMessage());
        }

        return convTime;
    }

    public static Double getPercentage(int totalValue, int usedValue) {
        double percentage = 0.0;
        try {
            if (totalValue != 0) {
                percentage = ((100 * usedValue) / totalValue);
            } else {
                percentage = 0.0;
            }
            Log.e("getPercentage", "percentage ==> " + percentage);
        } catch (Exception e) {
            Log.e("getPercentage", "Exception ==> " + e);
            percentage = 0.0;
        }
        return percentage;
    }

    public static Double convertInto10(int totalValue) {
        double convertValue = 0.0;
        try {
            if (totalValue != 0) {
                convertValue = Math.round((totalValue / 100) * 10);
            } else {
                convertValue = 0.0;
            }
            Log.e("convertInto10", "convertValue ==> " + convertValue);
        } catch (Exception e) {
            Log.e("convertInto10", "Exception ==> " + e);
            convertValue = 0.0;
        }
        return convertValue;
    }

    public static void shimmerShow(ShimmerFrameLayout shimmer) {
        if (shimmer != null) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.showShimmer(true);
            shimmer.startShimmer();
        }
    }

    public static void shimmerHide(ShimmerFrameLayout shimmer) {
        if (shimmer != null && shimmer.isShimmerVisible()) {
            shimmer.stopShimmer();
            shimmer.hideShimmer();
            shimmer.setVisibility(View.GONE);
        }
    }

    public static void ProgressBarShow(Context mContext) {
        if (pDialog == null) {
            pDialog = new ProgressDialog(mContext, R.style.AlertDialogDanger);
            pDialog.setMessage("" + mContext.getResources().getString(R.string.please_wait));
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        }
    }

    public static void ProgressbarHide() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    /* Display messages using Snackbar */
    public static void showSnackBar(Activity activity, String showFor, String userName) {
        TSnackbar tSnackbar = TSnackbar.make(activity.findViewById(android.R.id.content), "", TSnackbar.LENGTH_LONG);
        tSnackbar.setActionTextColor(ContextCompat.getColor(activity, R.color.white));
        View snackbarView = tSnackbar.getView();

        Log.e("showFor", "===>>> " + showFor);
        Log.e("userName", "===>>> " + userName);
        if (showFor.equalsIgnoreCase("Welcome")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.welcome_back) + " " + userName + ".");
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.lightBlack));

        } else if (showFor.equalsIgnoreCase("DownloadDone")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.file_has_been_downloaded));
            tSnackbar.setIconLeft(R.drawable.ic_tick, 18);
            tSnackbar.setIconPadding(15);
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.white));

        } else if (showFor.equalsIgnoreCase("NoInternet")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.sorry_not_connected_to_internet));
            tSnackbar.setIconLeft(R.drawable.internet_off, 18);
            tSnackbar.setIconPadding(15);
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.failureBG));

        } else if (showFor.equalsIgnoreCase("BackToOnline")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.back_to_online));
            tSnackbar.setIconLeft(R.drawable.internet_on, 18);
            tSnackbar.setIconPadding(15);
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.green));

        } else if (showFor.equalsIgnoreCase("LinkCopy")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.link_copied));
            tSnackbar.setIconLeft(R.drawable.ic_tick, 18);
            tSnackbar.setIconPadding(15);
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.successBG));

        } else if (showFor.equalsIgnoreCase("WatchlistAdd")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.add_to_watchlist_msg));
            tSnackbar.setIconLeft(R.drawable.ic_tick, 18);
            tSnackbar.setIconPadding(15);
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.successBG));

        } else if (showFor.equalsIgnoreCase("WatchlistRemove")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.removed_from_watchlist_msg));
            tSnackbar.setIconLeft(R.drawable.ic_tick, 18);
            tSnackbar.setIconPadding(15);
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.successBG));

        } else if (showFor.equalsIgnoreCase("DeleteDone")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.delete));
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.failureBG));

        } else if (showFor.equalsIgnoreCase("NoVideo")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.no_video));
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.infoBG));

        } else if (showFor.equalsIgnoreCase("NoEpisode")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.no_episode));
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.infoBG));

        } else if (showFor.equalsIgnoreCase("NoData")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.no_data));
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.infoBG));

        } else if (showFor.equalsIgnoreCase("NoTrailer")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.no_trailer));
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.infoBG));

        } else if (showFor.equalsIgnoreCase("purchase")) {
            tSnackbar.setText("" + activity.getResources().getString(R.string.already_purchased));
            snackbarView.setBackgroundColor(ContextCompat.getColor(activity, R.color.infoBG));

        }
        TextView textView = (TextView) snackbarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(activity, R.color.white));
        tSnackbar.show();
    }

    /* check login status */
    public static boolean checkLoginUser(Activity context) {
        PrefManager prefManager = new PrefManager(context);
        if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
            return true;
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            return false;
        }
    }

    /* check login status in SplashScreen */
    public static boolean checkSplashLoginUser(Activity context) {
        PrefManager prefManager = new PrefManager(context);
        if (!prefManager.getLoginId().equalsIgnoreCase("0")) {
            return true;
        } else {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
            context.overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
            context.finish();
            return false;
        }
    }

    /* Clear Saved data From PrefManager */
    public static void clearPrefManager(Activity activity) {
        PrefManager prefManager = new PrefManager(activity);

        /* Clear UserData */
        prefManager.setLoginId("0");
        prefManager.setValue("Email", "");
        prefManager.setValue("Username", "");
        prefManager.setValue("Phone", "");
        prefManager.setValue("userType", "");
        prefManager.setValue("userImage", "");

        /* Push Notification */
        prefManager.setBool("PUSH", true);

    }

    /* this will hide the bottom mobile navigation control */
    public static void HideNavigation(Activity activity) {

        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        // This work only for android 4.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            activity.getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = activity.getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }

    }

    /* Get Firebase Token */
    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fcm_token", "empty");
    }

    /* Update User Profile missing data for Payments */
    //get Data From User
    public static void getMissingDataFromUser(Activity activity, String userType) {
        final BottomSheetDialog bsDialog = new BottomSheetDialog(activity, R.style.SheetDialog);
        bsDialog.setContentView(R.layout.add_missing_data_dialog);
        View bottomSheetInternal = bsDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bsDialog.setCanceledOnTouchOutside(false);
        bsDialog.setCancelable(false);
        bsDialog.show();

        final LinearLayout lyClose = bsDialog.findViewById(R.id.lyClose);
        final LinearLayout lySubmit = bsDialog.findViewById(R.id.lySubmit);
        final LinearLayout lyEmail = bsDialog.findViewById(R.id.lyEmail);
        final LinearLayout lyMobile = bsDialog.findViewById(R.id.lyMobile);
        final LinearLayout lyFullName = bsDialog.findViewById(R.id.lyFullName);
        final TextInputEditText etEmail = bsDialog.findViewById(R.id.etEmail);
        final TextInputEditText etFullName = bsDialog.findViewById(R.id.etFullName);
        final TextInputEditText etMobile = bsDialog.findViewById(R.id.etMobile);
        final CountryCodePicker etCountryCodePicker = bsDialog.findViewById(R.id.etCountryCodePicker);
        etCountryCodePicker.setCountryForNameCode("IN");

        if (userType.equalsIgnoreCase("3")) {
            lyEmail.setVisibility(View.VISIBLE);
            lyMobile.setVisibility(View.GONE);
            lyFullName.setVisibility(View.VISIBLE);
        } else if (userType.equalsIgnoreCase("1") || userType.equalsIgnoreCase("2") || userType.equalsIgnoreCase("4")) {
            lyEmail.setVisibility(View.GONE);
            lyMobile.setVisibility(View.VISIBLE);
            lyFullName.setVisibility(View.GONE);
        } else {
            lyEmail.setVisibility(View.VISIBLE);
            lyMobile.setVisibility(View.VISIBLE);
            lyFullName.setVisibility(View.VISIBLE);
        }

        lyClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bsDialog.isShowing()) {
                    bsDialog.setDismissWithAnimation(true);
                    bsDialog.dismiss();
                }
            }
        });

        lySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = "" + etEmail.getText().toString().trim();
                String userName = "" + etFullName.getText().toString().trim();
                String mobileNumber = "" + etMobile.getText().toString().trim();
                String coutryCode = "" + etCountryCodePicker.getSelectedCountryCode();
                Log.e("email", "" + email);
                Log.e("mobileNumber", "" + mobileNumber);
                Log.e("coutryCode", "" + coutryCode);

                if (userType.equalsIgnoreCase("3")) {
                    if (TextUtils.isEmpty(email)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_email_profile), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userName)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_your_name_profile), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                } else if (userType.equalsIgnoreCase("1") || userType.equalsIgnoreCase("2") || userType.equalsIgnoreCase("4")) {
                    if (TextUtils.isEmpty(mobileNumber)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_your_mobile_number), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(coutryCode)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_coutry_code), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    if (TextUtils.isEmpty(email)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_email_profile), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(userName)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_your_name_profile), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(mobileNumber)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_your_mobile_number), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                    if (TextUtils.isEmpty(coutryCode)) {
                        Toasty.warning(activity, "" + activity.getResources().getString(R.string.enter_coutry_code), Toasty.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (userType.equalsIgnoreCase("3")) {
                    UpdateEmailName(activity, "" + userName, "" + email, "" + mobileNumber);
                } else {
                    Intent intent = new Intent(activity, OTPVerification.class);
                    intent.putExtra("entryFrom", "Payment");
                    intent.putExtra("emailAddress", "" + email);
                    intent.putExtra("userName", "" + userName);
                    intent.putExtra("mobile", "+" + coutryCode + mobileNumber);
                    activity.startActivity(intent);
                }

                if (bsDialog.isShowing()) {
                    bsDialog.setDismissWithAnimation(true);
                    bsDialog.dismiss();
                }
            }
        });
    }

    /* update_profile API */
    public static void UpdateEmailName(Activity activity, String userName, String userEmail, String userMobile) {
        PrefManager prefManager = new PrefManager(activity);
        if (!((Activity) activity).isFinishing()) {
            ProgressBarShow(activity);
        }

        if (TextUtils.isEmpty(userMobile)) {
            userMobile = "" + prefManager.getValue("Phone");
        }
        Log.e(TAG, "userMobile ===============> " + userMobile);
        Call<ProfileModel> call = BaseURL.getVideoAPI().updateMissingData("" + prefManager.getLoginId(), "" + userName,
                "" + userMobile, "" + userEmail);
        String finalUserMobile = userMobile;
        call.enqueue(new Callback<ProfileModel>() {
            @Override
            public void onResponse(@NonNull Call<ProfileModel> call, @NonNull Response<ProfileModel> response) {
                ProgressbarHide();
                try {
                    Log.e("updateMobileEmail", "status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {
                        Log.e("updateMobileEmail", "userEmail => " + userEmail);
                        Log.e("updateMobileEmail", "finalUserMobile => " + finalUserMobile);

                        prefManager.setValue("Email", "" + userEmail);

                        if (!TextUtils.isEmpty(userName)) {
                            prefManager.setValue("Username", "" + userName);
                        }

                        if (prefManager.getValue("userType").equalsIgnoreCase("3")) {
                            prefManager.setValue("Phone", "" + finalUserMobile);
                        } else if (!TextUtils.isEmpty(finalUserMobile)) {
                            prefManager.setValue("Phone", "" + finalUserMobile);
                        }

                        Toasty.success(activity, "" + activity.getResources().getString(R.string.verification_success), Toasty.LENGTH_SHORT).show();
                    } else {
                        exitAlertDialog(activity, "" + response.body().getMessage(), false, true);
                    }
                } catch (Exception e) {
                    Log.e("updateMobileEmail", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileModel> call, @NonNull Throwable t) {
                Log.e("updateMobileEmail", "onFailure => " + t.getMessage());
                ProgressbarHide();
                exitAlertDialog(activity, "" + t.getMessage(), false, true);
            }
        });
    }

    //check Email & Mobile Number Available or Not
    public static boolean checkMissingData(Activity activity, String userType) {
        PrefManager prefManager = new PrefManager(activity);
        if (userType.equalsIgnoreCase("3")) {
            if (!TextUtils.isEmpty(prefManager.getValue("Email")) && !TextUtils.isEmpty(prefManager.getValue("Username"))) {
                return true;
            } else {
                return false;
            }
        } else {
            if (!TextUtils.isEmpty(prefManager.getValue("Phone"))) {
                return true;
            } else {
                return false;
            }
        }
    }
    /* Update User Profile missing data for Payments */

    /* ***************** generate Unique OrderID START ***************** */
    public static long generateRandomOrderID() {
        long getRandomNumber;
        String finalOID;

        Random r = new Random();
        int ran5thDigit = r.nextInt(9);
        Log.e("Random", "ran5thDigit =>>> " + ran5thDigit);

        long randomNumber = ThreadLocalRandom.current().nextLong(0, 9999999L);
        Log.e("Random", "randomNumber =>>> " + randomNumber);
        if (randomNumber < 0) {
            randomNumber = -randomNumber;
        }
        getRandomNumber = (long) randomNumber;
        Log.e("getRandomNumber", "=>>> " + getRandomNumber);

        finalOID = Constant.fixFourDigit + "" + ran5thDigit + "" + Constant.fixSixDigit + "" + getRandomNumber;
        Log.e("finalOID", "=>>> " + finalOID);

        return Long.parseLong(finalOID);
    }
    /* ***************** generate Unique OrderID END ***************** */

}
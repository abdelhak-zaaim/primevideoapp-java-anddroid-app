package com.cinefilmz.tv.Utils;

public class Constant {

    public static String BASE_URL = "https://exetechsolutions.com/api/";

    /* OneSignal App ID   */
    public static final String ONESIGNAL_APP_ID = "a003928c-aa50-4bad-ba76-837526019616";

    /* App Screen capture feature boolean */
    // After setting "setScreenCapture" boolean to "true", no one take screenshots and record video of this app
    // if set this boolean to "false", this feature will be disabled.
    public static boolean setScreenCapture = true;


    // set the profile image max size for now it is 1000 * 1000
    public static final int PROFILE_IMAGE_SIZE = 1000;
    //Select pic or not
    public static boolean isSelectPic = false;

    public static int swipeRefreshDelay = 2000;
    public static int showInfoMsgFor = 3000;

    /* InApp Purchage */
    public static boolean InApp_isLive;
    public static String PRODUCT_ID = "android.test.purchased";

    /* Paypal Credintial */
    public static String CONFIG_ENVIRONMENT = "";
    public static String PAYPAL_CLIENT_ID = "";

    /* Test/Live FlutterWave */
    public static String FWPublic_Key = "";
    public static String FWEncryption_Key = "";
    public static boolean FW_isLive;

    /* Test/Live PayUMoney */
    public static String PayUMerchant_ID = "";
    public static String PayUMerchant_Key = "";
    public static String PayUMerchant_Salt = "";
    public static boolean PayU_isLive;

    /* Test/Live PayTm */
    public static String PayTmMerchant_ID = "";
    public static String PayTmMerchant_Key = "";
    public static boolean PayTm_isLive;

    /* PayTm Parameters */
    public static final String CHANNEL_ID = "WAP";
    public static final String INDUSTRY_TYPE_ID = "Retail"; //Paytm industry type, get it from paytm credential
    public static final String LIVE_WEBSITE = "DEFAULT";
    public static final String TEST_WEBSITE = "WEBSTAGING";
    public static final String TEST_CALLBACK_URL = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=";
    public static final String LIVE_CALLBACK_URL = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=";


    /* Type for Login : 1-facebook, 2-gmail, 3-mobile otp */
    public static final String typeFacebook = "1";
    public static final String typeGmail = "2";
    public static final String typeOTP = "3";

    /*Payment OrderID pattern*/
    public static long fixFourDigit = 1317;
    public static long fixSixDigit = 161613;
    public static long orderIDSequence = 0L;

    public static String hawkVIDEOList = "myVideoList_";
    public static String hawkKIDSVIDEOList = "myKidsVideoList_";
    public static String hawkSHOWList = "myShowList_";
    public static String hawkSEASONList = "mySeasonList_";
    public static String hawkEPISODEList = "myEpisodeList_";

    /* Download Services */
    public static String VIDEO_DOWNLOAD_SERVICE = "startservice";
    public static String SHOW_DOWNLOAD_SERVICE = "startshowservice";

}
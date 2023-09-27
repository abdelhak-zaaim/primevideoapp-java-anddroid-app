package com.cinefilmz.tv.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;

public class AboutPrivacyTerms extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;

    private LinearLayout lyToolbarTitle;
    private TextView txtToolbarTitle;
    private WebView webAboutPrivacyTerms;

    private String getInfoUrl = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutprivacyterms);
        PrefManager.forceRTLIfSupported(getWindow(), AboutPrivacyTerms.this);

        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            getInfoUrl = "" + bundle.getString("loadUrl");
            Log.e("getInfoUrl", "===>>> " + getInfoUrl);
            txtToolbarTitle.setText("" + bundle.getString("mainTitle"));

            if (!TextUtils.isEmpty(getInfoUrl)) {
//                webAboutPrivacyTerms.loadData("" + getInfoUrl, "text/html; charset=utf-8", "UTF-8");
                webAboutPrivacyTerms.loadUrl("" + getInfoUrl);
            } else {
                Log.e("website", "===>>> " + prefManager.getValue("website"));
                webAboutPrivacyTerms.loadUrl("" + prefManager.getValue("website"));
            }
        }
    }

    private void init() {
        try {
            prefManager = new PrefManager(AboutPrivacyTerms.this);

            lyToolbarTitle = findViewById(R.id.lyToolbarTitle);
            lyToolbarTitle.setVisibility(View.VISIBLE);
            txtToolbarTitle = findViewById(R.id.txtToolbarTitle);
            webAboutPrivacyTerms = findViewById(R.id.webAboutPrivacyTerms);

            webAboutPrivacyTerms.setWebViewClient(new MyBrowser());
            webAboutPrivacyTerms.clearCache(true);
            webAboutPrivacyTerms.clearHistory();
            webAboutPrivacyTerms.getSettings().setLoadsImagesAutomatically(true);
            webAboutPrivacyTerms.getSettings().setJavaScriptEnabled(true);
            webAboutPrivacyTerms.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webAboutPrivacyTerms.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(webAboutPrivacyTerms.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
            }
            webAboutPrivacyTerms.setBackgroundColor(getResources().getColor(R.color.windowBG));

        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("==>url", "" + url);
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "called");
    }

}
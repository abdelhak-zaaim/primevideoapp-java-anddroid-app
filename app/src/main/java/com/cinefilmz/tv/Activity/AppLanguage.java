package com.cinefilmz.tv.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.LocaleUtils;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AppLanguage extends AppCompatActivity implements View.OnClickListener {

    private PrefManager prefManager;
    private BottomSheetDialog bottomSheetDialog;

    private LinearLayout lyLangEnglish, lyLangArabic, lyLangFrench, lyLangHindi, lyLangPashto, lyLangSwahili, lyLangTamil, lyLangTelugu, lyLangZulu;
    private TextView txtEnglish, txtEnglishIcon, txtArabic, txtArabicIcon, txtFrench, txtFrenchIcon, txtHindi, txtHindiIcon, txtPashto, txtPashtoIcon,
            txtSwahili, txtSwahiliIcon, txtTamil, txtTamilIcon, txtTelugu, txtTeluguIcon, txtZulu, txtZuluIcon;

    private String currentLanguage = "en";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        PrefManager.forceRTLIfSupported(getWindow(), AppLanguage.this);

        init();

        currentLanguage = LocaleUtils.getSelectedLanguageId();
        Log.e("currentLanguage", "=> " + currentLanguage);
        setLanguageClick("" + Utility.getAppLanguage("" + currentLanguage));
    }

    private void init() {
        try {
            prefManager = new PrefManager(AppLanguage.this);

            lyLangEnglish = findViewById(R.id.lyLangEnglish);
            lyLangArabic = findViewById(R.id.lyLangArabic);
            lyLangFrench = findViewById(R.id.lyLangFrench);
            lyLangHindi = findViewById(R.id.lyLangHindi);
            lyLangPashto = findViewById(R.id.lyLangPashto);
            lyLangSwahili = findViewById(R.id.lyLangSwahili);
            lyLangTamil = findViewById(R.id.lyLangTamil);
            lyLangTelugu = findViewById(R.id.lyLangTelugu);
            lyLangZulu = findViewById(R.id.lyLangZulu);

            txtEnglish = findViewById(R.id.txtEnglish);
            txtEnglishIcon = findViewById(R.id.txtEnglishIcon);
            txtArabic = findViewById(R.id.txtArabic);
            txtArabicIcon = findViewById(R.id.txtArabicIcon);
            txtFrench = findViewById(R.id.txtFrench);
            txtFrenchIcon = findViewById(R.id.txtFrenchIcon);
            txtHindi = findViewById(R.id.txtHindi);
            txtHindiIcon = findViewById(R.id.txtHindiIcon);
            txtPashto = findViewById(R.id.txtPashto);
            txtPashtoIcon = findViewById(R.id.txtPashtoIcon);
            txtSwahili = findViewById(R.id.txtSwahili);
            txtSwahiliIcon = findViewById(R.id.txtSwahiliIcon);
            txtTamil = findViewById(R.id.txtTamil);
            txtTamilIcon = findViewById(R.id.txtTamilIcon);
            txtTelugu = findViewById(R.id.txtTelugu);
            txtTeluguIcon = findViewById(R.id.txtTeluguIcon);
            txtZulu = findViewById(R.id.txtZulu);
            txtZuluIcon = findViewById(R.id.txtZuluIcon);

            lyLangEnglish.setOnClickListener(this);
            lyLangArabic.setOnClickListener(this);
            lyLangFrench.setOnClickListener(this);
            lyLangHindi.setOnClickListener(this);
            lyLangPashto.setOnClickListener(this);
            lyLangSwahili.setOnClickListener(this);
            lyLangTamil.setOnClickListener(this);
            lyLangTelugu.setOnClickListener(this);
            lyLangZulu.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyLangEnglish:
                setLanguageClick("English");
                showLangConfirmDialog("en");
                break;

            case R.id.lyLangArabic:
                setLanguageClick("Arabic");
                showLangConfirmDialog("ar");
                break;

            case R.id.lyLangFrench:
                setLanguageClick("French");
                showLangConfirmDialog("fr");
                break;

            case R.id.lyLangHindi:
                setLanguageClick("Hindi");
                showLangConfirmDialog("hi");
                break;

            case R.id.lyLangPashto:
                setLanguageClick("Pashto");
                showLangConfirmDialog("ps");
                break;

            case R.id.lyLangSwahili:
                setLanguageClick("Swahili");
                showLangConfirmDialog("sw");
                break;

            case R.id.lyLangTamil:
                setLanguageClick("Tamil");
                showLangConfirmDialog("ta");
                break;

            case R.id.lyLangTelugu:
                setLanguageClick("Telugu");
                showLangConfirmDialog("te");
                break;

            case R.id.lyLangZulu:
                setLanguageClick("Zulu");
                showLangConfirmDialog("zu");
                break;
        }
    }

    private void showLangConfirmDialog(String langCode) {
        bottomSheetDialog = new BottomSheetDialog(AppLanguage.this, R.style.SheetDialog);
        bottomSheetDialog.setContentView(R.layout.language_confirm_dialog);
        View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
        BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setCancelable(true);
        bottomSheetDialog.show();

        RelativeLayout rlDialog = bottomSheetDialog.findViewById(R.id.rlDialog);
        LinearLayout lyClickYes = bottomSheetDialog.findViewById(R.id.lyClickYes);
        LinearLayout lyClickNo = bottomSheetDialog.findViewById(R.id.lyClickNo);

        lyClickYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                setLocale("" + langCode);
            }
        });

        lyClickNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.setDismissWithAnimation(true);
                    bottomSheetDialog.dismiss();
                }
                setLanguageClick("" + Utility.getAppLanguage("" + currentLanguage));
            }
        });

    }

    private void setLocale(String localeName) {
        try {
            Log.e("langCode", "==>> " + localeName);
            Log.e("currentLanguage", "==>> " + currentLanguage);
            if (!localeName.equals(currentLanguage)) {
                LocaleUtils.setSelectedLanguageId(localeName);
                Intent i = AppLanguage.this.getBaseContext().getPackageManager().getLaunchIntentForPackage(AppLanguage.this.getBaseContext().getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                AppLanguage.this.finish();
            } else {
            }
        } catch (Exception e) {
            Log.e("SetLocale", "Exception => " + e.getMessage());
        }
    }

    private void setLanguageClick(String languageName) {
        Log.e("=>languageName", "" + languageName);
        if (languageName.equalsIgnoreCase("English")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        } else if (languageName.equalsIgnoreCase("Arabic")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        } else if (languageName.equalsIgnoreCase("French")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        } else if (languageName.equalsIgnoreCase("Hindi")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        } else if (languageName.equalsIgnoreCase("Pashto")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        } else if (languageName.equalsIgnoreCase("Swahili")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        } else if (languageName.equalsIgnoreCase("Tamil")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        } else if (languageName.equalsIgnoreCase("Telugu")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        } else if (languageName.equalsIgnoreCase("Zulu")) {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_lang_selected));

        } else {
            txtEnglishIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtArabicIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtFrenchIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtHindiIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtPashtoIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtSwahiliIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTamilIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtTeluguIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));
            txtZuluIcon.setBackground(getResources().getDrawable(R.drawable.round_bg_white));

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utility.ProgressbarHide();
        Log.e("onPause", "called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utility.ProgressbarHide();
        Log.e("onDestroy", "called");
        if (bottomSheetDialog != null) {
            if (bottomSheetDialog.isShowing()) {
                bottomSheetDialog.setDismissWithAnimation(true);
                bottomSheetDialog.dismiss();
            }
        }
    }

}
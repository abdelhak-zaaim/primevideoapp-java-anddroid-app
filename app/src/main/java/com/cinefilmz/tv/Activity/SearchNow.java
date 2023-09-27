package com.cinefilmz.tv.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cinefilmz.tv.Adapter.SearchSuggestionAdapter;
import com.cinefilmz.tv.Adapter.TabPagerAdapter;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Fragment.SearchedItemsF;
import com.cinefilmz.tv.Interface.GetSetCallBack;
import com.cinefilmz.tv.Interface.OnSuggestionClick;
import com.cinefilmz.tv.Model.SearchModel.SearchModel;
import com.cinefilmz.tv.Model.SearchModel.Tvshow;
import com.cinefilmz.tv.Model.SearchModel.Video;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchNow extends AppCompatActivity implements View.OnClickListener, OnSuggestionClick, TextWatcher, TabLayout.OnTabSelectedListener {

    private PrefManager prefManager;
    private static final int SPEECH_REQUEST_CODE = 0;

    private ShimmerFrameLayout shimmer;
    private TabLayout tabLayout;
    private ViewPager tabViewpager;
    private LinearLayout lySearch, lyBack, lyVoiceClearIcon, lySearchData;
    private TextView txtVoiceClearIcon, txtResultFor;
    private EditText etSearchBy;
    private RecyclerView rvSuggestions;

    private SearchSuggestionAdapter searchSuggestionAdapter;
    private List<String> suggestionList;
    private List<Video> searchVideoList;
    private List<Tvshow> searchTvShowList;

    private SearchedItemsF searchedVideosF, searchedShowsF;
    private GetSetCallBack getSetCallBack;
    private String strSearch = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        PrefManager.forceRTLIfSupported(getWindow(), SearchNow.this);

        init();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strSearch = bundle.getString("searchText");
            Log.e("==>strSearch", "" + strSearch);

            if (!TextUtils.isEmpty(strSearch)) {
                etSearchBy.setText("" + strSearch);
                ShowSearches("" + strSearch);
            } else {
                lySearchData.setVisibility(View.GONE);
            }
        }

        etSearchBy.addTextChangedListener(this);
        etSearchBy.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    strSearch = "" + etSearchBy.getText().toString().trim();
                    Log.e("strSearch =>", "" + strSearch);

                    if (strSearch.length() > 0) {
                        ShowSearches("" + strSearch);
                    } else {
                        lyVoiceClearIcon.setVisibility(View.VISIBLE);
                        lySearch.setBackground(getResources().getDrawable(R.drawable.round_bg_white_search));
                        etSearchBy.setTextColor(getResources().getColor(R.color.text_black));
                    }
                    return true;
                }
                return false;
            }
        });

        setupViewPager(tabViewpager);
        tabLayout.setupWithViewPager(tabViewpager);
    }

    private void init() {
        try {
            prefManager = new PrefManager(SearchNow.this);

            shimmer = findViewById(R.id.shimmer);

            lySearch = findViewById(R.id.lySearch);
            lyBack = findViewById(R.id.lyBack);
            lyVoiceClearIcon = findViewById(R.id.lyVoiceClearIcon);
            lySearchData = findViewById(R.id.lySearchData);

            tabLayout = findViewById(R.id.tabLayout);
            tabLayout.addOnTabSelectedListener(this);
            tabViewpager = findViewById(R.id.tabViewpager);

            txtVoiceClearIcon = findViewById(R.id.txtVoiceClearIcon);
            txtResultFor = findViewById(R.id.txtResultFor);
            etSearchBy = findViewById(R.id.etSearchBy);

            rvSuggestions = findViewById(R.id.rvSuggestions);

            lyBack.setOnClickListener(this);
            lySearch.setOnClickListener(this);
            lyVoiceClearIcon.setOnClickListener(this);
            etSearchBy.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(getSupportFragmentManager());

        searchedVideosF = new SearchedItemsF();
        tabPagerAdapter.addFragment(searchedVideosF, "" + getResources().getString(R.string.videos_));

        searchedShowsF = new SearchedItemsF();
        tabPagerAdapter.addFragment(searchedShowsF, "" + getResources().getString(R.string.shows));

        viewPager.setAdapter(tabPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Functions.showKeyboard(SearchNow.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lyBack:
                Functions.hideSoftKeyboard(SearchNow.this);
                finish();
                break;

            case R.id.etSearchBy:
                lyVoiceClearIcon.setVisibility(View.VISIBLE);
                lySearch.setBackground(getResources().getDrawable(R.drawable.round_bg_white_search));
                etSearchBy.setTextColor(getResources().getColor(R.color.text_black));
                lySearchData.setVisibility(View.GONE);
                rvSuggestions.setVisibility(View.VISIBLE);
                break;

            case R.id.lyVoiceClearIcon:
                Log.e("strSearch =>", "" + strSearch);
                if (strSearch.length() > 0) {
                    etSearchBy.addTextChangedListener(this);
                    etSearchBy.setText("");
                    strSearch = "";
                    txtVoiceClearIcon.setBackground(getResources().getDrawable(R.drawable.ic_microphone));
                } else {
                    displaySpeechRecognizer();
                }
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s.length() > 1) {
            lySearchData.setVisibility(View.GONE);
            lyVoiceClearIcon.setVisibility(View.VISIBLE);
            txtVoiceClearIcon.setBackground(getResources().getDrawable(R.drawable.ic_close));
            rvSuggestions.setVisibility(View.VISIBLE);
            strSearch = "" + s;

            GetSuggestions("" + s);
        } else {
            rvSuggestions.setVisibility(View.GONE);
            strSearch = "";
            txtVoiceClearIcon.setBackground(getResources().getDrawable(R.drawable.ic_microphone));
        }
        lySearch.setBackground(getResources().getDrawable(R.drawable.round_bg_white_search));
        etSearchBy.setTextColor(getResources().getColor(R.color.text_black));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /* search_by_name API */
    private void GetSuggestions(String sugText) {
        suggestionList = new ArrayList<>();

        Utility.shimmerShow(shimmer);
        Call<SearchModel> call = BaseURL.getVideoAPI().search_video("" + prefManager.getLoginId(), "" + sugText);
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                try {
                    Log.e("search_by_name", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getVideo() != null && response.body().getVideo().size() > 0) {
                            for (int i = 0; i < response.body().getVideo().size(); i++) {
                                suggestionList.add(response.body().getVideo().get(i).getName());
                                Log.e("suggestionList", "size =>>> " + suggestionList.size());
                            }
                        }

                        if (response.body().getTvshow() != null && response.body().getTvshow().size() > 0) {
                            for (int i = 0; i < response.body().getTvshow().size(); i++) {
                                suggestionList.add(response.body().getTvshow().get(i).getName());
                                Log.e("suggestionList", "size Final =>>> " + suggestionList.size());
                            }
                        }

                        ShowSuggestions();

                    } else {
                        Log.e("search_by_name", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("search_by_name", "Exception => " + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e("search_by_name", "onFailure => " + t.getMessage());
            }
        });
    }

    private void ShowSuggestions() {
        if (suggestionList.size() > 0) {
            searchSuggestionAdapter = new SearchSuggestionAdapter(SearchNow.this, suggestionList, SearchNow.this, "Search");
            rvSuggestions.setLayoutManager(new GridLayoutManager(SearchNow.this, 1));
            rvSuggestions.setAdapter(searchSuggestionAdapter);
            searchSuggestionAdapter.notifyDataSetChanged();

            rvSuggestions.setVisibility(View.VISIBLE);
        } else {
            rvSuggestions.setVisibility(View.GONE);
        }
    }

    private void ShowSearches(String searchFor) {
        Log.e("ShowSearches", "searchFor ==>>> " + searchFor);
        rvSuggestions.setVisibility(View.GONE);
        lyVoiceClearIcon.setVisibility(View.INVISIBLE);
        lySearchData.setVisibility(View.VISIBLE);
        lySearch.setBackground(getResources().getDrawable(R.drawable.round_bg_with_bor));
        etSearchBy.setTextColor(getResources().getColor(R.color.text_white));
        txtResultFor.setText("" + getResources().getString(R.string.results_for) + " " + "\"" + searchFor + "\"");

        AllVideoTVShow();
    }

    /* search_by_name API */
    private void AllVideoTVShow() {
        Utility.shimmerShow(shimmer);
        Call<SearchModel> call = BaseURL.getVideoAPI().search_video("" + prefManager.getLoginId(), "" + strSearch);
        call.enqueue(new Callback<SearchModel>() {
            @Override
            public void onResponse(Call<SearchModel> call, Response<SearchModel> response) {
                try {
                    searchVideoList = new ArrayList<>();
                    searchTvShowList = new ArrayList<>();
                    Log.e("search_by_name", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getVideo() != null) {
                            if (response.body().getVideo().size() > 0) {
                                searchVideoList = response.body().getVideo();
                                Log.e("searchVideoList", "size =>>> " + searchVideoList.size());
                            }
                        }
                        if (response.body().getTvshow() != null) {
                            if (response.body().getTvshow().size() > 0) {
                                searchTvShowList = response.body().getTvshow();
                                Log.e("searchTvShowList", "size =>>> " + searchTvShowList.size());
                            }
                        }
                        setDataInTabs(tabLayout.getSelectedTabPosition());

                    } else {
                        Log.e("search_by_name", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("search_by_name", "Exception => " + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<SearchModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e("search_by_name", "onFailure => " + t.getMessage());
            }
        });
    }

    /* Set Videos & TvShows */
    private void setDataInTabs(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("searchText", "" + strSearch);
        bundle.putInt("tabPos", position);
        bundle.putSerializable("videoList", (Serializable) searchVideoList);
        bundle.putSerializable("showList", (Serializable) searchTvShowList);

        if (position == 0) {
            if (searchedVideosF != null) {
                getSetCallBack = searchedVideosF;
                getSetCallBack.setBundle(bundle);
            }
        } else if (position == 1) {
            if (searchedShowsF != null) {
                getSetCallBack = searchedShowsF;
                getSetCallBack.setBundle(bundle);
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        Log.e("onTabSelected", "tab pos ==>>> " + tab.getPosition());
        setDataInTabs(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        Log.e("onTabUnselected", "tab pos ==>>> " + tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        Log.e("onTabReselected", "tab pos ==>>> " + tab.getPosition());
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // This starts the activity and populates the intent with the speech text.
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
    // This is where you process the intent and extract the speech text from the intent.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.e("==>spokenText", "" + spokenText);

            strSearch = spokenText;
            if (!TextUtils.isEmpty(strSearch)) {
                etSearchBy.setText("" + strSearch);
                ShowSearches("" + strSearch);
            }

            // Do something with spokenText.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void suggestionClick(String suggestion, int position) {
        Log.e("suggestion =>", "" + suggestion);
        Log.e("position =>", "" + position);

        etSearchBy.removeTextChangedListener(this);

        rvSuggestions.setVisibility(View.GONE);
        lySearchData.setVisibility(View.VISIBLE);
        etSearchBy.setText("" + suggestion);
        strSearch = "" + etSearchBy.getText().toString().trim();
        ShowSearches("" + strSearch);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        Functions.hideSoftKeyboard(SearchNow.this);
        Utility.shimmerHide(shimmer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabLayout.removeOnTabSelectedListener(this);
        Log.e("onDestroy", "called");
        Functions.hideSoftKeyboard(SearchNow.this);
        Utility.shimmerHide(shimmer);
    }

}
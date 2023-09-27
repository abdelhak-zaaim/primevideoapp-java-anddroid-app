package com.cinefilmz.tv.Fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Activity.SearchNow;
import com.cinefilmz.tv.Adapter.AllGenresAdapter;
import com.cinefilmz.tv.Adapter.AllLanguageAdapter;
import com.cinefilmz.tv.Adapter.BrowseByAdapter;
import com.cinefilmz.tv.Model.LangaugeModel.LangaugeModel;
import com.cinefilmz.tv.Model.SectionTypeModel.Result;
import com.cinefilmz.tv.Model.SectionTypeModel.SectionTypeModel;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.Model.GenresModel.GenresModel;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.PrefManager;
import com.cinefilmz.tv.Utils.Utility;
import com.cinefilmz.tv.WebService.BaseURL;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindF extends Fragment implements View.OnClickListener, OnItemClick {

    public FindF() {
    }

    private PrefManager prefManager;
    private View root;
    private static final int SPEECH_REQUEST_CODE = 0;

    private ShimmerFrameLayout shimmer;
    private LinearLayout lySearch, lyVoiceSearch, lyBrowseBy, lyGenres, lyLanguage, lySeeMoreGenres, lySeeMoreLanguage;
    private RecyclerView rvBrowseBy, rvGenres, rvLanguage;

    private BrowseByAdapter browseByAdapter;
    private AllGenresAdapter allGenresAdapter;
    private AllLanguageAdapter allLanguageAdapter;

    private List<Result> typeList;
    private List<com.cinefilmz.tv.Model.LangaugeModel.Result> languageList;
    private List<com.cinefilmz.tv.Model.GenresModel.Result> genresList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_find, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();

        BrowseBy();
        AllGenres();
        AllLanguage();

        return root;
    }

    private void init() {
        try {
            prefManager = new PrefManager(getActivity());

            shimmer = root.findViewById(R.id.shimmer);

            lySearch = root.findViewById(R.id.lySearch);
            lyVoiceSearch = root.findViewById(R.id.lyVoiceSearch);
            lySeeMoreGenres = root.findViewById(R.id.lySeeMoreGenres);
            lySeeMoreLanguage = root.findViewById(R.id.lySeeMoreLanguage);
            lyBrowseBy = root.findViewById(R.id.lyBrowseBy);
            lyGenres = root.findViewById(R.id.lyGenres);
            lyLanguage = root.findViewById(R.id.lyLanguage);

            rvBrowseBy = root.findViewById(R.id.rvBrowseBy);
            rvGenres = root.findViewById(R.id.rvGenres);
            rvLanguage = root.findViewById(R.id.rvLanguage);

            lySearch.setOnClickListener(this);
            lyVoiceSearch.setOnClickListener(this);
            lySeeMoreGenres.setOnClickListener(this);
            lySeeMoreLanguage.setOnClickListener(this);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lySearch:
                pushSearchF(getActivity(), "");
                break;

            case R.id.lyVoiceSearch:
                displaySpeechRecognizer();
                break;

            case R.id.lySeeMoreGenres:
                allGenresAdapter.setDataListSize(genresList.size());
                lySeeMoreGenres.setVisibility(View.GONE);
                break;

            case R.id.lySeeMoreLanguage:
                allLanguageAdapter.setDataListSize(languageList.size());
                lySeeMoreLanguage.setVisibility(View.GONE);
                break;
        }
    }

    public static void pushSearchF(Context context, String searchText) {
        try {
            Log.e("SearchF", "searchText => " + searchText);
            Intent intent = new Intent(context, SearchNow.class);
            intent.putExtra("searchText", "" + searchText);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e("pushSearchF", "Exception => " + e);
        }
    }

    /* get_type API */
    private void BrowseBy() {
        Utility.shimmerShow(shimmer);

        Call<SectionTypeModel> call = BaseURL.getVideoAPI().get_type();
        call.enqueue(new Callback<SectionTypeModel>() {
            @Override
            public void onResponse(Call<SectionTypeModel> call, Response<SectionTypeModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    Log.e("get_type", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult() != null) {
                            if (response.body().getResult().size() > 0) {
                                typeList = new ArrayList<>();
                                typeList = response.body().getResult();
                                Log.e("typeList", "" + typeList.size());

                                browseByAdapter = new BrowseByAdapter(getActivity(), "ByBrowse", typeList);
                                rvBrowseBy.setLayoutManager(new GridLayoutManager(getActivity(), 2));
                                rvBrowseBy.setAdapter(browseByAdapter);
                                browseByAdapter.notifyDataSetChanged();

                                lyBrowseBy.setVisibility(View.VISIBLE);
                            } else {
                                lyBrowseBy.setVisibility(View.GONE);
                            }
                        } else {
                            lyBrowseBy.setVisibility(View.GONE);
                        }

                    } else {
                        lyBrowseBy.setVisibility(View.GONE);
                        Log.e("get_type", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    lyBrowseBy.setVisibility(View.GONE);
                    Log.e("get_type", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<SectionTypeModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                lyBrowseBy.setVisibility(View.GONE);
                Log.e("get_type", "onFailure => " + t.getMessage());
            }
        });
    }

    /* get_category API */
    private void AllGenres() {
        Utility.shimmerShow(shimmer);

        Call<GenresModel> call = BaseURL.getVideoAPI().get_category();
        call.enqueue(new Callback<GenresModel>() {
            @Override
            public void onResponse(Call<GenresModel> call, Response<GenresModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    Log.e("get_category", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            genresList = new ArrayList<>();
                            genresList = response.body().getResult();
                            Log.e("genresList", "" + genresList.size());

                            allGenresAdapter = new AllGenresAdapter(getActivity(), genresList, "ByGenres");
                            rvGenres.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            rvGenres.setAdapter(allGenresAdapter);
                            allGenresAdapter.notifyDataSetChanged();

                            if (genresList.size() > 5) {
                                lySeeMoreGenres.setVisibility(View.VISIBLE);
                            } else {
                                lySeeMoreGenres.setVisibility(View.GONE);
                            }
                            lyGenres.setVisibility(View.VISIBLE);
                        } else {
                            lyGenres.setVisibility(View.GONE);
                        }

                    } else {
                        lyGenres.setVisibility(View.GONE);
                        Log.e("get_category", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    lyGenres.setVisibility(View.GONE);
                    Log.e("get_category", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<GenresModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                lyGenres.setVisibility(View.GONE);
                Log.e("get_category", "onFailure => " + t.getMessage());
            }
        });
    }

    /* get_language API */
    private void AllLanguage() {
        Utility.shimmerShow(shimmer);

        Call<LangaugeModel> call = BaseURL.getVideoAPI().get_language();
        call.enqueue(new Callback<LangaugeModel>() {
            @Override
            public void onResponse(Call<LangaugeModel> call, Response<LangaugeModel> response) {
                Utility.shimmerHide(shimmer);
                try {
                    Log.e("get_language", "Status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            languageList = new ArrayList<>();
                            languageList = response.body().getResult();
                            Log.e("languageList", "" + languageList.size());

                            allLanguageAdapter = new AllLanguageAdapter(getActivity(), languageList, "ByLanguage");
                            rvLanguage.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                            rvLanguage.setAdapter(allLanguageAdapter);
                            allLanguageAdapter.notifyDataSetChanged();

                            if (languageList.size() > 5) {
                                lySeeMoreLanguage.setVisibility(View.VISIBLE);
                            } else {
                                lySeeMoreLanguage.setVisibility(View.GONE);
                            }
                            lyLanguage.setVisibility(View.VISIBLE);
                        } else {
                            lyLanguage.setVisibility(View.GONE);
                        }

                    } else {
                        lyLanguage.setVisibility(View.GONE);
                        Log.e("get_language", "Message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    lyLanguage.setVisibility(View.GONE);
                    Log.e("get_language", "Exception => " + e);
                }
            }

            @Override
            public void onFailure(Call<LangaugeModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                lyLanguage.setVisibility(View.GONE);
                Log.e("get_language", "onFailure => " + t.getMessage());
            }
        });
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

            if (!TextUtils.isEmpty(spokenText)) {
                pushSearchF(getActivity(), "" + spokenText);
            }
            // Do something with spokenText.
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("onPause", "called");
        Utility.shimmerHide(shimmer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("onDestroy", "called");
        Utility.shimmerHide(shimmer);
    }

    @Override
    public void longClick(String itemID, String clickType, int position) {
        Log.e("==>itemID", "" + itemID);
        Log.e("==>clickType", "" + clickType);
        Log.e("==>position", "" + position);
    }

    @Override
    public void itemClick(String itemID, String clickType, int position) {
        Log.e("==>itemID", "" + itemID);
        Log.e("==>clickType", "" + clickType);
        Log.e("==>position", "" + position);
    }

}
package com.cinefilmz.tv.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Activity.AllPaymentActivity;
import com.cinefilmz.tv.Adapter.SubscriptionAdapter;
import com.cinefilmz.tv.Model.SubscriptionModel.Datum;
import com.cinefilmz.tv.Model.SubscriptionModel.Result;
import com.cinefilmz.tv.Model.SubscriptionModel.SubscriptionModel;
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

public class SubscriptionF extends Fragment {

    private PrefManager prefManager;
    private View root;
    private static final String TAG = SubscriptionF.class.getSimpleName();

    private ShimmerFrameLayout shimmer;
    private LinearLayout lySubscription, lyNoData, lyChooseButton;
    private CardView cvSubscription;
    private TextView txtSubTitle, txtSubPlan, txtChoosePlan;
    private RecyclerView rvSubscription;

    private SubscriptionAdapter subscriptionAdapter;

    private List<Result> subscriptionList;
    private List<Datum> benefitsList;
    private int subPosition, isSubSelected;

    public SubscriptionF() {
    }

    public static Fragment addFragment(int subPosition, int isSelected) {
        SubscriptionF fragment = new SubscriptionF();
        Bundle args = new Bundle();
        args.putInt("subPosition", subPosition);
        args.putInt("isSelected", isSelected);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_subscription, container, false);
        PrefManager.forceRTLIfSupported(getActivity().getWindow(), getActivity());

        init();

        if (getArguments() != null) {
            subPosition = getArguments().getInt("subPosition");
            isSubSelected = getArguments().getInt("isSelected");

            Log.e("subPosition", "==>> " + subPosition);
            Log.e("isSelected", "==>> " + isSubSelected);

            if (isSubSelected == 1) {
                cvSubscription.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary));
                txtSubTitle.setSelected(true);
                txtSubPlan.setSelected(true);
                lyChooseButton.setSelected(true);
                txtChoosePlan.setText("" + getResources().getString(R.string.current));
            }
            GetSubscription();
        }

        lyChooseButton.setOnClickListener(v -> {
            for (int i = 0; i < subscriptionList.size(); i++) {
                if (subscriptionList.get(i).getIsBuy() == 1) {
                    Utility.showSnackBar(getActivity(), "purchase", "");
                    return;
                }
            }
            if (Utility.checkLoginUser(getActivity())) {
                if (Utility.checkMissingData(getActivity(), "" + prefManager.getValue("userType"))) {
                    Intent intent = new Intent(getActivity(), AllPaymentActivity.class);
                    intent.putExtra("payType", "Package");
                    intent.putExtra("itemId", "" + subscriptionList.get(subPosition).getId());
                    intent.putExtra("price", "" + subscriptionList.get(subPosition).getPrice());
                    intent.putExtra("itemTitle", "" + subscriptionList.get(subPosition).getName());
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Utility.getMissingDataFromUser(getActivity(), "" + prefManager.getValue("userType"));
                }
            }
        });

        return root;
    }

    private void init() {
        try {
            prefManager = new PrefManager(getActivity());

            shimmer = root.findViewById(R.id.shimmer);

            lySubscription = root.findViewById(R.id.lySubscription);
            cvSubscription = root.findViewById(R.id.cvSubscription);
            lyChooseButton = root.findViewById(R.id.lyChooseButton);
            lyNoData = root.findViewById(R.id.lyNoData);

            rvSubscription = root.findViewById(R.id.rvSubscription);

            txtSubTitle = root.findViewById(R.id.txtSubTitle);
            txtSubPlan = root.findViewById(R.id.txtSubPlan);
            txtChoosePlan = root.findViewById(R.id.txtChoosePlan);
        } catch (Exception e) {
            Log.e("init", "Exception => " + e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void GetSubscription() {
        Utility.shimmerShow(shimmer);
        Call<SubscriptionModel> call = BaseURL.getVideoAPI().get_package("" + prefManager.getLoginId());
        call.enqueue(new Callback<SubscriptionModel>() {
            @Override
            public void onResponse(Call<SubscriptionModel> call, Response<SubscriptionModel> response) {
                try {
                    Log.e("get_package", "status => " + response.body().getStatus());
                    if (response.code() == 200 && response.body().getStatus() == 200) {

                        if (response.body().getResult().size() > 0) {
                            subscriptionList = new ArrayList<>();
                            subscriptionList = response.body().getResult();
                            Log.e("subscriptionList", "" + subscriptionList.size());

                            benefitsList = new ArrayList<>();
                            benefitsList = subscriptionList.get(subPosition).getData();

                            txtSubTitle.setText("" + subscriptionList.get(subPosition).getName());
                            txtSubPlan.setText("" + subscriptionList.get(subPosition).getPrice() + " / "
                                    + subscriptionList.get(subPosition).getTime() + " " + subscriptionList.get(subPosition).getType());

                            SetSubscriptionPackages();
                        }

                    } else {
                        Log.e("get_package", "message => " + response.body().getMessage());
                    }
                } catch (Exception e) {
                    Log.e("get_package", "Exception => " + e);
                }
                Utility.shimmerHide(shimmer);
            }

            @Override
            public void onFailure(Call<SubscriptionModel> call, Throwable t) {
                Utility.shimmerHide(shimmer);
                Log.e("get_package", "onFailure => " + t.getMessage());
            }
        });
    }

    private void SetSubscriptionPackages() {
        subscriptionAdapter = new SubscriptionAdapter(getActivity(), benefitsList, "Package", isSubSelected);
        rvSubscription.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        rvSubscription.setAdapter(subscriptionAdapter);
        subscriptionAdapter.notifyDataSetChanged();
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

}
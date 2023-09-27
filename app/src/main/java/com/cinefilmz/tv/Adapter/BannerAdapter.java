package com.cinefilmz.tv.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.cinefilmz.tv.Model.SectionBannerModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BannerAdapter extends PagerAdapter {

    private Context context;
    private List<Result> bannerList;
    private String type;
    private View imageLayout;

    public BannerAdapter(Context context, List<Result> bannerList, String type) {
        this.context = context;
        this.bannerList = bannerList;
        this.type = type;
    }

    @Override
    public int getCount() {
        return bannerList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        imageLayout = LayoutInflater.from(container.getContext()).inflate(R.layout.banner_item, container, false);

        ImageView ivBanner = imageLayout.findViewById(R.id.ivBanner);

        if (!TextUtils.isEmpty(bannerList.get(position).getLandscape())) {
            Picasso.get().load(bannerList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(ivBanner);
        } else {
            Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(ivBanner);
        }

        ivBanner.setOnClickListener(v -> {
            Log.e("position", "" + position);
            Utility.openDetails(context, "" + bannerList.get(position).getId(), "" + bannerList.get(position).getVideoType(),
                    "" + bannerList.get(position).getTypeId(), "" + bannerList.get(position).getUpcomingType());
        });

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
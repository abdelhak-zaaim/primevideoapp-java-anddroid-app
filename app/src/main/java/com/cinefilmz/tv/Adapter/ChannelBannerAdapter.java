package com.cinefilmz.tv.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.cinefilmz.tv.players.PlayerActivity;
import com.cinefilmz.tv.Model.SectionChannelModel.LiveUrl;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChannelBannerAdapter extends PagerAdapter {

    private Activity context;
    private List<LiveUrl> channelBannerList;
    private String from, type;
    private View imageLayout;

    public ChannelBannerAdapter(Activity context, List<LiveUrl> channelBannerList, String type) {
        this.context = context;
        this.channelBannerList = channelBannerList;
        this.type = type;
    }

    @Override
    public int getCount() {
        return channelBannerList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        if (type.equalsIgnoreCase("ChannelAllVideo")) {
            imageLayout = LayoutInflater.from(container.getContext()).inflate(R.layout.channel_allvideo_banner_item, container, false);
        } else {
            imageLayout = LayoutInflater.from(container.getContext()).inflate(R.layout.channel_banner_item, container, false);
        }

        ImageView ivBanner = imageLayout.findViewById(R.id.ivBanner);

        if (!TextUtils.isEmpty(channelBannerList.get(position).getImage())) {
            Picasso.get().load(channelBannerList.get(position).getImage()).placeholder(R.drawable.no_image_land).into(ivBanner);
        } else {
            Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(ivBanner);
        }

        ivBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("position", "" + position);
                if (Utility.checkLoginUser(context)) {
                    if (Utility.checkPrimeUser(context, channelBannerList.get(position).getIsBuy())) {
                        if (!TextUtils.isEmpty(channelBannerList.get(position).getLink())) {
                            Intent intent = new Intent(context, PlayerActivity.class);
                            intent.putExtra("videoFrom", "LiveChannel");
                            intent.putExtra("url", "" + channelBannerList.get(position).getLink());
                            intent.putExtra("itemID", "" + channelBannerList.get(position).getId());
                            intent.putExtra("image", "" + channelBannerList.get(position).getImage());
                            context.startActivity(intent);
                        }
                    }
                }
            }
        });

        container.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
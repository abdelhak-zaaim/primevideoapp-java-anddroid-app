package com.cinefilmz.tv.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.RentProductModel.Tvshow;
import com.cinefilmz.tv.Model.RentProductModel.Video;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RentPurchaseAdapter extends RecyclerView.Adapter<RentPurchaseAdapter.MyViewHolder> {

    private Context context;
    private List<Video> rentVideoList;
    private List<Tvshow> rentTvShowList;
    private OnItemClick onItemClick;
    private String from, type, currencySymbol;

    public RentPurchaseAdapter(Context context, List<Video> rentVideoList, OnItemClick onItemClick, String from, String currencySymbol) {
        this.context = context;
        this.rentVideoList = rentVideoList;
        this.onItemClick = onItemClick;
        this.from = from;
        this.currencySymbol = currencySymbol;
    }

    public RentPurchaseAdapter(Context context, List<Tvshow> rentTvShowList, OnItemClick onItemClick, String from, String type, String currencySymbol) {
        this.context = context;
        this.rentTvShowList = rentTvShowList;
        this.onItemClick = onItemClick;
        this.from = from;
        this.type = type;
        this.currencySymbol = currencySymbol;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivThumb;
        LinearLayout lyContent, lyNewTag;
        TextView txtNewTag;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            ivThumb = view.findViewById(R.id.ivThumb);
            lyNewTag = view.findViewById(R.id.lyNewTag);
            txtNewTag = view.findViewById(R.id.txtNewTag);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_landscape_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (from.equalsIgnoreCase("Video")) {
            if (!TextUtils.isEmpty("" + rentVideoList.get(position).getLandscape())) {
                Picasso.get().load("" + rentVideoList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else if (!TextUtils.isEmpty("" + rentVideoList.get(position).getThumbnail())) {
                Picasso.get().load("" + rentVideoList.get(position).getThumbnail()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
            if (!TextUtils.isEmpty(rentVideoList.get(position).getReleaseTag())) {
                holder.lyNewTag.setVisibility(View.VISIBLE);
                holder.txtNewTag.setText("" + rentVideoList.get(position).getReleaseTag());
            } else {
                holder.lyNewTag.setVisibility(View.GONE);
            }
        } else {
            holder.lyNewTag.setVisibility(View.GONE);
            if (!TextUtils.isEmpty("" + rentTvShowList.get(position).getLandscape())) {
                Picasso.get().load("" + rentTvShowList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else if (!TextUtils.isEmpty("" + rentTvShowList.get(position).getThumbnail())) {
                Picasso.get().load("" + rentTvShowList.get(position).getThumbnail()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("LongClick", "pos => " + position);
                if (from.equalsIgnoreCase("Video")) {
                    onItemClick.itemClick("" + rentVideoList.get(position).getId(), "Video", position);
                } else {
                    onItemClick.itemClick("" + rentTvShowList.get(position).getId(), "Show", position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (from.equalsIgnoreCase("Video")) {
            return rentVideoList.size();
        } else {
            return rentTvShowList.size();
        }
    }

}
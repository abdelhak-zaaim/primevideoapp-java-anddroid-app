package com.cinefilmz.tv.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.RentProductModel.Video;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RentVideoAdapter extends RecyclerView.Adapter<RentVideoAdapter.MyViewHolder> {

    private Context context;
    private String currencyCode, layoutType;
    private List<Video> rentVideoList;

    public RentVideoAdapter(Context context, List<Video> rentVideoList, String currencyCode, String layoutType) {
        this.context = context;
        this.rentVideoList = rentVideoList;
        this.currencyCode = currencyCode;
        this.layoutType = layoutType;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivRent;
        TextView txtRentPrice;
        LinearLayout lyContent;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            ivRent = view.findViewById(R.id.ivRent);
            txtRentPrice = view.findViewById(R.id.txtRentPrice);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (layoutType.equalsIgnoreCase("landscape")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_fix_land_item, parent, false);
        } else if (layoutType.equalsIgnoreCase("potrait")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_fix_potr_item, parent, false);
        } else if (layoutType.equalsIgnoreCase("square")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_fix_square_item, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rent_fix_land_item, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtRentPrice.setText(currencyCode + "" + rentVideoList.get(position).getRentPrice());
        if (!TextUtils.isEmpty("" + rentVideoList.get(position).getLandscape())) {
            Picasso.get().load("" + rentVideoList.get(position).getLandscape()).into(holder.ivRent);
        } else if (!TextUtils.isEmpty("" + rentVideoList.get(position).getThumbnail())) {
            Picasso.get().load("" + rentVideoList.get(position).getThumbnail()).into(holder.ivRent);
        } else {
            Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivRent);
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openDetails(context, "" + rentVideoList.get(position).getId(),
                        "" + rentVideoList.get(position).getVideoType(), "" + rentVideoList.get(position).getTypeId(),
                        "" + rentVideoList.get(position).getUpcomingType());
            }
        });

    }

    @Override
    public int getItemCount() {
        return rentVideoList.size();
    }

}
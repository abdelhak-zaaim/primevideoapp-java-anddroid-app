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

import com.cinefilmz.tv.Model.RentProductModel.Tvshow;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RentShowAdapter extends RecyclerView.Adapter<RentShowAdapter.MyViewHolder> {

    private Context context;
    private String currencyCode, layoutType;
    private List<Tvshow> rentShowList;

    public RentShowAdapter(Context context, List<Tvshow> rentShowList, String currencyCode, String layoutType) {
        this.context = context;
        this.rentShowList = rentShowList;
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

        holder.txtRentPrice.setText(currencyCode + "" + rentShowList.get(position).getRentPrice());
        if (!TextUtils.isEmpty("" + rentShowList.get(position).getLandscape())) {
            Picasso.get().load("" + rentShowList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivRent);
        } else if (!TextUtils.isEmpty("" + rentShowList.get(position).getThumbnail())) {
            Picasso.get().load("" + rentShowList.get(position).getThumbnail()).placeholder(R.drawable.no_image_land).into(holder.ivRent);
        } else {
            Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivRent);
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openDetails(context, "" + rentShowList.get(position).getId(),
                        "" + rentShowList.get(position).getVideoType(), "" + rentShowList.get(position).getTypeId(),
                        "" + rentShowList.get(position).getUpcomingType());
            }
        });

    }

    @Override
    public int getItemCount() {
        return rentShowList.size();
    }

}
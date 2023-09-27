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

import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.Model.SearchModel.Tvshow;
import com.cinefilmz.tv.Model.SearchModel.Video;
import com.cinefilmz.tv.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SearchItemsAdapter extends RecyclerView.Adapter<SearchItemsAdapter.MyViewHolder> {

    private Context context;
    private List<Video> searchVideoList;
    private List<Tvshow> searchTvShowList;
    private OnItemClick onItemClick;
    private String from, type, currencyCode;

    public SearchItemsAdapter(Context context, List<Video> searchVideoList, OnItemClick onItemClick, String from, String currencyCode) {
        this.context = context;
        this.searchVideoList = searchVideoList;
        this.onItemClick = onItemClick;
        this.from = from;
        this.currencyCode = currencyCode;
    }

    public SearchItemsAdapter(Context context, List<Tvshow> searchTvShowList, OnItemClick onItemClick, String from, String type, String currencyCode) {
        this.context = context;
        this.searchTvShowList = searchTvShowList;
        this.onItemClick = onItemClick;
        this.from = from;
        this.type = type;
        this.currencyCode = currencyCode;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivThumb;
        LinearLayout lyContent, lyNewReleaseTag, lyViewDetails, lyRentTag;
        TextView txtTitle, txtNewReleaseTag, txtPrimeTag, txtRentTag, txtCurrencySymbol, txtViewDetail, txtCategory;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            lyNewReleaseTag = view.findViewById(R.id.lyNewReleaseTag);
            lyViewDetails = view.findViewById(R.id.lyViewDetails);
            lyRentTag = view.findViewById(R.id.lyRentTag);
            ivThumb = view.findViewById(R.id.ivThumb);
            txtPrimeTag = view.findViewById(R.id.txtPrimeTag);
            txtCurrencySymbol = view.findViewById(R.id.txtCurrencySymbol);
            txtRentTag = view.findViewById(R.id.txtRentTag);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtNewReleaseTag = view.findViewById(R.id.txtNewReleaseTag);
            txtViewDetail = view.findViewById(R.id.txtViewDetail);
            txtCategory = view.findViewById(R.id.txtCategory);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (from.equalsIgnoreCase("Video")) {
            holder.txtTitle.setText("" + searchVideoList.get(position).getName());
            if (!TextUtils.isEmpty(searchVideoList.get(position).getCategoryName())) {
                holder.txtCategory.setVisibility(View.VISIBLE);
                holder.txtCategory.setText("" + searchVideoList.get(position).getCategoryName());
            } else {
                holder.txtCategory.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty("" + searchVideoList.get(position).getLandscape())) {
                Picasso.get().load("" + searchVideoList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
            if (!TextUtils.isEmpty(searchVideoList.get(position).getReleaseTag())) {
                holder.lyNewReleaseTag.setVisibility(View.VISIBLE);
                holder.txtNewReleaseTag.setText("" + searchVideoList.get(position).getReleaseTag());
            } else {
                holder.lyNewReleaseTag.setVisibility(View.GONE);
            }
            if (searchVideoList.get(position).getIsPremium() == 1) {
                holder.txtPrimeTag.setVisibility(View.VISIBLE);
                holder.txtPrimeTag.setText("" + context.getResources().getString(R.string.prime));
            } else {
                holder.txtPrimeTag.setVisibility(View.GONE);
            }
            if (searchVideoList.get(position).getIsRent() == 1) {
                holder.lyRentTag.setVisibility(View.VISIBLE);
                holder.txtCurrencySymbol.setText("" + currencyCode);
            } else {
                holder.lyRentTag.setVisibility(View.GONE);
            }
        } else {
            holder.txtTitle.setText("" + searchTvShowList.get(position).getName());
            if (!TextUtils.isEmpty(searchTvShowList.get(position).getCategoryName())) {
                holder.txtCategory.setVisibility(View.VISIBLE);
                holder.txtCategory.setText("" + searchTvShowList.get(position).getCategoryName());
            } else {
                holder.txtCategory.setVisibility(View.GONE);
            }
            holder.lyNewReleaseTag.setVisibility(View.GONE);
            if (!TextUtils.isEmpty("" + searchTvShowList.get(position).getLandscape())) {
                Picasso.get().load("" + searchTvShowList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }

            if (searchTvShowList.get(position).getIsPremium() == 1) {
                holder.txtPrimeTag.setVisibility(View.VISIBLE);
                holder.txtPrimeTag.setText("" + context.getResources().getString(R.string.prime));
            } else {
                holder.txtPrimeTag.setVisibility(View.GONE);
            }
            if (searchTvShowList.get(position).getIsRent() == 1) {
                holder.lyRentTag.setVisibility(View.VISIBLE);
                holder.txtCurrencySymbol.setText("" + currencyCode);
            } else {
                holder.lyRentTag.setVisibility(View.GONE);
            }
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("LongClick", "pos => " + position);
                if (from.equalsIgnoreCase("Video")) {
                    onItemClick.itemClick("Video", "Details", position);
                } else {
                    onItemClick.itemClick("Show", "Details", position);
                }
            }
        });

        holder.lyContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("LongClick", "pos => " + position);
                if (from.equalsIgnoreCase("Video")) {
                    onItemClick.longClick("Video", "ViewMore", position);
                } else {
                    onItemClick.longClick("Show", "ViewMore", position);
                }
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (from.equalsIgnoreCase("Video")) {
            return searchVideoList.size();
        } else {
            return searchTvShowList.size();
        }
    }

}
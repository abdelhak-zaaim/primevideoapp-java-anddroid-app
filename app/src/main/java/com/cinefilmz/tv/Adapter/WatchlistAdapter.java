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

import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.Model.VideoModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WatchlistAdapter extends RecyclerView.Adapter<WatchlistAdapter.MyViewHolder> {

    private Context context;
    private String from;
    private OnItemClick itemClick;
    private List<Result> watchlistList;

    public WatchlistAdapter(Context context, List<Result> watchlistList, String from, OnItemClick itemClick) {
        this.context = context;
        this.watchlistList = watchlistList;
        this.from = from;
        this.itemClick = itemClick;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivThumb;
        RoundedProgressBar progressWatch;
        LinearLayout lyContent, lyNewReleaseTag, lyViewDetails;
        TextView txtTitle, txtReleaseYear, txtTotalDuration, txtPrimeTag, txtRentTag, txtViewDetail, txtNewReleaseTag, txtPlay;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            lyNewReleaseTag = view.findViewById(R.id.lyNewReleaseTag);
            lyViewDetails = view.findViewById(R.id.lyViewDetails);
            ivThumb = view.findViewById(R.id.ivThumb);
            txtNewReleaseTag = view.findViewById(R.id.txtNewReleaseTag);
            txtPrimeTag = view.findViewById(R.id.txtPrimeTag);
            txtRentTag = view.findViewById(R.id.txtRentTag);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtReleaseYear = view.findViewById(R.id.txtReleaseYear);
            txtTotalDuration = view.findViewById(R.id.txtTotalDuration);
            txtViewDetail = view.findViewById(R.id.txtViewDetail);
            txtPlay = view.findViewById(R.id.txtPlay);
            progressWatch = view.findViewById(R.id.progressWatch);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (from.equalsIgnoreCase("Watchlist")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_items, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_items, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!TextUtils.isEmpty(watchlistList.get(position).getLandscape())) {
            Picasso.get().load(watchlistList.get(position).getLandscape()).into(holder.ivThumb);
        } else if (!TextUtils.isEmpty(watchlistList.get(position).getThumbnail())) {
            Picasso.get().load(watchlistList.get(position).getThumbnail()).into(holder.ivThumb);
        }

        holder.txtTitle.setText("" + watchlistList.get(position).getName());
        if (!TextUtils.isEmpty(watchlistList.get(position).getReleaseYear())) {
            holder.txtReleaseYear.setVisibility(View.VISIBLE);
            holder.txtReleaseYear.setText("" + watchlistList.get(position).getReleaseYear());
        } else {
            holder.txtReleaseYear.setVisibility(View.GONE);
        }

        if (watchlistList.get(position).getVideoType() != 2) {
            if (watchlistList.get(position).getVideoDuration() != 0) {
                holder.txtTotalDuration.setVisibility(View.VISIBLE);
                holder.txtTotalDuration.setText("" + Functions.formatDurationInMinute(watchlistList.get(position).getVideoDuration()));
            } else {
                holder.txtTotalDuration.setVisibility(View.GONE);
            }
        } else {
            holder.txtTotalDuration.setVisibility(View.GONE);
        }

        if (watchlistList.get(position).getIsPremium() == 1) {
            holder.txtPrimeTag.setVisibility(View.VISIBLE);
        } else {
            holder.txtPrimeTag.setVisibility(View.GONE);
        }
        if (watchlistList.get(position).getIsRent() == 1) {
            holder.txtRentTag.setVisibility(View.VISIBLE);
        } else {
            holder.txtRentTag.setVisibility(View.GONE);
        }

        if (watchlistList.get(position).getVideoType() != 2) {
            holder.txtPlay.setVisibility(View.VISIBLE);
            if (watchlistList.get(position).getStopTime() > 0 && watchlistList.get(position).getVideoDuration() > 0) {
                holder.progressWatch.setVisibility(View.VISIBLE);
                holder.progressWatch.setProgressPercentage(Utility.getPercentage(watchlistList.get(position).getVideoDuration(),
                        watchlistList.get(position).getStopTime()), true);
            } else {
                holder.progressWatch.setVisibility(View.GONE);
            }
        } else {
            holder.progressWatch.setVisibility(View.GONE);
            holder.txtPlay.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(watchlistList.get(position).getReleaseTag())) {
            holder.lyNewReleaseTag.setVisibility(View.VISIBLE);
            holder.txtNewReleaseTag.setText("" + watchlistList.get(position).getReleaseTag());
        } else {
            holder.lyNewReleaseTag.setVisibility(View.GONE);
        }

        holder.txtPlay.setOnClickListener(v -> {
            if (watchlistList.get(position).getVideoType() == 1) {
                if (watchlistList.get(position).getStopTime() > 0) {
                    Utility.playVideoOnClick(context, "Continue", "" + watchlistList.get(position).getVideoUploadType(),
                            watchlistList.get(position).getStopTime(), "" + watchlistList.get(position).getVideo320(),
                            "" + watchlistList.get(position).getVideo320(), "" + watchlistList.get(position).getId(),
                            "" + watchlistList.get(position).getCategoryId(), "" + watchlistList.get(position).getLandscape(),
                            "" + watchlistList.get(position).getName(), "" + watchlistList.get(position).getVideoType(), "");
                } else {
                    Utility.playVideoOnClick(context, "Watchlist", "" + watchlistList.get(position).getVideoUploadType(),
                            0, "" + watchlistList.get(position).getVideo320(), "" + watchlistList.get(position).getVideo320(),
                            "" + watchlistList.get(position).getId(), "" + watchlistList.get(position).getCategoryId(),
                            "" + watchlistList.get(position).getLandscape(), "" + watchlistList.get(position).getName(),
                            "" + watchlistList.get(position).getVideoType(), "");
                }
            }
        });

        holder.lyContent.setOnClickListener(v -> {
            Log.e("OnClick", "pos => " + position);
            itemClick.itemClick("" + watchlistList.get(position).getId(),
                    "" + watchlistList.get(position).getVideoType(), position);
        });

        holder.lyContent.setOnLongClickListener(v -> {
            Log.e("LongClick", "pos => " + position);
            itemClick.longClick("" + watchlistList.get(position).getId(), "" + watchlistList.get(position).getVideoType(), position);
            return false;
        });

        holder.lyViewDetails.setOnClickListener(v -> {
            Log.e("Click", "pos => " + position);
            itemClick.longClick("" + watchlistList.get(position).getId(), "" + watchlistList.get(position).getVideoType(), position);
        });

    }

    @Override
    public int getItemCount() {
        return watchlistList.size();
    }

}
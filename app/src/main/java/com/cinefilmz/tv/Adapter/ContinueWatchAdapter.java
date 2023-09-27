package com.cinefilmz.tv.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.SectionListModel.ContinueWatching;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContinueWatchAdapter extends RecyclerView.Adapter<ContinueWatchAdapter.MyViewHolder> {

    private Activity context;
    private List<ContinueWatching> continueWatchList;
    private String from;

    public ContinueWatchAdapter(Activity context, List<ContinueWatching> continueWatchList, String from) {
        this.context = context;
        this.continueWatchList = continueWatchList;
        this.from = from;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        LinearLayout lyContent, lyNewTag;
        RoundedProgressBar progressWatch;
        TextView txtPlay, txtNewTag;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            lyNewTag = view.findViewById(R.id.lyNewTag);
            ivThumb = view.findViewById(R.id.ivThumb);
            progressWatch = view.findViewById(R.id.progressWatch);
            txtPlay = view.findViewById(R.id.txtPlay);
            txtNewTag = view.findViewById(R.id.txtNewTag);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (from.equalsIgnoreCase("Home")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.continue_watch_items, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.continue_watch_items, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (continueWatchList.get(position) != null) {
            if (!TextUtils.isEmpty(continueWatchList.get(position).getLandscape())) {
                Picasso.get().load(continueWatchList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }

            holder.lyNewTag.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(continueWatchList.get(position).getReleaseTag())) {
                holder.lyNewTag.setVisibility(View.VISIBLE);
                holder.txtNewTag.setText("" + continueWatchList.get(position).getReleaseTag());
            }

            holder.progressWatch.setVisibility(View.GONE);
            if (continueWatchList.get(position).getVideoDuration() != null) {
                if (continueWatchList.get(position).getStopTime() > 0 && continueWatchList.get(position).getVideoDuration() > 0) {
                    holder.progressWatch.setVisibility(View.VISIBLE);
                    holder.progressWatch.setProgressPercentage(Utility.getPercentage(continueWatchList.get(position).getVideoDuration(),
                            continueWatchList.get(position).getStopTime()), true);
                }
            }

            holder.txtPlay.setOnClickListener(v -> {
                if (continueWatchList.get(position).getStopTime() > 0) {
                    Utility.playVideoOnClick(context, "Continue",
                            "" + continueWatchList.get(position).getVideoUploadType(),
                            continueWatchList.get(position).getStopTime(), "" + continueWatchList.get(position).getVideo320(),
                            "" + continueWatchList.get(position).getVideo320(), "" + continueWatchList.get(position).getId(),
                            "" + continueWatchList.get(position).getCategoryId(),
                            "" + continueWatchList.get(position).getLandscape(),
                            "" + continueWatchList.get(position).getName(), "" + continueWatchList.get(position).getVideoType(),
                            "");
                } else {
                    Utility.playVideoOnClick(context, "Home", continueWatchList.get(position).getVideoUploadType(), 0,
                            "" + continueWatchList.get(position).getVideo320(),
                            "" + continueWatchList.get(position).getVideo320(),
                            "" + continueWatchList.get(position).getId(), "" + continueWatchList.get(position).getCategoryId(),
                            "" + continueWatchList.get(position).getLandscape(),
                            "" + continueWatchList.get(position).getName(), "" + continueWatchList.get(position).getVideoType(),
                            "");
                }
            });

            holder.lyContent.setOnClickListener(v -> {
                Utility.openDetails(context,
                        continueWatchList.get(position).getVideoType().toString().equalsIgnoreCase("2") ?
                                ("" + continueWatchList.get(position).getShowId()) : ("" + continueWatchList.get(position).getId()),
                        "" + continueWatchList.get(position).getVideoType(),
                        "" + continueWatchList.get(position).getTypeId(),
                        "" + continueWatchList.get(position).getUpcomingType());
            });

            holder.lyContent.setOnLongClickListener(v -> {
                if (continueWatchList.get(position).getVideoType() == 1) {
                    Utility.showLongPressedMovieDialog(context, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), continueWatchList, position);
                } else if (continueWatchList.get(position).getVideoType() == 2) {
                    Utility.showLongPressedTVShowDialog(context, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), continueWatchList, position);
                }
                return false;
            });

        }

    }

    @Override
    public int getItemCount() {
        return continueWatchList.size();
    }

}
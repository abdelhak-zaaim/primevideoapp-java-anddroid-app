package com.cinefilmz.tv.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.Model.VideoSeasonModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class SectionEpisodeAdapter extends RecyclerView.Adapter<SectionEpisodeAdapter.MyViewHolder> {

    private Context context;
    private OnItemClick itemClick;
    private List<Result> episodeList;
    private String seasonID;

    public SectionEpisodeAdapter(Context context, List<Result> episodeList, String seasonID, OnItemClick itemClick) {
        this.context = context;
        this.episodeList = episodeList;
        this.seasonID = seasonID;
        this.itemClick = itemClick;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivEpiPoster;
        LinearLayout lyContent, lyDownloadStarted, lyPlayEpi;
        RelativeLayout rlDWithProgress;
        RoundedProgressBar progressWatch;
        CircularProgressBar downloadProgress;
        ExpandableLayout explContent;
        TextView txtPlayIcon, txtEpiDuration, txtHeaderDesc, txtEpiDesc, txtDuration, txtPrimeTag, txtDownloadPauseIcon;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            explContent = view.findViewById(R.id.explContent);

            lyPlayEpi = view.findViewById(R.id.lyPlayEpi);
            progressWatch = view.findViewById(R.id.progressWatch);
            txtPlayIcon = view.findViewById(R.id.txtPlayIcon);
            txtEpiDuration = view.findViewById(R.id.txtEpiDuration);
            txtHeaderDesc = view.findViewById(R.id.txtHeaderDesc);

            ivEpiPoster = view.findViewById(R.id.ivEpiPoster);
            txtEpiDesc = view.findViewById(R.id.txtEpiDesc);
            txtDuration = view.findViewById(R.id.txtDuration);
            txtPrimeTag = view.findViewById(R.id.txtPrimeTag);

            lyDownloadStarted = view.findViewById(R.id.lyDownloadStarted);
            rlDWithProgress = view.findViewById(R.id.rlDWithProgress);
            downloadProgress = view.findViewById(R.id.downloadProgress);
            txtDownloadPauseIcon = view.findViewById(R.id.txtDownloadPauseIcon);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tv_episode_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtHeaderDesc.setText("" + Html.fromHtml(episodeList.get(position).getDescription()));
        holder.txtEpiDesc.setText("" + Html.fromHtml(episodeList.get(position).getDescription()));
        if (!TextUtils.isEmpty("" + episodeList.get(position).getVideoDuration())) {
            holder.txtEpiDuration.setVisibility(View.VISIBLE);
            holder.txtEpiDuration.setText("" + Utility.covertToColonText(Long.parseLong("" + episodeList.get(position).getVideoDuration())));
        } else {
            holder.txtEpiDuration.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty("" + episodeList.get(position).getVideoDuration())) {
            holder.txtDuration.setVisibility(View.VISIBLE);
            holder.txtDuration.setText("" + Utility.covertToText(Long.parseLong("" + episodeList.get(position).getVideoDuration())));
        } else {
            holder.txtDuration.setVisibility(View.GONE);
        }
        if (episodeList.get(position).getVideoDuration() != null) {
            if (episodeList.get(position).getStopTime() > 0 && episodeList.get(position).getVideoDuration() > 0) {
                holder.progressWatch.setVisibility(View.VISIBLE);
                holder.progressWatch.setProgressPercentage(Utility.getPercentage(episodeList.get(position).getVideoDuration(),
                        episodeList.get(position).getStopTime()), true);
            }
        } else {
            holder.progressWatch.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty("" + episodeList.get(position).getLandscape())) {
            Picasso.get().load(episodeList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivEpiPoster);
        } else if (!TextUtils.isEmpty("" + episodeList.get(position).getThumbnail())) {
            Picasso.get().load(episodeList.get(position).getThumbnail()).placeholder(R.drawable.no_image_land).into(holder.ivEpiPoster);
        } else {
            Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivEpiPoster);
        }

        if (episodeList.get(position).getIsPremium() == 1) {
            holder.txtPrimeTag.setVisibility(View.VISIBLE);
        } else {
            holder.txtPrimeTag.setVisibility(View.GONE);
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "position => " + position);
                if (!holder.explContent.isExpanded()) {
                    holder.explContent.setExpanded(true);
                } else {
                    holder.explContent.setExpanded(false);
                }
            }
        });

        holder.txtPlayIcon.setOnClickListener(v -> {
            Log.e("Click", "position => " + position);
            if (!TextUtils.isEmpty("" + episodeList.get(position).getVideo320())) {
                itemClick.itemClick("" + episodeList.get(position).getId(), "Play", position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return episodeList.size();
    }

}
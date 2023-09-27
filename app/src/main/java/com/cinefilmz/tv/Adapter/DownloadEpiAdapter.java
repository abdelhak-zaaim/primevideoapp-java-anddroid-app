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

import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Interface.OnSingleItemClick;
import com.cinefilmz.tv.Model.DownloadShowModel.EpisodeItem;
import com.cinefilmz.tv.R;
import com.mackhartley.roundedprogressbar.RoundedProgressBar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DownloadEpiAdapter extends RecyclerView.Adapter<DownloadEpiAdapter.MyViewHolder> {

    private Context context;
    private String type;
    private List<EpisodeItem> episodeItemList;
    private OnSingleItemClick onSingleItemClick;
    public static int adapterSize = 0;

    public DownloadEpiAdapter(Context context, List<EpisodeItem> episodeItemList, String type, OnSingleItemClick onSingleItemClick) {
        this.context = context;
        this.episodeItemList = episodeItemList;
        this.type = type;
        this.onSingleItemClick = onSingleItemClick;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivThumb;
        RoundedProgressBar progressWatch;
        LinearLayout lyContent, lyNewReleaseTag, lyTotalEpiSize;
        TextView txtTitle, txtTotalDuration, txtTotalSize, txtPrimeTag, txtViewDetail, txtNewReleaseTag, txtPlay;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            lyNewReleaseTag = view.findViewById(R.id.lyNewReleaseTag);
            lyTotalEpiSize = view.findViewById(R.id.lyTotalEpiSize);
            ivThumb = view.findViewById(R.id.ivThumb);
            txtNewReleaseTag = view.findViewById(R.id.txtNewReleaseTag);
            txtPrimeTag = view.findViewById(R.id.txtPrimeTag);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtTotalDuration = view.findViewById(R.id.txtTotalDuration);
            txtTotalSize = view.findViewById(R.id.txtTotalSize);
            txtViewDetail = view.findViewById(R.id.txtViewDetail);
            txtPlay = view.findViewById(R.id.txtPlay);
            progressWatch = view.findViewById(R.id.progressWatch);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_episode_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!TextUtils.isEmpty(episodeItemList.get(position).getLandscape())) {
            Picasso.get().load(episodeItemList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
        } else if (!TextUtils.isEmpty(episodeItemList.get(position).getThumbnail())) {
            Picasso.get().load(episodeItemList.get(position).getThumbnail()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
        } else {
            Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
        }

        holder.txtTitle.setText("" + episodeItemList.get(position).getDescription());

        if (!TextUtils.isEmpty(episodeItemList.get(position).getVideoDuration())) {
            holder.txtTotalDuration.setVisibility(View.VISIBLE);
            holder.txtTotalDuration.setText("" + Functions.formatDurationInMinute(Long.parseLong("" + episodeItemList.get(position).getVideoDuration())));
        } else {
            holder.txtTotalDuration.setVisibility(View.GONE);
        }

        if (episodeItemList.get(position).getFileSize() != 0) {
            holder.txtTotalSize.setVisibility(View.VISIBLE);
            holder.txtTotalSize.setText("" + Functions.getStringSizeOfFile(episodeItemList.get(position).getFileSize()));
        } else {
            holder.txtTotalSize.setVisibility(View.GONE);
        }

        holder.lyContent.setOnClickListener(v -> onSingleItemClick.onSingleClick(position, "" + episodeItemList.get(position).getShowId()));

    }

    @Override
    public int getItemCount() {
        return adapterSize;
    }

}
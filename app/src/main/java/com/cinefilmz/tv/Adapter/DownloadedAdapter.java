package com.cinefilmz.tv.Adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.Model.DownloadVideoItem;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DownloadedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final DownloadListener downloadListener;
    private List<DownloadVideoItem> downloadVideoItemList;
    private Activity activity;
    private OnItemClick itemClick;
    public static int adapterSize = 0;

    public DownloadedAdapter(Activity activity, List<DownloadVideoItem> downloadVideoItemList, DownloadListener downloadListener,
                             OnItemClick itemClick) {
        this.activity = activity;
        this.downloadVideoItemList = downloadVideoItemList;
        this.downloadListener = downloadListener;
        this.itemClick = itemClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.download_items, null);
        RecyclerView.ViewHolder viewHolder = new DownloadedHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DownloadedHolder downloadedHolder = (DownloadedHolder) holder;

        downloadedHolder.txtTitle.setText(downloadVideoItemList.get(position).getTitle());

        if (!TextUtils.isEmpty(downloadVideoItemList.get(position).getImage())) {
            Picasso.get().load(downloadVideoItemList.get(position).getImage() != null ? downloadVideoItemList.get(position).getImage() : "")
                    .placeholder(R.drawable.no_image_land).into(downloadedHolder.ivThumb);
        } else {
            Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(downloadedHolder.ivThumb);
        }

        Log.e("getPath", "==> " + downloadVideoItemList.get(position).getPath());
        /* ****************************
        video_type =>  1-video,  2-show
        **************************** */
        if (downloadVideoItemList.get(position).getVideoType().equalsIgnoreCase("2")) {
            downloadedHolder.lyViewDetails.setVisibility(View.VISIBLE);
            downloadedHolder.lyViewMore.setVisibility(View.GONE);
            downloadedHolder.txtTotalSeason.setVisibility(View.VISIBLE);
            downloadedHolder.txtTotalSeason.setText(downloadVideoItemList.get(position).getSessionItems().size()
                    + " " + (downloadVideoItemList.get(position).getSessionItems().size() > 1
                    ? activity.getResources().getString(R.string.seasons)
                    : activity.getResources().getString(R.string.season)));

            if (downloadVideoItemList.get(position).getSessionItems() != null) {
                if (downloadVideoItemList.get(position).getSessionItems().size() > 0) {
                    downloadedHolder.txtTotalDuration.setVisibility(View.VISIBLE);
                    downloadedHolder.txtTotalDuration.setText("" + Utility.getTotalEpisodeDuration(downloadVideoItemList.get(position).getSessionItems(),
                            "" + downloadVideoItemList.get(position).getId()));
                } else {
                    downloadedHolder.txtTotalDuration.setVisibility(View.GONE);
                }
            } else {
                downloadedHolder.txtTotalDuration.setVisibility(View.GONE);
            }

            if (downloadVideoItemList.get(position).getSessionItems() != null) {
                if (downloadVideoItemList.get(position).getSessionItems().size() > 0) {
                    downloadedHolder.txtTotalSize.setVisibility(View.VISIBLE);
                    downloadedHolder.txtTotalSize.setText("" + Utility.getTotalEpisodeFileSize(downloadVideoItemList.get(position).getSessionItems(),
                            "" + downloadVideoItemList.get(position).getId()));
                } else {
                    downloadedHolder.txtTotalSize.setVisibility(View.GONE);
                }
            } else {
                downloadedHolder.txtTotalSize.setVisibility(View.GONE);
            }
        } else {
            downloadedHolder.lyViewDetails.setVisibility(View.GONE);
            downloadedHolder.lyViewMore.setVisibility(View.VISIBLE);
            downloadedHolder.txtTotalSeason.setVisibility(View.GONE);

            if (downloadVideoItemList.get(position).getDuration() != 0) {
                downloadedHolder.txtTotalDuration.setVisibility(View.VISIBLE);
                downloadedHolder.txtTotalDuration.setText("" + Functions.formatDurationInMinute(downloadVideoItemList.get(position).getDuration()));
            } else {
                downloadedHolder.txtTotalDuration.setVisibility(View.GONE);
            }

            if (downloadVideoItemList.get(position).getSize() != 0) {
                downloadedHolder.txtTotalSize.setVisibility(View.VISIBLE);
                downloadedHolder.txtTotalSize.setText("" + Functions.getStringSizeOfFile(downloadVideoItemList.get(position).getSize()));
            } else {
                downloadedHolder.txtTotalSize.setVisibility(View.GONE);
            }
        }

        downloadedHolder.lyContent.setOnLongClickListener(v -> {
            Log.e("Long Click", "position =====> " + position);
            itemClick.longClick("" + downloadVideoItemList.get(position).getId(), "Selection", position);
            return false;
        });

        downloadedHolder.ivThumb.setOnLongClickListener(v -> {
            itemClick.longClick("" + downloadVideoItemList.get(position).getId(), "Selection", position);
            return false;
        });

        downloadedHolder.lyContent.setOnClickListener(v -> {
            if (downloadVideoItemList.get(position).getVideoType().equalsIgnoreCase("2")) {
                itemClick.itemClick("" + downloadVideoItemList.get(position).getId(), "Details", position);
            } else {
                downloadListener.OnPlay(downloadVideoItemList.get(position));
            }
        });

        downloadedHolder.ivThumb.setOnClickListener(v -> {
            if (downloadVideoItemList.get(position).getVideoType().equalsIgnoreCase("2")) {
                itemClick.itemClick("" + downloadVideoItemList.get(position).getId(), "Details", position);
            } else {
                downloadListener.OnPlay(downloadVideoItemList.get(position));
            }
        });

        downloadedHolder.lyViewDetails.setOnClickListener(v -> {
            Log.e("onClick", "position =====> " + position);
            Log.e("onClick", "SessionItems =====> " + downloadVideoItemList.get(position).getSessionItems().size());
            Utility.pushDownloadEpiF(activity, position, "" + downloadVideoItemList.get(position).getTitle(),
                    "" + downloadVideoItemList.get(position).getId(), "" + downloadVideoItemList.get(position).getVideoType(),
                    "" + downloadVideoItemList.get(position).getTypeId());
        });

        downloadedHolder.lyViewMore.setOnClickListener(v -> {
            itemClick.itemClick("" + downloadVideoItemList.get(position).getId(), "Delete", position);
        });

    }

    @Override
    public int getItemCount() {
        return adapterSize;
    }

    public class DownloadedHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivThumb;
        View overlayView;
        LinearLayout lyContent, lyNewReleaseTag, lyTotalEpiSize, lyViewDetails, lyViewMore;
        RelativeLayout rlDPWithProgress;
        TextView txtTitle, txtTotalSeason, txtTotalDuration, txtTotalSize, txtViewDetail, txtViewMore, txtDownloadPauseIcon;
        CircularProgressBar downloadProgress;

        public DownloadedHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            lyNewReleaseTag = view.findViewById(R.id.lyNewReleaseTag);
            lyTotalEpiSize = view.findViewById(R.id.lyTotalEpiSize);
            lyViewDetails = view.findViewById(R.id.lyViewDetails);
            lyViewMore = view.findViewById(R.id.lyViewMore);
            rlDPWithProgress = view.findViewById(R.id.rlDPWithProgress);
            ivThumb = view.findViewById(R.id.ivThumb);
            overlayView = view.findViewById(R.id.overlayView);
            txtTitle = view.findViewById(R.id.txtTitle);
            txtTotalSeason = view.findViewById(R.id.txtTotalSeason);
            txtTotalDuration = view.findViewById(R.id.txtTotalDuration);
            txtTotalSize = view.findViewById(R.id.txtTotalSize);
            txtViewDetail = view.findViewById(R.id.txtViewDetail);
            txtViewMore = view.findViewById(R.id.txtViewMore);
            txtDownloadPauseIcon = view.findViewById(R.id.txtDownloadPauseIcon);
            downloadProgress = view.findViewById(R.id.downloadProgress);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return downloadVideoItemList.get(position).getTypeView();
    }

    public interface DownloadListener {
        void OnPlay(DownloadVideoItem downloadVideoItem);
    }

}
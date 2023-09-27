package com.cinefilmz.tv.Adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.VideoModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AllVideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIDEO_L = 10;
    private static final int VIDEO_P = 11;
    private static final int VIDEO_S = 12;

    private static final int SHOW_L = 20;
    private static final int SHOW_P = 21;
    private static final int SHOW_S = 22;

    private Activity context;
    private String from, videoType, layoutType, typeID;
    private List<Result> allVideoList;

    public AllVideosAdapter(Activity context, List<Result> allVideoList, String from, String videoType, String layoutType, String typeID) {
        this.context = context;
        this.allVideoList = allVideoList;
        this.from = from;
        this.videoType = videoType;
        this.layoutType = layoutType;
        this.typeID = typeID;
    }

    /* ****************************
    video_type =>  1-video,  2-show
    **************************** */
    @Override
    public int getItemViewType(int position) {
        if (videoType.equalsIgnoreCase("1")) {
            if (layoutType.equalsIgnoreCase("landscape")) {
                return VIDEO_L;
            } else if (layoutType.equalsIgnoreCase("potrait")) {
                return VIDEO_P;
            } else if (layoutType.equalsIgnoreCase("square")) {
                return VIDEO_S;
            } else {
                return VIDEO_L;
            }

        } else if (videoType.equalsIgnoreCase("2")) {
            if (layoutType.equalsIgnoreCase("landscape")) {
                return SHOW_L;
            } else if (layoutType.equalsIgnoreCase("potrait")) {
                return SHOW_P;
            } else if (layoutType.equalsIgnoreCase("square")) {
                return SHOW_S;
            } else {
                return SHOW_L;
            }

        } else {
            if (layoutType.equalsIgnoreCase("landscape")) {
                return VIDEO_L;
            } else if (layoutType.equalsIgnoreCase("potrait")) {
                return VIDEO_P;
            } else if (layoutType.equalsIgnoreCase("square")) {
                return VIDEO_S;
            } else {
                return VIDEO_L;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case VIDEO_L:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_landscape_item, parent, false);
                return new VideoHolder(itemView);

            case VIDEO_P:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_potrait_item, parent, false);
                return new VideoHolder(itemView);

            case VIDEO_S:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_sqaure_item, parent, false);
                return new VideoHolder(itemView);

            case SHOW_L:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_landscape_item, parent, false);
                return new ShowHolder(itemView);

            case SHOW_P:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_potrait_item, parent, false);
                return new ShowHolder(itemView);

            case SHOW_S:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_sqaure_item, parent, false);
                return new ShowHolder(itemView);

            default:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_landscape_item, parent, false);
                return new VideoHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VideoHolder) {
            onBindVideoHolder((VideoHolder) holder, position);
        } else if (holder instanceof ShowHolder) {
            onBindShowHolder((ShowHolder) holder, position);
        }
    }

    /* ***** Video ***** */
    public class VideoHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivThumb;
        LinearLayout lyContent, lyNewTag;
        TextView txtNewTag;

        public VideoHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            ivThumb = view.findViewById(R.id.ivThumb);
            txtNewTag = view.findViewById(R.id.txtNewTag);
            lyNewTag = view.findViewById(R.id.lyNewTag);
        }
    }

    public void onBindVideoHolder(VideoHolder holder, int position) {

        if (layoutType.equalsIgnoreCase("square")) {
            if (!TextUtils.isEmpty(allVideoList.get(position).getThumbnail())) {
                Picasso.get().load(allVideoList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else if (layoutType.equalsIgnoreCase("potrait")) {
            if (!TextUtils.isEmpty(allVideoList.get(position).getThumbnail())) {
                Picasso.get().load(allVideoList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else {
            if (!TextUtils.isEmpty(allVideoList.get(position).getLandscape())) {
                Picasso.get().load(allVideoList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
        }

        if (!TextUtils.isEmpty(allVideoList.get(position).getReleaseTag())) {
            holder.lyNewTag.setVisibility(View.VISIBLE);
            holder.txtNewTag.setText("" + allVideoList.get(position).getReleaseTag());
        } else {
            holder.lyNewTag.setVisibility(View.GONE);
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openDetails(context, "" + allVideoList.get(position).getId(),
                        "" + allVideoList.get(position).getVideoType(), "" + allVideoList.get(position).getTypeId(),
                        "" + allVideoList.get(position).getUpcomingType());
            }
        });

        holder.lyContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utility.showLongPressedMovieDialog(context, new ArrayList<>(), allVideoList, new ArrayList<>(), new ArrayList<>(), position);
                return false;
            }
        });
    }
    /* ***** Video ***** */

    /* ***** Show ***** */
    public class ShowHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivThumb;
        LinearLayout lyContent, lyNewTag;
        TextView txtNewTag;

        public ShowHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            ivThumb = view.findViewById(R.id.ivThumb);
            txtNewTag = view.findViewById(R.id.txtNewTag);
            lyNewTag = view.findViewById(R.id.lyNewTag);
        }
    }

    public void onBindShowHolder(ShowHolder holder, int position) {

        if (layoutType.equalsIgnoreCase("square")) {
            if (!TextUtils.isEmpty(allVideoList.get(position).getThumbnail())) {
                Picasso.get().load(allVideoList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else if (layoutType.equalsIgnoreCase("potrait")) {
            if (!TextUtils.isEmpty(allVideoList.get(position).getThumbnail())) {
                Picasso.get().load(allVideoList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else {
            if (!TextUtils.isEmpty(allVideoList.get(position).getLandscape())) {
                Picasso.get().load(allVideoList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
        }

        if (!TextUtils.isEmpty(allVideoList.get(position).getReleaseTag())) {
            holder.lyNewTag.setVisibility(View.VISIBLE);
            holder.txtNewTag.setText("" + allVideoList.get(position).getReleaseTag());
        } else {
            holder.lyNewTag.setVisibility(View.GONE);
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.openDetails(context, "" + allVideoList.get(position).getId(),
                        "" + allVideoList.get(position).getVideoType(), "" + allVideoList.get(position).getTypeId(),
                        "" + allVideoList.get(position).getUpcomingType());
            }
        });

        holder.lyContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utility.showLongPressedTVShowDialog(context, new ArrayList<>(), allVideoList, new ArrayList<>(), new ArrayList<>(), position);
                return false;
            }
        });
    }
    /* ***** Show ***** */

    public void setFilteredData(List<Result> filteredDataList) {
        this.allVideoList = filteredDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return allVideoList.size();
    }

}
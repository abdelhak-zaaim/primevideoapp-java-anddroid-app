package com.cinefilmz.tv.Adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.SectionChannelModel.Datum;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.google.android.exoplayer2.util.Log;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SectionChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIDEO_L = 10;
    private static final int VIDEO_P = 11;
    private static final int VIDEO_S = 12;

    private static final int SHOW_L = 20;
    private static final int SHOW_P = 21;
    private static final int SHOW_S = 22;

    private Activity context;
    private String from, videoType, layoutType;
    private List<Datum> sectionChannelDataList;

    public SectionChannelAdapter(Activity context, List<Datum> sectionChannelDataList, String from, String videoType, String layoutType) {
        this.context = context;
        this.sectionChannelDataList = sectionChannelDataList;
        this.from = from;
        this.videoType = videoType;
        this.layoutType = layoutType;
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
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_landscape_item, parent, false);
                return new VideoHolder(itemView);

            case VIDEO_P:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_potrait_item, parent, false);
                return new VideoHolder(itemView);

            case VIDEO_S:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movies_square_item, parent, false);
                return new VideoHolder(itemView);

            case SHOW_L:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvshow_landscape_item, parent, false);
                return new ShowHolder(itemView);

            case SHOW_P:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvshow_potrait_item, parent, false);
                return new ShowHolder(itemView);

            case SHOW_S:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvshow_square_item, parent, false);
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
            if (!TextUtils.isEmpty(sectionChannelDataList.get(position).getThumbnail())) {
                Picasso.get().load(sectionChannelDataList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else if (layoutType.equalsIgnoreCase("potrait")) {
            if (!TextUtils.isEmpty(sectionChannelDataList.get(position).getThumbnail())) {
                Picasso.get().load(sectionChannelDataList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else {
            if (!TextUtils.isEmpty(sectionChannelDataList.get(position).getLandscape())) {
                Picasso.get().load(sectionChannelDataList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
        }
        if (!TextUtils.isEmpty(sectionChannelDataList.get(position).getReleaseTag())) {
            holder.lyNewTag.setVisibility(View.VISIBLE);
            holder.txtNewTag.setText("" + sectionChannelDataList.get(position).getReleaseTag());
        } else {
            holder.lyNewTag.setVisibility(View.GONE);
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionChannelDataList.get(position).getId());
                Log.e("videoType", "==>> " + sectionChannelDataList.get(position).getVideoType());
                Log.e("typeID", "==>> " + sectionChannelDataList.get(position).getTypeId());
                Utility.openDetails(context, "" + sectionChannelDataList.get(position).getId(), "" + sectionChannelDataList.get(position).getVideoType(),
                        "" + sectionChannelDataList.get(position).getTypeId(), "" + sectionChannelDataList.get(position).getUpcomingType());
            }
        });

        holder.lyContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionChannelDataList.get(position).getId());
                Utility.showLongPressedChannelDialog(context, sectionChannelDataList, position);
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
            if (!TextUtils.isEmpty(sectionChannelDataList.get(position).getThumbnail())) {
                Picasso.get().load(sectionChannelDataList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else if (layoutType.equalsIgnoreCase("potrait")) {
            if (!TextUtils.isEmpty(sectionChannelDataList.get(position).getThumbnail())) {
                Picasso.get().load(sectionChannelDataList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else {
            if (!TextUtils.isEmpty(sectionChannelDataList.get(position).getLandscape())) {
                Picasso.get().load(sectionChannelDataList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
        }
        if (!TextUtils.isEmpty(sectionChannelDataList.get(position).getReleaseTag())) {
            holder.lyNewTag.setVisibility(View.VISIBLE);
            holder.txtNewTag.setText("" + sectionChannelDataList.get(position).getReleaseTag());
        } else {
            holder.lyNewTag.setVisibility(View.GONE);
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionChannelDataList.get(position).getId());
                Log.e("videoType", "==>> " + sectionChannelDataList.get(position).getVideoType());
                Log.e("typeID", "==>> " + sectionChannelDataList.get(position).getTypeId());
                Utility.openDetails(context, "" + sectionChannelDataList.get(position).getId(), "" + sectionChannelDataList.get(position).getVideoType(),
                        "" + sectionChannelDataList.get(position).getTypeId(),
                        "" + sectionChannelDataList.get(position).getUpcomingType());
            }
        });

        holder.lyContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionChannelDataList.get(position).getId());
                Log.e("videoType", "==>> " + sectionChannelDataList.get(position).getVideoType());
                Log.e("typeID", "==>> " + sectionChannelDataList.get(position).getTypeId());
                Utility.showLongPressedChannelDialog(context, sectionChannelDataList, position);
                return false;
            }
        });
    }
    /* ***** Show ***** */

    @Override
    public int getItemCount() {
        return sectionChannelDataList.size();
    }

}
package com.cinefilmz.tv.Adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.SectionListModel.Datum;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.google.android.exoplayer2.util.Log;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SectionHomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIDEO_L = 10;
    private static final int VIDEO_P = 11;
    private static final int VIDEO_S = 12;

    private static final int SHOW_L = 20;
    private static final int SHOW_P = 21;
    private static final int SHOW_S = 22;

    private static final int LANGUAGE = 3;
    private static final int CATEGORY = 4;

    private static final int VIEWALL_L = 50;
    private static final int VIEWALL_P = 51;
    private static final int VIEWALL_S = 52;

    private Activity context;
    private String from, videoType, layoutType, typeID;
    private List<Datum> sectionDataList;

    public SectionHomeAdapter(Activity context, List<Datum> sectionDataList, String from, String videoType, String layoutType, String typeID) {
        this.context = context;
        this.sectionDataList = sectionDataList;
        this.from = from;
        this.videoType = videoType;
        this.layoutType = layoutType;
        this.typeID = typeID;
    }

    /* ******************************************************
    video_type =>  1-video,  2-show,  3-language,  4-category
    ****************************************************** */
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

        } else if (videoType.equalsIgnoreCase("3")) {
            return LANGUAGE;
        } else if (videoType.equalsIgnoreCase("4")) {
            return CATEGORY;

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
        Log.e("viewType", "==>> " + viewType);
        switch (viewType) {
            case VIDEO_L:
                Log.e("viewType", "==>> " + viewType);
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

            case LANGUAGE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.language_common_item, parent, false);
                return new LanguageHolder(itemView);

            case CATEGORY:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.genres_common_item, parent, false);
                return new CategoryHolder(itemView);

            case VIEWALL_P:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_potrait_item, parent, false);
                return new VideoHolder(itemView);

            case VIEWALL_S:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewall_sqaure_item, parent, false);
                return new VideoHolder(itemView);

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

        } else if (holder instanceof LanguageHolder) {
            onBindLanguageHolder((LanguageHolder) holder, position);

        } else if (holder instanceof CategoryHolder) {
            onBindCategoryHolder((CategoryHolder) holder, position);
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

        Log.e("itemID", "==>> " + sectionDataList.get(position).getId());
        Log.e("TypeId", "==>> " + sectionDataList.get(position).getTypeId());
        Log.e("layoutType", "==>> " + layoutType);
        if (layoutType.equalsIgnoreCase("square")) {
            if (!TextUtils.isEmpty(sectionDataList.get(position).getThumbnail())) {
                Picasso.get().load(sectionDataList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else if (layoutType.equalsIgnoreCase("potrait")) {
            if (!TextUtils.isEmpty(sectionDataList.get(position).getThumbnail())) {
                Picasso.get().load(sectionDataList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else {
            if (!TextUtils.isEmpty(sectionDataList.get(position).getLandscape())) {
                Picasso.get().load(sectionDataList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
        }
        if (!TextUtils.isEmpty(sectionDataList.get(position).getReleaseTag())) {
            holder.lyNewTag.setVisibility(View.VISIBLE);
            holder.txtNewTag.setText("" + sectionDataList.get(position).getReleaseTag());
        } else {
            holder.lyNewTag.setVisibility(View.GONE);
        }
        Log.e("Thumbnail", "==>> " + sectionDataList.get(position).getThumbnail());

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionDataList.get(position).getId());
                Log.e("videoType", "==>> " + sectionDataList.get(position).getVideoType());
                Log.e("typeID", "==>> " + sectionDataList.get(position).getTypeId());
                Utility.openDetails(context, "" + sectionDataList.get(position).getId(),
                        "" + sectionDataList.get(position).getVideoType(), "" + sectionDataList.get(position).getTypeId(),
                        "" + sectionDataList.get(position).getUpcomingType());
            }
        });

        holder.lyContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionDataList.get(position).getId());
                Log.e("videoType", "==>> " + sectionDataList.get(position).getVideoType());
                Log.e("typeID", "==>> " + sectionDataList.get(position).getTypeId());
                Utility.showLongPressedMovieDialog(context, sectionDataList, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), position);
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
            if (!TextUtils.isEmpty(sectionDataList.get(position).getThumbnail())) {
                Picasso.get().load(sectionDataList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else if (layoutType.equalsIgnoreCase("potrait")) {
            if (!TextUtils.isEmpty(sectionDataList.get(position).getThumbnail())) {
                Picasso.get().load(sectionDataList.get(position).getThumbnail()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
            }
        } else {
            if (!TextUtils.isEmpty(sectionDataList.get(position).getLandscape())) {
                Picasso.get().load(sectionDataList.get(position).getLandscape()).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            } else {
                Picasso.get().load(R.drawable.no_image_land).placeholder(R.drawable.no_image_land).into(holder.ivThumb);
            }
        }

        if (!TextUtils.isEmpty(sectionDataList.get(position).getReleaseTag())) {
            holder.lyNewTag.setVisibility(View.VISIBLE);
            holder.txtNewTag.setText("" + sectionDataList.get(position).getReleaseTag());
        } else {
            holder.lyNewTag.setVisibility(View.GONE);
        }

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionDataList.get(position).getId());
                Log.e("videoType", "==>> " + sectionDataList.get(position).getVideoType());
                Log.e("typeID", "==>> " + sectionDataList.get(position).getTypeId());
                Utility.openDetails(context, "" + sectionDataList.get(position).getId(),
                        "" + sectionDataList.get(position).getVideoType(), "" + sectionDataList.get(position).getTypeId(),
                        "" + sectionDataList.get(position).getUpcomingType());
            }
        });

        holder.lyContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionDataList.get(position).getId());
                Log.e("videoType", "==>> " + videoType);
                Log.e("typeID", "==>> " + typeID);
                Utility.showLongPressedTVShowDialog(context, sectionDataList, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), position);
                return false;
            }
        });
    }
    /* ***** Show ***** */

    /* ***** Language ***** */
    public class LanguageHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        LinearLayout lyContent;
        TextView txtGenres;

        public LanguageHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            ivThumb = view.findViewById(R.id.ivThumb);
            txtGenres = view.findViewById(R.id.txtGenres);
        }
    }

    public void onBindLanguageHolder(LanguageHolder holder, int position) {

        if (!TextUtils.isEmpty(sectionDataList.get(position).getImage())) {
            Picasso.get().load(sectionDataList.get(position).getImage()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
        } else {
            Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
        }
        holder.txtGenres.setText("" + sectionDataList.get(position).getName());

        holder.ivThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionDataList.get(position).getId());
                Log.e("videoType", "==>> " + videoType);
                Log.e("typeID", "==>> " + typeID);
                Utility.pushVideosByIDF(context, "ByLanguage", "" + sectionDataList.get(position).getId(),
                        "" + sectionDataList.get(position).getName(), "" + sectionDataList.get(position).getVideoType(),
                        "" + layoutType, "" + typeID);
            }
        });

    }
    /* ***** Language ***** */

    /* ***** Category ***** */
    public class CategoryHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        LinearLayout lyContent;
        TextView txtGenres;

        public CategoryHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            ivThumb = view.findViewById(R.id.ivThumb);
            txtGenres = view.findViewById(R.id.txtGenres);
        }
    }

    public void onBindCategoryHolder(CategoryHolder holder, int position) {

        if (!TextUtils.isEmpty(sectionDataList.get(position).getImage())) {
            Picasso.get().load(sectionDataList.get(position).getImage()).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
        } else {
            Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivThumb);
        }

        holder.txtGenres.setText("" + sectionDataList.get(position).getName());

        holder.ivThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("position", "==>> " + position);
                Log.e("itemID", "==>> " + sectionDataList.get(position).getId());
                Log.e("videoType", "==>> " + videoType);
                Log.e("typeID", "==>> " + typeID);
                Utility.pushVideosByIDF(context, "ByGenres", "" + sectionDataList.get(position).getId(),
                        "" + sectionDataList.get(position).getName(), "" + sectionDataList.get(position).getVideoType(),
                        "" + layoutType, "" + typeID);
            }
        });

    }
    /* ***** Category ***** */

    @Override
    public int getItemCount() {
        return sectionDataList.size();
    }

}
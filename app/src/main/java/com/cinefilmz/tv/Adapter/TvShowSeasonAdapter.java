package com.cinefilmz.tv.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Interface.OnItemClick;
import com.cinefilmz.tv.Model.DownloadShowModel.SessionItem;
import com.cinefilmz.tv.Model.SectionDetailModel.Session;
import com.cinefilmz.tv.R;

import java.util.List;

public class TvShowSeasonAdapter extends RecyclerView.Adapter<TvShowSeasonAdapter.MyViewHolder> {

    private Context context;
    private List<SessionItem> seasonList;
    private List<Session> tvShowSessionList;
    private String type;
    private OnItemClick itemClick;
    public static int mSelectedPos = 0;

    public TvShowSeasonAdapter(Context context, List<Session> tvShowSessionList, List<SessionItem> seasonList, String type,
                               OnItemClick itemClick) {
        this.context = context;
        this.tvShowSessionList = tvShowSessionList;
        this.seasonList = seasonList;
        this.type = type;
        this.itemClick = itemClick;
        Log.e("type", "==> " + type);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lyContent;
        TextView txtTitle;
        View viewLine;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            txtTitle = view.findViewById(R.id.txtTitle);
            viewLine = view.findViewById(R.id.viewLine);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tvshow_season_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (type.equalsIgnoreCase("DSeason")) {
            holder.txtTitle.setText("" + seasonList.get(position).getName());
            holder.lyContent.setOnClickListener(v -> {
                Log.e("Click", "position => " + position);
                mSelectedPos = position;
                notifyDataSetChanged();
                itemClick.itemClick("" + seasonList.get(position).getId(), "Season", position);
            });

        } else {
            holder.txtTitle.setText("" + tvShowSessionList.get(position).getName());
            holder.lyContent.setOnClickListener(v -> {
                Log.e("Click", "position => " + position);
                mSelectedPos = position;
                notifyDataSetChanged();
                itemClick.itemClick("" + tvShowSessionList.get(position).getId(), "Season", position);
            });
        }

        if (mSelectedPos == position) {
            holder.txtTitle.setSelected(true);
            holder.viewLine.setVisibility(View.VISIBLE);
        } else {
            holder.txtTitle.setSelected(false);
            holder.viewLine.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (type.equalsIgnoreCase("DSeason"))
            return seasonList.size();
        else
            return tvShowSessionList.size();
    }

}
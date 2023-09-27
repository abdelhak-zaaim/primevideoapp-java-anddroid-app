package com.cinefilmz.tv.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.SectionDetailModel.Cast;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CastAndCrewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private String from;
    private List<Cast> castCrewList;

    public CastAndCrewAdapter(Context context, List<Cast> castCrewList, String from) {
        this.context = context;
        this.castCrewList = castCrewList;
        this.from = from;
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        LinearLayout lyContent;
        TextView txtName;

        public CastViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            ivThumb = view.findViewById(R.id.ivThumb);
            txtName = view.findViewById(R.id.txtName);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (from.equalsIgnoreCase("Details")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_and_crew_items, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_and_crew_items, parent, false);
        }
        return new CastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        onBindCastHolder((CastViewHolder) holder, position);
    }

    public void onBindCastHolder(CastViewHolder holder, int position) {

        if (!TextUtils.isEmpty(castCrewList.get(position).getImage())) {
            Picasso.get().load(castCrewList.get(position).getImage()).placeholder(context.getDrawable(R.drawable.no_user)).into(holder.ivThumb);
        }
        holder.txtName.setText("" + castCrewList.get(position).getName());

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("itemID", "==>>> " + castCrewList.get(position).getId());
                Utility.pushCastCrewDetailsF(context, "" + castCrewList.get(position).getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return castCrewList.size();
    }

}
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

import com.cinefilmz.tv.Model.GenresModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;

import java.util.List;

public class AllGenresAdapter extends RecyclerView.Adapter<AllGenresAdapter.MyViewHolder> {

    private Context context;
    private List<Result> genersList;
    private String type;
    private int setNewSize = 5;

    public AllGenresAdapter(Context context, List<Result> genersList, String type) {
        this.context = context;
        this.genersList = genersList;
        this.type = type;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lyContent;
        TextView txtTitle;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            txtTitle = view.findViewById(R.id.txtTitle);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_lang_genres_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtTitle.setText("" + genersList.get(position).getName());

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "position => " + position);
                Utility.pushViewAllF(context, "ByGenres", "" + genersList.get(position).getId(), "" + genersList.get(position).getName(),
                        "" + type, "", "0");
            }
        });

    }

    public void setDataListSize(int setNewSize) {
        this.setNewSize = setNewSize;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (setNewSize < genersList.size()) {
            return setNewSize;
        } else {
            return genersList.size();
        }
    }

}
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

import com.cinefilmz.tv.Model.LangaugeModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;

import java.util.List;

public class AllLanguageAdapter extends RecyclerView.Adapter<AllLanguageAdapter.MyViewHolder> {

    private Context context;
    private List<Result> languageList;
    private String type;
    private int setNewSize = 5;

    public AllLanguageAdapter(Context context, List<Result> languageList, String type) {
        this.context = context;
        this.languageList = languageList;
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
        holder.txtTitle.setText("" + languageList.get(position).getName());

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "position => " + position);
                Utility.pushViewAllF(context, "ByLanguage", "" + languageList.get(position).getId(), "" + languageList.get(position).getName(),
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
        if (setNewSize < languageList.size()) {
            return setNewSize;
        } else {
            return languageList.size();
        }
    }

}
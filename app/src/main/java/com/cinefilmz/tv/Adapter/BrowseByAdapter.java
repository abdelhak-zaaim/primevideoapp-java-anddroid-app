package com.cinefilmz.tv.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.SectionTypeModel.Result;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Utility;

import java.util.List;

public class BrowseByAdapter extends RecyclerView.Adapter<BrowseByAdapter.MyViewHolder> {

    private Context context;
    private String type;
    private List<Result> typeList;

    public BrowseByAdapter(Context context, String type, List<Result> typeList) {
        this.context = context;
        this.type = type;
        this.typeList = typeList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtBrowseTitle;

        public MyViewHolder(View view) {
            super(view);
            txtBrowseTitle = view.findViewById(R.id.txtBrowseTitle);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse_by_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.txtBrowseTitle.setText("" + typeList.get(position).getName());

        holder.txtBrowseTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "position => " + position);
                Utility.pushSectionByTypeF(context, "" + typeList.get(position).getId(), "" + typeList.get(position).getName(),
                        "" + typeList.get(position).getType(), "2");
            }
        });

    }

    @Override
    public int getItemCount() {
        return typeList.size();
    }

}
package com.cinefilmz.tv.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Interface.OnSuggestionClick;
import com.cinefilmz.tv.R;

import java.util.List;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.MyViewHolder> {

    private Context context;
    private List<String> suggestionList;
    private OnSuggestionClick suggestionClick;
    private String from;

    public SearchSuggestionAdapter(Context context, List<String> suggestionList, OnSuggestionClick suggestionClick, String from) {
        this.context = context;
        this.suggestionList = suggestionList;
        this.suggestionClick = suggestionClick;
        this.from = from;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtSuggestion;
        LinearLayout lyContent;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            txtSuggestion = view.findViewById(R.id.txtSuggestion);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.txtSuggestion.setText("" + suggestionList.get(position));

        holder.lyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "suggestion ==> " + suggestionList.get(position));
                Log.e("Click", "position ==> " + position);
                suggestionClick.suggestionClick("" + suggestionList.get(position), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return suggestionList.size();
    }

}
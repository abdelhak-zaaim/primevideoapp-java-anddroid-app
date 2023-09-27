package com.cinefilmz.tv.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Model.SubscriptionModel.Datum;
import com.cinefilmz.tv.R;

import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.MyViewHolder> {

    private Context context;
    private List<Datum> benefitsList;
    private String from;
    private int isSelected;

    public SubscriptionAdapter(Context context, List<Datum> benefitsList, String from, int isSelected) {
        this.context = context;
        this.benefitsList = benefitsList;
        this.from = from;
        this.isSelected = isSelected;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout lyContent;
        TextView txtBenefits, txtCheckUncheck, txtOtherText;

        public MyViewHolder(View view) {
            super(view);
            lyContent = view.findViewById(R.id.lyContent);
            txtBenefits = view.findViewById(R.id.txtBenefits);
            txtCheckUncheck = view.findViewById(R.id.txtCheckUncheck);
            txtOtherText = view.findViewById(R.id.txtOtherText);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (isSelected == 1) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_selected_items, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscription_items, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Log.e("isSelected", "==>>> " + isSelected);
        Log.e("position", "==>>> " + position);

        holder.txtBenefits.setText("" + benefitsList.get(position).getPackageKey());

        if (benefitsList.get(position).getPackageValue().equalsIgnoreCase("1") ||
                benefitsList.get(position).getPackageValue().equalsIgnoreCase("0")) {
            holder.txtCheckUncheck.setVisibility(View.VISIBLE);
            holder.txtOtherText.setVisibility(View.GONE);
            if (benefitsList.get(position).getPackageValue().equalsIgnoreCase("1")) {
                if (isSelected == 0) {
                    holder.txtCheckUncheck.setBackground(context.getResources().getDrawable(R.drawable.ic_checked));
                } else {
                    holder.txtCheckUncheck.setBackground(context.getResources().getDrawable(R.drawable.ic_checked_black));
                }
            } else {
                holder.txtCheckUncheck.setBackground(context.getResources().getDrawable(R.drawable.ic_unchecked));
            }
        } else {
            holder.txtCheckUncheck.setVisibility(View.GONE);
            holder.txtOtherText.setVisibility(View.VISIBLE);
            holder.txtOtherText.setText("" + benefitsList.get(position).getPackageValue());
        }

    }

    @Override
    public int getItemCount() {
        return benefitsList.size();
    }

}
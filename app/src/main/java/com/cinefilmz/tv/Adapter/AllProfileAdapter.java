package com.cinefilmz.tv.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Activity.EditProfile;
import com.cinefilmz.tv.R;
import com.cinefilmz.tv.Utils.Constant;
import com.makeramen.roundedimageview.RoundedImageView;

public class AllProfileAdapter extends RecyclerView.Adapter<AllProfileAdapter.MyViewHolder> {

    private Context context;
    private String type;

    public AllProfileAdapter(Context context, String type) {
        this.context = context;
        this.type = type;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivProfileImg;
        LinearLayout lyProfile, lyEditIcon;
        TextView txtUserName;

        public MyViewHolder(View view) {
            super(view);
            ivProfileImg = view.findViewById(R.id.ivProfileImg);
            lyProfile = view.findViewById(R.id.lyProfile);
            lyEditIcon = view.findViewById(R.id.lyEditIcon);
            txtUserName = view.findViewById(R.id.txtUserName);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (type.equalsIgnoreCase("MyStuff")) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_profile_item_small, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_profile_item, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (type.equalsIgnoreCase("MyStuff")) {
        } else {
            holder.lyEditIcon.setVisibility(View.VISIBLE);

            holder.lyEditIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Constant.isSelectPic = false;
                    context.startActivity(new Intent(context, EditProfile.class));
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (type.equalsIgnoreCase("MyStuff")) {
            return 2;
        } else {
            return 3;
        }
    }

}
package com.cinefilmz.tv.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.cinefilmz.tv.Interface.OnSingleItemClick;
import com.cinefilmz.tv.Model.AvatarModel.Result;
import com.cinefilmz.tv.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.MyViewHolder> {

    private Context context;
    private List<Result> avatarList;
    private String type;
    private OnSingleItemClick onSingleItemClick;

    public AvatarAdapter(Context context, List<Result> avatarList, String type, OnSingleItemClick onSingleItemClick) {
        this.context = context;
        this.avatarList = avatarList;
        this.type = type;
        this.onSingleItemClick = onSingleItemClick;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView ivAvatarImg;

        public MyViewHolder(View view) {
            super(view);
            ivAvatarImg = view.findViewById(R.id.ivAvatarImg);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.avatar_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!TextUtils.isEmpty(avatarList.get(position).getImage())) {
            Picasso.get().load(avatarList.get(position).getImage()).placeholder(R.drawable.no_image_port).into(holder.ivAvatarImg);
        } else {
            Picasso.get().load(R.drawable.no_image_port).placeholder(R.drawable.no_image_port).into(holder.ivAvatarImg);
        }

        holder.ivAvatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Click", "position => " + position);
                onSingleItemClick.onSingleClick(position, "" + avatarList.get(position).getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return avatarList.size();
    }

}
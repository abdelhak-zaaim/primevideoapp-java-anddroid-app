package com.cinefilmz.tv.Model.DownloadShowModel;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Keep
public class SessionItem implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("session_position")
    @Expose
    private Integer sessionPosition;
    @SerializedName("show_id")
    @Expose
    private Integer showId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("sessionEpisode")
    @Expose
    private List<EpisodeItem> episodeItems = null;

    private int typeView = 1;

    public SessionItem(Integer id, Integer showId, Integer sessionPos, String name, Integer status, String createdAt, String updatedAt, List<EpisodeItem> episodeItems) {
        this.id = id;
        this.showId = showId;
        this.sessionPosition = sessionPos;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.episodeItems = episodeItems;
    }

    @SuppressLint("NewApi")
    protected SessionItem(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        showId = in.readInt();
        sessionPosition = in.readInt();
        name = in.readString();
        status = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
        episodeItems = (List<EpisodeItem>) in.readSerializable();
    }

    @SuppressLint("NewApi")
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeInt(showId);
        dest.writeInt(sessionPosition);
        dest.writeString(name);
        dest.writeInt(status);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeSerializable((Serializable) episodeItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SessionItem> CREATOR = new Creator<SessionItem>() {
        @Override
        public SessionItem createFromParcel(Parcel in) {
            return new SessionItem(in);
        }

        @Override
        public SessionItem[] newArray(int size) {
            return new SessionItem[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Integer getSessionPosition() {
        return sessionPosition;
    }

    public void setSessionPosition(Integer sessionPosition) {
        this.sessionPosition = sessionPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<EpisodeItem> getEpisodeItems() {
        return episodeItems;
    }

    public void setEpisodeItems(List<EpisodeItem> episodeItems) {
        this.episodeItems = episodeItems;
    }

    public int getTypeView() {
        return typeView;
    }

    public SessionItem setTypeView(int typeView) {
        this.typeView = typeView;
        return this;
    }

}
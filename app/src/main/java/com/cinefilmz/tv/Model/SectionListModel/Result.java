package com.cinefilmz.tv.Model.SectionListModel;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Result implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("is_home_screen")
    @Expose
    private Integer isHomeScreen;
    @SerializedName("type_id")
    @Expose
    private Integer typeId;
    @SerializedName("video_type")
    @Expose
    private Integer videoType;
    @SerializedName("upcoming_type")
    @Expose
    private Integer upcomingType;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("screen_layout")
    @Expose
    private String screenLayout;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("data")
    @Expose
    private List<Datum> data;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIsHomeScreen() {
        return isHomeScreen;
    }

    public void setIsHomeScreen(Integer isHomeScreen) {
        this.isHomeScreen = isHomeScreen;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getVideoType() {
        return videoType;
    }

    public void setVideoType(Integer videoType) {
        this.videoType = videoType;
    }

    public Integer getUpcomingType() {
        return upcomingType;
    }

    public void setUpcomingType(Integer upcomingType) {
        this.upcomingType = upcomingType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getScreenLayout() {
        return screenLayout;
    }

    public void setScreenLayout(String screenLayout) {
        this.screenLayout = screenLayout;
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

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}
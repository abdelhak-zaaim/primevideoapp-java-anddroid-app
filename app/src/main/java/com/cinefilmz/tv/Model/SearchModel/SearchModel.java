package com.cinefilmz.tv.Model.SearchModel;

import androidx.annotation.Keep;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class SearchModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private List<Object> result;
    @SerializedName("video")
    @Expose
    private List<Video> video = null;
    @SerializedName("tvshow")
    @Expose
    private List<Tvshow> tvshow = null;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Object> getResult() {
        return result;
    }

    public void setResult(List<Object> result) {
        this.result = result;
    }

    public List<Video> getVideo() {
        return video;
    }

    public void setVideo(List<Video> video) {
        this.video = video;
    }

    public List<Tvshow> getTvshow() {
        return tvshow;
    }

    public void setTvshow(List<Tvshow> tvshow) {
        this.tvshow = tvshow;
    }

}
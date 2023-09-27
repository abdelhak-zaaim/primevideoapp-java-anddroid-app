package com.cinefilmz.tv.Model.SectionChannelModel;

import androidx.annotation.Keep;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class SectionChannelModel {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private List<Result> result;
    @SerializedName("live_url")
    @Expose
    private List<LiveUrl> liveUrl;

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public List<LiveUrl> getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(List<LiveUrl> liveUrl) {
        this.liveUrl = liveUrl;
    }

}

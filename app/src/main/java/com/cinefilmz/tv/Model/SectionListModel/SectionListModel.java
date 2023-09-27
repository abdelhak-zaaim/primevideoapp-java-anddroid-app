package com.cinefilmz.tv.Model.SectionListModel;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class SectionListModel implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private List<Result> result;
    @SerializedName("continue_watching")
    @Expose
    private List<ContinueWatching> continueWatching;

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

    public List<ContinueWatching> getContinueWatching() {
        return continueWatching;
    }

    public void setContinueWatching(List<ContinueWatching> continueWatching) {
        this.continueWatching = continueWatching;
    }

}
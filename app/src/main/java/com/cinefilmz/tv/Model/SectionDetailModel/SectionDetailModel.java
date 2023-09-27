package com.cinefilmz.tv.Model.SectionDetailModel;

import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class SectionDetailModel implements Serializable {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("result")
    @Expose
    private Result result;
    @SerializedName("cast")
    @Expose
    private List<Cast> cast;
    @SerializedName("session")
    @Expose
    private List<Session> session;
    @SerializedName("get_related_video")
    @Expose
    private List<GetRelatedVideo> getRelatedVideo;
    @SerializedName("language")
    @Expose
    private List<Language> language;
    @SerializedName("more_details")
    @Expose
    private List<MoreDetail> moreDetails;

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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<Cast> getCast() {
        return cast;
    }

    public void setCast(List<Cast> cast) {
        this.cast = cast;
    }

    public List<Session> getSession() {
        return session;
    }

    public void setSession(List<Session> session) {
        this.session = session;
    }

    public List<GetRelatedVideo> getGetRelatedVideo() {
        return getRelatedVideo;
    }

    public void setGetRelatedVideo(List<GetRelatedVideo> getRelatedVideo) {
        this.getRelatedVideo = getRelatedVideo;
    }

    public List<Language> getLanguage() {
        return language;
    }

    public void setLanguage(List<Language> language) {
        this.language = language;
    }

    public List<MoreDetail> getMoreDetails() {
        return moreDetails;
    }

    public void setMoreDetails(List<MoreDetail> moreDetails) {
        this.moreDetails = moreDetails;
    }

}
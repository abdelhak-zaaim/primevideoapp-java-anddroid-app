package com.cinefilmz.tv.Model.SectionDetailModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Episode {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("show_id")
    @Expose
    private Integer showId;
    @SerializedName("session_id")
    @Expose
    private Integer sessionId;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("landscape")
    @Expose
    private String landscape;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("video_extension")
    @Expose
    private String videoExtension;
    @SerializedName("video_320")
    @Expose
    private String video320;
    @SerializedName("video_480")
    @Expose
    private String video480;
    @SerializedName("video_720")
    @Expose
    private String video720;
    @SerializedName("video_1080")
    @Expose
    private String video1080;
    @SerializedName("subtitle_type")
    @Expose
    private String subtitleType;
    @SerializedName("subtitle")
    @Expose
    private String subtitle;
    @SerializedName("subtitle_lang_1")
    @Expose
    private String subtitleLang1;
    @SerializedName("subtitle_lang_2")
    @Expose
    private String subtitleLang2;
    @SerializedName("subtitle_lang_3")
    @Expose
    private String subtitleLang3;
    @SerializedName("subtitle_1")
    @Expose
    private String subtitle1;
    @SerializedName("subtitle_2")
    @Expose
    private String subtitle2;
    @SerializedName("subtitle_3")
    @Expose
    private String subtitle3;
    @SerializedName("is_premium")
    @Expose
    private String isPremium;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("view")
    @Expose
    private Integer view;
    @SerializedName("download")
    @Expose
    private Integer download;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("is_title")
    @Expose
    private String isTitle;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

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

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getLandscape() {
        return landscape;
    }

    public void setLandscape(String landscape) {
        this.landscape = landscape;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getVideoExtension() {
        return videoExtension;
    }

    public void setVideoExtension(String videoExtension) {
        this.videoExtension = videoExtension;
    }

    public String getVideo320() {
        return video320;
    }

    public void setVideo320(String video320) {
        this.video320 = video320;
    }

    public String getVideo480() {
        return video480;
    }

    public void setVideo480(String video480) {
        this.video480 = video480;
    }

    public String getVideo720() {
        return video720;
    }

    public void setVideo720(String video720) {
        this.video720 = video720;
    }

    public String getVideo1080() {
        return video1080;
    }

    public void setVideo1080(String video1080) {
        this.video1080 = video1080;
    }

    public String getSubtitleType() {
        return subtitleType;
    }

    public void setSubtitleType(String subtitleType) {
        this.subtitleType = subtitleType;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSubtitleLang1() {
        return subtitleLang1;
    }

    public void setSubtitleLang1(String subtitleLang1) {
        this.subtitleLang1 = subtitleLang1;
    }

    public String getSubtitleLang2() {
        return subtitleLang2;
    }

    public void setSubtitleLang2(String subtitleLang2) {
        this.subtitleLang2 = subtitleLang2;
    }

    public String getSubtitleLang3() {
        return subtitleLang3;
    }

    public void setSubtitleLang3(String subtitleLang3) {
        this.subtitleLang3 = subtitleLang3;
    }

    public String getSubtitle1() {
        return subtitle1;
    }

    public void setSubtitle1(String subtitle1) {
        this.subtitle1 = subtitle1;
    }

    public String getSubtitle2() {
        return subtitle2;
    }

    public void setSubtitle2(String subtitle2) {
        this.subtitle2 = subtitle2;
    }

    public String getSubtitle3() {
        return subtitle3;
    }

    public void setSubtitle3(String subtitle3) {
        this.subtitle3 = subtitle3;
    }

    public String getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(String isPremium) {
        this.isPremium = isPremium;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getIsTitle() {
        return isTitle;
    }

    public void setIsTitle(String isTitle) {
        this.isTitle = isTitle;
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

}
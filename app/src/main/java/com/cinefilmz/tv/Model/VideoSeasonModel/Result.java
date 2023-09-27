package com.cinefilmz.tv.Model.VideoSeasonModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class Result implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("show_id")
    @Expose
    private Integer showId;
    @SerializedName("session_id")
    @Expose
    private Integer sessionId;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("landscape")
    @Expose
    private String landscape;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("is_premium")
    @Expose
    private Integer isPremium;
    @SerializedName("is_title")
    @Expose
    private String isTitle;
    @SerializedName("download")
    @Expose
    private Integer download;
    @SerializedName("video_upload_type")
    @Expose
    private String videoUploadType;
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
    @SerializedName("video_extension")
    @Expose
    private String videoExtension;
    @SerializedName("video_duration")
    @Expose
    private Integer videoDuration;
    @SerializedName("subtitle_type")
    @Expose
    private String subtitleType;
    @SerializedName("subtitle_lang_1")
    @Expose
    private String subtitleLang1;
    @SerializedName("subtitle_1")
    @Expose
    private String subtitle1;
    @SerializedName("subtitle_lang_2")
    @Expose
    private String subtitleLang2;
    @SerializedName("subtitle_2")
    @Expose
    private String subtitle2;
    @SerializedName("subtitle_lang_3")
    @Expose
    private String subtitleLang3;
    @SerializedName("subtitle_3")
    @Expose
    private String subtitle3;
    @SerializedName("view")
    @Expose
    private Integer view;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("stop_time")
    @Expose
    private Integer stopTime;
    @SerializedName("is_downloaded")
    @Expose
    private Integer isDownloaded;
    @SerializedName("is_bookmark")
    @Expose
    private Integer isBookmark;
    @SerializedName("rent_buy")
    @Expose
    private Integer rentBuy;
    @SerializedName("is_rent")
    @Expose
    private Integer isRent;
    @SerializedName("rent_price")
    @Expose
    private Integer rentPrice;
    @SerializedName("is_buy")
    @Expose
    private Integer isBuy;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("upcoming_type")
    @Expose
    private Integer upcomingType;

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

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Integer isPremium) {
        this.isPremium = isPremium;
    }

    public String getIsTitle() {
        return isTitle;
    }

    public void setIsTitle(String isTitle) {
        this.isTitle = isTitle;
    }

    public Integer getDownload() {
        return download;
    }

    public void setDownload(Integer download) {
        this.download = download;
    }

    public String getVideoUploadType() {
        return videoUploadType;
    }

    public void setVideoUploadType(String videoUploadType) {
        this.videoUploadType = videoUploadType;
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

    public String getVideoExtension() {
        return videoExtension;
    }

    public void setVideoExtension(String videoExtension) {
        this.videoExtension = videoExtension;
    }

    public Integer getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(Integer videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getSubtitleType() {
        return subtitleType;
    }

    public void setSubtitleType(String subtitleType) {
        this.subtitleType = subtitleType;
    }

    public String getSubtitleLang1() {
        return subtitleLang1;
    }

    public void setSubtitleLang1(String subtitleLang1) {
        this.subtitleLang1 = subtitleLang1;
    }

    public String getSubtitle1() {
        return subtitle1;
    }

    public void setSubtitle1(String subtitle1) {
        this.subtitle1 = subtitle1;
    }

    public String getSubtitleLang2() {
        return subtitleLang2;
    }

    public void setSubtitleLang2(String subtitleLang2) {
        this.subtitleLang2 = subtitleLang2;
    }

    public String getSubtitle2() {
        return subtitle2;
    }

    public void setSubtitle2(String subtitle2) {
        this.subtitle2 = subtitle2;
    }

    public String getSubtitleLang3() {
        return subtitleLang3;
    }

    public void setSubtitleLang3(String subtitleLang3) {
        this.subtitleLang3 = subtitleLang3;
    }

    public String getSubtitle3() {
        return subtitle3;
    }

    public void setSubtitle3(String subtitle3) {
        this.subtitle3 = subtitle3;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
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

    public Integer getStopTime() {
        return stopTime;
    }

    public void setStopTime(Integer stopTime) {
        this.stopTime = stopTime;
    }

    public Integer getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(Integer isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public Integer getIsBookmark() {
        return isBookmark;
    }

    public void setIsBookmark(Integer isBookmark) {
        this.isBookmark = isBookmark;
    }

    public Integer getRentBuy() {
        return rentBuy;
    }

    public void setRentBuy(Integer rentBuy) {
        this.rentBuy = rentBuy;
    }

    public Integer getIsRent() {
        return isRent;
    }

    public void setIsRent(Integer isRent) {
        this.isRent = isRent;
    }

    public Integer getRentPrice() {
        return rentPrice;
    }

    public void setRentPrice(Integer rentPrice) {
        this.rentPrice = rentPrice;
    }

    public Integer getIsBuy() {
        return isBuy;
    }

    public void setIsBuy(Integer isBuy) {
        this.isBuy = isBuy;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getUpcomingType() {
        return upcomingType;
    }

    public void setUpcomingType(Integer upcomingType) {
        this.upcomingType = upcomingType;
    }

}

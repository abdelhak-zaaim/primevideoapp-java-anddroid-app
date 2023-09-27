package com.cinefilmz.tv.Model.SectionBannerModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Keep
public class Result {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type_id")
    @Expose
    private Integer typeId;
    @SerializedName("video_type")
    @Expose
    private Integer videoType;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("landscape")
    @Expose
    private String landscape;
    @SerializedName("video_upload_type")
    @Expose
    private String videoUploadType;
    @SerializedName("trailer_type")
    @Expose
    private String trailerType;
    @SerializedName("subtitle_type")
    @Expose
    private String subtitleType;
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
    @SerializedName("session_id")
    @Expose
    private String sessionId;
    @SerializedName("upcoming_type")
    @Expose
    private Integer upcomingType;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getVideoUploadType() {
        return videoUploadType;
    }

    public void setVideoUploadType(String videoUploadType) {
        this.videoUploadType = videoUploadType;
    }

    public String getTrailerType() {
        return trailerType;
    }

    public void setTrailerType(String trailerType) {
        this.trailerType = trailerType;
    }

    public String getSubtitleType() {
        return subtitleType;
    }

    public void setSubtitleType(String subtitleType) {
        this.subtitleType = subtitleType;
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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getUpcomingType() {
        return upcomingType;
    }

    public void setUpcomingType(Integer upcomingType) {
        this.upcomingType = upcomingType;
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

}
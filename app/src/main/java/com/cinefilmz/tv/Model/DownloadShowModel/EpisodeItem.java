package com.cinefilmz.tv.Model.DownloadShowModel;

import android.annotation.SuppressLint;
import android.os.Parcel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class EpisodeItem implements Serializable {

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
    @SerializedName("video")
    @Expose
    private String video;
    @SerializedName("video_path")
    @Expose
    private String videoPath;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("upcoming_type")
    @Expose
    private String upcomingType;
    @SerializedName("video_url")
    @Expose
    private String videoUrl;
    @SerializedName("video_extension")
    @Expose
    private String videoExtension;
    @SerializedName("video_duration")
    @Expose
    private String videoDuration;
    @SerializedName("file_size")
    @Expose
    private long fileSize;
    @SerializedName("is_premium")
    @Expose
    private Integer isPremium;
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
    @SerializedName("element")
    @Expose
    private Integer element;
    @SerializedName("downloadid")
    @Expose
    private long downloadid;
    @SerializedName("is_downloaded")
    @Expose
    private Integer isDownloaded;
    @SerializedName("secretKey")
    @Expose
    private String secretKey;

    private int typeView = 1;

    public EpisodeItem(Integer id, Integer showId, Integer sessionId, String description, String thumbnail, String landscape, String video,
                       String videoPath, String videoType, String upcomingType, String videoUrl, String videoExtension, String videoDuration, Integer view,
                       Integer download, Integer status, String isTitle, long fileSize, Integer isPremium, String createdAt, String updatedAt,
                       Integer isDownloaded, Integer rentBuy, Integer isRent, Integer rentPrice, Integer isBuy, Integer element,
                       long downloadid, String secretKey) {
        this.id = id;
        this.showId = showId;
        this.sessionId = sessionId;
        this.description = description;
        this.thumbnail = thumbnail;
        this.landscape = landscape;
        this.video = video;
        this.videoPath = videoPath;
        this.videoType = videoType;
        this.upcomingType = upcomingType;
        this.videoUrl = videoUrl;
        this.videoExtension = videoExtension;
        this.videoDuration = videoDuration;
        this.view = view;
        this.download = download;
        this.status = status;
        this.isTitle = isTitle;
        this.fileSize = fileSize;
        this.isPremium = isPremium;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDownloaded = isDownloaded;
        this.rentBuy = rentBuy;
        this.isRent = isRent;
        this.rentPrice = rentPrice;
        this.isBuy = isBuy;
        this.element = element;
        this.downloadid = downloadid;
        this.secretKey = secretKey;
    }

    @SuppressLint("NewApi")
    protected EpisodeItem(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        showId = in.readInt();
        sessionId = in.readInt();
        description = in.readString();
        thumbnail = in.readString();
        landscape = in.readString();
        video = in.readString();
        videoPath = in.readString();
        videoType = in.readString();
        upcomingType = in.readString();
        videoUrl = in.readString();
        videoExtension = in.readString();
        videoDuration = in.readString();
        view = in.readInt();
        download = in.readInt();
        status = in.readInt();
        isTitle = in.readString();
        fileSize = in.readLong();
        isPremium = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
        isDownloaded = in.readInt();
        rentBuy = in.readInt();
        isRent = in.readInt();
        rentPrice = in.readInt();
        isBuy = in.readInt();
        if (in.readByte() == 0) {
            element = null;
        } else {
            element = in.readInt();
        }
        downloadid = in.readLong();
        secretKey = in.readString();
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoType() {
        return videoType;
    }

    public void setVideoType(String videoType) {
        this.videoType = videoType;
    }

    public String getUpcomingType() {
        return upcomingType;
    }

    public void setUpcomingType(String upcomingType) {
        this.upcomingType = upcomingType;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoExtension() {
        return videoExtension;
    }

    public void setVideoExtension(String videoExtension) {
        this.videoExtension = videoExtension;
    }

    public String getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(String videoDuration) {
        this.videoDuration = videoDuration;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getIsPremium() {
        return isPremium;
    }

    public void setIsPremium(Integer isPremium) {
        this.isPremium = isPremium;
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

    public Integer getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(Integer isDownloaded) {
        this.isDownloaded = isDownloaded;
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

    public Integer getElement() {
        return element;
    }

    public void setElement(Integer element) {
        this.element = element;
    }

    public long getDownloadid() {
        return downloadid;
    }

    public void setDownloadid(long downloadid) {
        this.downloadid = downloadid;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public int getTypeView() {
        return typeView;
    }

    public EpisodeItem setTypeView(int typeView) {
        this.typeView = typeView;
        return this;
    }

}
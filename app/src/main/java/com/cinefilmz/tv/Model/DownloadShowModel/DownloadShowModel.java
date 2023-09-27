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
public class DownloadShowModel implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("language_id")
    @Expose
    private String languageId;
    @SerializedName("cast_id")
    @Expose
    private String castId;
    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("director_id")
    @Expose
    private String directorId;
    @SerializedName("starring_id")
    @Expose
    private String starringId;
    @SerializedName("supporting_cast_id")
    @Expose
    private String supportingCastId;
    @SerializedName("networks")
    @Expose
    private String networks;
    @SerializedName("maturity_rating")
    @Expose
    private String maturityRating;
    @SerializedName("studios")
    @Expose
    private String studios;
    @SerializedName("content_advisory")
    @Expose
    private String contentAdvisory;
    @SerializedName("viewing_rights")
    @Expose
    private String viewingRights;
    @SerializedName("type_id")
    @Expose
    private Integer typeId;
    @SerializedName("video_type")
    @Expose
    private Integer videoType;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("landscape")
    @Expose
    private String landscape;
    @SerializedName("view")
    @Expose
    private Integer view;
    @SerializedName("imdb_rating")
    @Expose
    private double imdbRating;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("is_title")
    @Expose
    private String isTitle;
    @SerializedName("is_premium")
    @Expose
    private Integer isPremium;
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
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("is_buy")
    @Expose
    private Integer isBuy;
    @SerializedName("element")
    @Expose
    private Integer element;
    @SerializedName("downloadid")
    @Expose
    private long downloadid;
    @SerializedName("session")
    @Expose
    private List<SessionItem> sessionItems = null;
    @SerializedName("sessionEpisode")
    @Expose
    private List<EpisodeItem> episodeItems = null;

    private int typeView = 1;

    public DownloadShowModel(Integer id, String categoryId, String languageId, String castId, Integer channelId, String directorId,
                             String starringId, String supportingCastId, String networks, String maturityRating, String studios,
                             String contentAdvisory, String viewingRights, Integer typeId, Integer videoType, String name, String description,
                             String thumbnail, String landscape, Integer view, double imdbRating,
                             Integer status, String isTitle, Integer isPremium, String createdAt, String updatedAt, Integer stopTime,
                             Integer isDownloaded, Integer isBookmark, Integer rentBuy, Integer isRent, Integer rentPrice, Integer isBuy,
                             String categoryName, Integer element, long downloadid, List<SessionItem> sessionItems, List<EpisodeItem> episodeItems) {
        this.id = id;
        this.categoryId = categoryId;
        this.languageId = languageId;
        this.castId = castId;
        this.channelId = channelId;
        this.directorId = directorId;
        this.starringId = starringId;
        this.supportingCastId = supportingCastId;
        this.networks = networks;
        this.maturityRating = maturityRating;
        this.studios = studios;
        this.contentAdvisory = contentAdvisory;
        this.viewingRights = viewingRights;
        this.typeId = typeId;
        this.videoType = videoType;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.landscape = landscape;
        this.view = view;
        this.imdbRating = imdbRating;
        this.status = status;
        this.isTitle = isTitle;
        this.isPremium = isPremium;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.stopTime = stopTime;
        this.isDownloaded = isDownloaded;
        this.isBookmark = isBookmark;
        this.rentBuy = rentBuy;
        this.isRent = isRent;
        this.rentPrice = rentPrice;
        this.isBuy = isBuy;
        this.categoryName = categoryName;
        this.element = element;
        this.downloadid = downloadid;
        this.sessionItems = sessionItems;
        this.episodeItems = episodeItems;
    }

    @SuppressLint("NewApi")
    protected DownloadShowModel(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        categoryId = in.readString();
        languageId = in.readString();
        castId = in.readString();
        channelId = in.readInt();
        directorId = in.readString();
        starringId = in.readString();
        supportingCastId = in.readString();
        networks = in.readString();
        maturityRating = in.readString();
        studios = in.readString();
        contentAdvisory = in.readString();
        viewingRights = in.readString();
        typeId = in.readInt();
        videoType = in.readInt();
        name = in.readString();
        description = in.readString();
        thumbnail = in.readString();
        landscape = in.readString();
        view = in.readInt();
        imdbRating = in.readDouble();
        status = in.readInt();
        isTitle = in.readString();
        isPremium = in.readInt();
        createdAt = in.readString();
        updatedAt = in.readString();
        stopTime = in.readInt();
        isDownloaded = in.readInt();
        isBookmark = in.readInt();
        rentBuy = in.readInt();
        isRent = in.readInt();
        rentPrice = in.readInt();
        isBuy = in.readInt();
        categoryName = in.readString();
        if (in.readByte() == 0) {
            element = null;
        } else {
            element = in.readInt();
        }
        downloadid = in.readLong();
        sessionItems = (List<SessionItem>) in.readSerializable();
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
        dest.writeString(categoryId);
        dest.writeString(languageId);
        dest.writeString(castId);
        dest.writeInt(channelId);
        dest.writeString(directorId);
        dest.writeString(starringId);
        dest.writeString(supportingCastId);
        dest.writeString(networks);
        dest.writeString(maturityRating);
        dest.writeString(studios);
        dest.writeString(contentAdvisory);
        dest.writeString(viewingRights);
        dest.writeInt(typeId);
        dest.writeInt(videoType);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(thumbnail);
        dest.writeString(landscape);
        dest.writeInt(view);
        dest.writeDouble(imdbRating);
        dest.writeInt(status);
        dest.writeString(isTitle);
        dest.writeInt(isPremium);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeInt(stopTime);
        dest.writeInt(isDownloaded);
        dest.writeInt(isBookmark);
        dest.writeInt(rentBuy);
        dest.writeInt(isRent);
        dest.writeInt(rentPrice);
        dest.writeInt(isBuy);
        dest.writeString(categoryName);
        if (element == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(element);
        }
        dest.writeLong(downloadid);
        dest.writeSerializable((Serializable) sessionItems);
        dest.writeSerializable((Serializable) episodeItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DownloadShowModel> CREATOR = new Creator<DownloadShowModel>() {
        @Override
        public DownloadShowModel createFromParcel(Parcel in) {
            return new DownloadShowModel(in);
        }

        @Override
        public DownloadShowModel[] newArray(int size) {
            return new DownloadShowModel[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getCastId() {
        return castId;
    }

    public void setCastId(String castId) {
        this.castId = castId;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getDirectorId() {
        return directorId;
    }

    public void setDirectorId(String directorId) {
        this.directorId = directorId;
    }

    public String getStarringId() {
        return starringId;
    }

    public void setStarringId(String starringId) {
        this.starringId = starringId;
    }

    public String getSupportingCastId() {
        return supportingCastId;
    }

    public void setSupportingCastId(String supportingCastId) {
        this.supportingCastId = supportingCastId;
    }

    public String getNetworks() {
        return networks;
    }

    public void setNetworks(String networks) {
        this.networks = networks;
    }

    public String getMaturityRating() {
        return maturityRating;
    }

    public void setMaturityRating(String maturityRating) {
        this.maturityRating = maturityRating;
    }

    public String getStudios() {
        return studios;
    }

    public void setStudios(String studios) {
        this.studios = studios;
    }

    public String getContentAdvisory() {
        return contentAdvisory;
    }

    public void setContentAdvisory(String contentAdvisory) {
        this.contentAdvisory = contentAdvisory;
    }

    public String getViewingRights() {
        return viewingRights;
    }

    public void setViewingRights(String viewingRights) {
        this.viewingRights = viewingRights;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public double getImdbRating() {
        return imdbRating;
    }

    public void setImdbRating(double imdbRating) {
        this.imdbRating = imdbRating;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public List<SessionItem> getSessionItems() {
        return sessionItems;
    }

    public void setSessionItems(List<SessionItem> sessionItems) {
        this.sessionItems = sessionItems;
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

    public DownloadShowModel setTypeView(int typeView) {
        this.typeView = typeView;
        return this;
    }

}
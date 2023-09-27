package com.cinefilmz.tv.Model.RentProductModel;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

@Keep
public class Tvshow implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("channel_id")
    @Expose
    private Integer channelId;
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("language_id")
    @Expose
    private String languageId;
    @SerializedName("cast_id")
    @Expose
    private String castId;
    @SerializedName("type_id")
    @Expose
    private Integer typeId;
    @SerializedName("video_type")
    @Expose
    private Integer videoType;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("landscape")
    @Expose
    private String landscape;
    @SerializedName("trailer_type")
    @Expose
    private String trailerType;
    @SerializedName("trailer_url")
    @Expose
    private String trailerUrl;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("is_premium")
    @Expose
    private Integer isPremium;
    @SerializedName("is_title")
    @Expose
    private String isTitle;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("view")
    @Expose
    private Integer view;
    @SerializedName("imdb_rating")
    @Expose
    private double imdbRating;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
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
    @SerializedName("rent_time")
    @Expose
    private String rentTime;
    @SerializedName("rent_type")
    @Expose
    private String rentType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
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

    public String getTrailerType() {
        return trailerType;
    }

    public void setTrailerType(String trailerType) {
        this.trailerType = trailerType;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
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

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
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

    public String getRentTime() {
        return rentTime;
    }

    public void setRentTime(String rentTime) {
        this.rentTime = rentTime;
    }

    public String getRentType() {
        return rentType;
    }

    public void setRentType(String rentType) {
        this.rentType = rentType;
    }

}
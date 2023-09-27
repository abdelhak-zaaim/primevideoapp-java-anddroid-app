package com.cinefilmz.tv.Model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.cinefilmz.tv.Model.DownloadShowModel.SessionItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Keep
public class DownloadVideoItem implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("video_type")
    @Expose
    private String videoType;
    @SerializedName("upcoming_type")
    @Expose
    private String upcomingType;
    @SerializedName("type_id")
    @Expose
    private String typeId;
    @SerializedName("path")
    @Expose
    private String path;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("release_year")
    @Expose
    private String releaseYear;
    @SerializedName("age_restriction")
    @Expose
    private String ageRestriction;
    @SerializedName("max_video_quality")
    @Expose
    private String maxVideoQuality;
    @SerializedName("release_tag")
    @Expose
    private String releaseTag;
    @SerializedName("is_prime")
    @Expose
    private boolean isPrime;
    @SerializedName("size")
    @Expose
    private long size;
    @SerializedName("duration")
    @Expose
    private long duration;
    @SerializedName("is_bookmark")
    @Expose
    private Integer isBookmark;
    @SerializedName("is_downloaded")
    @Expose
    private Integer isDownloaded;
    @SerializedName("stop_time")
    @Expose
    private Integer stopTime;
    @SerializedName("element")
    @Expose
    private Integer element;
    @SerializedName("downloadid")
    @Expose
    private long downloadid;
    @SerializedName("secretKey")
    @Expose
    private String secretKey;
    @SerializedName("session")
    @Expose
    private List<SessionItem> sessionItems = null;

    private int typeView = 1;

    public DownloadVideoItem(Integer id, String title, String videoType, String typeId, String upcomingType, String releaseYear, String ageRestriction, String maxVideoQuality, String releaseTag,
                             String path, String image, boolean isPrime, long size, long duration, Integer isBookmark, Integer isDownloaded, Integer stopTime, Integer element,
                             long downloadid, String secretKey, List<SessionItem> sessionItems) {
        this.id = id;
        this.title = title;
        this.videoType = videoType;
        this.upcomingType = upcomingType;
        this.typeId = typeId;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.maxVideoQuality = maxVideoQuality;
        this.releaseTag = releaseTag;
        this.path = path;
        this.image = image;
        this.isPrime = isPrime;
        this.size = size;
        this.duration = duration;
        this.isBookmark = isBookmark;
        this.isDownloaded = isDownloaded;
        this.stopTime = stopTime;
        this.element = element;
        this.downloadid = downloadid;
        this.secretKey = secretKey;
        this.sessionItems = sessionItems;
    }

    public DownloadVideoItem() {
    }

    @SuppressLint("NewApi")
    protected DownloadVideoItem(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        videoType = in.readString();
        upcomingType = in.readString();
        typeId = in.readString();
        releaseYear = in.readString();
        ageRestriction = in.readString();
        maxVideoQuality = in.readString();
        releaseTag = in.readString();
        path = in.readString();
        image = in.readString();
        isPrime = in.readBoolean();
        size = in.readLong();
        duration = in.readLong();
        isBookmark = in.readInt();
        isDownloaded = in.readInt();
        stopTime = in.readInt();
        if (in.readByte() == 0) {
            element = null;
        } else {
            element = in.readInt();
        }
        downloadid = in.readLong();
        secretKey = in.readString();
        sessionItems = (List<SessionItem>) in.readSerializable();
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
        dest.writeString(title);
        dest.writeString(videoType);
        dest.writeString(upcomingType);
        dest.writeString(typeId);
        dest.writeString(releaseYear);
        dest.writeString(ageRestriction);
        dest.writeString(maxVideoQuality);
        dest.writeString(path);
        dest.writeString(image);
        dest.writeBoolean(isPrime);
        dest.writeLong(size);
        dest.writeLong(duration);
        dest.writeInt(isBookmark);
        dest.writeInt(isDownloaded);
        dest.writeInt(stopTime);
        if (element == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(element);
        }
        dest.writeLong(downloadid);
        dest.writeString(secretKey);
        dest.writeSerializable((Serializable) sessionItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DownloadVideoItem> CREATOR = new Creator<DownloadVideoItem>() {
        @Override
        public DownloadVideoItem createFromParcel(Parcel in) {
            return new DownloadVideoItem(in);
        }

        @Override
        public DownloadVideoItem[] newArray(int size) {
            return new DownloadVideoItem[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getAgeRestriction() {
        return ageRestriction;
    }

    public void setAgeRestriction(String ageRestriction) {
        this.ageRestriction = ageRestriction;
    }

    public String getMaxVideoQuality() {
        return maxVideoQuality;
    }

    public void setMaxVideoQuality(String maxVideoQuality) {
        this.maxVideoQuality = maxVideoQuality;
    }

    public String getReleaseTag() {
        return releaseTag;
    }

    public void setReleaseTag(String releaseTag) {
        this.releaseTag = releaseTag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getIsPrime() {
        return isPrime;
    }

    public void setIsPrime(boolean isPrime) {
        this.isPrime = isPrime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Integer getIsBookmark() {
        return isBookmark;
    }

    public void setIsBookmark(Integer isBookmark) {
        this.isBookmark = isBookmark;
    }

    public Integer getIsDownloaded() {
        return isDownloaded;
    }

    public void setIsDownloaded(Integer isDownloaded) {
        this.isDownloaded = isDownloaded;
    }

    public Integer getStopTime() {
        return stopTime;
    }

    public void setStopTime(Integer stopTime) {
        this.stopTime = stopTime;
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

    public List<SessionItem> getSessionItems() {
        return sessionItems;
    }

    public void setSessionItems(List<SessionItem> sessionItems) {
        this.sessionItems = sessionItems;
    }

    public int getTypeView() {
        return typeView;
    }

    public DownloadVideoItem setTypeView(int typeView) {
        this.typeView = typeView;
        return this;
    }

}
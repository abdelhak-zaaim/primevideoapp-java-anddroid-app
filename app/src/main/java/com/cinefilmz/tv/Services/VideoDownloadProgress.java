package com.cinefilmz.tv.Services;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoDownloadProgress implements Parcelable {

    public String videoId; // range from 1-100
    public int progress; // range from 1-100
    public long fileSize;// Total size of file to be downlaoded
    public long downloadedSize; // Size of the downlaoded file

    protected VideoDownloadProgress(Parcel in) {
        progress = in.readInt();
        videoId = in.readString();
        fileSize = in.readLong();
        downloadedSize = in.readLong();
    }

    public static final Creator<VideoDownloadProgress> CREATOR = new Creator<VideoDownloadProgress>() {
        @Override
        public VideoDownloadProgress createFromParcel(Parcel in) {
            return new VideoDownloadProgress(in);
        }

        @Override
        public VideoDownloadProgress[] newArray(int size) {
            return new VideoDownloadProgress[size];
        }
    };

    public VideoDownloadProgress() {
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(progress);
        dest.writeString(videoId);
        dest.writeLong(fileSize);
        dest.writeLong(downloadedSize);
    }

}
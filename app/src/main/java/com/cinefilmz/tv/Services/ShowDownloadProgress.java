package com.cinefilmz.tv.Services;

import android.os.Parcel;
import android.os.Parcelable;

public class ShowDownloadProgress implements Parcelable {

    public int position;
    public String epiFilePath, secretKey, showID, dSeasonID;
    public int progress; // range from 1-100
    public long fileSize;// Total size of file to be downlaoded
    public long downloadedSize; // Size of the downlaoded file

    protected ShowDownloadProgress(Parcel in) {
        position = in.readInt();
        progress = in.readInt();
        fileSize = in.readLong();
        downloadedSize = in.readLong();
        epiFilePath = in.readString();
        secretKey = in.readString();
        dSeasonID = in.readString();
        showID = in.readString();
    }

    public static final Creator<ShowDownloadProgress> CREATOR = new Creator<ShowDownloadProgress>() {
        @Override
        public ShowDownloadProgress createFromParcel(Parcel in) {
            return new ShowDownloadProgress(in);
        }

        @Override
        public ShowDownloadProgress[] newArray(int size) {
            return new ShowDownloadProgress[size];
        }
    };

    public ShowDownloadProgress() {
    }

    public void setPosition(int position) {
        this.position = position;
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

    public void setEpiFilePath(String epiFilePath) {
        this.epiFilePath = epiFilePath;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setShowID(String showID) {
        this.showID = showID;
    }

    public void setdSeasonID(String dSeasonID) {
        this.dSeasonID = dSeasonID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(position);
        dest.writeInt(progress);
        dest.writeLong(fileSize);
        dest.writeLong(downloadedSize);
        dest.writeString(epiFilePath);
        dest.writeString(secretKey);
        dest.writeString(dSeasonID);
        dest.writeString(showID);
    }

}
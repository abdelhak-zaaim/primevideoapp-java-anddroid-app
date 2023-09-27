package com.cinefilmz.tv.Interface;

import com.cinefilmz.tv.Services.VideoDownloadProgress;

public interface DownloadVideoListener {
    void onProgressUpdate(VideoDownloadProgress progress);

    void onStartDownload(String url);

    void OnDownloadCompleted();

    void onCancelDownload();
}
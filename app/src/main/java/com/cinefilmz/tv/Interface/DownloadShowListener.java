package com.cinefilmz.tv.Interface;

import com.cinefilmz.tv.Services.ShowDownloadProgress;

public interface DownloadShowListener {
    void onDShowProgress(ShowDownloadProgress progress);

    void onDShowStart(String url);

    void onDShowCompleted();

    void onDShowCancel();
}
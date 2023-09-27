package com.cinefilmz.tv.Utils;

import android.util.Log;

import java.io.File;

public class DirectoryCleaner {
    private final File mFile;

    public DirectoryCleaner(File file) {
        mFile = file;
    }

    public void clean() {
        if (null == mFile || !mFile.exists() || !mFile.isDirectory()) return;
        for (File file : mFile.listFiles()) {
            Log.e("clean", "filePath ==>> " + file.getPath());
            delete(file);
        }
    }

    private void delete(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                Log.e("delete", "child ==>> " + file.getPath());
                delete(child);
            }
        }
        file.delete();
    }
}

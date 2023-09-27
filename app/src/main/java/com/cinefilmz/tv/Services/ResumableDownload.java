package com.cinefilmz.tv.Services;

import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cinefilmz.tv.Interface.DownloadVideoListener;
import com.cinefilmz.tv.Utils.PrefManager;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.crypto.AesCipherDataSink;
import com.google.android.exoplayer2.util.Util;

import java.io.*;
import java.net.*;

public class ResumableDownload {

    private PrefManager prefManager;
    private static final String TAG = SecureVideoDownload.class.getSimpleName();

    private static String mainKeyAlias = "", downloadFrom = "";
    private static int fileLength;
    private static Handler handler = new Handler(Looper.getMainLooper());

    private static DownloadVideoListener downloadVideoListener;

    public static long downloadFile(String downloadUrl, String saveAsFileName) throws IOException, URISyntaxException {
        File outputFile = new File(saveAsFileName);
        URLConnection downloadFileConnection = new URI(downloadUrl).toURL().openConnection();
        return transferDataAndGetBytesDownloaded(downloadFileConnection, outputFile);
    }

    private static long transferDataAndGetBytesDownloaded(URLConnection downloadFileConnection, File outputFile) throws IOException {

        long bytesDownloaded = 0;
        AesCipherDataSink encryptingDataSink = null;
        try (InputStream is = downloadFileConnection.getInputStream()) {

            Log.e(TAG, "mainKeyAlias ===>>> " + mainKeyAlias);
            //Downloading and encrypting the Video
            encryptingDataSink = new AesCipherDataSink(Util.getUtf8Bytes(mainKeyAlias), new DataSink() {
                private FileOutputStream fileOutputStream;

                @Override
                public void open(DataSpec dataSpec) throws IOException {
                    fileOutputStream = new FileOutputStream(outputFile);
                }

                @Override
                public void write(byte[] buffer, int offset, int length) throws IOException {
                    fileOutputStream.write(buffer, offset, length);
                }

                @Override
                public void close() throws IOException {
                    fileOutputStream.close();
                }
            });
            // Push the data through the sink, and close everything.
            encryptingDataSink.open(new DataSpec(Uri.fromFile(outputFile)));

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            fileLength = downloadFileConnection.getContentLength();
            Log.e(TAG, "fileLength ===>>> " + fileLength);

            byte data[] = new byte[1024 * 1024];
            int bytesCount;

            while ((bytesCount = is.read(data)) != -1) {
                bytesDownloaded += bytesCount;

                // publishing the progress....
                if (fileLength > 0) {
                    // only if total length is known
                    int progress = (int) (bytesDownloaded * 100 / fileLength);

                    VideoDownloadProgress videoDownloadProgress = new VideoDownloadProgress();
                    videoDownloadProgress.setProgress(progress);
                    videoDownloadProgress.setDownloadedSize(bytesDownloaded);
                    videoDownloadProgress.setFileSize(fileLength);
                    Log.e(TAG, "progress ===>>> " + videoDownloadProgress.progress);
                    Log.e(TAG, "fileSize ===>>> " + videoDownloadProgress.fileSize);
                    Log.e(TAG, "downloadedSize ===>>> " + videoDownloadProgress.downloadedSize);
                    downloadVideoListener.onProgressUpdate(videoDownloadProgress);
                }

                encryptingDataSink.write(data, 0, bytesCount);
            }
        }
        return bytesDownloaded;
    }

    public static long downloadFileWithResume(String downloadUrl, String saveAsFileName) throws IOException, URISyntaxException {
        File outputFile = new File(saveAsFileName);

        URLConnection downloadFileConnection = addFileResumeFunctionality(downloadUrl, outputFile);
        return transferDataAndGetBytesDownloaded(downloadFileConnection, outputFile);
    }

    private static URLConnection addFileResumeFunctionality(String downloadUrl, File outputFile) throws IOException, URISyntaxException, ProtocolException, ProtocolException {
        long existingFileSize = 0L;
        URLConnection downloadFileConnection = new URI(downloadUrl).toURL().openConnection();

        if (outputFile.exists() && downloadFileConnection instanceof HttpURLConnection) {
            HttpURLConnection httpFileConnection = (HttpURLConnection) downloadFileConnection;

            HttpURLConnection tmpFileConn = (HttpURLConnection) new URI(downloadUrl).toURL().openConnection();
            tmpFileConn.setRequestMethod("HEAD");
            long fileLength = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fileLength = tmpFileConn.getContentLengthLong();
            } else {
                fileLength = tmpFileConn.getContentLength();
            }
            existingFileSize = outputFile.length();

            if (existingFileSize < fileLength) {
                httpFileConnection.setRequestProperty("Range", "bytes=" + existingFileSize + "-" + fileLength);
            } else {
                throw new IOException("File Download already completed.");
            }
        }
        return downloadFileConnection;
    }

}
package com.cinefilmz.tv.Services;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.cinefilmz.tv.Interface.DownloadVideoListener;
import com.cinefilmz.tv.Utils.PrefManager;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.crypto.AesCipherDataSink;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SecureVideoDownload extends AsyncTask<String, Integer, String> {

    private PrefManager prefManager;
    private static final String TAG = SecureVideoDownload.class.getSimpleName();

    private Context mContext;
    private File mTargetFile;
    private String mainKeyAlias = "";
    private int itemID;

    boolean mIsDownloading;
    long mDownloaded = 0, mFileLength, fileLength = -1;

    private DownloadVideoListener downloadVideoListener;

    //Constructor parameters :
    // @context (current Activity)
    // @targetFile (path for saving video)
    // @sectionDetailList (Video Details data)
    public SecureVideoDownload(Context context, int itemID, File targetFile, String mainKeyAlias, DownloadVideoListener downloadVideoListener) {
        this.mContext = context;
        this.itemID = itemID;
        this.mTargetFile = targetFile;
        this.mainKeyAlias = mainKeyAlias;
        this.downloadVideoListener = downloadVideoListener;
        prefManager = new PrefManager(mContext);

        // reference to instance to use inside listener
        final SecureVideoDownload me = this;
        Log.i(TAG, "Constructor done");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // take CPU lock to prevent CPU from going off if the user
        // presses the power button during download
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        AesCipherDataSink encryptingDataSink = null;
        HttpURLConnection httpURLConnection = null;
        try {
            Log.e(TAG, "sUrl ===>>> " + sUrl[0]);
            URL url = new URL(sUrl[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setChunkedStreamingMode(0);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            Log.e(TAG, "mIsDownloading ===>>> " + mIsDownloading);
            if (mIsDownloading) {
                httpURLConnection.setRequestProperty("Range", "bytes=" + mDownloaded + "-");
                Log.e(TAG, "mTargetFile length is : " + mTargetFile.length());
                Log.e(TAG, "Set range to : " + mDownloaded);
            }
            httpURLConnection.connect();

            Log.e(TAG, "mainKeyAlias ===>>> " + mainKeyAlias);

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            if (mIsDownloading) {
                Log.e(TAG, "mDownloaded ===>>> " + mDownloaded);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileLength = (int) (mDownloaded + httpURLConnection.getContentLengthLong());
                } else {
                    fileLength = mDownloaded + httpURLConnection.getContentLength();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fileLength = (int) httpURLConnection.getContentLengthLong();
                } else {
                    fileLength = httpURLConnection.getContentLength();
                }
            }
            Log.e(TAG, "fileLength ===>>> " + fileLength);
            mFileLength = fileLength;
            Log.e(TAG, "mFileLength ===>>> " + mFileLength);

            //Downloading and encrypting the Video
            encryptingDataSink = new AesCipherDataSink(Util.getUtf8Bytes(mainKeyAlias), new DataSink() {
                private FileOutputStream fileOutputStream;

                @Override
                public void open(DataSpec dataSpec) throws IOException {
                    Log.e(TAG, "mIsDownloading ==>>" + mIsDownloading);
                    if (mIsDownloading) {
                        fileOutputStream = new FileOutputStream(mTargetFile, true);
                    } else {
                        fileOutputStream = new FileOutputStream(mTargetFile);
                    }
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
            encryptingDataSink.open(new DataSpec(Uri.fromFile(mTargetFile)));

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (httpURLConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + httpURLConnection.getResponseCode() + " " + httpURLConnection.getResponseMessage();
            }
            Log.i(TAG, "Response " + httpURLConnection.getResponseCode());

            // download the file
            input = httpURLConnection.getInputStream();

            byte data[] = new byte[1024 * 1024];
            long downloaded = 0;
            int count;

            while ((count = input.read(data)) != -1) {
                mIsDownloading = true;

                downloaded += count;
                mDownloaded = mDownloaded + count;

                Log.e(TAG, "<<<<===  Downloaded  ===>>>> " + downloaded);

                // publishing the progress....
                if (fileLength > 0) {
                    // only if total length is known
                    int progress = (int) (mDownloaded * 100 / fileLength);

                    VideoDownloadProgress videoDownloadProgress = new VideoDownloadProgress();
                    videoDownloadProgress.setProgress(progress);
                    videoDownloadProgress.setDownloadedSize(downloaded);
                    videoDownloadProgress.setFileSize(fileLength);
                    videoDownloadProgress.setVideoId("" + itemID);
                    Log.e(TAG, "progress ===>>> " + videoDownloadProgress.progress);
                    Log.e(TAG, "fileSize ===>>> " + videoDownloadProgress.fileSize);
                    Log.e(TAG, "downloadedSize ===>>> " + videoDownloadProgress.downloadedSize);
                    downloadVideoListener.onProgressUpdate(videoDownloadProgress);
                }

                encryptingDataSink.write(data, 0, count);

                Log.e(TAG, "Downloaded so far :  ===>>>> " + mDownloaded);
                Log.e(TAG, "Total Download :  ===>>>> " + mFileLength);
                if (mFileLength == mDownloaded) {
                    mIsDownloading = false;
                    mDownloaded = 0;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Exception ===>>>> " + e);
            return e.toString();
        } finally {
            try {
                if (encryptingDataSink != null)
                    encryptingDataSink.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                Log.e(TAG, "IOException ===>>>> " + ignored);
            }

            if (httpURLConnection != null)
                httpURLConnection.disconnect();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false
        //com.cinefilmz.tv.Utils.Log.log("<<==== progress : " + progress[0] + " ====>>");
        //downloadListener.onProgressUpdate(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "Work Done! PostExecute");
        Log.e(TAG, "onPostExecute ===>>>> " + result);
        if (result == null) {
            downloadVideoListener.OnDownloadCompleted();
        } else {
            downloadVideoListener.onCancelDownload();
        }
        if (mFileLength == mDownloaded) {
            mIsDownloading = false;
            mDownloaded = 0;
        } else {
            Log.e(TAG, "Download not complete, trying again in 30 seconds");
        }
    }

}
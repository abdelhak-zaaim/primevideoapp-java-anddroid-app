package com.cinefilmz.tv.Services;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.cinefilmz.tv.Model.SectionDetailModel.Result;
import com.cinefilmz.tv.Utils.Functions;
import com.cinefilmz.tv.Interface.DownloadShowListener;
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
import java.util.ArrayList;
import java.util.List;

public class SecureShowDownload extends AsyncTask<String, Integer, String> {

    private PrefManager prefManager;
    private static final String TAG = SecureShowDownload.class.getSimpleName();

    private Context mContext;
    private File mTargetFile;
    private String mainKeyAlias = "", seasonName = "";
    private Result sectionDetails;
    private List<com.cinefilmz.tv.Model.VideoSeasonModel.Result> showEpisodeList;

    boolean mIsDownloading;
    long mDownloaded = 0, mFileLength, fileLength = -1;

    private DownloadShowListener downloadShowListener;

    //Constructor parameters :
    // @context (current Activity)
    // @targetFile (path for saving video)
    // @sectionDetailList (Video Details data)
    public SecureShowDownload(Context context, Result sectionDetails,
                              String seasonName, List<com.cinefilmz.tv.Model.VideoSeasonModel.Result> showEpisodeList, DownloadShowListener downloadShowListener) {
        this.sectionDetails = new Result();
        this.showEpisodeList = new ArrayList<>();
        this.mContext = context;
        this.sectionDetails = sectionDetails;
        this.seasonName = seasonName;
        this.showEpisodeList = showEpisodeList;
        this.downloadShowListener = downloadShowListener;
        prefManager = new PrefManager(mContext);

        // reference to instance to use inside listener
        final SecureShowDownload me = this;
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
            for (int i = 0; i < showEpisodeList.size(); i++) {

                mainKeyAlias = "" + Functions.getRandomString();
                Log.e("mainKeyAlias", "== " + i + " ==>>" + mainKeyAlias);

                mTargetFile = new File(Functions.getAppFolder(mContext.getApplicationContext())
                        + "downloads" + "/" + sectionDetails.getName() + "/" + seasonName,
                        "" + seasonName.replace(" ", "_").replace(".", "")
                                .replaceAll("[^\\.A-Za-z0-9_]", "")
                                + "Ep" + (i + 1) + "_" + showEpisodeList.get(i).getId() + "" + prefManager.getLoginId()
                                + "." + showEpisodeList.get(i).getVideoExtension());

                if (!mTargetFile.exists()) {
                    mTargetFile.getParentFile().mkdirs();
                } else {
                    if (mTargetFile.length() > 0) {

                    }
                }

                Log.e(TAG, "mTargetFile == " + i + " ==>>" + mTargetFile);
                Log.e(TAG, "epi Video == " + i + " ==>> " + showEpisodeList.get(i).getVideo320());

                URL url = new URL(showEpisodeList.get(i).getVideo320());
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
                Log.e(TAG, "fileLength == " + i + " ==>>" + fileLength);
                mFileLength = fileLength;
                Log.e(TAG, "mFileLength == " + i + " ==>>" + mFileLength);

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
                Log.e(TAG, "Response " + httpURLConnection.getResponseCode());

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
                    if (mFileLength > 0) {
                        // only if total length is known
                        int progress = (int) (mDownloaded * 100 / fileLength);
                        Log.e(TAG, "<<<<===  progress  ===>>>> " + progress);

                        ShowDownloadProgress showDownloadProgress = new ShowDownloadProgress();
                        showDownloadProgress.setPosition(i);
                        showDownloadProgress.setdSeasonID("" + showEpisodeList.get(i).getSessionId());
                        showDownloadProgress.setShowID("" + sectionDetails.getId());
                        showDownloadProgress.setProgress(progress);
                        showDownloadProgress.setDownloadedSize(downloaded);
                        showDownloadProgress.setFileSize(fileLength);
                        showDownloadProgress.setEpiFilePath(mTargetFile.getPath());
                        showDownloadProgress.setSecretKey(mainKeyAlias);
                        Log.e("==========", "=======================================");
                        Log.e(TAG, "position ===>>> " + showDownloadProgress.position);
                        Log.e(TAG, "progress ===>>> " + showDownloadProgress.progress);
                        Log.e(TAG, "fileSize ===>>> " + showDownloadProgress.fileSize);
                        Log.e(TAG, "downloadedSize ===>>> " + showDownloadProgress.downloadedSize);
                        Log.e(TAG, "epiFilePath ===>>> " + showDownloadProgress.epiFilePath);
                        Log.e(TAG, "secretKey ===>>> " + showDownloadProgress.secretKey);
                        Log.e("==========", "=======================================");
                        downloadShowListener.onDShowProgress(showDownloadProgress);
                    }

                    encryptingDataSink.write(data, 0, count);

                    Log.e(TAG, "Downloaded so far :  ===>>>> " + mDownloaded);
                    Log.e(TAG, "Total Download :  ===>>>> " + mFileLength);
                    if (mFileLength == mDownloaded) {
                        mIsDownloading = false;
                        mDownloaded = 0;
                    }
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
            downloadShowListener.onDShowCompleted();
        } else {
            downloadShowListener.onDShowCancel();
        }
        if (mFileLength == mDownloaded) {
            mIsDownloading = false;
            mDownloaded = 0;
        } else {
            Log.e(TAG, "Download not complete, trying again in 30 seconds");
        }
    }

}
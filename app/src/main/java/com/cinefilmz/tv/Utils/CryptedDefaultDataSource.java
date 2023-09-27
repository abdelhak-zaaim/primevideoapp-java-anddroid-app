package com.cinefilmz.tv.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.upstream.crypto.AesCipherDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CryptedDefaultDataSource implements DataSource {

    private static final String TAG = CryptedDefaultDataSource.class.getSimpleName();

    private final String secretkey;
    private final List<TransferListener> transferListeners;
    private final DataSource baseDataSource;
    private @Nullable
    DataSource fileDataSource, aesCipherDataSource, dataSource;
    private Context context;

    public CryptedDefaultDataSource(Context context, DataSource baseDataSource, String secretkey) {
        this.context = context;
        this.baseDataSource = Assertions.checkNotNull(baseDataSource);
        this.secretkey = secretkey;

        Log.e(TAG, "secretkey ==>>> " + this.secretkey);
        Log.e(TAG, "baseDataSource Uri ==>>> " + baseDataSource.getUri());
        transferListeners = new ArrayList<>();
    }

    @Override
    public void addTransferListener(TransferListener transferListener) {
        baseDataSource.addTransferListener(transferListener);
        transferListeners.add(transferListener);
        maybeAddListenerToDataSource(fileDataSource, transferListener);
        maybeAddListenerToDataSource(aesCipherDataSource, transferListener);
    }

    @Override
    public long open(DataSpec dataSpec) throws IOException {
        Assertions.checkState(dataSource == null);
        Log.e(TAG, "open dataSpec uri ==>>> " + dataSpec.uri);
        if (Util.isLocalFileUri(dataSpec.uri)) {
            dataSource = getCryptedDataSource(getFileDataSource());
            Log.e(TAG, "getFileDataSource ==>>> " + dataSource.getUri());
        } else {
            Log.e(TAG, "baseDataSource ==>>> " + baseDataSource.getUri());
            dataSource = getCryptedDataSource(baseDataSource);
        }
        return dataSource.open(dataSpec);
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws IOException {
        Log.e("TAG", "read dataSource uri ==>>> " + dataSource.getUri());
        return Assertions.checkNotNull(dataSource).read(buffer, offset, readLength);
    }

    @Nullable
    @Override
    public Uri getUri() {
        Log.e(TAG, "dataSource uri ==>>> " + dataSource.getUri());
        return dataSource == null ? null : dataSource.getUri();
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return dataSource == null
                ? DataSource.super.getResponseHeaders()
                : dataSource.getResponseHeaders();
    }

    @Override
    public void close() throws IOException {
        if (dataSource != null) {
            try {
                dataSource.close();
            } finally {
                dataSource = null;
            }
        }
    }

    private DataSource getFileDataSource() {
        if (fileDataSource == null) {
            fileDataSource = new FileDataSource();
            addListenersToDataSource(fileDataSource);
        }
        return fileDataSource;
    }

    private DataSource getCryptedDataSource(DataSource upstreamDataSource) {
        if (aesCipherDataSource == null) {
            Log.e(TAG, "upstreamDataSource uri ==>>> " + upstreamDataSource.getUri());
            Log.e(TAG, "secretkey ==>>> " + secretkey);
            Log.e(TAG, "secretkey =in Bytes=>>> " + secretkey.getBytes());
            aesCipherDataSource = new AesCipherDataSource(secretkey.getBytes(), upstreamDataSource);
            addListenersToDataSource(aesCipherDataSource);
        }
        return aesCipherDataSource;
    }

    private void addListenersToDataSource(DataSource dataSource) {
        for (int i = 0; i < transferListeners.size(); i++) {
            dataSource.addTransferListener(transferListeners.get(i));
        }
    }

    private void maybeAddListenerToDataSource(
            @Nullable DataSource dataSource, TransferListener listener) {
        if (dataSource != null) {
            dataSource.addTransferListener(listener);
        }
    }

}
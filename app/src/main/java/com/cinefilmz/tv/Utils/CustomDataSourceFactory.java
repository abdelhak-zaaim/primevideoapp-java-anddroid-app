package com.cinefilmz.tv.Utils;

import android.content.Context;

import com.google.android.exoplayer2.upstream.DataSource;

public class CustomDataSourceFactory implements DataSource.Factory {

    private final Context context;
    private final DataSource.Factory baseDataSourceFactory;
    private final String secretkey;

    public CustomDataSourceFactory(Context context, DataSource.Factory baseDataSourceFactory, String secretkey) {
        this.context = context.getApplicationContext();
        this.baseDataSourceFactory = baseDataSourceFactory;
        this.secretkey = secretkey;
    }

    @Override
    public DataSource createDataSource() {
        return new CryptedDefaultDataSource(context, baseDataSourceFactory.createDataSource(), secretkey);
    }

}
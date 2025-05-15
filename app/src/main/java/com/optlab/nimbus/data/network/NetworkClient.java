package com.optlab.nimbus.data.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class NetworkClient {
    protected final HttpLoggingInterceptor httpLoggingInterceptor =
            new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    protected final OkHttpClient okHttpClient =
            new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
}

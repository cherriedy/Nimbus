package com.optlab.nimbus.data.network.tomorrowio;

import com.optlab.nimbus.constant.TomorrowIoConstant;
import com.optlab.nimbus.data.network.NetworkClient;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** TomorrowIoClient is a singleton class that provides an instance of TomorrowIoService */
@Getter
@Singleton
public class TomorrowIoClient extends NetworkClient {
    private final TomorrowIoService tomorrowIoService;

    @Inject
    public TomorrowIoClient() {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(TomorrowIoConstant.BASE_URL)
                        // Add RxJava3CallAdapterFactory for RxJava support
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        // Add GsonConverterFactory for JSON parsing
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient) // Set the OkHttpClient instance
                        .build();

        // Create an instance of TomorrowIoService using the Retrofit instance
        tomorrowIoService = retrofit.create(TomorrowIoService.class);
    }
}

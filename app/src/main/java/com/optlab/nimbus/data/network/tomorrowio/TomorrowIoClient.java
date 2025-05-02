package com.optlab.nimbus.data.network.tomorrowio;

import com.optlab.nimbus.constant.TomorrowIoConstant;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import lombok.Getter;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** TomorrowIoClient is a singleton class that provides an instance of TomorrowIoService */
@Getter // Lombok annotation to generate getter methods
@Singleton // Dagger Hilt annotation to indicate a singleton instance
public class TomorrowIoClient {
    private final TomorrowIoService tomorrowIoService;

    @Inject // Dagger Hilt annotation to inject dependencies
    public TomorrowIoClient() {
        // Create an instance of HttpLoggingInterceptor for logging HTTP requests and responses
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create an instance of OkHttpClient with the logging interceptor
        OkHttpClient okHttpClient =
                new OkHttpClient.Builder()
                        .addInterceptor(httpLoggingInterceptor) // Add the logging interceptor
                        .build(); // Build the OkHttpClient instance

        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(TomorrowIoConstant.BASE_URL) // Base URL for the API
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

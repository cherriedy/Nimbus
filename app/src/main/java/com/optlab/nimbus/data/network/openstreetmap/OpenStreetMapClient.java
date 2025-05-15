package com.optlab.nimbus.data.network.openstreetmap;

import com.google.gson.Gson;
import com.optlab.nimbus.data.network.NetworkClient;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import lombok.Getter;
import lombok.Setter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Getter
@Singleton
public class OpenStreetMapClient extends NetworkClient {
    private final OpenStreetMapService openStreetMapService;

    @Inject
    public OpenStreetMapClient() {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://nominatim.openstreetmap.org/")
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

        // Create an instance of OpenStreetMapService using the Retrofit instance
        openStreetMapService = retrofit.create(OpenStreetMapService.class);
    }
}

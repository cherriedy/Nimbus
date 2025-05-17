package com.optlab.nimbus.data.network.openstreetmap;

import com.optlab.nimbus.data.network.BaseRetrofitClient;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Getter
@Singleton
public class OpenStreetMapRetrofitClient extends BaseRetrofitClient {
    private final OpenStreetMapService openStreetMapService;

    @Inject
    public OpenStreetMapRetrofitClient() {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl("https://nominatim.openstreetmap.org/")
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

        openStreetMapService = retrofit.create(OpenStreetMapService.class);
    }
}

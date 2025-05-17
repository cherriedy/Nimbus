package com.optlab.nimbus.data.network.tomorrowio;

import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.network.BaseRetrofitClient;
import com.optlab.nimbus.data.repository.interfaces.PreferencesRepository;

import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import lombok.Getter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** TomorrowIoClient is a singleton class that provides an instance of TomorrowIoService */
public class TomorrowIoClient extends BaseRetrofitClient {
    private static final String BASE_URL = "https://api.tomorrow.io/";
    private final TomorrowIoService tomorrowIoService;

    public TomorrowIoClient() {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(okHttpClient)
                        .build();

        tomorrowIoService = retrofit.create(TomorrowIoService.class);
    }

    public Single<TomorrowIoResponse> getForecast(
            Coordinates coordinates, TimeZone timeZone, String apiKey) {
        return tomorrowIoService.getForecast(
                coordinates.getCoordinates(),
                TomorrowIoService.FIELDS,
                TomorrowIoService.TIMESTEPS,
                TomorrowIoService.METRIC,
                TomorrowIoService.PLUS_1_DAYS_FROM_TODAY,
                TomorrowIoService.PLUS_5_DAYS_FROM_TODAY,
                timeZone.getID(),
                apiKey);
    }
}

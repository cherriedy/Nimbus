package com.optlab.nimbus.data.network.tomorrowio;

import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.TomorrowIoResponse;

import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Observable;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** TomorrowIoClient is a singleton class that provides an instance of TomorrowIoService */
@Singleton
public class TomorrowIoClient {
    public static final String BASE_URL = "https://api.tomorrow.io/";

    public static final String CURRENT_WEATHER_FIELDS =
            String.join(
                    ",",
                    "temperature",
                    "temperatureMax",
                    "temperatureMin",
                    "weatherCode",
                    "windSpeed",
                    "humidity",
                    "pressureSurfaceLevel");
    public static final String HOURLY_WEATHER_FIELDS =
            String.join(",", "temperature", "weatherCode");
    public static final String DAILY_WEATHER_FIELDS =
            String.join(
                    ",",
                    "temperature",
                    "temperatureMax",
                    "temperatureMin",
                    "weatherCode",
                    "sunriseTime",
                    "sunsetTime",
                    "windSpeed",
                    "humidity",
                    "pressureSurfaceLevel");
    public static final String TIMESTEPS_ONE_DAY = "1d";
    public static final String TIMESTEPS_ONE_HOUR = "1h";
    public static final String TIMESTEPS_CURRENT = "current";
    public static final String PLUS_1_DAYS_FROM_TODAY = "nowPlus1d";
    public static final String PLUS_5_DAYS_FROM_TODAY = "nowPlus5d";
    public static final String METRIC = "metric";
    private static final HttpLoggingInterceptor.Level LOG_LEVEL = HttpLoggingInterceptor.Level.BODY;

    private final TomorrowIoService tomorrowIoService;

    @Inject
    public TomorrowIoClient() {
        this.tomorrowIoService =
                createRetrofit(createOkHttpClient()).create(TomorrowIoService.class);
    }

    /**
     * Get weather forecast for given coordinates and parameters.
     *
     * @param coordinates Location coordinates
     * @param fields Fields to request
     * @param timesteps Timesteps to request
     * @param timeZone Timezone
     * @param apiKey API key
     * @return Observable of TomorrowIoResponse
     */
    public Observable<TomorrowIoResponse> getForecast(
            final Coordinates coordinates,
            final String fields,
            final String timesteps,
            final TimeZone timeZone,
            final String apiKey) {
        return tomorrowIoService.getWeatherByLocationCode(
                coordinates.getCoordinates(),
                fields,
                timesteps,
                METRIC,
                timeZone.getID(),
                apiKey);
    }

    /**
     * Get weather forecast for given coordinates and parameters with start and end time.
     *
     * @param coordinates Location coordinates
     * @param fields Fields to request
     * @param timesteps Timesteps to request
     * @param startTime Start time
     * @param endTime End time
     * @param timeZone Timezone
     * @param apiKey API key
     * @return Observable of TomorrowIoResponse
     */
    public Observable<TomorrowIoResponse> getForecast(
            final Coordinates coordinates,
            final String fields,
            final String timesteps,
            final String startTime,
            final String endTime,
            final TimeZone timeZone,
            final String apiKey) {
        return tomorrowIoService.getWeatherByLocationCode(
                coordinates.getCoordinates(),
                fields,
                timesteps,
                METRIC,
                startTime,
                endTime,
                timeZone.getID(),
                apiKey);
    }

    private static OkHttpClient createOkHttpClient() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(LOG_LEVEL);
        return new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
    }

    private static Retrofit createRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }
}

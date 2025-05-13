package com.optlab.nimbus.data.model.mapper;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.optlab.nimbus.R;
import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.model.tomorrowio.TomorrowIoResponse;

import java.util.ArrayList;
import java.util.List;

/** Maps TomorrowIoResponse to a list of UnifiedWeatherResponse. */
public final class TomorrowIoMapper {
    private TomorrowIoMapper() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    /**
     * Maps TomorrowIoResponse to a list of WeatherResponse.
     *
     * @param response TomorrowIoResponse
     * @return List of WeatherResponse
     */
    public static List<WeatherResponse> map(@NonNull TomorrowIoResponse response) {
        // List is used to store the weather summaries (daily weather data)
        List<WeatherResponse> weatherSummaries = new ArrayList<>();

        response.data() // Get the data from the response
                .timelines() // Get the timelines from the data
                .get(0) // Get the first timeline (daily)
                .intervals() // Get the intervals from the timeline
                .forEach(
                        interval -> {
                            // Create a new WeatherResponse object for each interval
                            WeatherResponse weather = new WeatherResponse();
                            weather.setDate(interval.startTime()); // Set the string date
                            weather.setTemperature(interval.values().temperature());
                            weather.setTemperatureMin(interval.values().temperatureMin());
                            weather.setTemperatureMax(interval.values().temperatureMax());
                            weather.setPressure(interval.values().pressureSurfaceLevel());
                            weather.setWindSpeed(interval.values().windSpeed());
                            weather.setHumidity(interval.values().humidity());

                            int weatherCode = interval.values().weatherCode();
                            weather.setWeatherCode(weatherCode);
                            weather.setWeatherIcon(mapWeatherCodeToIcon(weatherCode));
                            weather.setWeatherDescription(mapWeatherCodeToDescription(weatherCode));

                            weatherSummaries.add(weather); // Add the weather summary to the list
                        });
        return weatherSummaries;
    }

    /**
     * Maps the weather code to a drawable resource ID.
     *
     * @param code the weather code to map
     * @return the drawable resource ID for the weather icon
     */
    private static int mapWeatherCodeToIcon(int code) {
        return switch (code) {
            case 1000 -> R.drawable.ic_large_2x_10000_clear;
            case 1001 -> R.drawable.ic_large_2x_10010_cloudy;
            case 1100 -> R.drawable.ic_large_2x_11000_mostly_clear;
            case 1101 -> R.drawable.ic_large_2x_11010_partly_cloudy;
            case 1102 -> R.drawable.ic_large_2x_11020_mostly_cloudy;
            case 2000 -> R.drawable.ic_large_2x_20000_fog;
            case 2100 -> R.drawable.ic_large_2x_21000_light_fog;
            case 4000 -> R.drawable.ic_large_2x_40000_drizzle;
            case 4001 -> R.drawable.ic_large_2x_40010_rain;
            case 4200 -> R.drawable.ic_large_2x_42000_light_rain;
            case 4201 -> R.drawable.ic_large_2x_42010_heavy_rain;
            case 5000 -> R.drawable.ic_large_2x_50000_snow;
            case 5001 -> R.drawable.ic_large_2x_50010_flurries;
            case 5100 -> R.drawable.ic_large_2x_51000_light_snow;
            case 5101 -> R.drawable.ic_large_2x_51010_heavy_snow;
            case 6000 -> R.drawable.ic_large_2x_60000_freezing_rain_drizzle;
            case 6001 -> R.drawable.ic_large_2x_60010_freezing_rain;
            case 6200 -> R.drawable.ic_large_2x_62000_light_freezing_rain;
            case 6201 -> R.drawable.ic_large_2x_62010_heavy_freezing_rain;
            case 7000 -> R.drawable.ic_large_2x_70000_ice_pellets;
            case 7101 -> R.drawable.ic_large_2x_71010_heavy_ice_pellets;
            case 7102 -> R.drawable.ic_large_2x_71020_light_ice_pellets;
            case 8000 -> R.drawable.ic_large_2x_80000_thunderstorm;
            default -> throw new IllegalStateException("Unexpected value: " + code);
        };
    }

    /**
     * Maps the weather code to a string resource ID.
     *
     * @param code the weather code to map
     * @return the string resource ID for the weather description
     */
    @StringRes
    private static int mapWeatherCodeToDescription(int code) {
        return switch (code) {
            case 1000 -> R.string.clear_sunny;
            case 1100 -> R.string.mostly_clear;
            case 1101 -> R.string.partly_cloudy;
            case 1102 -> R.string.mostly_cloudy;
            case 1001 -> R.string.cloudy;
            case 2000 -> R.string.fog;
            case 2100 -> R.string.light_fog;
            case 4000 -> R.string.drizzle;
            case 4001 -> R.string.rain;
            case 4200 -> R.string.light_rain;
            case 4201 -> R.string.heavy_rain;
            case 5000 -> R.string.snow;
            case 5001 -> R.string.flurries;
            case 5100 -> R.string.light_snow;
            case 5101 -> R.string.heavy_snow;
            case 6000 -> R.string.freezing_drizzle;
            case 6001 -> R.string.freezing_rain;
            case 6200 -> R.string.light_freezing_rain;
            case 6201 -> R.string.heavy_freezing_rain;
            case 7000 -> R.string.ice_pellets;
            case 7101 -> R.string.heavy_ice_pellets;
            case 7102 -> R.string.light_ice_pellets;
            case 8000 -> R.string.thunderstorm;
            default -> R.string.unknown;
        };
    }
}

package com.optlab.nimbus.data.model.mapper;

import android.content.Context;

import androidx.annotation.NonNull;

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
    public static List<WeatherResponse> map(
            @NonNull Context context, @NonNull TomorrowIoResponse response) {
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
                            weather.setWeatherDescription(
                                    mapWeatherCodeToDescription(context, weatherCode));

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
            case 1000 -> R.drawable.ic_clear_day;
            case 1100, 1101, 1001 -> R.drawable.cloudy;
            case 1102 -> R.drawable.ic_broken_clouds;
            case 2000, 2100 -> R.drawable.ic_launcher_foreground; // Replace with actual fog icon
            case 4000 -> R.drawable.ic_drizzle;
            case 4001, 4200, 4201 -> R.drawable.ic_rain;
            case 5000 -> R.drawable.ic_snow;
            case 5100 -> R.drawable.ic_snow; // Light snow
            case 5101 -> R.drawable.ic_rain; // Heavy snow
            case 6000, 6001, 6200, 6201 -> R.drawable.ic_rain; // Freezing rain
            case 8000 -> R.drawable.ic_thunderstorm;
            default -> R.drawable.ic_launcher_foreground; // Replace with a generic unknown icon
        };
    }

    /**
     * Maps the weather code to a description string.
     *
     * @param context the context to access resources
     * @param code the weather code to map
     * @return the description string for the weather code
     */
    @NonNull
    private static String mapWeatherCodeToDescription(Context context, int code) {
        return switch (code) {
            case 1000 -> context.getString(R.string.clear_sunny);
            case 1100 -> context.getString(R.string.mostly_clear);
            case 1101 -> context.getString(R.string.partly_cloudy);
            case 1102 -> context.getString(R.string.mostly_cloudy);
            case 1001 -> context.getString(R.string.cloudy);
            case 2000 -> context.getString(R.string.fog);
            case 2100 -> context.getString(R.string.light_fog);
            case 4000 -> context.getString(R.string.drizzle);
            case 4001 -> context.getString(R.string.rain);
            case 4200 -> context.getString(R.string.light_rain);
            case 4201 -> context.getString(R.string.heavy_rain);
            case 5000 -> context.getString(R.string.snow);
            case 5001 -> context.getString(R.string.flurries);
            case 5100 -> context.getString(R.string.light_snow);
            case 5101 -> context.getString(R.string.heavy_snow);
            case 6000 -> context.getString(R.string.freezing_drizzle);
            case 6001 -> context.getString(R.string.freezing_rain);
            case 6200 -> context.getString(R.string.light_freezing_rain);
            case 6201 -> context.getString(R.string.heavy_freezing_rain);
            case 7000 -> context.getString(R.string.ice_pellets);
            case 7101 -> context.getString(R.string.heavy_ice_pellets);
            case 7102 -> context.getString(R.string.light_ice_pellets);
            case 8000 -> context.getString(R.string.thunderstorm);
            default -> context.getString(R.string.unknown);
        };
    }
}

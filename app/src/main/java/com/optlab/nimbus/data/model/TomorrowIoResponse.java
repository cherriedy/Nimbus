package com.optlab.nimbus.data.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.optlab.nimbus.R;

import java.util.ArrayList;
import java.util.List;

public record TomorrowIoResponse(@SerializedName("data") Data data) {
    public record Values(
            @SerializedName("temperature") float temperature,
            @SerializedName("temperatureMax") float temperatureMax,
            @SerializedName("temperatureMin") float temperatureMin,
            @SerializedName("weatherCode") int weatherCode,
            @SerializedName("sunriseTime") String sunriseTime,
            @SerializedName("sunsetTime") String sunsetTime,
            @SerializedName("uvIndex") int uvIndex,
            @SerializedName("windSpeed") float windSpeed,
            @SerializedName("windDirection") int windDirection,
            @SerializedName("humidity") float humidity,
            @SerializedName("pressure") float pressureSurfaceLevel) {}

    public record Interval(
            @SerializedName("startTime") String startTime,
            @SerializedName("values") Values values) {}

    public record Data(@SerializedName("timelines") List<Timeline> timelines) {}

    public record Timeline(
            @SerializedName("timestep") String timeStep,
            @SerializedName("intervals") List<Interval> intervals) {}

    /**
     * Maps a TomorrowIoResponse to a list of WeatherResponse objects. Performs null and bounds
     * checks for safer operation.
     *
     * @param context Android context for resource access
     * @param response The TomorrowIoResponse to map
     * @return List of WeatherResponse objects
     */
    public static List<WeatherResponse> mapToResponses(TomorrowIoResponse response) {
        List<WeatherResponse> responses = new ArrayList<>();

        if (response.data() == null
                || response.data().timelines() == null
                || response.data().timelines().isEmpty()) {
            return responses;
        }
        Timeline timeline = response.data().timelines().get(0);
        if (timeline == null || timeline.intervals() == null || timeline.intervals().isEmpty()) {
            return responses;
        }
        for (Interval interval : timeline.intervals()) {
            if (interval == null || interval.values() == null) continue;
            Values values = interval.values();
            WeatherResponse weather = new WeatherResponse();
            weather.setDate(interval.startTime());
            weather.setTemperature(values.temperature());
            weather.setTemperatureMin(values.temperatureMin());
            weather.setTemperatureMax(values.temperatureMax());
            weather.setPressure(values.pressureSurfaceLevel());
            weather.setWindSpeed(values.windSpeed());
            weather.setHumidity(values.humidity());

            int weatherCode = values.weatherCode();
            weather.setWeatherCode(weatherCode);
            weather.setWeatherIcon(mapWeatherCodeToIcon(weatherCode));
            weather.setWeatherDescription(mapWeatherCodeToDescription(weatherCode));

            responses.add(weather);
        }
        return responses;
    }

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

    @DrawableRes
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
            default -> throw new IllegalStateException("Invalid weather code: " + code);
        };
    }
}

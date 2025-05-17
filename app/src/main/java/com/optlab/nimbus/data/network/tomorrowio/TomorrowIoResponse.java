package com.optlab.nimbus.data.network.tomorrowio;

import static com.optlab.nimbus.utility.Constants.*;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.optlab.nimbus.R;
import com.optlab.nimbus.data.model.Direction;
import com.optlab.nimbus.data.model.ForecastReport;
import com.optlab.nimbus.data.model.Pressure;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.data.model.Wind;
import com.optlab.nimbus.data.model.WindSpeed;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
            @SerializedName("pressure") float pressure) {
        public Values {
            if (temperature < MIN_VALID_CELSIUS || temperature > MAX_VALID_CELSIUS) {
                throw new IllegalArgumentException("Invalid temperature: " + temperature);
            }
            if (temperatureMax < MIN_VALID_CELSIUS || temperatureMax > MAX_VALID_CELSIUS) {
                throw new IllegalArgumentException("Invalid max temperature: " + temperatureMax);
            }
            if (temperatureMin < MIN_VALID_CELSIUS || temperatureMin > MAX_VALID_CELSIUS) {
                throw new IllegalArgumentException("Invalid min temperature: " + temperatureMin);
            }
        }
    }

    public record Interval(
            @SerializedName("startTime") String startTime,
            @SerializedName("values") Values values) {}

    public record Data(@SerializedName("timelines") List<Timeline> timelines) {}

    public record Timeline(
            @SerializedName("timestep") String timeStep,
            @SerializedName("intervals") List<Interval> intervals) {}

    public static List<ForecastReport> mapToReports(TomorrowIoResponse response) {
        List<ForecastReport> reports = new ArrayList<>();
        for (Timeline timeline : response.data.timelines) {
            for (Interval interval : timeline.intervals) {
                ZonedDateTime endTimeZdt =
                        ZonedDateTime.parse(interval.startTime, DateTimeFormatter.ISO_DATE_TIME);

                String endTime = endTimeZdt.toString();
                switch (timeline.timeStep) {
                    case "current" -> {
                        reports.add(0, mapIntervalToReport(interval, endTime));
                        continue;
                    }
                    case "1h" -> endTime = endTimeZdt.plusHours(1).toString();
                    case "1d" -> endTime = endTimeZdt.plusDays(1).toString();
                }

                reports.add(mapIntervalToReport(interval, endTime));
            }
        }
        return reports;
    }

    private static ForecastReport mapIntervalToReport(Interval interval, String endTime) {
        Values values = interval.values;
        Temperature temp = new Temperature(values.temperature, Temperature.Unit.CELSIUS);
        Temperature tempMax = new Temperature(values.temperatureMax, Temperature.Unit.CELSIUS);
        Temperature tempMin = new Temperature(values.temperatureMin, Temperature.Unit.CELSIUS);
        Pressure pressure = new Pressure(values.pressure, Pressure.Unit.HECTOPASCAL);
        Wind wind =
                new Wind(
                        new WindSpeed(values.windSpeed, WindSpeed.Unit.METERS_PER_SECOND),
                        new Direction(values.windDirection, Direction.Unit.DEGREE));
        return ForecastReport.builder()
                .startTime(interval.startTime)
                .endTime(endTime)
                .humidity(values.humidity)
                .sunsetTime(values.sunsetTime)
                .sunriseTime(values.sunsetTime)
                .uvIndex(values.uvIndex)
                .temperature(temp)
                .temperatureMin(tempMin)
                .weatherIcon(mapWeatherCodeToIcon(values.weatherCode))
                .weatherDescription(mapWeatherCodeToDescription(values.weatherCode))
                .temperatureMax(tempMax)
                .pressure(pressure)
                .wind(wind)
                .build();
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

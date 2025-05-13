package com.optlab.nimbus.utility;

import android.content.Context;

import com.optlab.nimbus.R;
import com.optlab.nimbus.data.preferences.UserPreferences;
import com.optlab.nimbus.data.preferences.UserPreferencesManager;

public class WeatherSummaryBuilder {
    private final Context context;
    private final UserPreferences userPreferences;

    public WeatherSummaryBuilder(Context context) {
        this.context = context;
        userPreferences = new UserPreferencesManager(context);
    }

    public String buildDailySummerSummary(
            double tempMax,
            double tempMin,
            double uvIndex,
            double humidity,
            double rainProbability,
            double windSpeed) {
        StringBuilder summary = new StringBuilder();

        summary.append(
                        context.getString(
                                R.string.expect_hot_conditions_with_temperatures_ranging_from))
                .append((int) tempMin)
                .append(userPreferences.getTemperatureUnit().getName())
                .append(context.getString(R.string.to))
                .append((int) tempMax)
                .append(userPreferences.getTemperatureUnit().getName())
                .append(". ");

        if (humidity > 60) {
            summary.append(context.getString(R.string.high_humidity_around))
                    .append((int) humidity)
                    .append(context.getString(R.string.will_make_it_feel_even_warmer));
        }

        if (uvIndex >= 8) {
            summary.append(context.getString(R.string.the_uv_index_will_be_very_high))
                    .append(uvIndex)
                    .append(context.getString(R.string.so_remember_to_use_sun_protection));
        }

        if (rainProbability > 20) {
            summary.append(context.getString(R.string.there_is_a))
                    .append((int) rainProbability)
                    .append(context.getString(R.string.chance_of_rain_so_keep_an_umbrella_handy));
        } else {
            summary.append(context.getString(R.string.the_chance_of_rain_is_low_at))
                    .append((int) rainProbability)
                    .append("%. ");
        }

        summary.append(context.getString(R.string.winds_will_be_around))
                .append((int) windSpeed)
                .append(userPreferences.getWindSpeedUnit().getName())
                .append(context.getString(R.string.offering_slight_relief_from_the_heat));

        return summary.toString();
    }
}

package com.optlab.nimbus.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import com.optlab.nimbus.R;
import com.optlab.nimbus.data.model.Pressure;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.data.model.WindSpeed;

public class WeatherSummary {
    private final Context context;
    private final Double tempMax;
    private final Double tempMin;
    private final Double uvIndex;
    private final Double humidity;
    private final Double windSpeed;
    private final Double rainProbability;
    private final Temperature.Unit temperatureUnit;
    private final WindSpeed.Unit windSpeedUnit;
    private final Pressure.Unit pressureUnit;

    private WeatherSummary(Builder builder) {
        this.context = builder.context;
        this.tempMax = builder.tempMax;
        this.tempMin = builder.tempMin;
        this.uvIndex = builder.uvIndex;
        this.humidity = builder.humidity;
        this.windSpeed = builder.windSpeed;
        this.rainProbability = builder.rainProbability;
        this.temperatureUnit = builder.temperatureUnit;
        this.windSpeedUnit = builder.windSpeedUnit;
        this.pressureUnit = builder.pressureUnit;
    }

    public static class Builder {
        private final Context context;
        private Double tempMax;
        private Double tempMin;
        private Double uvIndex;
        private Double humidity;
        private Double windSpeed;
        private Double rainProbability;
        private Temperature.Unit temperatureUnit;
        private WindSpeed.Unit windSpeedUnit;
        private Pressure.Unit pressureUnit;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder tempMax(Double tempMax) {
            this.tempMax = tempMax;
            return this;
        }

        public Builder tempMin(Double tempMin) {
            this.tempMin = tempMin;
            return this;
        }

        public Builder uvIndex(Double uvIndex) {
            this.uvIndex = uvIndex;
            return this;
        }

        public Builder humidity(Double humidity) {
            this.humidity = humidity;
            return this;
        }

        public Builder windSpeed(Double windSpeed) {
            this.windSpeed = windSpeed;
            return this;
        }

        public Builder rainProbability(Double rainProbability) {
            this.rainProbability = rainProbability;
            return this;
        }

        public Builder temperatureUnit(Temperature.Unit temperatureUnit) {
            this.temperatureUnit = temperatureUnit;
            return this;
        }

        public Builder windSpeedUnit(WindSpeed.Unit windSpeedUnit) {
            this.windSpeedUnit = windSpeedUnit;
            return this;
        }

        public Builder pressureUnit(Pressure.Unit pressureUnit) {
            this.pressureUnit = pressureUnit;
            return this;
        }

        public WeatherSummary build() {
            return new WeatherSummary(this);
        }
    }

    // @SuppressLint("DefaultLocale")
    // public String generate() {
    //     StringBuilder summary = new StringBuilder();
    //
    //     if (tempMax != null) {
    //         summary.append(
    //                         context.getString(
    //                                 R.string.expect_hot_conditions_with_temperatures_ranging_from))
    //                 .append(tempMax.intValue())
    //                 .append(temperatureUnit.getName())
    //                 .append(context.getString(R.string.to));
    //     }
    //
    //     if (tempMin != null) {
    //         summary.append(tempMin.intValue()).append(temperatureUnit.getName()).append(". ");
    //     }
    //
    //     if (humidity != null && humidity > 60) {
    //         summary.append(context.getString(R.string.high_humidity_around))
    //                 .append(humidity)
    //                 .append(context.getString(R.string.will_make_it_feel_even_warmer));
    //     }
    //
    //     if (uvIndex != null && uvIndex >= 8) {
    //         summary.append(context.getString(R.string.the_uv_index_will_be_very_high))
    //                 .append(uvIndex)
    //                 .append(context.getString(R.string.so_remember_to_use_sun_protection));
    //     }
    //
    //     if (rainProbability != null) {
    //         if (rainProbability > 20) {
    //             summary.append(context.getString(R.string.there_is_a))
    //                     .append(rainProbability.intValue())
    //                     .append(
    //                             context.getString(
    //                                     R.string.chance_of_rain_so_keep_an_umbrella_handy));
    //         } else {
    //             summary.append(context.getString(R.string.the_chance_of_rain_is_low_at))
    //                     .append(rainProbability.intValue())
    //                     .append("%. ");
    //         }
    //     }
    //
    //     if (windSpeed != null) {
    //         summary.append(context.getString(R.string.winds_will_be_around))
    //                 .append(String.format("%.1f", windSpeed))
    //                 .append(windSpeedUnit.getName())
    //                 .append(context.getString(R.string.offering_slight_relief_from_the_heat));
    //     }
    //
    //     return summary.toString();
    // }
}

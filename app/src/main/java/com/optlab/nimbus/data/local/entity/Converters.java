package com.optlab.nimbus.data.local.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.optlab.nimbus.data.model.ForecastProvider;
import com.optlab.nimbus.data.model.Coordinates;

public class Converters {
    @TypeConverter
    public static String fromWeatherProvider(@NonNull ForecastProvider provider) {
        return provider != null ? provider.name() : "";
    }

    @TypeConverter
    public static ForecastProvider toWeatherProvider(@NonNull String provider) {
        return !TextUtils.isEmpty(provider) ? ForecastProvider.valueOf(provider) : null;
    }

    @TypeConverter
    public static String fromCoordinates(@NonNull Coordinates coordinates) {
        return coordinates != null ? coordinates.toString() : "";
    }

    @TypeConverter
    public static Coordinates toCoordinates(@NonNull String coordinates) {
        return !TextUtils.isEmpty(coordinates) ? Coordinates.parse(coordinates) : null;
    }
}

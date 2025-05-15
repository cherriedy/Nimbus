package com.optlab.nimbus.data.local.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.optlab.nimbus.data.common.ForecastProvider;
import com.optlab.nimbus.data.model.Coordinates;

public class WeatherConverters {
    @TypeConverter
    public static String fromType(@NonNull ForecastEntity.Type type) {
        return type != null ? type.name() : "";
    }

    @TypeConverter
    public static ForecastEntity.Type toType(@NonNull String type) {
        return !TextUtils.isEmpty(type) ? ForecastEntity.Type.valueOf(type) : null;
    }

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
        return !TextUtils.isEmpty(coordinates) ? Coordinates.fromString(coordinates) : null;
    }
}

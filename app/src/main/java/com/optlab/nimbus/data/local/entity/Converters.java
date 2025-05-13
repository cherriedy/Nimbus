package com.optlab.nimbus.data.local.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.ProvidedTypeConverter;
import androidx.room.TypeConverter;

import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.model.common.Coordinates;

public class Converters {
    @TypeConverter
    public static String fromType(@NonNull WeatherEntity.Type type) {
        return type != null ? type.name() : "";
    }

    @TypeConverter
    public static WeatherEntity.Type toType(@NonNull String type) {
        return !TextUtils.isEmpty(type) ? WeatherEntity.Type.valueOf(type) : null;
    }

    @TypeConverter
    public static String fromWeatherProvider(@NonNull WeatherProvider provider) {
        return provider != null ? provider.name() : "";
    }

    @TypeConverter
    public static WeatherProvider toWeatherProvider(@NonNull String provider) {
        return !TextUtils.isEmpty(provider) ? WeatherProvider.valueOf(provider) : null;
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

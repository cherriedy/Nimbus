package com.optlab.nimbus.data.local.entity;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

public abstract class Converters {
    @TypeConverter
    public static String fromType(@NonNull WeatherEntity.Type type) {
        return type != null ? type.name() : "";
    }

    @TypeConverter
    public static WeatherEntity.Type toType(@NonNull String type) {
        return !TextUtils.isEmpty(type) ? WeatherEntity.Type.valueOf(type) : null;
    }
}

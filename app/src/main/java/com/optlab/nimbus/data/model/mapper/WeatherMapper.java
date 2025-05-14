package com.optlab.nimbus.data.model.mapper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;

import io.reactivex.rxjava3.annotations.NonNull;

import timber.log.Timber;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public final class WeatherMapper {
    private WeatherMapper() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    public static WeatherEntity mapToWeatherEntity(
            @NonNull List<WeatherResponse> response,
            @NonNull Coordinates coordinates,
            @NonNull WeatherEntity.Type type) {
        WeatherEntity entity = new WeatherEntity();
        entity.setCoordinates(coordinates);
        entity.setType(type);
        entity.setData(new Gson().toJson(response));
        entity.setProvider(WeatherProvider.TOMORROW_IO);
        entity.setTimestamp(System.currentTimeMillis());
        return entity;
    }

    public static List<WeatherResponse> mapFromWeatherEntity(WeatherEntity entity) {
        if (entity == null || entity.getData() == null) {
            Timber.e("WeatherEntity or its data is null");
            return Collections.emptyList();
        }
        try {
            Type listType = new TypeToken<List<WeatherResponse>>() {}.getType();
            return new Gson().fromJson(entity.getData(), listType);
        } catch (Exception e) {
            Timber.e("Failed to parse WeatherEntity data: %s", e.getMessage());
            return Collections.emptyList();
        }
    }
}

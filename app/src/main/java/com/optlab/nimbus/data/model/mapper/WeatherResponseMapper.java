package com.optlab.nimbus.data.model.mapper;

import com.google.gson.Gson;
import com.optlab.nimbus.data.common.WeatherProvider;
import com.optlab.nimbus.data.local.entity.WeatherEntity;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;

import io.reactivex.rxjava3.annotations.NonNull;

import java.util.List;

public final class WeatherResponseMapper {
    private WeatherResponseMapper() {
        // Private constructor to prevent instantiation
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated.");
    }

    public static WeatherEntity map(
            @NonNull List<WeatherResponse> response, @NonNull Coordinates coordinates) {
        WeatherEntity entity = new WeatherEntity();
        entity.setType(WeatherEntity.Type.DAILY);
        entity.setCoordinates(coordinates);
        entity.setData(new Gson().toJson(response));
        entity.setProvider(WeatherProvider.TOMORROW_IO);
        entity.setTimestamp(System.currentTimeMillis());
        return entity;
    }
}

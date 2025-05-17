package com.optlab.nimbus.data.repository.interfaces;

import com.optlab.nimbus.data.local.entity.ForecastEntity;
import com.optlab.nimbus.data.model.Coordinates;

import io.reactivex.rxjava3.core.Flowable;

public interface ForecastRepository {
    Flowable<ForecastEntity> getForecast(Coordinates coordinates);
}

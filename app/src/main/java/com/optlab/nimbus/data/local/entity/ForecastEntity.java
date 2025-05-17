package com.optlab.nimbus.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.optlab.nimbus.data.network.ResponseConstant;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.ForecastProvider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@Entity(tableName = "forecast_database")
@TypeConverters({Converters.class})
public class ForecastEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * Weather provider from which the data is fetched. This is used to determine which API to use.
     */
    private ForecastProvider provider;

    /**
     * Weather data in JSON format. This is the raw data fetched from the API. It is stored as a
     * string in the database.
     */
    private String data;

    /**
     * Coordinates of the location for which the weather data is fetched. This is used to determine
     * the location for which the weather data is fetched.
     */
    private Coordinates coordinates;

    /**
     * Timestamp of when the weather data was fetched. This is used to determine if the data is
     * expired or not.
     */
    private long timestamp;
}

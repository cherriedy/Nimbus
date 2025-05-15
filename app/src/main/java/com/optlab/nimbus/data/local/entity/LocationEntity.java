package com.optlab.nimbus.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity(tableName = "location_database")
public class LocationEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    /** Address of the location. To be used for displaying the location name in the UI. */
    private String address;

    /**
     * Coordinates of the location. This is used to determine the location for which the weather
     * data is fetched.
     */
    private double latitude;

    /**
     * Coordinates of the location. This is used to determine the location for which the weather
     * data is fetched.
     */
    private double longitude;

    /**
     * Indicates whether the location is the current location. This is used to determine if the
     * location is the current location or not.
     */
    private boolean isCurrent;

    /**
     * Last updated time of the location. This is used to determine if the location is updated or
     * not.
     */
    private long lastUpdated = System.currentTimeMillis();
}

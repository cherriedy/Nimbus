package com.optlab.nimbus.data.model;

import androidx.annotation.NonNull;

/**
 * UnifiedWeatherResponse is a record class that represents the weather data for a specific date.
 *
 * @param lat representing the latitude of the location
 * @param lon representing the longitude of the location
 */
public record Coordinates(String lat, String lon) {
    public Coordinates(double latitude, double longitude) {
        this(String.valueOf(latitude), String.valueOf(longitude));
    }

    public Double getDoubleLat() {
        return Double.valueOf(lat);
    }

    public Double getDoubleLon() {
        return Double.valueOf(lon);
    }

    /**
     * Converts a string representation of coordinates to a Coordinates object. The string should be
     * in the format "lat,lon".
     *
     * @param coordinates the string representation of coordinates
     * @return a Coordinates object
     */
    public static Coordinates fromString(String coordinates) {
        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid coordinates format");
        }
        return new Coordinates(parts[0], parts[1]);
    }

    public String getLocationParameter() {
        return lat + "," + lon;
    }

    @NonNull
    @Override
    public String toString() {
        return getLocationParameter();
    }
}

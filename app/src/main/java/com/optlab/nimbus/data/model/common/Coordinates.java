package com.optlab.nimbus.data.model.common;

/**
 * UnifiedWeatherResponse is a record class that represents the weather data for a specific date.
 *
 * @param lat representing the latitude of the location
 * @param lon representing the longitude of the location
 */
public record Coordinates(String lat, String lon) {
    public String getLocationParameter() {
        return lat + "," + lon;
    }
}

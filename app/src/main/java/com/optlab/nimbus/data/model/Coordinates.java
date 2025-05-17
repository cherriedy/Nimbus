package com.optlab.nimbus.data.model;

import androidx.annotation.NonNull;

/**
 * Coordinates class represents a geographical location using latitude and longitude.
 *
 * @param lat representing the latitude of the location
 * @param lon representing the longitude of the location
 */
public record Coordinates(double lat, double lon) {
    public Coordinates {
        if (lat < -90 || lat > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (lon < -180 || lon > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
    }

    public static Coordinates parse(String coordinates) {
        String[] parts = coordinates.split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid coordinates format");
        }
        try {
            double latitude = Double.parseDouble(parts[0].trim());
            double longitude = Double.parseDouble(parts[1].trim());
            return new Coordinates(latitude, longitude);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Coordinates must be valid numbers", e);
        }
    }

    public String getCoordinates() {
        return lat + "," + lon;
    }

    @NonNull
    @Override
    public String toString() {
        return getCoordinates();
    }
}

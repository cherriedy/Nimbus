package com.optlab.nimbus.data.model;

import androidx.annotation.NonNull;

/**
 * Coordinates is a record class that represents a latitude/longitude pair.
 *
 * @param lat representing the latitude of the location
 * @param lon representing the longitude of the location
 */
public record Coordinates(double lat, double lon) {
    public Coordinates {
        if (!isValid(lat, lon)) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90, Longitude must be between -180 and 180");
        }
    }

    public static boolean isValid(double lat, double lon) {
        return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
    }

    public boolean isValid() {
        return isValid(lat, lon);
    }

    public static Coordinates parse(String coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            throw new IllegalArgumentException("Coordinates string cannot be null or empty");
        }
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

    public String getCoordinates(int precision) {
        String format = "%1$." + precision + "f,%2$." + precision + "f";
        return String.format(format, lat, lon);
    }

    /** Calculates the Haversine distance (in meters) between this and another Coordinates. */
    public double distanceTo(Coordinates other) {
        final int R = 6371000; // Earth radius in meters
        double lat1Rad = Math.toRadians(this.lat);
        double lat2Rad = Math.toRadians(other.lat);
        double deltaLat = Math.toRadians(other.lat - this.lat);
        double deltaLon = Math.toRadians(other.lon - this.lon);
        double a =
                Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                        + Math.cos(lat1Rad)
                                * Math.cos(lat2Rad)
                                * Math.sin(deltaLon / 2)
                                * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @NonNull
    @Override
    public String toString() {
        return getCoordinates();
    }
}

package com.optlab.nimbus.data.network.openstreetmap;

import com.google.gson.annotations.SerializedName;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.Place;

import java.util.stream.Stream;

public record OsmPlaceResponse(
        @SerializedName("place_id") int placeId,
        @SerializedName("lat") double lat,
        @SerializedName("lon") double lon,
        @SerializedName("display_name") String displayName,
        @SerializedName("address") Address address) {

    public record Address(
            @SerializedName("suburb") String suburb,
            @SerializedName("quarter") String quarter,
            @SerializedName("city") String city,
            @SerializedName("county") String county,
            @SerializedName("state") String state,
            @SerializedName("country") String country) {

        public String[] getComponents() {
            return Stream.of(suburb, quarter, city, county, state, country)
                    .filter(component -> component != null && !component.isEmpty())
                    .toArray(String[]::new);
        }

        public String joinedComponents() {
            return String.join(",", getComponents());
        }
    }

    public Place toPlace() {
        return Place.builder()
                .coordinates(new Coordinates(lat, lon))
                .name(address.joinedComponents())
                .details(displayName)
                .build();
    }
}

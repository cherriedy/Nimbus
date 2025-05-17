package com.optlab.nimbus.data.model;

import lombok.Builder;

public record Place(String name, String details, Coordinates coordinates) {
    @Builder
    public Place {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (details == null || details.isEmpty()) {
            throw new IllegalArgumentException("Details cannot be null or empty");
        }
    }
}

package com.optlab.nimbus.data.repository;

import com.optlab.nimbus.data.local.entity.LocationEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.openstreetmap.AddressResponse;

import java.util.List;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface LocationRepository {
    Maybe<List<LocationEntity>> getLocations();

    void setLocation(LocationEntity location);

    void setLocation(Coordinates coordinates, String address, boolean isCurrent);

    void deleteLocation(LocationEntity location);

    Maybe<LocationEntity> getCurrentLocation();

    Single<AddressResponse> getLocationAddress(Coordinates coordinates);
}

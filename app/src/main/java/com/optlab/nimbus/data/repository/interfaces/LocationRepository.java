package com.optlab.nimbus.data.repository.interfaces;

import com.optlab.nimbus.data.local.entity.LocationEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.network.openstreetmap.OsmPlaceResponse;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface LocationRepository {
    Maybe<List<LocationEntity>> getLocations();

    void setLocation(LocationEntity location);

    void setLocation(Coordinates coordinates, String address, boolean isCurrent);

    void deleteLocation(LocationEntity location);

    Maybe<LocationEntity> getCurrentLocation();

    Single<OsmPlaceResponse> getLocationAddress(Coordinates coordinates);
}

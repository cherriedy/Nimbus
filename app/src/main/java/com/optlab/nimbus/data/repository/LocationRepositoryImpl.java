package com.optlab.nimbus.data.repository;

import com.optlab.nimbus.data.local.dao.LocationDao;
import com.optlab.nimbus.data.local.entity.LocationEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.network.openstreetmap.OpenStreetMapRetrofitClient;
import com.optlab.nimbus.data.network.openstreetmap.OsmPlaceResponse;
import com.optlab.nimbus.data.repository.interfaces.LocationRepository;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

import java.util.List;

import javax.inject.Inject;

public class LocationRepositoryImpl implements LocationRepository {
    private final LocationDao locationDao;
    private final OpenStreetMapRetrofitClient openStreetMapClient;

    @Inject
    public LocationRepositoryImpl(
            LocationDao locationDao, OpenStreetMapRetrofitClient openStreetMapClient) {
        this.locationDao = locationDao;
        this.openStreetMapClient = openStreetMapClient;
    }

    @Override
    public Maybe<List<LocationEntity>> getLocations() {
        return locationDao.getLocations();
    }

    @Override
    @SuppressWarnings({"ResultOfMethodCallIgnored", "CheckResult"})
    public void setLocation(LocationEntity location) {
        locationDao
                .insert(location)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onInsertSuccess, this::onInsertFail);
    }

    @Override
    public void setLocation(Coordinates coordinates, String address, boolean isCurrent) {
        LocationEntity entity = new LocationEntity();
        entity.setLongitude(coordinates.lon());
        entity.setLatitude(coordinates.lat());
        entity.setCurrent(isCurrent);
        entity.setAddress(address);
        setLocation(entity);
    }

    private void onInsertFail(Throwable throwable) {
        if (throwable instanceof Exception) {
            Timber.e(throwable, "Failed to insert location: %s", throwable.getMessage());
        } else {
            Timber.e(throwable, "Failed to insert location: Unknown error occurred");
        }
    }

    private void onInsertSuccess() {
        Timber.d("Location inserted successfully");
    }

    @Override
    public void deleteLocation(LocationEntity location) {
        locationDao.delete(location);
    }

    @Override
    public Maybe<LocationEntity> getCurrentLocation() {
        return locationDao.getCurrent();
    }

    @Override
    public Single<OsmPlaceResponse> getLocationAddress(Coordinates coordinates) {
        return openStreetMapClient
                .getOpenStreetMapService()
                .reverseGeocode(coordinates.lat(), coordinates.lon(), "json");
    }
}

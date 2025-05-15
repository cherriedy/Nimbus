package com.optlab.nimbus.data.repository;

import android.annotation.SuppressLint;

import com.optlab.nimbus.data.local.dao.LocationDao;
import com.optlab.nimbus.data.local.entity.LocationEntity;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.openstreetmap.AddressResponse;
import com.optlab.nimbus.data.network.openstreetmap.OpenStreetMapClient;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class LocationRepositoryImpl implements LocationRepository {
    private final LocationDao locationDao;
    private final OpenStreetMapClient openStreetMapClient;

    @Inject
    public LocationRepositoryImpl(
            LocationDao locationDao, OpenStreetMapClient openStreetMapClient) {
        this.locationDao = locationDao;
        this.openStreetMapClient = openStreetMapClient;
    }

    @Override
    public Maybe<List<LocationEntity>> getLocations() {
        return locationDao.getLocations();
    }

    @Override
    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void setLocation(LocationEntity location) {
        locationDao
                .insert(location)
                .subscribeOn(Schedulers.io())
                .subscribe(this::onInsertSuccess, this::onInsertFail);
    }

    @Override
    @SuppressLint("CheckResult")
    public void setLocation(Coordinates coordinates, String address, boolean isCurrent) {
        LocationEntity entity = new LocationEntity();
        entity.setLongitude(coordinates.getDoubleLon());
        entity.setLatitude(coordinates.getDoubleLat());
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
    @SuppressLint("CheckResult")
    public Single<AddressResponse> getLocationAddress(Coordinates coordinates) {
        return openStreetMapClient
                .getOpenStreetMapService()
                .reverseGeocode(coordinates.getDoubleLat(), coordinates.getDoubleLon(), "json");
    }
}

package com.optlab.nimbus.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.optlab.nimbus.data.local.entity.LocationEntity;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM location_database")
    Maybe<List<LocationEntity>> getLocations();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(LocationEntity location);

    @Delete
    Completable delete(LocationEntity location);

    @Query("SELECT * FROM LOCATION_DATABASE WHERE isCurrent = 1 LIMIT 1")
    Maybe<LocationEntity> getCurrent();
}

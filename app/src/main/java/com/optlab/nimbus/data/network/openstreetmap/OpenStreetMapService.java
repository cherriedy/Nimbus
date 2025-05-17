package com.optlab.nimbus.data.network.openstreetmap;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenStreetMapService {
    @GET("reverse")
    Single<OsmPlaceResponse> reverseGeocode(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("format") String format);
}

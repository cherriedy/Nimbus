package com.optlab.nimbus.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.optlab.nimbus.data.model.Coordinates;

public class LocationProvider implements DefaultLifecycleObserver {
    /**
     * Callback interface to be implemented by the client to receive location updates and errors. This
     * interface provides methods to handle location results, errors, and permission denial.
     */
    public interface Callback {
        void onLocationResult(Coordinates coordinates);

        void onLocationError(Exception e);

        void onPermissionDenied();
    }

    private final Context context;

    /**
     * FusedLocationProviderClient is the main entry point for interacting with the Fused Location
     * Provider. It provides access to location services and allows you to request location updates
     * and get the last known location.
     */
    private final FusedLocationProviderClient fusedLocationClient;

    /**
     * ActivityResultLauncher is a class that provides a way to launch an activity for a result using
     * the new Activity Result API. It is used to request permissions in a more modern way.
     */
    private final ActivityResultLauncher<String> permissionLauncher;

    private Callback callback;
    private boolean requestOnResume = false;

    @RequiresPermission(
            allOf = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            })
    public LocationProvider(@NonNull ActivityResultCaller caller, @NonNull Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.permissionLauncher =
                caller.registerForActivityResult(
                        new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (Boolean.TRUE.equals(isGranted)) {
                                fetchLocationInternal();
                            } else if (callback != null) {
                                callback.onPermissionDenied();
                            }
                        });
    }

    public void requestLocation(Callback callback) {
        this.callback = callback;
        this.requestOnResume = true;
    }

    /**
     * onResume is called when the lifecycle owner is resumed.
     *
     * <p>This method checks if the requestOnResume flag is set. If it is, it checks if the location
     * permission is granted. If the permission is granted, it fetches the location. If not, it
     * launches the permission request.
     *
     * @param owner The lifecycle owner that is being observed.
     */
    @RequiresPermission(
            allOf = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            })
    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        if (!requestOnResume) return;
        requestOnResume = false;
        if (hasLocationPermission()) {
            fetchLocationInternal();
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Checks if the location permission is granted. It checks both fine and coarse location
     * permissions.
     *
     * <p>
     *
     * <ul>
     *   <li>ACCESS_FINE_LOCATION: Allows an app to access precise location from location sources such
     *       as GPS.
     *   <li>ACCESS_COARSE_LOCATION: Allows an app to access approximate location from location
     *       sources such as Wi-Fi and cell towers.
     * </ul>
     */
    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * fetchLocationInternal is a private method that fetches the last known location using the
     * FusedLocationProviderClient. It checks if the location permission is granted before attempting
     * to fetch the location. If the location is successfully fetched, it calls the onLocationResult
     * method of the callback with the coordinates. If there is an error, it calls the onLocationError
     * method of the callback with the exception.
     */
    @RequiresPermission(
            allOf = {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            })
    private void fetchLocationInternal() {
        if (!hasLocationPermission()) return;
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(
                        location -> {
                            if (location != null && callback != null) {
                                callback.onLocationResult(
                                        new Coordinates(location.getLatitude(), location.getLongitude()));
                            } else if (callback != null) {
                                callback.onLocationError(new Exception("Location is null"));
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            if (callback != null) callback.onLocationError(e);
                        });
    }
}

package com.optlab.nimbus.ui.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.optlab.nimbus.R;
import com.optlab.nimbus.data.model.common.Coordinates;
import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.databinding.FragmentHomeBinding;
import com.optlab.nimbus.ui.adapter.HourlyForecastAdapater;
import com.optlab.nimbus.ui.decoration.LinearSpacingStrategy;
import com.optlab.nimbus.ui.decoration.SpacingItemDecoration;
import com.optlab.nimbus.ui.decoration.SpacingStrategy;
import com.optlab.nimbus.ui.viewmodel.CurrentForecastViewModel;
import com.optlab.nimbus.utility.WeatherSummary;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

@AndroidEntryPoint
public class CurrentForecastFragment extends Fragment {
    private FragmentHomeBinding binding;
    private CurrentForecastViewModel viewModel;
    private HourlyForecastAdapater adapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Inject protected SettingPreferences settingPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        initViewModel();
        initAdapter();
        requestLocationPermission();
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Timber.d("Location permission granted");
            fetchLocation();
        } else {
            Timber.d("Location permission not granted, requesting permission");
            ActivityResultLauncher<String> requestPermissionLauncher =
                    registerForActivityResult(
                            new ActivityResultContracts.RequestPermission(),
                            isGranted -> {
                                if (Boolean.TRUE.equals(isGranted)) {
                                    Timber.d("Location permission granted");
                                    fetchLocation();
                                } else {
                                    Timber.d("Location permission denied");
                                }
                            });
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @RequiresPermission(
            allOf = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            })
    private void fetchLocation() {
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(
                        location -> {
                            if (location != null) {
                                Timber.d(
                                        "Location: %f, %f",
                                        location.getLatitude(), location.getLongitude());
                                Coordinates coordinates =
                                        new Coordinates(
                                                String.valueOf(location.getLatitude()),
                                                String.valueOf(location.getLongitude()));
                                settingPreferences.setLocation(coordinates);
                                viewModel.fetchCurrent(coordinates);
                                viewModel.fetchHourly(coordinates);
                            }
                        })
                .addOnFailureListener(e -> Timber.e("Error fetching location: %s", e.getMessage()));
    }

    private void initAdapter() {
        adapter = new HourlyForecastAdapater(settingPreferences);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(CurrentForecastViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        observeViewModel();
        fetchAndDisplayLocation();

        binding.included.mtb.setNavigationOnClickListener(
                v -> binding.dl.openDrawer(GravityCompat.START));
    }

    private void fetchAndDisplayLocation() {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            Coordinates coordinates = settingPreferences.getLocation(0);

            List<Address> addresses =
                    geocoder.getFromLocation(
                            Double.parseDouble(coordinates.lat()),
                            Double.parseDouble(coordinates.lon()),
                            1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                binding.included.mtb.setSubtitle(address.getLocality());
            } else {
                Timber.d("No address found for the given coordinates");
            }
        } catch (IOException e) {
            Timber.e("Error fetching address: %s", e.getMessage());
        } catch (NullPointerException e) {
            Timber.e("There is no location saved");
        }
    }

    private void observeViewModel() {
        viewModel.getHourly().observe(getViewLifecycleOwner(), adapter::submitList);
        viewModel
                .getCurrent()
                .observe(
                        getViewLifecycleOwner(),
                        current -> {
                            WeatherResponse response = current.get(0);
                            binding.included.tvSummary.setText(
                                    new WeatherSummary.Builder(requireContext(), settingPreferences)
                                            .humidity(response.getHumidity())
                                            .windSpeed(response.getWindSpeed())
                                            .build()
                                            .generate()
                            );
                        });
    }

    private void initRecyclerView() {
        binding.included.rvHourlyWeather.setAdapter(adapter);
        binding.included.rvHourlyWeather.setHasFixedSize(true);

        SpacingStrategy spacingStrategy = new LinearSpacingStrategy(requireContext(), 8, false);
        binding.included.rvHourlyWeather.addItemDecoration(
                new SpacingItemDecoration(spacingStrategy));
    }

    public void onMoreTextClick(@NonNull View view) {
        Navigation.findNavController(view).navigate(R.id.weaklyForecastFragment);
    }
}

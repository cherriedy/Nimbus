package com.optlab.nimbus.ui.view;

import android.Manifest;
import android.content.pm.PackageManager;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.optlab.nimbus.R;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.preferences.UserPreferencesManager;
import com.optlab.nimbus.databinding.FragmentMainDashboardBinding;
import com.optlab.nimbus.ui.adapter.HourlyForecastAdapater;
import com.optlab.nimbus.ui.decoration.LinearSpacingStrategy;
import com.optlab.nimbus.ui.decoration.SpacingItemDecoration;
import com.optlab.nimbus.ui.decoration.SpacingStrategy;
import com.optlab.nimbus.ui.viewmodel.HomeViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

import javax.inject.Inject;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    @Inject protected UserPreferencesManager userPrefs;

    private FragmentMainDashboardBinding binding;
    private HomeViewModel viewModel;
    private HourlyForecastAdapater adapter;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        initViewModel();
        initAdapter();
        requestLocationPermission();
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
                                Coordinates coordinates =
                                        new Coordinates(
                                                location.getLatitude(), location.getLongitude());
                                userPrefs.setLocation(coordinates);
                                viewModel.fetchCurrentWeatherByLocation(coordinates);
                                viewModel.fetchHourlyWeathersByLocation(coordinates);
                            }
                        })
                .addOnFailureListener(e -> Timber.e("Error fetching location: %s", e.getMessage()));
    }

    private void initAdapter() {
        adapter = new HourlyForecastAdapater(userPrefs);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainDashboardBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.setUserPrefs(userPrefs);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getHourly().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    private void initRecyclerView() {
        binding.rvHourlyWeather.setAdapter(adapter);
        binding.rvHourlyWeather.setHasFixedSize(true);

        SpacingStrategy spacingStrategy = new LinearSpacingStrategy(requireContext(), 8, false);
        binding.rvHourlyWeather.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
    }

    public void onMoreTextClick(@NonNull View view) {
        Navigation.findNavController(view).navigate(R.id.dailyWeatherFragment);
    }
}

package com.optlab.nimbus.ui.view.forecast;

import static kotlinx.coroutines.flow.FlowKt.observeOn;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;
import com.optlab.nimbus.R;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.forecast.ForecastResponse;
import com.optlab.nimbus.data.model.openstreetmap.AddressResponse;
import com.optlab.nimbus.data.repository.LocationRepository;
import com.optlab.nimbus.data.repository.PreferencesRepository;
import com.optlab.nimbus.databinding.FragmentHomeBinding;
import com.optlab.nimbus.ui.adapter.HourlyForecastAdapter;
import com.optlab.nimbus.ui.adapter.LocationsAdapter;
import com.optlab.nimbus.ui.decoration.LinearSpacingStrategy;
import com.optlab.nimbus.ui.decoration.SpacingItemDecoration;
import com.optlab.nimbus.ui.viewmodel.CurrentForecastViewModel;
import com.optlab.nimbus.utility.WeatherSummary;

import dagger.hilt.android.AndroidEntryPoint;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import javax.inject.Inject;

@AndroidEntryPoint
public class CurrentForecastFragment extends Fragment {
    @Inject protected PreferencesRepository preferencesRepository;
    @Inject protected LocationRepository locationRepository;

    private FragmentHomeBinding binding;
    private CurrentForecastViewModel viewModel;
    private HourlyForecastAdapter hourlyForecastAdapter;
    private FusedLocationProviderClient fusedLocationClient;
    private View navigationViewHeader;
    private LocationsAdapter locationsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        initViewModel();
        initAdapters();
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

    @SuppressLint("CheckResult")
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
                                Timber.d("Location fetched: %s", location.toString());

                                Coordinates coordinates =
                                        new Coordinates(
                                                location.getLatitude(), location.getLongitude());

                                Single<AddressResponse> addressResponse =
                                        locationRepository.getLocationAddress(coordinates);
                                addressResponse
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(
                                                address -> {
                                                    Timber.d(
                                                            "Address fetched: %s",
                                                            address.toString());
                                                    String[] components =
                                                            address.getAddress().getComponents();
                                                    String displayName =
                                                            components[1] + ", " + components[2];
                                                    locationRepository.setLocation(
                                                            coordinates, displayName, true);
                                                    viewModel.fetchForecast(coordinates);
                                                    binding.included.mtb.setSubtitle(displayName);
                                                },
                                                e -> {
                                                    Timber.e(
                                                            "Error fetching address: %s",
                                                            e.getMessage());
                                                });
                            }
                        })
                .addOnFailureListener(e -> Timber.e("Error fetching location: %s", e.getMessage()));
    }

    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void initAdapters() {
        hourlyForecastAdapter = new HourlyForecastAdapter();
        hourlyForecastAdapter.setTemperatureUnit(
                preferencesRepository.getTemperatureUnit().blockingFirst());

        locationsAdapter = new LocationsAdapter();
        locationRepository
                .getLocations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        locationsAdapter::submitList,
                        e -> {
                            Timber.e("Error fetching locations: %s", e.getMessage());
                            locationsAdapter.submitList(null);
                        });
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(CurrentForecastViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navigationViewHeader = binding.nv.getHeaderView(0);
        binding.included.mtb.setNavigationOnClickListener(
                v -> binding.dl.openDrawer(GravityCompat.START));
        initRecyclerView();
        setupSettingsNavigation();
        observeViewModels();
        fetchAndDisplayLocation();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupSettingsNavigation() {
        TextInputEditText tieSearch = navigationViewHeader.findViewById(R.id.tie_search);
        tieSearch.setOnTouchListener(
                (v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        Drawable drawableEnd = tieSearch.getCompoundDrawables()[2];
                        if (drawableEnd != null) {
                            int drawableWidth = drawableEnd.getBounds().width();
                            if (event.getRawX()
                                    >= (tieSearch.getRight()
                                            - drawableWidth
                                            - tieSearch.getPaddingEnd())) {
                                Navigation.findNavController(v).navigate(R.id.settingFragment);
                                return true;
                            }
                        }
                    }
                    return false;
                });
    }

    private void fetchAndDisplayLocation() {}

    private void observeViewModels() {
        viewModel.getHourly().observe(getViewLifecycleOwner(), hourlyForecastAdapter::submitList);
        viewModel
                .getCurrent()
                .observe(
                        getViewLifecycleOwner(),
                        current -> {
                            ForecastResponse response = current.get(0);
                            binding.included.tvSummary.setText(
                                    new WeatherSummary.Builder(requireContext())
                                            .tempMax(response.getTemperatureMax())
                                            .tempMin(response.getTemperatureMin())
                                            .humidity(response.getHumidity())
                                            .windSpeed(response.getWindSpeed())
                                            .temperatureUnit(
                                                    preferencesRepository
                                                            .getTemperatureUnit()
                                                            .blockingFirst())
                                            .windSpeedUnit(
                                                    preferencesRepository
                                                            .getWindSpeedUnit()
                                                            .blockingFirst())
                                            .pressureUnit(
                                                    preferencesRepository
                                                            .getPressureUnit()
                                                            .blockingFirst())
                                            .build()
                                            .generate());
                        });
    }

    private void initRecyclerView() {
        setupHourlyWeatherRecyclerView();
        setupLocationsRecyclerView();
    }

    private void setupLocationsRecyclerView() {
        RecyclerView rvLocations = navigationViewHeader.findViewById(R.id.rv_locations);
        rvLocations.setAdapter(locationsAdapter);

        rvLocations.addItemDecoration(
                new SpacingItemDecoration(new LinearSpacingStrategy(requireContext(), 8, false)));
    }

    private void setupHourlyWeatherRecyclerView() {
        binding.included.rvHourlyWeather.setAdapter(hourlyForecastAdapter);
        binding.included.rvHourlyWeather.setHasFixedSize(true);

        binding.included.rvHourlyWeather.addItemDecoration(
                new SpacingItemDecoration(new LinearSpacingStrategy(requireContext(), 8, false)));
    }

    public void onMoreTextClick(@NonNull View view) {
        Navigation.findNavController(view).navigate(R.id.weaklyForecastFragment);
    }
}

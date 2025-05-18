package com.optlab.nimbus.ui.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.optlab.nimbus.R;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.preferences.UserPreferencesManager;
import com.optlab.nimbus.databinding.FragmentMainDashboardBinding;
import com.optlab.nimbus.ui.adapter.HourlyForecastAdapater;
import com.optlab.nimbus.ui.decoration.LinearSpacingStrategy;
import com.optlab.nimbus.ui.decoration.SpacingItemDecoration;
import com.optlab.nimbus.ui.decoration.SpacingStrategy;
import com.optlab.nimbus.ui.viewmodel.HomeViewModel;
import com.optlab.nimbus.utility.LocationProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class HomeFragment extends Fragment {
    @Inject
    protected UserPreferencesManager userPrefs;

    private FragmentMainDashboardBinding binding;
    private HomeViewModel viewModel;
    private HourlyForecastAdapater adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initAdapter();
        initLocationServices();
    }

    private void initLocationServices() {
        @SuppressLint("MissingPermission")
        LocationProvider locationProvider = new LocationProvider(this, requireContext());
        getLifecycle().addObserver(locationProvider);
        locationProvider.requestLocation(
                new LocationProvider.Callback() {
                    @Override
                    public void onLocationResult(Coordinates coordinates) {
                        userPrefs.setLocation(coordinates);
                        viewModel.fetchCurrentWeatherByLocation(coordinates);
                        viewModel.fetchHourlyWeathersByLocation(coordinates);
                    }

                    @Override
                    public void onLocationError(Exception e) {
                        Timber.e("Error fetching location: %s", e.getMessage());
                    }

                    @Override
                    public void onPermissionDenied() {
                        Timber.d("Location permission denied");
                    }
                });
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

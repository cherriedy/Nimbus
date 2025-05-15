package com.optlab.nimbus.ui.view.forecast;

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
import com.optlab.nimbus.data.repository.LocationRepository;
import com.optlab.nimbus.data.repository.PreferencesRepository;
import com.optlab.nimbus.databinding.FragmentWeaklyForecastBinding;
import com.optlab.nimbus.ui.adapter.WeaklyForecastAdapter;
import com.optlab.nimbus.ui.decoration.LinearSpacingStrategy;
import com.optlab.nimbus.ui.decoration.SpacingItemDecoration;
import com.optlab.nimbus.ui.viewmodel.WeaklyForecastViewModel;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

import javax.inject.Inject;

@AndroidEntryPoint
public class WeaklyForecastFragment extends Fragment {
    @Inject protected PreferencesRepository preferencesRepository;
    @Inject protected LocationRepository locationRepository;

    private FragmentWeaklyForecastBinding binding;
    private WeaklyForecastViewModel viewModel;
    private WeaklyForecastAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initAdapter();
    }

    private void initAdapter() {
        adapter = new WeaklyForecastAdapter();
        adapter.setTemperatureUnit(preferencesRepository.getTemperatureUnit().blockingFirst());
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(WeaklyForecastViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeaklyForecastBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setViewModel(viewModel);
        return binding.getRoot();
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        initRecyclerView();
        observeViewModel();

        locationRepository
                .getCurrentLocation()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        entity -> {
                            viewModel.fetchDaily(
                                    new Coordinates(entity.getLatitude(), entity.getLongitude()));
                        });
    }

    private void setupToolbar() {
        binding.tb.setNavigationIcon(R.drawable.ic_back);
        binding.tb.setNavigationOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void initRecyclerView() {
        binding.rvDailyWeather.setHasFixedSize(true);
        binding.rvDailyWeather.setAdapter(adapter);
        LinearSpacingStrategy strategy = new LinearSpacingStrategy(requireContext(), 8, false);
        binding.rvDailyWeather.addItemDecoration(new SpacingItemDecoration(strategy));
    }

    private void observeViewModel() {
        viewModel.getWeakly().observe(getViewLifecycleOwner(), adapter::submitList);
    }
}

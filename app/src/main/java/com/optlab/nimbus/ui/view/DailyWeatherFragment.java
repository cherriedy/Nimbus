package com.optlab.nimbus.ui.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.optlab.nimbus.R;
import com.optlab.nimbus.data.preferences.UserPreferencesManager;
import com.optlab.nimbus.databinding.FragmentDailyWeatherBinding;
import com.optlab.nimbus.ui.adapter.DailyForecastAdapter;
import com.optlab.nimbus.ui.decoration.LinearSpacingStrategy;
import com.optlab.nimbus.ui.decoration.SpacingItemDecoration;
import com.optlab.nimbus.ui.viewmodel.DailyWeatherViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DailyWeatherFragment extends Fragment {
    private FragmentDailyWeatherBinding binding;
    private DailyWeatherViewModel viewModel;
    private DailyForecastAdapter adapter;

    @Inject protected UserPreferencesManager userPrefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initAdapter();
    }

    private void initAdapter() {
        adapter = new DailyForecastAdapter(userPrefs);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(DailyWeatherViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDailyWeatherBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.setUserPrefs(userPrefs);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        observeViewModel();
        viewModel.fetchDailyWeatherByLocation(userPrefs.getLocation(0));
        binding.tb.setNavigationIcon(R.drawable.ic_back);
        binding.tb.setNavigationOnClickListener(
                v -> Navigation.findNavController(view).navigateUp());
    }

    private void initRecyclerView() {
        binding.rvDailyWeather.setHasFixedSize(true);
        binding.rvDailyWeather.setAdapter(adapter);
        LinearSpacingStrategy strategy = new LinearSpacingStrategy(requireContext(), 8, false);
        binding.rvDailyWeather.addItemDecoration(new SpacingItemDecoration(strategy));
    }

    private void observeViewModel() {
        viewModel.getDaily().observe(getViewLifecycleOwner(), adapter::submitList);
    }
}

package com.optlab.nimbus.ui.view;

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
import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.databinding.FragmentWeaklyForecastBinding;
import com.optlab.nimbus.ui.adapter.DailyForecastAdapter;
import com.optlab.nimbus.ui.decoration.LinearSpacingStrategy;
import com.optlab.nimbus.ui.decoration.SpacingItemDecoration;
import com.optlab.nimbus.ui.viewmodel.WeaklyForecastViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import javax.inject.Inject;

@AndroidEntryPoint
public class WeaklyForecastFragment extends Fragment {
    private FragmentWeaklyForecastBinding binding;
    private WeaklyForecastViewModel viewModel;
    private DailyForecastAdapter adapter;

    @Inject protected SettingPreferences settingPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        initAdapter();
    }

    private void initAdapter() {
        adapter = new DailyForecastAdapter(settingPreferences);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        observeViewModel();
        viewModel.fetchDaily(settingPreferences.getLocation(0));
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
        viewModel.getWeakly().observe(getViewLifecycleOwner(), adapter::submitList);
    }
}

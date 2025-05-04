package com.optlab.nimbus.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.preferences.UserPrefsManager;
import com.optlab.nimbus.databinding.LayoutItemHourlyWeatherBinding;

public class HourlyForecastAdapater
        extends ListAdapter<WeatherResponse, HourlyForecastAdapater.ViewHolder> {
    private static final DiffUtil.ItemCallback<WeatherResponse> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull WeatherResponse oldItem,
                        @NonNull WeatherResponse newItem) {
                    return oldItem.getDate().equals(newItem.getDate());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull WeatherResponse oldItem,
                        @NonNull WeatherResponse newItem) {
                    return oldItem.equals(newItem);
                }
            };

    protected final UserPrefsManager userPrefs;

    public HourlyForecastAdapater(UserPrefsManager userPrefs) {
        super(CALL_BACK);
        this.userPrefs = userPrefs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemHourlyWeatherBinding binding =
                LayoutItemHourlyWeatherBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        binding.setUserPrefs(userPrefs);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutItemHourlyWeatherBinding binding;

        public ViewHolder(@NonNull LayoutItemHourlyWeatherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull WeatherResponse response) {
            binding.setResponse(response);
            binding.executePendingBindings();
        }
    }
}

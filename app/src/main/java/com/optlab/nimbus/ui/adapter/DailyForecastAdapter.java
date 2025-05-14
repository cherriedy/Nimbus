package com.optlab.nimbus.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.nimbus.data.model.common.WeatherResponse;
import com.optlab.nimbus.data.preferences.SettingPreferences;
import com.optlab.nimbus.databinding.LayoutItemWeaklyForecastBinding;

public class DailyForecastAdapter
        extends ListAdapter<WeatherResponse, DailyForecastAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<WeatherResponse> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull WeatherResponse oldItem, @NonNull WeatherResponse newItem) {
                    return oldItem.getDate().equals(newItem.getDate());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull WeatherResponse oldItem, @NonNull WeatherResponse newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private final SettingPreferences userPrefs;

    public DailyForecastAdapter(@NonNull SettingPreferences userPrefs) {
        super(CALL_BACK);
        this.userPrefs = userPrefs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemWeaklyForecastBinding binding =
                LayoutItemWeaklyForecastBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        binding.setUserPrefs(userPrefs);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutItemWeaklyForecastBinding binding;

        public ViewHolder(@NonNull LayoutItemWeaklyForecastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull WeatherResponse response) {
            binding.setResponse(response);
            binding.executePendingBindings();
        }
    }
}

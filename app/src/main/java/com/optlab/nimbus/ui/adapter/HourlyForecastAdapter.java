package com.optlab.nimbus.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.nimbus.data.model.Forecast;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.databinding.LayoutItemHourlyForecastBinding;

import lombok.Setter;

@Setter
public class HourlyForecastAdapter
        extends ListAdapter<Forecast.HourlyReport, HourlyForecastAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<Forecast.HourlyReport> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Forecast.HourlyReport oldItem,
                        @NonNull Forecast.HourlyReport newItem) {
                    return oldItem.getStartTime().equals(newItem.getStartTime());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Forecast.HourlyReport oldItem,
                        @NonNull Forecast.HourlyReport newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private Temperature.Unit temperatureUnit;

    public HourlyForecastAdapter() {
        super(CALL_BACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemHourlyForecastBinding binding =
                LayoutItemHourlyForecastBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        binding.setTemperatureUnit(temperatureUnit);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutItemHourlyForecastBinding binding;

        public ViewHolder(@NonNull LayoutItemHourlyForecastBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull Forecast.HourlyReport hourly) {
            binding.setHourly(hourly);
            binding.executePendingBindings();
        }
    }
}

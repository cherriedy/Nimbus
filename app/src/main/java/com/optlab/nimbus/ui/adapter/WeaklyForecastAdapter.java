package com.optlab.nimbus.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.nimbus.data.model.Forecast;
import com.optlab.nimbus.data.model.Temperature;
import com.optlab.nimbus.databinding.LayoutItemWeaklyForecastBinding;

import lombok.Setter;

@Setter
public class WeaklyForecastAdapter
        extends ListAdapter<Forecast.Day, WeaklyForecastAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<Forecast.Day> CALL_BACK =
            new DiffUtil.ItemCallback<Forecast.Day>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Forecast.Day oldItem, @NonNull Forecast.Day newItem) {
                    return oldItem.getStartTime().equals(newItem.getStartTime());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Forecast.Day oldItem, @NonNull Forecast.Day newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private Temperature.Unit temperatureUnit;

    public WeaklyForecastAdapter() {
        super(CALL_BACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemWeaklyForecastBinding binding =
                LayoutItemWeaklyForecastBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        binding.setTemperatureUnit(temperatureUnit);
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

        public void bind(@NonNull Forecast.Day day) {
            binding.setDay(day);
            binding.executePendingBindings();
        }
    }
}

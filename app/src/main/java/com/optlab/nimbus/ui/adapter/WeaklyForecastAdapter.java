package com.optlab.nimbus.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.nimbus.data.common.TemperatureUnit;
import com.optlab.nimbus.data.model.forecast.ForecastResponse;
import com.optlab.nimbus.databinding.LayoutItemWeaklyForecastBinding;

import lombok.Setter;

public class WeaklyForecastAdapter
        extends ListAdapter<ForecastResponse, WeaklyForecastAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<ForecastResponse> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull ForecastResponse oldItem, @NonNull ForecastResponse newItem) {
                    return oldItem.getDate().equals(newItem.getDate());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull ForecastResponse oldItem, @NonNull ForecastResponse newItem) {
                    return oldItem.equals(newItem);
                }
            };

    @Setter private TemperatureUnit temperatureUnit;

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

        public void bind(@NonNull ForecastResponse response) {
            binding.setResponse(response);
            binding.executePendingBindings();
        }
    }
}

package com.optlab.nimbus.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.nimbus.data.model.common.UnifiedWeatherResponse;
import com.optlab.nimbus.databinding.LayoutItemDailyWeatherBinding;

public class DailyForecastAdapter
        extends ListAdapter<UnifiedWeatherResponse, DailyForecastAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<UnifiedWeatherResponse> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull UnifiedWeatherResponse oldItem,
                        @NonNull UnifiedWeatherResponse newItem) {
                    return oldItem.getDate().equals(newItem.getDate());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull UnifiedWeatherResponse oldItem,
                        @NonNull UnifiedWeatherResponse newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public DailyForecastAdapter() {
        super(CALL_BACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemDailyWeatherBinding binding =
                LayoutItemDailyWeatherBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutItemDailyWeatherBinding binding;

        public ViewHolder(@NonNull LayoutItemDailyWeatherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(@NonNull UnifiedWeatherResponse response) {
            binding.setResponse(response);
            binding.executePendingBindings();
        }
    }
}

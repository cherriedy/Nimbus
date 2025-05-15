package com.optlab.nimbus.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.nimbus.data.local.entity.LocationEntity;
import com.optlab.nimbus.databinding.LayoutItemLocationBinding;

public class LocationsAdapter extends ListAdapter<LocationEntity, LocationsAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<LocationEntity> CALL_BACK =
            new DiffUtil.ItemCallback<LocationEntity>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull LocationEntity oldItem, @NonNull LocationEntity newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull LocationEntity oldItem, @NonNull LocationEntity newItem) {
                    return oldItem.equals(newItem);
                }
            };

    public LocationsAdapter() {
        super(CALL_BACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutItemLocationBinding binding =
                LayoutItemLocationBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationEntity locationEntity = getItem(position);
        try {
            String name = locationEntity.getAddress();
            String cityName = name.split(" ,")[0];
            holder.binding.tvCityName.setText(cityName);
            holder.binding.tvCountryName.setText(name);
        } catch (NullPointerException e) {
            holder.binding.tvCityName.setText("");
            holder.binding.tvCountryName.setText("");
        }
        holder.binding.ivLocation.setVisibility(
                locationEntity.isCurrent() ? View.VISIBLE : View.GONE);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final LayoutItemLocationBinding binding;

        public ViewHolder(LayoutItemLocationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}

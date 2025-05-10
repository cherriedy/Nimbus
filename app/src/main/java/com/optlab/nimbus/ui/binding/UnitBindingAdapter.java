package com.optlab.nimbus.ui.binding;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.optlab.nimbus.R;
import com.optlab.nimbus.data.model.common.PressureUnit;
import com.optlab.nimbus.data.model.common.TemperatureUnit;
import com.optlab.nimbus.data.model.common.WindSpeedUnit;
import com.optlab.nimbus.utility.DateTimeUtil;
import com.optlab.nimbus.utility.convertor.PressureConvertor;
import com.optlab.nimbus.utility.convertor.TemperatureConvertor;
import com.optlab.nimbus.utility.convertor.WindSpeedConvertor;

import java.time.format.TextStyle;
import java.util.Locale;

/**
 * BindingAdapter class for setting temperature, pressure, wind speed, humidity, and day formatted
 * text in the UI.
 */
public class UnitBindingAdapter {
    /**
     * Sets the temperature in the specified unit.
     *
     * @param view the TextView to set the temperature on
     * @param temperature the temperature value in Celsius
     * @param unit the unit to convert the temperature to
     */
    @BindingAdapter(value = {"temperature", "unit"})
    public static void setTemperature(
            @NonNull TextView view, double temperature, @NonNull TemperatureUnit unit) {
        int roundedTemperature = TemperatureConvertor.fromCelsius(temperature, unit);
        view.setText(String.format(Locale.ENGLISH, "%d%s", roundedTemperature, unit.getName()));
    }

    /**
     * Sets the pressure in the specified unit.
     *
     * @param view the TextView to set the pressure on
     * @param pressure the pressure value in hectopascal (hPa)
     * @param unit the unit to convert the pressure to
     */
    @BindingAdapter(value = {"pressure", "unit"})
    public static void setPressure(
            @NonNull TextView view, double pressure, @NonNull PressureUnit unit) {
        if (unit != PressureUnit.HECTOPASCAL) {
            pressure = PressureConvertor.fromHectopascal(pressure, unit);
        }
        view.setText(String.format(Locale.ENGLISH, "%.1f %s", pressure, unit.getName()));
    }

    /**
     * Sets the wind speed in the specified unit.
     *
     * @param view the TextView to set the wind speed on
     * @param speed the wind speed value in meters per second (m/s)
     * @param unit the unit to convert the wind speed to
     */
    @BindingAdapter(value = {"wind_speed", "unit"})
    public static void setWindSpeed(
            @NonNull TextView view, double speed, @NonNull WindSpeedUnit unit) {
        if (unit != WindSpeedUnit.METERS_PER_SECOND) {
            speed = WindSpeedConvertor.fromMeterPerSecond(speed, unit);
        }
        view.setText(String.format(Locale.ENGLISH, "%.1f %s", speed, unit.getName()));
    }

    /**
     * Sets the humidity value in percentage.
     *
     * @param view the TextView to set the humidity on
     * @param humidity the humidity value in percentage
     */
    @BindingAdapter("humidity")
    public static void setHumidity(@NonNull TextView view, double humidity) {
        view.setText(String.format(Locale.ENGLISH, "%.1f%%", humidity));
    }

    /**
     * Sets the day of the week based on the provided date string.
     *
     * @param view the TextView to set the day on
     * @param date the date string
     */
    @BindingAdapter(value = {"day", "style"})
    public static void setDay(
            @NonNull TextView view, @NonNull String date, @NonNull TextStyle style) {
        view.setText(DateTimeUtil.getDayOfWeek(date, style));
    }

    /**
     * Sets the day and time based on the provided date string.
     *
     * @param view the TextView to set the day and time on
     * @param date the date string
     */
    @BindingAdapter("day_time")
    public static void setDayTime(@NonNull TextView view, @NonNull String date) {
        if (TextUtils.isEmpty(date)) {
            return;
        }
        view.setText(DateTimeUtil.getDayTime(date));
    }

    /**
     * Sets the weather icon based on the provided weather code.
     *
     * @param view the ImageView to set the icon on
     * @param icon the resource ID of the weather icon
     */
    @BindingAdapter("weather_icon")
    public static void setWeatherIcon(@NonNull ImageView view, int icon) {
        if (icon == 0) {
            return;
        }
        Glide.with(view.getContext()).load(icon).centerCrop().into(view);
    }

    /**
     * Sets the hour based on the provided date time string.
     *
     * @param view the TextView to set the hour on
     * @param dateTime the date time string
     */
    @BindingAdapter("hour")
    public static void setHour(@NonNull TextView view, @NonNull String dateTime) {
        view.setText(DateTimeUtil.getHours(dateTime));
    }

    /**
     * Sets the updated time based on the provided date time string.
     *
     * @param view the TextView to set the updated time on
     * @param dateTime the date time string
     */
    @SuppressLint("SetTextI18n")
    @BindingAdapter("updated_at")
    public static void setUpdatedAt(@NonNull TextView view, @NonNull String dateTime) {
        String updatedAt = view.getContext().getString(R.string.updated_at);
        view.setText(updatedAt + ": " + DateTimeUtil.getDayTime(dateTime));
    }
}

package com.optlab.nimbus.data.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.optlab.nimbus.data.model.PressureUnit;
import com.optlab.nimbus.data.model.TemperatureUnit;
import com.optlab.nimbus.data.model.WindSpeedUnit;

import org.junit.Before;
import org.junit.Test;

public class UserPreferencesManagerTest {
    private SharedPreferences mockSharedPreferences;
    private SharedPreferences.Editor mockEditor;
    private UserPreferencesManager unitUserPreferencesManager;

    @Before
    public void setUp() {
        Context mockContext = mock(Context.class); // Mock the Context
        mockSharedPreferences = mock(SharedPreferences.class); // Mock SharedPreferences
        mockEditor = mock(SharedPreferences.Editor.class); // Mock SharedPreferences.Editor

        // thenReturn() is used to specify what the mock should return when a method is called
        // on it. In this case, when getSharedPreferences() is called on the mockContext, it should
        // return the mockSharedPreferences.
        when(mockContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
                .thenReturn(mockSharedPreferences);

        // when() is used to specify what the mock should return when a method is called on it.
        // In this case, when edit() is called on the mockSharedPreferences, it should return the
        // mockEditor.
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        // when() is used to specify what the mock should return when a method is called on it.
        // In this case, when putString() is called on the mockEditor, it should return the
        // mockEditor again. This is useful for method chaining.
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);

        unitUserPreferencesManager = new UserPreferencesManager(mockContext);
    }

    @Test
    public void setUnit_valid_key_unit() {
        unitUserPreferencesManager.setUnit(UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.CELSIUS);

        // Verify that the putString method was called on the mockEditor with the correct key and
        // value. The key should be UserPrefsManager.TEMPERATURE_UNIT and the value should be
        // TemperatureUnit.CELSIUS.name().
        verify(mockEditor)
                .putString(UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name());

        verify(mockEditor).apply(); // Verify that apply() was called on the mockEditor.
    }

    @Test(expected = NullPointerException.class)
    public void setUnit_null_key() {
        unitUserPreferencesManager.setUnit(null, TemperatureUnit.CELSIUS);

        verify(mockEditor).putString(null, TemperatureUnit.CELSIUS.name());
        verify(mockEditor).apply();
    }

    @Test(expected = NullPointerException.class)
    public void setUnit_null_unit() {
        unitUserPreferencesManager.setUnit(UserPreferencesManager.PRESSURE_UNIT, null);
    }

    @Test(expected = NullPointerException.class)
    public void setUnit_empty_key() {
        unitUserPreferencesManager.setUnit(null, WindSpeedUnit.KNOTS);
    }

    @Test(expected = NullPointerException.class)
    public void setUnit_whitespace_key() {
        unitUserPreferencesManager.setUnit(" ", PressureUnit.BAR);
    }

    @Test
    public void getUnit_valid_key_enum_default() {
        when(mockSharedPreferences.getString(
                        UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name()))
                .thenReturn(TemperatureUnit.FAHRENHEIT.name());

        Enum result = unitUserPreferencesManager.getUnit(UserPreferencesManager.TEMPERATURE_UNIT);

        assertEquals(TemperatureUnit.FAHRENHEIT, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUnit_nonexistent_key() {
        unitUserPreferencesManager.getUnit("non_exists");
    }

    @Test(expected = NullPointerException.class)
    public void getUnit_null_key() {
        unitUserPreferencesManager.getUnit(null);
    }

    @Test(expected = NullPointerException.class)
    public void getUnit_empty_key() {
        unitUserPreferencesManager.getUnit("");
    }

    @Test(expected = NullPointerException.class)
    public void getUnit_whitespace_key() {
        unitUserPreferencesManager.getUnit(" ");
    }

    /** Test if a value set by setUnit can be retrieved correctly by getUnit using same key. */
    @Test
    public void getUnit_setUnit_consistency() {
        when(mockSharedPreferences.getString(
                        UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.CELSIUS.name()))
                .thenReturn(TemperatureUnit.KELVIN.name());

        unitUserPreferencesManager.setUnit(UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.KELVIN);
        Enum result = unitUserPreferencesManager.getUnit(UserPreferencesManager.TEMPERATURE_UNIT);
        assertEquals(TemperatureUnit.KELVIN, result);
    }

    @Test
    public void setUnit_overwrite_test() {
        // Test if a new value set by setUnit can overwrite the old value.
    }

    @Test
    public void setUnit_valid_key_wind_speed() {
        // Verify that setUnit correctly stores a valid WindSpeedUnit using the WIND_SPEED_UNIT key.
        // TODO implement test
    }

    @Test
    public void setUnit_valid_key_pressure() {
        // Verify that setUnit correctly stores a valid PressureUnit using the PRESSURE_UNIT key.
        // TODO implement test
    }

    @Test
    public void getUnit_wind_speed_default() {
        // Verify that getUnit returns the default WindSpeedUnit (METERS_PER_SECOND) when no value
        // has been previously set for WIND_SPEED_UNIT.
        // TODO implement test
    }

    @Test
    public void getUnit_pressure_default() {
        // Verify that getUnit returns the default PressureUnit (HECTOPASCAL) when no value
        // has been previously set for PRESSURE_UNIT.
        // TODO implement test
    }

    @Test
    public void getUnit_set_and_get_wind_speed_consistency() {
        // Verify that a WindSpeedUnit set by setUnit can be correctly retrieved by getUnit using
        // the
        // same key (WIND_SPEED_UNIT).
        // TODO implement test
    }

    @Test
    public void getUnit_set_and_get_pressure_consistency() {
        // Verify that a PressureUnit set by setUnit can be correctly retrieved by getUnit using the
        // same key (PRESSURE_UNIT).
        // TODO implement test
    }

    @Test
    public void setUnit_invalid_key() {
        // Verify that setUnit throws an IllegalArgumentException when an invalid key is provided.
        // TODO implement test
    }

    @Test
    public void getUnit_invalid_key() {
        // Verify that getUnit throws an IllegalArgumentException when an invalid key is provided.
        // TODO implement test
    }

    @Test
    public void setUnit_and_get_cross_check_different_keys() {
        // Test if we setUnit with one key, getUnit with a different key should not work.
        // Ensure that each unit enum is stored and retrieved independently.
        // TODO implement test
    }

    @Test
    public void getUnit_unexpected_value() {
        // Test if there is an unexpected key is added that will be caught by the switch case.
        // TODO implement test
    }

    @Test
    public void setUnit_with_various_valid_inputs() {
        // Test all valid enums for all valid keys, including all values for the
        // TemperatureUnit, WindSpeedUnit, and PressureUnit
        // TODO implement test
    }

    @Test
    public void getUnit_after_deleting_shared_preference() {
        // Test getUnit after clearing all the shared preference to assert they return the default
        // value.
        // TODO implement test
    }
}

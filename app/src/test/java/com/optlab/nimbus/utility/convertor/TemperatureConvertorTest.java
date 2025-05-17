package com.optlab.nimbus.utility.convertor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.optlab.nimbus.data.model.TemperatureUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

// | Scenario                                      | Input Temperature | Input Unit     | Expected Output | Notes                                      |
// |-----------------------------------------------|------------------|---------------|----------------|--------------------------------------------|
// | Convert 0°C to Fahrenheit                     | 0.0              | FAHRENHEIT    | -18            | (0-32)\*5/9 ≈ -18                          |
// | Convert 100°C to Fahrenheit                   | 100.0            | FAHRENHEIT    | 38             | (100-32)\*5/9 ≈ 38                         |
// | Convert -40°C to Fahrenheit                   | -40.0            | FAHRENHEIT    | -40            | (-40-32)\*5/9 = -40                        |
// | Convert 0°C to Kelvin                         | 0.0              | KELVIN        | -273           | 0-273.15 ≈ -273                            |
// | Convert 100°C to Kelvin                       | 100.0            | KELVIN        | -173           | 100-273.15 ≈ -173                          |
// | Convert -273.15°C to Kelvin (absolute zero)   | -273.15          | KELVIN        | -546           | -273.15-273.15 ≈ -546                      |
// | Convert large negative Celsius to Kelvin      | -1e6             | KELVIN        | -1000273       | -1e6-273.15 ≈ -1,000,273                   |
// | Convert +Infinity to Kelvin                   | +Infinity        | KELVIN        | +Infinity      | Edge case                                  |
// | Convert 25°C to Celsius                       | 25.0             | CELSIUS       | 25             | Identity conversion                        |
// | Convert 100°C to Celsius                      | 100.0            | CELSIUS       | 100            | Identity conversion                        |
// | Convert -40°C to Celsius                      | -40.0            | CELSIUS       | -40            | Identity conversion                        |
// | Convert -Infinity to Celsius                  | -Infinity        | CELSIUS       | -Infinity      | Edge case                                  |
// | Convert NaN to Fahrenheit                     | NaN              | FAHRENHEIT    | NaN            | Should handle gracefully                   |
// | Convert large positive Celsius to Fahrenheit  | 1e6              | FAHRENHEIT    | 555444         | (1e6-32)\*5/9 ≈ 555,444                    |


/**
 * This class contains unit tests for the TemperatureConvertor class, specifically for the
 * fromCelsius method.
 *
 * <p>The tests cover various scenarios, including conversions from Celsius to Fahrenheit, Kelvin,
 * and Celsius itself. The tests also include edge cases such as NaN, positive and negative
 * infinity, and large positive and negative values.
 */
@RunWith(Parameterized.class)
public class TemperatureConvertorTest {
    @Parameterized.Parameters(name = "{index}: fromCelsius({0}, {1}) = {2}")
    public static List<Object[]> data() {
        return List.of(new Object[][]{
                {0.0, TemperatureUnit.FAHRENHEIT, -17},
                {100.0, TemperatureUnit.FAHRENHEIT, 37},
                {-40.0, TemperatureUnit.FAHRENHEIT, -40},

                {0.0, TemperatureUnit.KELVIN, -273},
                {100.0, TemperatureUnit.KELVIN, -173},
                {-273.15, TemperatureUnit.KELVIN, -546},

                {-1e6, TemperatureUnit.KELVIN, -1000273},
                {Double.POSITIVE_INFINITY, TemperatureUnit.KELVIN, Integer.MAX_VALUE},
                {25.0, TemperatureUnit.CELSIUS, 25},

                {100.0, TemperatureUnit.CELSIUS, 100},
                {-40.0, TemperatureUnit.CELSIUS, -40},
                {Double.NEGATIVE_INFINITY, TemperatureUnit.CELSIUS, Integer.MIN_VALUE},

                {Double.NaN, TemperatureUnit.FAHRENHEIT, 0},
                {1e6, TemperatureUnit.FAHRENHEIT, 555537}
        });
    }

    @Parameterized.Parameter(0)
    public double inputTemperature;

    @Parameterized.Parameter(1)
    public TemperatureUnit unit;

    @Parameterized.Parameter(2)
    public int expectedOutput;

    @Test
    public void testFromCelsius() {
        int result = TemperatureConvertor.fromCelsius(inputTemperature, unit);
        if (Double.isNaN(expectedOutput)) {
            assertTrue(Double.isNaN(result));
        } else if (Double.isInfinite(expectedOutput)) {
            assertTrue(Double.isInfinite(result));
        } else {
            assertEquals(expectedOutput, result, 0.0001);
        }
    }
}

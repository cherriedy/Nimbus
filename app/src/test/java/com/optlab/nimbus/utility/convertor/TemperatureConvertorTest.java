package com.optlab.nimbus.utility.convertor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.optlab.nimbus.data.model.TemperatureUnit;
import com.optlab.nimbus.data.model.WindSpeedUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;



import java.util.List;

//   | Scenario                                     | Input Temperature | Input Unit | Expected Output | Notes                                                       |
//   | -------------------------------------------- | ----------------- | ---------- | --------------- | ----------------------------------------------------------- |
//   | Convert 0°C to Fahrenheit                    | 0.0               | FAHRENHEIT | 32.0            | (0 × 9/5) + 32 = 32                                         |
//   | Convert 100°C to Fahrenheit                  | 100.0             | FAHRENHEIT | 212.0           | (100 × 9/5) + 32 = 212                                      |
//   | Convert -40°C to Fahrenheit                  | -40.0             | FAHRENHEIT | -40.0           | (-40 × 9/5) + 32 = -40                                      |
//   | Convert 0°C to Kelvin                        | 0.0               | KELVIN     | 273.15          | 0 + 273.15 = 273.15                                         |
//   | Convert 100°C to Kelvin                      | 100.0             | KELVIN     | 373.15          | 100 + 273.15 = 373.15                                       |
//   | Convert -273.15°C to Kelvin (absolute zero)  | -273.15           | KELVIN     | 0.0             | -273.15 + 273.15 = 0.0                                      |
//   | Convert large negative Celsius to Kelvin     | -1e6              | KELVIN     | -999726.85      | -1e6 + 273.15 = -999726.85 (physically invalid, test logic) |
//   | Convert +Infinity to Kelvin                  | +Infinity         | KELVIN     | +Infinity       | Mathematical edge case                                      |
//   | Convert 25°C to Celsius                      | 25.0              | CELSIUS    | 25.0            | Identity conversion                                         |
//   | Convert 100°C to Celsius                     | 100.0             | CELSIUS    | 100.0           | Identity conversion                                         |
//   | Convert -40°C to Celsius                     | -40.0             | CELSIUS    | -40.0           | Identity conversion                                         |
//   | Convert -Infinity to Celsius                 | -Infinity         | CELSIUS    | -Infinity       | Edge case                                                   |
//   | Convert NaN to Fahrenheit                    | NaN               | FAHRENHEIT | NaN             | Should handle gracefully                                    |
//   | Convert large positive Celsius to Fahrenheit | 1e6               | FAHRENHEIT | 1800032.0       | (1e6 × 9/5) + 32 = 1,800,032                                |


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

    private static final double DELTA = 1e-6; // Độ sai số cho phép khi so sánh số thực

    @Parameterized.Parameter(0)
    public double inputTemp;

    @Parameterized.Parameter(1)
    public TemperatureUnit targetUnit;

    @Parameterized.Parameter(2)
    public double expectedOutput;

    @Parameterized.Parameters(name = "{index}: {0}°C to {1} = {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {0.0, TemperatureUnit.FAHRENHEIT, 32.0},
                {100.0, TemperatureUnit.FAHRENHEIT, 212.0},
                {-40.0, TemperatureUnit.FAHRENHEIT, -40.0},
                {0.0, TemperatureUnit.KELVIN, 273.15},
                {100.0, TemperatureUnit.KELVIN, 373.15},
                {-273.15, TemperatureUnit.KELVIN, 0.0},
                {-1e6, TemperatureUnit.KELVIN, -999726.85},
                {Double.POSITIVE_INFINITY, TemperatureUnit.KELVIN, Double.POSITIVE_INFINITY},
                {25.0, TemperatureUnit.CELSIUS, 25.0},
                {100.0, TemperatureUnit.CELSIUS, 100.0},
                {-40.0, TemperatureUnit.CELSIUS, -40.0},
                {Double.NEGATIVE_INFINITY, TemperatureUnit.CELSIUS, Double.NEGATIVE_INFINITY},
                {Double.NaN, TemperatureUnit.FAHRENHEIT, Double.NaN},
                {1e6, TemperatureUnit.FAHRENHEIT, 1800032.0}
        });
    }

    @Test
    public void testTemperatureConversion() {
        double result = TemperatureConvertor.fromCelsius(inputTemp, targetUnit);
        if (Double.isNaN(expectedOutput)) {
            assertTrue("Expected NaN", Double.isNaN(result));
        } else if (Double.isInfinite(expectedOutput)) {
            assertEquals("Expected infinity", expectedOutput, result, 0.0);
        } else {
            assertEquals(expectedOutput, result, DELTA);
        }
    }
}

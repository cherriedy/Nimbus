package com.optlab.nimbus.utility.convertor;

import static org.junit.Assert.assertEquals;

import com.optlab.nimbus.data.model.common.TemperatureUnit;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

// | Scenario                                                      | Input Temperature | Input Unit     | Method         | Expected Output         | Notes                                 |
// |---------------------------------------------------------------|------------------|---------------|----------------|------------------------|---------------------------------------|
// | Convert 0°C to Fahrenheit                                     | 0.0              | FAHRENHEIT    | fromCelsius    | -18                    | (0-32)\*5/9 = -17.78 ≈ -18            |
// | Convert 100°C to Fahrenheit                                   | 100.0            | FAHRENHEIT    | fromCelsius    | 38                     | (100-32)\*5/9 = 37.78 ≈ 38            |
// | Convert -40°C to Fahrenheit                                   | -40.0            | FAHRENHEIT    | fromCelsius    | -40                    | (-40-32)\*5/9 = -40                   |
// | Convert 0°C to Kelvin                                         | 0.0              | KELVIN        | fromCelsius    | -273                   | 0-273.15 = -273.15 ≈ -273             |
// | Convert 100°C to Kelvin                                       | 100.0            | KELVIN        | fromCelsius    | -173                   | 100-273.15 = -173.15 ≈ -173           |
// | Convert -273.15°C to Kelvin (absolute zero)                   | -273.15          | KELVIN        | fromCelsius    | -546                   | -273.15-273.15 = -546.3 ≈ -546        |
// | Convert 25°C to Celsius                                       | 25.0             | CELSIUS       | fromCelsius    | 25                     | Identity conversion                   |
// | Convert NaN to Fahrenheit                                     | NaN              | FAHRENHEIT    | fromCelsius    | NaN                    | Should handle gracefully              |
// | Convert +Infinity to Kelvin                                   | +Infinity        | KELVIN        | fromCelsius    | +Infinity              | Edge case                             |
// | Convert -Infinity to Celsius                                  | -Infinity        | CELSIUS       | fromCelsius    | -Infinity              | Edge case                             |
// | Convert large positive Celsius to Fahrenheit                  | 1e6              | FAHRENHEIT    | fromCelsius    | 555444                  | (1e6-32)\*5/9 ≈ 555,444               |
// | Convert large negative Celsius to Kelvin                      | -1e6             | KELVIN        | fromCelsius    | -1003273               | -1e6-273.15 ≈ -1,000,273              |

/**
 * This class contains unit tests for the TemperatureConvertor class, specifically for the
 * fromCelsius method.
 *
 * <p>The tests cover various scenarios, including conversions from Celsius to Fahrenheit, Kelvin,
 * and Celsius itself. The tests also include edge cases such as NaN, positive and negative
 * infinity, and large positive and negative values.
 *
 * <table>
 * <thead>
 * <tr>
 * <th>Scenario</th>
 * <th>Input Temperature</th>
 * <th>Input Unit</th>
 * <th>Method</th>
 * <th>Expected Output</th>
 * <th>Notes</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>Convert 0&#176;C to Fahrenheit</td>
 * <td>0.0</td>
 * <td>FAHRENHEIT</td>
 * <td>fromCelsius</td>
 * <td>-18</td>
 * <td>(0-32)*5/9 = -17.78 &asymp; -18</td>
 * </tr>
 * <tr>
 * <td>Convert 100&#176;C to Fahrenheit</td>
 * <td>100.0</td>
 * <td>FAHRENHEIT</td>
 * <td>fromCelsius</td>
 * <td>38</td>
 * <td>(100-32)*5/9 = 37.78 &asymp; 38</td>
 * </tr>
 * <tr>
 * <td>Convert -40&#176;C to Fahrenheit</td>
 * <td>-40.0</td>
 * <td>FAHRENHEIT</td>
 * <td>fromCelsius</td>
 * <td>-40</td>
 * <td>(-40-32)*5/9 = -40</td>
 * </tr>
 * <tr>
 * <td>Convert 0&#176;C to Kelvin</td>
 * <td>0.0</td>
 * <td>KELVIN</td>
 * <td>fromCelsius</td>
 * <td>-273</td>
 * <td>0-273.15 = -273.15 &asymp; -273</td>
 * </tr>
 * <tr>
 * <td>Convert 100&#176;C to Kelvin</td>
 * <td>100.0</td>
 * <td>KELVIN</td>
 * <td>fromCelsius</td>
 * <td>-173</td>
 * <td>100-273.15 = -173.15 &asymp; -173</td>
 * </tr>
 * <tr>
 * <td>Convert -273.15&#176;C to Kelvin (absolute zero)</td>
 * <td>-273.15</td>
 * <td>KELVIN</td>
 * <td>fromCelsius</td>
 * <td>-546</td>
 * <td>-273.15-273.15 = -546.3 &asymp; -546</td>
 * </tr>
 * <tr>
 * <td>Convert 25&#176;C to Celsius</td>
 * <td>25.0</td>
 * <td>CELSIUS</td>
 * <td>fromCelsius</td>
 * <td>25</td>
 * <td>Identity conversion</td>
 * </tr>
 * <tr>
 * <td>Convert NaN to Fahrenheit</td>
 * <td>NaN</td>
 * <td>FAHRENHEIT</td>
 * <td>fromCelsius</td>
 * <td>NaN</td>
 * <td>Should handle gracefully</td>
 * </tr>
 * <tr>
 * <td>Convert +Infinity to Kelvin</td>
 * <td>+Infinity</td>
 * <td>KELVIN</td>
 * <td>fromCelsius</td>
 * <td>+Infinity</td>
 * <td>Edge case</td>
 * </tr>
 * <tr>
 * <td>Convert -Infinity to Celsius</td>
 * <td>-Infinity</td>
 * <td>CELSIUS</td>
 * <td>fromCelsius</td>
 * <td>-Infinity</td>
 * <td>Edge case</td>
 * </tr>
 * <tr>
 * <td>Convert large positive Celsius to Fahrenheit</td>
 * <td>1e6</td>
 * <td>FAHRENHEIT</td>
 * <td>fromCelsius</td>
 * <td>555444</td>
 * <td>(1e6-32)*5/9 &asymp; 555,444</td>
 * </tr>
 * <tr>
 * <td>Convert large negative Celsius to Kelvin</td>
 * <td>-1e6</td>
 * <td>KELVIN</td>
 * <td>fromCelsius</td>
 * <td>-1003273</td>
 * <td>-1e6-273.15 &asymp; -1,000,273</td>
 * </tr>
 * </tbody>
 * </table>
 */
@RunWith(Enclosed.class)
public class TemperatureConvertorTest {
    @RunWith(Parameterized.class)
    public static class FromCelsiusToFahrenheitParameterizedTest {
        /**
         * Test data for the parameterized test.
         *
         * <p>The test data is provided as a 2D array of objects, where each row represents a test
         * case. The first column is the initial temperature in Celsius, the second column is the
         * target unit (Fahrenheit), and the third column is the expected temperature in Fahrenheit.
         */
        @Parameterized.Parameters(name = "{index}: fromCelsius({0}, {1}) = {2}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {0.0, TemperatureUnit.FAHRENHEIT, -18},
                        {100.0, TemperatureUnit.FAHRENHEIT, 38},
                        {-40.0, TemperatureUnit.FAHRENHEIT, -40}
                    });
        }

        @Parameterized.Parameter(0)
        public double initialTemperature;

        @Parameterized.Parameter(1)
        public TemperatureUnit unit;

        @Parameterized.Parameter(2)
        public int expectedTemperature;

        @Test
        public void testFromCelsius() {
            assertEquals(
                    expectedTemperature,
                    TemperatureConvertor.fromCelsius(initialTemperature, unit));
        }
    }

    @RunWith(Parameterized.class)
    public static class FromCelsiusToKelvinParameterizedTest {
        /**
         * Test data for the parameterized test.
         *
         * <p>The test data is provided as a 2D array of objects, where each row represents a test
         * case. The first column is the initial temperature in Celsius, the second column is the
         * target unit (Kelvin), and the third column is the expected temperature in Kelvin.
         */
        @Parameterized.Parameters(name = "{index}: fromCelsius({0}, {1}) = {2}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {0.0, TemperatureUnit.KELVIN, -273},
                        {100.0, TemperatureUnit.KELVIN, -173},
                        {-273.15, TemperatureUnit.KELVIN, -546}
                    });
        }

        @Parameterized.Parameter(0)
        public double initialTemperature;

        @Parameterized.Parameter(1)
        public TemperatureUnit unit;

        @Parameterized.Parameter(2)
        public int expectedTemperature;

        @Test
        public void testFromCelsius() {
            assertEquals(
                    expectedTemperature,
                    TemperatureConvertor.fromCelsius(initialTemperature, unit));
        }
    }

    @RunWith(Parameterized.class)
    public static class FromCelsiusToCelsiusParameterizedTest {
        /**
         * Test data for the parameterized test.
         *
         * <p>The test data is provided as a 2D array of objects, where each row represents a test
         * case. The first column is the initial temperature in Celsius, the second column is the
         * target unit (Celsius), and the third column is the expected temperature in Celsius.
         */
        @Parameterized.Parameters(name = "{index}: fromCelsius({0}, {1}) = {2}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {25.0, TemperatureUnit.CELSIUS, 25},
                        {100.0, TemperatureUnit.CELSIUS, 100},
                        {-40.0, TemperatureUnit.CELSIUS, -40}
                    });
        }

        @Parameterized.Parameter(0)
        public double initialTemperature;

        @Parameterized.Parameter(1)
        public TemperatureUnit unit;

        @Parameterized.Parameter(2)
        public int expectedTemperature;

        @Test
        public void testFromCelsius() {
            assertEquals(
                    expectedTemperature,
                    TemperatureConvertor.fromCelsius(initialTemperature, unit));
        }
    }

    /** This test checks that when NaN is passed as the temperature, the method returns NaN. */
    @Test
    public void FromCelsiusToFahrenheit_NaN() {
        assertEquals(
                Double.NaN, // Expected output
                TemperatureConvertor.fromCelsius(Double.NaN, TemperatureUnit.FAHRENHEIT),
                0.0001 // Allowable error margin for floating-point comparisons
                );
    }

    /**
     * This test checks that when +Infinity is passed as the temperature, the method returns
     * +Infinity.
     */
    @Test
    public void FromCelsiusToFahrenheit_PositiveInfinity() {
        assertEquals(
                Double.POSITIVE_INFINITY, // Expected output
                TemperatureConvertor.fromCelsius(
                        Double.POSITIVE_INFINITY, TemperatureUnit.FAHRENHEIT),
                0.0001 // Allowable error margin for floating-point comparisons
                );
    }

    /**
     * This test checks that when -Infinity is passed as the temperature, the method returns
     * -Infinity.
     */
    @Test
    public void FromCelsiusToFahrenheit_NegativeInfinity() {
        assertEquals(
                Double.NEGATIVE_INFINITY, // Expected output
                TemperatureConvertor.fromCelsius(
                        Double.NEGATIVE_INFINITY, TemperatureUnit.FAHRENHEIT),
                0.0001 // Allowable error margin for floating-point comparisons
                );
    }

    /**
     * This test checks that when a large positive Celsius value is passed, the method returns the
     * expected Fahrenheit value.
     */
    @Test
    public void FromCelsiusToFahrenheit_LargePositiveValue() {
        assertEquals(
                555444, // Expected output
                TemperatureConvertor.fromCelsius(1e6, TemperatureUnit.FAHRENHEIT),
                0.0001 // Allowable error margin for floating-point comparisons
                );
    }

    /**
     * This test checks that when a large negative Celsius value is passed, the method returns the
     * expected Kelvin value.
     */
    @Test
    public void FromCelsiusToFahrenheit_LargeNegativeValue() {
        assertEquals(
                -1003273, // Expected output
                TemperatureConvertor.fromCelsius(-1e6, TemperatureUnit.KELVIN),
                0.0001 // Allowable error margin for floating-point comparisons
                );
    }
}

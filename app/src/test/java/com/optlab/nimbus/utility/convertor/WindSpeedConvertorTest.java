package com.optlab.nimbus.utility.convertor;

import static org.junit.Assert.*;

import com.optlab.nimbus.data.model.common.WindSpeedUnit;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

/**
 * <table>
 *   <thead>
 *     <tr>
 *       <th>Scenario</th>
 *       <th>Input Speed (m/s)</th>
 *       <th>Target Unit</th>
 *       <th>Expected Output</th>
 *       <th>Notes</th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr><td>Convert positive speed to km/h</td><td>10.0</td><td>KILOMETERS_PER_HOUR</td><td>36.0</td><td>10.0 * 3.6</td></tr>
 *     <tr><td>Convert positive speed to mph</td><td>10.0</td><td>MILES_PER_HOUR</td><td>22.3694</td><td>10.0 * 2.23694</td></tr>
 *     <tr><td>Convert positive speed to knots</td><td>10.0</td><td>KNOTS</td><td>19.4384</td><td>10.0 * 1.94384</td></tr>
 *     <tr><td>Convert positive speed to m/s (default)</td><td>10.0</td><td>(default/other)</td><td>10.0</td><td>No conversion</td></tr>
 *     <tr><td>Convert zero speed to km/h</td><td>0.0</td><td>KILOMETERS_PER_HOUR</td><td>0.0</td><td>Edge case</td></tr>
 *     <tr><td>Convert zero speed to mph</td><td>0.0</td><td>MILES_PER_HOUR</td><td>0.0</td><td>Edge case</td></tr>
 *     <tr><td>Convert zero speed to knots</td><td>0.0</td><td>KNOTS</td><td>0.0</td><td>Edge case</td></tr>
 *     <tr><td>Convert zero speed to m/s (default)</td><td>0.0</td><td>(default/other)</td><td>0.0</td><td>Edge case</td></tr>
 *     <tr><td>Convert negative speed to km/h</td><td>-5.0</td><td>KILOMETERS_PER_HOUR</td><td>-18.0</td><td>-5.0 * 3.6</td></tr>
 *     <tr><td>Convert negative speed to mph</td><td>-5.0</td><td>MILES_PER_HOUR</td><td>-11.1847</td><td>-5.0 * 2.23694</td></tr>
 *     <tr><td>Convert negative speed to knots</td><td>-5.0</td><td>KNOTS</td><td>-9.7192</td><td>-5.0 * 1.94384</td></tr>
 *     <tr><td>Convert negative speed to m/s (default)</td><td>-5.0</td><td>(default/other)</td><td>-5.0</td><td></td></tr>
 *     <tr><td>Convert Double.MAX_VALUE to km/h</td><td>Double.MAX_VALUE</td><td>KILOMETERS_PER_HOUR</td><td>Double.POSITIVE_INFINITY</td><td>May overflow to infinity</td></tr>
 *     <tr><td>Convert Double.MAX_VALUE to mph</td><td>Double.MAX_VALUE</td><td>MILES_PER_HOUR</td><td>Double.POSITIVE_INFINITY</td><td>May overflow to infinity</td></tr>
 *     <tr><td>Convert Double.MAX_VALUE to knots</td><td>Double.MAX_VALUE</td><td>KNOTS</td><td>Double.POSITIVE_INFINITY</td><td>May overflow to infinity</td></tr>
 *     <tr><td>Convert Double.MAX_VALUE to m/s (default)</td><td>Double.MAX_VALUE</td><td>(default/other)</td><td>Double.MAX_VALUE</td><td></td></tr>
 *     <tr><td>Convert Double.MIN_VALUE to km/h</td><td>Double.MIN_VALUE</td><td>KILOMETERS_PER_HOUR</td><td>Very small positive</td><td>Double.MIN_VALUE * 3.6</td></tr>
 *     <tr><td>Convert Double.MIN_VALUE to mph</td><td>Double.MIN_VALUE</td><td>MILES_PER_HOUR</td><td>Very small positive</td><td>Double.MIN_VALUE * 2.23694</td></tr>
 *     <tr><td>Convert Double.MIN_VALUE to knots</td><td>Double.MIN_VALUE</td><td>KNOTS</td><td>Very small positive</td><td>Double.MIN_VALUE * 1.94384</td></tr>
 *     <tr><td>Convert Double.MIN_VALUE to m/s (default)</td><td>Double.MIN_VALUE</td><td>(default/other)</td><td>Double.MIN_VALUE</td><td></td></tr>
 *     <tr><td>Convert NaN to km/h</td><td>NaN</td><td>KILOMETERS_PER_HOUR</td><td>NaN</td><td>Should handle gracefully</td></tr>
 *     <tr><td>Convert NaN to mph</td><td>NaN</td><td>MILES_PER_HOUR</td><td>NaN</td><td>Should handle gracefully</td></tr>
 *     <tr><td>Convert NaN to knots</td><td>NaN</td><td>KNOTS</td><td>NaN</td><td>Should handle gracefully</td></tr>
 *     <tr><td>Convert NaN to m/s (default)</td><td>NaN</td><td>(default/other)</td><td>NaN</td><td></td></tr>
 *     <tr><td>Convert +Infinity to km/h</td><td>+Infinity</td><td>KILOMETERS_PER_HOUR</td><td>+Infinity</td><td></td></tr>
 *     <tr><td>Convert +Infinity to mph</td><td>+Infinity</td><td>MILES_PER_HOUR</td><td>+Infinity</td><td></td></tr>
 *     <tr><td>Convert +Infinity to knots</td><td>+Infinity</td><td>KNOTS</td><td>+Infinity</td><td></td></tr>
 *     <tr><td>Convert +Infinity to m/s (default)</td><td>+Infinity</td><td>(default/other)</td><td>+Infinity</td><td></td></tr>
 *     <tr><td>Convert -Infinity to km/h</td><td>-Infinity</td><td>KILOMETERS_PER_HOUR</td><td>-Infinity</td><td></td></tr>
 *     <tr><td>Convert -Infinity to mph</td><td>-Infinity</td><td>MILES_PER_HOUR</td><td>-Infinity</td><td></td></tr>
 *     <tr><td>Convert -Infinity to knots</td><td>-Infinity</td><td>KNOTS</td><td>-Infinity</td><td></td></tr>
 *     <tr><td>Convert -Infinity to m/s (default)</td><td>-Infinity</td><td>(default/other)</td><td>-Infinity</td><td></td></tr>
 *   </tbody>
 * </table>
 */
@RunWith(Parameterized.class)
public class WindSpeedConvertorTest {
    @Parameterized.Parameters(name = "{index}: fromMeterPerSecond({0}, {1}) = {2}")
    public static Collection<Object[]> data() {
        return List.of(
                new Object[][]{
                        // Positive
                        {10.0, WindSpeedUnit.KILOMETERS_PER_HOUR, 36.0},
                        {10.0, WindSpeedUnit.MILES_PER_HOUR, 22.3694},
                        {10.0, WindSpeedUnit.KNOTS, 19.4384},
                        {10.0, WindSpeedUnit.METERS_PER_SECOND, 10.0},
                        // Zero
                        {0.0, WindSpeedUnit.KILOMETERS_PER_HOUR, 0.0},
                        {0.0, WindSpeedUnit.MILES_PER_HOUR, 0.0},
                        {0.0, WindSpeedUnit.KNOTS, 0.0},
                        {0.0, WindSpeedUnit.METERS_PER_SECOND, 0.0},
                        // Negative
                        {-5.0, WindSpeedUnit.KILOMETERS_PER_HOUR, -18.0},
                        {-5.0, WindSpeedUnit.MILES_PER_HOUR, -11.1847},
                        {-5.0, WindSpeedUnit.KNOTS, -9.7192},
                        {-5.0, WindSpeedUnit.METERS_PER_SECOND, -5.0},
                        // Double.MAX_VALUE
                        {Double.MAX_VALUE, WindSpeedUnit.KILOMETERS_PER_HOUR, Double.POSITIVE_INFINITY},
                        {Double.MAX_VALUE, WindSpeedUnit.MILES_PER_HOUR, Double.POSITIVE_INFINITY},
                        {Double.MAX_VALUE, WindSpeedUnit.KNOTS, Double.POSITIVE_INFINITY},
                        {Double.MAX_VALUE, WindSpeedUnit.METERS_PER_SECOND, Double.MAX_VALUE},
                        // Double.MIN_VALUE
                        {Double.MIN_VALUE, WindSpeedUnit.KILOMETERS_PER_HOUR, Double.MIN_VALUE * 3.6},
                        {Double.MIN_VALUE, WindSpeedUnit.MILES_PER_HOUR, Double.MIN_VALUE * 2.23694},
                        {Double.MIN_VALUE, WindSpeedUnit.KNOTS, Double.MIN_VALUE * 1.94384},
                        {Double.MIN_VALUE, WindSpeedUnit.METERS_PER_SECOND, Double.MIN_VALUE},
                        // NaN
                        {Double.NaN, WindSpeedUnit.KILOMETERS_PER_HOUR, Double.NaN},
                        {Double.NaN, WindSpeedUnit.MILES_PER_HOUR, Double.NaN},
                        {Double.NaN, WindSpeedUnit.KNOTS, Double.NaN},
                        {Double.NaN, WindSpeedUnit.METERS_PER_SECOND, Double.NaN},
                        // +Infinity
                        {Double.POSITIVE_INFINITY, WindSpeedUnit.KILOMETERS_PER_HOUR, Double.POSITIVE_INFINITY},
                        {Double.POSITIVE_INFINITY, WindSpeedUnit.MILES_PER_HOUR, Double.POSITIVE_INFINITY},
                        {Double.POSITIVE_INFINITY, WindSpeedUnit.KNOTS, Double.POSITIVE_INFINITY},
                        {Double.POSITIVE_INFINITY, WindSpeedUnit.METERS_PER_SECOND, Double.POSITIVE_INFINITY},
                        // -Infinity
                        {Double.NEGATIVE_INFINITY, WindSpeedUnit.KILOMETERS_PER_HOUR, Double.NEGATIVE_INFINITY},
                        {Double.NEGATIVE_INFINITY, WindSpeedUnit.MILES_PER_HOUR, Double.NEGATIVE_INFINITY},
                        {Double.NEGATIVE_INFINITY, WindSpeedUnit.KNOTS, Double.NEGATIVE_INFINITY},
                        {Double.NEGATIVE_INFINITY, WindSpeedUnit.METERS_PER_SECOND, Double.NEGATIVE_INFINITY},
                });
    }

    @Parameterized.Parameter(0)
    public double inputSpeed;

    @Parameterized.Parameter(1)
    public WindSpeedUnit targetUnit;

    @Parameterized.Parameter(2)
    public double expectedOutput;

    @Test
    public void testFromMeterPerSecond() {
        double result = WindSpeedConvertor.fromMeterPerSecond(inputSpeed, targetUnit);
        if (Double.isNaN(expectedOutput)) {
            assertTrue(Double.isNaN(result));
        } else if (Double.isInfinite(expectedOutput)) {
            assertTrue(Double.isInfinite(result));
        } else {
            assertEquals(expectedOutput, result, 0.0001);
        }
    }
}

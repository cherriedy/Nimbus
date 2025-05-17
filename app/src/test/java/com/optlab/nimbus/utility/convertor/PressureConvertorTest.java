package com.optlab.nimbus.utility.convertor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.optlab.nimbus.data.model.PressureUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

// | Scenario                                      | Input Pressure | Input Unit         | Expected Output                | Notes                                                      |
// |-----------------------------------------------|---------------|--------------------|-------------------------------|------------------------------------------------------------|
// | Convert 1013.25 hPa to ATMOSPHERE             | 1013.25       | ATMOSPHERE         | 1.0                           | Standard atmosphere                                         |
// | Convert 1000 hPa to BAR                       | 1000          | BAR                | 1.0                           | 1 bar = 1000 hPa                                           |
// | Convert 1 hPa to PASCAL                       | 1             | PASCAL             | 100.0                         | 1 hPa = 100 Pa                                             |
// | Convert 1 hPa to TORR                         | 1             | TORR               | 0.750062                      | 1 hPa ≈ 0.750062 Torr                                      |
// | Identity conversion (hPa to hPa)              | 500           | HECTOPASCAL        | 500.0                         | Should return input value                                  |
// | Convert 0 hPa to ATMOSPHERE                   | 0             | ATMOSPHERE         | 0.0                           | Zero value                                                 |
// | Convert negative hPa to BAR                   | -100          | BAR                | -0.1                          | Negative value                                             |
// | Convert Double.NaN to PASCAL                  | NaN           | PASCAL             | NaN                           | Should handle gracefully                                   |
// | Convert Double.POSITIVE_INFINITY to TORR      | +Infinity     | TORR               | +Infinity                     | Edge case                                                  |
// | Convert Double.NEGATIVE_INFINITY to ATMOSPHERE| -Infinity     | ATMOSPHERE         | -Infinity                     | Edge case                                                  |
// | Null unit parameter                           | 100           | null               | Exception                     | Should throw NullPointerException due to switch on null    |

@RunWith(Parameterized.class)
public class PressureConvertorTest {
    @Parameterized.Parameters(name = "{index}: fromHectopascal({0}, {1}) = {2}")
    public static Collection<Object[]> data() {
        return List.of(new Object[][]{
                {1013.25, PressureUnit.ATMOSPHERE, 1.0},
                {1000, PressureUnit.BAR, 1.0},
                {1, PressureUnit.PASCAL, 100.0},
                {1, PressureUnit.TORR, 0.750062},
                {500, PressureUnit.HECTOPASCAL, 500.0},
                {0, PressureUnit.ATMOSPHERE, 0.0},
                {-100, PressureUnit.BAR, -0.1},
                {Double.NaN, PressureUnit.PASCAL, Double.NaN},
                {Double.POSITIVE_INFINITY, PressureUnit.TORR, Double.POSITIVE_INFINITY},
                {Double.NEGATIVE_INFINITY, PressureUnit.ATMOSPHERE, Double.NEGATIVE_INFINITY},
        });
    }

    @Parameterized.Parameter(0)
    public double inputPressure;

    @Parameterized.Parameter(1)
    public PressureUnit inputUnit;

    @Parameterized.Parameter(2)
    public double expectedOutput;

    @Test
    public void testFromHectopascal() {
        double result = PressureConvertor.fromHectopascal(inputPressure, inputUnit);
        if (Double.isNaN(inputPressure)) {
            assertTrue(Double.isNaN(expectedOutput));
        } else if (Double.isInfinite(inputPressure)) {
            assertEquals(expectedOutput, result, 0.0);
        } else {
            assertEquals(expectedOutput, result, 0.0001);
        }
    }

    @Test
    public void testFromHectopascal_NullUnit() {
        assertThrows(NullPointerException.class, () -> {
            PressureConvertor.fromHectopascal(100, null);
        });
    }
}

package com.optlab.nimbus.data.network.tomorrowio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.gson.Gson;
import com.optlab.nimbus.data.network.WeatherResponse;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link TomorrowIoResponse} mapping logic.
 *
 * <p>
 * <b>Table 1: MapToResponseWithMockDataParameterizedTest Scenarios</b>
 * <pre>
 * | Scenario Name                    | Input Description                                                      | Expected Output             |
 * |----------------------------------|------------------------------------------------------------------------|-----------------------------|
 * | nullInput                        | TomorrowIoResponse is null                                             | Empty list                  |
 * | nullData                         | TomorrowIoResponse.data is null                                        | Empty list                  |
 * | nullTimelines                    | TomorrowIoResponse.data.timelines is null                              | Empty list                  |
 * | emptyTimelines                   | TomorrowIoResponse.data.timelines is empty                             | Empty list                  |
 * | firstTimelineIsNull              | First timeline in list is null                                         | Empty list                  |
 * | firstTimelineHasNullIntervals    | First timeline's intervals is null                                     | Empty list                  |
 * | firstTimelineHasEmptyIntervals   | First timeline's intervals is empty                                    | Empty list                  |
 * | firstTimelineHasNullInterval     | First timeline's intervals contains null and an interval with null     | Empty list                  |
 * | firstTimelineHasAnEmptyInterval  | First timeline's intervals: 1 null, 3 valid intervals                  | List of 3 WeatherResponse   |
 * | timelineHasASingleValidTimeline  | First timeline's intervals: 1 null, 1 valid, 2 null                    | List of 1 WeatherResponse   |
 * | multipleTimelinesUsesOnlyFirst   | Multiple timelines, only first is used, 1 valid interval               | List of 1 WeatherResponse   |
 * </pre>
 *
 * <b>Table 2: MapToResponsesWithValidJsonParameterizedTest Scenarios</b>
 * <pre>
 * | Index | Date (expectedData)         | Temp (expectedTemp) | TempMin | TempMax | Pressure | WindSpeed | Humidity | WeatherCode |
 * |-------|-----------------------------|---------------------|---------|---------|----------|-----------|----------|-------------|
 * | 0     | 2025-05-19T06:00:00+07:00   | 31.9                | 25.7    | 31.9    | 1010     | 3.2       | 95       | 1001        |
 * | 1     | 2025-05-20T06:00:00+07:00   | 32.8                | 25.9    | 32.8    | 1009.42  | 2.8       | 97       | 1001        |
 * | 2     | 2025-05-21T06:00:00+07:00   | 33.7                | 26      | 33.7    | 1009.92  | 1.9       | 96       | 1001        |
 * | 3     | 2025-05-22T06:00:00+07:00   | 32.5                | 25      | 32.5    | 1008.22  | 1.8       | 97       | 1001        |
 * | 4     | 2025-05-23T06:00:00+07:00   | 30.7                | 24.7    | 30.7    | 1006.31  | 1.7       | 97       | 1001        |
 * </pre>
 */
@RunWith(Enclosed.class)
public class TomorrowIoResponseTest {
    @RunWith(Parameterized.class)
    public static class MapToResponseWithMockDataParameterizedTest {
        private static final String mockTimestep = "2025-05-19T06:00:00+07:00";
        private static final TomorrowIoResponse.Values mockValues =
                new TomorrowIoResponse.Values(
                        31.9,
                        32.9,
                        25.7,
                        1001,
                        "2025-05-19T06:00:00+07:00",
                        "2025-05-19T06:00:00+07:00",
                        3,
                        3,
                        256,
                        100,
                        20
                );

        private static TomorrowIoResponse nullInput() {
            return null;
        }

        private static TomorrowIoResponse nullData() {
            return new TomorrowIoResponse(null);
        }

        private static TomorrowIoResponse nullTimelines() {
            return new TomorrowIoResponse(new TomorrowIoResponse.Data(null));
        }

        private static TomorrowIoResponse emptyTimelines() {
            return new TomorrowIoResponse(new TomorrowIoResponse.Data(new ArrayList<>()));
        }

        private static TomorrowIoResponse firstTimelineIsNull() {
            return new TomorrowIoResponse(
                    new TomorrowIoResponse.Data(
                            Arrays.asList(
                                    null,
                                    new TomorrowIoResponse.Timeline(mockTimestep, null),
                                    new TomorrowIoResponse.Timeline(mockTimestep, null))));
        }

        private static TomorrowIoResponse firstTimelineHasNullIntervals() {
            return new TomorrowIoResponse(
                    new TomorrowIoResponse.Data(
                            Arrays.asList(new TomorrowIoResponse.Timeline(mockTimestep, null))));
        }

        private static TomorrowIoResponse firstTimelineHasEmptyIntervals() {
            return new TomorrowIoResponse(
                    new TomorrowIoResponse.Data(
                            Arrays.asList(
                                    new TomorrowIoResponse.Timeline(
                                            mockTimestep, new ArrayList<>()))));
        }

        private static TomorrowIoResponse firstTimelineHasNullInterval() {
            return new TomorrowIoResponse(
                    new TomorrowIoResponse.Data(
                            Arrays.asList(
                                    new TomorrowIoResponse.Timeline(
                                            mockTimestep,
                                            Arrays.asList(
                                                    null,
                                                    new TomorrowIoResponse.Interval(
                                                            mockTimestep, null))))));
        }

        public static TomorrowIoResponse firstTimelineHasAnEmptyInterval() {
            return new TomorrowIoResponse(
                    new TomorrowIoResponse.Data(
                            Arrays.asList(
                                    new TomorrowIoResponse.Timeline(
                                            mockTimestep,
                                            Arrays.asList(
                                                    null,
                                                    new TomorrowIoResponse.Interval(mockTimestep, new TomorrowIoResponse.Values(mockValues)),
                                                    new TomorrowIoResponse.Interval(mockTimestep, new TomorrowIoResponse.Values(mockValues)),
                                                    new TomorrowIoResponse.Interval(mockTimestep, new TomorrowIoResponse.Values(mockValues)))))));
        }

        public static TomorrowIoResponse timelineHasASingleValidTimeline() {
            return new TomorrowIoResponse(
                    new TomorrowIoResponse.Data(
                            Arrays.asList(
                                    new TomorrowIoResponse.Timeline(
                                            mockTimestep,
                                            Arrays.asList(
                                                    null,
                                                    new TomorrowIoResponse.Interval(mockTimestep, new TomorrowIoResponse.Values(mockValues)),
                                                    null,
                                                    null)))));
        }

        public static TomorrowIoResponse multipleTimelinesUsesOnlyFirst() {
            return new TomorrowIoResponse(
                    new TomorrowIoResponse.Data(
                            Arrays.asList(
                                    new TomorrowIoResponse.Timeline(
                                            mockTimestep,
                                            Arrays.asList(new TomorrowIoResponse.Interval(mockTimestep, mockValues))),
                                    new TomorrowIoResponse.Timeline(
                                            mockTimestep,
                                            Arrays.asList(new TomorrowIoResponse.Interval(mockTimestep, mockValues))),
                                    new TomorrowIoResponse.Timeline(
                                            mockTimestep,
                                            Arrays.asList(new TomorrowIoResponse.Interval(mockTimestep, mockValues)))
                            )));
        }

        @Parameterized.Parameters
        public static List<Object[]> data() {
            return Arrays.asList(
                    new Object[][]{
                            {nullInput(), Collections.emptyList()},
                            {nullData(), Collections.emptyList()},
                            {nullTimelines(), Collections.emptyList()},
                            {emptyTimelines(), Collections.emptyList()},
                            {firstTimelineIsNull(), Collections.emptyList()},
                            {firstTimelineHasNullIntervals(), Collections.emptyList()},
                            {firstTimelineHasEmptyIntervals(), Collections.emptyList()},
                            {firstTimelineHasNullInterval(), Collections.emptyList()},
                            {firstTimelineHasAnEmptyInterval(), Collections.nCopies(3, new WeatherResponse())},
                            {timelineHasASingleValidTimeline(), Collections.nCopies(1, new WeatherResponse())},
                            {multipleTimelinesUsesOnlyFirst(), Collections.nCopies(1, new WeatherResponse())}
                    });
        }

        @Parameterized.Parameter(0)
        public TomorrowIoResponse input;

        @Parameterized.Parameter(1)
        public List<WeatherResponse> expected;

        @Test
        public void testMapToResponse() {
            List<WeatherResponse> actual = TomorrowIoResponse.mapToResponses(input);
            if (expected.isEmpty()) {
                assertTrue("Expected empty list but got non-empty list", actual.isEmpty());
            } else {
                assertEquals("Expected equal but not", expected.size(), actual.size());
            }
        }
    }

    /**
     * Class for testing mapping response with valid JSON data using parameterized tests.
     */
    @RunWith(Parameterized.class)
    public static class MapToResponsesWithValidJsonParameterizedTest {
        /**
         * Input data for the test. This is a list of WeatherResponse objects that are read from a
         * JSON file.
         */
        public static List<WeatherResponse> inputs;

        @Parameterized.Parameter(0)
        public int index;

        @Parameterized.Parameter(1)
        public String expectedData;

        @Parameterized.Parameter(2)
        public double expectedTemp;

        @Parameterized.Parameter(3)
        public double expectedTempMin;

        @Parameterized.Parameter(4)
        public double expectedTempMax;

        @Parameterized.Parameter(5)
        public double expectedPressure;

        @Parameterized.Parameter(6)
        public double expectedWindSpeed;

        @Parameterized.Parameter(7)
        public double humidity;

        @Parameterized.Parameter(8)
        public int weatherCode;

        @Parameterized.Parameters(
                name = "{index}: mapToResponses() = [{1}, {2}, {3}, {4}, {5}, {6}, {7}]")
        public static List<Object[]> data() throws IOException {

            // Read the JSON response from the file, which is a mock of the TomorrowIo API
            byte[] bytes =
                    Files.readAllBytes(
                            Paths.get(
                                    "src/test/java/com/optlab/nimbus/data/network/tomorrowio/5days_response.json"));
            String json = new String(bytes, StandardCharsets.UTF_8);

            // Parse the json response to TomorrowIoResponse
            TomorrowIoResponse response = new Gson().fromJson(json, TomorrowIoResponse.class);
            inputs = TomorrowIoResponse.mapToResponses(response); // Input data read from json

            return List.of(
                    new Object[][]{
                            {0, "2025-05-19T06:00:00+07:00", 31.9, 25.7, 31.9, 1010, 3.2, 95, 1001},
                            {1, "2025-05-20T06:00:00+07:00", 32.8, 25.9, 32.8, 1009.42, 2.8, 97, 1001},
                            {2, "2025-05-21T06:00:00+07:00", 33.7, 26, 33.7, 1009.92, 1.9, 96, 1001},
                            {3, "2025-05-22T06:00:00+07:00", 32.5, 25, 32.5, 1008.22, 1.8, 97, 1001},
                            {4, "2025-05-23T06:00:00+07:00", 30.7, 24.7, 30.7, 1006.31, 1.7, 97, 1001}
                    });
        }

        @Test
        public void testMapToResponse() {
            WeatherResponse input = inputs.get(index);
            assertEquals("Date mismatch at interval " + index, expectedData, input.getDate());
            assertEquals(
                    "TemperatureMin mismatch at interval " + index,
                    expectedTempMin,
                    input.getTemperatureMin(),
                    0.01);
            assertEquals(
                    "TemperatureMax mismatch at interval " + index,
                    expectedTempMax,
                    input.getTemperatureMax(),
                    0.01);
            assertEquals(
                    "Pressure mismatch at interval " + index,
                    expectedPressure,
                    input.getPressure(),
                    0.01);
            assertEquals(
                    "WindSpeed mismatch at interval " + index,
                    expectedWindSpeed,
                    input.getWindSpeed(),
                    0.01);
            assertEquals(
                    "Humidity mismatch at interval " + index, humidity, input.getHumidity(), 0.01);
            assertEquals(
                    "WeatherCode mismatch at interval " + index,
                    weatherCode,
                    input.getWeatherCode());
            assertTrue(
                    "WeatherIcon should not be zero at interval " + index,
                    input.getWeatherIcon() != 0);
            assertTrue(
                    "WeatherDescription should not be zero at interval " + index,
                    input.getWeatherDescription() != 0);
        }
    }
}

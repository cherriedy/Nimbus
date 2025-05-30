package com.optlab.nimbus.data.preferences;

import static com.optlab.nimbus.data.preferences.UserPreferencesManagerTest.SharedMocks.INVALID_UNIT_KEY;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.optlab.nimbus.data.model.Coordinates;
import com.optlab.nimbus.data.model.PressureUnit;
import com.optlab.nimbus.data.model.TemperatureUnit;
import com.optlab.nimbus.data.model.WindSpeedUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(Enclosed.class)
public class UserPreferencesManagerTest {
    /**
     * Static container class for all shared test objects This allows all nested test classes to
     * access the same mock objects
     */
    static class SharedMocks {
        static Context mockContext;
        static SharedPreferences mockSharedPreferences;
        static SharedPreferences.Editor mockEditor;
        static UserPreferencesManager userPreferencesManager;
        static final String INVALID_UNIT_KEY = "INVALID_UNIT_KEY";

        /**
         * Storage for preferences to simulate SharedPreferences behavior. This map will hold
         * key-value pairs for preferences
         */
        static final Map<String, String> preferenceStorage = new HashMap<>();

        /** Set up the basic mock objects and behaviors */
        private static void setupMocks() {
            if (mockContext == null) {
                mockContext = mock(Context.class);
                mockSharedPreferences = mock(SharedPreferences.class);
                mockEditor = mock(SharedPreferences.Editor.class);
                preferenceStorage.clear(); // Clear storage on initialization

                when(mockContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
                        .thenReturn(mockSharedPreferences);

                // Store values in our map when putString is called
                when(mockEditor.putString(anyString(), anyString()))
                        .thenAnswer(
                                invocation -> {
                                    String key = invocation.getArgument(0);
                                    String value = invocation.getArgument(1);
                                    preferenceStorage.put(key, value);
                                    return mockEditor;
                                });

                when(mockSharedPreferences.edit()).thenReturn(mockEditor);

                // Check our storage map first, then fall back to default value
                when(mockSharedPreferences.getString(anyString(), anyString()))
                        .thenAnswer(
                                invocation -> {
                                    String key = invocation.getArgument(0);
                                    String defaultValue = invocation.getArgument(1);
                                    return preferenceStorage.getOrDefault(key, defaultValue);
                                });

                // Mock contains to check our storage map
                when(mockSharedPreferences.contains(anyString()))
                        .thenAnswer(
                                invocation -> {
                                    String key = invocation.getArgument(0);
                                    return preferenceStorage.containsKey(key);
                                });
            }
        }

        /**
         * Initialize all shared mock objects and create UserPreferencesManager. Call this
         * from @BeforeClass methods in nested test classes
         */
        static void initSharedObjects() {
            setupMocks();
            createUserPreferencesManager();
        }

        /** Create a new UserPreferencesManager instance with the current mock context */
        static void createUserPreferencesManager() {
            userPreferencesManager = new UserPreferencesManager(mockContext);
        }

        /**
         * Reset the shared objects if needed between test classes. Call this from @AfterClass
         * methods if you need to reset state
         */
        static void resetSharedObjects() {
            mockContext = null;
            mockSharedPreferences = null;
            mockEditor = null;
            userPreferencesManager = null;
            preferenceStorage.clear();
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class without any default values
     * set in the shared preferences. It checks if the manager correctly handles the absence of
     * default values.
     */
    public static class NoDefaultValueParameterizedTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.mockContext = mock(Context.class);
            SharedMocks.mockSharedPreferences = mock(SharedPreferences.class);
            SharedMocks.mockEditor = mock(SharedPreferences.Editor.class);
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Test
        public void testNoDefaultValueSet() {
            when(SharedMocks.mockSharedPreferences.contains(
                            UserPreferencesManager.TEMPERATURE_UNIT))
                    .thenReturn(true);

            when(SharedMocks.mockSharedPreferences.contains(UserPreferencesManager.WIND_SPEED_UNIT))
                    .thenReturn(true);

            when(SharedMocks.mockSharedPreferences.contains(UserPreferencesManager.PRESSURE_UNIT))
                    .thenReturn(true);

            when(SharedMocks.mockContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE))
                    .thenReturn(SharedMocks.mockSharedPreferences);

            when(SharedMocks.mockSharedPreferences.edit()).thenReturn(SharedMocks.mockEditor);

            new UserPreferencesManager(SharedMocks.mockContext);

            verify(SharedMocks.mockSharedPreferences, atLeastOnce()).contains(anyString());
            verify(SharedMocks.mockEditor, never()).putString(anyString(), anyString());
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * setUnit method is correct when valid keys and units are provided. It checks if the setUnit
     * method correctly stores the unit in the shared preferences and applies the changes.
     */
    @RunWith(Parameterized.class)
    public static class SetUnitValidKeyUnitParameterizedTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.initSharedObjects();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Parameterized.Parameters(name = "{0}, {1}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.CELSIUS},
                        {UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.FAHRENHEIT},
                        {UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.KELVIN},
                        {UserPreferencesManager.WIND_SPEED_UNIT, WindSpeedUnit.METERS_PER_SECOND},
                        {UserPreferencesManager.WIND_SPEED_UNIT, WindSpeedUnit.KNOTS},
                        {UserPreferencesManager.WIND_SPEED_UNIT, WindSpeedUnit.MILES_PER_HOUR},
                        {UserPreferencesManager.PRESSURE_UNIT, PressureUnit.HECTOPASCAL},
                        {UserPreferencesManager.PRESSURE_UNIT, PressureUnit.BAR},
                        {UserPreferencesManager.PRESSURE_UNIT, PressureUnit.PASCAL}
                    });
        }

        @Parameterized.Parameter(0)
        public String unit;

        @Parameterized.Parameter(1)
        public Enum<?> value;

        @Test
        public void testSetUnit() {
            // Reset the mock editor before each test to ensure a clean state because in constructor
            // we init default values and we want to test only the setUnit method and prevent
            // exception: TooManyActualInvocations
            Mockito.reset(SharedMocks.mockEditor);

            // When putString is called, return the mock editor to allow chaining
            when(SharedMocks.mockEditor.putString(anyString(), anyString()))
                    .thenReturn(SharedMocks.mockEditor);

            SharedMocks.userPreferencesManager.setUnit(unit, value);

            verify(SharedMocks.mockEditor).putString(unit, value.name());
            verify(SharedMocks.mockEditor).apply();
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * setUnit method is correct when null keys or units are provided. It checks if the setUnit
     * method throws the appropriate exceptions for invalid inputs.
     */
    @RunWith(Parameterized.class)
    public static class SetUnitWithInvalidValueParameterizedTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.initSharedObjects();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Parameterized.Parameters(name = "{0}, {1}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {UserPreferencesManager.TEMPERATURE_UNIT, null},
                        {UserPreferencesManager.WIND_SPEED_UNIT, null},
                        {UserPreferencesManager.PRESSURE_UNIT, null},
                        {" ", null},
                        {null, null},
                        {"INVALID_KEY", null}
                    });
        }

        @Parameterized.Parameter(0)
        public String unit;

        @Parameterized.Parameter(1)
        public Enum<?> value;

        @Test
        public void testGetUnit() {
            Class<?>[] exceptions = {NullPointerException.class, IllegalArgumentException.class};
            assertThrowsOneOf(
                    exceptions, () -> SharedMocks.userPreferencesManager.setUnit(unit, value));
        }
    }

    /**
     * This method is used to assert that one of the specified exceptions is thrown when the action
     * is executed. If none of the exceptions are thrown, it fails the test.
     */
    private static void assertThrowsOneOf(Class<?>[] exceptions, Runnable action) {
        try {
            action.run(); // Execute the action that is expected to throw an exception
            fail("Expected one of the exceptions to be thrown, but none was.");
        } catch (Throwable throwable) {
            for (Class<?> e : exceptions) {
                if (e.isInstance(throwable)) {
                    return;
                }
            }
            fail("Expected one of the exceptions to be thrown, but got: " + throwable);
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * getUnit method is correct when valid keys are provided. It checks if the getUnit method
     * returns the correct unit for each key.
     */
    @RunWith(Parameterized.class)
    public static class GetUnitReturnDefaultValueParameterizedTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.initSharedObjects();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Parameterized.Parameters(name = "{0}, {1}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {UserPreferencesManager.TEMPERATURE_UNIT, TemperatureUnit.CELSIUS},
                        {UserPreferencesManager.WIND_SPEED_UNIT, WindSpeedUnit.METERS_PER_SECOND},
                        {UserPreferencesManager.PRESSURE_UNIT, PressureUnit.HECTOPASCAL},
                        {INVALID_UNIT_KEY, null}
                    });
        }

        @Parameterized.Parameter(0)
        public String unit;

        @Parameterized.Parameter(1)
        public Enum<?> value;

        @Test
        public void testGetValueReturnDefault() {
            verify(SharedMocks.mockSharedPreferences, times(1))
                    .contains(UserPreferencesManager.TEMPERATURE_UNIT);
            verify(SharedMocks.mockSharedPreferences, times(1))
                    .contains(UserPreferencesManager.WIND_SPEED_UNIT);
            verify(SharedMocks.mockSharedPreferences, times(1))
                    .contains(UserPreferencesManager.PRESSURE_UNIT);

            if (unit.equals(INVALID_UNIT_KEY)) {
                //  It never reaches the default case since the key will be checked in
                // assertValidKey() method before it can be used to get the value. So
                // default is unnecessary here. This leads to the failure of the test.
                assertThrows(
                        IllegalStateException.class,
                        () -> SharedMocks.userPreferencesManager.getUnit(unit));
            } else {
                assertEquals(value, SharedMocks.userPreferencesManager.getUnit(unit));
            }
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * getTemperatureUnit methods is correct when valid units are set. It checks if these methods
     * return the correct unit.
     */
    @RunWith(Parameterized.class)
    public static class GetTemperatureUnitParameterizedTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.initSharedObjects();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {TemperatureUnit.CELSIUS},
                        {TemperatureUnit.FAHRENHEIT},
                        {TemperatureUnit.KELVIN},
                    });
        }

        @Parameterized.Parameter() public TemperatureUnit unit;

        @Test
        public void testGetTemperatureUnit() {
            SharedMocks.userPreferencesManager.setUnit(
                    UserPreferencesManager.TEMPERATURE_UNIT, unit);

            assertEquals(unit, SharedMocks.userPreferencesManager.getTemperatureUnit());
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * getWindSpeedUnit methods is correct when valid units are set. It checks if these methods
     * return the correct unit.
     */
    @RunWith(Parameterized.class)
    public static class GetWindSpeedUnitParameterizedTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.initSharedObjects();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {WindSpeedUnit.METERS_PER_SECOND},
                        {WindSpeedUnit.KNOTS},
                        {WindSpeedUnit.MILES_PER_HOUR},
                    });
        }

        @Parameterized.Parameter() public WindSpeedUnit unit;

        @Test
        public void testGetWindSpeedUnit() {
            SharedMocks.userPreferencesManager.setUnit(
                    UserPreferencesManager.WIND_SPEED_UNIT, unit);

            assertEquals(unit, SharedMocks.userPreferencesManager.getWindSpeedUnit());
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * getPressureUnit methods is correct when valid units are set. It checks if these methods
     * return the correct unit.
     */
    @RunWith(Parameterized.class)
    public static class GetPressureUnitParameterizedTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.initSharedObjects();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Parameterized.Parameters(name = "{0}")
        public static Collection<Object[]> data() {
            return List.of(
                    new Object[][] {
                        {PressureUnit.HECTOPASCAL}, {PressureUnit.BAR}, {PressureUnit.PASCAL}
                    });
        }

        @Parameterized.Parameter() public PressureUnit unit;

        @Test
        public void testGetPressureUnit() {
            SharedMocks.userPreferencesManager.setUnit(UserPreferencesManager.PRESSURE_UNIT, unit);

            assertEquals(unit, SharedMocks.userPreferencesManager.getPressureUnit());
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * getLocations method is correct when no locations are stored. It checks if the getLocations
     * method returns an empty list when no locations are set in the shared preferences.
     */
    public static class GetLocationsReturnEmptyListTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.initSharedObjects();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Test
        public void testGetLocations() {
            assertEquals(
                    Collections.emptyList(), SharedMocks.userPreferencesManager.getLocations());
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * getLocations method is correct when a list of locations is already stored. It checks if the
     * getLocations method returns the correct list of locations.
     */
    public static class GetLocationsReturnExistingListTest {
        private static Gson gson;
        private static List<Coordinates> coordinates;
        private static List<String> expected;
        private static String locationsJson;

        @BeforeClass
        public static void setUpClass() {
            gson = new Gson();

            SharedMocks.setupMocks();

            coordinates = Collections.nCopies(10, new Coordinates(1.0, 1.0));
            expected = coordinates.stream().map(gson::toJson).collect(Collectors.toList());
            locationsJson = gson.toJson(expected);

            // Force a specific mock behavior for getString to ensure it returns our value
            when(SharedMocks.mockSharedPreferences.getString(eq("locations"), isNull()))
                    .thenReturn(locationsJson);

            // Now create the UserPreferencesManager instance
            SharedMocks.createUserPreferencesManager();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Test
        public void testGetLocations() {
            List<String> result = SharedMocks.userPreferencesManager.getLocations();
            assertEquals(expected, result);
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * getLocation method is correct when no locations are stored. It checks if the getLocation
     * method returns null when no locations are set in the shared preferences.
     */
    public static class GetLocationReturnsNullTest {
        @BeforeClass
        public static void setUpClass() {
            // Initialize mocks without creating the UserPreferencesManager
            SharedMocks.setupMocks();

            // Mock behavior to ensure locations is null (or getLocations returns empty list)
            when(SharedMocks.mockSharedPreferences.getString(eq("locations"), isNull()))
                    .thenReturn(null);

            // Now create the UserPreferencesManager
            SharedMocks.createUserPreferencesManager();

            // Verify our setup is correct by checking getLocations returns empty list
            assertEquals(
                    Collections.emptyList(), SharedMocks.userPreferencesManager.getLocations());
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Test
        public void testGetLocation() {
            // Create a spy on the UserPreferencesManager to verify getLocations is called
            UserPreferencesManager spy = Mockito.spy(SharedMocks.userPreferencesManager);

            // Call getLocation
            Coordinates result = spy.getLocation(0);

            // Verify getLocations was called as part of getLocation
            verify(spy, times(1)).getLocations();

            // Verify the result is null
            assertNull("getLocation should return null when locations list is empty", result);

            // Or, but may lead to TooManyActualInvocations.
            // assertNull(SharedMocks.userPreferencesManager.getLocation(0));
            // verify(SharedMocks.mockSharedPreferences, times(1))
            //         .getString(eq("locations"), isNull());
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * getLocation method is correct when a location is already stored. It checks if the getLocation
     * method returns the correct location for a given position.
     */
    public static class GetLocationReturnExistingDataTest {
        private static Gson gson;
        private static List<Coordinates> coordinates;
        private static List<String> expected;
        private static String locationsJson;

        @BeforeClass
        public static void setUpClass() {
            gson = new Gson();

            SharedMocks.setupMocks();

            coordinates = Collections.nCopies(10, new Coordinates(1.0, 1.0));
            expected = coordinates.stream().map(gson::toJson).collect(Collectors.toList());
            locationsJson = gson.toJson(expected);

            // Force a specific mock behavior for getString to ensure it returns our value
            when(SharedMocks.mockSharedPreferences.getString(eq("locations"), isNull()))
                    .thenReturn(locationsJson);

            // Now create the UserPreferencesManager instance
            SharedMocks.createUserPreferencesManager();

            // Verify our setup is correct by checking getLocations returns the expected list
            assertNotNull(SharedMocks.userPreferencesManager.getLocations());
            assertEquals(expected, SharedMocks.userPreferencesManager.getLocations());
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Test
        public void testGetLocations() {
            Coordinates expectedCoordinates = new Coordinates(1.0, 1.0);
            UserPreferencesManager spy = Mockito.spy(SharedMocks.userPreferencesManager);

            Coordinates result = spy.getLocation(0);

            verify(spy, times(1)).getLocations();
            assertEquals(expectedCoordinates, result);
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * setLocation method is correct when a new location is added. It checks if the setLocation
     * method correctly adds the location to the list of locations.
     */
    public static class SetLocationTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.setupMocks();

            // Return value from the mock SharedPreferences for the "locations" key and
            // null as the default value.
            when(SharedMocks.mockSharedPreferences.getString(eq("locations"), isNull()))
                    .thenAnswer(
                            invocation -> {
                                String key = invocation.getArgument(0);
                                String defaultValue = invocation.getArgument(1);
                                return SharedMocks.preferenceStorage.getOrDefault(
                                        key, defaultValue);
                            });

            SharedMocks.createUserPreferencesManager();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Test
        public void testSetLocation() {
            Coordinates coordinates = new Coordinates(1.0, 1.0);
            SharedMocks.userPreferencesManager.setLocation(coordinates);

            List<String> locations = SharedMocks.userPreferencesManager.getLocations();
            assertEquals(1, locations.size());
            assertEquals(new Gson().toJson(coordinates), locations.get(0));
        }
    }

    /**
     * This test class is used to test the UserPreferencesManager class if the behavior of the
     * setLocation method is correct when a duplicate location is added. It checks if the
     * setLocation method does not add the same location multiple times.
     */
    public static class SetLocationDuplicateTest {
        @BeforeClass
        public static void setUpClass() {
            SharedMocks.setupMocks();

            // Return value from the mock SharedPreferences for the "locations" key and
            // null as the default value.
            when(SharedMocks.mockSharedPreferences.getString(eq("locations"), isNull()))
                    .thenAnswer(
                            invocation -> {
                                String key = invocation.getArgument(0);
                                String defaultValue = invocation.getArgument(1);
                                return SharedMocks.preferenceStorage.getOrDefault(
                                        key, defaultValue);
                            });

            SharedMocks.createUserPreferencesManager();
        }

        @AfterClass
        public static void tearDownClass() {
            SharedMocks.resetSharedObjects();
        }

        @Test
        public void testSetLocationDuplicate() {
            Coordinates coordinates = new Coordinates(1.0, 1.0);
            SharedMocks.userPreferencesManager.setLocation(coordinates);
            SharedMocks.userPreferencesManager.setLocation(coordinates);

            List<String> locations = SharedMocks.userPreferencesManager.getLocations();
            assertEquals(1, locations.size());
            assertEquals(new Gson().toJson(coordinates), locations.get(0));
        }
    }
}

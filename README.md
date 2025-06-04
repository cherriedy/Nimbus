# 🧪 Nimbus - Testing Branch

<div align="center">

![Testing](https://img.shields.io/badge/Purpose-Testing-blue)
![Platform](https://img.shields.io/badge/Platform-Android-brightgreen)
![Status](https://img.shields.io/badge/Status-Active-success)

</div>

This branch is dedicated to comprehensive testing of the Nimbus Android weather application.

## 🛠️ Testing Framework

The Nimbus project employs a robust testing strategy using:

| Framework         | Purpose                                |
|-------------------|----------------------------------------|
| **JUnit4**        | Core testing framework                 |
| **Mockito**       | Mocking dependencies                   |
| **Parameterized** | Testing multiple scenarios efficiently |

## 📂 Test Organization

Tests are neatly organized in the `app/src/test` directory, following the same package structure as
the main codebase for easy navigation and maintenance.

## 🔍 Key Test Areas

### 🧰 Utility Tests

> Tests for utility classes ensuring core functionality works as expected

- ⏰ **DateTimeUtil**: Time zone handling, date formatting and parsing
- 🌡️ **Temperature Conversion**: Celsius, Fahrenheit, and Kelvin conversions
- 💨 **Wind Speed Conversion**: Conversions between different units
- 🔄 **Pressure Conversion**: Atmospheric pressure unit handling

### 📱 ViewModel Tests

> Tests ensuring UI logic and state management work correctly

- 🏠 **HomeViewModel**: Weather display and user interactions
- 📅 **DailyWeatherViewModel**: Forecast data presentation
- 🔄 **State Management**: LiveData updates and UI state transitions
- 🖱️ **Event Handling**: User input processing

### 📚 Repository Tests

> Tests for data layer components

- ☁️ **TomorrowIoRepository**: Weather data retrieval and management
- 💾 **Caching Mechanisms**: Local data storage strategies
- 🔄 **Data Transformation**: Converting raw data to domain models

### 🌐 Network Tests

> Tests for API integration and data fetching

- 📊 **Response Parsing**: Converting JSON to model objects
- ❌ **Error Handling**: Network failure scenarios
- 📨 **Request Formatting**: API request construction

### ⚙️ User Preferences Tests

> Tests for user settings management

- 💾 **Settings Persistence**: Storage of user preferences
- 🔄 **Preference Management**: Retrieval and updates

### 👷 Worker Tests

> Android instrumentation tests for background operations

- ⏱️ **WorkManager Integration**: Scheduled task execution
- 🔙 **Background Processing**: Operations when app is not in foreground
- 💉 **Dependency Injection**: Component wiring with workers

## ▶️ Running Tests

Run all unit tests:

```bash
./gradlew test
```

Run a specific test class:

```bash
./gradlew testDebugUnitTest --tests com.optlab.nimbus.utility.DateTimeUtilTest
```

Run instrumentation tests:

```bash
./gradlew connectedAndroidTest
```

## 📊 Test Coverage

Generate detailed test coverage reports:

```bash
./gradlew testDebugUnitTestCoverage
```

Reports will be available at: `app/build/reports/coverage/`

## 📝 Guidelines for Writing Tests

| # | Guideline                                                                |
|---|--------------------------------------------------------------------------|
| 1 | Each test should focus on a **single functionality**                     |
| 2 | Use **descriptive naming** conventions that indicate what's being tested |
| 3 | Follow the **AAA pattern**: Arrange-Act-Assert                           |
| 4 | **Mock dependencies** to isolate the unit being tested                   |
| 5 | Use **parameterized tests** for multiple inputs with the same logic      |

---

<div align="center">
  <i>This README is for the testing purpose branch and documents the testing approach used in the Nimbus project.</i>
</div>

package com.optlab.nimbus.data.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Getter
public class Forecast {
    private final List<ForecastReport> reports;
    private final TimeZone timeZone;
    private final Current current;
    private final Today today;
    private final List<Day> coming;

    public Forecast(List<ForecastReport> reports, TimeZone timeZone) {
        this.reports = reports;
        this.timeZone = timeZone;
        if (reports == null || reports.isEmpty()) {
            throw new IllegalStateException("Forecast cannot null or empty");
        }

        List<List<ForecastReport>> grouped = groupByTimestep();
        current = getCurrent(grouped.get(0).get(0));
        today = getToday(grouped.get(0).subList(1, grouped.get(0).size()));
        coming = getComing(grouped.subList(1, grouped.size()));
    }

    private List<Day> getComing(List<List<ForecastReport>> grouped) {
        List<Day> days = new ArrayList<>();
        for (List<ForecastReport> reports : grouped) {
            for (ForecastReport report : reports) {
                days.add(
                        Day.builder()
                                .startTime(reports.get(0).getStartTime())
                                .temperatureMax(report.getTemperatureMax())
                                .temperatureMin(report.getTemperatureMin())
                                .weatherIcon(report.getWeatherIcon())
                                .weatherDescription(report.getWeatherDescription())
                                .build());
            }
        }
        return days;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private Today getToday(List<ForecastReport> reports) {
        List<HourlyReport> hourly =
                reports.stream().map(this::toHourly).collect(Collectors.toList());
        Comparator<HourlyReport> comparator = Comparator.comparing(HourlyReport::getTemperature);
        Temperature lowestTemp = hourly.stream().min(comparator).get().getTemperature();
        Temperature highestTemp = hourly.stream().max(comparator).get().getTemperature();
        return Today.builder()
                .lowestTemperature(lowestTemp)
                .highestTemperature(highestTemp)
                .hourly(hourly)
                .build();
    }

    private HourlyReport toHourly(ForecastReport report) {
        return HourlyReport.builder()
                .startTime(report.getStartTime())
                .temperature(report.getTemperature())
                .weatherIcon(report.getWeatherIcon())
                .weatherDescription(report.getWeatherDescription())
                .build();
    }

    private List<List<ForecastReport>> groupByTimestep() {
        return new ArrayList<>(
                reports.stream().collect(Collectors.groupingBy(this::toLocalDate)).values());
    }

    private LocalDate toLocalDate(ForecastReport report) {
        return toLocalDataTime(report).toLocalDate();
    }

    private LocalDateTime toLocalDataTime(ForecastReport report) {
        return ZonedDateTime.parse(report.getStartTime(), DateTimeFormatter.ISO_DATE_TIME)
                .withZoneSameLocal(timeZone.toZoneId())
                .toLocalDateTime();
    }

    private Current getCurrent(ForecastReport report) {
        return Current.builder()
                .startTime(report.getStartTime())
                .temperature(report.getTemperature())
                .feelLikes(report.getFeelsLike())
                .weatherIcon(report.getWeatherIcon())
                .weatherDescription(report.getWeatherDescription())
                .humidity(report.getHumidity())
                .pressure(report.getPressure())
                .uvIndex(report.getUvIndex())
                .wind(report.getWind())
                .build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Current {
        private String startTime;
        private Temperature temperature;
        private Temperature feelLikes;
        private int weatherIcon;
        private int weatherDescription;
        private double humidity;
        private Pressure pressure;
        private int uvIndex;
        private Wind wind;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class HourlyReport {
        private String startTime;
        private Temperature temperature;
        private int weatherIcon;
        private int weatherDescription;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Today {
        private Temperature highestTemperature;
        private Temperature lowestTemperature;
        private List<HourlyReport> hourly;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class Day {
        private String startTime;
        private Temperature temperatureMax;
        private Temperature temperatureMin;
        private int weatherIcon;
        private int weatherDescription;
    }
}

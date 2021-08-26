package com.example.weather.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastApiDto {

  private String weather;
  private LocalDate date;

  @JsonProperty("temp2m")
  private Temperature temperature;

  public ForecastApiDto() {
    this.temperature = new Temperature();
  }

  public ForecastApiDto(LocalDate date, float min, float max) {
    this.date = date;
    this.temperature = new Temperature();
    this.temperature.min = min;
    this.temperature.max = max;
  }

  public String getWeather() {
    return this.weather;
  }

  public void setWeather(String weather) {
    this.weather = weather;
  }

  public float getMaxTemperature() {
    return this.temperature.max;
  }

  public void setMaxTemperature(float max) {
    this.temperature.max = max;
  }

  public float getMinTemperature() {
    return this.temperature.min;
  }

  public void setMinTemperature(float min) {
    this.temperature.min = min;
  }

  public LocalDate getDate() {
    return this.date;
  }

  @JsonSetter("date")
  public void setDate(String date) {
    this.date = LocalDate.parse(date, DateTimeFormatter.BASIC_ISO_DATE);
  }

  static class Temperature {

    public float min;
    public float max;
  }

}

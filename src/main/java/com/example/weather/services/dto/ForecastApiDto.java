package com.example.weather.services.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastApiDto {

  public String weather;
  public String date;

  @JsonProperty("temp2m")
  public Temperature temperature;

  public static class Temperature {

    public float min;
    public float max;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

}

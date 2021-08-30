package com.example.weather.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class ForecastDto {

  public LocalDate date;
  @JsonProperty("min-forecasted")
  public float minForecasted;
  @JsonProperty("max-forecasted")
  public float maxForecasted;

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public float getMinForecasted() {
    return minForecasted;
  }

  public void setMinForecasted(float minForecasted) {
    this.minForecasted = minForecasted;
  }

  public float getMaxForecasted() {
    return maxForecasted;
  }

  public void setMaxForecasted(float maxForecasted) {
    this.maxForecasted = maxForecasted;
  }

}

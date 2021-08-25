package com.example.weather.services.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class ForecastDto {

  public LocalDate date;
  @JsonProperty("min-forecasted")
  public float minForecasted;
  @JsonProperty("max-forecasted")
  public float maxForecasted;

}

package com.example.weather.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class ForecastId implements Serializable {

  private String location;
  private LocalDate date;

  public ForecastId() {
  }

  public ForecastId(Location location, LocalDate date) {
    this.location = location.getSlug();
    this.date = date;
  }

}

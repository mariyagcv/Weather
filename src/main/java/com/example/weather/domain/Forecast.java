package com.example.weather.domain;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@IdClass(ForecastId.class)
public class Forecast {

  @Id
  @ManyToOne
  @JoinColumn(name = "slug", nullable = false, updatable = false)
  private Location location;
  @Id
  private LocalDate date;
  private float minForecasted;
  private float maxForecasted;

  public Forecast() {
  }

  /**
   * Creates a Forecast associated with a location, that has a date, minimum temperature forecasted,
   * and maximum temperature forecasted
   *
   * @param location
   * @param date
   * @param minForecasted
   * @param maxForecasted
   */
  public Forecast(Location location, LocalDate date, float minForecasted, float maxForecasted) {
    this.location = location;
    this.date = date;
    this.minForecasted = minForecasted;
    this.maxForecasted = maxForecasted;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public float getMinForecasted() {
    return minForecasted;
  }

  public void setMinForecasted(float minTemperature) {
    this.minForecasted = minTemperature;
  }

  public float getMaxForecasted() {
    return maxForecasted;
  }

  public void setMaxForecasted(float maxTemperature) {
    this.maxForecasted = maxTemperature;
  }

}

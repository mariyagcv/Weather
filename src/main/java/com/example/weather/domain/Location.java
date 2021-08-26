package com.example.weather.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

@Entity
public class Location {

  @Id
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  private String slug;
  private Float latitude;
  private Float longitude;

  public Location() {
  }

  /**
   * Creates a location with the given latitude and longitude
   *
   * @param slug      A unique url-safe slug representing the location
   * @param latitude  The location latitude as a Float
   * @param longitude The location longitude as a Float
   */
  public Location(String slug, Float latitude, Float longitude) {
    this.slug = slug;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public Float getLatitude() {
    return latitude;
  }

  public void setLatitude(Float latitude) {
    this.latitude = latitude;
  }

  public Float getLongitude() {
    return longitude;
  }

  public void setLongitude(Float longitude) {
    this.longitude = longitude;
  }
}

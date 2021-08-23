package com.example.weather.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;

@Entity
public class Location {

  @Id
  @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$")
  // todo: maybe rename to id
  private String slug;
  private float latitude;
  private float longitude;

  public Location() {
  }

  public Location(String slug, float latitude, float longitude) {
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

  public float getLatitude() {
    return latitude;
  }

  public void setLatitude(float latitude) {
    this.latitude = latitude;
  }

  public float getLongitude() {
    return longitude;
  }

  public void setLongitude(float longitude) {
    this.longitude = longitude;
  }
}

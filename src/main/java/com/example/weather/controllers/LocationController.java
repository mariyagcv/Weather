package com.example.weather.controllers;

import com.example.weather.domain.Location;
import com.example.weather.repositories.LocationRepository;
import com.example.weather.services.LocationService;
import com.example.weather.services.dto.ForecastDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/locations")
public class LocationController {

  @Autowired
  LocationRepository locationRepository;
  @Autowired
  LocationService locationService;

  @GetMapping
  public Iterable<Location> findAll() {
    return locationRepository.findAll();
  }

  @GetMapping("/{slug}")
  public Location findBySlug(@PathVariable("slug") String slug) {
    Optional<Location> location = locationRepository.findById(slug);

    if (!location.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return location.get();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  /**
   * Creates a location and saves it
   *
   * @param slug A unique url-safe slug passed as a path variable
   * @param location A request with a location consisting of slug, latitude, and longitude
   * @throws ResponseStatusException BAD_REQUEST if the latitude and longitude in the body are null
   */
  public void create(@Valid @RequestBody Location location) {
    if (location.getLatitude() == null || location.getLongitude() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Latitude and longitude cannot be null");
    }
    locationRepository.save(location);
  }

  @PutMapping(value = "/{slug}")
  @ResponseStatus(HttpStatus.OK)
  /**
   * Updates a location and saves
   *
   * @param slug A unique url-safe slug passed as a path variable
   * @param location A request with a location consisting of slug, latitude, and longitude
   * @throws ResponseStatusException BAD_REQUEST if slug value is different than the existing one
   * @throws ResponseStatusException NOT_FOUND if the location to update does not exist
   */
  public void update(@PathVariable("slug") String slug, @Valid @RequestBody Location location) {
    Optional<Location> locationToUpdateOptional = locationRepository.findById(slug);

    if (!locationToUpdateOptional.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    if (location.getSlug() != null && !slug.equals(location.getSlug())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Location slug cannot be changed");
    }

    Location locationToUpdate = locationToUpdateOptional.get();
    if (location.getLatitude() != null) {
      locationToUpdate.setLatitude(location.getLatitude());
    }
    if (location.getLongitude() != null) {
      locationToUpdate.setLongitude(location.getLongitude());
    }

    locationRepository.save(locationToUpdate);
  }

  @DeleteMapping(value = "/{slug}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  /**
   * Deletes a location
   *
   * @param slug A unique url-safe slug passed as a path variable
   * @throws ResponseStatusException NOT_FOUND if the location to delete does not exist
   */
  public void delete(@PathVariable("slug") String slug) {
    Optional<Location> location = locationRepository.findById(slug);

    if (!location.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    locationRepository.deleteById(slug);
  }

  @GetMapping("/{slug}/forecast")
  @ResponseStatus(HttpStatus.OK)
  /**
   * Returns a forecast for an existing location within a given range between start and end date.
   * The forecast returns as information the minimum and maximum temperatures for the dates, for which
   * the start date should be either today or in the present.
   *
   * @param slug A unique url-safe slug passed as a path variable
   * @param startDate A start date for the date range of the forecast
   * @param endDate An end date for the date range of the forecast
   * @throws ResponseStatusException BAD_REQUEST if the start date is in the past
   * @throws ResponseStatusException NOT_FOUND if the location does not exist
   */
  public List<ForecastDto> getForecastBySlug(@PathVariable("slug") String slug,
      @RequestParam("start_date")
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    if (startDate.isBefore(LocalDate.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Start date should not be earlier than today");
    }

    Optional<Location> location = locationRepository.findById(slug);

    if (!location.isPresent()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return locationService.getForecastByLocation(location.get(), startDate, endDate);
  }

}

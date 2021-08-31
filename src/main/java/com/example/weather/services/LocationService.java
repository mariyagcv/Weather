package com.example.weather.services;

import com.example.weather.domain.Location;
import com.example.weather.repositories.LocationRepository;
import java.time.LocalDate;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LocationService {

  Logger logger = LoggerFactory.getLogger(LocationService.class);

  @Autowired
  LocationRepository locationRepository;

  @Autowired
  ForecastApiService forecastApiService;

  @Autowired
  ForecastService forecastService;

  /**
   * Creates a location and calls a synchronization function that fetches most up-to-date
   * information from the external API
   *
   * @param location
   */
  public Location createLocation(Location location) {
    location.setCreatedDate(LocalDate.now());
    location = locationRepository.save(location);
    forecastService.synchroniseForecastForSingleLocation(location);

    return location;
  }

  /**
   * Invokes synchronisation of the forecast of all created locations on every 60000ms
   */
  @Scheduled(fixedDelay = 60000)
  public void synchroniseForecastForAllLocations() {
    logger.info("Starting synchronisation of all locations");
    Iterable<Location> allLocations = locationRepository.findAll();
    allLocations
        .forEach(location -> forecastService.synchroniseForecastForSingleLocation(location));
    logger.info("All locations have been synchronised");

  }

}

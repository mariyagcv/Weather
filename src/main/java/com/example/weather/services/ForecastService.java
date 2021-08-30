package com.example.weather.services;

import com.example.weather.domain.Forecast;
import com.example.weather.domain.Location;
import com.example.weather.repositories.ForecastRepository;
import com.example.weather.services.dto.ForecastApiDto;
import com.example.weather.services.dto.ForecastDto;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ForecastService {

  @Autowired
  ForecastRepository forecastRepository;

  @Autowired
  ForecastApiService forecastApiService;

  @Autowired
  ModelMapper modelMapper;

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  /**
   * Obtain forecast for a particular location given a time range
   *
   * @param location
   * @param startDate
   * @param endDate
   * @return List<ForecastDto>
   */
  public List<ForecastDto> getForecastForLocation(Location location, LocalDate startDate,
      LocalDate endDate) {

    List<ForecastDto> forecastDtos = forecastRepository.findAllByLocationSlug(location.getSlug())
        .stream()
        .filter(forecast ->
            filterByDates(forecast, startDate, endDate))
        .map(this::convertForecastoToForecastDto)
        .collect(Collectors.toList());

    return forecastDtos;
  }

  /**
   * Given a range of start and end dates, it filters out the results
   *
   * @param forecast
   * @param startDate
   * @param endDate
   * @return boolean
   */
  private boolean filterByDates(Forecast forecast, LocalDate startDate,
      LocalDate endDate) {
    return (forecast.getDate().equals(startDate) || forecast.getDate().isAfter(startDate))
        && (forecast.getDate().equals(endDate) || forecast.getDate().isBefore(endDate));
  }

  /**
   * Synchronises forecast for the given location by calling an external API and obtaining
   * the information
   * @param location
   */
  public void synchroniseForecastForSingleLocation(Location location) {
    List<ForecastApiDto> forecastApiDtos = forecastApiService
        .getForecastFromApi(location.getLatitude(), location.getLongitude());

    List<Forecast> forecasts = forecastApiDtos.stream().map(
        forecastApiDto -> new Forecast(location, forecastApiDto.getDate(),
            forecastApiDto.getMinTemperature(),
            forecastApiDto.getMaxTemperature())).collect(Collectors.toList());

    forecasts.forEach(forecast -> forecastRepository.save(forecast));
  }

  private ForecastDto convertForecastoToForecastDto(Forecast forecast) {
    return modelMapper.map(forecast, ForecastDto.class);
  }

}

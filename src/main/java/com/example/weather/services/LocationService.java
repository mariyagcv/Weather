package com.example.weather.services;

import com.example.weather.domain.Location;
import com.example.weather.services.dto.ForecastApiDto;
import com.example.weather.services.dto.ForecastDto;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {

  @Autowired
  ForecastApiService forecastApiService;

  /**
   * Obtain forecast for a particular location given a time range
   *
   * @param location
   * @param startDate
   * @param endDate
   * @return
   */
  public List<ForecastDto> getForecastByLocation(Location location, LocalDate startDate,
      LocalDate endDate) {

    List<ForecastApiDto> forecastApiDtos = forecastApiService
        .getForecastFromApi(location.getLatitude(), location.getLongitude());

    List<ForecastDto> forecastDtos = forecastApiDtos.stream()
        .map(this::convertForecastApiDtoToForecastDto).filter(forecastDto ->
            filterDtoByDates(forecastDto, startDate, endDate))
        .collect(Collectors.toList());

    return forecastDtos;
  }

  /**
   * Given a range of start and end dates, it filters out the results
   *
   * @param forecastDto
   * @param startDate
   * @param endDate
   * @return
   */
  private boolean filterDtoByDates(ForecastDto forecastDto, LocalDate startDate,
      LocalDate endDate) {
    return (forecastDto.date.equals(startDate) || forecastDto.date.isAfter(startDate))
        && (forecastDto.date.equals(endDate) || forecastDto.date.isBefore(endDate));
  }

  private ForecastDto convertForecastApiDtoToForecastDto(ForecastApiDto forecast) {
    ForecastDto forecastDto = new ForecastDto();
    forecastDto.maxForecasted = forecast.getMaxTemperature();
    forecastDto.minForecasted = forecast.getMinTemperature();
    forecastDto.date = forecast.getDate();
    return forecastDto;
  }

}

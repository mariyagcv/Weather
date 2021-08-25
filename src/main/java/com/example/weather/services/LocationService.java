package com.example.weather.services;

import com.example.weather.domain.Location;
import com.example.weather.repositories.LocationRepository;
import com.example.weather.services.dto.ForecastApiDto;
import com.example.weather.services.dto.ForecastDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class LocationService {

  public static final String URL = "https://www.7timer.info/bin/civillight.php?ac=0&unit=metric&output=json&tzshift=0";
  @Autowired
  LocationRepository locationRepository;

  public List<ForecastDto> getForecastByLocation(Location location, LocalDate startDate,
      LocalDate endDate) {

    RestTemplate restTemplate = new RestTemplate();

    String url = UriComponentsBuilder.fromUriString(URL)
        .queryParam("lon", location.getLongitude())
        .queryParam("lat", location.getLatitude())
        .build()
        .toUriString();

    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    List<ForecastApiDto> forecastApiDtos = getForecastDTOSFromResponse(response);

    List<ForecastDto> forecastDtos = forecastApiDtos.stream().map(forecast -> {
      ForecastDto forecastDto = new ForecastDto();
      forecastDto.maxForecasted = forecast.temperature.max;
      forecastDto.minForecasted = forecast.temperature.min;
      forecastDto.date = LocalDate.parse(forecast.date, DateTimeFormatter.BASIC_ISO_DATE);

      return forecastDto;
    }).filter(
        forecastDto -> (forecastDto.date.equals(startDate) || forecastDto.date.isAfter(startDate))
            && (forecastDto.date.equals(endDate) || forecastDto.date.isBefore(endDate)))
        .collect(Collectors.toList());

    return forecastDtos;
  }

  private List<ForecastApiDto> getForecastDTOSFromResponse(ResponseEntity<String> response) {
    List<ForecastApiDto> listDtos = null;

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(response.getBody());
      JsonNode dataseries = root.path("dataseries");
      listDtos = mapper.readValue(dataseries.toString(), new TypeReference<List<ForecastApiDto>>() {
      });
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return listDtos;
  }

}

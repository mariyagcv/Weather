package com.example.weather.services;

import com.example.weather.services.dto.ForecastApiDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
/**
 * Communicates with the external 7timer API to obtain information about the forecast of a location
 */
public class ForecastApiService {

  @Value("${forecast-api-url}")
  private String URL;

  @Autowired
  RestTemplate restTemplate;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  /**
   * @param latitude  The latitude of the location
   * @param longitude The longitude of the location
   * @return List<ForecastApiDto> A list of DTOs which contain forecast information for all of the
   * dates requested
   */
  public List<ForecastApiDto> getForecastFromApi(float latitude, float longitude) {
    String url = UriComponentsBuilder.fromUriString(URL)
        .queryParam("lat", latitude)
        .queryParam("lon", longitude)
        .build()
        .toUriString();

    ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

    return getForecastDTOSFromResponse(response);
  }

  /**
   * Builds and returns list of DTOs from the returned JSON response of the API
   *
   * @param response The response received from the external API
   * @return List<ForecastApiDto> A list of ForecastApiDtos objects
   */
  private List<ForecastApiDto> getForecastDTOSFromResponse(ResponseEntity<String> response) {
    List<ForecastApiDto> listDtos = null;

    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(response.getBody());
      JsonNode dataseries = root.path("dataseries");
      listDtos = mapper.readValue(dataseries.toString(), new TypeReference<List<ForecastApiDto>>() {
      });
    } catch (JsonProcessingException e) {
      // todo: throw exception
      e.printStackTrace();
    }
    return listDtos;
  }
}

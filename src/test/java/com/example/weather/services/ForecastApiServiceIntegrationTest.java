package com.example.weather.services;

import com.example.weather.services.dto.ForecastApiDto;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
public class ForecastApiServiceIntegrationTest {

  @Autowired
  ForecastApiService forecastApiService;

  @Test
  public void testExternalApiCall() {
    List<ForecastApiDto> forecastApiDtos = forecastApiService.getForecastFromApi(23.1f, 113.2f);

    Assert.assertFalse(forecastApiDtos.isEmpty());
    Assert.assertTrue(forecastApiDtos.get(0).getWeather() != null);
    Assert.assertTrue(forecastApiDtos.get(0).getDate() != null);

  }
}

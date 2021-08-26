package com.example.weather.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.example.weather.services.dto.ForecastApiDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ForecastApiServiceTest {

  @Autowired
  ForecastApiService forecastApiService;

  @MockBean
  RestTemplate restTemplateMock;

  private static final String JSON_RESPONSE = "{ \"product\" : \"civillight\" , \"init\" : \"2021082506\" , \"dataseries\" : [ { \"date\" : 20210825, \"weather\" : \"lightrain\", \"temp2m\" : { \"max\" : 33, \"min\" : 30 }, \"wind10m_max\" : 3 }, { \"date\" : 20210826, \"weather\" : \"lightrain\", \"temp2m\" : { \"max\" : 33, \"min\" : 27 }, \"wind10m_max\" : 3 }, { \"date\" : 20210827, \"weather\" : \"ts\", \"temp2m\" : { \"max\" : 36, \"min\" : 27 }, \"wind10m_max\" : 3 }] }";

  @Test
  public void forecastApiTest() {
    Mockito.when(restTemplateMock.getForEntity(anyString(), any()))
        .thenReturn(new ResponseEntity<>(JSON_RESPONSE, HttpStatus.OK));

    List<ForecastApiDto> forecastApiDtosList = forecastApiService.getForecastFromApi(1f, 1f);

    Assertions.assertEquals(3, forecastApiDtosList.size());

    ForecastApiDto firstForecastDto = forecastApiDtosList.stream().findFirst().get();
    Assertions.assertEquals(LocalDate.of(2021, 8, 25), firstForecastDto.getDate());
    Assertions.assertEquals(30f, firstForecastDto.getMinTemperature());
    Assertions.assertEquals(33f, firstForecastDto.getMaxTemperature());
  }

}

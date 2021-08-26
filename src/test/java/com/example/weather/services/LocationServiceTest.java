package com.example.weather.services;

import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.weather.domain.Location;
import com.example.weather.services.dto.ForecastApiDto;
import com.example.weather.services.dto.ForecastDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LocationServiceTest {

  public static final LocalDate BEGINNING_OF_TIME = LocalDate.of(1900, 1, 1);
  public static final LocalDate END_OF_TIME = LocalDate.of(2100, 1, 1);

  @Autowired
  LocationService locationService;

  @MockBean
  ForecastApiService forecastApiServiceMock;

  @Test
  public void forecastApiIsInvokedWithCorrectLatitudeAndLongitude() {
    LocalDate anyDate = LocalDate.now();
    Location location = new Location("test-slug", 1f, 2f);

    List<ForecastApiDto> forecastApiDtoList = new ArrayList<>();
    forecastApiDtoList.add(new ForecastApiDto(anyDate, 0f, 0f));

    Mockito.when(forecastApiServiceMock.getForecastFromApi(anyFloat(), anyFloat()))
        .thenReturn(forecastApiDtoList);

    locationService.getForecastByLocation(location, anyDate, anyDate);

    // Assert that the forecastApi was called with the correct latitude and longitude
    verify(forecastApiServiceMock, times(1)).getForecastFromApi(1f, 2f);
  }

  @Test
  public void getForecastByLocationTest() {
    ForecastApiDto forecastApiDto = new ForecastApiDto(LocalDate.of(2021, 1, 1), 1f, 50f);

    List<ForecastApiDto> forecastApiDtoList = new ArrayList<>();
    forecastApiDtoList.add(forecastApiDto);

    Mockito.when(forecastApiServiceMock.getForecastFromApi(anyFloat(), anyFloat()))
        .thenReturn(forecastApiDtoList);

    Location testLocation = new Location("test-slug", 0f, 0f);

    List<ForecastDto> forecastDtosList = locationService
        .getForecastByLocation(testLocation, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 1));

    Assertions.assertEquals(forecastDtosList.size(), 1);
    Assertions.assertEquals(LocalDate.of(2021, 1, 1), forecastDtosList.get(0).date);
    Assertions.assertEquals(1f, forecastDtosList.get(0).minForecasted);
    Assertions.assertEquals(50f, forecastDtosList.get(0).maxForecasted);
  }

  @Test
  public void getForecastByLocationDateFilterLowerLimit() {
    ForecastApiDto forecastApiDto1 = new ForecastApiDto(LocalDate.of(2021, 1, 2), 1f, 1f);
    ForecastApiDto forecastApiDto2 = new ForecastApiDto(LocalDate.of(2021, 1, 3), 1f, 1f);
    ForecastApiDto forecastApiDto3 = new ForecastApiDto(LocalDate.of(2021, 1, 4), 1f, 1f);

    List<ForecastApiDto> forecastApiDtoList = new ArrayList<>();
    forecastApiDtoList.add(forecastApiDto1);
    forecastApiDtoList.add(forecastApiDto2);
    forecastApiDtoList.add(forecastApiDto3);

    Mockito.when(forecastApiServiceMock.getForecastFromApi(anyFloat(), anyFloat()))
        .thenReturn(forecastApiDtoList);

    Location testLocation = new Location("test-slug", 0f, 0f);

    List<ForecastDto> forecastDtosListIncludeLower = locationService
        .getForecastByLocation(testLocation, BEGINNING_OF_TIME, LocalDate.of(2021, 1, 2));
    List<ForecastDto> forecastDtosListStartAndEndInPast = locationService
        .getForecastByLocation(testLocation, BEGINNING_OF_TIME, LocalDate.of(2021, 1, 1));

    Assertions.assertEquals(forecastDtosListIncludeLower.size(), 1);
    Assertions.assertEquals(forecastDtosListStartAndEndInPast.size(), 0);
  }

  @Test
  public void getForecastByLocationDateFilterUpperLimit() {
    ForecastApiDto forecastApiDto1 = new ForecastApiDto(LocalDate.of(2021, 1, 1), 1f, 1f);
    ForecastApiDto forecastApiDto2 = new ForecastApiDto(LocalDate.of(2021, 1, 2), 1f, 1f);
    ForecastApiDto forecastApiDto3 = new ForecastApiDto(LocalDate.of(2021, 1, 3), 1f, 1f);

    List<ForecastApiDto> forecastApiDtoList = new ArrayList<>();
    forecastApiDtoList.add(forecastApiDto1);
    forecastApiDtoList.add(forecastApiDto2);
    forecastApiDtoList.add(forecastApiDto3);

    Mockito.when(forecastApiServiceMock.getForecastFromApi(anyFloat(), anyFloat()))
        .thenReturn(forecastApiDtoList);

    Location testLocation = new Location("test-slug", 0f, 0f);

    List<ForecastDto> forecastDtosListIncludeUpper = locationService
        .getForecastByLocation(testLocation, LocalDate.of(2021, 1, 3), END_OF_TIME);
    List<ForecastDto> forecastDtosListStartAndEndInFuture = locationService
        .getForecastByLocation(testLocation, LocalDate.of(2021, 1, 4), END_OF_TIME);

    Assertions.assertEquals(forecastDtosListIncludeUpper.size(), 1);
    Assertions.assertEquals(forecastDtosListStartAndEndInFuture.size(), 0);
  }

}

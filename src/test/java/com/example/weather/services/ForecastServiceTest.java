package com.example.weather.services;

import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.weather.domain.Forecast;
import com.example.weather.domain.Location;
import com.example.weather.repositories.ForecastRepository;
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
public class ForecastServiceTest {

  public static final LocalDate BEGINNING_OF_TIME = LocalDate.of(1900, 1, 1);
  public static final LocalDate END_OF_TIME = LocalDate.of(2100, 1, 1);

  @Autowired
  ForecastService forecastService;

  @MockBean
  ForecastApiService forecastApiServiceMock;

  @MockBean
  ForecastRepository forecastRepositoryMock;

  @Test
  public void forecastApiIsInvokedWithCorrectLatitudeAndLongitude() {
    LocalDate anyDate = LocalDate.now();
    Location location = new Location("test-slug", 1f, 2f);

    List<ForecastApiDto> forecastApiDtoList = new ArrayList<>();
    forecastApiDtoList.add(new ForecastApiDto(anyDate, 0f, 0f));

    Mockito.when(forecastApiServiceMock.getForecastFromApi(anyFloat(), anyFloat()))
        .thenReturn(forecastApiDtoList);

    forecastService.synchroniseForecastForSingleLocation(location);

    // Verify that the forecastApiService is called with the right latitude and longitude
    verify(forecastApiServiceMock, times(1)).getForecastFromApi(1f, 2f);
  }


  @Test
  public void getForecastByLocationDateFilterLowerLimit() {
    Location location = new Location("test-slug", 0f, 0f);
    location.setCreatedDate(LocalDate.of(2021, 1, 2));
    List<Forecast> forecasts = new ArrayList<>();

    forecasts.add(new Forecast(location, LocalDate.of(2021, 1, 2), 1f, 1f));
    forecasts.add(new Forecast(location, LocalDate.of(2021, 1, 3), 1f, 1f));
    forecasts.add(new Forecast(location, LocalDate.of(2021, 1, 4), 1f, 1f));

    Mockito.when(forecastRepositoryMock.findAllByLocationSlug(location.getSlug()))
        .thenReturn(forecasts);

    List<ForecastDto> forecastDtosListIncludeLower = forecastService
        .getForecastForLocation(location, BEGINNING_OF_TIME, LocalDate.of(2021, 1, 2));
    List<ForecastDto> forecastDtosListStartAndEndInPast = forecastService
        .getForecastForLocation(location, BEGINNING_OF_TIME, LocalDate.of(2021, 1, 1));

    Assertions.assertEquals(1, forecastDtosListIncludeLower.size());
    Assertions.assertEquals(0, forecastDtosListStartAndEndInPast.size());
  }


  @Test
  public void getForecastByLocationDateFilterUpperLimit() {
    Location location = new Location("test-slug", 0f, 0f);
    location.setCreatedDate(LocalDate.of(2021, 1, 2));

    List<Forecast> forecasts = new ArrayList<>();
    forecasts.add(new Forecast(location, LocalDate.of(2021, 1, 2), 1f, 1f));
    forecasts.add(new Forecast(location, LocalDate.of(2021, 1, 3), 1f, 1f));
    forecasts.add(new Forecast(location, LocalDate.of(2021, 1, 4), 1f, 1f));

    Mockito.when(forecastRepositoryMock.findAllByLocationSlug(location.getSlug()))
        .thenReturn(forecasts);

    List<ForecastDto> forecastDtosListIncludeUpper = forecastService
        .getForecastForLocation(location, LocalDate.of(2021, 1, 3), END_OF_TIME);
    List<ForecastDto> forecastDtosListStartAndEndInFuture = forecastService
        .getForecastForLocation(location, LocalDate.of(2021, 1, 5), END_OF_TIME);

    Assertions.assertEquals(2, forecastDtosListIncludeUpper.size());
    Assertions.assertEquals(0, forecastDtosListStartAndEndInFuture.size());
  }

  @Test
  public void getForecastByLocationTest() {
    Location location = new Location("test-slug", 0f, 0f);

    List<Forecast> forecasts = new ArrayList<>();
    forecasts.add(new Forecast(location, LocalDate.of(2021, 1, 1), 1f, 50f));

    Mockito.when(forecastRepositoryMock.findAllByLocationSlug(location.getSlug()))
        .thenReturn(forecasts);

    List<ForecastDto> forecastDtosList = forecastService
        .getForecastForLocation(location, LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 1));

    Assertions.assertEquals(1, forecastDtosList.size());
    Assertions.assertEquals(LocalDate.of(2021, 1, 1), forecastDtosList.get(0).date);
    Assertions.assertEquals(1f, forecastDtosList.get(0).minForecasted);
    Assertions.assertEquals(50f, forecastDtosList.get(0).maxForecasted);
  }

}

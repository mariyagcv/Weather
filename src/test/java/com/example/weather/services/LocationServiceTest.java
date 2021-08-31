package com.example.weather.services;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.weather.domain.Location;
import com.example.weather.repositories.LocationRepository;
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

  @Autowired
  LocationService locationService;

  @MockBean
  ForecastService forecastServiceMock;

  @MockBean
  LocationRepository locationRepositoryMock;

  @Test
  public void shouldCreateLocation() {
    Location location = new Location("test-slug", 1f, 2f);
    Mockito.when(locationRepositoryMock.save(location)).thenReturn(location);

    locationService.createLocation(location);

    // Assert that the forecast is synchronized at creation
    verify(forecastServiceMock, times(1)).synchroniseForecastForSingleLocation(location);

    // Assert that the entity was correctly saved
    Assertions.assertNotNull(location.getCreatedDate());
    Assertions.assertEquals(Float.valueOf(1f), location.getLatitude());
    Assertions.assertEquals(Float.valueOf(2f), location.getLongitude());
  }

}

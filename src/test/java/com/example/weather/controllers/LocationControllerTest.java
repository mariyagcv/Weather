package com.example.weather.controllers;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.weather.domain.Location;
import com.example.weather.repositories.LocationRepository;
import com.example.weather.services.ForecastService;
import com.example.weather.services.LocationService;
import com.example.weather.services.dto.ForecastDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LocationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  LocationRepository locationRepositoryMock;

  @MockBean
  LocationService locationServiceMock;

  @MockBean
  ForecastService forecastServiceMock;

  @Test
  public void shouldReturnMultipleLocationsOnGetLocations() throws Exception {
    Location location1 = new Location("test1", 1f, 1f);
    Location location2 = new Location("test2", 2f, 2f);

    Mockito.when(locationRepositoryMock.findAll()).thenReturn(List.of(location1, location2));

    this.mockMvc.perform(get("/locations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
  }

  @Test
  public void shouldReturnEmptyOnGetLocationsWhenEmpty() throws Exception {
    Mockito.when(locationRepositoryMock.findAll()).thenReturn(new ArrayList<Location>());

    this.mockMvc.perform(get("/locations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  public void shouldReturnLocationOnGetSingleLocation() throws Exception {
    Location location = new Location("test-slug", 1f, 1f);

    Mockito.when(locationRepositoryMock.findById(location.getSlug()))
        .thenReturn(Optional.of(location));

    this.mockMvc.perform(get("/locations/{slug}/", "test-slug"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.slug").value(location.getSlug()));
  }

  @Test
  public void shouldReturn404WhenLocationNotFound() throws Exception {
    Mockito.when(locationRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

    this.mockMvc.perform(get("/locations/{slug}/", "non-existing-slug"))
        .andExpect(status().isNotFound());
  }

  @Test
  public void shouldCreateLocationWhenValid() throws Exception {
    Location location = new Location("test-slug", 1.23f, 4.56f);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(location);

    Mockito.when(locationRepositoryMock.findById(location.getSlug()))
        .thenReturn(Optional.of(location));

    this.mockMvc.perform(post("/locations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isCreated());
  }

  @Test
  public void shouldReturnBadRequestWhenCreatingLocationWithInvalidSlug() throws Exception {
    Location location = new Location("invalid%test%slug", 0.2f, -1.2f);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(location);

    this.mockMvc.perform(post("/locations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnBadRequestWhenCreatingLocationWithoutLatitude() throws Exception {
    Location location = new Location("test-slug", null, 1f);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(location);

    this.mockMvc.perform(post("/locations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnBadRequestWhenCreatingLocationWithoutLongitude() throws Exception {
    Location location = new Location("test-slug", 1f, null);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(location);

    this.mockMvc.perform(post("/locations")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldUpdateLocationWhenValid() throws Exception {
    Location oldLocation = new Location("test-slug", 1.23f, 4.56f);
    Mockito.when(locationRepositoryMock.findById(oldLocation.getSlug()))
        .thenReturn(Optional.of(oldLocation));

    Location updatedLocation = new Location("test-slug", -5.00f, 6.00f);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(updatedLocation);

    mockMvc.perform(put("/locations/{slug}", "test-slug")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isOk());
  }

  @Test
  public void shouldUpdateOnlyLocationLatitudeWhenGivenLatitude() throws Exception {
    Location oldLocation = new Location("test-slug", 1.23f, 4.56f);
    Mockito.when(locationRepositoryMock.findById(oldLocation.getSlug()))
        .thenReturn(Optional.of(oldLocation));

    Location newLocation = new Location("test-slug", 1f, null);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(newLocation);

    mockMvc.perform(put("/locations/{slug}", "test-slug")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isOk());

    ArgumentCaptor<Location> argumentCaptor = ArgumentCaptor.forClass(Location.class);
    verify(locationRepositoryMock, times(1)).save(argumentCaptor.capture());
    Location updatedLocation = argumentCaptor.getValue();

    Assertions.assertEquals(Float.valueOf(1f), updatedLocation.getLatitude());
    Assertions.assertEquals(Float.valueOf(4.56f), updatedLocation.getLongitude());
  }

  @Test
  public void shouldUpdateOnlyLocationLongitudeWhenGivenLongitude() throws Exception {
    Location oldLocation = new Location("test-slug", 1.23f, 4.56f);
    Mockito.when(locationRepositoryMock.findById(oldLocation.getSlug()))
        .thenReturn(Optional.of(oldLocation));

    Location newLocation = new Location("test-slug", null, 1f);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(newLocation);

    mockMvc.perform(put("/locations/{slug}", "test-slug")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isOk());

    ArgumentCaptor<Location> argumentCaptor = ArgumentCaptor.forClass(Location.class);
    verify(locationRepositoryMock, times(1)).save(argumentCaptor.capture());
    Location updatedLocation = argumentCaptor.getValue();

    Assertions.assertEquals(Float.valueOf(1.23f), updatedLocation.getLatitude());
    Assertions.assertEquals(Float.valueOf(1f), updatedLocation.getLongitude());
  }

  @Test
  public void shouldReturnBadRequestWhenUpdatingSlug() throws Exception {
    Location oldLocation = new Location("test-slug", 1.23f, 4.56f);
    Mockito.when(locationRepositoryMock.findById(oldLocation.getSlug()))
        .thenReturn(Optional.of(oldLocation));

    Location updatedLocation = new Location("updated-slug", 1f, null);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = objectMapper.writeValueAsString(updatedLocation);

    mockMvc.perform(put("/locations/{slug}", "test-slug")
        .contentType(MediaType.APPLICATION_JSON)
        .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnLocationForecastWhenGivenLocation() throws Exception {

    Location location = new Location("test-slug", -3.3f, 5.555f);
    LocalDate startDate = LocalDate.now();
    LocalDate endDate = LocalDate.now().plusDays(1);
    location.setCreatedDate(startDate);

    ForecastDto forecastDto = new ForecastDto();
    forecastDto.date = LocalDate.of(2021, 1, 1);
    forecastDto.maxForecasted = 35f;
    forecastDto.minForecasted = 18.5f;

    ArrayList<ForecastDto> listDto = new ArrayList<>();
    listDto.add(forecastDto);

    Mockito.when(locationRepositoryMock.findById("test-slug"))
        .thenReturn(java.util.Optional.of(location));
    Mockito.when(forecastServiceMock.getForecastForLocation(location, startDate, endDate))
        .thenReturn(listDto);

    this.mockMvc.perform(
        get("/locations/{slug}/forecast/", "test-slug").param("start_date", startDate.toString())
            .param("end_date", endDate.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].date").value(forecastDto.date.toString()))
        .andExpect(jsonPath("$[0].max-forecasted").value(forecastDto.maxForecasted))
        .andExpect(jsonPath("$[0].min-forecasted").value(forecastDto.minForecasted));
  }

  @Test
  public void shouldReturnBadRequestWhenStartDateIsBeforeCreatedDate() throws Exception {
    Location location = new Location("test-slug", LocalDate.of(2021, 1, 1), 1f, 1f);
    LocalDate oneDayBeforeCreated = location.getCreatedDate().minusDays(1);
    LocalDate endDate = oneDayBeforeCreated.plusDays(1);

    Mockito.when(locationRepositoryMock.findById(location.getSlug()))
        .thenReturn(Optional.of(location));

    this.mockMvc.perform(
        get("/locations/{slug}/forecast/", "test-slug")
            .param("start_date", oneDayBeforeCreated.toString())
            .param("end_date", endDate.toString()))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void shouldReturnNoContentWhenDeletingExistingLocation() throws Exception {
    Location location = new Location("test-slug", 1f, 1f);

    Mockito.when(locationRepositoryMock.findById(location.getSlug()))
        .thenReturn(Optional.of(location));

    this.mockMvc.perform(delete("/locations/{slug}/", "test-slug"))
        .andExpect(status().isNoContent());
  }

  @Test
  public void shouldReturnNotFoundWhenDeletingNonExistingLocation() throws Exception {
    Mockito.when(locationRepositoryMock.findById(anyString())).thenReturn(Optional.empty());

    this.mockMvc.perform(delete("/locations/{slug}/", "non-existing-slug"))
        .andExpect(status().isNotFound());
  }
}

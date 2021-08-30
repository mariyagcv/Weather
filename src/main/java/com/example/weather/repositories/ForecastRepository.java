package com.example.weather.repositories;

import com.example.weather.domain.Forecast;
import com.example.weather.domain.Location;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ForecastRepository extends CrudRepository<Forecast, Long> {

  List<Forecast> findAllByLocationSlug(String locationSlug);
  void deleteAllByLocationSlug(String locationSlug);

}

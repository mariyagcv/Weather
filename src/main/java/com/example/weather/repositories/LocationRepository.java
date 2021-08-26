package com.example.weather.repositories;

import com.example.weather.domain.Location;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Location, String> {

  List<Location> findAll();

}

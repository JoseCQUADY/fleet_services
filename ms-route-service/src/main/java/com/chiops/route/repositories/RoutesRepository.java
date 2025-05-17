package com.chiops.route.repositories;

import com.chiops.route.entities.Route;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;


@Repository
public interface RoutesRepository extends CrudRepository<Route, Long> {

    Optional<Route> findByVehicleVin(String vin);
}

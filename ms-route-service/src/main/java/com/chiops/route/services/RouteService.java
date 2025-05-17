package com.chiops.route.services;

import com.chiops.route.libs.dtos.RouteDTO;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;

import java.util.List;

@Singleton
public interface RouteService {

    RouteDTO createRoute(@Valid RouteDTO routeDTO);

    RouteDTO updateRoute(@Valid RouteDTO routeDTO);

    RouteDTO deleteRoute(String name);

    RouteDTO getRouteByVehicleVin(String vin);

    List<RouteDTO> getAllRoutes();
}

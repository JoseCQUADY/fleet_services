package com.chiops.route.services.impl;

import com.chiops.route.entities.Location;
import com.chiops.route.entities.Route;
import com.chiops.route.libs.clients.VehicleAssignmentClient;
import com.chiops.route.libs.dtos.RouteDTO;
import com.chiops.route.libs.exceptions.exception.*;
import com.chiops.route.repositories.RoutesRepository;
import com.chiops.route.services.RouteService;

import io.micronaut.validation.Validated;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Validated
@Singleton
public class RouteServiceImpl implements RouteService {

    private final RoutesRepository routesRepository;
    private final VehicleAssignmentClient vehicleAssignmentClient;

    public RouteServiceImpl(RoutesRepository routesRepository, 
                          VehicleAssignmentClient vehicleAssignmentClient) {
        this.routesRepository = routesRepository;
        this.vehicleAssignmentClient = vehicleAssignmentClient;
    }

    @Override
    public RouteDTO createRoute(@Valid RouteDTO routeDTO) {

        if (vehicleAssignmentClient.findByVin(routeDTO.getVehicleVin()).isEmpty()) {
            throw new ConflictException("Vehicle with VIN " + routeDTO.getVehicleVin() + " is not assigned to any driver");
        }

        if (routesRepository.findByVehicleVin(routeDTO.getVehicleVin()).isPresent()) {
            throw new ConflictException("Route already exists for vehicle with VIN " + routeDTO.getVehicleVin());
        }

        Route route = dtoToRoute(routeDTO);
        return routeToDto(routesRepository.save(route));
    }

    @Override
    public RouteDTO updateRoute(@Valid RouteDTO routeDTO) {
        Route existingRoute = routesRepository.findByVehicleVin(routeDTO.getVehicleVin())
                .orElseThrow(() -> new NotFoundException("Route with VIN " + routeDTO.getVehicleVin() + " not found"));

        if (routeDTO.getVehicleVin() != null &&
                !routeDTO.getVehicleVin().equals(existingRoute.getVehicleVin())) {
            throw new BadRequestException("Vehicle VIN cannot be changed");
        }


        if (vehicleAssignmentClient.findByVin(routeDTO.getVehicleVin()).isEmpty()) {
            throw new ConflictException("Vehicle with VIN " + routeDTO.getVehicleVin() + " is not assigned to any driver");
        }

        updateRouteFields(existingRoute, routeDTO);
        return routeToDto(routesRepository.update(existingRoute));
    }

    @Override
    public RouteDTO deleteRoute(String vin) {
        Route route = routesRepository.findByVehicleVin(vin)
                .orElseThrow(() -> new BadRequestException("Route with VIN " + vin + " is incorrect"));


        if (vehicleAssignmentClient.findByVin(route.getVehicleVin()).isEmpty()) {
            throw new ConflictException("Vehicle with VIN " + route.getVehicleVin() + " is not assigned to any driver");
        }

        routesRepository.delete(route);
        return routeToDto(route);
    }

    @Override
    public RouteDTO getRouteByVehicleVin(String vin) {
        Route route = routesRepository.findByVehicleVin(vin)
                .orElseThrow(() -> new NotFoundException("Route with VIN " + vin + " not found"));


        if (vehicleAssignmentClient.findByVin(route.getVehicleVin()).isEmpty()) {
            throw new ConflictException("Vehicle with VIN " + route.getVehicleVin() + " is not assigned to any driver");
        }

        return routeToDto(route);
    }

    @Override
    public List<RouteDTO> getAllRoutes() {
        List<Route> routes = routesRepository.findAll();

        routes.forEach(route -> {
            if (vehicleAssignmentClient.findByVin(route.getVehicleVin()).isEmpty()) {
                throw new ConflictException("Vehicle with VIN " + route.getVehicleVin() + " is not assigned to any driver");
            }
        });

        return routes.stream().map(this::routeToDto).toList();
    }

    private Route dtoToRoute(RouteDTO routeDTO) {
        return new Route(routeDTO.getVehicleVin(), routeDTO.getRouteName(), routeDTO.getTravelDate(),
                routeDTO.getCreationDate(), new Location(routeDTO.getStartLatitude(), routeDTO.getStartLongitude(),
                routeDTO.getEndLatitude(), routeDTO.getEndLongitude()));
    }

    private RouteDTO routeToDto(Route route) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setVehicleVin(route.getVehicleVin());
        routeDTO.setRouteName(route.getRouteName());
        routeDTO.setTravelDate(route.getTravelDate());
        routeDTO.setCreationDate(route.getCreationDate());
        routeDTO.setStartLatitude(route.getLocation().getStartLatitude());
        routeDTO.setStartLongitude(route.getLocation().getStartLongitude());
        routeDTO.setEndLatitude(route.getLocation().getEndLatitude());
        routeDTO.setEndLongitude(route.getLocation().getEndLongitude());
        return routeDTO;
    }

    private void updateRouteFields(Route route, RouteDTO dto) {
        route.setRouteName(dto.getRouteName());
        route.setTravelDate(dto.getTravelDate());
        route.setCreationDate(dto.getCreationDate());
        route.getLocation().setStartLatitude(dto.getStartLatitude());
        route.getLocation().setStartLongitude(dto.getStartLongitude());
        route.getLocation().setEndLatitude(dto.getEndLatitude());
        route.getLocation().setEndLongitude(dto.getEndLongitude());
    }
}
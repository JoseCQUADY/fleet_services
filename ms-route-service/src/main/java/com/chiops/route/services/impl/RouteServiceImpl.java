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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Validated
@Singleton
public class RouteServiceImpl implements RouteService {

    private static final Logger LOG = LoggerFactory.getLogger(RouteServiceImpl.class);
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
            LOG.error("Vehicle with VIN {} is not assigned to any driver", routeDTO.getVehicleVin());
            throw new ConflictException("Vehicle with VIN " + routeDTO.getVehicleVin() + " is not assigned to any driver");
        }

        if (routesRepository.findByVehicleVin(routeDTO.getVehicleVin()).isPresent()) {
            LOG.error("Route already exists for vehicle with VIN {}", routeDTO.getVehicleVin());
            throw new ConflictException("Route already exists for vehicle with VIN " + routeDTO.getVehicleVin());
        }

        Route route = dtoToRoute(routeDTO);
        LOG.info("Creating new route for vehicle with VIN {}", route.getVehicleVin());
        return routeToDto(routesRepository.save(route));
    }

    @Override
    public RouteDTO updateRoute(@Valid RouteDTO routeDTO) {
        Route existingRoute = routesRepository.findByVehicleVin(routeDTO.getVehicleVin())
                .orElseThrow(() -> {
                    LOG.error("Route with VIN {} not found", routeDTO.getVehicleVin());
                    return new NotFoundException("Route with VIN " + routeDTO.getVehicleVin() + " not found");
                });

        if (routeDTO.getVehicleVin() != null &&
                !routeDTO.getVehicleVin().equals(existingRoute.getVehicleVin())) {
            LOG.error("Vehicle VIN cannot be changed for route with VIN {}", existingRoute.getVehicleVin());
            throw new BadRequestException("Vehicle VIN cannot be changed");
        }


        if (vehicleAssignmentClient.findByVin(routeDTO.getVehicleVin()).isEmpty()) {
            LOG.error("Vehicle with VIN {} is not assigned to any driver", routeDTO.getVehicleVin());
            throw new ConflictException("Vehicle with VIN " + routeDTO.getVehicleVin() + " is not assigned to any driver");
        }

        updateRouteFields(existingRoute, routeDTO);

        LOG.info("Updating route for vehicle with VIN {}", existingRoute.getVehicleVin());
        return routeToDto(routesRepository.update(existingRoute));
    }

    @Override
    public RouteDTO deleteRoute(String vin) {
        Route route = routesRepository.findByVehicleVin(vin)
                .orElseThrow(() -> {
                    LOG.error("Route with VIN {} not found", vin);
                    return new BadRequestException("Route with VIN " + vin + " is incorrect");
                });


        if (vehicleAssignmentClient.findByVin(route.getVehicleVin()).isEmpty()) {
            LOG.error("Vehicle with VIN {} is not assigned to any driver", route.getVehicleVin());
            throw new ConflictException("Vehicle with VIN " + route.getVehicleVin() + " is not assigned to any driver");
        }

        routesRepository.delete(route);
        LOG.info("Deleted route for vehicle with VIN {}", vin);
        return routeToDto(route);
    }

    @Override
    public RouteDTO getRouteByVehicleVin(String vin) {
        Route route = routesRepository.findByVehicleVin(vin)
                .orElseThrow(() -> {
                    LOG.error("Route with VIN {} not found", vin);
                    return new NotFoundException("Route with VIN " + vin + " not found");
                    });


        if (vehicleAssignmentClient.findByVin(route.getVehicleVin()).isEmpty()) {
            LOG.error("Vehicle with VIN {} is not assigned to any driver", route.getVehicleVin());
            throw new ConflictException("Vehicle with VIN " + route.getVehicleVin() + " is not assigned to any driver");
        }

        LOG.info("Retrieved route for vehicle with VIN {}", vin);
        return routeToDto(route);
    }

    @Override
    public List<RouteDTO> getAllRoutes() {
        List<Route> routes = routesRepository.findAll();

        routes.forEach(route -> {
            if (vehicleAssignmentClient.findByVin(route.getVehicleVin()).isEmpty()) {
                LOG.error("Vehicle with VIN {} is not assigned to any driver", route.getVehicleVin());
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
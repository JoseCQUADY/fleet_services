package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.RouteClient;
import com.chiops.gateway.libs.dtos.RouteDTO;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/route")
public class RouteController {

    private static final Logger LOG = LoggerFactory.getLogger(RouteController.class);
    private final RouteClient routeClient;

    public RouteController(RouteClient routeClient) {
        LOG.info("Initializing RouteController with RouteClient");
        this.routeClient = routeClient;
    }

    @Post("/create")
    public RouteDTO createRoute(@Body RouteDTO route) {
        LOG.info("Received request to create route with Vehicle VIN: {}", route.getVehicleVin());
        return routeClient.createRoute(route);
    }

    @Put("/update")
    public RouteDTO updateRoute(@Body RouteDTO route) {
        LOG.info("Received request to update route with Vehicle VIN: {}", route.getVehicleVin());
        return routeClient.updateRoute(route);
    }

    @Delete("/delete/{vin}")
    public RouteDTO deleteRoute(@PathVariable String vin) {
        LOG.info("Received request to delete route with Vehicle VIN: {}", vin);
        return routeClient.deleteRoute(vin);
    }

    @Get("/getall")
    public Iterable<RouteDTO> getAllRoutes() {
        LOG.info("Received request to get all routes");
        return routeClient.getAllRoutes();
    }

    @Get("/get/{vin}")
    public RouteDTO getRoutesByVin(@PathVariable String vin) {
        LOG.info("Received request to get route by Vehicle VIN: {}", vin);
        return routeClient.getRoutesByVin(vin);
    }
}

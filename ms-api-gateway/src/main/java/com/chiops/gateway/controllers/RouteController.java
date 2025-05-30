package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.RouteClient;
import com.chiops.gateway.libs.dtos.RouteDTO;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/route")
public class RouteController {

    private static final Logger LOG = LoggerFactory.getLogger(RouteController.class);
    private final RouteClient routeClient;

    public RouteController(RouteClient routeClient) {
        MDC.put("controller", "RouteController");
        MDC.put("path", "api/route");
        MDC.put("method", "initialize");
        LOG.info("Initializing RouteController with RouteClient");
        this.routeClient = routeClient;
    }

    @Post("/create")
    public RouteDTO createRoute(@Body RouteDTO route) {
        MDC.put("method", "POST");
        MDC.put("path", "api/route/create");
        MDC.put("user", route.getVehicleVin());
        try {
            LOG.info("Received request to create route with Vehicle VIN: {}", route.getVehicleVin());
            RouteDTO response = routeClient.createRoute(route);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error creating route with Vehicle VIN: {}", route.getVehicleVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Put("/update")
    public RouteDTO updateRoute(@Body RouteDTO route) {
        MDC.put("method", "PUT");
        MDC.put("path", "api/route/update");
        MDC.put("user", route.getVehicleVin());
        try {
            LOG.info("Received request to update route with Vehicle VIN: {}", route.getVehicleVin());
            RouteDTO response = routeClient.updateRoute(route);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error updating route with Vehicle VIN: {}", route.getVehicleVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Delete("/delete/{vin}")
    public RouteDTO deleteRoute(@PathVariable String vin) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/route/delete/" + vin);
        MDC.put("user", vin);
        try {
            LOG.info("Received request to delete route with Vehicle VIN: {}", vin);
            RouteDTO response = routeClient.deleteRoute(vin);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error deleting route with Vehicle VIN: {}", vin, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get("/getall")
    public Iterable<RouteDTO> getAllRoutes() {
        MDC.put("method", "GET");
        MDC.put("path", "api/route/getall");
        try {
            LOG.info("Received request to get all routes");
            Iterable<RouteDTO> response = routeClient.getAllRoutes();
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving all routes", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get("/get/{vin}")
    public RouteDTO getRoutesByVin(@PathVariable String vin) {
        MDC.put("method", "GET");
        MDC.put("path", "api/route/get/" + vin);
        MDC.put("user", vin);
        try {
            LOG.info("Received request to get route by Vehicle VIN: {}", vin);
            RouteDTO response = routeClient.getRoutesByVin(vin);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving route with Vehicle VIN: {}", vin, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}

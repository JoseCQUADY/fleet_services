package com.chiops.route.controllers;

import com.chiops.route.libs.dtos.RouteDTO;
import com.chiops.route.libs.exceptions.entities.ErrorResponse;
import com.chiops.route.libs.exceptions.exception.BadRequestException;
import com.chiops.route.libs.exceptions.exception.InternalServerException;
import com.chiops.route.libs.exceptions.exception.MethodNotAllowedException;
import com.chiops.route.libs.exceptions.exception.NotFoundException;
import com.chiops.route.services.RouteService;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import jakarta.validation.ConstraintViolationException;

@Controller("/route")
@ExecuteOn(TaskExecutors.BLOCKING)
public class RouteController {

    private static final Logger LOG = LoggerFactory.getLogger(RouteController.class);
    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @Post("/create")
    public RouteDTO createRoute(@Body RouteDTO route) {
        MDC.put("method", "POST");
        MDC.put("path", "service/route/create");
        MDC.put("user", route.getVehicleVin());
        LOG.info("Received request to create route: {}", route);
        try {
            return routeService.createRoute(route);
        } catch(ConstraintViolationException e){
            MDC.put("status", "400");
            LOG.error("Bad request while trying to create the route: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to create the route: " + e.getMessage());
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to create the route: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al crear la ruta: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to create the route: {}", e.getMessage());
            throw new InternalServerException("Error interno al crear la ruta: " + e.getMessage());
        }
    }

    @Put("/update")
    public RouteDTO updateRoute(@Body RouteDTO route) {
        MDC.put("method", "PUT");
        MDC.put("path", "service/route/update");
        MDC.put("user", route.getVehicleVin());
        LOG.info("Received request to update route: {}", route);
        try {
            return routeService.updateRoute(route);
        } catch(ConstraintViolationException e){
            MDC.put("status", "400");
            LOG.error("Bad request while trying to create the route: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to create the route: " + e.getMessage());
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to update the route: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al actualizar la ruta: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to update the route: {}", e.getMessage());
            throw new InternalServerException("Error interno al actualizar la ruta: " + e.getMessage());
        }
    }

    @Delete("/delete/{vin}")
    public RouteDTO deleteRoute(@PathVariable String vin) {
        MDC.put("method", "DELETE");
        MDC.put("path", "service/route/delete/" + vin);
        MDC.put("user", vin);
        LOG.info("Received request to delete route with VIN: {}", vin);
        try {
            return routeService.deleteRoute(vin);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to delete the route: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al eliminar la ruta: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to delete the route: {}", e.getMessage());
            throw new InternalServerException("Error interno al eliminar la ruta: " + e.getMessage());
        }
    }

    @Get("/getall")
    public Iterable<RouteDTO> getAllRoutes() {
        MDC.put("method", "GET");
        MDC.put("path", "service/route/getall");
        MDC.put("user", "all_routes");
        LOG.info("Received request to get all routes");
        try {
            return routeService.getAllRoutes();
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to get all routes: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al obtener todas las rutas: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to get all routes: {}", e.getMessage());
            throw new InternalServerException("Error interno al obtener todas las rutas: " + e.getMessage());
        }
    }

    @Get("/get/{vin}")
    public RouteDTO getRoutesByVin(@PathVariable String vin) {
        MDC.put("method", "GET");
        MDC.put("path", "service/route/get/" + vin);
        MDC.put("user", vin);
        LOG.info("Received request to get route by VIN: {}", vin);
        try {
            return routeService.getRouteByVehicleVin(vin);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to get the route by VIN: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al obtener la ruta por VIN: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to get the route by VIN: {}", e.getMessage());
            throw new InternalServerException("Error interno al obtener la ruta por VIN: " + e.getMessage());
        }
    }
}

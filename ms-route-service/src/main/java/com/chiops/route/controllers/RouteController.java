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
import jakarta.validation.ConstraintViolationException;

@Controller("/route")
@ExecuteOn(TaskExecutors.BLOCKING)
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @Post("/create")
    public RouteDTO createRoute(@Body RouteDTO route) {
        try {
            return routeService.createRoute(route);
        } catch(ConstraintViolationException e){
            throw new BadRequestException("Bad request while trying to create the route: " + e.getMessage());
        }catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al crear la ruta: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al crear la ruta: " + e.getMessage());
        }
    }

    @Put("/update")
    public RouteDTO updateRoute(@Body RouteDTO route) {
        try {
            return routeService.updateRoute(route);
        } catch(ConstraintViolationException e){
            throw new BadRequestException("Bad request while trying to create the driver: " + e.getMessage());
        }catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al actualizar la ruta: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al actualizar la ruta: " + e.getMessage());
        }
    }

    @Delete("/delete/{vin}")
    public RouteDTO deleteRoute(@PathVariable String vin) {
        try {
            return routeService.deleteRoute(vin);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al eliminar la ruta: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al eliminar la ruta: " + e.getMessage());
        }
    }

    @Get("/getall")
    public Iterable<RouteDTO> getAllRoutes() {
        try {
            return routeService.getAllRoutes();
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al obtener todas las rutas: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al obtener todas las rutas: " + e.getMessage());
        }
    }

    @Get("/get/{vin}")
    public RouteDTO getRoutesByVin(@PathVariable String vin) {
        try {
            return routeService.getRouteByVehicleVin(vin);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al obtener la ruta por VIN: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al obtener la ruta por VIN: " + e.getMessage());
        }
    }
}

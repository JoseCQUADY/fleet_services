package com.chiops.vehicle.controllers;

import com.chiops.vehicle.libs.dtos.VehicleAssignmentDTO;
import com.chiops.vehicle.libs.exceptions.entities.ErrorResponse;
import com.chiops.vehicle.libs.exceptions.exception.BadRequestException;
import com.chiops.vehicle.libs.exceptions.exception.InternalServerException;
import com.chiops.vehicle.libs.exceptions.exception.MethodNotAllowedException;
import com.chiops.vehicle.libs.exceptions.exception.NotFoundException;
import com.chiops.vehicle.services.VehicleAssignmentService;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


import java.util.List;

@Controller("/vehicle/assignment")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
public class VehicleAssigmentController {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleAssigmentController.class);
    private final VehicleAssignmentService vehicleAssignmentService;

    public VehicleAssigmentController(VehicleAssignmentService vehicleAssignmentService) {
        this.vehicleAssignmentService = vehicleAssignmentService;
    }

    @Get(value = "/status/{status}", produces = MediaType.APPLICATION_JSON)
    public List<VehicleAssignmentDTO> findByStatus(@PathVariable String status) {
        MDC.put("method", "GET");
        MDC.put("path", "service/vehicle/assignment/status/" + status);
        MDC.put("user", "anonymous");
        LOG.info("Received request to find assignments by status: {}", status);
        try {
            return vehicleAssignmentService.findByStatus(status);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to find assignments by status: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to find assignments by status: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to find assignments by status: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to find assignments by status: " + e.getMessage());
        }
    }

    @Get(value = "/history", produces = MediaType.APPLICATION_JSON)
    public List<VehicleAssignmentDTO> assignmentsHistory() {
        MDC.put("method", "GET");
        MDC.put("path", "service/vehicle/assignment/history");
        LOG.info("Received request to get vehicle assignments history");
        try {
            return vehicleAssignmentService.assignmentsHistory();
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            throw new BadRequestException("Bad request while trying to get assignment history: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            throw new InternalServerException("Internal server error while trying to get assignment history: " + e.getMessage());
        }
    }

    @Get(value = "/vin/{vin}", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO findByVin(@PathVariable String vin) {
        MDC.put("method", "GET");
        MDC.put("path", "service/vehicle/assignment/vin/" + vin);
        MDC.put("user", vin);
        LOG.info("Received request to find vehicle assignment by VIN: {}", vin);
        try {
            return vehicleAssignmentService.findByVin(vin);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            throw new BadRequestException("Bad request while trying to find assignment by VIN: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            throw new InternalServerException("Internal server error while trying to find assignment by VIN: " + e.getMessage());
        }
    }

    @Post(value = "/assign", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO assignVehicleToDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
        MDC.put("method", "POST");
        MDC.put("path", "service/vehicle/assignment/assign");
        MDC.put("user", vehicleAssignmentDto.getVin());
        LOG.info("Received request to assign vehicle to driver: {}", vehicleAssignmentDto.getVin());
        try {
            return vehicleAssignmentService.assignVehicleToDriver(vehicleAssignmentDto);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            throw new BadRequestException("Bad request while trying to assign vehicle to driver: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            throw new InternalServerException("Internal server error while trying to assign vehicle to driver: " + e.getMessage());
        }
    }

    @Post(value = "/release", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO releaseVehicleFromDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
        MDC.put("method", "POST");
        MDC.put("path", "service/vehicle/assignment/release");
        MDC.put("user", vehicleAssignmentDto.getVin());
        LOG.info("Received request to release vehicle from driver: {}", vehicleAssignmentDto.getVin());
        try {
            return vehicleAssignmentService.releaseVehicleFromDriver(vehicleAssignmentDto);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            throw new BadRequestException("Bad request while trying to release vehicle from driver: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            throw new InternalServerException("Internal server error while trying to release vehicle from driver: " + e.getMessage());
        }
    }

    @Put(value = "/change", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO changeDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
    MDC.put("method", "PUT");
        MDC.put("path", "service/vehicle/assignment/change");
        MDC.put("user", vehicleAssignmentDto.getVin());
        LOG.info("Received request to change driver for vehicle: {}", vehicleAssignmentDto.getVin());
        try {
            return vehicleAssignmentService.changeDriver(vehicleAssignmentDto);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            throw new BadRequestException("Bad request while trying to change driver: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            throw new InternalServerException("Internal server error while trying to change driver: " + e.getMessage());
        }
    }
}

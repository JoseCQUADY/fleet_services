package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.VehicleAssignmentClient;
import com.chiops.gateway.libs.dtos.VehicleAssignmentDTO;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


import java.util.List;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/vehicle/assignment")
public class VehicleAssignmentController {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleAssignmentController.class);
    private final VehicleAssignmentClient vehicleAssignmentClient;

    public VehicleAssignmentController(VehicleAssignmentClient vehicleAssignmentClient) {
        this.vehicleAssignmentClient = vehicleAssignmentClient;
    }

    @Get(value = "/status/{status}", produces = MediaType.APPLICATION_JSON)
    public List<VehicleAssignmentDTO> findByStatus(@PathVariable String status) {
        MDC.put("method", "GET");
        MDC.put("path", "api/vehicle/assignment/status/" + status);
        MDC.put("user", status);
        try {
            LOG.info("Received request to find vehicle assignments by status: {}", status);
            List<VehicleAssignmentDTO> response = vehicleAssignmentClient.findByStatus(status);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving vehicle assignments by status: {}", status, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get(value = "/history", produces = MediaType.APPLICATION_JSON)
    public List<VehicleAssignmentDTO> assignmentsHistory() {
        MDC.put("method", "GET");
        MDC.put("path", "api/vehicle/assignment/history");
        try {
            LOG.info("Received request to get vehicle assignments history");
            List<VehicleAssignmentDTO> response = vehicleAssignmentClient.assignmentsHistory();
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving vehicle assignments history", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get(value = "/vin/{vin}", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO findByVin(@PathVariable String vin) {
        MDC.put("method", "GET");
        MDC.put("path", "api/vehicle/assignment/vin/" + vin);
        MDC.put("user", vin);
        try {
            LOG.info("Received request to find vehicle assignment by VIN: {}", vin);
            VehicleAssignmentDTO response = vehicleAssignmentClient.findByVin(vin);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving vehicle assignment by VIN: {}", vin, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Post(value = "/assign", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO assignVehicleToDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
        MDC.put("method", "POST");
        MDC.put("path", "api/vehicle/assignment/assign");
        MDC.put("user", vehicleAssignmentDto.getVin());
        try {
            LOG.info("Received request to assign vehicle to driver: {}", vehicleAssignmentDto.getVin());
            VehicleAssignmentDTO response = vehicleAssignmentClient.assignVehicleToDriver(vehicleAssignmentDto);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error assigning vehicle to driver: {}", vehicleAssignmentDto.getVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Post(value = "/release", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO releaseVehicleFromDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
        MDC.put("method", "POST");
        MDC.put("path", "api/vehicle/assignment/release");
        MDC.put("user", vehicleAssignmentDto.getVin());
        try {
            LOG.info("Received request to release vehicle from driver: {}", vehicleAssignmentDto.getVin());
            VehicleAssignmentDTO response = vehicleAssignmentClient.releaseVehicleFromDriver(vehicleAssignmentDto);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error releasing vehicle from driver: {}", vehicleAssignmentDto.getVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Put(value = "/change", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO changeDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
        MDC.put("method", "PUT");
        MDC.put("path", "api/vehicle/assignment/change");
        MDC.put("user", vehicleAssignmentDto.getVin());
         try {
            LOG.info("Received request to change driver for vehicle: {}", vehicleAssignmentDto.getVin());
            VehicleAssignmentDTO response = vehicleAssignmentClient.changeDriver(vehicleAssignmentDto);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error changing driver for vehicle: {}", vehicleAssignmentDto.getVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}

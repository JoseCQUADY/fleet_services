package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.VehicleAssignmentClient;
import com.chiops.gateway.libs.dtos.VehicleAssignmentDTO;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        LOG.info("Received request to find vehicle assignments by status: {}", status);
        return vehicleAssignmentClient.findByStatus(status);
    }

    @Get(value = "/history", produces = MediaType.APPLICATION_JSON)
    public List<VehicleAssignmentDTO> assignmentsHistory() {
        LOG.info("Received request to get vehicle assignments history");
        return vehicleAssignmentClient.assignmentsHistory();
    }

    @Get(value = "/vin/{vin}", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO findByVin(@PathVariable String vin) {
        LOG.info("Received request to find vehicle assignment by VIN: {}", vin);
        return vehicleAssignmentClient.findByVin(vin);
    }

    @Post(value = "/assign", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO assignVehicleToDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
        LOG.info("Received request to assign vehicle to driver: {}", vehicleAssignmentDto.getVin());
        return vehicleAssignmentClient.assignVehicleToDriver(vehicleAssignmentDto);
    }

    @Post(value = "/release", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO releaseVehicleFromDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
        LOG.info("Received request to release vehicle from driver: {}", vehicleAssignmentDto.getVin());
        return vehicleAssignmentClient.releaseVehicleFromDriver(vehicleAssignmentDto);
    }

    @Put(value = "/change", produces = MediaType.APPLICATION_JSON)
    public VehicleAssignmentDTO changeDriver(@Body VehicleAssignmentDTO vehicleAssignmentDto) {
        LOG.info("Received request to change driver for vehicle: {}", vehicleAssignmentDto.getVin());
        return vehicleAssignmentClient.changeDriver(vehicleAssignmentDto);
    }
}

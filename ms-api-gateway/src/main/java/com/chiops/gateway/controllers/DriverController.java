package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.DriverClient;
import com.chiops.gateway.libs.dtos.DriverDTO;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/driver")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
public class DriverController {

    private static final Logger LOG = LoggerFactory.getLogger(DriverController.class);
    private final DriverClient driverClient;

    public DriverController(DriverClient driverClient) {
        this.driverClient = driverClient;
    }

    @Post("/create")
    public DriverDTO createDriver(@Body DriverDTO driverDTO) {
        LOG.info("Received request to create driver with CURP: {}", driverDTO.getCurp());
        return driverClient.createDriver(driverDTO);
    }

    @Put("/update")
    public DriverDTO updateDriver(@Body DriverDTO driverDTO) {
        LOG.info("Received request to update driver with CURP: {}", driverDTO.getCurp());
        return driverClient.updateDriver(driverDTO);
    }

    @Delete("/delete/{curp}")
    public void deleteDriver(@PathVariable String curp) {
        LOG.info("Received request to delete driver with CURP: {}", curp);
        driverClient.deleteDriver(curp);
    }

    @Get("/get/{curp}")
    public DriverDTO getDriverByCurp(@PathVariable String curp) {
        LOG.info("Received request to get driver by CURP: {}", curp);
        return driverClient.getDriverByCurp(curp);
    }

    @Get("/getall")
    public List<DriverDTO> getAllDrivers() {
        LOG.info("Received request to get all drivers");
        return driverClient.getAllDrivers();
    }
}

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
import org.slf4j.MDC;


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
        MDC.put("method", "POST");
        MDC.put("path", "api/driver/create");
        MDC.put("user", driverDTO.getCurp());
        try {
            LOG.info("Received request to create driver with CURP: {}", driverDTO.getCurp());
            DriverDTO response = driverClient.createDriver(driverDTO);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error creating driver with CURP: {}", driverDTO.getCurp(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Put("/update")
    public DriverDTO updateDriver(@Body DriverDTO driverDTO) {
        MDC.put("method", "PUT");
        MDC.put("path", "api/driver/update");
        MDC.put("user", driverDTO.getCurp());
        try {
            LOG.info("Received request to update driver with CURP: {}", driverDTO.getCurp());
            DriverDTO response = driverClient.updateDriver(driverDTO);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error updating driver with CURP: {}", driverDTO.getCurp(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Delete("/delete/{curp}")
    public void deleteDriver(@PathVariable String curp) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/driver/delete/" + curp);
        MDC.put("user", curp);
        try {
            LOG.info("Received request to delete driver with CURP: {}", curp);
            driverClient.deleteDriver(curp);
            MDC.put("status", "200");
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error deleting driver with CURP: {}", curp, e);
            throw e;
        } finally {
            MDC.clear();
        }
        
    }

    @Get("/get/{curp}")
    public DriverDTO getDriverByCurp(@PathVariable String curp) {
        MDC.put("method", "GET");
        MDC.put("path", "api/driver/get/" + curp);
        MDC.put("user", curp);
        try {
            LOG.info("Received request to get driver by CURP: {}", curp);
            DriverDTO response = driverClient.getDriverByCurp(curp);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving driver by CURP: {}", curp, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get("/getall")
    public List<DriverDTO> getAllDrivers() {
        MDC.put("method", "GET");
        MDC.put("path", "api/driver/getall");
        MDC.put("user", "all");
        try {
            LOG.info("Received request to get all drivers");
            List<DriverDTO> response = driverClient.getAllDrivers();
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving all drivers", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}

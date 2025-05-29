package com.chiops.driver.controllers;

import com.chiops.driver.libs.dtos.DriverDTO;
import com.chiops.driver.libs.exceptions.exception.BadRequestException;
import com.chiops.driver.libs.exceptions.exception.InternalServerException;

import com.chiops.driver.services.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.micronaut.http.annotation.*;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolationException;

import java.util.List;

@Controller("/driver")
public class DriverController {

    private static final Logger LOG = LoggerFactory.getLogger(DriverController.class);
    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @Post("/create")
    public DriverDTO createDriver(@Body DriverDTO driverDTO) {
        MDC.put("method", "POST");
        MDC.put("path", "api/driver/create");
        MDC.put("user", driverDTO.getCurp());
        LOG.info("Received request to create driver with CURP: {}", driverDTO.getCurp());
        try {
            return driverService.createDriver(driverDTO);
        }catch(ConstraintViolationException e){
            MDC.put("satus", "400");
            LOG.error("Bad request while trying to create the driver: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to create the driver: " + e.getMessage());
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to create the driver: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to create the driver: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to create the driver: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to create the driver: " + e.getMessage());
        }
    }

    @Put("/update")
    public DriverDTO updateDriver(@Body DriverDTO driverDTO) {
        MDC.put("method", "PUT");
        MDC.put("path", "api/driver/update");
        MDC.put("user", driverDTO.getCurp());
        LOG.info("Received request to update driver with CURP: {}", driverDTO.getCurp());
        try {
            return driverService.updateDriver(driverDTO);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to update the driver: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to update the driver: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to update the driver: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to update the driver: " + e.getMessage());
        }
    }

    @Delete("/delete/{curp}")
    public void deleteDriver(@PathVariable String curp) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/driver/delete/" + curp);
        MDC.put("user", curp);
        LOG.info("Received request to delete driver with CURP: {}", curp);
        try {
            driverService.deleteDriver(curp);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to delete the driver with CURP {}: {}", curp, e.getMessage());
            throw new BadRequestException("Bad request while trying to delete the driver with CURP " + curp + ": " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to delete the driver with CURP {}: {}", curp, e.getMessage());
            throw new InternalServerException("Internal server error while trying to delete the driver with CURP " + curp + ": " + e.getMessage());
        }
    }

    @Get("/get/{curp}")
    public DriverDTO getDriverByCurp(@PathVariable String curp) {
        MDC.put("method", "GET");
        MDC.put("path", "api/driver/get/" + curp);
        MDC.put("user", curp);
        LOG.info("Received request to get driver with CURP: {}", curp);
        try {
            return driverService.getDriverByCurp(curp);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to get the driver with CURP {}: {}", curp, e.getMessage());
            throw new BadRequestException("Bad request while trying to get the driver with CURP " + curp + ": " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to get the driver with CURP {}: {}", curp, e.getMessage());
            throw new InternalServerException("Internal server error while trying to get the driver with CURP " + curp + ": " + e.getMessage());
        }
    }

    @Get("/getall")
    public List<DriverDTO> getAllDrivers() {
        MDC.put("method", "GET");
        MDC.put("path", "api/driver/getall");
        MDC.put("user", "all");
        LOG.info("Received request to get all drivers");
        try {
            return driverService.getAllDrivers();
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to get all drivers: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to get all drivers: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to get all drivers: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to get all drivers: " + e.getMessage());
        }
    }
}

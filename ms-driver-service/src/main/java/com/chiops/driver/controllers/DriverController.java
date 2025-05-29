package com.chiops.driver.controllers;

import com.chiops.driver.libs.dtos.DriverDTO;
import com.chiops.driver.libs.exceptions.exception.BadRequestException;
import com.chiops.driver.libs.exceptions.exception.InternalServerException;

import com.chiops.driver.services.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        LOG.info("Received request to create driver with CURP: {}", driverDTO.getCurp());
        try {
            return driverService.createDriver(driverDTO);
        }catch(ConstraintViolationException e){
            LOG.error("Bad request while trying to create the driver: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to create the driver: " + e.getMessage());
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to create the driver: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to create the driver: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to create the driver: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to create the driver: " + e.getMessage());
        }
    }

    @Put("/update")
    public DriverDTO updateDriver(@Body DriverDTO driverDTO) {
        LOG.info("Received request to update driver with CURP: {}", driverDTO.getCurp());
        try {
            return driverService.updateDriver(driverDTO);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to update the driver: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to update the driver: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to update the driver: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to update the driver: " + e.getMessage());
        }
    }

    @Delete("/delete/{curp}")
    public void deleteDriver(@PathVariable String curp) {
    LOG.info("Received request to delete driver with CURP: {}", curp);
        try {
            driverService.deleteDriver(curp);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to delete the driver with CURP {}: {}", curp, e.getMessage());
            throw new BadRequestException("Bad request while trying to delete the driver with CURP " + curp + ": " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to delete the driver with CURP {}: {}", curp, e.getMessage());
            throw new InternalServerException("Internal server error while trying to delete the driver with CURP " + curp + ": " + e.getMessage());
        }
    }

    @Get("/get/{curp}")
    public DriverDTO getDriverByCurp(@PathVariable String curp) {
    LOG.info("Received request to get driver with CURP: {}", curp);
        try {
            return driverService.getDriverByCurp(curp);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to get the driver with CURP {}: {}", curp, e.getMessage());
            throw new BadRequestException("Bad request while trying to get the driver with CURP " + curp + ": " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to get the driver with CURP {}: {}", curp, e.getMessage());
            throw new InternalServerException("Internal server error while trying to get the driver with CURP " + curp + ": " + e.getMessage());
        }
    }

    @Get("/getall")
    public List<DriverDTO> getAllDrivers() {
    LOG.info("Received request to get all drivers");
        try {
            return driverService.getAllDrivers();
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to get all drivers: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to get all drivers: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to get all drivers: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to get all drivers: " + e.getMessage());
        }
    }
}

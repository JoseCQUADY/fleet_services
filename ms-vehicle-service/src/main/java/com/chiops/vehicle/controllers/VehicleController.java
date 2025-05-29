package com.chiops.vehicle.controllers;

import com.chiops.vehicle.libs.dtos.VehicleAssignmentDTO;
import com.chiops.vehicle.libs.dtos.VehicleDTO;
import com.chiops.vehicle.libs.dtos.VehicleWithImageDTO;
import com.chiops.vehicle.libs.exceptions.entities.ErrorResponse;
import com.chiops.vehicle.libs.exceptions.exception.BadRequestException;
import com.chiops.vehicle.libs.exceptions.exception.InternalServerException;
import com.chiops.vehicle.libs.exceptions.exception.MethodNotAllowedException;
import com.chiops.vehicle.libs.exceptions.exception.NotFoundException;
import com.chiops.vehicle.services.ImageStoreService;
import com.chiops.vehicle.services.VehicleImageDecodingService;
import com.chiops.vehicle.services.VehicleService;

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
@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/vehicle")
@Secured(SecurityRule.IS_ANONYMOUS)
public class VehicleController {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleController.class);
    private final VehicleService vehicleService;
    private final ImageStoreService imageStoreService;
    private final VehicleImageDecodingService decodingService;

    public VehicleController(VehicleService vehicleService,
                              ImageStoreService imageStoreService, 
                              VehicleImageDecodingService decodingService) {
        this.vehicleService = vehicleService;
        this.imageStoreService = imageStoreService;
        this.decodingService = decodingService;
    }

    @Post(value = "/create", consumes = MediaType.APPLICATION_JSON)
    public VehicleDTO createVehicle(@Body VehicleWithImageDTO vehicleWithImageDTO) {
            
        MDC.put("method", "POST");
        MDC.put("path", "service/vehicle/create");
        MDC.put("user", vehicleWithImageDTO.getVehicle().getVin());
        LOG.info("Received request to create vehicle: {}", vehicleWithImageDTO);
        try {
            return decodingService.createVehicleFromEncodedImage(vehicleWithImageDTO);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to create the vehicle: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to create the vehicle: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to create the vehicle: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to create the vehicle: " + e.getMessage());
        }
    }

    @Put(value = "/update", consumes = MediaType.APPLICATION_JSON)
    public VehicleDTO updateVehicle(@Body VehicleDTO vehicle) {
        MDC.put("method", "PUT");
        MDC.put("path", "service/vehicle/update");
        MDC.put("user", vehicle.getVin());
        LOG.info("Received request to update vehicle: {}", vehicle);
        try {
            return vehicleService.updateVehicle(vehicle);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to update the vehicle: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to update the vehicle: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to update the vehicle: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to update the vehicle: " + e.getMessage());
        }
    }

    @Delete("/delete/{vin}")
    public void deleteVehicle(@PathVariable String vin) {
        MDC.put("method", "DELETE");
        MDC.put("path", "service/vehicle/delete/" + vin);
        MDC.put("user", vin);
        LOG.info("Received request to delete vehicle with VIN: {}", vin);
        try {
            vehicleService.deleteVehicle(vin);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to delete the vehicle: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to delete the vehicle: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to delete the vehicle: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to delete the vehicle: " + e.getMessage());
        }
    }

    @Get("/get/{vin}")
    public VehicleDTO getVehicleByVin(@PathVariable String vin) {
        MDC.put("method", "GET");
        LOG.info("Received request to get vehicle by VIN: {}", vin);
        try {
            return vehicleService.getVehicleByVin(vin);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to get the vehicle by VIN: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to get the vehicle by VIN: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to get the vehicle by VIN: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to get the vehicle by VIN: " + e.getMessage());
        }
    }

    @Get(value = "/model/{model}", consumes = MediaType.APPLICATION_JSON)
    public List<VehicleDTO> getAllVehiclesByModel(@QueryValue String model) {
        MDC.put("method", "GET");
        MDC.put("path", "service/vehicle/model/" + model);
        MDC.put("user", model);
        LOG.info("Received request to get vehicles by model: {}", model);
        try {
            return vehicleService.getVehiclesByModelName(model);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to get vehicles by model: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to get vehicles by model: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to get vehicles by model: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to get vehicles by model: " + e.getMessage());
        }
    }

    @Get(value = "/getall", consumes = MediaType.APPLICATION_JSON)
    public List<VehicleDTO> getAllVehicles() {
        MDC.put("method", "GET");
        MDC.put("path", "service/vehicle/getall");
        MDC.put("user", "all");
        LOG.info("Received request to get all vehicles");
        try {
            return vehicleService.getAllVehicles();
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to get all vehicles: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to get all vehicles: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to get all vehicles: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to get all vehicles: " + e.getMessage());
        }
    }

    @Get("/view/{filename}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<?> viewImage(@PathVariable String filename) {
        MDC.put("method", "GET");
        MDC.put("path", "service/vehicle/view/" + filename);
        MDC.put("user", filename);
        LOG.info("Received request to view image: {}", filename);
        try {
            return imageStoreService.view(filename);
        } catch (BadRequestException e) {
            MDC.put("status", "400");
            LOG.error("Bad request while trying to view the vehicle image: {}", e.getMessage());
            throw new BadRequestException("Bad request while trying to view the vehicle image: " + e.getMessage());
        } catch (InternalServerException e) {
            MDC.put("status", "500");
            LOG.error("Internal server error while trying to view the vehicle image: {}", e.getMessage());
            throw new InternalServerException("Internal server error while trying to view the vehicle image: " + e.getMessage());
        }
    }
}

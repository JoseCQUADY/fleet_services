package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.VehicleClient;
import com.chiops.gateway.libs.dtos.VehicleDTO;
import com.chiops.gateway.services.VehicleImageEncodingService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;
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
public class VehicleController {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleController.class);
    private final VehicleImageEncodingService vehicleImageEncodingService;
    private final VehicleClient vehicleClient;

    public VehicleController(VehicleImageEncodingService vehicleImageEncodingService,
                             VehicleClient vehicleClient) {
        this.vehicleImageEncodingService = vehicleImageEncodingService;
        this.vehicleClient = vehicleClient;
    }

    @Post(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA)
    public VehicleDTO createVehicle(@Part("vehicle") VehicleDTO vehicle,
                                    @Part("imageFile") CompletedFileUpload imageFile) {
        MDC.put("method", "POST");
        MDC.put("path", "api/vehicle/create");
        MDC.put("user", vehicle.getVin());
        try {
            LOG.info("Received request to create vehicle with VIN: {}", vehicle.getVin());
            VehicleDTO response = vehicleImageEncodingService.createVehicleWithImageEncoded(vehicle, imageFile);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error creating vehicle with VIN: {}", vehicle.getVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Put(value = "/update", consumes = MediaType.APPLICATION_JSON)
    public VehicleDTO updateVehicle(@Body VehicleDTO vehicle) {
        MDC.put("method", "PUT");
        MDC.put("path", "api/vehicle/update");
        MDC.put("user", vehicle.getVin());
        try {
            LOG.info("Received request to update vehicle with VIN: {}", vehicle.getVin());
            VehicleDTO response = vehicleClient.updateVehicle(vehicle);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error updating vehicle with VIN: {}", vehicle.getVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Delete("/delete/{vin}")
    public void deleteVehicle(@PathVariable String vin) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/vehicle/delete/" + vin);
        MDC.put("user", vin);
        try {
            LOG.info("Received request to delete vehicle with VIN: {}", vin);
            vehicleClient.deleteVehicle(vin);
            MDC.put("status", "204");
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error deleting vehicle with VIN: {}", vin, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get("/get/{vin}")
    public VehicleDTO getVehicleByVin(@PathVariable String vin) {
        MDC.put("method", "GET");
        MDC.put("path", "api/vehicle/get/" + vin);
        MDC.put("user", vin);
        try {
            LOG.info("Received request to get vehicle by VIN: {}", vin);
            VehicleDTO response = vehicleClient.getVehicleByVin(vin);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving vehicle by VIN: {}", vin, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get(value = "/model/{model}", consumes = MediaType.APPLICATION_JSON)
    public List<VehicleDTO> getAllVehiclesByModel(@QueryValue String model) {
        MDC.put("method", "GET");
        MDC.put("path", "api/vehicle/model/" + model);
        MDC.put("user", model);
        try {
            LOG.info("Received request to get all vehicles by model: {}", model);
            List<VehicleDTO> response = vehicleClient.getAllVehiclesByModel(model);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving vehicles by model: {}", model, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get(value = "/getall", consumes = MediaType.APPLICATION_JSON)
    public List<VehicleDTO> getAllVehicles() {
        MDC.put("method", "GET");
        MDC.put("path", "api/vehicle/getall");
        MDC.put("user", "all");
        try {
            LOG.info("Received request to get all vehicles");
            List<VehicleDTO> response = vehicleClient.getAllVehicles();
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving all vehicles", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get("/view/{filename}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<?> viewImage(@PathVariable String filename) {
        MDC.put("method", "GET");
        MDC.put("path", "api/vehicle/view/" + filename);
        MDC.put("user", filename);
        try {
            LOG.info("Received request to view image with filename: {}", filename);
            HttpResponse<?> response = vehicleClient.viewImage(filename);
            MDC.put("status", String.valueOf(response.getStatus().getCode()));
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving image: {}", filename, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
}

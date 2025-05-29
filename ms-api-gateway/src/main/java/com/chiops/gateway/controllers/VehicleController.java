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
        LOG.info("Received request to create vehicle with VIN: {}", vehicle.getVin());
        return vehicleImageEncodingService.createVehicleWithImageEncoded(vehicle, imageFile);
    }

    @Put(value = "/update", consumes = MediaType.APPLICATION_JSON)
    public VehicleDTO updateVehicle(@Body VehicleDTO vehicle) {
        LOG.info("Received request to update vehicle with VIN: {}", vehicle.getVin());
        return vehicleClient.updateVehicle(vehicle);
    }

    @Delete("/delete/{vin}")
    public void deleteVehicle(@PathVariable String vin) {
        LOG.info("Received request to delete vehicle with VIN: {}", vin);
        vehicleClient.deleteVehicle(vin);
    }

    @Get("/get/{vin}")
    public VehicleDTO getVehicleByVin(@PathVariable String vin) {
        LOG.info("Received request to get vehicle by VIN: {}", vin);
        return vehicleClient.getVehicleByVin(vin);
    }

    @Get(value = "/model/{model}", consumes = MediaType.APPLICATION_JSON)
    public List<VehicleDTO> getAllVehiclesByModel(@QueryValue String model) {
        LOG.info("Received request to get all vehicles by model: {}", model);
        return vehicleClient.getAllVehiclesByModel(model);
    }

    @Get(value = "/getall", consumes = MediaType.APPLICATION_JSON)
    public List<VehicleDTO> getAllVehicles() {
        LOG.info("Received request to get all vehicles");
        return vehicleClient.getAllVehicles();
    }

    @Get("/view/{filename}")
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<?> viewImage(@PathVariable String filename) {
    LOG.info("Received request to view image with filename: {}", filename);
        return vehicleClient.viewImage(filename);
    }
}

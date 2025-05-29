package com.chiops.vehicle.services;

import com.chiops.vehicle.libs.dtos.VehicleAssignmentDTO;
import com.chiops.vehicle.libs.dtos.VehicleDTO;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.validation.Valid;

import java.util.List;

public interface VehicleService {

    VehicleDTO getVehicleByVin(String vin);

    List<VehicleDTO> getVehiclesByModelName(String model);

    List <VehicleDTO> getAllVehicles();

    VehicleDTO createVehicle(VehicleDTO vehicle, byte[] imageBytes);

    VehicleDTO updateVehicle(VehicleDTO vehicle);

    VehicleDTO deleteVehicle(String vin);


}

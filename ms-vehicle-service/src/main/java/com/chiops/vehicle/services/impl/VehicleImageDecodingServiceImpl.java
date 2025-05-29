package com.chiops.vehicle.services.impl;

import com.chiops.vehicle.libs.dtos.VehicleWithImageDTO;
import com.chiops.vehicle.libs.dtos.VehicleDTO;
import com.chiops.vehicle.libs.exceptions.exception.InternalServerException;
import com.chiops.vehicle.services.VehicleImageDecodingService;
import com.chiops.vehicle.services.VehicleService;

import com.chiops.vehicle.libs.exceptions.exception.*;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

@Singleton
public class VehicleImageDecodingServiceImpl implements VehicleImageDecodingService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleImageDecodingServiceImpl.class);
    private final VehicleService vehicleService;

    public VehicleImageDecodingServiceImpl(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Override
    public VehicleDTO createVehicleFromEncodedImage(VehicleWithImageDTO dto) {
        try {
            VehicleDTO vehicleDto = dto.getVehicle();
            byte[] imageBytes = Base64.getDecoder().decode(dto.getImageBase64());
            return vehicleService.createVehicle(vehicleDto, imageBytes);
        } catch (IllegalArgumentException e) {
            LOG.error("Invalid Base64 image data: {}", e.getMessage(), e); 
            throw new BadRequestException("Invalid Base64 image data: " + e.getMessage());
        } catch (Exception e) {
            LOG.error("Error decoding image: {}", e.getMessage(), e);
            throw new InternalServerException("Error al decodificar la imagen: " + e.getMessage());
        }
    }
}

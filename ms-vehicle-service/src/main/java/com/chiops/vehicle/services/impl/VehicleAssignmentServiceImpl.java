package com.chiops.vehicle.services.impl;

import com.chiops.vehicle.entities.Vehicle;
import com.chiops.vehicle.entities.VehicleAssignment;
import com.chiops.vehicle.libs.clients.DriverClient;
import com.chiops.vehicle.libs.dtos.VehicleAssignmentDTO;
import com.chiops.vehicle.libs.exceptions.exception.*;
import com.chiops.vehicle.repositories.VehicleAssignmentRepository;
import com.chiops.vehicle.repositories.VehicleRepository;
import com.chiops.vehicle.services.VehicleAssignmentService;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class VehicleAssignmentServiceImpl implements VehicleAssignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleAssignmentServiceImpl.class);
    private final VehicleAssignmentRepository vehicleAssignmentRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverClient driverClient;

    public VehicleAssignmentServiceImpl(VehicleAssignmentRepository vehicleAssignmentRepository,
                                      VehicleRepository vehicleRepository, DriverClient driverClient) {
        this.vehicleAssignmentRepository = vehicleAssignmentRepository;
        this.vehicleRepository = vehicleRepository;
        this.driverClient = driverClient;
    }

    @Override
    public List<VehicleAssignmentDTO> findByStatus(String status) {
        if (status == null || status.isBlank()) {
            LOG.error("Status field is obligatory");
            throw new BadRequestException("status field is obligatory");
        }

        List<VehicleAssignment> vehicleAssignments = vehicleAssignmentRepository.findByStatus(status);
        if (vehicleAssignments.isEmpty()) {
            LOG.error("No vehicle assignments found with status: {}", status);
            throw new NotFoundException("No vehicle assignments found with status: " + status);
        }

        LOG.info("Found {} vehicle assignments with status: {}", vehicleAssignments.size(), status);
        return vehicleAssignments.stream()
                .map(this::vehicleAssignmentToDTO)
                .toList();
    }

    @Override
    public List<VehicleAssignmentDTO> assignmentsHistory() {
        List<VehicleAssignment> vehicleAssignments = vehicleAssignmentRepository.findAll();

        LOG.info("Found {} vehicle assignments in history", vehicleAssignments.size());
        return vehicleAssignments.stream()
                .map(this::vehicleAssignmentToDTO)
                .toList();
    }

    @Override
    public VehicleAssignmentDTO findByVin(String vin) {
        if (vin == null || vin.isBlank()) {
            LOG.error("VIN field is obligatory");
            throw new BadRequestException("vin field is obligatory");
        }

        Vehicle vehicle = vehicleRepository.findByVin(vin)
                .orElseThrow(() -> {
                    LOG.error("Vehicle with VIN {} not found", vin);
                    return new NotFoundException("Vehicle with VIN " + vin + " not found");
                });
    
        if (vehicle.getVehicleAssignment() == null) {
            LOG.error("Vehicle with VIN {} is not assigned to any driver", vin);
            throw new ConflictException("Vehicle with VIN " + vin + " is not assigned to any driver");
        }
    
        LOG.info("Found vehicle assignment for VIN: {}", vin);
        return vehicleAssignmentToDTO(vehicle.getVehicleAssignment());
    }
    
    @Override
    public VehicleAssignmentDTO assignVehicleToDriver(VehicleAssignmentDTO vehicleAssignmentDto) {
        if(vehicleAssignmentDto.getVin() == null || vehicleAssignmentDto.getVin().isEmpty()){
            LOG.error("VIN field is obligatory");
            throw new BadRequestException("vin field is obligatory");
        }
        
        if(vehicleAssignmentDto.getDriverCurp() == null || vehicleAssignmentDto.getDriverCurp().isEmpty()){
            LOG.error("CURP field is obligatory");
            throw new BadRequestException("CURP field is obligatory");
        }

        Vehicle vehicle = vehicleRepository.findByVin(vehicleAssignmentDto.getVin())
        .orElseThrow(() -> {
            LOG.error("Vehicle with VIN {} not found", vehicleAssignmentDto.getVin());
            return new NotFoundException("Vehicle with VIN " + vehicleAssignmentDto.getVin() + " not found");
        });


        if (vehicle.getVehicleAssignment() != null && "assigned".equals(vehicle.getVehicleAssignment().getStatus())) {
            LOG.error("Vehicle with VIN {} is already assigned to a driver", vehicleAssignmentDto.getVin());
            throw new ConflictException("Vehicle with VIN " + vehicleAssignmentDto.getVin() + " is already assigned to a driver");
        }

        if (driverClient.getDriverByCurp(vehicleAssignmentDto.getDriverCurp()).isEmpty()) {
            LOG.error("Driver with CURP {} not found", vehicleAssignmentDto.getDriverCurp());
            throw new NotFoundException("Driver with CURP " + vehicleAssignmentDto.getDriverCurp() + " not found");
        }

        if (vehicleAssignmentRepository.findByDriverCurpAndStatus(
            vehicleAssignmentDto.getDriverCurp(), "assigned").isPresent()) {
            LOG.error("Driver with CURP {} is already assigned to a vehicle", vehicleAssignmentDto.getDriverCurp());
            throw new ConflictException("Driver with CURP " + vehicleAssignmentDto.getDriverCurp() + " is already assigned to a vehicle");
        }

        VehicleAssignment vehicleAssignment = new VehicleAssignment();
        vehicleAssignment.setDriverCurp(vehicleAssignmentDto.getDriverCurp());
        vehicleAssignment.setAssignedAt(LocalDateTime.now());
        vehicleAssignment.setVehicle(vehicle);
        vehicleAssignment.setStatus("assigned");
        vehicleAssignment.setReleasedAt(null);

        vehicleAssignmentRepository.save(vehicleAssignment);
        vehicle.setVehicleAssignment(vehicleAssignment);
        vehicleRepository.update(vehicle);

        LOG.info("Vehicle with VIN {} assigned to driver with CURP {}", vehicleAssignmentDto.getVin(), vehicleAssignmentDto.getDriverCurp());
        return vehicleToDTO(vehicle);
    }

    @Override
    public VehicleAssignmentDTO releaseVehicleFromDriver(VehicleAssignmentDTO vehicleAssignmentDto) {
        if(vehicleAssignmentDto.getVin() == null || vehicleAssignmentDto.getVin().isEmpty()){
            LOG.error("VIN field is obligatory");
            throw new BadRequestException("vin field is obligatory");
        }
        
        if(vehicleAssignmentDto.getDriverCurp() == null || vehicleAssignmentDto.getDriverCurp().isEmpty()){
            LOG.error("CURP field is obligatory");
            throw new BadRequestException("CURP field is obligatory");
        }

        Vehicle vehicle = vehicleRepository.findByVin(vehicleAssignmentDto.getVin())
                .orElseThrow(() -> {
                    LOG.error("Vehicle with VIN {} not found", vehicleAssignmentDto.getVin());
                    return new NotFoundException("Vehicle with VIN " + vehicleAssignmentDto.getVin() + " not found");
                });
                
        if (vehicle.getVehicleAssignment() == null) {
            LOG.error("Vehicle with VIN {} is not assigned to any driver", vehicleAssignmentDto.getVin());
            throw new ConflictException("Vehicle with VIN " + vehicleAssignmentDto.getVin() + " is not assigned to any driver");
        }

        if (driverClient.getDriverByCurp(vehicleAssignmentDto.getDriverCurp()).isEmpty()) {
            LOG.error("Driver with CURP {} not found", vehicleAssignmentDto.getDriverCurp());
            throw new NotFoundException("Driver with CURP " + vehicleAssignmentDto.getDriverCurp() + " not found");
        }

        if (!vehicle.getVehicleAssignment().getDriverCurp().equals(vehicleAssignmentDto.getDriverCurp())) {
            LOG.error("Driver CURP {} does not match the assigned driver", vehicleAssignmentDto.getDriverCurp());
            throw new BadRequestException("Driver CURP " + vehicleAssignmentDto.getDriverCurp() + " does not match the assigned driver");
        }

        VehicleAssignment vehicleAssignment = vehicle.getVehicleAssignment();
        vehicleAssignment.setReleasedAt(LocalDateTime.now());
        vehicleAssignment.setStatus("released");

        vehicleAssignmentRepository.update(vehicleAssignment);
        vehicle.setVehicleAssignment(null);
        vehicleRepository.update(vehicle);

        LOG.info("Vehicle with VIN {} released from driver with CURP {}", vehicleAssignmentDto.getVin(), vehicleAssignmentDto.getDriverCurp());
        return vehicleAssignmentToDTO(vehicleAssignment);
    }

    @Override
    public VehicleAssignmentDTO changeDriver(VehicleAssignmentDTO vehicleAssignmentDto) {
        if(vehicleAssignmentDto.getVin() == null || vehicleAssignmentDto.getVin().isEmpty()){
            LOG.error("VIN field is obligatory");
            throw new BadRequestException("vin field is obligatory");
        }
        
        if(vehicleAssignmentDto.getDriverCurp() == null || vehicleAssignmentDto.getDriverCurp().isEmpty()){
            LOG.error("CURP field is obligatory");
            throw new BadRequestException("CURP field is obligatory");
        }

        if(vehicleAssignmentDto.getChangedDriverCurp() == null || vehicleAssignmentDto.getChangedDriverCurp().isEmpty()){
            LOG.error("Changed driver CURP field is obligatory");
            throw new BadRequestException("CURP field is obligatory");
        }

        Vehicle vehicle = vehicleRepository.findByVin(vehicleAssignmentDto.getVin())
                .orElseThrow(() -> {
                    LOG.error("Vehicle with VIN {} not found", vehicleAssignmentDto.getVin());
                    return new NotFoundException("Vehicle with VIN " + vehicleAssignmentDto.getVin() + " not found");
                });

        if (vehicle.getVehicleAssignment() == null) {
            LOG.error("Vehicle with VIN {} is not assigned to any driver", vehicleAssignmentDto.getVin());
            throw new ConflictException("Vehicle with VIN " + vehicleAssignmentDto.getVin() + " is not assigned to any driver");
        }

        if (driverClient.getDriverByCurp(vehicleAssignmentDto.getDriverCurp()).isEmpty()) {
            LOG.error("Driver with CURP {} is incorrect", vehicleAssignmentDto.getDriverCurp());
            throw new BadRequestException("Driver with CURP " + vehicleAssignmentDto.getDriverCurp() + "is incorrect");
        }
        
        if (driverClient.getDriverByCurp(vehicleAssignmentDto.getChangedDriverCurp()).isEmpty()) {
            LOG.error("Changed driver with CURP {} is incorrect", vehicleAssignmentDto.getChangedDriverCurp());
            throw new BadRequestException("Changed driver with CURP " + vehicleAssignmentDto.getChangedDriverCurp() + " is incorrect");
        }

        VehicleAssignment oldVehicleAssignment = vehicleAssignmentRepository
                .findByDriverCurpAndStatus(vehicleAssignmentDto.getDriverCurp(), "assigned")
                .orElseThrow(() -> {
                    LOG.error("Driver with CURP {} is not assigned to any vehicle", vehicleAssignmentDto.getDriverCurp());
                    return new ConflictException("Driver with CURP " + vehicleAssignmentDto.getDriverCurp() + " is not assigned to any vehicle");
                });

        if (oldVehicleAssignment.getDriverCurp().equals(vehicleAssignmentDto.getChangedDriverCurp())
        || vehicleAssignmentRepository.findByDriverCurpAndStatus(vehicleAssignmentDto.getChangedDriverCurp(), "assigned").isPresent()) {
            LOG.error("Driver with CURP {} is already assigned to this vehicle", vehicleAssignmentDto.getChangedDriverCurp());
            throw new ConflictException("Driver with CURP " + vehicleAssignmentDto.getChangedDriverCurp() + " is already assigned to this vehicle");
        }

        oldVehicleAssignment.setReleasedAt(LocalDateTime.now());
        oldVehicleAssignment.setStatus("changed");
        oldVehicleAssignment.setChangedDriverCurp(vehicleAssignmentDto.getChangedDriverCurp());

        vehicleAssignmentRepository.update(oldVehicleAssignment);

        VehicleAssignment newVehicleAssignment = new VehicleAssignment();
        newVehicleAssignment.setAssignedAt(LocalDateTime.now());
        newVehicleAssignment.setDriverCurp(vehicleAssignmentDto.getChangedDriverCurp());
        newVehicleAssignment.setReleasedAt(null);
        newVehicleAssignment.setVehicle(vehicle);
        newVehicleAssignment.setStatus("assigned");

        vehicleAssignmentRepository.save(newVehicleAssignment);
        vehicle.setVehicleAssignment(newVehicleAssignment);
        vehicleRepository.update(vehicle);
        
        LOG.info("Vehicle with VIN {} changed driver from {} to {}", vehicleAssignmentDto.getVin(),
                vehicleAssignmentDto.getDriverCurp(), vehicleAssignmentDto.getChangedDriverCurp());
        return vehicleToDTO(vehicle);
    }

    private VehicleAssignmentDTO vehicleToDTO(Vehicle vehicle) {
        VehicleAssignmentDTO dto = new VehicleAssignmentDTO();
        dto.setDriverCurp(vehicle.getVehicleAssignment().getDriverCurp());
        dto.setAssignedAt(vehicle.getVehicleAssignment().getAssignedAt());
        dto.setReleasedAt(vehicle.getVehicleAssignment().getReleasedAt());
        dto.setStatus(vehicle.getVehicleAssignment().getStatus());
        dto.setChangedDriverCurp(vehicle.getVehicleAssignment().getChangedDriverCurp());
        dto.setVin(vehicle.getVin());
        return dto;
    }

    private VehicleAssignmentDTO vehicleAssignmentToDTO(VehicleAssignment vehicleAssignment) {
        VehicleAssignmentDTO dto = new VehicleAssignmentDTO();
        dto.setDriverCurp(vehicleAssignment.getDriverCurp());
        dto.setVin(vehicleAssignment.getVehicle().getVin());
        dto.setAssignedAt(vehicleAssignment.getAssignedAt());
        dto.setReleasedAt(vehicleAssignment.getReleasedAt());
        dto.setStatus(vehicleAssignment.getStatus());
        dto.setChangedDriverCurp(vehicleAssignment.getChangedDriverCurp());
        return dto;
    }
}
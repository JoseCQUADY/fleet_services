package com.chiops.driver.services.impl;

import com.chiops.driver.entities.Address;
import com.chiops.driver.entities.Driver;
import com.chiops.driver.entities.FullName;
import com.chiops.driver.entities.License;
import com.chiops.driver.libs.dtos.DriverDTO;
import com.chiops.driver.libs.exceptions.exception.*;
import com.chiops.driver.repositories.DriverRepository;
import com.chiops.driver.services.DriverService;
import io.micronaut.transaction.annotation.Transactional;
import io.micronaut.validation.Validated;
import jakarta.inject.Singleton;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Validated
@Singleton
public class DriverServiceImpl implements DriverService {

    private static final Logger LOG = LoggerFactory.getLogger(DriverServiceImpl.class);
    private final DriverRepository driverRepository;

    public DriverServiceImpl(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(this::toDriverDTO)
                .toList();
    }

    @Override
    public DriverDTO getDriverByCurp(String curp) {

        if (curp == null || curp.isBlank()) {
            LOG.error("CURP field is obligatory");
            throw new BadRequestException("CURP field is obligatory");
        }
        Optional<Driver> opt = driverRepository.findByCurp(curp);
        if (opt.isEmpty()) {
            LOG.error("Driver not found with CURP: {}", curp);
            throw new NotFoundException("Driver not found with CURP: " + curp);
        }

        LOG.info("Driver found with CURP: {}", curp);
        return toDriverDTO(opt.get());
    }

    @Override
    @Transactional
    public DriverDTO createDriver(@Valid DriverDTO dto) {
        if (dto.getCurp() == null || dto.getCurp().isBlank()) {
            LOG.error("CURP field is obligatory");
            throw new BadRequestException("CURP field is obligatory");
        }
        

        if (driverRepository.findByCurp(dto.getCurp()).isPresent()) {
            LOG.error("Driver with CURP {} already exists", dto.getCurp());
            throw new ConflictException("Driver with CURP " + dto.getCurp() + " already exists");
        }

        FullName fullName = new FullName(dto.getFirstName(), dto.getLastName());
        Address address = new Address(dto.getStreet(), dto.getCity(), dto.getState());
        License license = new License(dto.getLicenseNumber());

        Driver driver = new Driver(
                fullName,
                dto.getCurp(),
                address,
                dto.getMonthlySalary(),
                license
        );

        LOG.info("Creating new driver with CURP: {}", dto.getCurp());
        return toDriverDTO(driverRepository.save(driver));
    }

    @Override
    public DriverDTO updateDriver(DriverDTO driverDTO) {
        Driver existingDriver = driverRepository.findByCurp(driverDTO.getCurp())
        .orElseThrow(() -> {
            LOG.error("Driver not found with CURP: {}", driverDTO.getCurp());
            return new BadRequestException("CURP cannot be changed, be sure to write the CURP correctly of the driver you want to update");
        });

                
        if (!existingDriver.getCurp().equals(driverDTO.getCurp())) {
            LOG.error("Attempt to change CURP from {} to {}", existingDriver.getCurp(), driverDTO.getCurp());
            throw new BadRequestException("CURP cannot be changed, be sure to write the CURP correctly of the driver you want to update");
        }

        existingDriver.getFullName().setFirstName(driverDTO.getFirstName());
        existingDriver.getFullName().setLastName(driverDTO.getLastName());
        existingDriver.getAddress().setStreet(driverDTO.getStreet());
        existingDriver.getAddress().setCity(driverDTO.getCity());
        existingDriver.getAddress().setState(driverDTO.getState());
        existingDriver.getLicense().setLicenseNumber(driverDTO.getLicenseNumber());
        existingDriver.setMonthlySalary(driverDTO.getMonthlySalary());
        existingDriver.setRegistrationDate(driverDTO.getRegistrationDate());

        driverRepository.update(existingDriver);
        LOG.info("Driver updated with CURP: {}", driverDTO.getCurp());

        return toDriverDTO(existingDriver);
    }

    @Override
    public DriverDTO  deleteDriver(String curp) {
        Optional<Driver> opt = driverRepository.findByCurp(curp);
        if (opt.isEmpty()) {
            LOG.error("Driver not found with CURP: {}", curp);
            throw new BadRequestException("The CURP is incorrect.");
        }
        Driver driver = opt.get();
        driverRepository.delete(driver);
        LOG.info("Driver deleted with CURP: {}", curp);
        return toDriverDTO(driver);
    }

    private DriverDTO toDriverDTO(Driver driver) {
        DriverDTO dto = new DriverDTO();
        dto.setFirstName(driver.getFullName().getFirstName());
        dto.setLastName(driver.getFullName().getLastName());
        dto.setCurp(driver.getCurp());
        dto.setStreet(driver.getAddress().getStreet());
        dto.setCity(driver.getAddress().getCity());
        dto.setState(driver.getAddress().getState());
        dto.setLicenseNumber(driver.getLicense().getLicenseNumber());
        dto.setMonthlySalary(driver.getMonthlySalary());
        dto.setRegistrationDate(driver.getRegistrationDate());
        return dto;
    }
}

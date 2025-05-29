package com.chiops.administrator.controllers;

import com.chiops.administrator.libs.dtos.request.AdministratorRequestDTO;
import com.chiops.administrator.libs.dtos.response.AdministratorResponseDTO;
import com.chiops.administrator.libs.exceptions.exception.BadRequestException;
import com.chiops.administrator.libs.exceptions.exception.InternalServerException;
import com.chiops.administrator.services.AdministratorService;

import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/admin")
@Validated
@Secured(SecurityRule.IS_ANONYMOUS)
public class AdministratorController {

    private static final Logger LOG = LoggerFactory.getLogger(AdministratorController.class);
    private final AdministratorService administratorService;

    public AdministratorController(AdministratorService administratorService) {
        this.administratorService = administratorService;
    }

    @Post("/register")
    public AdministratorResponseDTO createAdministrator(@Valid @Body AdministratorRequestDTO administrator) {
        MDC.put("method", "POST");
        MDC.put("path", "service/admin/register");
        MDC.put("user", administrator.getEmail());
        LOG.info("Received request to register administrator: {}", administrator.getEmail());
        try {
            return administratorService.signUpAdministrator(administrator);
        } catch (BadRequestException e) {
            LOG.error("Bad request while registering administrator: {}", e.getMessage());
            throw new BadRequestException("Bad request while registering administrator: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while registering administrator: {}", e.getMessage());
            throw new InternalServerException("Internal server error while registering administrator: " + e.getMessage());
        }
    }

    @Post("/login")
    public AdministratorResponseDTO signInAdministrator(@Valid @Body AdministratorRequestDTO administrator) {
        MDC.put("method", "POST");
        MDC.put("path", "service/admin/login");
        MDC.put("user", administrator.getEmail());
        LOG.info("Received request to sign in administrator: {}", administrator.getEmail());
        try {
            return administratorService.signInAdministrator(administrator);
        } catch (BadRequestException e) {
            LOG.error("Bad request while signing in administrator: {}", e.getMessage());
            throw new BadRequestException("Bad request while signing in: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while signing in administrator: {}", e.getMessage());
            throw new InternalServerException("Internal server error while signing in: " + e.getMessage());
        }
    }

    @Post("/get/{email}")
    public AdministratorResponseDTO findAdministratorByEmail(@Valid @PathVariable String email) {
        MDC.put("method", "POST");
        MDC.put("path", "service/admin/get/" + email);
        MDC.put("user", email);
        LOG.info("Received request to find administrator by email: {}", email);
        try {
            return administratorService.findAdministratorByEmail(email);
        } catch (BadRequestException e) {
            LOG.error("Bad request while retrieving administrator with email {}: {}", email, e.getMessage());
            throw new BadRequestException("Bad request while retrieving administrator with email " + email + ": " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while retrieving administrator with email {}: {}", email, e.getMessage());
            throw new InternalServerException("Internal server error while retrieving administrator with email " + email + ": " + e.getMessage());
        }
    }

    @Delete("/delete/{email}")
    public void deleteAdministratorByEmail(@Valid @PathVariable String email) {
        MDC.put("method", "DELETE");
        MDC.put("path", "service/admin/delete/" + email);
        MDC.put("user", email);
        LOG.info("Received request to delete administrator with email: {}", email);
        try {
            administratorService.deleteAdministratorByEmail(email);
        } catch (BadRequestException e) {
            LOG.error("Bad request while deleting administrator with email {}: {}", email, e.getMessage());
            throw new BadRequestException("Bad request while deleting administrator with email " + email + ": " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while deleting administrator with email {}: {}", email, e.getMessage());
            throw new InternalServerException("Internal server error while deleting administrator with email " + email + ": " + e.getMessage());
        }
    }

    @Put("/update")
    public AdministratorResponseDTO updateAdministrator(@Valid @Body AdministratorRequestDTO administrator) {
        MDC.put("method", "PUT");
        MDC.put("path", "service/admin/update");
        MDC.put("user", administrator.getEmail());
        LOG.info("Received request to update administrator: {}", administrator.getEmail());
        try {
            return administratorService.updateAdministrator(administrator);
        } catch (BadRequestException e) {
            LOG.error("Bad request while updating administrator: {}", e.getMessage());
            throw new BadRequestException("Bad request while updating administrator: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while updating administrator: {}", e.getMessage());
            throw new InternalServerException("Internal server error while updating administrator: " + e.getMessage());
        }
    }

    @Get("/getall")
    public List<AdministratorResponseDTO> getAdministratorList() {
        MDC.put("method", "GET");
        MDC.put("path", "service/admin/getall");
        MDC.put("user", "all");
        LOG.info("Received request to retrieve all administrators");
        try {
            return administratorService.getAdministratorList();
        } catch (BadRequestException e) {
            LOG.error("Bad request while retrieving administrator list: {}", e.getMessage());
            throw new BadRequestException("Bad request while retrieving administrator list: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while retrieving administrator list: {}", e.getMessage());
            throw new InternalServerException("Internal server error while retrieving administrator list: " + e.getMessage());
        }
    }
}

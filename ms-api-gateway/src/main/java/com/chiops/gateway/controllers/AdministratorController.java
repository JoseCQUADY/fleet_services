package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.AdministratorClient;
import com.chiops.gateway.libs.dtos.request.AdministratorRequestDTO;
import com.chiops.gateway.libs.dtos.response.AdministratorResponseDTO;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.validation.Valid;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/admin")
@Secured(SecurityRule.IS_ANONYMOUS)
public class AdministratorController {

    private static final Logger LOG = LoggerFactory.getLogger(AdministratorController.class);
    private final AdministratorClient adminClient;

    public AdministratorController(AdministratorClient administratorClient) {
        this.adminClient = administratorClient;
    }

    @Post("/register")
    public AdministratorResponseDTO createAdministrator(@Valid @Body AdministratorRequestDTO administrator) {
        MDC.put("method", "POST");
        MDC.put("path", "api/admin/register");
        MDC.put("user", administrator.getEmail());
        LOG.info("Received request to register administrator: {}", administrator.getEmail());
        return adminClient.createAdministrator(administrator);
    }

    @Post("/login")
    public AdministratorResponseDTO signInAdministrator(@Valid @Body AdministratorRequestDTO administrator) {
        MDC.put("method", "POST");
        MDC.put("path", "api/admin/login");
        MDC.put("user", administrator.getEmail());
        LOG.info("Received request to sign in administrator: {}", administrator.getEmail());
        return adminClient.signInAdministrator(administrator);
    }

    @Post("/get/{email}")
    public AdministratorResponseDTO findAdministratorByEmail(@Valid @PathVariable String email) {
        MDC.put("method", "POST");
        MDC.put("path", "api/admin/get/" + email);
        MDC.put("user", email);
        LOG.info("Received request to find administrator by email: {}", email);
        return adminClient.findAdministratorByEmail(email);
    }

    @Delete("/delete/{email}")
    public void deleteAdministratorByEmail(@Valid @PathVariable String email) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/admin/delete/" + email);
        MDC.put("user", email);
        LOG.info("Received request to delete administrator by email: {}", email);
        adminClient.deleteAdministratorByEmail(email);
    }

    @Put("/update")
    public AdministratorResponseDTO updateAdministrator(@Valid @Body AdministratorRequestDTO administrator) {
        MDC.put("method", "PUT");
        MDC.put("path", "api/admin/update");
        MDC.put("user", administrator.getEmail());
        LOG.info("Received request to update administrator: {}", administrator.getEmail());
        return adminClient.updateAdministrator(administrator);
    }

    @Get("/getall")
    public List<AdministratorResponseDTO> getAdministratorList() {
        MDC.put("method", "GET");
        MDC.put("path", "api/admin/getall");
        LOG.info("Received request to get all administrators");
        return adminClient.getAdministratorList();
    }

}

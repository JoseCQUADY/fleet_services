package com.chiops.gateway.controllers;

import java.util.List;

import com.chiops.gateway.libs.clients.InvitationCodeClient;
import com.chiops.gateway.libs.dtos.InvitationCodeDTO;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/code")
@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
public class InvitationCodeController {

    private static final Logger LOG = LoggerFactory.getLogger(InvitationCodeController.class);
    private final InvitationCodeClient invitationCodeClient;

    public InvitationCodeController(InvitationCodeClient invitationCodeClient) {
        this.invitationCodeClient = invitationCodeClient;
    }

    @Get("/get/{code}")
    @Status(HttpStatus.FOUND)
    public InvitationCodeDTO findByCode(@PathVariable String code) {
        LOG.info("Received request to find invitation code: {}", code);
        return invitationCodeClient.findByCode(code);
    }

    @Get("/getall")
    @Status(HttpStatus.OK)
    public List<InvitationCodeDTO> getAllCodes() {
        return invitationCodeClient.getAllCodes();
    }
    @Get("/generate")
    @Status(HttpStatus.CREATED)
    public InvitationCodeDTO generateInvitationCode() {
        LOG.info("Received request to generate a new invitation code");
        return invitationCodeClient.generateInvitationCode();
    }

    @Delete("/delete/{code}")
    @Status(HttpStatus.OK)
    public void deleteByCode(@PathVariable String code) {
        LOG.info("Received request to delete invitation code: {}", code);
        invitationCodeClient.deleteByCode(code);
    }

    @Post("/use/{code}")
    public InvitationCodeDTO markAsUsed(@PathVariable String code) {
        LOG.info("Received request to mark invitation code as used: {}", code);
        return invitationCodeClient.markAsUsed(code);
    }

}

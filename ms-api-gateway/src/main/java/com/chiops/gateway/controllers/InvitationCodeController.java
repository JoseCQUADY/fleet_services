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
import org.slf4j.MDC;


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
        MDC.put("method", "GET");
        MDC.put("path", "api/code/get/" + code);
        MDC.put("user", code);
        LOG.info("Received request to find invitation code: {}", code);
        return invitationCodeClient.findByCode(code);
    }

    @Get("/getall")
    @Status(HttpStatus.OK)
    public List<InvitationCodeDTO> getAllCodes() {
        MDC.put("method", "GET");
        MDC.put("path", "api/code/getall");
        LOG.info("Received request to get all invitation codes");
        return invitationCodeClient.getAllCodes();
    }
    @Get("/generate")
    @Status(HttpStatus.CREATED)
    public InvitationCodeDTO generateInvitationCode() {
        MDC.put("method", "GET");
        MDC.put("path", "api/code/generate");
        LOG.info("Received request to generate a new invitation code");
        return invitationCodeClient.generateInvitationCode();
    }

    @Delete("/delete/{code}")
    @Status(HttpStatus.OK)
    public void deleteByCode(@PathVariable String code) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/code/delete/" + code);
        MDC.put("user", code);
        LOG.info("Received request to delete invitation code: {}", code);
        invitationCodeClient.deleteByCode(code);
    }

    @Post("/use/{code}")
    public InvitationCodeDTO markAsUsed(@PathVariable String code) {
        MDC.put("method", "POST");
        MDC.put("path", "api/code/use/" + code);
        MDC.put("user", code);
        LOG.info("Received request to mark invitation code as used: {}", code);
        return invitationCodeClient.markAsUsed(code);
    }

}

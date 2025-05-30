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
        try {
            LOG.info("Received request to find invitation code: {}", code);
            InvitationCodeDTO response = invitationCodeClient.findByCode(code);
            MDC.put("status", "302");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error finding invitation code: {}", code, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get("/getall")
    @Status(HttpStatus.OK)
    public List<InvitationCodeDTO> getAllCodes() {
        MDC.put("method", "GET");
        MDC.put("path", "api/code/getall");
        try {
            LOG.info("Received request to get all invitation codes");
            List<InvitationCodeDTO> response = invitationCodeClient.getAllCodes();
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving all invitation codes", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
    @Get("/generate")
    @Status(HttpStatus.CREATED)
    public InvitationCodeDTO generateInvitationCode() {
        MDC.put("method", "GET");
        MDC.put("path", "api/code/generate");
        try {
            LOG.info("Received request to generate a new invitation code");
            InvitationCodeDTO response = invitationCodeClient.generateInvitationCode();
            MDC.put("status", "201");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error generating invitation code", e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Delete("/delete/{code}")
    @Status(HttpStatus.OK)
    public void deleteByCode(@PathVariable String code) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/code/delete/" + code);
        MDC.put("user", code);
        try {
            LOG.info("Received request to delete invitation code: {}", code);
            invitationCodeClient.deleteByCode(code);
            MDC.put("status", "200");
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error deleting invitation code: {}", code, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Post("/use/{code}")
    public InvitationCodeDTO markAsUsed(@PathVariable String code) {
        MDC.put("method", "POST");
        MDC.put("path", "api/code/use/" + code);
        MDC.put("user", code);
        try {
            LOG.info("Received request to mark invitation code as used: {}", code);
            InvitationCodeDTO response = invitationCodeClient.markAsUsed(code);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error marking invitation code as used: {}", code, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

}

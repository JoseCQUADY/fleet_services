package com.chiops.invitation.controllers;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;

import com.chiops.invitation.libs.dto.InvitationCodeDTO;
import com.chiops.invitation.libs.exceptions.entities.ErrorResponse;
import com.chiops.invitation.libs.exceptions.exception.BadRequestException;
import com.chiops.invitation.libs.exceptions.exception.InternalServerException;
import com.chiops.invitation.libs.exceptions.exception.MethodNotAllowedException;
import com.chiops.invitation.libs.exceptions.exception.NotFoundException;
import com.chiops.invitation.services.InvitationCodeService;

import java.util.List;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.scheduling.TaskExecutors;
import jakarta.validation.Valid;
import io.micronaut.validation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/code")
@Validated
public class InvitationCodeController {

    private static final Logger LOG = LoggerFactory.getLogger(InvitationCodeController.class);
    private final InvitationCodeService invitationCodeSevice;

    public InvitationCodeController(InvitationCodeService invitationCodeSevice) {
        this.invitationCodeSevice = invitationCodeSevice;
    }

    @Get("/get/{code}")
    @Status(HttpStatus.FOUND)
    public InvitationCodeDTO findByCode(@PathVariable String code) {
        LOG.info("Received request to find invitation code: {}", code);
        try {
            return invitationCodeSevice.findByCode(code);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to find the code: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al buscar el código: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to find the code: {}", e.getMessage());
            throw new InternalServerException("Error interno al buscar el código: " + e.getMessage());
        }
    }
    
    @Get("/getall")
    @Status(HttpStatus.OK)
    public List<InvitationCodeDTO> getAllCodes() {
        try {
            return invitationCodeSevice.getAllCodes();
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al obtener todos los códigos: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al obtener todos los códigos: " + e.getMessage());
        }
    }

    @Get("/generate")
    @Status(HttpStatus.CREATED)
    public InvitationCodeDTO generateInvitationCode() {
    LOG.info("Received request to generate a new invitation code");
        try {
            return invitationCodeSevice.generateCode();
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to generate an invitation code: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al generar un código de invitación: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to generate an invitation code: {}", e.getMessage());
            throw new InternalServerException("Error interno al generar un código de invitación: " + e.getMessage());
        }
    }

    @Delete("/delete/{code}")
    @Status(HttpStatus.OK)
    public void deleteByCode(@PathVariable String code) {
    LOG.info("Received request to delete invitation code: {}", code);
        try {
            invitationCodeSevice.deleteByCode(code);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to delete the code: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al eliminar el código: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to delete the code: {}", e.getMessage());
            throw new InternalServerException("Error interno al eliminar el código: " + e.getMessage());
        }
    }

    @Post("/use/{code}")
    public InvitationCodeDTO markAsUsed(@PathVariable String code) {
    LOG.info("Received request to mark invitation code as used: {}", code);
        try {
            return invitationCodeSevice.markAsUsed(code);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to mark the code as used: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al marcar el código como usado: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to mark the code as used: {}", e.getMessage());
            throw new InternalServerException("Error interno al marcar el código como usado: " + e.getMessage());
        }
    }
}

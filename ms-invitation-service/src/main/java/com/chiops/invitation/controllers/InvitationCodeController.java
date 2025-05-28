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

@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/code")
@Validated
public class InvitationCodeController {

    private final InvitationCodeService invitationCodeSevice;

    public InvitationCodeController(InvitationCodeService invitationCodeSevice) {
        this.invitationCodeSevice = invitationCodeSevice;
    }

    @Get("/get/{code}")
    @Status(HttpStatus.FOUND)
    public InvitationCodeDTO findByCode(@PathVariable String code) {
        try {
            return invitationCodeSevice.findByCode(code);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al buscar el código: " + e.getMessage());
        } catch (InternalServerException e) {
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
        try {
            return invitationCodeSevice.generateCode();
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al generar un código de invitación: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al generar un código de invitación: " + e.getMessage());
        }
    }

    @Delete("/delete/{code}")
    @Status(HttpStatus.OK)
    public void deleteByCode(@PathVariable String code) {
        try {
            invitationCodeSevice.deleteByCode(code);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al eliminar el código: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al eliminar el código: " + e.getMessage());
        }
    }

    @Post("/use/{code}")
    public InvitationCodeDTO markAsUsed(@PathVariable String code) {
        try {
            return invitationCodeSevice.markAsUsed(code);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al marcar el código como usado: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al marcar el código como usado: " + e.getMessage());
        }
    }
    
}

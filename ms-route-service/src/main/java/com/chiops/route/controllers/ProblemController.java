package com.chiops.route.controllers;

import com.chiops.route.libs.dtos.ProblemDTO;
import com.chiops.route.libs.exceptions.entities.ErrorResponse;
import com.chiops.route.libs.exceptions.exception.BadRequestException;
import com.chiops.route.libs.exceptions.exception.InternalServerException;
import com.chiops.route.libs.exceptions.exception.MethodNotAllowedException;
import com.chiops.route.libs.exceptions.exception.NotFoundException;
import com.chiops.route.services.ProblemService;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.annotation.Error;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller("/route/problem")
public class ProblemController {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemController.class);
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Post("/assign")
    public ProblemDTO assignProblem(@Body ProblemDTO problem) {
        LOG.info("Received request to assign problem: {}", problem);
        try {
            return problemService.assignProblem(problem);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to assign the problem: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al asignar el problema: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to assign the problem: {}", e.getMessage());
            throw new InternalServerException("Error interno al asignar el problema: " + e.getMessage());
        }
    }

    @Put("/update")
    public ProblemDTO updateProblem(@Body ProblemDTO problem) {
        LOG.info("Received request to update problem: {}", problem);
        try {
            return problemService.updateProblem(problem);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to update the problem: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al actualizar el problema: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to update the problem: {}", e.getMessage());
            throw new InternalServerException("Error interno al actualizar el problema: " + e.getMessage());
        }
    }

    @Delete("/delete/{vin}")
    public void deleteProblem(@PathVariable String vin) {
        LOG.info("Received request to delete problem with VIN: {}", vin);
        try {
            problemService.deleteProblem(vin);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to delete the problem: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al eliminar el problema: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to delete the problem: {}", e.getMessage());
            throw new InternalServerException("Error interno al eliminar el problema: " + e.getMessage());
        }
    }

    @Get("/get/{vin}")
    public ProblemDTO getProblemById(@PathVariable String vin) {
        LOG.info("Received request to get problem by VIN: {}", vin);
        try {
            return problemService.getProblemById(vin);
        } catch (BadRequestException e) {
            LOG.error("Bad request while trying to get the problem: {}", e.getMessage());
            throw new BadRequestException("Error de solicitud al obtener el problema: " + e.getMessage());
        } catch (InternalServerException e) {
            LOG.error("Internal server error while trying to get the problem: {}", e.getMessage());
            throw new InternalServerException("Error interno al obtener el problema: " + e.getMessage());
        }
    }
}

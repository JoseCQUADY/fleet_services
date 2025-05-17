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

@Controller("/route/problem")
public class ProblemController {

    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @Post("/assign")
    public ProblemDTO assignProblem(@Body ProblemDTO problem) {
        try {
            return problemService.assignProblem(problem);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al asignar el problema: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al asignar el problema: " + e.getMessage());
        }
    }

    @Put("/update")
    public ProblemDTO updateProblem(@Body ProblemDTO problem) {
        try {
            return problemService.updateProblem(problem);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al actualizar el problema: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al actualizar el problema: " + e.getMessage());
        }
    }

    @Delete("/delete/{vin}")
    public void deleteProblem(@PathVariable String vin) {
        try {
            problemService.deleteProblem(vin);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al eliminar el problema: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al eliminar el problema: " + e.getMessage());
        }
    }

    @Get("/get/{vin}")
    public ProblemDTO getProblemById(@PathVariable String vin) {
        try {
            return problemService.getProblemById(vin);
        } catch (BadRequestException e) {
            throw new BadRequestException("Error de solicitud al obtener el problema: " + e.getMessage());
        } catch (InternalServerException e) {
            throw new InternalServerException("Error interno al obtener el problema: " + e.getMessage());
        }
    }
}

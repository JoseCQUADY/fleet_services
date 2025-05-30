package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.ProblemClient;
import com.chiops.gateway.libs.dtos.ProblemDTO;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


@ExecuteOn(TaskExecutors.BLOCKING)
@Controller("/route/problem")
public class ProblemController {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemController.class);
    private final ProblemClient problemClient;

    public ProblemController(ProblemClient problemClient) {
        this.problemClient = problemClient;
    }

    @Post("/assign")
    public ProblemDTO assignProblem(@Body ProblemDTO problem) {
        MDC.put("method", "POST");
        MDC.put("path", "api/route/problem/assign");
        MDC.put("user", problem.getVin());
        try {
            LOG.info("Received request to assign problem with VIN: {}", problem.getVin());
            ProblemDTO response = problemClient.assignProblem(problem);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error assigning problem with VIN: {}", problem.getVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Put("/update")
    public ProblemDTO updateProblem(@Body ProblemDTO problem) {
        MDC.put("method", "PUT");
        MDC.put("path", "api/route/problem/update");
        MDC.put("user", problem.getVin());
        try {
            LOG.info("Received request to update problem with VIN: {}", problem.getVin());
            ProblemDTO response = problemClient.updateProblem(problem);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error updating problem with VIN: {}", problem.getVin(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Delete("/delete/{vin}")
    public void deleteProblem(@PathVariable String vin) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/route/problem/delete/" + vin);
        MDC.put("user", vin);
        try {
            LOG.info("Received request to delete problem with VIN: {}", vin);
            problemClient.deleteProblem(vin);
            MDC.put("status", "200");
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error deleting problem with VIN: {}", vin, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    @Get("/get/{vin}")
    public ProblemDTO getProblemById(@PathVariable String vin) {
        MDC.put("method", "GET");
        MDC.put("path", "api/route/problem/get/" + vin);
        MDC.put("user", vin);
        try {
            LOG.info("Received request to get problem by VIN: {}", vin);
            ProblemDTO response = problemClient.getProblemById(vin);
            MDC.put("status", "200");
            return response;
        } catch (Exception e) {
            MDC.put("status", "500");
            LOG.error("Error retrieving problem with VIN: {}", vin, e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

}
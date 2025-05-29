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
        LOG.info("Received request to assign problem with VIN: {}", problem.getVin());
        return problemClient.assignProblem(problem);
    }

    @Put("/update")
    public ProblemDTO updateProblem(@Body ProblemDTO problem) {
        MDC.put("method", "PUT");
        MDC.put("path", "api/route/problem/update");
        MDC.put("user", problem.getVin());
        LOG.info("Received request to update problem with VIN: {}", problem.getVin());
        return problemClient.updateProblem(problem);
    }

    @Delete("/delete/{vin}")
    public void deleteProblem(@PathVariable String vin) {
        MDC.put("method", "DELETE");
        MDC.put("path", "api/route/problem/delete/" + vin);
        MDC.put("user", vin);
        LOG.info("Received request to delete problem with VIN: {}", vin);
        problemClient.deleteProblem(vin);
    }

    @Get("/get/{vin}")
    public ProblemDTO getProblemById(@PathVariable String vin) {
        MDC.put("method", "GET");
        MDC.put("path", "api/route/problem/get/" + vin);
        MDC.put("user", vin);
        LOG.info("Received request to get problem by VIN: {}", vin);
        return problemClient.getProblemById(vin);
    }

}
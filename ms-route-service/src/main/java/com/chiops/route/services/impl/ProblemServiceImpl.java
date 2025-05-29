package com.chiops.route.services.impl;

import com.chiops.route.entities.Problem;
import com.chiops.route.entities.Route;
import com.chiops.route.libs.dtos.ProblemDTO;
import com.chiops.route.libs.exceptions.exception.*;
import com.chiops.route.repositories.ProblemRepository;
import com.chiops.route.repositories.RoutesRepository;
import com.chiops.route.services.ProblemService;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class ProblemServiceImpl implements ProblemService {

    private static final Logger LOG = LoggerFactory.getLogger(ProblemServiceImpl.class);
    private final ProblemRepository problemRepository;
    private final RoutesRepository routeRepository;

    public ProblemServiceImpl(ProblemRepository problemRepository, RoutesRepository routeRepository) {
        this.problemRepository = problemRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public ProblemDTO assignProblem(ProblemDTO problem) {
        if(problem.getName().isEmpty() || problem.getDescription().isEmpty() || problem.getVin().isEmpty()){
            LOG.error("The field of Name, description and vin are OBLIGATORY");
            throw new BadRequestException("The field of Name, description and vin are OBLIGATORY");
        }
        Route route = routeRepository.findByVehicleVin(problem.getVin())
                .orElseThrow(() -> {
                    LOG.error("Route not found for this vehicle, the vin filed is incorrect: {}", problem.getVin());
                    return new BadRequestException("Route not found for this vehicle, the vin filed is incorrect: " + problem.getVin());
                });

        if (route.getProblem() != null) {
            LOG.error("Problem already exists for this route: {}", problem.getVin());
            throw new ConflictException("Problem already exists for this route: " + problem.getVin());
        }

        Problem problemEntity = new Problem();
        problemEntity.setDescription(problem.getDescription());
        problemEntity.setComment(problem.getComment());
        problemEntity.setName(problem.getName());

        Problem savedProblem = problemRepository.save(problemEntity);

        route.setProblem(savedProblem);
        routeRepository.update(route);

        LOG.info("Problem assigned to route with VIN: {}", problem.getVin());
        return problemToDto(savedProblem);
    }

    @Override
    public ProblemDTO updateProblem(ProblemDTO problem) {
        if(problem.getName().isEmpty() || problem.getDescription().isEmpty() || problem.getVin().isEmpty()){
            LOG.error("The field of Name, description and vin are OBLIGATORY");
            throw new BadRequestException("The field of Name, description and vin are OBLIGATORY");
        }
        if (routeRepository.findByVehicleVin(problem.getVin()).isEmpty()) {
            LOG.error("Route not found for this vehicle, the vin filed is incorrect: {}", problem.getVin());
            throw new BadRequestException("Route not found for this vehicle, the vin filed is incorrect: " + problem.getVin());
        }

        Problem problemEntity = new Problem();
        problemEntity.setDescription(problem.getDescription());
        problemEntity.setComment(problem.getComment());
        problemEntity.setName(problem.getName());

        LOG.info("Updating problem for route with VIN: {}", problem.getVin());
        return problemToDto(problemRepository.update(problemEntity));
    }

    @Override
    public void deleteProblem(String vin) {

        if (vin == null || vin.isBlank()) {
            LOG.error("vin as parameter is obligatory");
            throw new BadRequestException("vin as parameter is obligatory");
        }

    
        Route route = routeRepository.findByVehicleVin(vin)
                .orElseThrow(() -> {
                    LOG.error("Route not found for this vehicle, the vin filed is incorrect: {}", vin);
                    return new BadRequestException("Route not found for this vehicle, the vin filed is incorrect: " + vin);
                });
        
        if (route.getProblem() == null) {
            LOG.error("No problem assigned to this route: {}", vin);
            throw new ConflictException("No problem assigned to this route: " + vin);
        }
        
        Problem problem = route.getProblem();
        route.setProblem(null);
        routeRepository.update(route);
        problemRepository.delete(problem);

        LOG.info("Problem deleted for route with VIN: {}", vin);
    }

    @Override
    public ProblemDTO getProblemById(String vin) {
        if (vin == null || vin.isBlank()) {
            LOG.error("vin as parameter is obligatory");
            throw new BadRequestException("vin as parameter is obligatory");
        }

        Route route = routeRepository.findByVehicleVin(vin)
                .orElseThrow(() -> {
                    LOG.error("Route not found for this vehicle, the vin filed is incorrect: {}", vin);
                    return new BadRequestException("Route not found for this vehicle, the vin filed is incorrect: " + vin);
                });
        
        if (route.getProblem() == null) {
            LOG.error("No problem assigned to this route: {}", vin);
            throw new NotFoundException("No problem assigned to this route: "+ vin);
        }
        
        return problemToDto(route.getProblem());
    }

    private ProblemDTO problemToDto(Problem problem) {
        ProblemDTO problemDTO = new ProblemDTO();
        problemDTO.setDescription(problem.getDescription());
        problemDTO.setComment(problem.getComment());
        problemDTO.setName(problem.getName());
        return problemDTO;
    }
}
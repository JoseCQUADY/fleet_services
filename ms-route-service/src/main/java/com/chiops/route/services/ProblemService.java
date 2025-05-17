package com.chiops.route.services;

import com.chiops.route.entities.Problem;
import com.chiops.route.libs.dtos.ProblemDTO;

import jakarta.validation.Valid;


public interface ProblemService {

    ProblemDTO assignProblem(@Valid  ProblemDTO problem);

    ProblemDTO updateProblem(@Valid ProblemDTO problem);

    void deleteProblem(String vin);

    ProblemDTO getProblemById(String vin);
}

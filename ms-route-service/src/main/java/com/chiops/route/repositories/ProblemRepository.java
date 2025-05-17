package com.chiops.route.repositories;

import com.chiops.route.entities.Problem;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface ProblemRepository extends CrudRepository<Problem, Integer> {

}

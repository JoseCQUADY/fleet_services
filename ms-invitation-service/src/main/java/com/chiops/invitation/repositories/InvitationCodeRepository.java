package com.chiops.invitation.repositories;

import com.chiops.invitation.entities.InvitationCode;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;


@Repository
public interface InvitationCodeRepository extends CrudRepository<InvitationCode, Long> {
   Optional<InvitationCode> findByCode(String code);
}

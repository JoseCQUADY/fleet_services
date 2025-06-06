package com.chiops.invitation.services;

import com.chiops.invitation.libs.dto.InvitationCodeDTO;

import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;

import java.util.List;

@Singleton
public interface InvitationCodeService {

    InvitationCodeDTO generateCode();

    InvitationCodeDTO findByCode(String code);

    InvitationCodeDTO deleteByCode(String code);

    InvitationCodeDTO markAsUsed(String code);

    List<InvitationCodeDTO> getAllCodes();
}

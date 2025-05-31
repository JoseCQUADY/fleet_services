package com.chiops.invitation.services.impl;

import java.security.SecureRandom;
import java.util.stream.Collectors;

import com.chiops.invitation.entities.InvitationCode;
import com.chiops.invitation.libs.dto.InvitationCodeDTO;
import com.chiops.invitation.repositories.InvitationCodeRepository;
import com.chiops.invitation.services.InvitationCodeService;
import com.chiops.invitation.libs.exceptions.exception.*;
import io.micronaut.http.HttpResponse;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Singleton;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class InvitationCodeImpl implements InvitationCodeService {

    private static final Logger LOG = LoggerFactory.getLogger(InvitationCodeImpl.class);
    private static final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int codeLength = 10;
    private final InvitationCodeRepository invitationCodeRepository;

    public InvitationCodeImpl(InvitationCodeRepository invitationCodeRepository) {
        this.invitationCodeRepository = invitationCodeRepository;
    }

    @Override
    @Transactional
    public InvitationCodeDTO generateCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder code = new StringBuilder(codeLength);
        for (int i = 0; i < codeLength; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            code.append(characters.charAt(randomIndex));
        }
        String generatedCode = code.toString();
        InvitationCode invitationCode = new InvitationCode("unassigned", generatedCode);

        invitationCodeRepository.save(invitationCode);

        LOG.info("Generated new invitation code: {}", generatedCode);
        return toDTO(invitationCode);
    }

    @Override
    @Transactional
    public InvitationCodeDTO findByCode(String code) {
        if (code == null || code.isBlank()) {
            LOG.error("A code as parameter is obligatory");
            throw new BadRequestException("A code as parameter is obligatory");
        }

        InvitationCode invitation = invitationCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    LOG.error("The code is incorrect: {}", code);
                    return new BadRequestException("The code is incorrect: " + code);
                });

        LOG.info("Found invitation code: {}", code);
        return toDTO(invitation);
    }

    @Override
    public List<InvitationCodeDTO> getAllCodes() {
        try {
            List<InvitationCode> codes = (List<InvitationCode>) invitationCodeRepository.findAll();
            return codes.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
        } catch (Exception e) {
            throw new InternalServerException("Error al obtener todos los códigos: " + e.getMessage());
        }
    }

    @Override
    public InvitationCodeDTO deleteByCode(String code) {
 
        if (code == null || code.isBlank()) {
            LOG.error("A code as parameter is obligatory");
            throw new BadRequestException("A code as parameter is obligatory");
        }

        InvitationCode invitation = invitationCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    LOG.error("The code is incorrect: {}", code);
                    return new BadRequestException("The code is incorrect: " + code);
                });

        invitationCodeRepository.delete(invitation);
        LOG.info("Deleted invitation code: {}", code);
        return toDTO(invitation);
    }

    @Override
    public InvitationCodeDTO markAsUsed(String code) {
        if (code == null || code.isBlank()) {
            LOG.error("A code as parameter is obligatory");
            throw new BadRequestException("A code as parameter is obligatory");
        }

        InvitationCode invitation = invitationCodeRepository.findByCode(code)
                .orElseThrow(() -> {
                    LOG.error("The code is incorrect: {}", code);
                    return new BadRequestException("The code is incorrect: " + code);
                });

        if (invitation.getStatus().equals("assigned")) {
            LOG.error("Invitation code already used: {}", code);
            throw new ConflictException("Invitation code already used");
        }else{
            invitation.setStatus("assigned");
        }
        invitationCodeRepository.update(invitation);
        LOG.info("Marked invitation code as used: {}", code);

        return toDTO(invitation);
    }

    public InvitationCodeDTO toDTO(InvitationCode invitationCode) {
        return new InvitationCodeDTO(invitationCode.getStatus(), invitationCode.getCode());
    }
}
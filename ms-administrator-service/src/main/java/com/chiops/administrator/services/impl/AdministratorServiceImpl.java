package com.chiops.administrator.services.impl;

import com.chiops.administrator.entities.Administrator;
import com.chiops.administrator.libs.exceptions.exception.*;
import com.chiops.administrator.libs.clients.InvitationCodeClient;
import com.chiops.administrator.libs.dtos.request.AdministratorRequestDTO;
import com.chiops.administrator.libs.dtos.response.AdministratorResponseDTO;
import com.chiops.administrator.repositories.AdministratorRepository;
import com.chiops.administrator.services.AdministratorService;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class AdministratorServiceImpl implements AdministratorService {

    private static final Logger LOG = LoggerFactory.getLogger(AdministratorServiceImpl.class);
    private final InvitationCodeClient invitationCodeClient;
    private final AdministratorRepository administratorRepository;

    public AdministratorServiceImpl(
            InvitationCodeClient invitationCodeClient, 
            AdministratorRepository administratorRepository) {
        this.invitationCodeClient = invitationCodeClient;
        this.administratorRepository = administratorRepository;
    }

    @Override
    public AdministratorResponseDTO signUpAdministrator(AdministratorRequestDTO administrator) {
        
        if(administrator.getEmail().isEmpty() || administrator.getInvitationCode().isEmpty() || administrator.getPassword().isEmpty()){
            LOG.error("Administrator registration failed: Missing required fields");
            throw new BadRequestException("The field of email, password and invitation code are OBLIGATORY");
        }

        if(!administrator.getEmail().endsWith(".com")){
            LOG.error("Administrator registration failed: Invalid email format");
            throw new BadRequestException("The field of email is incorrect, write a valid email");
        }

        if(invitationCodeClient.findByCode(administrator.getInvitationCode()).isEmpty()) {
            LOG.error("Administrator registration failed: Invalid invitation code {}", administrator.getInvitationCode());
            throw new BadRequestException("Bad invitation code " + administrator.getInvitationCode() + "be sure to write the correct invitation code.");
        }

        if (administratorRepository.findByEmail(administrator.getEmail()).isPresent()) {
            LOG.error("Administrator registration failed: Email {} already exists", administrator.getEmail());
            throw new ConflictException("Email " + administrator.getEmail() + " already exists");
        }

        if (administratorRepository.findByInvitationCode(administrator.getInvitationCode()).isPresent()) {
            LOG.error("Administrator registration failed: Invitation code {} already used", administrator.getInvitationCode());
            throw new ConflictException("Invitation code " + administrator.getInvitationCode() + " already used");
        }

        invitationCodeClient.markAsUsed(administrator.getInvitationCode());

        Administrator entity = toEntity(administrator);
        entity.setInvitationCode(administrator.getInvitationCode());

        return toResponseDTO(administratorRepository.save(entity));
    }

    @Override
    public AdministratorResponseDTO findAdministratorByEmail(String email) {
        if (email== null || email.isBlank()){
            LOG.error("Email parameter is missing in request");
            throw new BadRequestException("Email as parameter is obligatory");
        }
        Administrator administrator = administratorRepository.findByEmail(email)
                .orElseThrow(() -> {
                    LOG.error("Administrator with email {} not found", email);
                    return new NotFoundException("Administrator with email " + email + " not found");
                });

        return toResponseDTO(administrator);
    }

    @Override
    public AdministratorResponseDTO signInAdministrator(AdministratorRequestDTO administrator) {

        if(administrator.getEmail().isEmpty() || administrator.getInvitationCode().isEmpty() || administrator.getPassword().isEmpty()){
            LOG.error("Administrator sign-in failed: Missing required fields");
            throw new BadRequestException("The field of email, password and invitation code are OBLIGATORY");
        }

        if(!administrator.getEmail().endsWith(".com")){
            LOG.error("Administrator sign-in failed: Invalid email format");
            throw new BadRequestException("The field of email is incorrect, write a valid email");
        }

        Administrator existingAdministrator = administratorRepository.findByEmail(administrator.getEmail())
                .orElseThrow(() -> {
                    LOG.error("Administrator with email {} not found", administrator.getEmail());
                    return new BadRequestException("Invalid email " + administrator.getEmail());
                });

        if (!existingAdministrator.getPassword().equals(administrator.getPassword())
            || !existingAdministrator.getInvitationCode().equals(administrator.getInvitationCode())) {
            LOG.error("Invalid credentials for administrator {}", administrator.getEmail());
            throw new BadRequestException("Invalid credentials for administrator " + administrator.getEmail());
        }

        if (existingAdministrator.getInvitationCode() == null) {
            LOG.error("No invitation code associated with administrator {}", existingAdministrator.getEmail());
            throw new ConflictException("No invitation code associated with administrator " + administrator.getEmail());
        }

        return toResponseDTO(existingAdministrator);
    }
    @Override
    public AdministratorResponseDTO deleteAdministratorByEmail(String email) {
        if (email == null || email.isBlank()) {
            LOG.error("Email parameter is missing in request");
            throw new BadRequestException("Email as parameter is obligatory");
        }

        Administrator existing = administratorRepository.findByEmail(email)
            .orElseThrow(() -> {
                LOG.error("Administrator with email {} not found", email);
                return new BadRequestException("The email parameter is incorrect");
            });

        administratorRepository.delete(existing);
        LOG.info("Administrator with email {} deleted successfully", email);
        return toResponseDTO(existing);
    }
        

    @Override
    public AdministratorResponseDTO updateAdministrator(AdministratorRequestDTO administrator) {
        
        if(administrator.getEmail().isEmpty() || administrator.getInvitationCode().isEmpty() || administrator.getPassword().isEmpty()){
            LOG.error("Administrator update failed: Missing required fields");
            throw new BadRequestException("The field of email, password and invitation code are OBLIGATORY");
        }

        if(!administrator.getEmail().endsWith(".com")){
            LOG.error("Administrator update failed: Invalid email format");
            throw new BadRequestException("The field of email is incorrect, write a valid email");
        }
        Administrator existingAdministrator = administratorRepository.findByInvitationCode(administrator.getInvitationCode())
        .orElseThrow(() -> {
            LOG.error("Administrator with invitation code {} not found", administrator.getInvitationCode());
            return new BadRequestException("InvitationCode cannot be changed, be sure to write the correct InvitationCode correctly");
        });


        if (!existingAdministrator.getInvitationCode().equals(administrator.getInvitationCode())){
            LOG.error("Invitation code {} does not match the existing administrator's invitation code", administrator.getInvitationCode());
            throw new BadRequestException("InvitationCode cannot be changed, it should not be different from the administrator previous register");
        }

        existingAdministrator.setEmail(administrator.getEmail());
        existingAdministrator.setPassword(administrator.getPassword());
        existingAdministrator.setInvitationCode(administrator.getInvitationCode());

        LOG.info("Updating administrator with email: {}", existingAdministrator.getEmail());
        return toResponseDTO(administratorRepository.update(existingAdministrator));
    }

    @Override
    public List<AdministratorResponseDTO> getAdministratorList() {
        List<Administrator> admins = administratorRepository.findAll();
        LOG.info("Retrieved {} administrators from the database", admins.size());
        return admins.stream().map(this::toResponseDTO).collect(Collectors.toList());
    }

    private Administrator toEntity(AdministratorRequestDTO dto) {
        Administrator admin = new Administrator();
        admin.setEmail(dto.getEmail());
        admin.setPassword(dto.getPassword());
        admin.setInvitationCode(dto.getInvitationCode());
        return admin;
    }

    private AdministratorResponseDTO toResponseDTO(Administrator admin) {
        AdministratorResponseDTO dto = new AdministratorResponseDTO();
        dto.setEmail(admin.getEmail());
        dto.setInvitationCode(admin.getInvitationCode());
        return dto;
    }
}
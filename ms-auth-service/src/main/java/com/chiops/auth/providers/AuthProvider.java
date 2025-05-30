package com.chiops.auth.providers;

import com.chiops.auth.libs.dtos.response.AdministratorResponseDTO;
import com.chiops.auth.libs.exceptions.exception.BadRequestException;
import com.chiops.auth.libs.exceptions.exception.ConflictException;
import com.chiops.auth.libs.exceptions.exception.InternalServerException;
import com.chiops.auth.libs.exceptions.exception.NotAuthenticatedException;
import com.chiops.auth.libs.exceptions.exception.NotFoundException;
import com.chiops.auth.libs.clients.AdministratorClient;
import com.chiops.auth.libs.dtos.request.AdministratorRequestDTO;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.token.generator.TokenGenerator;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import reactor.core.publisher.Mono;

@Singleton
public class AuthProvider {

  private static final Logger LOG = LoggerFactory.getLogger(AuthProvider.class);
  private final AdministratorClient administratorClient;
  private final TokenGenerator tokenGenerator;

  public AuthProvider(AdministratorClient administratorClient, TokenGenerator tokenGenerator) {
    this.administratorClient = administratorClient;
    this.tokenGenerator = tokenGenerator;
  }

  public Mono<HttpResponse<?>> login(AdministratorRequestDTO dto) {
    return Mono.fromCallable(() -> {
        try {
            if(dto.getEmail().isEmpty() || dto.getInvitationCode().isEmpty() || dto.getPassword().isEmpty()){
                LOG.error("Login failed: The field of email, password and invitation code are OBLIGATORY");
                throw new BadRequestException("The field of email, password and invitation code are OBLIGATORY");
            }

            if(!dto.getEmail().endsWith(".com")){
                LOG.error("Login failed: The field of email is incorrect, write a valid email");
                throw new BadRequestException("The field of email is incorrect, write a valid email");
            }
            AdministratorResponseDTO existingAdministrator = administratorClient.findAdministratorByEmail(dto.getEmail());

            if (!existingAdministrator.getEmail().equals(dto.getEmail())
            || !existingAdministrator.getInvitationCode().equals(dto.getInvitationCode())) {
                LOG.error("Login failed: Invalid credentials for administrator {}", dto.getEmail());
                throw new BadRequestException("Invalid credentials for administrator " + dto.getEmail());
            }

            AdministratorResponseDTO response = administratorClient.signInAdministrator(dto);
            
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", response.getEmail());
            claims.put("name", response.getInvitationCode());

            Optional<String> token = tokenGenerator.generateToken(claims);
            LOG.info("Token generated successfully for administrator: {}", response.getEmail());
            return token
                    .<HttpResponse<?>>map(t -> HttpResponse.ok(Map.of("access_token", t)))
                    .orElseThrow(() -> new InternalServerException("Token generation failed"));
        } catch (HttpClientResponseException e) {
            HttpStatus status = e.getStatus();
            if (status != null) {
                switch (status.getCode()) {
                    case 400:
                    throw new BadRequestException("Bad request. Invalid input: " + e.getMessage());
                    default:
                        throw new InternalServerException("Unexpected error during login: " + status);
                }
            } else {
                throw new InternalServerException("Login failed: " + e.getMessage());
            }
        }
    });
}

    public Mono<HttpResponse<?>> register(AdministratorRequestDTO dto) {
        return Mono.fromCallable(() -> {
            try {
                if(dto.getEmail().isEmpty() || dto.getInvitationCode().isEmpty() || dto.getPassword().isEmpty()){
                    LOG.error("Registration failed: The field of email, password and invitation code are OBLIGATORY");
                    throw new BadRequestException("The field of email, password and invitation code are OBLIGATORY");
                }

                
                if(!dto.getEmail().endsWith(".com")){
                    LOG.error("Registration failed: The field of email is incorrect, write a valid email");
                    throw new BadRequestException("The field of email is incorrect, write a valid email");
                }
        
                AdministratorResponseDTO response = administratorClient.createAdministrator(dto);

                LOG.info("Administrator registered successfully: {}", response.getEmail());
                return HttpResponse.ok(Map.of("message", "Registration successful"));

            } catch (HttpClientResponseException e) { 
                HttpStatus status = e.getStatus();
                if (status != null) {
                    switch (status.getCode()) {
                        case 400:
                            LOG.error("Registration failed: Bad request. Invalid input: {}", e.getMessage());
                            throw new BadRequestException("Bad request. Invalid input: " + e.getMessage());
                        case 409:
                            LOG.error("Registration failed: An administrator with this email or invitation code already exists.");
                            throw new ConflictException("An administrator with this email or invitation code already exists.");
                        default:
                            LOG.error("Registration failed: Unexpected error from administrator service: {}", status);
                            throw new InternalServerException("Unexpected error from administrator service: " + status);
                    }
                } else {
                    LOG.error("Registration failed: " + e.getMessage());
                    throw new InternalServerException("Registration failed: " + e.getMessage());
                }
            }
        });
    }

}

package com.chiops.auth.controllers;

import com.chiops.auth.libs.dtos.request.AdministratorRequestDTO;
import com.chiops.auth.libs.exceptions.exception.BadRequestException;
import com.chiops.auth.libs.exceptions.exception.InternalServerException;
import com.chiops.auth.providers.AuthProvider;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import reactor.core.publisher.Mono;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final AuthProvider authProvider;

    public AuthController(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Post("/login")
    public Mono<HttpResponse<?>> login(@Body AdministratorRequestDTO dto) {
        MDC.put("method", "POST");
        MDC.put("path", "service/auth/login");
        MDC.put("user", dto.getEmail());
        LOG.info("Received login request for email: {}", dto.getEmail());
        try {
            return authProvider.login(dto);
        } catch (BadRequestException e) {
            LOG.error("Login request error: {}", e.getMessage());
            return Mono.error(new BadRequestException("Login request error: " + e.getMessage()));
        } catch (InternalServerException e) {
            LOG.error("Internal error during login: {}", e.getMessage());
            return Mono.error(new InternalServerException("Internal error during login: " + e.getMessage()));
        }
    }

    @Post("/register")
    public Mono<HttpResponse<?>> register(@Body AdministratorRequestDTO dto) {
        MDC.put("method", "POST");
        MDC.put("path", "service/auth/register");
        MDC.put("user", dto.getEmail());
        LOG.info("Received registration request for email: {}", dto.getEmail());
        try {
            return authProvider.register(dto);
        } catch (BadRequestException e) {
            LOG.error("Registration request error: {}", e.getMessage());
            return Mono.error(new BadRequestException("Registration request error: " + e.getMessage()));
        } catch (InternalServerException e) {
            LOG.error("Internal error during registration: {}", e.getMessage());
            return Mono.error(new InternalServerException("Internal error during registration: " + e.getMessage()));
        }
    }
}

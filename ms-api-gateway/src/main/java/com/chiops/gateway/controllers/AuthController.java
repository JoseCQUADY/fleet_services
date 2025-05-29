package com.chiops.gateway.controllers;

import com.chiops.gateway.libs.clients.AuthClient;
import com.chiops.gateway.libs.dtos.request.AdministratorRequestDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.inject.Inject;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
    private final AuthClient authClient;

    @Inject
    public AuthController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @Post("/login")
    public Mono<HttpResponse<?>> login(@Body AdministratorRequestDTO dto) {
        MDC.put("method", "POST");
        MDC.put("path", "api/auth/login");
        MDC.put("user", dto.getEmail());
        LOG.info("Received login request for administrator: {}", dto.getEmail());
        return authClient.login(dto);
    }

    @Post("/register")
    public Mono<HttpResponse<?>> register(@Body AdministratorRequestDTO dto) {
        MDC.put("method", "POST");
        MDC.put("path", "api/auth/register");
        MDC.put("user", dto.getEmail());
        LOG.info("Received registration request for administrator: {}", dto.getEmail());
        return authClient.register(dto);
    }

}

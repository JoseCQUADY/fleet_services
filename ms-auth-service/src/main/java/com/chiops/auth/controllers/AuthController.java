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

@ExecuteOn(TaskExecutors.BLOCKING)
@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/auth")
public class AuthController {

    private final AuthProvider authProvider;

    public AuthController(AuthProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Post("/login")
    public Mono<HttpResponse<?>> login(@Body AdministratorRequestDTO dto) {
        try {
            return authProvider.login(dto);
        } catch (BadRequestException e) {
            return Mono.error(new BadRequestException("Login request error: " + e.getMessage()));
        } catch (InternalServerException e) {
            return Mono.error(new InternalServerException("Internal error during login: " + e.getMessage()));
        }
    }

    @Post("/register")
    public Mono<HttpResponse<?>> register(@Body AdministratorRequestDTO dto) {
        try {
            return authProvider.register(dto);
        } catch (BadRequestException e) {
            return Mono.error(new BadRequestException("Registration request error: " + e.getMessage()));
        } catch (InternalServerException e) {
            return Mono.error(new InternalServerException("Internal error during registration: " + e.getMessage()));
        }
    }
}

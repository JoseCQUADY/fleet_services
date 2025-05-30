package com.chiops.auth.libs.clients;

import com.chiops.auth.libs.dtos.request.AdministratorRequestDTO;
import com.chiops.auth.libs.dtos.response.AdministratorResponseDTO;
import io.micronaut.http.annotation.*;
import io.micronaut.http.client.annotation.Client;
import jakarta.validation.Valid;

import java.util.List;

@Client("${services.administrator.url}/admin")
public interface AdministratorClient {

  @Post("/register")
  public AdministratorResponseDTO createAdministrator(
          @Valid @Body AdministratorRequestDTO administrator);

  @Post("/login")
  public AdministratorResponseDTO signInAdministrator(
          @Valid @Body AdministratorRequestDTO administrator);

  @Post("/get/{email}")
  public AdministratorResponseDTO findAdministratorByEmail(@Valid @PathVariable String email);

  @Delete("/delete/{email}")
  public void deleteAdministratorByEmail(@Valid @PathVariable String email);

  @Post("/update")
  public AdministratorResponseDTO updateAdministrator(
          @Valid @Body AdministratorRequestDTO administrator);

  @Get("/getall")
  public List<AdministratorResponseDTO> getAdministratorList();

}

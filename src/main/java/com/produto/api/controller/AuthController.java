package com.produto.api.controller;

import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.request.user.RegisterUserRequestDTO;
import com.produto.api.dto.response.user.LoginResponseDTO;
import com.produto.api.dto.response.user.RegisterUserResponseDTO;
import com.produto.api.exception.ErrorResponse;
import com.produto.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "Endpoints de autenticação")
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService service;

    @Operation(summary = "Faz autenticação do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/login")
    public ResponseEntity<Void> login(
            @RequestBody @Valid LoginRequestDTO request)
    {
        LoginResponseDTO tokenJwt = service.login(request);

        ResponseCookie jwtCookie = ResponseCookie.from("JWT_TOKEN", tokenJwt.token())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(86400)
                .sameSite("Lax")
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .build();
    }

    @Operation(summary = "Cadastra novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Usuário existente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> register(
            @RequestBody @Valid RegisterUserRequestDTO request)
    {
        RegisterUserResponseDTO response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Cadastra novo usuário com role específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "409", description = "Usuário existente", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
    })
    @PostMapping("/admin/register")
    public ResponseEntity<RegisterUserResponseDTO> adminRegister(
            @RequestBody @Valid RegisterUserRequestDTO request)
    {
        RegisterUserResponseDTO response = service.registerAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
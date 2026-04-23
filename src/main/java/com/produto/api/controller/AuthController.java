package com.produto.api.controller;

import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.request.user.RegisterUserRequestDTO;
import com.produto.api.dto.response.user.LoginResponseDTO;
import com.produto.api.dto.response.user.RegisterUserResponseDTO;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO request) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(),request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        return null;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponseDTO> register(@RequestBody @Valid RegisterUserRequestDTO request) {
        if (repository.existsByLogin(request.login())){
            throw new RuntimeException("Teste");
        }
        User newUser = new User();
        newUser.setLogin(request.login());
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(request.role());

        repository.save(newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterUserResponseDTO(newUser.getName(), newUser.getLogin()));
    }


}
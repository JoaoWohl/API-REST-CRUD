package com.produto.api.service;

import com.produto.api.exception.auth.EmailOrPasswordWrongException;
import com.produto.api.service.security.JwtTokenService;
import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.request.user.RegisterUserRequestDTO;
import com.produto.api.dto.response.user.LoginResponseDTO;
import com.produto.api.dto.response.user.RegisterUserResponseDTO;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.exception.auth.UserExistException;
import com.produto.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenService tokenConfig;

    public LoginResponseDTO login(LoginRequestDTO request){
        if (request.login() == null || request.login().isBlank()) throw new IllegalArgumentException();
        if (request.password() == null || request.password().isBlank()) throw new IllegalArgumentException();

        try {
            UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.login().toLowerCase(), request.password());
            Authentication authentication = authenticationManager.authenticate(userAndPass);

            User user = (User) authentication.getPrincipal();
            String token = tokenConfig.generateToken(user);

            return new LoginResponseDTO(token);
        } catch (AuthenticationException e) {
            throw new EmailOrPasswordWrongException();
        }
    }

    public RegisterUserResponseDTO register(RegisterUserRequestDTO request){
        if (request.login() == null || request.login().isEmpty() || request.login().isBlank())throw new IllegalArgumentException();
        if (request.name() == null || request.name().isEmpty() || request.name().isBlank()) throw new IllegalArgumentException();
        if (request.password() == null || request.password().isEmpty() || request.password().isBlank()) throw new IllegalArgumentException();
        if (repository.existsByLogin(request.login().toLowerCase())) throw new UserExistException("Usuário Já cadastrado");

        User newUser = new User();
        newUser.setName(request.name());
        newUser.setLogin(request.login().toLowerCase());
        newUser.setPassword(encoder.encode(request.password()));
        newUser.setRole(UserRole.USER);

        repository.save(newUser);

        return new RegisterUserResponseDTO(newUser.getName(), newUser.getLogin());
    }

    public RegisterUserResponseDTO registerAdmin(RegisterUserRequestDTO request){
        if (request.password() == null ||request.password().isEmpty() || request.password().isBlank()) throw new IllegalArgumentException();
        if (request.login() == null || request.login().isEmpty() || request.login().isBlank()) throw new IllegalArgumentException();
        if (request.name() == null || request.name().isEmpty() || request.name().isBlank()) throw new IllegalArgumentException();
        if (repository.existsByLogin(request.login().toLowerCase())) throw new UserExistException("Usuário Já Cadastrado");

        User newUser = new User();
        newUser.setName(request.name());
        newUser.setLogin(request.login().toLowerCase());
        newUser.setPassword(encoder.encode(request.password()));
        newUser.setRole(request.role());

        repository.save(newUser);

        return new RegisterUserResponseDTO(newUser.getName(), newUser.getLogin());
    }
}

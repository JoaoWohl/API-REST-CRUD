package com.produto.api.service;

import com.produto.api.config.security.TokenConfig;
import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.response.user.LoginResponseDTO;
import com.produto.api.entity.user.User;
import com.produto.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private TokenConfig tokenConfig;

    public LoginResponseDTO login(LoginRequestDTO request){
        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.login(),request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);

        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);

        return new LoginResponseDTO(token);
    }
}

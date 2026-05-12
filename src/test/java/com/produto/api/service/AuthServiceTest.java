package com.produto.api.service;

import com.produto.api.config.security.TokenConfig;
import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.request.user.RegisterUserRequestDTO;
import com.produto.api.dto.response.user.LoginResponseDTO;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.exception.auth.UserNotFoundException;
import com.produto.api.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    TokenConfig tokenConfig;

    @InjectMocks
    AuthService authService;

    @Test
    void login_ShowldReturnSuccess_WhenEverthingOkay() {
        User user = new User("id-1","TestName","TestEmail@test.com","TestPassword",UserRole.USER);
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com","TestPassword");

        when(userRepository.existsByLogin(request.login())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        when(tokenConfig.generateToken(user)).thenReturn("jwt-token");

        LoginResponseDTO response = authService.login(request);

        assertEquals("jwt-token",response.token());
    }

    @Test
    void login_ShowldReturnFail_WhenUserDoNotExist() {
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com","TestPassword");
        assertThrows(UserNotFoundException.class,() -> authService.login(request));
    }

    @Test
    void login_ShowldReturnFail_WhenPasswordIsWrong() {
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com","TestWrongPassword");

        when(userRepository.existsByLogin(request.login())).thenReturn(true);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(AuthenticationException.class, () -> authService.login(request));
        verify(tokenConfig, never()).generateToken(any());
    }

    @Test
    void login_ShowldReturnFail_WhenPasswordIsNull(){
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com",null);
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void login_ShowldReturnFail_WhenPasswordIsEmpty(){
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com","");
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void login_ShowldReturnFail_WhenLoginIsNull(){
        LoginRequestDTO request = new LoginRequestDTO(null,"TestPassword");
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void login_ShowldReturnFail_WhenLoginIsEmpty(){
        LoginRequestDTO request = new LoginRequestDTO("","TestPassword");
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }


}
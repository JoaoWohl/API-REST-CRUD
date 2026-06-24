package com.produto.api.unitarios.service;

import com.produto.api.exception.auth.EmailOrPasswordWrongException;
import com.produto.api.service.AuthService;
import com.produto.api.service.security.JwtTokenService;
import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.request.user.RegisterUserRequestDTO;
import com.produto.api.dto.response.user.LoginResponseDTO;
import com.produto.api.dto.response.user.RegisterUserResponseDTO;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.exception.auth.UserExistException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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
    JwtTokenService tokenConfig;
    @Mock
    PasswordEncoder encoder;

    @InjectMocks
    AuthService authService;

    @Test
    void login_ShouldReturnSuccess_WhenEverythingOkay() {
        User user = new User("id-1","TestName","TestEmail@test.com","TestPassword",UserRole.USER);
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com","TestPassword");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);

        when(tokenConfig.generateToken(user)).thenReturn("jwt-token");

        LoginResponseDTO response = authService.login(request);

        assertEquals("jwt-token",response.token());
    }

    @Test
    void login_ShouldReturnFail_WhenPasswordIsWrong() {
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com","TestWrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(EmailOrPasswordWrongException.class, () -> authService.login(request));
        verify(tokenConfig, never()).generateToken(any());
    }

    @Test
    void login_ShouldReturnFail_WhenPasswordIsNull(){
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com",null);
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void login_ShouldReturnFail_WhenPasswordIsEmpty(){
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com","");
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void login_ShouldReturnFail_WhenPasswordIsBlank(){
        LoginRequestDTO request = new LoginRequestDTO("TestEmail@test.com"," ");
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void login_ShouldReturnFail_WhenLoginIsNull(){
        LoginRequestDTO request = new LoginRequestDTO(null,"TestPassword");
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void login_ShouldReturnFail_WhenLoginIsEmpty(){
        LoginRequestDTO request = new LoginRequestDTO("","TestPassword");
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void login_ShouldReturnFail_WhenLoginIsBlank(){
        LoginRequestDTO request = new LoginRequestDTO(" ","TestPassword");
        assertThrows(IllegalArgumentException.class,() -> authService.login(request));
    }

    @Test
    void register_ShouldReturnSuccess_WhenEverythingOkay(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);
        when(encoder.encode(request.password())).thenReturn("EncodedPassword");

        RegisterUserResponseDTO result = authService.register(request);

        assertEquals(new RegisterUserResponseDTO("TestName","TestEmail@test.com"),result);
        verify(userRepository).save(argThat(u ->
                u.getName().equals("TestName")
                && u.getLogin().equals("TestEmail@test.com")
                && u.getPassword().equals("EncodedPassword")
                && u.getRole().equals(UserRole.USER)
        ));
    }

    @Test
    void register_ShouldReturnSuccess_WhenUserRoleIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com","TestPassword",null);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);
        when(encoder.encode(request.password())).thenReturn("EncodedPassword");

        RegisterUserResponseDTO result = authService.register(request);

        assertEquals(new RegisterUserResponseDTO("TestName","TestEmail@test.com"),result);
        verify(userRepository).save(argThat(u ->
                u.getName().equals("TestName")
                        && u.getLogin().equals("TestEmail@test.com")
                        && u.getPassword().equals("EncodedPassword")
                        && u.getRole().equals(UserRole.USER)
        ));
    }

    @Test
    void register_ShouldReturnFail_WhenUserExist(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(true);

        assertThrows(UserExistException.class, () -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenNameIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO(null,"TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenNameIsEmpty(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("","TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenNameIsBlank(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO(" ","TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenLoginIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName",null,"TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenLoginIsEmpty(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenLoginIsBlank(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName"," ","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenPasswordIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com",null,UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenPasswordIsEmpty(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com","",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void register_ShouldReturnFail_WhenPasswordIsBlank(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com"," ",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.register(request));
    }

    @Test
    void registerAdmin_ShouldReturnSuccess_WhenEverythingOkay(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);
        when(encoder.encode(request.password())).thenReturn("EncodedPassword");

        RegisterUserResponseDTO result = authService.registerAdmin(request);

        assertEquals(new RegisterUserResponseDTO("TestName","TestEmail@test.com"), result);
        verify(userRepository).save(argThat(u ->
                u.getName().equals("TestName")
                && u.getLogin().equals(request.login())
                && u.getPassword().equals("EncodedPassword")
                && u.getRole().equals(UserRole.ADMIN)
        ));
    }

    @Test
    void registerAdmin_ShouldReturnSuccess_WhenRoleIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com","TestPassword",null);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);
        when(encoder.encode(request.password())).thenReturn("EncodedPassword");

        RegisterUserResponseDTO result = authService.registerAdmin(request);

        assertEquals(new RegisterUserResponseDTO("TestName","TestEmail@test.com"), result);
        verify(userRepository).save(argThat(u ->
                u.getName().equals("TestName")
                && u.getLogin().equals(request.login())
                && u.getPassword().equals("EncodedPassword")
                && u.getRole().equals(UserRole.USER)
        ));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenNameIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO(null,"TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenNameIsEmpty(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("","TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenNameIsBlank(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO(" ","TestEmail@test.com","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenLoginIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName",null,"TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenLoginIsEmpty(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenLoginIsBlank(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName"," ","TestPassword",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenPasswordIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com",null,UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenPasswordIsEmpty(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com","",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

    @Test
    void registerAdmin_ShouldReturnFail_WhenPasswordIsBlank(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com"," ",UserRole.ADMIN);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() -> authService.registerAdmin(request));
    }

}
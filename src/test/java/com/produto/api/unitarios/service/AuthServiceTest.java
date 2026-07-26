package com.produto.api.unitarios.service;

import com.produto.api.exception.auth.EmailOrPasswordWrongException;
import com.produto.api.service.AuthService;
import com.produto.api.security.JwtTokenService;
import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.request.user.RegisterUserRequestDTO;
import com.produto.api.dto.response.user.LoginResponseDTO;
import com.produto.api.dto.response.user.RegisterUserResponseDTO;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.exception.auth.UserExistException;
import com.produto.api.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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

    private User validUser;
    private LoginRequestDTO validLoginRequestDTO;
    private RegisterUserRequestDTO validRegisterUserRequestDTO;
    private final static UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        validUser = new User(USER_ID, "User Name", "testlogin@example.com", "TestPassword",UserRole.USER);
        validLoginRequestDTO = new LoginRequestDTO("testlogin@example.com", "TestPassword");
        validRegisterUserRequestDTO = new RegisterUserRequestDTO("User Name","testlogin@example.com","TestPassword", UserRole.USER);
    }

    @Test
    void login_ShouldReturnSuccess_WhenEverythingOkay() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(validUser);

        when(tokenConfig.generateToken(validUser)).thenReturn("jwt-token");

        LoginResponseDTO response = authService.login(validLoginRequestDTO);

        assertEquals("jwt-token",response.token());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_ShouldReturnFail_WhenPasswordIsWrong() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(EmailOrPasswordWrongException.class, () -> authService.login(validLoginRequestDTO));
        verify(tokenConfig, never()).generateToken(any());
    }

    static Stream<Arguments> invalidLoginRequestDTO() {
        return Stream.of(
                Arguments.of("", "TestPassword"),
                Arguments.of(" ", "TestPassword"),
                Arguments.of(null, "TestPassword"),
                Arguments.of("testlogin@example.com", ""),
                Arguments.of("testlogin@example.com", " "),
                Arguments.of("testlogin@example.com", null)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidLoginRequestDTO")
    void login_ShouldReturnIllegalArgumentsException_WhenInvalidLoginRequestDTO(String login, String password) {
        LoginRequestDTO invalidLoginRequestDTO = new LoginRequestDTO(login, password);
        assertThrows(IllegalArgumentException.class, () -> authService.login(invalidLoginRequestDTO));
    }

    @Test
    void register_ShouldReturnSuccess_WhenEverythingOkay(){
        when(userRepository.existsByLogin(validRegisterUserRequestDTO.login())).thenReturn(false);
        when(encoder.encode(validRegisterUserRequestDTO.password())).thenReturn("EncodedPassword");

        RegisterUserResponseDTO result = authService.register(validRegisterUserRequestDTO);

        assertThat(result).isEqualTo(new RegisterUserResponseDTO("User Name", "testlogin@example.com"));
        verify(userRepository, times(1)).existsByLogin(validRegisterUserRequestDTO.login());
        verify(encoder, times(1)).encode(validRegisterUserRequestDTO.password());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldReturnSuccess_WhenUserRoleIsNull(){
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("TestName","TestEmail@test.com","TestPassword",null);
        when(userRepository.existsByLogin(request.login())).thenReturn(false);
        when(encoder.encode(request.password())).thenReturn("EncodedPassword");

        RegisterUserResponseDTO result = authService.register(request);

        assertEquals(new RegisterUserResponseDTO("TestName","TestEmail@test.com"),result);
        verify(userRepository, times(1)).existsByLogin(anyString());
        verify(encoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_ShouldReturnFail_WhenUserExist(){
        when(userRepository.existsByLogin(validRegisterUserRequestDTO.login())).thenReturn(true);

        assertThrows(UserExistException.class, () -> authService.register(validRegisterUserRequestDTO));
    }

    static Stream<Arguments> invalidRegisterUserRequestDTO() {
        return Stream.of(
                Arguments.of("", "testlogin@example.com", "TestPassword", UserRole.USER),
                Arguments.of(" ", "testlogin@example.com", "TestPassword", UserRole.USER),
                Arguments.of(null, "testlogin@example.com", "TestPassword", UserRole.USER),
                Arguments.of("User Name", "", "TestPassword", UserRole.USER),
                Arguments.of("User Name", " ", "TestPassword", UserRole.USER),
                Arguments.of("User Name", null, "TestPassword", UserRole.USER),
                Arguments.of("User Name", "testlogin@example.com", "", UserRole.USER),
                Arguments.of("User Name", "testlogin@example.com", " ", UserRole.USER),
                Arguments.of("User Name", "testlogin@example.com", null, UserRole.USER)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidRegisterUserRequestDTO")
    void register_ShouldReturnIllegalArgumentException_WhenInvalidRegisterRequestDTO (String name, String login, String password, UserRole role) {
        RegisterUserRequestDTO invalidRegisterUserRequestDTO = new RegisterUserRequestDTO(name, login, password, role);
        assertThrows(IllegalArgumentException.class, () -> authService.register(invalidRegisterUserRequestDTO));
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

    @ParameterizedTest
    @MethodSource("invalidRegisterUserRequestDTO")
    void registerAdmin_ShouldReturnIllegalArgumentException_WhenInvalidRegisterRequestDTO (String name, String login, String password, UserRole role) {
        RegisterUserRequestDTO invalidRegisterUserRequestDTO = new RegisterUserRequestDTO(name, login, password, role);
        assertThrows(IllegalArgumentException.class, () -> authService.registerAdmin(invalidRegisterUserRequestDTO));
    }

}
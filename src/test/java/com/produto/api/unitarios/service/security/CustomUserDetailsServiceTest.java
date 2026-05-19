package com.produto.api.unitarios.service.security;

import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.repository.UserRepository;
import com.produto.api.service.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_ShouldReturnSuccess_WhenEverythingOk() {
        User user = new User("id-1","TestName","TestEmail@test.com","TestPassword", UserRole.USER);
        String login = "TestEmail@test.com";

        when(repository.findByLogin(login)).thenReturn(Optional.of(user));

        UserDetails result = customUserDetailsService.loadUserByUsername(login);

        assertEquals(user, result);
    }

    @Test
    void loadUserByUsername_ShouldReturnFail_WhenUserNotFound() {
        String login = "TestEmail@test.com";

        when(repository.findByLogin(login)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,() -> customUserDetailsService.loadUserByUsername(login));
    }
}
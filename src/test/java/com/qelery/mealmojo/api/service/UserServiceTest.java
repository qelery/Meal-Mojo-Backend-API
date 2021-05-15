package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.model.login.LoginRequest;
import com.qelery.mealmojo.api.model.login.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    @DisplayName("Should save new user to database and return 201 response")
    void createUser() {
        UserService userService = new UserService(userRepository, userDetailsService, passwordEncoder, jwtUtils, authenticationManager);
        User user = new User("test@test.com", "password", null, null);

        String message = "Successfully registered new user with email address " + user.getEmail();
        ResponseEntity<String> expectedResponse = ResponseEntity.status(201).body(message);
        ResponseEntity<String> actualResponse = userService.createUser(user);
        assertEquals(expectedResponse, actualResponse);

        Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());

        assertEquals("test@test.com", userArgumentCaptor.getValue().getEmail());
    }

    @Test
    @DisplayName("Should login a user and return 200 response")
    void loginUser() {

        UserService userService = new UserService(userRepository, userDetailsService, passwordEncoder, jwtUtils, authenticationManager);
        LoginRequest loginRequest = new LoginRequest();
        ResponseEntity<LoginResponse> actualResponse = userService.loginUser(loginRequest);
        assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
    }

}

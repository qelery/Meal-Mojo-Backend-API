package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsServiceImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PropertyCopier propertyCopier;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    @DisplayName("Should save new user to database and return 201 response")
    void createUser() {
        UserService userService = new UserService(userRepository, userDetailsService, passwordEncoder, jwtUtils, authenticationManager, propertyCopier);
        User user = new User("test@test.com", "password", Role.CUSTOMER);

        ResponseEntity<User> expectedResponse = new ResponseEntity<>(user, HttpStatus.CREATED);
        ResponseEntity<User> actualResponse = userService.createUserWithCustomerRole(user);
        assertEquals(expectedResponse, actualResponse);

        Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());

        assertEquals("test@test.com", userArgumentCaptor.getValue().getEmail());
    }

}

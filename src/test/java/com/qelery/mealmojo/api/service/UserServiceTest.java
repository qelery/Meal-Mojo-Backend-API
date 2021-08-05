package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.model.dto.UserDtoIn;
import com.qelery.mealmojo.api.model.dto.UserDtoOut;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.security.UserDetailsServiceImpl;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;
    @Mock
    UserDetailsServiceImpl userDetailsService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtUtils jwtUtils;
    @Mock
    AuthenticationManager authenticationManager;
    @Spy
    MapperUtils mapperUtils;

    @Captor
    ArgumentCaptor<User> userCaptor;
    @Captor
    ArgumentCaptor<String> usernameCaptor;
    @Captor
    ArgumentCaptor<UserDetails> userDetailsCaptor;

    UserDtoIn userDtoIn;

    @BeforeEach
    void setup() {
        userDtoIn = new UserDtoIn();
        userDtoIn.setFirstName("John");
        userDtoIn.setLastName("Smith");
        userDtoIn.setEmail("john@example.org");
        userDtoIn.setPassword("password");
    }

    @Nested
    @DisplayName("Should create a user")
    class shouldCreateUser {
        @Test
        @DisplayName("with a CustomerProfile")
        void createUserCustomer() {
            userDtoIn.setRole(Role.CUSTOMER);

            UserDtoOut userDtoOut = userService.createUser(userDtoIn);

            verify(passwordEncoder).encode(userDtoIn.getPassword());
            verify(userRepository).save(userCaptor.capture());
            User userSavedToDatabase = userCaptor.getValue();
            verify(mapperUtils).map(userSavedToDatabase, UserDtoOut.class);

            assertEquals(userDtoIn.getEmail(), userSavedToDatabase.getEmail());
            assertEquals(userDtoIn.getRole(), userSavedToDatabase.getRole());
            assertEquals(userDtoIn.getFirstName(), userSavedToDatabase.getCustomerProfile().getFirstName());
            assertEquals(userDtoIn.getLastName(), userSavedToDatabase.getCustomerProfile().getLastName());

            assertEquals(userDtoIn.getFirstName(), userDtoOut.getFirstName());
            assertEquals(userDtoIn.getLastName(), userDtoOut.getLastName());
            assertEquals(userDtoIn.getEmail(), userDtoOut.getEmail());
            assertEquals(userDtoIn.getRole(), userDtoOut.getRole());
        }

        @Test
        @DisplayName("with a MerchantProfile")
        void createUserMerchant() {
            userDtoIn.setRole(Role.MERCHANT);

            UserDtoOut userDtoOut = userService.createUser(userDtoIn);

            verify(passwordEncoder).encode(userDtoIn.getPassword());
            verify(userRepository).save(userCaptor.capture());
            User userSavedToDatabase = userCaptor.getValue();
            verify(mapperUtils).map(userSavedToDatabase, UserDtoOut.class);

            assertEquals(userDtoIn.getEmail(), userSavedToDatabase.getEmail());
            assertEquals(userDtoIn.getRole(), userSavedToDatabase.getRole());
            assertEquals(userDtoIn.getFirstName(), userSavedToDatabase.getMerchantProfile().getFirstName());
            assertEquals(userDtoIn.getLastName(), userSavedToDatabase.getMerchantProfile().getLastName());

            assertEquals(userDtoIn.getFirstName(), userDtoOut.getFirstName());
            assertEquals(userDtoIn.getLastName(), userDtoOut.getLastName());
            assertEquals(userDtoIn.getEmail(), userDtoOut.getEmail());
            assertEquals(userDtoIn.getRole(), userDtoOut.getRole());
        }

        @Test
        @DisplayName("only if one doesn't already exist with that email")
        void createUserEmailExistsException() {
            when(userRepository.existsByEmail(anyString()))
                    .thenReturn(true);
            Exception exception = assertThrows(EmailExistsException.class, () -> {
                userService.createUser(userDtoIn);
            });
            assertEquals("User already exists with email " + userDtoIn.getEmail(), exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should login user and return JWT")
    void loginUser() {
        LoginRequest loginRequest = new LoginRequest("john@example.org", "password");
        UserDetails userDetails = new UserDetailsImpl(new User());
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(userDetails);
        when(jwtUtils.generateToken(any(UserDetails.class)))
                .thenReturn("myJwtToken");

        LoginResponse actualLoginResponse = userService.loginUser(loginRequest);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(usernameCaptor.capture());
        verify(jwtUtils).generateToken(userDetailsCaptor.capture());

        assertEquals(loginRequest.getEmail(), usernameCaptor.getValue());
        assertEquals(userDetails, userDetailsCaptor.getValue());
        assertEquals("myJwtToken", actualLoginResponse.getJWT());
    }
}

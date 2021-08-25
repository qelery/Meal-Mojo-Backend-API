package com.qelery.mealmojo.api.unitTests.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.exception.UserNotFoundException;
import com.qelery.mealmojo.api.model.dto.UserCreationDtoIn;
import com.qelery.mealmojo.api.model.dto.UserCreationDtoOut;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsServiceImpl;
import com.qelery.mealmojo.api.service.UserService;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    UserCreationDtoIn userCreationDtoIn;

    @BeforeEach
    void setup() {
        userCreationDtoIn = new UserCreationDtoIn();
        userCreationDtoIn.setFirstName("John");
        userCreationDtoIn.setLastName("Smith");
        userCreationDtoIn.setEmail("john@example.com");
        userCreationDtoIn.setPassword("password");
    }

    @Nested
    @DisplayName("Should create a user")
    class shouldCreateUser {
        @Test
        @DisplayName("with a CustomerProfile")
        void createUserCustomer() {
            userCreationDtoIn.setRole(Role.CUSTOMER);

            UserCreationDtoOut userCreationDtoOut = userService.createUser(userCreationDtoIn);

            verify(passwordEncoder).encode(userCreationDtoIn.getPassword());
            verify(userRepository).save(userCaptor.capture());
            User userSavedToDatabase = userCaptor.getValue();
            verify(mapperUtils).map(userSavedToDatabase, UserCreationDtoOut.class);

            assertEquals(userCreationDtoIn.getEmail(), userSavedToDatabase.getEmail());
            assertEquals(userCreationDtoIn.getRole(), userSavedToDatabase.getRole());
            assertEquals(userCreationDtoIn.getFirstName(), userSavedToDatabase.getCustomerProfile().getFirstName());
            assertEquals(userCreationDtoIn.getLastName(), userSavedToDatabase.getCustomerProfile().getLastName());

            assertEquals(userCreationDtoIn.getFirstName(), userCreationDtoOut.getFirstName());
            assertEquals(userCreationDtoIn.getLastName(), userCreationDtoOut.getLastName());
            assertEquals(userCreationDtoIn.getEmail(), userCreationDtoOut.getEmail());
            assertEquals(userCreationDtoIn.getRole(), userCreationDtoOut.getRole());
        }

        @Test
        @DisplayName("with a MerchantProfile")
        void createUserMerchant() {
            userCreationDtoIn.setRole(Role.MERCHANT);

            UserCreationDtoOut userCreationDtoOut = userService.createUser(userCreationDtoIn);

            verify(passwordEncoder).encode(userCreationDtoIn.getPassword());
            verify(userRepository).save(userCaptor.capture());
            User userSavedToDatabase = userCaptor.getValue();
            verify(mapperUtils).map(userSavedToDatabase, UserCreationDtoOut.class);

            assertEquals(userCreationDtoIn.getEmail(), userSavedToDatabase.getEmail());
            assertEquals(userCreationDtoIn.getRole(), userSavedToDatabase.getRole());
            assertEquals(userCreationDtoIn.getFirstName(), userSavedToDatabase.getMerchantProfile().getFirstName());
            assertEquals(userCreationDtoIn.getLastName(), userSavedToDatabase.getMerchantProfile().getLastName());

            assertEquals(userCreationDtoIn.getFirstName(), userCreationDtoOut.getFirstName());
            assertEquals(userCreationDtoIn.getLastName(), userCreationDtoOut.getLastName());
            assertEquals(userCreationDtoIn.getEmail(), userCreationDtoOut.getEmail());
            assertEquals(userCreationDtoIn.getRole(), userCreationDtoOut.getRole());
        }

        @Test
        @DisplayName("only if one doesn't already exist with that email")
        void createUserEmailExistsException() {
            when(userRepository.existsByEmail(anyString()))
                    .thenReturn(true);
            Exception exception = assertThrows(EmailExistsException.class, () -> {
                userService.createUser(userCreationDtoIn);
            });
            assertEquals("User already exists with email " + userCreationDtoIn.getEmail(), exception.getMessage());
        }
    }

    @Test
    @DisplayName("Should login user and return JWT")
    void loginUser() {
        LoginRequest loginRequest = new LoginRequest("john@example.com", "password");
        User user = new User();
        when(userDetailsService.loadUserByUsername(anyString()))
                .thenReturn(user);
        when(jwtUtils.generateToken(any(UserDetails.class)))
                .thenReturn("myJwtToken");

        LoginResponse actualLoginResponse = userService.loginUser(loginRequest);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService).loadUserByUsername(usernameCaptor.capture());
        verify(jwtUtils).generateToken(userDetailsCaptor.capture());

        assertEquals(loginRequest.getEmail(), usernameCaptor.getValue());
        assertEquals(user, userDetailsCaptor.getValue());
        assertEquals("myJwtToken", actualLoginResponse.getJWT());
    }

    @Test
    @DisplayName("Should deactivate a user")
    void changeUserActiveState_deactivate() {
        User activeUser = new User();
        activeUser.setId(1L);
        activeUser.setIsActive(true);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(activeUser));

        userService.changeUserActiveState(activeUser.getId(), false);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertFalse(savedUser.getIsActive());
    }

    @Test
    @DisplayName("Should re-activate a user")
    void changeUserActiveState_activate() {
        User deactivatedUser = new User();
        deactivatedUser.setId(1L);
        deactivatedUser.setIsActive(false);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(deactivatedUser));

        userService.changeUserActiveState(deactivatedUser.getId(), true);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.getIsActive());
    }

    @Test
    @DisplayName("Should throw error when trying to change active status on user that does not exist")
    void changeUserActiveState_userNotFound() {
        Long userIdThatDoesNotExist = 4493L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> userService.changeUserActiveState(userIdThatDoesNotExist, true));
    }
}

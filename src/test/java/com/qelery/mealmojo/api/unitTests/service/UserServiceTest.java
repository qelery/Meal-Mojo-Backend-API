package com.qelery.mealmojo.api.unitTests.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.exception.UserNotFoundException;
import com.qelery.mealmojo.api.model.dto.UserCreationDto;
import com.qelery.mealmojo.api.model.entity.CustomerProfile;
import com.qelery.mealmojo.api.model.entity.MerchantProfile;
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

    UserCreationDto userCreationDto;

    @BeforeEach
    void setup() {
        userCreationDto = new UserCreationDto();
        userCreationDto.setFirstName("John");
        userCreationDto.setLastName("Smith");
        userCreationDto.setEmail("john@example.com");
        userCreationDto.setPassword("password");
    }

    @Nested
    @DisplayName("Should create a user")
    class shouldCreateUser {
        @Test
        @DisplayName("with a CustomerProfile")
        void createUserCustomer() {
            userCreationDto.setRole(Role.CUSTOMER);

            CustomerProfile expectedCustomerProfile = new CustomerProfile();
            expectedCustomerProfile.setFirstName(userCreationDto.getFirstName());
            expectedCustomerProfile.setLastName(userCreationDto.getLastName());
            User expectedUser = new User();
            expectedUser.setCustomerProfile(expectedCustomerProfile);
            expectedUser.setEmail(userCreationDto.getEmail());
            expectedUser.setRole(userCreationDto.getRole());

            when(userDetailsService.loadUserByUsername(anyString()))
                    .thenReturn(expectedUser);
            when(jwtUtils.generateToken(any(UserDetails.class)))
                    .thenReturn("myJwtToken");

            LoginResponse loginResponse = userService.createUser(userCreationDto);

            verify(passwordEncoder).encode(userCreationDto.getPassword());
            verify(userRepository).save(userCaptor.capture());
            User userSavedToDatabase = userCaptor.getValue();

            assertEquals(userCreationDto.getEmail(), userSavedToDatabase.getEmail());
            assertEquals(userCreationDto.getRole(), userSavedToDatabase.getRole());
            assertEquals(userCreationDto.getFirstName(), userSavedToDatabase.getCustomerProfile().getFirstName());
            assertEquals(userCreationDto.getLastName(), userSavedToDatabase.getCustomerProfile().getLastName());

            assertEquals(userCreationDto.getFirstName(), loginResponse.getUserInfo().getFirstName());
            assertEquals(userCreationDto.getLastName(), loginResponse.getUserInfo().getLastName());
            assertEquals(userCreationDto.getEmail(), loginResponse.getUserInfo().getEmail());
            assertEquals("myJwtToken", loginResponse.getToken());
        }

        @Test
        @DisplayName("with a MerchantProfile")
        void createUserMerchant() {
            userCreationDto.setRole(Role.MERCHANT);

            MerchantProfile expectedMerchantProfile = new MerchantProfile();
            expectedMerchantProfile.setFirstName(userCreationDto.getFirstName());
            expectedMerchantProfile.setLastName(userCreationDto.getLastName());
            User expectedUser = new User();
            expectedUser.setMerchantProfile(expectedMerchantProfile);
            expectedUser.setEmail(userCreationDto.getEmail());
            expectedUser.setRole(userCreationDto.getRole());

            when(userDetailsService.loadUserByUsername(anyString()))
                    .thenReturn(expectedUser);
            when(jwtUtils.generateToken(any(UserDetails.class)))
                    .thenReturn("myJwtToken");

            LoginResponse loginResponse = userService.createUser(userCreationDto);

            verify(passwordEncoder).encode(userCreationDto.getPassword());
            verify(userRepository).save(userCaptor.capture());
            User userSavedToDatabase = userCaptor.getValue();

            assertEquals(userCreationDto.getEmail(), userSavedToDatabase.getEmail());
            assertEquals(userCreationDto.getRole(), userSavedToDatabase.getRole());
            assertEquals(userCreationDto.getFirstName(), userSavedToDatabase.getMerchantProfile().getFirstName());
            assertEquals(userCreationDto.getLastName(), userSavedToDatabase.getMerchantProfile().getLastName());

            assertEquals(userCreationDto.getFirstName(), loginResponse.getUserInfo().getFirstName());
            assertEquals(userCreationDto.getLastName(), loginResponse.getUserInfo().getLastName());
            assertEquals(userCreationDto.getEmail(), loginResponse.getUserInfo().getEmail());
            assertEquals("myJwtToken", loginResponse.getToken());
        }

        @Test
        @DisplayName("only if one doesn't already exist with that email")
        void createUserEmailExistsException() {
            when(userRepository.existsByEmailIgnoreCase(anyString()))
                    .thenReturn(true);
            Exception exception = assertThrows(EmailExistsException.class, () -> userService.createUser(userCreationDto));
            assertEquals("User already exists with email " + userCreationDto.getEmail(), exception.getMessage());
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

        verify(userDetailsService).loadUserByUsername(usernameCaptor.capture());
        verify(jwtUtils).generateToken(userDetailsCaptor.capture());

        assertEquals(loginRequest.getUsername(), usernameCaptor.getValue());
        assertEquals(user, userDetailsCaptor.getValue());
        assertEquals("myJwtToken", actualLoginResponse.getToken());
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

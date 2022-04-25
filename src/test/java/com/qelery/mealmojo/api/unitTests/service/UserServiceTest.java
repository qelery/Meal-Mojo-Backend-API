package com.qelery.mealmojo.api.unitTests.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.exception.ProhibitedByRoleException;
import com.qelery.mealmojo.api.exception.UserNotFoundException;
import com.qelery.mealmojo.api.model.dto.AddressDto;
import com.qelery.mealmojo.api.model.dto.UserCreationDto;
import com.qelery.mealmojo.api.model.dto.UserInfoDto;
import com.qelery.mealmojo.api.model.entity.Address;
import com.qelery.mealmojo.api.model.entity.CustomerProfile;
import com.qelery.mealmojo.api.model.entity.MerchantProfile;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.enums.State;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsServiceImpl;
import com.qelery.mealmojo.api.service.UserService;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

            assertEquals(userCreationDto.getFirstName(), loginResponse.getUser().getFirstName());
            assertEquals(userCreationDto.getLastName(), loginResponse.getUser().getLastName());
            assertEquals(userCreationDto.getEmail(), loginResponse.getUser().getEmail());
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

            assertEquals(userCreationDto.getFirstName(), loginResponse.getUser().getFirstName());
            assertEquals(userCreationDto.getLastName(), loginResponse.getUser().getLastName());
            assertEquals(userCreationDto.getEmail(), loginResponse.getUser().getEmail());
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
        activeUser.setUserId(1L);
        activeUser.setIsActive(true);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(activeUser));

        userService.changeUserActiveState(activeUser.getUserId(), false);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertFalse(savedUser.getIsActive());
    }

    @Test
    @DisplayName("Should re-activate a user")
    void changeUserActiveState_activate() {
        User deactivatedUser = new User();
        deactivatedUser.setUserId(1L);
        deactivatedUser.setIsActive(false);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(deactivatedUser));

        userService.changeUserActiveState(deactivatedUser.getUserId(), true);

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

    @Test
    @DisplayName("Should update user info for user")
    void updateUser() {
        Address address = new Address();
        address.setStreet1("1400 S Lake Shore Dr");
        address.setStreet2("Building 2");
        address.setStreet3("#3A");
        address.setCity("Chicago");
        address.setState(State.IL);
        address.setZipcode("60605");
        address.setCountry(Country.US);
        address.setLatitude(41.866265);
        address.setLongitude(-87.6191692);

        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setFirstName("John");
        customerProfile.setLastName("Smith");
        customerProfile.setAddress(address);

        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setEmail("john@example.com");
        user.setCustomerProfile(customerProfile);

        AddressDto newAddress = new AddressDto();
        newAddress.setStreet1("02115");
        newAddress.setCity("Boston");
        newAddress.setState(State.MA);
        newAddress.setZipcode("02115");
        newAddress.setCountry(Country.US);
        newAddress.setLatitude(42.3393849);
        newAddress.setLongitude(-71.0962367);

        UserInfoDto updatedUserInfo = new UserInfoDto();
        updatedUserInfo.setFirstName("Michael");
        updatedUserInfo.setLastName("Johnson");
        updatedUserInfo.setEmail("michael@example.com");
        updatedUserInfo.setAddress(newAddress);

        addUserToSecurityContext(user);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        UserInfoDto actualUserDto = userService.updateUser(updatedUserInfo);

        assertEquals(updatedUserInfo, actualUserDto);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(updatedUserInfo.getEmail(), savedUser.getEmail());
        assertEquals(updatedUserInfo.getFirstName(), savedUser.getCustomerProfile().getFirstName());
        assertEquals(updatedUserInfo.getLastName(), savedUser.getCustomerProfile().getLastName());
        Assertions.assertThat(savedUser.getCustomerProfile().getAddress())
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(updatedUserInfo.getAddress());
    }

    @Test
    @DisplayName("Should get role for logged in user")
    void getLoggedInUserRole() {
        Role expectedRole = Role.CUSTOMER;
        User user = new User();
        user.setRole(expectedRole);
        addUserToSecurityContext(user);

        assertEquals(expectedRole, userService.getLoggedInUserRole());
    }

    @Test
    @DisplayName("Should get customer profile for logged in user")
    void getLoggedInCustomerProfile() {
        CustomerProfile expectedCustomerProfile = new CustomerProfile();
        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setCustomerProfile(expectedCustomerProfile);
        addUserToSecurityContext(user);

        CustomerProfile actualCustomerProfile = userService.getLoggedInCustomerProfile();

        assertEquals(expectedCustomerProfile, actualCustomerProfile);
    }

    @Test
    @DisplayName("Should get merchant profile for logged in user")
    void getLoggedInMerchantProfile() {
        MerchantProfile expectedMerchantProfile = new MerchantProfile();
        User user = new User();
        user.setRole(Role.MERCHANT);
        user.setMerchantProfile(expectedMerchantProfile);
        addUserToSecurityContext(user);

        MerchantProfile actualMerchantProfile = userService.getLoggedInUserMerchantProfile();

        assertEquals(expectedMerchantProfile, actualMerchantProfile);
    }

    @Test
    @DisplayName("Should throw exception if retrieving customer profile for user without customer role")
    void getLoggedInCustomerProfile_throwException() {
        MerchantProfile merchantProfile = new MerchantProfile();
        User user = new User();
        user.setRole(Role.MERCHANT);
        user.setMerchantProfile(merchantProfile);
        addUserToSecurityContext(user);

        assertThrows(ProhibitedByRoleException.class, () -> userService.getLoggedInCustomerProfile());
    }

    @Test
    @DisplayName("Should throw exception if retrieving merchant profile for user without merchant role")
    void getLoggedInMerchantProfile_throwException() {
        CustomerProfile customerProfile = new CustomerProfile();
        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setCustomerProfile(customerProfile);
        addUserToSecurityContext(user);

        assertThrows(ProhibitedByRoleException.class, () -> userService.getLoggedInUserMerchantProfile());
    }

    @Test
    @DisplayName("Should get user address")
    void getAddress() {
        Address expectedAddress = new Address();
        expectedAddress.setStreet1("123 Maple Lane");
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setAddress(expectedAddress);
        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setCustomerProfile(customerProfile);
        addUserToSecurityContext(user);

        AddressDto actualAddressDto = userService.getAddress();

        assertEquals(mapperUtils.map(expectedAddress, AddressDto.class), actualAddressDto);
    }

    @Test
    @DisplayName("Should get return null is user doesn't have an address")
    void getAddress_returnNull() {
        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setAddress(null);
        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setCustomerProfile(customerProfile);
        addUserToSecurityContext(user);

        AddressDto actualAddressDto = userService.getAddress();

        assertNull(actualAddressDto);
    }

    @Test
    @DisplayName("Should update user address")
    void updateAddress() {
        AddressDto updatedAddressDto = new AddressDto();
        updatedAddressDto.setStreet1("02115");
        updatedAddressDto.setCity("Boston");
        updatedAddressDto.setState(State.MA);
        updatedAddressDto.setZipcode("02115");
        updatedAddressDto.setCountry(Country.US);
        updatedAddressDto.setLatitude(42.3393849);
        updatedAddressDto.setLongitude(-71.0962367);

        Address oldAddress = new Address();
        oldAddress.setStreet1("1400 S Lake Shore Dr");
        oldAddress.setStreet2("Building 2");
        oldAddress.setStreet3("#3A");
        oldAddress.setCity("Chicago");
        oldAddress.setState(State.IL);
        oldAddress.setZipcode("60605");
        oldAddress.setCountry(Country.US);
        oldAddress.setLatitude(41.866265);
        oldAddress.setLongitude(-87.6191692);

        CustomerProfile customerProfile = new CustomerProfile();
        customerProfile.setFirstName("John");
        customerProfile.setLastName("Smith");
        customerProfile.setAddress(oldAddress);
        User user = new User();
        user.setRole(Role.CUSTOMER);
        user.setCustomerProfile(customerProfile);
        addUserToSecurityContext(user);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        userService.updateAddress(updatedAddressDto);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals(user.getEmail(), savedUser.getEmail());
        assertEquals(customerProfile.getFirstName(), savedUser.getCustomerProfile().getFirstName());
        assertEquals(customerProfile.getLastName(), savedUser.getCustomerProfile().getLastName());
        Address savedAddress = savedUser.getCustomerProfile().getAddress();
        Assertions.assertThat(savedAddress)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(updatedAddressDto);
    }

    private void addUserToSecurityContext(User user) {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

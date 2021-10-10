package com.qelery.mealmojo.api.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.model.dto.AddressDto;
import com.qelery.mealmojo.api.model.dto.UserCreationDto;
import com.qelery.mealmojo.api.model.dto.UserInfoDto;
import com.qelery.mealmojo.api.model.entity.*;
import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.enums.State;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.testUtils.HttpRequestDispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static com.qelery.mealmojo.api.testUtils.CustomAssertions.assertContainsErrorMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(scripts = {"/seed/load-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/seed/clear-test-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class LoginAndRegistrationIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    HttpRequestDispatcher httpRequestDispatcher;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("[Integration Tests - Through All Layers] When user registers,")
    class throughAllLayers_user_registration {

        @Test
        @DisplayName("Should be able to sign up for a Customer account")
        void shouldRegisterCustomer() throws Exception {
            UserCreationDto userSignUpInfoDto = new UserCreationDto();
            userSignUpInfoDto.setEmail("gordon@example.com");
            userSignUpInfoDto.setPassword("password");
            userSignUpInfoDto.setRole(Role.CUSTOMER);
            userSignUpInfoDto.setFirstName("Gordon");
            userSignUpInfoDto.setLastName("Ramsay");

            String url = "/api/users/register";
            String jsonResponse = httpRequestDispatcher.performPOST(url, userSignUpInfoDto);
            LoginResponse actualLoginResponse = objectMapper.readValue(jsonResponse, LoginResponse.class);

            assertThat(actualLoginResponse.getUserInfo())
                    .usingRecursiveComparison()
                    .ignoringFields("address")
                    .isEqualTo(userSignUpInfoDto);
            Optional<User> userFromDatabase = userRepository.findByEmailIgnoreCase(userSignUpInfoDto.getEmail());
            assertTrue(userFromDatabase.isPresent());
            assertEquals(Role.CUSTOMER, userFromDatabase.get().getRole());
        }

        @Test
        @DisplayName("Should be able to sign up for a Merchant account")
        void shouldRegisterMerchant() throws Exception {
            UserCreationDto userSignUpInfoDto = new UserCreationDto();
            userSignUpInfoDto.setEmail("julia@example.com");
            userSignUpInfoDto.setPassword("password");
            userSignUpInfoDto.setRole(Role.MERCHANT);
            userSignUpInfoDto.setFirstName("Julia");
            userSignUpInfoDto.setLastName("Child");

            String url = "/api/users/register";
            String jsonResponse = httpRequestDispatcher.performPOST(url, userSignUpInfoDto);
            LoginResponse actualLoginResponse = objectMapper.readValue(jsonResponse, LoginResponse.class);

            assertThat(actualLoginResponse.getUserInfo())
                    .usingRecursiveComparison()
                    .ignoringFields("address")
                    .isEqualTo(userSignUpInfoDto);
            Optional<User> userFromDatabase = userRepository.findByEmailIgnoreCase(userSignUpInfoDto.getEmail());
            assertTrue(userFromDatabase.isPresent());
            assertEquals(Role.MERCHANT, userFromDatabase.get().getRole());
        }

        @Test
        @DisplayName("Should be required to use a unique email address")
        void shouldRequireUniqueEmail() throws Exception {
            String emailAlreadyInDatabase = "john_customer@example.com";
            String errorMessage = new EmailExistsException(emailAlreadyInDatabase).getMessage();

            UserCreationDto userSignUpInfoDto = new UserCreationDto();
            userSignUpInfoDto.setEmail(emailAlreadyInDatabase);
            userSignUpInfoDto.setPassword("password");
            userSignUpInfoDto.setRole(Role.CUSTOMER);
            userSignUpInfoDto.setFirstName("John");
            userSignUpInfoDto.setLastName("Matthews");

            String url = "/api/users/register";
            String jsonResponse = httpRequestDispatcher.performPOST(url, userSignUpInfoDto, status().isConflict());

            assertContainsErrorMessage(jsonResponse, errorMessage);
        }
    }

    @Nested
    @DisplayName("[Integration Tests - Through All Layers] When user logs in,")
    class throughAllLayers_user_login {

        @Test
        @DisplayName("Should be successful with correct credentials")
        void shouldLoginOnCorrectCredentials() throws Exception {
            String username = "alice_customer@example.com";
            String password = "password";
            LoginRequest loginRequest = new LoginRequest(username, password);

            String url = "/api/users/login";
            httpRequestDispatcher.performPOST(url, loginRequest, 200);
        }

        @Test
        @DisplayName("Should fail with wrong credentials")
        void shouldFailLoginOnWrongCredentials() throws Exception {
            String username = "alice_customer@example.com";
            String password = "notThePassword";
            LoginRequest loginRequest = new LoginRequest(username, password);

            String url = "/api/users/login";
            httpRequestDispatcher.performPOST(url, loginRequest, 403);
        }

        @Test
        @DisplayName("Should fail if account deactivated")
        void shouldFailLoginWithDeactivatedAccount() throws Exception {
            String username = "gregory_customer@example.com";
            String password = "password";
            LoginRequest loginRequest = new LoginRequest(username, password);

            String url = "/api/users/login";
            httpRequestDispatcher.performPOST(url, loginRequest, 401);
        }
    }

    @Nested
    @DisplayName("[Integration Tests - Through All Layers] Should be able to update user info,")
    class throughAllLayers_user_updateUserInfo {

        UserInfoDto updatedUserInfoDto;

        @BeforeEach()
        void setup() {
            AddressDto updatedAddress = new AddressDto();
            updatedAddress.setStreet1("60 E Broadway");
            updatedAddress.setCity("Bloomington");
            updatedAddress.setZipcode("55425");
            updatedAddress.setState(State.MN);
            updatedAddress.setCountry(Country.US);
            updatedAddress.setLatitude(44.8548651);
            updatedAddress.setLongitude(93.2422148);

            updatedUserInfoDto = new UserInfoDto();
            updatedUserInfoDto.setFirstName("Sharon");
            updatedUserInfoDto.setLastName("Miller");
            updatedUserInfoDto.setEmail("sharon@example.com");
            updatedUserInfoDto.setAddress(updatedAddress);
        }

        @Test
        @WithUserDetails("alice_customer@example.com")
        @DisplayName("As a customer")
        void shouldUpdateUserInfo_customer() throws Exception {
            String url = "/api/users";
            httpRequestDispatcher.performPATCH(url, updatedUserInfoDto, 200);

            Optional<User> userFoundByOldEmail = userRepository.findByEmailIgnoreCase("alice_customer@example.com");
            assertFalse(userFoundByOldEmail.isPresent());

            Optional<User> userFoundByNewEmail = userRepository.findByEmailIgnoreCase(updatedUserInfoDto.getEmail());
            assertTrue(userFoundByNewEmail.isPresent());

            CustomerProfile updatedProfileFromDatabase = userFoundByNewEmail.get().getCustomerProfile();
            assertEquals(updatedUserInfoDto.getFirstName(), updatedProfileFromDatabase.getFirstName());
            assertEquals(updatedUserInfoDto.getLastName(), updatedProfileFromDatabase.getLastName());

            Address updatedAddressFromDatabase = updatedProfileFromDatabase.getAddress();
            assertThat(updatedAddressFromDatabase)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(updatedUserInfoDto.getAddress());
        }

        @Test
        @WithUserDetails("rebecca_merchant@example.com")
        @DisplayName("As a merchant")
        void shouldUpdateUserInfo_merchant() throws Exception {
            String url = "/api/users";
            httpRequestDispatcher.performPATCH(url, updatedUserInfoDto, 200);

            Optional<User> userFoundByOldEmail = userRepository.findByEmailIgnoreCase("rebecca_customer@example.com");
            assertFalse(userFoundByOldEmail.isPresent());

            Optional<User> userFoundByNewEmail = userRepository.findByEmailIgnoreCase(updatedUserInfoDto.getEmail());
            assertTrue(userFoundByNewEmail.isPresent());

            MerchantProfile updatedProfileFromDatabase = userFoundByNewEmail.get().getMerchantProfile();
            assertEquals(updatedUserInfoDto.getFirstName(), updatedProfileFromDatabase.getFirstName());
            assertEquals(updatedUserInfoDto.getLastName(), updatedProfileFromDatabase.getLastName());

            Address updatedAddressFromDatabase = updatedProfileFromDatabase.getAddress();
            assertThat(updatedAddressFromDatabase)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(updatedUserInfoDto.getAddress());
        }
    }

    @Nested
    @DisplayName("[Integration Tests - Through All Layers] Should be able to update user address,")
    class throughAllLayers_user_updateUserAddress {

        AddressDto updatedAddressDto;

        @BeforeEach()
        void setup() {
            updatedAddressDto = new AddressDto();
            updatedAddressDto.setStreet1("60 E Broadway");
            updatedAddressDto.setCity("Bloomington");
            updatedAddressDto.setZipcode("55425");
            updatedAddressDto.setState(State.MN);
            updatedAddressDto.setCountry(Country.US);
            updatedAddressDto.setLatitude(44.8548651);
            updatedAddressDto.setLongitude(93.2422148);
        }

        @Test
        @WithUserDetails("alice_customer@example.com")
        @DisplayName("As a customer")
        void shouldUpdateUserAddress_customer() throws Exception {
            String url = "/api/users/address";
            httpRequestDispatcher.performPATCH(url, updatedAddressDto, 200);

            Optional<User> userFromDatabase = userRepository.findByEmailIgnoreCase("alice_customer@example.com");
            assertTrue(userFromDatabase.isPresent());

            Address updatedAddressFromDatabase = userFromDatabase.get().getCustomerProfile().getAddress();
            assertThat(updatedAddressFromDatabase)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(updatedAddressDto);
        }

        @Test
        @WithUserDetails("rebecca_merchant@example.com")
        @DisplayName("As a merchant")
        void shouldUpdateUserAddress_merchant() throws Exception {
            String url = "/api/users/address";
            httpRequestDispatcher.performPATCH(url, updatedAddressDto, 200);

            Optional<User> userFromDatabase = userRepository.findByEmailIgnoreCase("rebecca_merchant@example.com");
            assertTrue(userFromDatabase.isPresent());

            Address updatedAddressFromDatabase = userFromDatabase.get().getMerchantProfile().getAddress();
            assertThat(updatedAddressFromDatabase)
                    .usingRecursiveComparison()
                    .ignoringFields("id")
                    .isEqualTo(updatedAddressDto);
        }
    }
}

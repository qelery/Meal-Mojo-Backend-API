package com.qelery.mealmojo.api.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.model.dto.UserCreationDtoIn;
import com.qelery.mealmojo.api.model.dto.UserCreationDtoOut;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.request.LoginRequest;
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
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static com.qelery.mealmojo.api.testUtils.CustomAssertions.assertContainsErrorMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        SecurityContextHolder.clearContext();;
    }

    @Nested
    @DisplayName("[Integration Tests - Through All Layers] When user registers,")
    class throughAllLayers_user_register {

        @Test
        @DisplayName("Should be able to sign up for a Customer account")
        void shouldRegisterCustomer() throws Exception {
            UserCreationDtoIn userSignUpInfoDto = new UserCreationDtoIn();
            userSignUpInfoDto.setEmail("gordon@example.com");
            userSignUpInfoDto.setPassword("password");
            userSignUpInfoDto.setRole(Role.CUSTOMER);
            userSignUpInfoDto.setFirstName("Gordon");
            userSignUpInfoDto.setLastName("Ramsay");

            String url = "/auth/users/register";
            String jsonResponse = httpRequestDispatcher.performPOST(url, userSignUpInfoDto);
            UserCreationDtoOut actualUserCreationDtoOut = objectMapper.readValue(jsonResponse, UserCreationDtoOut.class);

            assertThat(actualUserCreationDtoOut).usingRecursiveComparison().isEqualTo(userSignUpInfoDto);
            Optional<User> userFromDatabase = userRepository.findByEmail(userSignUpInfoDto.getEmail());
            assertTrue(userFromDatabase.isPresent());
            assertEquals(Role.CUSTOMER, userFromDatabase.get().getRole());
        }

        @Test
        @DisplayName("Should be able to sign up for a Merchant account")
        void shouldRegisterMerchant() throws Exception {
            UserCreationDtoIn userSignUpInfoDto = new UserCreationDtoIn();
            userSignUpInfoDto.setEmail("julia@example.com");
            userSignUpInfoDto.setPassword("password");
            userSignUpInfoDto.setRole(Role.MERCHANT);
            userSignUpInfoDto.setFirstName("Julia");
            userSignUpInfoDto.setLastName("Child");

            String url = "/auth/users/register";
            String jsonResponse = httpRequestDispatcher.performPOST(url, userSignUpInfoDto);
            UserCreationDtoOut actualUserCreationDtoOut = objectMapper.readValue(jsonResponse, UserCreationDtoOut.class);

            assertThat(actualUserCreationDtoOut).usingRecursiveComparison().isEqualTo(userSignUpInfoDto);
            Optional<User> userFromDatabase = userRepository.findByEmail(userSignUpInfoDto.getEmail());
            assertTrue(userFromDatabase.isPresent());
            assertEquals(Role.MERCHANT, userFromDatabase.get().getRole());
        }

        @Test
        @DisplayName("Should be required to use a unique email address")
        void shouldRequireUniqueEmail() throws Exception {
            String emailAlreadyInDatabase = "john_customer@example.com";
            String errorMessage = new EmailExistsException(emailAlreadyInDatabase).getMessage();

            UserCreationDtoIn userSignUpInfoDto = new UserCreationDtoIn();
            userSignUpInfoDto.setEmail(emailAlreadyInDatabase);
            userSignUpInfoDto.setPassword("password");
            userSignUpInfoDto.setRole(Role.CUSTOMER);
            userSignUpInfoDto.setFirstName("John");
            userSignUpInfoDto.setLastName("Matthews");

            String url = "/auth/users/register";
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

            String url = "/auth/users/login";
            httpRequestDispatcher.performPOST(url, loginRequest, 200);
        }

        @Test
        @DisplayName("Should fail with wrong credentials")
        void shouldFailLoginOnWrongCredentials() throws Exception {
            String username = "alice_customer@example.com";
            String password = "notThePassword";
            LoginRequest loginRequest = new LoginRequest(username, password);

            String url = "/auth/users/login";
            httpRequestDispatcher.performPOST(url, loginRequest, 403);
        }

        @Test
        @DisplayName("Should fail if account deactivated")
        void shouldFailLoginWithDeactivatedAccount() throws Exception {
            String username = "gregory_customer@example.com";
            String password = "password";
            LoginRequest loginRequest = new LoginRequest(username, password);

            String url = "/auth/users/login";
            httpRequestDispatcher.performPOST(url, loginRequest, 403);
        }
    }
}

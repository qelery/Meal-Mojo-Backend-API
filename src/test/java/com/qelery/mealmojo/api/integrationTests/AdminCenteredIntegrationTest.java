package com.qelery.mealmojo.api.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qelery.mealmojo.api.exception.UserNotFoundException;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.testUtils.HttpRequestDispatcher;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static com.qelery.mealmojo.api.testUtils.CustomAssertions.assertContainsErrorMessage;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude = FlywayAutoConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Sql(scripts = {"/seed/load-test-data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/seed/clear-test-data.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class AdminCenteredIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    HttpRequestDispatcher httpRequestDispatcher;

    @Autowired
    UserRepository userRepository;

    @Nested
    @DisplayName("[Integration Tests - Through All Layers] With admin logged in,")
    class throughAllLayers_admin {

        @Test
        @DisplayName("Should be able to deactivate a user")
        @WithUserDetails("admin@example.com")
        void shouldDeactivateUser() throws Exception {
            long activeUsersId = 2L;

            String url = "/admin/users/" + activeUsersId + "/activation?active=" + false;
            httpRequestDispatcher.performPATCH(url);

            Optional<User> user = userRepository.findById(activeUsersId);
            assertTrue(user.isPresent());
            assertFalse(user.get().getIsActive());
        }

        @Test
        @DisplayName("Should be able to re-active a user")
        @WithUserDetails("admin@example.com")
        void shouldActivateUser() throws Exception {
            long deactivatedUsersId = 4L;

            String url = "/admin/users/" + deactivatedUsersId + "/activation?active=" + true;
            httpRequestDispatcher.performPATCH(url);

            Optional<User> user = userRepository.findById(deactivatedUsersId);
            assertTrue(user.isPresent());
            assertTrue(user.get().getIsActive());
        }

        @Test
        @DisplayName("Should get 404 response when updating active status for user that doesn't exist")
        @WithUserDetails("admin@example.com")
        void should404OnNonExistentUserDeactivation() throws Exception {
            long userIdThatDoesNotExist = 5003L;
            String errorMessage = new UserNotFoundException(userIdThatDoesNotExist).getMessage();

            String url = "/admin/users/" + userIdThatDoesNotExist + "/activation?active=" + false;
            String jsonResponse = httpRequestDispatcher.performPATCH(url, 404);

            assertContainsErrorMessage(jsonResponse, errorMessage);
        }
    }
}

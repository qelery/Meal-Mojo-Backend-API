package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.singletons.DockerContaineredDatabaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends DockerContaineredDatabaseTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user information to database")
    void savesUserToDatabase() {
        User expectedUserObject = new User("testuser46782@gmail.com", "password", null, null);
        User actualUserObject = userRepository.save(expectedUserObject);

        assertEquals(expectedUserObject.getEmail(), actualUserObject.getEmail());
        assertEquals(expectedUserObject.getPassword(), actualUserObject.getPassword());
        assertNotEquals("notTheEmailAddress@gmail.com", actualUserObject.getEmail());
        assertNotEquals("notThePassword", actualUserObject.getPassword());
    }

    @Test
    @DisplayName("Should only save user to database if email address is unique")
    void onlySavesUniqueEmail() {
        User user = new User("testuser793140@gmail.com", "password", null, null);
        User userWithNonUniqueEmail = new User("testuser793140@gmail.com", "password", null, null);
        User userWithDifferentEmail = new User("differentEmail@gmail.com", "password", null, null);

        userRepository.save(user);
        userRepository.save(userWithDifferentEmail);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(userWithNonUniqueEmail));
    }

    @Test
    @DisplayName("Should only save user to database if the email and password are not null")
    void onlySavesUsersWithEmailsAndPasswords() {
        User userWithNullEmail = new User(null, "password", null, null);
        User userWithNullPassword = new User("testuser7301985@gmail.com", null, null, null);
        User userWithAllRequiredFields = new User("testuser389210@gmail.com", "password", null, null);

        userRepository.save(userWithAllRequiredFields);

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(userWithNullEmail));
        assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(userWithNullPassword));
    }

    @Test
    @DisplayName("Should find user in database given an email address")
    void findByEmail() {
        User expectedUserObject = new User("testuser46782@gmail.com", "password", null, null);
        userRepository.save(expectedUserObject);
        User actualUserObject = userRepository.findByEmail(expectedUserObject.getEmail());

        assertEquals(expectedUserObject.getEmail(), actualUserObject.getEmail());
        assertEquals(expectedUserObject.getPassword(), actualUserObject.getPassword());
        assertNotEquals("notTheEmailAddress@gmail.com", actualUserObject.getEmail());
        assertNotEquals("notThePassword", actualUserObject.getPassword());
    }

    @Test
    @DisplayName("Should check if user with given email address exists in database")
    void existsByEmail() {
        User expectedUserObject = new User("testuser46782@gmail.com", "password", null, null);
        userRepository.save(expectedUserObject);

        assertTrue(userRepository.existsByEmail(expectedUserObject.getEmail()));
        assertFalse(userRepository.existsByEmail("notTheEmailAddress@gmail.com"));
    }
}
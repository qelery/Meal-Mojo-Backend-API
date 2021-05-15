package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.singletons.DatabaseTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends DatabaseTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should save user information to database")
    void shouldSaveUser() {
        User expectedUserObject = new User("testuser46782@gmail.com", "password", null, null);
        User actualUserObject = userRepository.save(expectedUserObject);

        assertEquals(expectedUserObject.getEmail(), actualUserObject.getEmail());
        assertEquals(expectedUserObject.getPassword(), actualUserObject.getPassword());
        assertNotEquals("notTheEmailAddress@gmail.com", actualUserObject.getEmail());
        assertNotEquals("notThePassword", actualUserObject.getPassword());
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

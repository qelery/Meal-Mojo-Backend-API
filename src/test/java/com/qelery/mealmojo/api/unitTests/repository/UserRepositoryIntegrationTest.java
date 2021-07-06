//package com.qelery.mealmojo.api.unitTests.repository;
//
//import com.qelery.mealmojo.api.model.User;
//import com.qelery.mealmojo.api.model.enums.Role;
//import com.qelery.mealmojo.api.repository.UserRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.dao.DataIntegrityViolationException;
//import com.qelery.mealmojo.api.unitTests.singletons.DockerContaineredDatabaseTest;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
//class UserRepositoryIntegrationTest extends DockerContaineredDatabaseTest {
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    @DisplayName("Should save user information to database")
//    void savesUserToDatabase() {
//        User expectedUserObject = new User(1L, "testuser540@gmail.com", "password", Role.CUSTOMER, null, null, null);
//        User actualUserObject = userRepository.save(expectedUserObject);
//
//        assertEquals(expectedUserObject.getEmail(), actualUserObject.getEmail());
//        assertEquals(expectedUserObject.getPassword(), actualUserObject.getPassword());
//        assertNotEquals("notTheEmailAddress@gmail.com", actualUserObject.getEmail());
//        assertNotEquals("notThePassword", actualUserObject.getPassword());
//    }
//
//    @Test
//    @DisplayName("Should only save user to database if email address is unique")
//    void onlySavesUniqueEmail() {
//        User user = new User(1L, "testuser@gmail.com", "password", Role.CUSTOMER, null, null, null);
//        User userWithNonUniqueEmail = new User(2L, "testuser@gmail.com", "password", Role.CUSTOMER, null, null, null);
//        User userWithDifferentEmail = new User(3L, "differentEmail@gmail.com", "password", Role.CUSTOMER, null, null, null);
//
//        userRepository.save(user);
//        userRepository.save(userWithDifferentEmail);
//
//        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(userWithNonUniqueEmail));
//    }
//
//    @Test
//    @DisplayName("Should only save user to database if the non-nullable fields, email and password are not null")
//    void savesOnlyWhenNonNullableFieldsAreNotNull() {
//        User userWithNullEmail = new User(1L, null, "password", Role.CUSTOMER, null, null, null);
//        User userWithNullPassword = new User(2L, "testuser730@gmail.com", null, Role.CUSTOMER, null, null, null);
//        User userWithAllRequiredFields = new User(3L, "testuser804@gmail.com", "password", Role.CUSTOMER, null, null, null);
//
//        userRepository.save(userWithAllRequiredFields);
//
//        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(userWithNullEmail));
//        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(userWithNullPassword));
//    }
//
//    @Test
//    @DisplayName("Should find user in database given an email address")
//    void shouldBeAbleToFindUserByEmail() {
//        User expectedUserObject = new User(1L, "testuser@gmail.com", "password", Role.CUSTOMER, null, null, null);
//        userRepository.save(expectedUserObject);
//
//        assertTrue(userRepository.existsByEmail(expectedUserObject.getEmail()));
//
//        Optional<User> optionalActualUserObject = userRepository.findByEmail(expectedUserObject.getEmail());
//        if (optionalActualUserObject.isEmpty())  {
//            fail();
//        }
//
//        User actualUserObject = optionalActualUserObject.get();
//
//        assertEquals(expectedUserObject.getEmail(), actualUserObject.getEmail());
//        assertEquals(expectedUserObject.getPassword(), actualUserObject.getPassword());
//
//        assertNotEquals("notTheEmailAddress@gmail.com", actualUserObject.getEmail());
//        assertNotEquals("notThePassword", actualUserObject.getPassword());
//    }
//}

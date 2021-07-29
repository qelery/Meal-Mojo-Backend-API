package unittests.com.qelery.mealmojo.api;

import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtUtils jwtUtils;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;


    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

//    @Test
//    @DisplayName("Should save new user to database and return 201 response")
//    void createUser() {
//        UserService userService = new UserService(userRepository, userDetailsService, passwordEncoder, jwtUtils, authenticationManager, propertyCopier);
//        User user = new User("test@test.com", "password", Role.CUSTOMER);
//
//        ResponseEntity<User> expectedResponse = new ResponseEntity<>(user, HttpStatus.CREATED);
//        ResponseEntity<User> actualResponse = userService.createUserWithCustomerRole(user);
//        assertEquals(expectedResponse, actualResponse);
//
//        Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());
//
//        assertEquals("test@test.com", userArgumentCaptor.getValue().getEmail());
//    }

}

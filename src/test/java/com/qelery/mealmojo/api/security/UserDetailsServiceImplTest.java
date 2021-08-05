package com.qelery.mealmojo.api.security;

import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("Should return UserDetails for given email")
    void loadUserByUsername() {
        User user = new User();
        UserDetails expectedUserDetails = new UserDetailsImpl(user);
        when(userRepository.findByEmail(anyString())).thenReturn(java.util.Optional.of(user));

        UserDetails actualUserDetails = userDetailsService.loadUserByUsername("example@google.com");

        assertEquals(actualUserDetails, expectedUserDetails);
    }

    @Test
    @DisplayName("Should throw exception when trying to retrieve UserDetails for an email that isn't assigned to a user")
    void loadUserByUsernameThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("example@google.com"));
    }
}

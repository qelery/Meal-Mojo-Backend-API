package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.login.LoginRequest;
import com.qelery.mealmojo.api.model.login.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserDetailsService userDetailsService,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<String> createUserWithCustomerRole(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailExistsException(user.getEmail());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.CUSTOMER);
            userRepository.save(user);
            String message = "Successfully registered new user with email address " + user.getEmail();
            return ResponseEntity.status(201).body(message);
        }
    }

    public ResponseEntity<String> createUserWithMerchantRole(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailExistsException(user.getEmail());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.MERCHANT);
            userRepository.save(user);
            String message = "Successfully registered new user with email address " + user.getEmail();
            return ResponseEntity.status(201).body(message);
        }
    }

    public ResponseEntity<LoginResponse> loginUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String JWT = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(JWT));
    }

    public ResponseEntity<String> grantRoleToUser(Long userId, String role) {
        Role assignedRole = getLoggedInUser().getRole();
        // only Admin users can change another user's role
        if (assignedRole.equals(Role.ADMIN)) {
            Optional<User> optionalUser = userRepository.findById(userId);
            User userToChange = optionalUser.orElseThrow(() -> new UsernameNotFoundException("Could not find User by id " + userId));
            userToChange.setRole(Role.valueOf(role.toUpperCase()));
            userRepository.save(userToChange);
            return ResponseEntity.ok("User with id " + userId + " has been granted the role " + role.toUpperCase());
        } else {
            String message = "Unauthorized. Must be ADMIN to change a user's role";
            return ResponseEntity.status(401).body(message);
        }
    }


    public void createDefaultAdmin() {
        boolean anAdminAccountExists = userRepository.existsByRole(Role.ADMIN);
        if (!anAdminAccountExists) {
            User user = new User();
            user.setEmail("admin");
            user.setPassword(passwordEncoder.encode("admin"));
            user.setRole(Role.ADMIN);
            userRepository.save(user);
        }
    }

    private User getLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}

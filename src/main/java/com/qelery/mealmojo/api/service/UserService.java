package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.model.Address;
import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.form.UserInfo;
import com.qelery.mealmojo.api.model.login.LoginRequest;
import com.qelery.mealmojo.api.model.login.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
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
    private final PropertyCopier propertyCopier;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserDetailsService userDetailsService,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager,
                       PropertyCopier propertyCopier) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.propertyCopier = propertyCopier;
    }

    public ResponseEntity<String> createUserWithCustomerRole(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailExistsException(user.getEmail());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.CUSTOMER);
            userRepository.save(user);
            String message = "Registered user, " + user.getEmail() + ", with the Customer role.";
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
            String message = "Registered user, " + user.getEmail() + ", with the Merchant role.";
            return ResponseEntity.status(201).body(message);
        }
    }

    public ResponseEntity<LoginResponse> loginUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String JWT = jwtUtils.generateToken(userDetails);
        return ResponseEntity.ok(new LoginResponse(JWT));
    }

    public ResponseEntity<String> updateUserInfo(UserInfo updatedUserInfo) {
        User currentUserInfo = getLoggedInUser();
        if (updatedUserInfo.getPassword() != null) {
            updatedUserInfo.setPassword(passwordEncoder.encode(updatedUserInfo.getPassword()));
        }
        Address currentAddress = currentUserInfo.getAddress();
        propertyCopier.copyNonNull(updatedUserInfo, currentUserInfo);

        if (currentAddress != null) {
            propertyCopier.copyNonNull(updatedUserInfo.getAddress(), currentAddress);
        }
        currentUserInfo.setAddress(currentAddress);
        userRepository.save(currentUserInfo);
        return ResponseEntity.ok("User updated");
    }

    private User getLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}

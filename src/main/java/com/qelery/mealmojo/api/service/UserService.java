package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.model.user.Customer;
import com.qelery.mealmojo.api.model.user.Merchant;
import com.qelery.mealmojo.api.model.login.LoginRequest;
import com.qelery.mealmojo.api.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(CustomerRepository userRepository,
                       UserDetailsService userDetailsService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> createMerchantUser(Merchant merchant) {
        return ResponseEntity.ok("");
    }

    public ResponseEntity<?> createCustomerUser(Customer customer) {
        return ResponseEntity.ok("");
    }

    public ResponseEntity<?> loginUser(LoginRequest loginRequest) {
        return ResponseEntity.ok("");
    }

    public User findByEmail(String email) {
        if (customerRepository.find)
    }
}

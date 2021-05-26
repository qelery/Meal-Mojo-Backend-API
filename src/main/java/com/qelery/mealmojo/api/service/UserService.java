package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.model.Address;
import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.request.UserInfoRequest;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsImpl;
import com.qelery.mealmojo.api.security.UserDetailsServiceImpl;
import com.qelery.mealmojo.api.service.utility.PropertyCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final PropertyCopier propertyCopier;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserDetailsServiceImpl userDetailsService,
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

    public void createUserWithCustomerRole(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailExistsException(user.getEmail());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.CUSTOMER);
            userRepository.save(user);
        }
    }

    public void createUserWithMerchantRole(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailExistsException(user.getEmail());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(Role.MERCHANT);
            userRepository.save(user);
        }
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        final UserDetailsImpl userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String JWT = jwtUtils.generateToken(userDetails);
        System.out.println(userDetails.getUser().getAddress());
        return new LoginResponse(JWT, userDetails.getUser().getAddress());
    }

    public ResponseEntity<User> updateUserInfo(UserInfoRequest updatedUserInfoRequest) {
        User currentUserInfo = getLoggedInUser();
        if (updatedUserInfoRequest.getPassword() != null) {
            updatedUserInfoRequest.setPassword(passwordEncoder.encode(updatedUserInfoRequest.getPassword()));
        }
        Address currentAddress = currentUserInfo.getAddress();
        propertyCopier.copyNonNull(updatedUserInfoRequest, currentUserInfo);
        if (currentAddress != null) {
            propertyCopier.copyNonNull(updatedUserInfoRequest.getAddress(), currentAddress);
        } else {
            currentAddress = updatedUserInfoRequest.getAddress();
        }
        currentUserInfo.setAddress(currentAddress);
        userRepository.save(currentUserInfo);
        return new ResponseEntity<>(currentUserInfo, HttpStatus.OK);
    }

    private User getLoggedInUser() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().
                getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }
}

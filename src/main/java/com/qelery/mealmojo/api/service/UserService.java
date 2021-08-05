package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.model.dto.UserDtoIn;
import com.qelery.mealmojo.api.model.dto.UserDtoOut;
import com.qelery.mealmojo.api.model.entity.CustomerProfile;
import com.qelery.mealmojo.api.model.entity.MerchantProfile;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.model.enums.Role;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.security.JwtUtils;
import com.qelery.mealmojo.api.security.UserDetailsServiceImpl;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MapperUtils mapperUtils;

    @Autowired
    public UserService(UserRepository userRepository,
                       UserDetailsServiceImpl userDetailsService,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       AuthenticationManager authenticationManager,
                       MapperUtils mapperUtils) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.mapperUtils = mapperUtils;
    }

    public UserDtoOut createUser(UserDtoIn userDtoIn) {
        User user = mapperUtils.map(userDtoIn, User.class);
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailExistsException(user.getEmail());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getRole() == Role.CUSTOMER) {
                CustomerProfile customerProfile = new CustomerProfile();
                customerProfile.setFirstName(userDtoIn.getFirstName());
                customerProfile.setLastName(userDtoIn.getLastName());
                user.setCustomerProfile(customerProfile);
            } else {
                MerchantProfile merchantProfile = new MerchantProfile();
                merchantProfile.setFirstName(userDtoIn.getFirstName());
                merchantProfile.setLastName(userDtoIn.getLastName());
                user.setMerchantProfile(merchantProfile);
            }
            userRepository.save(user);
            return mapperUtils.map(user, UserDtoOut.class);
        }
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final String JWT = jwtUtils.generateToken(userDetails);
        return new LoginResponse(JWT);
    }
}

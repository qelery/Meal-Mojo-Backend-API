package com.qelery.mealmojo.api.service;

import com.qelery.mealmojo.api.exception.EmailExistsException;
import com.qelery.mealmojo.api.exception.ProhibitedByRoleException;
import com.qelery.mealmojo.api.exception.UserNotFoundException;
import com.qelery.mealmojo.api.model.dto.UserCreationDto;
import com.qelery.mealmojo.api.model.dto.UserInfoDto;
import com.qelery.mealmojo.api.model.entity.*;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public LoginResponse createUser(UserCreationDto userCreationDto) {
        User user = mapperUtils.map(userCreationDto, User.class);

        if (userRepository.existsByEmailIgnoreCase(user.getEmail())) {
            throw new EmailExistsException(user.getEmail());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getRole() == Role.CUSTOMER) {
                CustomerProfile customerProfile = new CustomerProfile();
                customerProfile.setFirstName(userCreationDto.getFirstName());
                customerProfile.setLastName(userCreationDto.getLastName());
                user.setCustomerProfile(customerProfile);
            } else {
                MerchantProfile merchantProfile = new MerchantProfile();
                merchantProfile.setFirstName(userCreationDto.getFirstName());
                merchantProfile.setLastName(userCreationDto.getLastName());
                user.setMerchantProfile(merchantProfile);
            }
            userRepository.save(user);
        }
        LoginRequest loginRequest = new LoginRequest(userCreationDto.getEmail(), userCreationDto.getPassword());
        return loginUser(loginRequest);
    }

    public LoginResponse loginUser(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        final String JWT = jwtUtils.generateToken(userDetails);
        UserInfoDto userInfoDto = mapperUtils.map((User) userDetails, UserInfoDto.class);
        return new LoginResponse(JWT, userInfoDto);
    }

    public UserInfoDto changeUserActiveState(Long userId, Boolean active) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException(userId));
        user.setIsActive(active);
        userRepository.save(user);
        return mapperUtils.map(user, UserInfoDto.class);
    }

    public UserInfoDto updateUser(UserInfoDto updatedUserInfo) {
        User loggedInUser = getLoggedInUser();
        loggedInUser.setEmail(updatedUserInfo.getEmail());
        Profile profile = loggedInUser.getRole() == Role.CUSTOMER ?
                loggedInUser.getCustomerProfile() :
                loggedInUser.getMerchantProfile();
        profile.setFirstName(updatedUserInfo.getFirstName());
        profile.setLastName(updatedUserInfo.getLastName());
        Address updatedAddress = mapperUtils.map(updatedUserInfo.getAddress(), Address.class);
        profile.setAddress(updatedAddress);
        userRepository.save(loggedInUser);
        return updatedUserInfo;
    }

    public Role getLoggedInUserRole() {
        return getLoggedInUser().getRole();
    }

    public CustomerProfile getLoggedInCustomerProfile() {
        if (getLoggedInUserRole() != Role.CUSTOMER) {
            throw new ProhibitedByRoleException();
        }
        return getLoggedInUser().getCustomerProfile();
    }

    public MerchantProfile getLoggedInUserMerchantProfile() {
        if (getLoggedInUserRole() != Role.MERCHANT) {
            throw new ProhibitedByRoleException();
        }
        return getLoggedInUser().getMerchantProfile();
    }

    private User getLoggedInUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}

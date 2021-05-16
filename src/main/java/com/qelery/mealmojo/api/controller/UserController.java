package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.model.login.LoginRequest;
import com.qelery.mealmojo.api.model.login.LoginResponse;
import com.qelery.mealmojo.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/auth/users")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void createDefaultAdmin() {
        userService.createDefaultAdmin();
    }

    @PostMapping("/register/customer")
    public ResponseEntity<String> createUserWithCustomerRole(@RequestBody User user) {
        return userService.createUserWithCustomerRole(user);
    }

    @PostMapping("/register/merchant")
    public ResponseEntity<String> createUserWithMerchantRole(@RequestBody User user) {
        return userService.createUserWithMerchantRole(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }

    @GetMapping("/access/{userId}/{userRole}")
    public ResponseEntity<String> grantRoleToUser(@PathVariable Long userId,
                                                 @PathVariable String userRole) {
        return userService.grantRoleToUser(userId, userRole);
    }

}

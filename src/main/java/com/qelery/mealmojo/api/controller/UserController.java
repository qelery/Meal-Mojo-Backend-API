package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.user.Customer;
import com.qelery.mealmojo.api.model.user.Merchant;
import com.qelery.mealmojo.api.model.login.LoginRequest;
import com.qelery.mealmojo.api.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {

    private UserService userService;

    @PostMapping("/merchant/register")
    public ResponseEntity<?> createMerchantUser(@RequestBody Merchant merchant) {
        return userService.createMerchantUser(merchant);
    }

    @PostMapping("/customer/register")
    public ResponseEntity<?> createCustomerUser(@RequestBody Customer customer) {
        return userService.createCustomerUser(customer);
    }

    @PostMapping(value={"/merchant/login", "/customer/login"})
    public ResponseEntity<?> loginMerchantAccount(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }
}

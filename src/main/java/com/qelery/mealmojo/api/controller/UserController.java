package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.UserCreationDtoIn;
import com.qelery.mealmojo.api.model.dto.UserCreationDtoOut;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserCreationDtoOut createUser(@RequestBody UserCreationDtoIn userDto) {
        return userService.createUser(userDto);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginUser(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }
}

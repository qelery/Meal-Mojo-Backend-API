package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.UserCreationDto;
import com.qelery.mealmojo.api.model.dto.UserInfoDto;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse createUser(@RequestBody UserCreationDto userDto) {
        return userService.createUser(userDto);
    }

    @PostMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse loginUser(@RequestBody LoginRequest loginRequest) {
        return userService.loginUser(loginRequest);
    }

    @PatchMapping("/users/{userId}/activation")
    @ResponseStatus(HttpStatus.OK)
    public UserInfoDto setUserActiveState(@PathVariable Long userId,
                                          @RequestParam Boolean active) {
        return userService.changeUserActiveState(userId, active);
    }
}

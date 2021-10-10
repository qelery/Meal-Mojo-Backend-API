package com.qelery.mealmojo.api.unitTests.controller;

import com.qelery.mealmojo.api.controller.UserController;
import com.qelery.mealmojo.api.model.dto.UserCreationDto;
import com.qelery.mealmojo.api.model.dto.UserInfoDto;
import com.qelery.mealmojo.api.model.request.LoginRequest;
import com.qelery.mealmojo.api.model.response.LoginResponse;
import com.qelery.mealmojo.api.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Test
    @DisplayName("Should return user dto from service after creating user")
    void createUser() {
        LoginResponse expectedResponse = new LoginResponse("myJwtToken", new UserInfoDto());
        when(userService.createUser(any(UserCreationDto.class)))
                .thenReturn(expectedResponse);

        LoginResponse actualResponse = userController.createUser(new UserCreationDto());

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Should return LoginResponse from service after logging in user")
    void loginUser() {
        LoginResponse expectedResponse = new LoginResponse("myJwtToken", new UserInfoDto());
        when(userService.loginUser(any(LoginRequest.class)))
                .thenReturn(expectedResponse);

        LoginResponse actualResponse = userController.loginUser(new LoginRequest("john@example.com", "password"));

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Should return from service user with new active state")
    void changeUserActiveState() {
        UserInfoDto expectedUserInfoDto = new UserInfoDto();
        when(userService.changeUserActiveState(anyLong(), anyBoolean()))
                .thenReturn(expectedUserInfoDto);

        UserInfoDto actualUserInfoDto = userController.setUserActiveState(1L, false);
        assertEquals(actualUserInfoDto, expectedUserInfoDto);
    }

    @Test
    @DisplayName("Should return from service user dto with update info")
    void updateUser() {
        UserInfoDto expectedUserInfoDto = new UserInfoDto();
        when(userService.updateUser(any(UserInfoDto.class)))
                .thenReturn(expectedUserInfoDto);

        UserInfoDto actualUserInfoDto = userController.updateUser(new UserInfoDto());
        assertEquals(actualUserInfoDto, expectedUserInfoDto);
    }
}

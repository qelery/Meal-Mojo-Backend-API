package com.qelery.mealmojo.api.unitTests.controller;

import com.qelery.mealmojo.api.controller.UserController;
import com.qelery.mealmojo.api.model.dto.UserCreationDtoIn;
import com.qelery.mealmojo.api.model.dto.UserCreationDtoOut;
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
        UserCreationDtoOut expectedDto = new UserCreationDtoOut();
        when(userService.createUser(any(UserCreationDtoIn.class)))
                .thenReturn(expectedDto);

        UserCreationDtoOut actualDto = userController.createUser(new UserCreationDtoIn());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return LoginResponse/JWT from service after logging in user")
    void loginUser() {
        LoginResponse expectedResponse = new LoginResponse("myJwtToken");
        when(userService.loginUser(any(LoginRequest.class)))
                .thenReturn(expectedResponse);

        LoginResponse actualResponse = userController.loginUser(new LoginRequest("john@example.com", "password"));

        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    @DisplayName("Should return from service user with new active state")
    void changeUserActiveState() {
        UserCreationDtoOut expectedUserCreationDtoOut = new UserCreationDtoOut();
        when(userService.changeUserActiveState(anyLong(), anyBoolean()))
                .thenReturn(expectedUserCreationDtoOut);

        UserCreationDtoOut actualUserCreationDtoOUt = userController.setUserActiveState(1L, false);
        assertEquals(actualUserCreationDtoOUt, expectedUserCreationDtoOut);
    }
}

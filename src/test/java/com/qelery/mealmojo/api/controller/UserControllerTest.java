package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.UserDtoIn;
import com.qelery.mealmojo.api.model.dto.UserDtoOut;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService mockUserService;

    @Test
    @DisplayName("Should return user dto from service after creating user")
    void createUser() {
        UserDtoOut expectedDto = new UserDtoOut();
        when(mockUserService.createUser(any(UserDtoIn.class)))
                .thenReturn(expectedDto);

        UserDtoOut actualDto = userController.createUser(new UserDtoIn());

        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("Should return LoginResponse/JWT from service after logging in user")
    void loginUser() {
        LoginResponse expectedResponse = new LoginResponse("myJwtToken");
        when(mockUserService.loginUser(any(LoginRequest.class)))
                .thenReturn(expectedResponse);

        LoginResponse actualResponse = userController.loginUser(new LoginRequest("john@example.org", "password"));

        assertEquals(expectedResponse, actualResponse);
    }
}

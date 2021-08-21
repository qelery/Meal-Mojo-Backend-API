package com.qelery.mealmojo.api.unitTests.controller;

import com.qelery.mealmojo.api.controller.AdminController;
import com.qelery.mealmojo.api.model.dto.UserDtoOut;
import com.qelery.mealmojo.api.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    @Test
    @DisplayName("Should return from service user wiht new active state")
    void changeUserActiveState() {
        UserDtoOut expectedUserDtoOut = new UserDtoOut();
        when(adminService.changeUserActiveState(anyLong(), anyBoolean()))
                .thenReturn(expectedUserDtoOut);

        UserDtoOut actualUserDtoOUt = adminController.setUserActiveState(1L, false);
        assertEquals(actualUserDtoOUt, expectedUserDtoOut);
    }
}

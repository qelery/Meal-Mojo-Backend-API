package com.qelery.mealmojo.api.unitTests.service;

import com.qelery.mealmojo.api.exception.UserNotFoundException;
import com.qelery.mealmojo.api.model.entity.User;
import com.qelery.mealmojo.api.repository.UserRepository;
import com.qelery.mealmojo.api.service.AdminService;
import com.qelery.mealmojo.api.service.utility.MapperUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private UserRepository userRepository;
    @Spy
    MapperUtils mapperUtils;

    @Test
    @DisplayName("Should deactivate a user")
    void changeUserActiveState_deactivate() {
        User activeUser = new User();
        activeUser.setId(1L);
        activeUser.setIsActive(true);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(activeUser));

        adminService.changeUserActiveState(activeUser.getId(), false);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertFalse(savedUser.getIsActive());
    }

    @Test
    @DisplayName("Should re-activate a user")
    void changeUserActiveState_activate() {
        User deactivatedUser = new User();
        deactivatedUser.setId(1L);
        deactivatedUser.setIsActive(false);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(deactivatedUser));

        adminService.changeUserActiveState(deactivatedUser.getId(), true);

        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertTrue(savedUser.getIsActive());
    }

    @Test
    @DisplayName("Should throw error when trying to change active status on user that does not exist")
    void changeUserActiveState_userNotFound() {
        Long userIdThatDoesNotExist = 4493L;
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> adminService.changeUserActiveState(userIdThatDoesNotExist, true));
    }
}

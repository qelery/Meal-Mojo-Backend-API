package com.qelery.mealmojo.api.controller;

import com.qelery.mealmojo.api.model.dto.UserDtoOut;
import com.qelery.mealmojo.api.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PatchMapping("/users/{userId}")
    public UserDtoOut setUserActiveState(@PathVariable Long userId,
                                         @RequestParam Boolean active) {
        return adminService.changeUserActiveState(userId, active);
    }
}

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


    /**
     * Endpoint that creates default admin.
     *
     * When the app starts up, it check if there exists at least one User with
     * the role Admin. If one does not exist, it creates a User with the admin
     * role and default login credentials admin/admin. After that point, only
     * Users with the roles "customer" and "merchant" can be created, and only
     * existing admins can grant other users the admin role.
     */
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

    /**
     * Endpoint used for changing an existing user's role.
     *
     * Can only be utilized by users with the role admin. Will give 401 status
     * for users with any other role.
     *
     * @param userId   the id of the user whose role should be changes
     * @param userRole the new role of the user
     * @return the 200 status if endpoint was accessed by admin, otherwise 401
     */
    @GetMapping("/access/{userId}/{userRole}")
    public ResponseEntity<String> grantRoleToUser(@PathVariable Long userId,
                                                 @PathVariable String userRole) {
        return userService.grantRoleToUser(userId, userRole);
    }

}

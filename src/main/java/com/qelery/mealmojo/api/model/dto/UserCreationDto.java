package com.qelery.mealmojo.api.model.dto;

import com.qelery.mealmojo.api.model.enums.Role;
import lombok.Data;

@Data
public class UserCreationDto {

    private String email;
    private String password;
    private Role role;
    private String firstName;
    private String lastName;
}

package com.qelery.mealmojo.api.model.response;

import com.qelery.mealmojo.api.model.dto.UserInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private UserInfoDto user;
}

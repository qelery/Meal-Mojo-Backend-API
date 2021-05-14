package com.qelery.mealmojo.api.model.login;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponse {

    private String JWT;

    public LoginResponse(String JWT) {
        this.JWT = JWT;
    }

}

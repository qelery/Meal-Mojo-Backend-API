package com.qelery.mealmojo.api.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LoginResponse {

    private String JWT;

    public LoginResponse(String JWT) {
        this.JWT = JWT;
    }

}

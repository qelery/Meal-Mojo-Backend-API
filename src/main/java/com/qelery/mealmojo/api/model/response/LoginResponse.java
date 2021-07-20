package com.qelery.mealmojo.api.model.response;

import lombok.Data;

@Data
public class LoginResponse {

    private String JWT;

    public LoginResponse(String JWT) {
        this.JWT = JWT;
    }

}

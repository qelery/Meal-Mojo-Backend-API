package com.qelery.mealmojo.api.model.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;
    private String password;
}

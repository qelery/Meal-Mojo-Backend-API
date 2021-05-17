package com.qelery.mealmojo.api.model.form;

import com.qelery.mealmojo.api.model.Address;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class UserInfo {

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private Address address;
}

package com.qelery.mealmojo.api.model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qelery.mealmojo.api.model.enums.Role;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String email;

    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private Role role;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="customer_profile_id", referencedColumnName="id")
    private CustomerProfile customerProfile;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="merchant_profile_id", referencedColumnName="id")
    private CustomerProfile merchantProfile;
}

package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToOne
    @JoinColumn(name="customer_profile_id")
    private CustomerProfile customerProfile;

    @OneToOne
    @JoinColumn(name="merchant_profile_id")
    private MerchantProfile merchantProfile;


    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", customerProfile=" + customerProfile +
                ", merchantProfile=" + merchantProfile +
                '}';
    }
}

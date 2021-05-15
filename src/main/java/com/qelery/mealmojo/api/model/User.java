package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="users")
@Getter @Setter @ToString(exclude={"password"})
@NoArgsConstructor @AllArgsConstructor
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
    @JoinColumn(name="restaurant_profile_id")
    private RestaurantProfile restaurantProfile;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

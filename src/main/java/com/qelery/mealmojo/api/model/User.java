package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qelery.mealmojo.api.model.enums.Role;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

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

    @Column
    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private Role role;  // can either be CUSTOMER or MERCHANT

    @Column
    private String firstName;
    private String lastName;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="address_id")
    private Address address;

    @OneToMany(mappedBy="user")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Restaurant> restaurantsOwned;   // this field will be NULL for users with CUSTOMER role

    @OneToMany(mappedBy="user")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Order> foodOrdersPlaced;   // this field will be NULL for users with MERCHANT role

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
}

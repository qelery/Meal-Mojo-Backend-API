package com.qelery.mealmojo.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qelery.mealmojo.api.model.enums.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"password"})
@NoArgsConstructor
@Table(name="users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String email;

    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;

    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="customer_profile_id", referencedColumnName="id")
    private CustomerProfile customerProfile;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="merchant_profile_id", referencedColumnName="id")
    private MerchantProfile merchantProfile;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.toString()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.isActive;
    }
}

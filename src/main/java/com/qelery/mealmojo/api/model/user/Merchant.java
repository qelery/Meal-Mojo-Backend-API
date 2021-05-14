package com.qelery.mealmojo.api.model.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qelery.mealmojo.api.model.Address;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Merchant implements User {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(unique=true, nullable=false)
    private String email;

    @Column(nullable=false)
    @JsonProperty(access=JsonProperty.Access.WRITE_ONLY)
    private String password;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="address_id")
    private Address address;

    private String business_name;
    private String description;
    private String time_zone;
    private String logo_image_url;
    private Boolean delivery_available;
    private Double delivery_fee;
    private Integer delivery_eta_minutes;
    private Integer pickup_eta_minutes;

    @Override
    public String toString() {
        return "Merchant{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", business_name='" + business_name + '\'' +
                ", description='" + description + '\'' +
                ", time_zone='" + time_zone + '\'' +
                ", logo_image_url='" + logo_image_url + '\'' +
                ", delivery_available=" + delivery_available +
                ", delivery_fee=" + delivery_fee +
                ", delivery_eta_minutes=" + delivery_eta_minutes +
                ", pickup_eta_minutes=" + pickup_eta_minutes +
                ", address=" + address +
                '}';
    }
}

package com.qelery.mealmojo.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class MerchantProfile {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

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
}

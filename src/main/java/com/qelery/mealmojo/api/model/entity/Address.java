package com.qelery.mealmojo.api.model.entity;

import com.qelery.mealmojo.api.model.enums.Country;
import com.qelery.mealmojo.api.model.enums.State;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="address")
public class Address {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String street1;
    private String street2;
    private String street3;
    private String city;
    private String zipcode;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private Country country;

    @Enumerated(EnumType.STRING)
    private State state;
}

package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qelery.mealmojo.api.model.enums.StateAbbreviation;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String street1;
    private String street2;
    private String city;
    private String zipcode;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private StateAbbreviation stateAbbreviation;

}

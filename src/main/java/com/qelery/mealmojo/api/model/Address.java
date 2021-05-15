package com.qelery.mealmojo.api.model;

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

    @Enumerated(EnumType.STRING)
    private StateAbbreviation stateAbbreviation;

}

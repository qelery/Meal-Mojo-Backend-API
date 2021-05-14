package com.qelery.mealmojo.api.model;

import com.qelery.mealmojo.api.model.enums.StateAbbreviation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
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

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street1='" + street1 + '\'' +
                ", street2='" + street2 + '\'' +
                ", city='" + city + '\'' +
                ", zipcode='" + zipcode + '\'' +
                ", stateAbbreviation=" + stateAbbreviation +
                '}';
    }
}

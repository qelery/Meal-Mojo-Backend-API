package com.qelery.mealmojo.api.model.entity;

import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name="merchant_profile")
public class MerchantProfile {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="address_id", referencedColumnName="id")
    private Address address;

    @OneToMany(mappedBy="merchantProfile")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Restaurant> restaurantsOwned;
}

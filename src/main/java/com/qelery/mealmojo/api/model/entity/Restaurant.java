package com.qelery.mealmojo.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@ToString(exclude = {"merchantProfile"})
@Table(name="restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean pickupAvailable;
    private Integer pickupEtaMinutes;
    private Boolean deliveryAvailable;
    private Integer deliveryEtaMinutes;
    private Double deliveryFee;
    private String logoImageUrl;
    private String heroImageUrl;
    private Boolean isActive = true;

    @OneToOne(cascade=CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="address_id", referencedColumnName="id")
    private Address address;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_profile_id", referencedColumnName="id")
    private MerchantProfile merchantProfile;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "restaurant_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OperatingHours> operatingHoursList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy="restaurant")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MenuItem> menuItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy="restaurant")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Order> orders = new ArrayList<>();
}

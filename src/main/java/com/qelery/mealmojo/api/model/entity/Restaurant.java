package com.qelery.mealmojo.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@ToString(exclude = {"merchantProfile"})
@Table(name = "restaurant")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantId;
    private String name;
    private String description;
    private Boolean pickupAvailable;
    private Integer pickupEtaMinutes;
    private Boolean deliveryAvailable;
    private Integer deliveryEtaMinutes;
    private Long deliveryFee; // cents
    private String logoImageUrl;
    private String heroImageUrl;
    private Boolean isActive = true;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "merchant_profile_id")
    private MerchantProfile merchantProfile;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "restaurant_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OperatingHours> operatingHoursList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "restaurant")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MenuItem> menuItems = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "restaurant")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "restaurant_cuisine",
            joinColumns = @JoinColumn(name = "restaurant_id"),
            inverseJoinColumns = @JoinColumn(name = "cuisine_id")
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Cuisine> cuisines = new HashSet<>();
}

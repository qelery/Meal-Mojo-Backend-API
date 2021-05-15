package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.qelery.mealmojo.api.model.enums.Cuisine;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class RestaurantProfile {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String businessName;
    private String description;
    private String timeZone;
    private String logoImageUrl;
    private Boolean deliveryAvailable;
    private Double deliveryFee;
    private Integer deliveryEtaMinutes;
    private Integer pickupEtaMinutes;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="address_id")
    private Address address;

    @OneToMany(mappedBy="restaurantProfile")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OperatingHours> operatingHoursList;

    @OneToMany(mappedBy="restaurantProfile")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MenuItem> menuItems;

    @ElementCollection(targetClass=Cuisine.class)
    @CollectionTable(name="restaurant_profile_cuisine", joinColumns=@JoinColumn(name="restaurant_profile_id"))
    @Column(name="cuisine")
    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private Set<Cuisine> cuisineSet = new HashSet<>();

    @JsonIgnore
    @OneToOne(mappedBy="restaurantProfile")
    private User user;
}

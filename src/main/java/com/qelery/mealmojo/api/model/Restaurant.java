package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qelery.mealmojo.api.model.enums.Cuisine;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @ToString(exclude={"user"})
@NoArgsConstructor @AllArgsConstructor
public class Restaurant {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String businessName;
    private String description;
    private String timeZone;
    private String logoUrl;
    private String heroImageUrl;
    private Boolean deliveryAvailable;
    private Double deliveryFee;
    private Integer deliveryEtaMinutes;
    private Integer pickupEtaMinutes;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="address_id")
    private Address address;

    @OneToMany(mappedBy="restaurant")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OperatingHours> operatingHoursList;

    @OneToMany(mappedBy="restaurant")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<MenuItem> menuItems;

    @OneToMany(mappedBy="restaurant")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Order> orders;

    @ElementCollection(targetClass=Cuisine.class)
    @CollectionTable(name="restaurant_cuisine", joinColumns=@JoinColumn(name="restaurant_id"))
    @Column(name="cuisine")
    @Enumerated(EnumType.STRING)
    @JsonFormat(with=JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private Set<Cuisine> cuisineSet = new HashSet<>();

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}

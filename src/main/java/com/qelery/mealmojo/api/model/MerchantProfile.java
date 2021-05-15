package com.qelery.mealmojo.api.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import com.qelery.mealmojo.api.model.enums.Cuisine;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter @NoArgsConstructor
public class MerchantProfile {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name="address_id")
    private Address address;

    @OneToMany(mappedBy="merchantProfile")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<OperatingHours> operatingHoursList;

    @ElementCollection(targetClass=Cuisine.class)
    @CollectionTable(name="merchant_profile_cuisine", joinColumns=@JoinColumn(name="merchant_profile_id"))
    @Column(name="cuisine")
    @Enumerated(EnumType.STRING)
    private final Set<Cuisine> cuisineSet = new HashSet<>();

    private String business_name;
    private String description;
    private String time_zone;
    private String logo_image_url;
    private Boolean delivery_available;
    private Double delivery_fee;
    private Integer delivery_eta_minutes;
    private Integer pickup_eta_minutes;
}

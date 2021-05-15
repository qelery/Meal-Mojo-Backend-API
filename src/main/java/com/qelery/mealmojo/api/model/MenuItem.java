package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean available;
    private String category;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_profile_id")
    private RestaurantProfile restaurantProfile;

}

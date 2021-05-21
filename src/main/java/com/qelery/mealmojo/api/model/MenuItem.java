package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString(exclude={"restaurant"})
@NoArgsConstructor @AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;
    private Double price;
    private String imageUrl;
    private Boolean available = true;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;

    @Column(name="restaurant_id", insertable=false, nullable=false, updatable=false)
    private Integer restaurantId;
}

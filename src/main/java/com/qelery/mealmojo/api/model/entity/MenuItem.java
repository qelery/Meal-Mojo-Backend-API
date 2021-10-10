package com.qelery.mealmojo.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@Entity
@ToString(exclude = {"restaurant"})
@Table(name="menu_item")
public class MenuItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Long price; // cents
    private String imageUrl;
    private Boolean isAvailable = true;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="restaurant_id", referencedColumnName="id")
    private Restaurant restaurant;
}

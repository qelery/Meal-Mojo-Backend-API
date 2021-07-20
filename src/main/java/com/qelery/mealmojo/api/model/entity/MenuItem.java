package com.qelery.mealmojo.api.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="menu_item")
public class MenuItem {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double price;
    private String imageUrl;
    private Boolean available = true;

    @ManyToOne
    @JoinColumn(name="restaurant_id", referencedColumnName="id")
    private Restaurant restaurant;
}

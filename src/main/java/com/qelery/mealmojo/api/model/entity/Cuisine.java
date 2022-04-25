package com.qelery.mealmojo.api.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="cuisine")
public class Cuisine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cuisineId;

    @Column(name = "category")
    private String category;
}

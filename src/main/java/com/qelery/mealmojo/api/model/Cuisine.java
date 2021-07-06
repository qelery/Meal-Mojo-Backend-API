package com.qelery.mealmojo.api.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="customer_profile")
public class Cuisine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String category;
}

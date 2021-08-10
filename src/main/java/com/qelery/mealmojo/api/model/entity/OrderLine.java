package com.qelery.mealmojo.api.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="order_line")
public class OrderLine {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private Integer quantity;
    private Double priceEach;

    @ManyToOne
    @JoinColumn(name="menu_item_id", referencedColumnName="id")
    private MenuItem menuItem;
}

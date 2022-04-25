package com.qelery.mealmojo.api.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="order_line")
public class OrderLine {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long orderLineId;
    private Integer quantity;
    private Long priceEach; // cents

    @ManyToOne
    @JoinColumn(name="menu_item_id")
    private MenuItem menuItem;
}

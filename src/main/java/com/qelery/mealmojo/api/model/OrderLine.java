package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor @AllArgsConstructor
public class OrderLine {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer quantity;
    private Double price_each;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="order_id")
    private Order order;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="menu_item_id")
    private MenuItem menuItem;

}

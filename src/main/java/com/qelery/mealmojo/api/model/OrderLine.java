package com.qelery.mealmojo.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter @NoArgsConstructor
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

    public OrderLine(Integer quantity, Double price_each, Order order, MenuItem menuItem) {
        this.quantity = quantity;
        this.price_each = price_each;
        this.order = order;
        this.menuItem = menuItem;
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", price_each=" + price_each +
                ", order=" + order +
                ", menuItem=" + menuItem +
                '}';
    }
}

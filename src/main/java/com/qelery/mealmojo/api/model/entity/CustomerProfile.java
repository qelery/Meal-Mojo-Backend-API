package com.qelery.mealmojo.api.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name="customer_profile")
public class CustomerProfile extends Profile {

    @OneToMany(mappedBy="customerProfile")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Order> placedOrders = new ArrayList<>();

    public List<Order> getPlacedOrders() {
        return this.placedOrders;
    }
}

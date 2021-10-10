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
@Table(name = "merchant_profile")
public class MerchantProfile extends Profile {


    @OneToMany(mappedBy = "merchantProfile")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Restaurant> restaurantsOwned = new ArrayList<>();

    public List<Restaurant> getRestaurantsOwned() {
        return this.restaurantsOwned;
    }

}


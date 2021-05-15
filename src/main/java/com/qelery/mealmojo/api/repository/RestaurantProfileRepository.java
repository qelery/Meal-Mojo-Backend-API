package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.RestaurantProfile;
import lombok.Generated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Generated
public interface RestaurantProfileRepository extends JpaRepository<RestaurantProfile, Long> {
}

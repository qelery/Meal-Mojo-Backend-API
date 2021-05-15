package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.RestaurantProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantProfileRepository extends JpaRepository<RestaurantProfile, Long> {
    Optional<RestaurantProfile> findByIdAndUserId(Long restaurantId, Long userId);
}

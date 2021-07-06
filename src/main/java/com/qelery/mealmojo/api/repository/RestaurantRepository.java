package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
//    Optional<Restaurant> findByIdAndUserId(Long restaurantId, Long userId);
//    List<Restaurant> findAllByUserId(Long userId);
}

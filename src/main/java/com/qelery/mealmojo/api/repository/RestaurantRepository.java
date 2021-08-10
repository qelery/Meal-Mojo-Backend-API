package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findAllByIsActive(Boolean isActive);

    List<Restaurant> findAllByMerchantProfileId(Long merchantProfileId);

    Optional<Restaurant> findByIdAndMerchantProfileId(Long restaurantId, Long merchantProfileId);
}

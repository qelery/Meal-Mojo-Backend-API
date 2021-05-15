package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndRestaurantId(Long id, Long restaurantId);
    List<Order> findAllByRestaurantIdAndUserId(Long restaurantId, Long userId);
    List<Order> findAllByRestaurantId(Long restaurantId);
    List<Order> findAllByUserId(Long userId);
}

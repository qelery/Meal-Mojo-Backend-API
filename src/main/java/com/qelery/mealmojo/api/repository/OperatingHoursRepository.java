package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.entity.OperatingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public interface OperatingHoursRepository extends JpaRepository<OperatingHours, Long> {

    Optional<OperatingHours> findByRestaurantIdAndDayOfWeek(Long restaurantId, DayOfWeek dayOfWeek);
}

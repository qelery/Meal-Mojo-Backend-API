package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.OperatingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public interface OperatingHoursRepository extends JpaRepository<OperatingHours, Long> {

    Optional<OperatingHours> findByRestaurantProfileIdAndDayOfWeek(Long restaurantProfileId, DayOfWeek dayOfWeek);
}

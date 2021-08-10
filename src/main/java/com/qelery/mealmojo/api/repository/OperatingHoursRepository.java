package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.entity.OperatingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperatingHoursRepository extends JpaRepository<OperatingHours, Long> {
}

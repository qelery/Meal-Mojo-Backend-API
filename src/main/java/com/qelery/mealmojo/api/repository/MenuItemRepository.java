package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}

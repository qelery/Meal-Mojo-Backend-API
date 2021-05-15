package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}

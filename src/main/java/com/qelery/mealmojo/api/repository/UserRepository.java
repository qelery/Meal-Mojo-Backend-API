package com.qelery.mealmojo.api.repository;

import com.qelery.mealmojo.api.model.User;
import com.qelery.mealmojo.api.model.enums.Role;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(Role role);
}

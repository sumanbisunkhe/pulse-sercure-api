package com.pulse.api.repo;

import com.pulse.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepo extends JpaRepository<Role, Long> {
    boolean existsByName(String name);
}

package com.marketplace.auth.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.marketplace.auth.models.ERole;
import com.marketplace.auth.models.Role;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}
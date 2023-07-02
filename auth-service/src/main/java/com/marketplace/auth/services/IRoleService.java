package com.marketplace.auth.services;

import java.util.Optional;

import com.marketplace.auth.models.ERole;
import com.marketplace.auth.models.Role;

public interface IRoleService {
	Optional<Role> findByName(ERole name);
}

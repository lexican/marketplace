package com.marketplace.auth.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marketplace.auth.models.ERole;
import com.marketplace.auth.models.Role;
import com.marketplace.auth.repository.RoleRepository;

@Service
public class RoleService implements IRoleService {

	private RoleRepository roleRepository;

	@Autowired
	RoleService(RoleRepository roleRepository) {
		this.roleRepository = roleRepository;
	}

	@Override
	public Optional<Role> findByName(ERole name) {
		return roleRepository.findByName(name);
	}

}

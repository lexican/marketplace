package com.marketplace.auth.services;

import java.util.List;
import java.util.Optional;

import com.marketplace.auth.models.User;

public interface IUserService {
	List<User> getAllUsers();

	Optional<User> findById(Long id);

	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	User save(User user);

	void deleteById(Long id);
}

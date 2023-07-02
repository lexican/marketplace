package com.marketplace.auth.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.auth.exceptions.ObjectNotFoundException;
import com.marketplace.auth.models.User;
import com.marketplace.auth.payload.response.MessageResponse;
import com.marketplace.auth.repository.RoleRepository;
import com.marketplace.auth.services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {
	UserService userService;

	RoleRepository roleRepository;

	// @Autowired
	public UserController(UserService userService, RoleRepository roleRepository) {
		this.userService = userService;
		this.roleRepository = roleRepository;
	}

	@GetMapping(value = "/users")
	public ResponseEntity<List<User>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}

	@GetMapping(value = "/user/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable("id") Long id) {
		Optional<User> user = userService.findById(id);
		if (user.isPresent()) {
			return ResponseEntity.ok(user);
		}
		return ResponseEntity.badRequest().body(new MessageResponse("User does not exist"));
	}

	// Update
	@PutMapping(value = "user/{id}")
	public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @RequestBody User user) {
		User oldUser = userService.findById(id).orElseThrow(() -> new ObjectNotFoundException("User does not exist"));
		oldUser.setEmail(user.getEmail());
		oldUser.setFullName(user.getFullName());
		oldUser.setUsername(user.getUsername());
		oldUser.setBio(user.getBio());
		return ResponseEntity.ok(userService.save(oldUser));
	}

	@DeleteMapping(value = "/user/{id}")
	public ResponseEntity<MessageResponse> deleteUser(@PathVariable("id") Long id) {
		User user = userService.findById(id).orElseThrow(() -> new ObjectNotFoundException("User does not exist"));
		userService.deleteById(user.getId());
		return ResponseEntity.ok(new MessageResponse("User deleted successfully"));
	}

}

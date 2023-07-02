package com.marketplace.auth.controllers;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.auth.exceptions.ObjectNotFoundException;
import com.marketplace.auth.models.ERole;
import com.marketplace.auth.models.Role;
import com.marketplace.auth.models.User;
import com.marketplace.auth.payload.request.SignupRequest;
import com.marketplace.auth.payload.request.response.MessageResponse;
import com.marketplace.auth.services.RoleService;
import com.marketplace.auth.services.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	UserService userService;

	RoleService roleService;

	@Autowired
	public AuthController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}

	@PostMapping(value = "/register")
	public ResponseEntity<MessageResponse> register(@Valid @RequestBody SignupRequest signUpRequest) {

		if (userService.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userService.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}	
		
		User user = User.builder().username(signUpRequest.getUsername()).email(signUpRequest.getEmail()).password(signUpRequest.getPassword()).build();

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleService.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new ObjectNotFoundException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				if (role == "admin") {
					Role adminRole = roleService.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new ObjectNotFoundException("Error: Role is not found."));
					roles.add(adminRole);
				} else {
					Role userRole = roleService.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new ObjectNotFoundException("Error: Role is not found."));
					roles.add(userRole);
				}

			});
		}

		user.setRoles(roles);
		userService.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

}
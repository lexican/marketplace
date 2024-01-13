package com.marketplace.auth.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.auth.config.JwtService;
import com.marketplace.auth.exceptions.ObjectNotFoundException;
import com.marketplace.auth.models.ERole;
import com.marketplace.auth.models.Role;
import com.marketplace.auth.models.User;
import com.marketplace.auth.payload.request.SigninRequest;
import com.marketplace.auth.payload.request.SignupRequest;
import com.marketplace.auth.payload.response.AuthenticationResponse;
import com.marketplace.auth.payload.response.MessageResponse;
import com.marketplace.auth.payload.response.VerifyTokenResponse;
import com.marketplace.auth.services.RoleService;
import com.marketplace.auth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final UserService userService;

	private final RoleService roleService;

	private final JwtService jwtService;

	private final AuthenticationManager authenticationManager;

	private final PasswordEncoder passwordEncoder;

	private final UserDetailsService userDetailsService;

	@PostMapping(value = "/register")
	public ResponseEntity<Object> register(@Valid @RequestBody SignupRequest signUpRequest) {

		if (userService.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userService.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		User user = User.builder().username(signUpRequest.getUsername()).email(signUpRequest.getEmail())
				.password(passwordEncoder.encode(signUpRequest.getPassword())).build();

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

		User savedUser = userService.save(user);

		var jwtToken = jwtService.generateToken(new HashMap<>(), savedUser);

		return ResponseEntity.ok(new AuthenticationResponse("User registered successfully!", jwtToken));
	}

	@PostMapping("/login")
	@Transactional
	public ResponseEntity<AuthenticationResponse> authenticate(@Valid @RequestBody SigninRequest signinrequest) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signinrequest.getEmail(), signinrequest.getPassword()));
		var user = userService.findByEmail(signinrequest.getEmail()).orElseThrow();
		var jwtToken = jwtService.generateToken(new HashMap<>(), user);
		return ResponseEntity
				.ok(AuthenticationResponse.builder().token(jwtToken).message("Login successfully!").build());
	}

	@GetMapping("/validate")
	public ResponseEntity<VerifyTokenResponse> verifyToken(@RequestParam("token") String token) {

		final String userEmail = jwtService.extractUsername(token);

		System.out.println("userEmail : " + userEmail);

		if (userEmail != null) {

			UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

			List<String> roles = userDetails.getAuthorities().stream().map(e -> e.getAuthority())
					.collect(Collectors.toList());

			if (jwtService.isTokenValid(token, userDetails)) {

				return ResponseEntity
						.ok(VerifyTokenResponse.builder().message("Valid Token").email(userEmail).roles(roles).build());
			}

		}
		return ResponseEntity.badRequest()
				.body(VerifyTokenResponse.builder().message("Invalid Token").email("").build());
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

}
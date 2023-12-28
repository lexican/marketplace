//package com.marketplace.auth.controllers;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.marketplace.auth.models.User;
//import com.marketplace.auth.payload.response.MessageResponse;
//import com.marketplace.auth.services.UserService;
//
//import lombok.AllArgsConstructor;
//
//@RestController
//@RequestMapping("/api/auth/xx")
//@AllArgsConstructor
//public class UserController {
//	final private UserService userService;
//
//	@GetMapping(value = "/users")
//	public ResponseEntity<List<User>> getAllUsers() {
//		return ResponseEntity.ok(userService.getAllUsers());
//	}
//
//	@GetMapping(value = "/user/{id}")
//	public ResponseEntity<Object> getUserById(@PathVariable("id") Long id) {
//		Optional<User> user = userService.findById(id);
//		if (user.isPresent()) {
//			return ResponseEntity.ok(user);
//		}
//		return ResponseEntity.badRequest().body(new MessageResponse("User does not exist"));
//	}
//
//}

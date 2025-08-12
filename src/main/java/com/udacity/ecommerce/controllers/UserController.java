package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.dto.CreateUserRequest;
import com.udacity.ecommerce.model.dto.UserResponse;
import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return userRepository.findById(id)
				.map(u -> {
					log.info("event=get_user_by_id status=success userId={}", id);
					return ResponseEntity.ok(u);
				})
				.orElseGet(() -> {
					log.warn("event=get_user_by_id status=failure reason={} userId={}", "user_not_found", id);
					return ResponseEntity.notFound().build();
				});
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			log.warn("event=get_user_by_username status=failure reason={} username={}", "user_not_found", username);
			return ResponseEntity.notFound().build();
		}
		log.info("event=get_user_by_username status=success userId={} username={}", user.getId(), username);
		return ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest req) {
		if (req == null || req.username() == null || req.username().isBlank()) {
			log.warn("event=create_user status=failure reason={} username={}", "missing_username", req != null ? req.username() : null);
			return ResponseEntity.badRequest().build();
		}
		if (req.password() == null || req.confirmPassword() == null) {
			log.warn("event=create_user status=failure reason={} username={}", "missing_password_fields", req.username());
			return ResponseEntity.badRequest().build();
		}
		if (req.password().length() < 7) {
			log.warn("event=create_user status=failure reason={} username={}", "password_too_short", req.username());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		if (!req.password().equals(req.confirmPassword())) {
			log.warn("event=create_user status=failure reason={} username={}", "password_mismatch", req.username());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		if (userRepository.findByUsername(req.username()) != null) {
			log.warn("event=create_user status=failure reason={} username={}", "username_already_exists", req.username());
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		Cart cart = new Cart();

		User user = new User();
		user.setUsername(req.username());
		user.setPassword(passwordEncoder.encode(req.password()));

		user.setCart(cart);
		cart.setUser(user);

		userRepository.save(user);

		log.info("event=create_user status=success userId={} username={}", user.getId(), user.getUsername());
		UserResponse body = new UserResponse(user.getId(), user.getUsername());
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}
}

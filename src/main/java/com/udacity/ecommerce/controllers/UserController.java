package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.dto.CreateUserRequest;
import com.udacity.ecommerce.model.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private  PasswordEncoder passwordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}


	@PostMapping("/create")
	public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest req) {
		if (req == null || req.username() == null || req.username().isBlank()) {
			return ResponseEntity.badRequest().build();
		}
		if (req.password() == null || req.confirmPassword() == null) {
			return ResponseEntity.badRequest().build();
		}
		if (req.password().length() < 7) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		if (!req.password().equals(req.confirmPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		if (userRepository.findByUsername(req.username()) != null) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}

		Cart cart = new Cart();
		cartRepository.save(cart);
		User user = new User();
		user.setUsername(req.username());
		user.setPassword(passwordEncoder.encode(req.password())); // hash
		user.setCart(cart);
		userRepository.save(user);
		UserResponse body = new UserResponse(user.getId(), user.getUsername());
		return ResponseEntity.status(HttpStatus.CREATED).body(body);
	}
	
}

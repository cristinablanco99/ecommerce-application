package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.UserOrder;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.OrderRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CartRepository cartRepository;


	@PostMapping("/submit/{username}")
	@Transactional
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		var user = userRepository.findByUsername(username);
		if (user == null) return ResponseEntity.notFound().build();

		var cartId = user.getCart().getId();
		var cart = cartRepository.findWithItemsById(cartId).orElse(null);
		if (cart == null) return ResponseEntity.badRequest().build();

		cart.getItems().size();
		if (cart.getItems().isEmpty()) return ResponseEntity.badRequest().build();

		var order = UserOrder.createFromCart(cart);
		orderRepository.save(order);

		cart.getItems().clear();
		cart.setTotal(java.math.BigDecimal.ZERO);
		cartRepository.save(cart);

		return ResponseEntity.ok(order);
	}


	@GetMapping("/history/{username}")
	@Transactional(readOnly = true)
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		var user = userRepository.findByUsername(username);
		if (user == null) return ResponseEntity.notFound().build();
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}

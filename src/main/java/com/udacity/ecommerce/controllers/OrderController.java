package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.UserOrder;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.OrderRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

	private static final Logger log = LoggerFactory.getLogger(OrderController.class);

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
		if (user == null) {
			log.warn("event=create_order status=failure reason={} username={}", "user_not_found", username);
			return ResponseEntity.notFound().build();
		}

		var cartId = user.getCart().getId();
		var cart = cartRepository.findWithItemsById(cartId).orElse(null);
		if (cart == null) {
			log.warn("event=create_order status=failure reason={} userId={}", "cart_not_found", user.getId());
			return ResponseEntity.badRequest().build();
		}

		cart.getItems().size();
		if (cart.getItems().isEmpty()) {
			log.warn("event=create_order status=failure reason={} userId={} cartId={}", "empty_cart", user.getId(), cartId);
			return ResponseEntity.badRequest().build();
		}

		var order = UserOrder.createFromCart(cart);
		orderRepository.save(order);

		cart.getItems().clear();
		cart.setTotal(java.math.BigDecimal.ZERO);
		cartRepository.save(cart);

		log.info("event=create_order status=success orderId={} userId={} amount={}", order.getId(), user.getId(), order.getTotal());
		return ResponseEntity.ok(order);
	}

	@GetMapping("/history/{username}")
	@Transactional(readOnly = true)
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		var user = userRepository.findByUsername(username);
		if (user == null) {
			log.warn("event=get_order_history status=failure reason={} username={}", "user_not_found", username);
			return ResponseEntity.notFound().build();
		}
		var orders = orderRepository.findByUser(user);
		log.info("event=get_order_history status=success userId={} count={}", user.getId(), orders.size());
		return ResponseEntity.ok(orders);
	}
}

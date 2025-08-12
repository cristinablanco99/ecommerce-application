package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.Cart;
import com.udacity.ecommerce.model.persistence.Item;
import com.udacity.ecommerce.model.persistence.User;
import com.udacity.ecommerce.model.persistence.repositories.CartRepository;
import com.udacity.ecommerce.model.persistence.repositories.ItemRepository;
import com.udacity.ecommerce.model.persistence.repositories.UserRepository;
import com.udacity.ecommerce.model.requests.ModifyCartRequest;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	private static final Logger log = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Transactional
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if (user == null) {
			log.warn("event=add_to_cart status=failure reason={} username={}", "user_not_found", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if (!item.isPresent()) {
			log.warn("event=add_to_cart status=failure reason={} userId={} itemId={}", "item_not_found", user.getId(), request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity()).forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		log.info("event=add_to_cart status=success userId={} cartId={} itemId={} quantity={}",
				user.getId(), cart.getId(), item.get().getId(), request.getQuantity());
		return ResponseEntity.ok(cart);
	}

	@Transactional
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if (user == null) {
			log.warn("event=remove_from_cart status=failure reason={} username={}", "user_not_found", request.getUsername());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if (!item.isPresent()) {
			log.warn("event=remove_from_cart status=failure reason={} userId={} itemId={}", "item_not_found", user.getId(), request.getItemId());
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity()).forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		log.info("event=remove_from_cart status=success userId={} cartId={} itemId={} quantity={}",
				user.getId(), cart.getId(), item.get().getId(), request.getQuantity());
		return ResponseEntity.ok(cart);
	}
}

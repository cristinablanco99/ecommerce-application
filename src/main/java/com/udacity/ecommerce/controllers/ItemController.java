package com.udacity.ecommerce.controllers;

import com.udacity.ecommerce.model.persistence.Item;
import com.udacity.ecommerce.model.persistence.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/item")
public class ItemController {

	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemRepository itemRepository;

	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		List<Item> items = itemRepository.findAll();
		log.info("event=list_items status=success count={}", items.size());
		return ResponseEntity.ok(items);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		return itemRepository.findById(id)
				.map(item -> {
					log.info("event=get_item_by_id status=success itemId={}", id);
					return ResponseEntity.ok(item);
				})
				.orElseGet(() -> {
					log.warn("event=get_item_by_id status=failure reason={} itemId={}", "item_not_found", id);
					return ResponseEntity.notFound().build();
				});
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		List<Item> items = itemRepository.findByName(name);
		if (items == null || items.isEmpty()) {
			log.warn("event=get_items_by_name status=failure reason={} name={}", "no_results", name);
			return ResponseEntity.notFound().build();
		}
		log.info("event=get_items_by_name status=success name={} count={}", name, items.size());
		return ResponseEntity.ok(items);
	}
}

package com.udacity.ecommerce.model.persistence.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.udacity.ecommerce.model.persistence.Item;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
	List<Item> findByName(String name);

}

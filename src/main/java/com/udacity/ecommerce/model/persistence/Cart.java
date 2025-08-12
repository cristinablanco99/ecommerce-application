package com.udacity.ecommerce.model.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToMany
	private List<Item> items = new ArrayList<>();

	@OneToOne(mappedBy = "cart")
	@JsonIgnore
	private User user;

	@Column
	private BigDecimal total = BigDecimal.ZERO;


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = (items != null) ? items : new ArrayList<>();

	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = (total != null) ? total : BigDecimal.ZERO;
	}


	public void addItem(Item item) {
		if (item == null) return;
		items.add(item);
		if (total == null) total = BigDecimal.ZERO;
		total = total.add(item.getPrice());
	}

	public void removeItem(Item item) {
		if (item == null) return;
		items.remove(item);
		if (total == null) total = BigDecimal.ZERO;
		total = total.subtract(item.getPrice());
	}


}


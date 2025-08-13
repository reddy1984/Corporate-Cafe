package com.company.cafe.service;

import com.company.cafe.domain.DomainModels.*;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CoreServices {
	@Service
	public static class MenuService {
		private final Map<String, MenuItem> idToItem = new LinkedHashMap<>();
		public void add(MenuItem item) { idToItem.put(item.getId(), item); }
		public MenuItem get(String id) { return idToItem.get(id); }
		public Collection<MenuItem> all() { return Collections.unmodifiableCollection(idToItem.values()); }
	}

	@Service
	public static class InventoryService {
		private final Map<String, InventoryItem> stock = new ConcurrentHashMap<>();
		public void add(InventoryItem item) { stock.put(item.getSku(), item); }
		public InventoryItem get(String sku) { return stock.get(sku); }
		public List<InventoryItem> all() { return new ArrayList<>(stock.values()); }
		public boolean consume(String sku, int qty) {
			InventoryItem it = stock.get(sku);
			if (it == null || it.getQuantity() < qty) return false;
			it.setQuantity(it.getQuantity() - qty); return true;
		}
		public void restock(String sku, int qty) { InventoryItem it = stock.get(sku); if (it != null) it.setQuantity(it.getQuantity() + qty); }
		public List<InventoryItem> lowStock() { List<InventoryItem> out = new ArrayList<>(); for (InventoryItem it : stock.values()) if (it.getQuantity() <= it.getReorderThreshold()) out.add(it); return out; }
	}

	@Service
	public static class PaymentService {
		public boolean charge(User user, double amount, PaymentMethod method) {
			if (amount <= 0) return true;
			switch (method) {
				case WALLET:
					if (user.getWalletBalance() >= amount) { user.setWalletBalance(user.getWalletBalance() - amount); return true; } return false;
				case CREDITS:
					if (user.getMonthlyCredits() >= amount) { user.setMonthlyCredits(user.getMonthlyCredits() - amount); return true; } return false;
				case CARD:
					return true;
				default: return false;
			}
		}
	}

	@Service
	public static class OrderService {
		private final InventoryService inventory; private final PaymentService payments;
		public OrderService(InventoryService inventory, PaymentService payments) { this.inventory = inventory; this.payments = payments; }
		public Optional<Order> place(User user, Map<MenuItem, Integer> selections, PaymentMethod method) {
			for (Map.Entry<MenuItem,Integer> e : selections.entrySet()) { InventoryItem inv = inventory.get(e.getKey().getId()); if (inv == null || inv.getQuantity() < e.getValue()) return Optional.empty(); }
			double total = selections.entrySet().stream().mapToDouble(e -> e.getKey().getPrice() * e.getValue()).sum();
			if (!payments.charge(user, total, method)) return Optional.empty();
			for (Map.Entry<MenuItem,Integer> e : selections.entrySet()) inventory.consume(e.getKey().getId(), e.getValue());
			return Optional.of(new Order(user.getId(), selections, total));
		}
		public void progress(Order order, Order.Status s) { order.setStatus(s); }
	}

	@Service
	public static class FeedbackService {
		private final List<Feedback> all = new ArrayList<>();
		public void add(Feedback f) { all.add(f); }
		public List<Feedback> all() { return Collections.unmodifiableList(all); }
	}

	@Service
	public static class AnalyticsService {
		private int orders; private double revenue; private final Map<String,Integer> sales = new HashMap<>();
		public void record(Order order) { orders++; revenue += order.getTotal(); for (Map.Entry<MenuItem,Integer> e : order.getItems().entrySet()) sales.merge(e.getKey().getName(), e.getValue(), Integer::sum); }
		public int getOrders() { return orders; }
		public double getRevenue() { return revenue; }
		public Map<String,Integer> getSales() { return Collections.unmodifiableMap(sales); }
	}

	@Service
	public static class UsersService {
		private final Map<String, User> users = new LinkedHashMap<>();
		public void add(User u) { users.put(u.getId(), u); }
		public Collection<User> all() { return Collections.unmodifiableCollection(users.values()); }
		public User get(String id) { return users.get(id); }
	}
} 
package com.company.cafe.domain;

import java.util.*;

public class DomainModels {
	public enum PaymentMethod { WALLET, CREDITS, CARD }

	public static class MenuItem {
		private final String id;
		private final String name;
		private final double price;
		private final int calories;
		private final boolean vegan;
		private final boolean glutenFree;
		public MenuItem(String id, String name, double price, int calories, boolean vegan, boolean glutenFree) {
			this.id = id; this.name = name; this.price = price; this.calories = calories; this.vegan = vegan; this.glutenFree = glutenFree;
		}
		public String getId() { return id; }
		public String getName() { return name; }
		public double getPrice() { return price; }
		public int getCalories() { return calories; }
		public boolean isVegan() { return vegan; }
		public boolean isGlutenFree() { return glutenFree; }
	}

	public static class InventoryItem {
		private final String sku;
		private final String name;
		private int quantity;
		private final int reorderThreshold;
		public InventoryItem(String sku, String name, int quantity, int reorderThreshold) {
			this.sku = sku; this.name = name; this.quantity = quantity; this.reorderThreshold = reorderThreshold;
		}
		public String getSku() { return sku; }
		public String getName() { return name; }
		public int getQuantity() { return quantity; }
		public void setQuantity(int quantity) { this.quantity = quantity; }
		public int getReorderThreshold() { return reorderThreshold; }
	}

	public static class User {
		private final String id;
		private final String name;
		private double walletBalance;
		private double monthlyCredits;
		private final Set<String> dietaryPreferences;
		public User(String id, String name, double walletBalance, double monthlyCredits, Set<String> dietaryPreferences) {
			this.id = id; this.name = name; this.walletBalance = walletBalance; this.monthlyCredits = monthlyCredits; this.dietaryPreferences = dietaryPreferences;
		}
		public String getId() { return id; }
		public String getName() { return name; }
		public double getWalletBalance() { return walletBalance; }
		public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }
		public double getMonthlyCredits() { return monthlyCredits; }
		public void setMonthlyCredits(double monthlyCredits) { this.monthlyCredits = monthlyCredits; }
		public Set<String> getDietaryPreferences() { return dietaryPreferences; }
	}

	public static class Order {
		public enum Status { PENDING, PREPARING, READY, COMPLETED }
		private final String id;
		private final String userId;
		private final Map<MenuItem, Integer> items;
		private final double total;
		private Status status;
		public Order(String userId, Map<MenuItem, Integer> items, double total) {
			this.id = UUID.randomUUID().toString(); this.userId = userId; this.items = new LinkedHashMap<>(items); this.total = total; this.status = Status.PENDING;
		}
		public String getId() { return id; }
		public String getUserId() { return userId; }
		public Map<MenuItem, Integer> getItems() { return Collections.unmodifiableMap(items); }
		public double getTotal() { return total; }
		public Status getStatus() { return status; }
		public void setStatus(Status status) { this.status = status; }
	}

	public static class Feedback {
		private final String orderId;
		private final int rating;
		private final String comment;
		private final long timestamp;
		public Feedback(String orderId, int rating, String comment, long timestamp) { this.orderId = orderId; this.rating = rating; this.comment = comment; this.timestamp = timestamp; }
		public String getOrderId() { return orderId; }
		public int getRating() { return rating; }
		public String getComment() { return comment; }
		public long getTimestamp() { return timestamp; }
	}
} 
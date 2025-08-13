package com.company.cafe.web;

import com.company.cafe.domain.DomainModels.*;
import com.company.cafe.service.CoreServices.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/")
public class CafeController {
	private final MenuService menu;
	private final InventoryService inventory;
	private final PaymentService payments;
	private final OrderService orders;
	private final FeedbackService feedback;
	private final AnalyticsService analytics;
	private final UsersService users;

	public CafeController(MenuService menu, InventoryService inventory, PaymentService payments, OrderService orders, FeedbackService feedback, AnalyticsService analytics, UsersService users) {
		this.menu = menu; this.inventory = inventory; this.payments = payments; this.orders = orders; this.feedback = feedback; this.analytics = analytics; this.users = users;
	}

	@GetMapping
	public String home(Model model, @RequestParam(value = "user", required = false) String userId) {
		model.addAttribute("menu", menu.all());
		model.addAttribute("inventory", inventory);
		model.addAttribute("users", users.all());
		model.addAttribute("selectedUser", userId != null ? users.get(userId) : null);
		return "home";
	}

	@PostMapping("/checkout")
	public String checkout(@RequestParam String userId,
						  @RequestParam List<String> itemId,
						  @RequestParam List<Integer> qty,
						  @RequestParam PaymentMethod method,
						  Model model) {
		User user = users.get(userId);
		if (user == null) return "redirect:/";
		Map<MenuItem, Integer> selections = new LinkedHashMap<>();
		for (int i = 0; i < itemId.size(); i++) {
			MenuItem it = menu.get(itemId.get(i));
			if (it != null && qty.get(i) != null && qty.get(i) > 0) selections.put(it, qty.get(i));
		}
		Optional<Order> maybe = orders.place(user, selections, method);
		if (maybe.isEmpty()) {
			model.addAttribute("error", "Order failed (stock or payment).");
			return home(model, userId);
		}
		Order order = maybe.get();
		orders.progress(order, Order.Status.PREPARING);
		orders.progress(order, Order.Status.READY);
		orders.progress(order, Order.Status.COMPLETED);
		analytics.record(order);
		model.addAttribute("order", order);
		return "thankyou";
	}

	@PostMapping("/feedback")
	public String feedback(@RequestParam String orderId, @RequestParam int rating, @RequestParam String comment) {
		feedback.add(new Feedback(orderId, rating, comment, System.currentTimeMillis()));
		return "redirect:/";
	}

	@GetMapping("/analytics")
	public String analytics(Model model) {
		List<Map.Entry<String,Integer>> sortedSales = new ArrayList<>(analytics.getSales().entrySet());
		sortedSales.sort(Map.Entry.<String,Integer>comparingByValue(Comparator.reverseOrder()));
		model.addAttribute("orders", analytics.getOrders());
		model.addAttribute("revenue", analytics.getRevenue());
		model.addAttribute("sales", sortedSales);
		model.addAttribute("lowStock", inventory.lowStock());
		return "analytics";
	}
} 
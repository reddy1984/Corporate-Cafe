package com.company.cafe.config;

import com.company.cafe.domain.DomainModels.*;
import com.company.cafe.service.CoreServices.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class DataLoader {
	@Bean
	CommandLineRunner seed(MenuService menu, InventoryService inv, UsersService users) {
		return args -> {
			menu.add(new MenuItem("C01", "Coffee", 2.50, 5, true, true));
			menu.add(new MenuItem("T01", "Tea", 2.00, 2, true, true));
			menu.add(new MenuItem("S01", "Veg Sandwich", 4.75, 350, true, false));
			menu.add(new MenuItem("S02", "Chicken Wrap", 5.50, 520, false, false));
			menu.add(new MenuItem("B01", "Blueberry Muffin", 3.25, 410, true, false));

			inv.add(new InventoryItem("C01", "Coffee Beans", 100, 20));
			inv.add(new InventoryItem("T01", "Tea Leaves", 100, 20));
			inv.add(new InventoryItem("S01", "Bread & Veggies", 50, 10));
			inv.add(new InventoryItem("S02", "Wrap & Chicken", 40, 10));
			inv.add(new InventoryItem("B01", "Muffins", 60, 15));

			users.add(new User("U01", "Ava", 20.00, 30.00, Set.of("vegan")));
			users.add(new User("U02", "Noah", 12.00, 15.00, Set.of("gluten-free")));
			users.add(new User("U03", "Liam", 5.00, 5.00, Set.of()));
		};
	}
} 
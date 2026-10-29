package org.rgupta.dataprocessing.practice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortOrders {

	record Order(
			String symbol,
			int quantity,
			double price) {
	}

	private static final List<Order> orders = new ArrayList<>(List.of(
			new Order("EURUSD", 100, 1.08),
			new Order("GBPUSD", 200, 1.27),
			new Order("EURUSD", 300, 1.09),
			new Order("EURUSD", 150, 1.08)));

	// Sort the orders by:
	// 1. Symbol alphabetically (ascending).
	// 2. Price descending within each symbol.
	// 3. Quantity descending when prices are equal.
	void sort(List<Order> orders) {
		orders.sort(Comparator.comparing(Order::symbol)
				.thenComparing(Comparator.comparingDouble(Order::price).reversed())
				.thenComparing(Comparator.comparingInt(Order::quantity).reversed()));

	}

	public static void main(String[] args) {
		SortOrders sortOrders = new SortOrders();
		sortOrders.sort(orders);
		System.out.println("orders = " + orders);
	}
}

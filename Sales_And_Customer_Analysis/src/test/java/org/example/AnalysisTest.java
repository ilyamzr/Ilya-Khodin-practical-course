package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnalysisTest {

    private OrderAnalyser analyser;
    private List<Order> orders;

    @BeforeEach
    void setUp() {
        analyser = new OrderAnalyser();
        orders = new ArrayList<>();

        Customer customer1 = new Customer("1", "Alice", "alice@gmail.com", LocalDateTime.of(2015, 10, 8, 15, 30, 45), 30, "Minsk");
        Customer customer2 = new Customer("2", "Bob", "bob@gmail.com", LocalDateTime.of(2018, 11, 10, 15, 30, 45), 25, "Moscow");
        Customer customer3 = new Customer("3", "Tom", "tom@gmail.com", LocalDateTime.of(2022, 10, 18, 15, 30, 45), 35, "Minsk");

        List<OrderItem> item1 = List.of(new OrderItem("Pants", 1, 30.0, Category.CLOTHING));
        List<OrderItem> item2 = List.of(new OrderItem("Apple Watch", 1, 250.0, Category.ELECTRONICS));
        List<OrderItem> item3 = List.of(new OrderItem("Book", 3, 10.0, Category.BOOKS));
        List<OrderItem> item4 = List.of(new OrderItem("Book", 2, 15.0, Category.BOOKS));
        List<OrderItem> item5 = List.of(new OrderItem("Shirt", 1, 20.0, Category.CLOTHING));

        Order order1 = new Order("1", LocalDateTime.of(2015, 10, 8, 15, 30, 45), customer1, item1);
        order1.setStatus(OrderStatus.DELIVERED);
        orders.add(order1);

        Order order2 = new Order("2", LocalDateTime.of(2015, 10, 8, 15, 30, 45), customer2, item2);
        order2.setStatus(OrderStatus.CANCELLED);
        orders.add(order2);

        Order order3 = new Order("3", LocalDateTime.of(2015, 10, 8, 15, 30, 45), customer1, item3);
        order3.setStatus(OrderStatus.CANCELLED);
        orders.add(order3);

        Order order4 = new Order("4", LocalDateTime.of(2015, 10, 8, 15, 30, 45), customer1, item5);
        order4.setStatus(OrderStatus.NEW);
        orders.add(order4);

        Order order5 = new Order("5", LocalDateTime.of(2015, 10, 8, 15, 30, 45), customer1, item5);
        order5.setStatus(OrderStatus.NEW);
        orders.add(order5);

        Order order6 = new Order("6", LocalDateTime.of(2015, 10, 8, 15, 30, 45), customer1, item5);
        order6.setStatus(OrderStatus.NEW);
        orders.add(order6);

        Order order7 = new Order("7", LocalDateTime.of(2015, 10, 8, 15, 30, 45), customer1, item4);
        order7.setStatus(OrderStatus.DELIVERED);
        orders.add(order7);
    }

    @Test
    void getMostPopularProductBySalesCheck() {
        ArrayList<String> products = analyser.getMostPopularProductBySales(orders);
        assertEquals(1, products.size());
        assertEquals("Book", products.getFirst());
    }

    @Test
    void getMostPopularProductBySalesEmptyCheck() {
        ArrayList<String> products = analyser.getMostPopularProductBySales(new ArrayList<>());
        assertEquals(0, products.size());
    }

    @Test
    void getMostPopularProductBySalesNullCheck() {
        assertThrows(IllegalArgumentException.class, () -> analyser.getMostPopularProductBySales(null));
    }

    @Test
    void getAverageCheckForSuccessfullyDeliveredProductsCheck() {
        double average = analyser.getAverageCheckForSuccessfullyDeliveredProducts(orders);
        assertEquals(30.0, average, 0.001);
    }

    @Test
    void getAverageCheckForSuccessfullyDeliveredProductsNoDeliveredCheck() {
        List<Order> noDelivered = orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.DELIVERED)
                .toList();
        double average = analyser.getAverageCheckForSuccessfullyDeliveredProducts(noDelivered);
        assertEquals(0.0, average, 0.001);
    }

    @Test
    void getAverageCheckForSuccessfullyDeliveredProductsNullCheck() {
        assertThrows(IllegalArgumentException.class, () -> analyser.getAverageCheckForSuccessfullyDeliveredProducts(null));
    }

    @Test
    void getCustomersWithMoreThanFiveOrdersCheck() {
        List<Customer> customers = analyser.getCustomersWithMoreThanFiveOrders(orders);
        assertEquals(1, customers.size());
        assertEquals("1", customers.getFirst().getCustomerId());
    }

    @Test
    void getCustomersWithMoreThanFiveOrdersEmptyCheck() {
        List<Customer> customers = analyser.getCustomersWithMoreThanFiveOrders(new ArrayList<>());
        assertEquals(0, customers.size());
    }

    @Test
    void getCustomersWithMoreThanFiveOrdersNullCheck() {
        assertThrows(IllegalArgumentException.class, () -> analyser.getCustomersWithMoreThanFiveOrders(null));
    }
}

package org.example;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrderAnalyser {

    public List<String> getUniqueCities(List<Order> orders)
    {
        if (orders == null) throw new IllegalArgumentException();
        List<String> cities = new ArrayList<>();
        Stream<Order> stream = orders.stream();
        stream.map(Order::getCustomer)
                .filter(Objects::nonNull)
                .map(Customer::getCustomerCity)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(cities::add);
        return cities;
    }

    public double getTotalIncome(List<Order> orders)
    {
        if (orders == null) throw new IllegalArgumentException();
        Stream<Order> stream = orders.stream();
        return stream.filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getItems)
                .flatMap(List::stream)
                .mapToDouble(OrderItem::getPrice)
                .sum();
    }

    public ArrayList<String> getMostPopularProductBySales(List<Order> orders)
    {
        if (orders == null) throw new IllegalArgumentException();
        Stream<Order> stream = orders.stream();
        Optional<Integer> maxQuantity = stream.filter(order -> order.getStatus() == OrderStatus.DELIVERED).
                map(Order::getItems)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(OrderItem::getQuantity)
                .max(Integer::compareTo);

        ArrayList<String> mostPopularProducts = new ArrayList<>();
        Stream<Order> stream2 = orders.stream();
        stream2.map(Order::getItems)
                .flatMap(List::stream)
                .filter(orderItem -> orderItem.getQuantity() == maxQuantity.get())
                .map(OrderItem::getProductName)
                .forEach(mostPopularProducts::add);
        return mostPopularProducts;
    }

    public double getAverageCheckForSuccessfullyDeliveredProducts(List<Order> orders)
    {
        if (orders == null) throw new IllegalArgumentException();
        Stream<Order> stream = orders.stream();
        OptionalDouble totalPrice = stream.filter(order -> order.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getItems)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .mapToDouble(item -> item.getQuantity() * item.getPrice())
                .average();
        if (totalPrice.isPresent()) return totalPrice.getAsDouble();
        else return 0;
    }

    public List<Customer> getCustomersWithMoreThanFiveOrders(List <Order> orders)
    {
        if (orders == null) throw new IllegalArgumentException();
        Stream<Order> stream = orders.stream();
        return stream.collect(Collectors.groupingBy(Order::getCustomer, Collectors.counting()))
                .entrySet().stream().filter(entry -> entry.getValue() > 5)
                .map(Map.Entry::getKey)
                .toList();
    }
}

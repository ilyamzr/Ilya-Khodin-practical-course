package org.example.orderservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityNotFoundException;
import org.example.orderservice.client.UserClient;
import org.example.orderservice.client.UserClientWrapper;
import org.example.orderservice.dto.*;
import org.example.orderservice.entity.Item;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderItem;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.ItemRepository;
import org.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final UserClientWrapper userClientWrapper;

    public OrderService(OrderRepository orderRepository, ItemRepository itemRepository, OrderMapper orderMapper, UserClientWrapper userClientWrapper) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
        this.userClientWrapper = userClientWrapper;
    }

    @Transactional
    public OrderWithUserResponse createOrder(CreateOrderRequest request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setStatus(OrderStatus.CREATED);

        List<OrderItem> orderItems = new ArrayList<>();
        for (var itemRequest : request.getItems()) {
            Item item = itemRepository.findById(itemRequest.getItemId())
                    .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemRequest.getItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        log.info("Created new order with id: {}", savedOrder.getId());

        UserDto userDto = userClientWrapper.getUserInfo(savedOrder.getUserId());
        OrderResponse orderResponse = orderMapper.toOrderResponse(savedOrder);

        return buildFinalResponse(orderResponse, userDto);
    }

    @Transactional(readOnly = true)
    public OrderWithUserResponse getOrderById(Long id) {
        Order order = findOrderById(id);
        UserDto userDto = getUserInfo(order.getUserId());
        OrderResponse orderResponse = orderMapper.toOrderResponse(order);
        return buildFinalResponse(orderResponse, userDto);
    }

    @Transactional(readOnly = true)
    public List<OrderWithUserResponse> getOrdersByIds(List<Long> ids) {
        return orderRepository.findAllByIdIn(ids).stream()
                .map(order -> {
                    UserDto userDto = userClientWrapper.getUserInfo(order.getUserId());
                    OrderResponse orderResponse = orderMapper.toOrderResponse(order);
                    return buildFinalResponse(orderResponse, userDto);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderWithUserResponse> getOrdersByStatuses(List<OrderStatus> statuses) {
        return orderRepository.findAllByStatusIn(statuses).stream()
                .map(order -> {
                    UserDto userDto = userClientWrapper.getUserInfo(order.getUserId());
                    OrderResponse orderResponse = orderMapper.toOrderResponse(order);
                    return buildFinalResponse(orderResponse, userDto);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderWithUserResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request) {
        Order order = findOrderById(id);
        order.setStatus(request.getStatus());
        Order updatedOrder = orderRepository.save(order);
        log.info("Updated status for order with id: {}", updatedOrder.getId());

        UserDto userDto = userClientWrapper.getUserInfo(order.getUserId());
        OrderResponse orderResponse = orderMapper.toOrderResponse(updatedOrder);

        return buildFinalResponse(orderResponse, userDto);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order with id=" + id + " not found");
        }
        orderRepository.deleteById(id);
        log.info("Deleted order with id: {}", id);
    }

    @CircuitBreaker(name = "user-service-cb", fallbackMethod = "getFallbackUserInfo")
    public UserDto getUserInfo(Long userId) {
        log.info("Requesting user info for user id: {}", userId);
        return userClientWrapper.getUserInfo(userId);
    }

    public UserDto getFallbackUserInfo(Long userId, Throwable t) {
        log.error("Fallback for user service, user id: {}, error: {}", userId, t.getMessage());
        UserDto fallbackDto = new UserDto();
        fallbackDto.setId(userId);
        fallbackDto.setName("N/A");
        fallbackDto.setSurname("N/A");
        fallbackDto.setEmail("User service is unavailable");
        fallbackDto.setBirthDate(LocalDate.parse("0"));
        return fallbackDto;
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with id=" + id + " not found"));
    }

    private OrderWithUserResponse buildFinalResponse(OrderResponse orderResponse, UserDto userDto) {
        OrderWithUserResponse finalResponse = new OrderWithUserResponse();
        finalResponse.setOrder(orderResponse);
        finalResponse.setUser(userDto);
        return finalResponse;
    }
}
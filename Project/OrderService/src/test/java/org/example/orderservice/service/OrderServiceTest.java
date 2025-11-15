package org.example.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import org.example.orderservice.client.UserClientWrapper;
import org.example.orderservice.dto.*;
import org.example.orderservice.entity.Item;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.mapper.OrderMapper;
import org.example.orderservice.repository.ItemRepository;
import org.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserClientWrapper userClientWrapper;

    @InjectMocks
    private OrderService orderService;

    private Item testItem;
    private Order testOrder;
    private UserDto testUserDto;
    private OrderResponse testOrderResponse;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Book");
        testItem.setPrice(new BigDecimal("100.00"));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setUserId(1L);
        testOrder.setStatus(OrderStatus.CREATED);

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setName("John");
        testUserDto.setEmail("john.doe@example.com");

        testOrderResponse = new OrderResponse();
        testOrderResponse.setId(1L);
    }

    @Test
    void createOrder_whenItemExists_shouldCreateOrder() {
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setUserId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setItemId(1L);
        itemRequest.setQuantity(2);
        createRequest.setItems(Collections.singletonList(itemRequest));

        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toOrderResponse(any(Order.class))).thenReturn(testOrderResponse);
        when(userClientWrapper.getUserInfo(1L)).thenReturn(testUserDto);

        OrderWithUserResponse result = orderService.createOrder(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getOrder().getId());
        assertEquals("John", result.getUser().getName());

        verify(itemRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(userClientWrapper).getUserInfo(1L);
    }

    @Test
    void createOrder_whenItemNotFound_shouldThrowException() {
        CreateOrderRequest createRequest = new CreateOrderRequest();
        createRequest.setUserId(1L);

        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setItemId(99L);
        createRequest.setItems(Collections.singletonList(itemRequest));

        when(itemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createRequest));

        verify(orderRepository, never()).save(any());
        verify(userClientWrapper, never()).getUserInfo(anyLong());
    }

    @Test
    void getOrderById_whenOrderExists_shouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toOrderResponse(testOrder)).thenReturn(testOrderResponse);
        when(userClientWrapper.getUserInfo(1L)).thenReturn(testUserDto);

        OrderWithUserResponse result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrder().getId());
        assertEquals("john.doe@example.com", result.getUser().getEmail());
    }

    @Test
    void deleteOrder_whenOrderExists_shouldDeleteSuccessfully() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderRepository).deleteById(1L);

        orderService.deleteOrder(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }
}

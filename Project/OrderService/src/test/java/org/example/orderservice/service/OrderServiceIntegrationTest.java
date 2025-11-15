package org.example.orderservice.service;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import jakarta.persistence.EntityNotFoundException;
import org.example.orderservice.dto.CreateOrderRequest;
import org.example.orderservice.dto.OrderItemRequest;
import org.example.orderservice.dto.OrderWithUserResponse;
import org.example.orderservice.entity.Item;
import org.example.orderservice.entity.Order;
import org.example.orderservice.entity.OrderStatus;
import org.example.orderservice.repository.ItemRepository;
import org.example.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    // WireMockServer должен быть статическим, чтобы быть доступным из статического метода
    static WireMockServer wireMockServer;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeAll
    static void startServer() {
        wireMockServer = new WireMockServer(0); // 0 = случайный порт
        wireMockServer.start();
    }

    @AfterAll
    static void stopServer() {
        wireMockServer.stop();
    }

    // В ЭТОТ МЕТОД НУЖНО ДОБАВИТЬ СВОЙСТВО ДЛЯ FEIGN-КЛИЕНТА
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Свойства для подключения к тестовой БД
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        // --- КЛЮЧЕВОЕ ИЗМЕНЕНИЕ ---
        // Устанавливаем URL для Feign-клиента ДО загрузки контекста
        registry.add("client.user-service.url", wireMockServer::baseUrl);
    }

    @BeforeEach
    void setUp() {
        // УДАЛЯЕМ System.setProperty отсюда
        // Очищаем WireMock и БД перед каждым тестом
        wireMockServer.resetAll();
        orderRepository.deleteAll();
        itemRepository.deleteAll();
    }

    // Метод tearDown больше не нужен, так как WireMock останавливается один раз в @AfterAll

    @Test
    void createOrder_withSuccessfulUserFetch_shouldReturnCombinedResponse() {
        // Given: 1. Создаем товар в тестовой БД
        Item savedItem = itemRepository.save(createTestItem("Laptop", "1500.00"));

        // Given: 2. Настраиваем заглушку для WireMock
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v1/users/1"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("user-response.json")
                        .withStatus(200)));

        // Given: 3. Создаем DTO для запроса
        CreateOrderRequest request = createOrderRequest(1L, savedItem.getId(), 1);

        // When
        OrderWithUserResponse response = orderService.createOrder(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.getOrder().getId());
        assertEquals(1, orderRepository.count());

        assertEquals(1L, response.getUser().getId());
        assertEquals("Jane", response.getUser().getName());
        assertEquals("jane.doe@example.com", response.getUser().getEmail());

        wireMockServer.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/api/v1/users/1")));
    }

    @Test
    void getOrderById_whenUserServiceFails_shouldReturnOrderWithFallbackUser() {
        wireMockServer.stubFor(WireMock.get(WireMock.urlEqualTo("/api/v1/users/2"))
                .willReturn(WireMock.serverError().withStatus(500)));

        Item savedItem = itemRepository.save(createTestItem("Mouse", "50.00"));
        CreateOrderRequest request = createOrderRequest(2L, savedItem.getId(), 5);
        OrderWithUserResponse createdOrderResponse = orderService.createOrder(request);
        Long orderId = createdOrderResponse.getOrder().getId();

        OrderWithUserResponse response = orderService.getOrderById(orderId);

        assertNotNull(response);
        assertEquals(orderId, response.getOrder().getId());

        assertEquals(2L, response.getUser().getId());
        assertEquals("N/A", response.getUser().getName());
        assertEquals("User service is unavailable", response.getUser().getEmail());
    }

    @Test
    void getOrderById_whenOrderNotFound_shouldThrowException() {
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(999L));
    }

    // Вспомогательные методы
    private Item createTestItem(String name, String price) {
        Item item = new Item();
        item.setName(name);
        item.setPrice(new BigDecimal(price));
        return item;
    }

    private CreateOrderRequest createOrderRequest(Long userId, Long itemId, int quantity) {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setItemId(itemId);
        itemRequest.setQuantity(quantity);
        CreateOrderRequest orderRequest = new CreateOrderRequest();
        orderRequest.setUserId(userId);
        orderRequest.setItems(Collections.singletonList(itemRequest));
        return orderRequest;
    }
}
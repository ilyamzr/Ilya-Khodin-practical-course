package org.example.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.project.dto.CardInfoDto;
import org.example.project.entity.CardInfo;
import org.example.project.entity.User;
import org.example.project.repository.CardInfoRepository;
import org.example.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class CardInfoControllerIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static final GenericContainer<?> redis = new GenericContainer<>("redis:alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.liquibase.enabled", () -> "true");
    }

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private CardInfoRepository cardInfoRepository;

    private User user;

    @BeforeEach
    void setUp() {
        cardInfoRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john@example.com");
        user = userRepository.save(user);
    }

    @Test
    void createCard_ShouldReturn201() throws Exception {
        CardInfoDto dto = new CardInfoDto();
        dto.setUserId(user.getId());
        dto.setNumber("1234-5678-9012-3456");
        dto.setHolder("John Doe");
        dto.setExpirationDate(LocalDate.of(2027, 12, 31)); // ← в будущем

        mockMvc.perform(post("/api/cards/{userId}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("1234-5678-9012-3456"));
    }

    @Test
    void getCardsByUserId_ShouldReturn200() throws Exception {
        CardInfo card = new CardInfo();
        card.setNumber("1111-2222-3333-4444");
        card.setHolder("John Doe");
        card.setExpirationDate(LocalDate.of(2026, 1, 1));
        card.setUser(user);
        cardInfoRepository.save(card);

        mockMvc.perform(get("/api/cards/user/{userId}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].number").value("1111-2222-3333-4444"));
    }
}
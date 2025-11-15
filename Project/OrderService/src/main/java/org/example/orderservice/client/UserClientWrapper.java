package org.example.orderservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.example.orderservice.dto.UserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientWrapper {

    private static final Logger log = LoggerFactory.getLogger(UserClientWrapper.class);
    private final UserClient userClient;

    public UserClientWrapper(UserClient userClient) {
        this.userClient = userClient;
    }

    @CircuitBreaker(name = "user-service-cb", fallbackMethod = "getFallbackUserInfo")
    public UserDto getUserInfo(Long userId) {
        log.info("Requesting user info for user id: {}", userId);
        return userClient.getUserById(userId);
    }

    public UserDto getFallbackUserInfo(Long userId, Throwable t) {
        log.error("Fallback for user service, user id: {}, error: {}", userId, t.getMessage());
        UserDto fallbackDto = new UserDto();
        fallbackDto.setId(userId);
        fallbackDto.setName("N/A");
        fallbackDto.setEmail("User service is unavailable");
        return fallbackDto;
    }
}
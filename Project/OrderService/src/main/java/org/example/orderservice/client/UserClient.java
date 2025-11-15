package org.example.orderservice.client;

import org.example.orderservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${client.user-service.url}")
public interface UserClient {
    @GetMapping("/api/v1/users/{id}")
    UserDto getUserById(@PathVariable("id") Long id);
}
package org.example.project.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.project.dto.UserWithCardsDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserWithCardsDto> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, UserWithCardsDto> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        template.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<UserWithCardsDto> serializer;
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        serializer = new Jackson2JsonRedisSerializer<>(UserWithCardsDto.class);
        serializer.setObjectMapper(om);

        template.setValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
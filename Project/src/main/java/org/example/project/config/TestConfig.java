package org.example.project.config;

import org.mapstruct.factory.Mappers;
import org.example.project.mapper.CardInfoMapper;
import org.example.project.mapper.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean
    public UserMapper userMapper() {
        return Mappers.getMapper(UserMapper.class);
    }

    @Bean
    public CardInfoMapper cardInfoMapper() {
        return Mappers.getMapper(CardInfoMapper.class);
    }
}
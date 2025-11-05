package org.example.project.service;

import org.example.project.dto.CardInfoDto;
import org.example.project.dto.UserDto;
import org.example.project.dto.UserWithCardsDto;
import org.example.project.entity.CardInfo;
import org.example.project.entity.User;
import org.example.project.exception.UserNotFoundException;
import org.example.project.mapper.CardInfoMapper;
import org.example.project.mapper.UserMapper;
import org.example.project.repository.CardInfoRepository;
import org.example.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CardInfoRepository cardInfoRepository;
    @Mock private UserMapper userMapper;
    @Mock private CardInfoMapper cardInfoMapper;
    @Mock private RedisTemplate<String, UserWithCardsDto> redisTemplate;
    @Mock private ValueOperations<String, UserWithCardsDto> valueOperations;

    @InjectMocks private UserService userService;

    private User user;
    private UserDto userDto;
    private CardInfo card;
    private CardInfoDto cardDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setBirthDate(LocalDate.of(1990, 1, 1));
        user.setEmail("john@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setSurname("Doe");
        userDto.setBirthDate(LocalDate.of(1990, 1, 1));
        userDto.setEmail("john@example.com");

        card = new CardInfo();
        card.setId(1L);
        card.setNumber("1234-5678-9012-3456");
        card.setUser(user);

        cardDto = new CardInfoDto();
        cardDto.setId(1L);
        cardDto.setNumber("1234-5678-9012-3456");
    }

    @Test
    void createUser_ShouldSaveAndReturnDto() {
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(userDto);

        assertEquals(userDto, result);
        verify(userRepository).save(user);
    }

    @Test
    void getUserById_ExistingId_ShouldReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertEquals(userDto, result);
    }

    @Test
    void getUserById_NonExistingId_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void getUserWithCards_CacheMiss_ShouldLoadFromDbAndCache() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("user:with-cards:1")).thenReturn(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardInfoRepository.findByUserId(1L, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(List.of(card)));
        when(cardInfoMapper.toDto(card)).thenReturn(cardDto);

        UserWithCardsDto expected = new UserWithCardsDto();
        expected.setId(1L);
        expected.setName("John");
        expected.setSurname("Doe");
        expected.setBirthDate(LocalDate.of(1990, 1, 1));
        expected.setEmail("john@example.com");
        expected.setCards(List.of(cardDto));

        UserWithCardsDto result = userService.getUserWithCards(1L);

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getName(), result.getName());

        verify(valueOperations).set(
                eq("user:with-cards:1"),
                any(UserWithCardsDto.class),
                eq(1L),
                eq(TimeUnit.HOURS)
        );
    }

    @Test
    void updateUser_ShouldUpdateAndEvictCache() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, userDto);

        verify(userMapper).updateEntityFromDto(userDto, user);
        verify(userRepository).save(user);
        verify(redisTemplate).delete("user:with-cards:1");
        assertEquals(userDto, result);
    }
}
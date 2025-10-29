package org.example.project.service;

import lombok.RequiredArgsConstructor;
import org.example.project.dto.UserDto;
import org.example.project.dto.UserWithCardsDto;
import org.example.project.entity.CardInfo;
import org.example.project.entity.User;
import org.example.project.exception.UserNotFoundException;
import org.example.project.mapper.CardInfoMapper;
import org.example.project.mapper.UserMapper;
import org.example.project.repository.CardInfoRepository;
import org.example.project.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final CardInfoRepository cardInfoRepository;
    private final UserMapper userMapper;
    private final CardInfoMapper cardInfoMapper;
    private final RedisTemplate<String, UserWithCardsDto> redisTemplate;

    private static final String CACHE_KEY_PREFIX = "user:with-cards:";
    private static final long CACHE_TTL_HOURS = 1;

    public UserService(UserRepository userRepository, CardInfoRepository cardInfoRepository, UserMapper userMapper, CardInfoMapper cardInfoMapper, RedisTemplate<String, UserWithCardsDto> redisTemplate) {
        this.userRepository = userRepository;
        this.cardInfoRepository = cardInfoRepository;
        this.userMapper = userMapper;
        this.cardInfoMapper = cardInfoMapper;
        this.redisTemplate = redisTemplate;
    }

    private String getCacheKey(Long userId) {
        return CACHE_KEY_PREFIX + userId;
    }

    public UserWithCardsDto getUserWithCards(Long id) {
        String key = getCacheKey(id);

        UserWithCardsDto cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return cached;
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        Page<CardInfo> page = cardInfoRepository.findByUserId(id, Pageable.unpaged());
        List<CardInfo> cards = page.getContent();
        UserWithCardsDto result = new UserWithCardsDto();
        result.setId(user.getId());
        result.setName(user.getName());
        result.setSurname(user.getSurname());
        result.setBirthDate(user.getBirthDate());
        result.setEmail(user.getEmail());
        result.setCards(cards.stream()
                .map(cardInfoMapper::toDto)
                .toList());

        redisTemplate.opsForValue().set(key, result, CACHE_TTL_HOURS, TimeUnit.HOURS);

        return result;
    }

    public void evictUserCache(Long userId) {
        redisTemplate.delete(getCacheKey(userId));
    }

    @Transactional
    public UserDto createUser(UserDto dto) {
        User user = userMapper.toEntity(dto);
        user = userRepository.save(user);
        // Кэш будет создан при первом getUserWithCards
        return userMapper.toDto(user);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return userMapper.toDto(user);
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAllUsers(pageable)
                .map(userMapper::toDto);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userMapper.updateEntityFromDto(dto, user);
        user = userRepository.save(user);
        evictUserCache(id);
        return userMapper.toDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        evictUserCache(id);
    }
}
package org.example.project.service;

import org.example.project.dto.CardInfoDto;
import org.example.project.entity.CardInfo;
import org.example.project.entity.User;
import org.example.project.exception.UserNotFoundException;
import org.example.project.mapper.CardInfoMapper;
import org.example.project.repository.CardInfoRepository;
import org.example.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {

    @Mock private CardInfoRepository cardInfoRepository;
    @Mock private UserRepository userRepository;
    @Mock private CardInfoMapper cardInfoMapper;

    @InjectMocks private CardInfoService cardInfoService;

    private User user;
    private CardInfo card;
    private CardInfoDto cardDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        card = new CardInfo();
        card.setId(1L);
        card.setNumber("1234-5678-9012-3456");
        card.setUser(user);

        cardDto = new CardInfoDto();
        cardDto.setId(1L);
        cardDto.setNumber("1234-5678-9012-3456");
    }

    @Test
    void createCard_ShouldSaveAndReturnDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cardInfoMapper.toEntity(cardDto)).thenReturn(card);
        when(cardInfoRepository.save(card)).thenReturn(card);
        when(cardInfoMapper.toDto(card)).thenReturn(cardDto);

        CardInfoDto result = cardInfoService.createCard(1L, cardDto);

        assertEquals(cardDto, result);
        verify(cardInfoRepository).save(card);
        assertEquals(user, card.getUser());
    }

    @Test
    void createCard_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> cardInfoService.createCard(999L, cardDto));
    }

    @Test
    void getCardsByUserId_ShouldReturnPagedDto() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardInfo> page = new PageImpl<>(List.of(card), pageable, 1);
        when(cardInfoRepository.findByUserId(1L, pageable)).thenReturn(page);
        when(cardInfoMapper.toDto(card)).thenReturn(cardDto);

        Page<CardInfoDto> result = cardInfoService.getCardsByUserId(1L, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(cardDto, result.getContent().get(0));
    }
}
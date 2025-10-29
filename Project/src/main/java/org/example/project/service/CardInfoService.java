    package org.example.project.service;

    import lombok.RequiredArgsConstructor;
    import org.example.project.dto.CardInfoDto;
    import org.example.project.entity.CardInfo;
    import org.example.project.entity.User;
    import org.example.project.exception.CardNotFoundException;
    import org.example.project.exception.UserNotFoundException;
    import org.example.project.mapper.CardInfoMapper;
    import org.example.project.repository.CardInfoRepository;
    import org.example.project.repository.UserRepository;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    @Service
    @Transactional(readOnly = true)
    public class CardInfoService {

        private final CardInfoRepository cardInfoRepository;
        private final UserRepository userRepository;
        private final CardInfoMapper cardInfoMapper;

        public CardInfoService(CardInfoRepository cardInfoRepository, UserRepository userRepository, CardInfoMapper cardInfoMapper)
        {
            this.cardInfoRepository = cardInfoRepository;
            this.userRepository = userRepository;
            this.cardInfoMapper = cardInfoMapper;
        }

        @Transactional
        public CardInfoDto createCard(Long userId, CardInfoDto dto) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

            CardInfo card = cardInfoMapper.toEntity(dto);
            card.setUser(user);
            card = cardInfoRepository.save(card);
            return cardInfoMapper.toDto(card);
        }

        public CardInfoDto getCardById(Long id) {
            CardInfo card = cardInfoRepository.findById(id)
                    .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
            return cardInfoMapper.toDto(card);
        }

        public Page<CardInfoDto> getCardsByUserId(Long userId, Pageable pageable) {
            return cardInfoRepository.findByUserId(userId, pageable)
                    .map(cardInfoMapper::toDto);
        }

        public Page<CardInfoDto> getAllCards(Pageable pageable) {
            return cardInfoRepository.findAllCards(pageable)
                    .map(cardInfoMapper::toDto);
        }

        @Transactional
        public CardInfoDto updateCard(Long id, CardInfoDto dto) {
            CardInfo card = cardInfoRepository.findById(id)
                    .orElseThrow(() -> new CardNotFoundException("Card not found with id: " + id));
            cardInfoMapper.updateEntityFromDto(dto, card);
            card = cardInfoRepository.save(card);
            return cardInfoMapper.toDto(card);
        }

        @Transactional
        public void deleteCard(Long id) {
            if (!cardInfoRepository.existsById(id)) {
                throw new CardNotFoundException("Card not found with id: " + id);
            }
            cardInfoRepository.deleteById(id);
        }
    }
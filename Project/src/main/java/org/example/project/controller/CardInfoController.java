package org.example.project.controller;

import jakarta.validation.Valid;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.project.dto.CardInfoDto;
import org.example.project.service.CardInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardInfoController {

    @Autowired
    private final CardInfoService cardInfoService;

    public CardInfoController(CardInfoService cardInfoService) {
        this.cardInfoService = cardInfoService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<CardInfoDto> createCard(
            @PathVariable Long userId,
            @Valid @RequestBody CardInfoDto dto) {
        CardInfoDto created = cardInfoService.createCard(userId, dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoDto> getCardById(@PathVariable Long id) {
        return ResponseEntity.ok(cardInfoService.getCardById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<CardInfoDto>> getCardsByUserId(
            @PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(cardInfoService.getCardsByUserId(userId, pageable));
    }

    @GetMapping//
    public ResponseEntity<Page<CardInfoDto>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(cardInfoService.getAllCards(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardInfoDto> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody CardInfoDto dto) {
        return ResponseEntity.ok(cardInfoService.updateCard(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardInfoService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }
}
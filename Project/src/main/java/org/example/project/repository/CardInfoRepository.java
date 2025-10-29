package org.example.project.repository;

import org.example.project.entity.CardInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    Page<CardInfo> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT c FROM CardInfo c")
    Page<CardInfo> findAllCards(Pageable pageable);

    @Query(value = "SELECT * FROM card_info WHERE number LIKE :numberPattern", nativeQuery = true)
    Page<CardInfo> findByNumberPattern(String numberPattern, Pageable pageable);
}
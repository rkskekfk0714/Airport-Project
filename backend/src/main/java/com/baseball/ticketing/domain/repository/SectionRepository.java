package com.baseball.ticketing.domain.repository;

import com.baseball.ticketing.domain.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {

    @Query("SELECT s FROM Section s WHERE s.game.id = :gameId ORDER BY s.type")
    List<Section> findByGameId(@Param("gameId") Long gameId);
}

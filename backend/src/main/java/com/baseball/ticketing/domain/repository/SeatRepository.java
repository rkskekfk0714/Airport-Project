package com.baseball.ticketing.domain.repository;

import com.baseball.ticketing.domain.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.section.id = :sectionId ORDER BY s.row, s.number")
    List<Seat> findBySectionId(@Param("sectionId") Long sectionId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("SELECT s FROM Seat s WHERE s.id = :id")
    Optional<Seat> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT s FROM Seat s WHERE s.id IN :ids")
    List<Seat> findAllByIds(@Param("ids") List<Long> ids);

    @Query("SELECT s FROM Seat s JOIN FETCH s.section WHERE s.section.game.id = :gameId AND s.status = 'HELD'")
    List<Seat> findHeldSeatsByGameId(@Param("gameId") Long gameId);
}

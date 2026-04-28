package com.baseball.ticketing.domain.repository;

import com.baseball.ticketing.domain.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "WHERE g.gameDateTime BETWEEN :start AND :end " +
           "ORDER BY g.gameDateTime ASC")
    List<Game> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "WHERE (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) " +
           "AND g.gameDateTime >= :from ORDER BY g.gameDateTime ASC")
    List<Game> findByTeamAndDateFrom(@Param("teamId") Long teamId, @Param("from") LocalDateTime from);

    @Query("SELECT g FROM Game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "WHERE g.status = 'SCHEDULED' AND g.saleStartAt <= :now")
    List<Game> findGamesReadyForSale(@Param("now") LocalDateTime now);
}

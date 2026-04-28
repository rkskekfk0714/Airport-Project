package com.baseball.ticketing.domain.repository;

import com.baseball.ticketing.domain.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.game g JOIN FETCH g.homeTeam JOIN FETCH g.awayTeam " +
           "WHERE r.user.id = :userId AND r.status != 'CANCELLED' ORDER BY r.createdAt DESC")
    List<Reservation> findActiveByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(rs) FROM ReservationSeat rs WHERE rs.reservation.user.id = :userId " +
           "AND rs.reservation.game.id = :gameId AND rs.reservation.status != 'CANCELLED'")
    int countBookedSeatsByUserAndGame(@Param("userId") Long userId, @Param("gameId") Long gameId);

    Optional<Reservation> findByQrCode(String qrCode);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.reservationSeats rs JOIN FETCH rs.seat " +
           "WHERE r.id = :id")
    Optional<Reservation> findByIdWithSeats(@Param("id") Long id);
}

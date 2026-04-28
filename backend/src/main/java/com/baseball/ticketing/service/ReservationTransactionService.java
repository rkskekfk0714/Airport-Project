package com.baseball.ticketing.service;

import com.baseball.ticketing.domain.entity.*;
import com.baseball.ticketing.domain.repository.*;
import com.baseball.ticketing.exception.ApiException;
import com.baseball.ticketing.infra.redis.SeatHoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 예매 생성 트랜잭션 로직 분리.
 * ReservationService에서 자기 호출(self-invocation)로는 @Transactional이 동작하지 않으므로,
 * 별도 빈으로 분리하여 Spring AOP 프록시를 통해 호출되도록 함.
 */
@Service
@RequiredArgsConstructor
public class ReservationTransactionService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final SeatHoldService seatHoldService;

    @Value("${reservation.max-per-game:4}")
    private int maxSeatsPerGame;

    @Transactional
    public Reservation createReservation(Long userId, Long gameId, List<Long> seatIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "경기를 찾을 수 없습니다."));

        if (game.getStatus() == Game.GameStatus.CANCELLED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "취소된 경기입니다.");
        }

        int alreadyBooked = reservationRepository.countBookedSeatsByUserAndGame(userId, gameId);
        if (alreadyBooked + seatIds.size() > maxSeatsPerGame) {
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "경기당 최대 " + maxSeatsPerGame + "매까지 예매할 수 있습니다. 현재 " + alreadyBooked + "매 예매됨.");
        }

        List<Seat> seats = seatIds.stream()
                .map(seatId -> {
                    if (!seatHoldService.isHeldByUser(seatId, userId)) {
                        throw new ApiException(HttpStatus.BAD_REQUEST, "임시 점유하지 않은 좌석이 포함되어 있습니다.");
                    }
                    return seatRepository.findByIdWithLock(seatId)
                            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "좌석을 찾을 수 없습니다."));
                })
                .toList();

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Seat seat : seats) {
            seat.book();
            totalPrice = totalPrice.add(seat.getSection().getPrice());
            seat.getSection().decreaseAvailableSeats(1);
        }

        Reservation reservation = Reservation.builder()
                .user(user)
                .game(game)
                .totalPrice(totalPrice)
                .build();
        reservationRepository.save(reservation);

        for (Seat seat : seats) {
            ReservationSeat rs = ReservationSeat.builder()
                    .reservation(reservation)
                    .seat(seat)
                    .build();
            reservation.getReservationSeats().add(rs);
        }

        seatHoldService.releaseSeats(seatIds, userId);
        return reservation;
    }
}

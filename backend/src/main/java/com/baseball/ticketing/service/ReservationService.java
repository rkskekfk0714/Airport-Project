package com.baseball.ticketing.service;

import com.baseball.ticketing.domain.entity.Reservation;
import com.baseball.ticketing.domain.repository.ReservationRepository;
import com.baseball.ticketing.domain.repository.SeatRepository;
import com.baseball.ticketing.domain.repository.SectionRepository;
import com.baseball.ticketing.domain.repository.GameRepository;
import com.baseball.ticketing.domain.repository.UserRepository;
import com.baseball.ticketing.dto.request.HoldRequest;
import com.baseball.ticketing.dto.request.ReservationRequest;
import com.baseball.ticketing.dto.response.ReservationResponse;
import com.baseball.ticketing.exception.ApiException;
import com.baseball.ticketing.infra.redis.DistributedLockService;
import com.baseball.ticketing.infra.redis.SeatHoldService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final SectionRepository sectionRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final SeatHoldService seatHoldService;
    private final DistributedLockService lockService;
    private final ReservationTransactionService reservationTransactionService;

    @Value("${reservation.max-per-game:4}")
    private int maxSeatsPerGame;

    /**
     * 좌석 임시 점유 (5분 타임아웃)
     */
    public void holdSeats(Long userId, HoldRequest request) {
        boolean success = seatHoldService.holdSeats(request.getSeatIds(), userId);
        if (!success) {
            throw new ApiException(HttpStatus.CONFLICT, "이미 다른 사람이 선택한 좌석이 포함되어 있습니다.");
        }
    }

    /**
     * 좌석 임시 점유 해제
     */
    public void releaseSeats(Long userId, List<Long> seatIds) {
        seatHoldService.releaseSeats(seatIds, userId);
    }

    /**
     * 예매 생성 (분산 락 + 낙관적 락으로 중복 방지)
     */
    public ReservationResponse createReservation(Long userId, ReservationRequest request) {
        final long[] reservationId = {0};

        lockService.executeWithMultiLock(request.getSeatIds(), () -> {
            Reservation reservation = reservationTransactionService.createReservation(
                    userId, request.getGameId(), request.getSeatIds());
            reservationId[0] = reservation.getId();
        });

        return reservationRepository.findByIdWithSeats(reservationId[0])
                .map(ReservationResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "예매 생성에 실패했습니다."));
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdWithSeats(reservationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "예매를 찾을 수 없습니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }
        return ReservationResponse.from(reservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservations(Long userId) {
        return reservationRepository.findActiveByUserId(userId).stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void cancelReservation(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findByIdWithSeats(reservationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "예매를 찾을 수 없습니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "이미 취소된 예매입니다.");
        }

        // 좌석 및 구역 잔여석 복원
        reservation.getReservationSeats().forEach(rs -> {
            rs.getSeat().release();
            rs.getSeat().getSection().increaseAvailableSeats(1);
        });

        reservation.cancel();
    }
}

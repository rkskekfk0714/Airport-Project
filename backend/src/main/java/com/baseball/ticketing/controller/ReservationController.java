package com.baseball.ticketing.controller;

import com.baseball.ticketing.dto.request.HoldRequest;
import com.baseball.ticketing.dto.request.ReservationRequest;
import com.baseball.ticketing.dto.response.ReservationResponse;
import com.baseball.ticketing.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Reservations", description = "예매 API")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/seats/hold")
    @Operation(summary = "좌석 임시 점유 (5분)")
    public ResponseEntity<Void> holdSeats(@AuthenticationPrincipal Long userId,
                                          @Valid @RequestBody HoldRequest request) {
        reservationService.holdSeats(userId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/seats/hold")
    @Operation(summary = "좌석 임시 점유 해제")
    public ResponseEntity<Void> releaseSeats(@AuthenticationPrincipal Long userId,
                                             @RequestBody List<Long> seatIds) {
        reservationService.releaseSeats(userId, seatIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reservations")
    @Operation(summary = "예매 생성")
    public ResponseEntity<ReservationResponse> createReservation(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservationService.createReservation(userId, request));
    }

    @GetMapping("/reservations")
    @Operation(summary = "내 예매 목록")
    public ResponseEntity<List<ReservationResponse>> getMyReservations(@AuthenticationPrincipal Long userId) {
        return ResponseEntity.ok(reservationService.getMyReservations(userId));
    }

    @GetMapping("/reservations/{reservationId}")
    @Operation(summary = "예매 상세 조회 (QR코드 포함)")
    public ResponseEntity<ReservationResponse> getReservation(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(reservationService.getReservation(userId, reservationId));
    }

    @DeleteMapping("/reservations/{reservationId}")
    @Operation(summary = "예매 취소")
    public ResponseEntity<Void> cancelReservation(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reservationId) {
        reservationService.cancelReservation(userId, reservationId);
        return ResponseEntity.noContent().build();
    }
}

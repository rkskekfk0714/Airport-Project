package com.baseball.ticketing.controller;

import com.baseball.ticketing.dto.request.PaymentRequest;
import com.baseball.ticketing.dto.response.PaymentResponse;
import com.baseball.ticketing.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "결제 API")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "결제 처리 (Mock PG)")
    public ResponseEntity<PaymentResponse> processPayment(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.processPayment(userId, request));
    }

    @GetMapping("/reservations/{reservationId}")
    @Operation(summary = "예매의 결제 정보 조회")
    public ResponseEntity<PaymentResponse> getPayment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long reservationId) {
        return ResponseEntity.ok(paymentService.getPayment(userId, reservationId));
    }
}

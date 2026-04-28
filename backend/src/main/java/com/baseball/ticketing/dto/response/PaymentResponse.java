package com.baseball.ticketing.dto.response;

import com.baseball.ticketing.domain.entity.Payment;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class PaymentResponse {
    private Long id;
    private Long reservationId;
    private BigDecimal amount;
    private String status;
    private String method;
    private String pgTransactionId;
    private LocalDateTime paidAt;

    public static PaymentResponse from(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .reservationId(payment.getReservation().getId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .method(payment.getMethod().name())
                .pgTransactionId(payment.getPgTransactionId())
                .paidAt(payment.getPaidAt())
                .build();
    }
}

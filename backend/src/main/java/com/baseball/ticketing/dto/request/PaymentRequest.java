package com.baseball.ticketing.dto.request;

import com.baseball.ticketing.domain.entity.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {

    @NotNull
    private Long reservationId;

    @NotNull
    private Payment.PaymentMethod method;
}

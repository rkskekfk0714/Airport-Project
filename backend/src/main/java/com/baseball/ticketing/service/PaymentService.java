package com.baseball.ticketing.service;

import com.baseball.ticketing.domain.entity.Payment;
import com.baseball.ticketing.domain.entity.Reservation;
import com.baseball.ticketing.domain.repository.PaymentRepository;
import com.baseball.ticketing.domain.repository.ReservationRepository;
import com.baseball.ticketing.dto.request.PaymentRequest;
import com.baseball.ticketing.dto.response.PaymentResponse;
import com.baseball.ticketing.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    /**
     * Mock PG 결제 처리.
     * 실제 서비스에서는 토스페이먼츠, 카카오페이 등 PG API 연동.
     */
    @Transactional
    public PaymentResponse processPayment(Long userId, PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "예매를 찾을 수 없습니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "결제할 수 없는 상태의 예매입니다.");
        }

        // 중복 결제 방지
        paymentRepository.findByReservationId(reservation.getId()).ifPresent(p -> {
            if (p.getStatus() == Payment.PaymentStatus.COMPLETED) {
                throw new ApiException(HttpStatus.CONFLICT, "이미 결제된 예매입니다.");
            }
        });

        Payment payment = Payment.builder()
                .reservation(reservation)
                .amount(reservation.getTotalPrice())
                .method(request.getMethod())
                .build();

        // Mock PG 호출 시뮬레이션
        String pgTransactionId = mockPgCall(payment);
        payment.complete(pgTransactionId);
        paymentRepository.save(payment);

        // 예매 상태 확정
        reservation.confirm();

        log.info("Payment completed: reservationId={}, pgTxId={}", reservation.getId(), pgTransactionId);
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long userId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "예매를 찾을 수 없습니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
        }

        Payment payment = paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

        return PaymentResponse.from(payment);
    }

    /**
     * Mock PG API 호출. 실제 환경에서는 PG사 SDK로 교체.
     */
    private String mockPgCall(Payment payment) {
        // 95% 성공, 5% 실패 시뮬레이션
        if (Math.random() < 0.05) {
            payment.fail();
            throw new ApiException(HttpStatus.PAYMENT_REQUIRED, "결제에 실패했습니다. 다시 시도해 주세요.");
        }
        return "PG-" + UUID.randomUUID().toString().substring(0, 16).toUpperCase();
    }
}

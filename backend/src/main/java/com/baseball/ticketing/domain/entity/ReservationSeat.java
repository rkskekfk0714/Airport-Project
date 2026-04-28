package com.baseball.ticketing.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation_seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Builder
    public ReservationSeat(Reservation reservation, Seat seat) {
        this.reservation = reservation;
        this.seat = seat;
    }
}

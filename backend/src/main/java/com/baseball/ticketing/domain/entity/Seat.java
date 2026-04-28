package com.baseball.ticketing.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seats", indexes = {
    @Index(name = "idx_seat_section", columnList = "section_id"),
    @Index(name = "idx_seat_status", columnList = "status")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(nullable = false, length = 5)
    private String row;

    @Column(nullable = false)
    private Integer number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status = SeatStatus.AVAILABLE;

    @Version
    private Long version;

    public enum SeatStatus {
        AVAILABLE, HELD, BOOKED
    }

    @Builder
    public Seat(Section section, String row, Integer number) {
        this.section = section;
        this.row = row;
        this.number = number;
        this.status = SeatStatus.AVAILABLE;
    }

    public void hold() {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 선택된 좌석입니다.");
        }
        this.status = SeatStatus.HELD;
    }

    public void book() {
        if (this.status == SeatStatus.BOOKED) {
            throw new IllegalStateException("이미 예매 완료된 좌석입니다.");
        }
        this.status = SeatStatus.BOOKED;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }
}

package com.baseball.ticketing.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sections")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SectionType type;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    public enum SectionType {
        FIRST_BASE,    // 1루
        THIRD_BASE,    // 3루
        OUTFIELD,      // 외야
        PREMIUM,       // 프리미엄
        FAMILY         // 패밀리
    }

    @Builder
    public Section(Game game, String name, SectionType type, BigDecimal price, int totalSeats) {
        this.game = game;
        this.name = name;
        this.type = type;
        this.price = price;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public void decreaseAvailableSeats(int count) {
        if (this.availableSeats < count) {
            throw new IllegalStateException("잔여 좌석이 부족합니다.");
        }
        this.availableSeats -= count;
    }

    public void increaseAvailableSeats(int count) {
        this.availableSeats += count;
    }
}

package com.baseball.ticketing.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @Column(nullable = false)
    private LocalDateTime gameDateTime;

    @Column(nullable = false, length = 100)
    private String stadium;

    @Column(nullable = false)
    private LocalDateTime saleStartAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameStatus status = GameStatus.SCHEDULED;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public enum GameStatus {
        SCHEDULED, ON_SALE, SOLD_OUT, COMPLETED, CANCELLED
    }

    @Builder
    public Game(Team homeTeam, Team awayTeam, LocalDateTime gameDateTime,
                String stadium, LocalDateTime saleStartAt, GameStatus status) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.gameDateTime = gameDateTime;
        this.stadium = stadium;
        this.saleStartAt = saleStartAt;
        this.status = status != null ? status : GameStatus.SCHEDULED;
    }

    public void updateStatus(GameStatus status) {
        this.status = status;
    }
}

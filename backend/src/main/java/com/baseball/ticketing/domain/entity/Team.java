package com.baseball.ticketing.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teams")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, length = 100)
    private String stadium;

    @Column(length = 255)
    private String logoUrl;

    @Column(nullable = false, length = 10)
    private String shortName;

    @Builder
    public Team(String name, String stadium, String logoUrl, String shortName) {
        this.name = name;
        this.stadium = stadium;
        this.logoUrl = logoUrl;
        this.shortName = shortName;
    }
}

package com.baseball.ticketing.dto.response;

import com.baseball.ticketing.domain.entity.Game;
import com.baseball.ticketing.domain.entity.Section;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GameResponse {
    private Long id;
    private TeamInfo homeTeam;
    private TeamInfo awayTeam;
    private LocalDateTime gameDateTime;
    private String stadium;
    private LocalDateTime saleStartAt;
    private String status;
    private int totalAvailableSeats;

    public static GameResponse from(Game game) {
        int totalAvailable = game.getSections().stream()
                .mapToInt(Section::getAvailableSeats)
                .sum();

        return GameResponse.builder()
                .id(game.getId())
                .homeTeam(TeamInfo.from(game.getHomeTeam()))
                .awayTeam(TeamInfo.from(game.getAwayTeam()))
                .gameDateTime(game.getGameDateTime())
                .stadium(game.getStadium())
                .saleStartAt(game.getSaleStartAt())
                .status(game.getStatus().name())
                .totalAvailableSeats(totalAvailable)
                .build();
    }

    @Getter
    @Builder
    public static class TeamInfo {
        private Long id;
        private String name;
        private String shortName;
        private String logoUrl;

        public static TeamInfo from(com.baseball.ticketing.domain.entity.Team team) {
            return TeamInfo.builder()
                    .id(team.getId())
                    .name(team.getName())
                    .shortName(team.getShortName())
                    .logoUrl(team.getLogoUrl())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class SectionInfo {
        private Long id;
        private String name;
        private String type;
        private BigDecimal price;
        private int totalSeats;
        private int availableSeats;

        public static SectionInfo from(Section section) {
            return SectionInfo.builder()
                    .id(section.getId())
                    .name(section.getName())
                    .type(section.getType().name())
                    .price(section.getPrice())
                    .totalSeats(section.getTotalSeats())
                    .availableSeats(section.getAvailableSeats())
                    .build();
        }
    }
}

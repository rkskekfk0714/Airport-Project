package com.baseball.ticketing.dto.response;

import com.baseball.ticketing.domain.entity.Reservation;
import com.baseball.ticketing.domain.entity.ReservationSeat;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReservationResponse {
    private Long id;
    private Long gameId;
    private String gameTitle;
    private LocalDateTime gameDateTime;
    private String stadium;
    private BigDecimal totalPrice;
    private String status;
    private String qrCode;
    private List<SeatInfo> seats;
    private LocalDateTime createdAt;

    public static ReservationResponse from(Reservation reservation) {
        String gameTitle = reservation.getGame().getHomeTeam().getShortName()
                + " vs " + reservation.getGame().getAwayTeam().getShortName();

        List<SeatInfo> seats = reservation.getReservationSeats().stream()
                .map(rs -> SeatInfo.from(rs))
                .toList();

        return ReservationResponse.builder()
                .id(reservation.getId())
                .gameId(reservation.getGame().getId())
                .gameTitle(gameTitle)
                .gameDateTime(reservation.getGame().getGameDateTime())
                .stadium(reservation.getGame().getStadium())
                .totalPrice(reservation.getTotalPrice())
                .status(reservation.getStatus().name())
                .qrCode(reservation.getQrCode())
                .seats(seats)
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    @Getter
    @Builder
    public static class SeatInfo {
        private Long seatId;
        private String sectionName;
        private String row;
        private Integer number;

        public static SeatInfo from(ReservationSeat rs) {
            return SeatInfo.builder()
                    .seatId(rs.getSeat().getId())
                    .sectionName(rs.getSeat().getSection().getName())
                    .row(rs.getSeat().getRow())
                    .number(rs.getSeat().getNumber())
                    .build();
        }
    }
}

package com.baseball.ticketing.dto.response;

import com.baseball.ticketing.domain.entity.Seat;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SeatResponse {
    private Long id;
    private String row;
    private Integer number;
    private String status;

    public static SeatResponse from(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .row(seat.getRow())
                .number(seat.getNumber())
                .status(seat.getStatus().name())
                .build();
    }
}

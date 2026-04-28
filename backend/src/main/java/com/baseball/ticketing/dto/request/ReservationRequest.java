package com.baseball.ticketing.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ReservationRequest {

    @NotNull
    private Long gameId;

    @NotEmpty
    @Size(min = 1, max = 4, message = "좌석은 1~4개까지 선택 가능합니다.")
    private List<Long> seatIds;
}

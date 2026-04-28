package com.baseball.ticketing.controller;

import com.baseball.ticketing.dto.response.GameResponse;
import com.baseball.ticketing.dto.response.SeatResponse;
import com.baseball.ticketing.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Tag(name = "Games", description = "경기 API")
public class GameController {

    private final GameService gameService;

    @GetMapping
    @Operation(summary = "경기 목록 조회 (날짜 또는 팀 기준 필터)")
    public ResponseEntity<List<GameResponse>> getGames(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long teamId) {
        return ResponseEntity.ok(gameService.getGames(date, teamId));
    }

    @GetMapping("/{gameId}")
    @Operation(summary = "경기 상세 조회")
    public ResponseEntity<GameResponse> getGame(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGame(gameId));
    }

    @GetMapping("/{gameId}/sections")
    @Operation(summary = "경기 구역 목록 (잔여 좌석 포함)")
    public ResponseEntity<List<GameResponse.SectionInfo>> getSections(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getSections(gameId));
    }

    @GetMapping("/sections/{sectionId}/seats")
    @Operation(summary = "구역 내 좌석 배치도")
    public ResponseEntity<List<SeatResponse>> getSeats(@PathVariable Long sectionId) {
        return ResponseEntity.ok(gameService.getSeats(sectionId));
    }
}

package com.baseball.ticketing.service;

import com.baseball.ticketing.domain.entity.Game;
import com.baseball.ticketing.domain.entity.Section;
import com.baseball.ticketing.domain.repository.GameRepository;
import com.baseball.ticketing.domain.repository.SeatRepository;
import com.baseball.ticketing.domain.repository.SectionRepository;
import com.baseball.ticketing.dto.response.GameResponse;
import com.baseball.ticketing.dto.response.SeatResponse;
import com.baseball.ticketing.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;

    @Transactional(readOnly = true)
    public List<GameResponse> getGames(LocalDate date, Long teamId) {
        List<Game> games;
        if (date != null) {
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            games = gameRepository.findByDateRange(start, end);
        } else if (teamId != null) {
            games = gameRepository.findByTeamAndDateFrom(teamId, LocalDateTime.now());
        } else {
            LocalDateTime start = LocalDateTime.now();
            LocalDateTime end = start.plusDays(30);
            games = gameRepository.findByDateRange(start, end);
        }
        return games.stream().map(GameResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public GameResponse getGame(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "경기를 찾을 수 없습니다."));
        return GameResponse.from(game);
    }

    @Transactional(readOnly = true)
    public List<GameResponse.SectionInfo> getSections(Long gameId) {
        gameRepository.findById(gameId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "경기를 찾을 수 없습니다."));
        return sectionRepository.findByGameId(gameId).stream()
                .map(GameResponse.SectionInfo::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SeatResponse> getSeats(Long sectionId) {
        return seatRepository.findBySectionId(sectionId).stream()
                .map(SeatResponse::from)
                .toList();
    }
}

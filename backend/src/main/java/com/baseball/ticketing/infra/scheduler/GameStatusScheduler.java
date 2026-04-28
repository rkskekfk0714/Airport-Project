package com.baseball.ticketing.infra.scheduler;

import com.baseball.ticketing.domain.entity.Game;
import com.baseball.ticketing.domain.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 예매 오픈 시간이 된 경기를 ON_SALE로 자동 전환.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GameStatusScheduler {

    private final GameRepository gameRepository;

    @Scheduled(fixedDelay = 60000) // 1분마다
    @Transactional
    public void activateGamesForSale() {
        List<Game> games = gameRepository.findGamesReadyForSale(java.time.LocalDateTime.now());
        games.forEach(game -> {
            game.updateStatus(Game.GameStatus.ON_SALE);
            log.info("Game {} is now ON_SALE", game.getId());
        });
    }
}

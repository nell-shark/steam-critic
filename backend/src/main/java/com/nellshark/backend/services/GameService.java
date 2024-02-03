package com.nellshark.backend.services;

import com.nellshark.backend.dtos.GameDTO;
import com.nellshark.backend.exceptions.GameNotFoundException;
import com.nellshark.backend.models.CountryCode;
import com.nellshark.backend.models.Game;
import com.nellshark.backend.models.Price;
import com.nellshark.backend.repositories.GameRepository;
import com.nellshark.backend.utils.MappingUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.nellshark.backend.models.CountryCode.DE;
import static com.nellshark.backend.models.CountryCode.KZ;
import static com.nellshark.backend.models.CountryCode.RU;
import static com.nellshark.backend.models.CountryCode.US;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final PriceService priceService;
    private final SteamService steamService;

    public List<GameDTO> getAllGameDTOs() {
        log.info("Getting all games DTO");
        return gameRepository.findAll()
                .stream()
                .map(MappingUtils::toDTO)
                .toList();
    }

    private List<Game> getAllGames() {
        log.info("Getting all games");
        return gameRepository.findAll();
    }

    public Game getGameById(long id) {
        log.info("Getting game by id: {}", id);
        return gameRepository
                .findById(id)
                .orElseThrow(() -> new GameNotFoundException("Game not found id=" + id));
    }

    private List<Long> getAllGameIds() {
        log.info("Getting all game ids");
        return gameRepository.findAllIds();
    }

    @Scheduled(cron = "@daily")
    @PostConstruct
    public void checkForNewGamesPeriodically() {
        log.info("Check new games");
        List<Long> allSteamGamesId = steamService.getAllSteamGameIds();
        List<Long> gameIdsFromDb = getAllGameIds();

        long newGamesCount = allSteamGamesId.stream()
                .filter(id -> !gameIdsFromDb.contains(id))
                .peek(this::addNewGame)
                .count();

        log.info("{} new games found", newGamesCount);
    }

    private void addNewGame(long id) {
        Game game = steamService.getGameInfo(id);
        if (isNull(game)) {
            return;
        }
        log.info("New game added to db: {}", game);
        gameRepository.save(game);
    }

    @Scheduled(initialDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void updateGamePricesPeriodically() {
        log.info("Check new prices");
        getAllGames().stream()
                .map(this::getUpdatedGamePrice)
                .forEach(priceService::savePrice);
    }

    private Price getUpdatedGamePrice(@NonNull Game game) {
        log.info("Updating game price: game={}", game);

        Map<CountryCode, Long> map = Stream.of(CountryCode.values())
                .collect(Collectors.toMap(
                        countryCode -> countryCode,
                        countryCode -> steamService.getNewGamePrice(game.getId(), countryCode)
                ));

        return new Price(
                map.get(US),
                map.get(DE),
                map.get(RU),
                map.get(KZ),
                LocalDateTime.now(),
                game
        );
    }
}

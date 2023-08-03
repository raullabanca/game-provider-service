package exercise.gameproviderservice.rest.controller;

import exercise.gameproviderservice.mapper.GameMapper;
import exercise.gameproviderservice.rest.payloads.GameRequest;
import exercise.gameproviderservice.rest.payloads.GameResponse;
import exercise.gameproviderservice.service.GameService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/v1/games", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final GameMapper gameMapper;

    @GetMapping
    public ResponseEntity<List<GameResponse>> getGames() {
        var gameList = gameService.getGames()
                .stream()
                .map(gameMapper::gameToGameResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gameList);
    }

    @GetMapping("/{gameName}")
    public ResponseEntity<GameResponse> getGame(@NotBlank @PathVariable String gameName) {
        var game = gameMapper.gameToGameResponse(gameService.getGame(gameName));
        return ResponseEntity.ok(game);
    }

    @PostMapping
    public ResponseEntity<GameResponse> insertGame(@Valid @RequestBody GameRequest gameRequest) {
        var game = gameService.saveGame(gameRequest);
        return ResponseEntity.ok(gameMapper.gameToGameResponse(game));
    }

    @PutMapping
    @Retryable(retryFor = { StaleObjectStateException.class,
            ObjectOptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public ResponseEntity<GameResponse> updateGame(@Valid @RequestBody GameRequest gameRequest) {
        var game = gameService.updateGame(gameRequest);
        return ResponseEntity.ok(gameMapper.gameToGameResponse(game));
    }

    @DeleteMapping("/{gameName}")
    @Retryable(retryFor = { StaleObjectStateException.class,
            ObjectOptimisticLockingFailureException.class }, maxAttempts = 3, backoff = @Backoff(delay = 5000))
    public ResponseEntity<Void> deleteGame(@NotBlank @PathVariable String gameName) {
        gameService.deleteGame(gameName);
        return ResponseEntity.noContent().build();
    }
}

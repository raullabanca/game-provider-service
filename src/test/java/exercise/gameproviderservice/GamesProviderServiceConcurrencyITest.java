package exercise.gameproviderservice;

import exercise.gameproviderservice.domain.GameModel;
import exercise.gameproviderservice.repository.GameRepository;
import exercise.gameproviderservice.rest.controller.GameController;
import exercise.gameproviderservice.rest.payloads.GameRequest;
import exercise.gameproviderservice.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static exercise.gameproviderservice.fixtures.GameFixtures.createGame;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class GamesProviderServiceConcurrencyITest {

    @SpyBean
    private GameService gameService;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameController gameController;

    private final List<Instant> dateOfCreationList = List.of(
            Instant.now().plus(1, ChronoUnit.MINUTES),
            Instant.now().plus(2, ChronoUnit.MINUTES));

    @Test
    void updateGame_withOptimisticLockingHandling() throws InterruptedException {
        GameModel entity = createGame();
        final GameModel gameModel = gameRepository.save(entity);
        assertEquals(0, gameModel.getVersion());


        final ExecutorService executor = Executors.newFixedThreadPool(dateOfCreationList.size());

        for (final Instant dateOfCreation : dateOfCreationList) {
            executor.execute(() -> gameController.updateGame(new GameRequest(entity.getName(), dateOfCreation, true)));
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        Thread.sleep(1000);

        final GameModel updatedGameModel = gameRepository.findById(gameModel.getId()).get();

        assertAll(
                () -> assertEquals(2, updatedGameModel.getVersion()),
                () -> verify(gameService, atLeast(3)).updateGame(any())
        );
    }
}

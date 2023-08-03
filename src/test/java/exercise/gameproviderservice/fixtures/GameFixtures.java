package exercise.gameproviderservice.fixtures;

import exercise.gameproviderservice.domain.GameModel;
import exercise.gameproviderservice.rest.payloads.GameRequest;
import exercise.gameproviderservice.rest.payloads.GameResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameFixtures {
    private static final String GAME_NAME = "Game name";
    private static final Instant DOC = Instant.now();
    private static final boolean ACTIVE = false;

    public static GameModel createGame() {
        return new GameModel(GAME_NAME, DOC, ACTIVE);
    }

    public static GameRequest createGameRequest() {
        return new GameRequest(GAME_NAME, DOC, ACTIVE);
    }

    public static GameResponse createGameResponse() {
        return new GameResponse(GAME_NAME, DOC, ACTIVE);
    }
}

package exercise.gameproviderservice.mapper;

import exercise.gameproviderservice.domain.GameModel;
import exercise.gameproviderservice.rest.payloads.GameRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GameModelMapperUTest {

    private static final String GAME_NAME = "Game name";
    private static final Instant DOC = Instant.now();
    private static final boolean ACTIVE = false;
    @InjectMocks
    private GameMapperImpl testObj;

    @Test
    void gameRequestToGame_mapsProperly() {
        var gameRequest = new GameRequest(GAME_NAME, DOC, ACTIVE);

        var actual = testObj.gameRequestToGame(gameRequest);

        assertThat(actual.getName()).isEqualTo(gameRequest.name());
        assertThat(actual.getDateOfCreation()).isEqualTo(gameRequest.dateOfCreation());
        assertThat(actual.getActive()).isEqualTo(gameRequest.active());
    }

    @Test
    void gameToGameRequest_mapsProperly() {
        var game = new GameModel(GAME_NAME, DOC, ACTIVE);

        var actual = testObj.gameToGameResponse(game);

        assertThat(actual.name()).isEqualTo(game.getName());
        assertThat(actual.dateOfCreation()).isEqualTo(game.getDateOfCreation());
        assertThat(actual.active()).isEqualTo(game.getActive());
    }
}
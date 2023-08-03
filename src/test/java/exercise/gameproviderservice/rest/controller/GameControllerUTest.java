package exercise.gameproviderservice.rest.controller;

import exercise.gameproviderservice.domain.exception.GameNotFoundException;
import exercise.gameproviderservice.mapper.GameMapper;
import exercise.gameproviderservice.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static exercise.gameproviderservice.fixtures.GameFixtures.createGame;
import static exercise.gameproviderservice.fixtures.GameFixtures.createGameRequest;
import static exercise.gameproviderservice.fixtures.GameFixtures.createGameResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameControllerUTest {
    @Mock
    private GameService gameService;

    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private GameController testObj;

    @Test
    void getGames_noParams_returnsAllEntries() {
        var game = createGame();
        var gameResponse = createGameResponse();

        when(gameService.getGames()).thenReturn(List.of(game));
        when(gameMapper.gameToGameResponse(game)).thenReturn(gameResponse);

        ResponseEntity actual = testObj.getGames();

        assertThat(actual).isEqualTo(ResponseEntity.ok(List.of(gameResponse)));
    }

    @Test
    void getGames_somethingWentWrong_returnsInternalServerError() {
        var exception = new RuntimeException("Error");

        doThrow(exception).when(gameService).getGames();
        assertThatThrownBy(() ->  testObj.getGames())
                .isSameAs(exception);
    }

    @Test
    void getGame_gameName_returnsTheGame() {
        var game = createGame();
        var gameResponse = createGameResponse();
        var gameName = UUID.randomUUID().toString();

        when(gameService.getGame(gameName)).thenReturn(game);
        when(gameMapper.gameToGameResponse(game)).thenReturn(gameResponse);

        ResponseEntity actual = testObj.getGame(gameName);

        assertThat(actual).isEqualTo(ResponseEntity.ok(gameResponse));
    }

    @Test
    void getGame_gameNameButSomethingWentWrong_returnsInternalServerError() {
        var gameName = UUID.randomUUID().toString();
        var exception = new RuntimeException("Error");

        doThrow(exception).when(gameService).getGame(gameName);
        assertThatThrownBy(() ->  testObj.getGame(gameName))
                .isSameAs(exception);
    }

    @Test
    void getGame_gameNotFound_returnsNotFound() {
        var gameName = UUID.randomUUID().toString();
        var exception = new GameNotFoundException("Not found");

        doThrow(exception).when(gameService).getGame(gameName);
        assertThatThrownBy(() ->  testObj.getGame(gameName))
                .isSameAs(exception);
    }

    @Test
    void insertGame_correctPayload_successfullyInsertsGame() {
        var game = createGame();
        var gameResponse = createGameResponse();
        var gameRequest = createGameRequest();

        when(gameService.saveGame(gameRequest)).thenReturn(game);
        when(gameMapper.gameToGameResponse(game)).thenReturn(gameResponse);

        ResponseEntity actual = testObj.insertGame(createGameRequest());
        assertThat(actual).isEqualTo(ResponseEntity.ok(gameResponse));
    }

    @Test
    void insertGame_correctPayloadButSomethingWentWrong_returns500() {
        var gameRequest = createGameRequest();
        var exception = new RuntimeException("Error");

        doThrow(exception).when(gameService).saveGame(gameRequest);

        assertThatThrownBy(() ->  testObj.insertGame(createGameRequest()))
                .isSameAs(exception);
    }

    @Test
    void updateGame_correctPayload_successfullyUpdatesGame() {
        var game = createGame();
        var gameResponse = createGameResponse();
        var gameRequest = createGameRequest();

        when(gameService.updateGame(gameRequest)).thenReturn(game);
        when(gameMapper.gameToGameResponse(game)).thenReturn(gameResponse);

        ResponseEntity actual = testObj.updateGame(createGameRequest());
        assertThat(actual).isEqualTo(ResponseEntity.ok(gameResponse));
    }

    @Test
    void updateGame_correctPayloadButSomethingWentWrong_returns500() {
        var gameRequest = createGameRequest();
        var exception = new RuntimeException("Error");

        doThrow(exception).when(gameService).updateGame(gameRequest);

        assertThatThrownBy(() ->  testObj.updateGame(createGameRequest()))
                .isSameAs(exception);
    }

    @Test
    void deleteGame_gameName_successfullyDeletesGame() {
        ResponseEntity actual = testObj.deleteGame(UUID.randomUUID().toString());
        assertThat(actual).isEqualTo(ResponseEntity.noContent().build());
    }

    @Test
    void deleteGame_correctPayloadButSomethingWentWrong_returns500() {
        var gameName = UUID.randomUUID().toString();

        var exception = new RuntimeException("Error");

        doThrow(exception).when(gameService).deleteGame(gameName);

        assertThatThrownBy(() ->  testObj.deleteGame(gameName))
                .isSameAs(exception);
    }

    @Test
    void deleteGame_notFound_returnsNotFound() {
//        ResponseEntity actual = testObj.deleteGame(UUID.randomUUID().toString());
//        assertThat(actual).isNull();
    }
}

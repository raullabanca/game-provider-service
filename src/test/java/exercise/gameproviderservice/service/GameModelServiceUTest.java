package exercise.gameproviderservice.service;

import exercise.gameproviderservice.domain.GameModel;
import exercise.gameproviderservice.domain.exception.GameAlreadyExistsException;
import exercise.gameproviderservice.domain.exception.GameNotFoundException;
import exercise.gameproviderservice.mapper.GameMapper;
import exercise.gameproviderservice.repository.GameRepository;
import exercise.gameproviderservice.rest.payloads.GameRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameModelServiceUTest {

    private static final String GAME_NAME = "gameName";

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameMapper gameMapper;

    @InjectMocks
    private GameService testObj;

    @Test
    void saveGame_success() {
        GameRequest gameRequest = mock(GameRequest.class);
        GameModel gameModel = mock(GameModel.class);

        when(gameRepository.save(gameModel)).thenReturn(gameModel);
        when(gameMapper.gameRequestToGame(gameRequest)).thenReturn(gameModel);

        GameModel actual = testObj.saveGame(gameRequest);

        assertThat(actual).isEqualTo(gameModel);
    }

    @Test
    void saveGame_fails() {
        GameRequest gameRequest = mock(GameRequest.class);
        GameModel gameModel = mock(GameModel.class);
        RuntimeException exception = new RuntimeException("Exception");

        when(gameMapper.gameRequestToGame(gameRequest)).thenReturn(gameModel);
        doThrow(exception).when(gameRepository).save(gameModel);

        assertThatThrownBy(() ->  testObj.saveGame(gameRequest))
                .isSameAs(exception);
    }

    @Test
    void saveGame_gameFound_fails() {
        GameRequest gameRequest = mock(GameRequest.class);
        GameModel gameModel = mock(GameModel.class);
        GameAlreadyExistsException exception = new GameAlreadyExistsException(GAME_NAME);

        when(gameRequest.name()).thenReturn(GAME_NAME);
        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.of(gameModel));

        assertThatThrownBy(() ->  testObj.saveGame(gameRequest))
                .hasMessageContaining(exception.getMessage());
    }

    @Test
    void getGame_success() {
        GameModel gameModel = mock(GameModel.class);

        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.of(gameModel));

        GameModel actual = testObj.getGame(GAME_NAME);

        assertThat(actual).isEqualTo(gameModel);
    }

    @Test
    void getGame_fails() {
        RuntimeException exception = new RuntimeException("Exception");

        doThrow(exception).when(gameRepository).findByName(GAME_NAME);

        assertThatThrownBy(() ->  testObj.getGame(GAME_NAME))
                .isSameAs(exception);
    }

    @Test
    void getGame_gameNotFound_fails() {
        GameNotFoundException exception = new GameNotFoundException(GAME_NAME);

        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->  testObj.getGame(GAME_NAME))
                .hasMessageContaining(exception.getMessage());
    }

    @Test
    void getGames_success() {
        GameModel gameModel = mock(GameModel.class);
        List<GameModel> gameModelList = List.of(gameModel);

        when(gameRepository.findAll()).thenReturn(gameModelList);

        List<GameModel> actual = testObj.getGames();

        assertThat(actual).isEqualTo(gameModelList);
    }

    @Test
    void getGames_fails() {
        RuntimeException exception = new RuntimeException("Exception");

        doThrow(exception).when(gameRepository).findAll();

        assertThatThrownBy(() ->  testObj.getGames())
                .isSameAs(exception);
    }

    @Test
    void updateGame_success() {
        GameRequest gameRequest = mock(GameRequest.class);
        GameModel gameModel = mock(GameModel.class);

        when(gameRequest.name()).thenReturn(GAME_NAME);
        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.of(gameModel));
        when(gameRepository.saveAndFlush(gameModel)).thenReturn(gameModel);

        GameModel actual = testObj.updateGame(gameRequest);

        assertThat(actual).isEqualTo(gameModel);
    }

    @Test
    void updateGame_fails() {
        GameRequest gameRequest = mock(GameRequest.class);
        GameModel gameModel = mock(GameModel.class);
        RuntimeException exception = new RuntimeException("Exception");

        when(gameRequest.name()).thenReturn(GAME_NAME);
        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.of(gameModel));
        doThrow(exception).when(gameRepository).saveAndFlush(gameModel);

        assertThatThrownBy(() ->  testObj.updateGame(gameRequest))
                .isSameAs(exception);
    }

    @Test
    void updateGame_gameNotFound_fails() {
        GameRequest gameRequest = mock(GameRequest.class);
        GameNotFoundException exception = new GameNotFoundException(GAME_NAME);

        when(gameRequest.name()).thenReturn(GAME_NAME);
        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->  testObj.updateGame(gameRequest))
                .hasMessageContaining(exception.getMessage());
    }

    @Test
    void deleteGame_success() {
        GameModel gameModel = mock(GameModel.class);

        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.of(gameModel));

        testObj.deleteGame(GAME_NAME);

        verify(gameRepository).deleteById(anyLong());
    }

    @Test
    void deleteGame_fails() {
        GameModel gameModel = mock(GameModel.class);
        RuntimeException exception = new RuntimeException("Exception");

        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.of(gameModel));
        doThrow(exception).when(gameRepository).deleteById(anyLong());

        assertThatThrownBy(() ->  testObj.deleteGame(GAME_NAME))
                .isSameAs(exception);

        verify(gameRepository).deleteById(anyLong());
    }

    @Test
    void deleteGame_gameNotFound_fails() {
        GameNotFoundException exception = new GameNotFoundException(GAME_NAME);

        when(gameRepository.findByName(GAME_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->  testObj.deleteGame(GAME_NAME))
                .hasMessageContaining(exception.getMessage());
    }

}

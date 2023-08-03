package exercise.gameproviderservice.service;

import exercise.gameproviderservice.domain.GameModel;
import exercise.gameproviderservice.domain.exception.GameAlreadyExistsException;
import exercise.gameproviderservice.domain.exception.GameNotFoundException;
import exercise.gameproviderservice.mapper.GameMapper;
import exercise.gameproviderservice.repository.GameRepository;
import exercise.gameproviderservice.rest.payloads.GameRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    final GameRepository gameRepository;
    final GameMapper mapper;

    static int i = 1;

    public GameModel saveGame(GameRequest gameRequest) {
        log.info("Save the game {}", gameRequest.name());

        gameRepository.findByName(gameRequest.name()).ifPresent(game -> {
            throw new GameAlreadyExistsException(gameRequest.name());
        });

        GameModel gameModel = mapper.gameRequestToGame(gameRequest);

        return gameRepository.save(gameModel);
    }

    @Transactional(readOnly = true)
    public GameModel getGame(String gameName){
        log.info("Get the game with name: {}", gameName);
        return gameRepository.findByName(gameName).orElseThrow(() -> new GameNotFoundException(gameName));
    }

    public List<GameModel> getGames(){
        return gameRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public GameModel updateGame(GameRequest gameRequest) {
        log.info("Update the game with name: {} and values: {}", gameRequest.name(), gameRequest);

        GameModel dbGameModel = getGame(gameRequest.name());

        dbGameModel.setName(gameRequest.name());
        dbGameModel.setDateOfCreation(gameRequest.dateOfCreation());
        dbGameModel.setActive(gameRequest.active());

        return gameRepository.saveAndFlush(dbGameModel);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteGame(String gameName) {
        log.info("Delete the game with name: {}", gameName);

        GameModel gameModel = getGame(gameName);

        gameRepository.deleteById(gameModel.getId());
    }
}

package exercise.gameproviderservice.domain.exception;

public class GameAlreadyExistsException extends RuntimeException {
    public GameAlreadyExistsException(String gameName) {
        super("The game [%s] already exists".formatted(gameName));
    }
}

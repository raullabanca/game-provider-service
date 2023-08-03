package exercise.gameproviderservice.domain.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String gameName) {
        super("Cannot find any game with Name [%s]".formatted(gameName));
    }
}

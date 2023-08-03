package exercise.gameproviderservice.rest.controller.advice;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class ErrorResponse {

    @Getter
    private final List<Error> errors;

    public ErrorResponse() {
        this.errors = new ArrayList<>();
    }

    public void add(Error error) {
        this.errors.add(error);
    }

    public record Error(@Getter String errorMessage) {}
}
package exercise.gameproviderservice.rest.controller.advice;

import exercise.gameproviderservice.domain.exception.GameAlreadyExistsException;
import exercise.gameproviderservice.domain.exception.GameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(GameNotFoundException.class)
    ErrorResponse handle(GameNotFoundException exception) {
        final var response = new ErrorResponse();
        response.add(new ErrorResponse.Error(exception.getMessage()));
        return response;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GameAlreadyExistsException.class)
    ErrorResponse handle(GameAlreadyExistsException exception) {
        final var response = new ErrorResponse();
        response.add(new ErrorResponse.Error(exception.getMessage()));
        return response;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ErrorResponse handle(MethodArgumentNotValidException exception) {

        final ErrorResponse response = new ErrorResponse();

        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            var message = String.format("'%s' has an invalid value '%s'", fieldError.getField(), fieldError.getDefaultMessage());
            response.add(new ErrorResponse.Error(message));
        }

        return response;
    }
}

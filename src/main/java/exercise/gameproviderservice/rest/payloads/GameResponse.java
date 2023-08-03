package exercise.gameproviderservice.rest.payloads;

import java.time.Instant;

public record GameResponse(String name, Instant dateOfCreation, Boolean active) {
}

package exercise.gameproviderservice.rest.payloads;

import java.time.Instant;
import jakarta.validation.constraints.NotBlank;

public record GameRequest(@NotBlank String name, Instant dateOfCreation, Boolean active) {
}

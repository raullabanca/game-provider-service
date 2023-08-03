package exercise.gameproviderservice.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@Data
@Entity
@NoArgsConstructor
public class GameModel {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private Instant dateOfCreation;
    private Boolean active;

    @Version
    private Integer version;

    public GameModel(String name, Instant dateOfCreation, Boolean active) {
        this.name = name;
        this.dateOfCreation = dateOfCreation;
        this.active = active;
    }
}

package exercise.gameproviderservice.repository;

import exercise.gameproviderservice.domain.GameModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<GameModel, Long> {
    Optional<GameModel> findByName(String name);
}

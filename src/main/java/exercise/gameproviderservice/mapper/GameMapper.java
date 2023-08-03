package exercise.gameproviderservice.mapper;

import exercise.gameproviderservice.domain.GameModel;
import exercise.gameproviderservice.rest.payloads.GameRequest;
import exercise.gameproviderservice.rest.payloads.GameResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface GameMapper {

    @Mapping(target = "name", source = "name")
    @Mapping(target = "dateOfCreation", source = "dateOfCreation")
    @Mapping(target = "active", source = "active")
    GameModel gameRequestToGame(GameRequest gameRequest);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "dateOfCreation", source = "dateOfCreation")
    @Mapping(target = "active", source = "active")
    GameResponse gameToGameResponse(GameModel gameModel);
}

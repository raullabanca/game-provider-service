package exercise.gameproviderservice.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import exercise.gameproviderservice.domain.exception.GameAlreadyExistsException;
import exercise.gameproviderservice.domain.exception.GameNotFoundException;
import exercise.gameproviderservice.mapper.GameMapper;
import exercise.gameproviderservice.rest.payloads.GameRequest;
import exercise.gameproviderservice.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static exercise.gameproviderservice.fixtures.GameFixtures.createGame;
import static exercise.gameproviderservice.fixtures.GameFixtures.createGameRequest;
import static exercise.gameproviderservice.fixtures.GameFixtures.createGameResponse;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = GameController.class)
class GameControllerITest {

    private static final String GAMES_BASE_PATH = "/v1/games";
    private static final String GAME_ID_BASE_PATH = "/v1/games/%s";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private GameService gameService;

    @MockBean
    private GameMapper gameMapper;

    @Test
    void getAll_noParams_thenReturns2xx() throws Exception {
        var request = createGameRequest();
        var game = createGame();
        var gameResponse = createGameResponse();

        when(gameService.getGames()).thenReturn(List.of(game));
        when(gameMapper.gameToGameResponse(game)).thenReturn(gameResponse);

        var requestBuilder = get(GAMES_BASE_PATH)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE);

        var mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status()
                        .is2xxSuccessful())
                .andReturn();

        var actualResponseAsJSON = mvcResult.getResponse().getContentAsString();
        assertThatJson(actualResponseAsJSON)
                .isArray()
                .hasSize(1)
                .anySatisfy(gameJson -> assertThatJson(gameJson).isObject()
                        .containsEntry("name", request.name())
                        .containsEntry("active", request.active())
                        .containsEntry("dateOfCreation", request.dateOfCreation().toString()));
    }

    @Test
    void getGame_gameName_thenReturns2xx() throws Exception {
        var request = createGameRequest();
        var game = createGame();
        var gameResponse = createGameResponse();

        when(gameService.getGame(request.name())).thenReturn(game);
        when(gameMapper.gameToGameResponse(game)).thenReturn(gameResponse);

        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get(GAME_ID_BASE_PATH.formatted(request.name()));
        var requestBuilder = mockHttpServletRequestBuilder
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE);

        performAndAssert(request, requestBuilder);
    }

    @Test
    void getGame_whenGameNameIsNotFound_thenReturns4xx() throws Exception {
        var request = createGameRequest();

        doThrow(new GameNotFoundException(request.name())).when(gameService).getGame(request.name());

        var requestBuilder = get(GAME_ID_BASE_PATH.formatted(request.name()))
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE);

        performAndAssertErrors("Cannot find any game with Name [%s]".formatted(request.name()), requestBuilder);
    }

    @Test
    void post_whenValidInput_thenReturns2xx() throws Exception {
        var request = createGameRequest();
        var game = createGame();
        var gameResponse = createGameResponse();

        when(gameService.saveGame(request)).thenReturn(game);
        when(gameMapper.gameToGameResponse(game)).thenReturn(gameResponse);

        var requestBuilder = post(GAMES_BASE_PATH)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString((request)));

        performAndAssert(request, requestBuilder);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badRequests")
    void post_whenInvalidInput_thenReturns4xx(String scenario, String value, String expectedMessage) throws Exception {
        var request = new GameRequest(value, Instant.now(), null);

        var requestBuilder = post(GAMES_BASE_PATH)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString((request)));

        performAndAssertErrors(expectedMessage, requestBuilder);
    }

    @Test
    void post_whenGameExists_thenReturns4xx() throws Exception {
        var request = createGameRequest();

        doThrow(new GameAlreadyExistsException(request.name())).when(gameService).updateGame(request);

        var requestBuilder = put(GAMES_BASE_PATH)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString((request)));

        performAndAssertErrors("The game [%s] already exists".formatted(request.name()), requestBuilder);
    }

    @Test
    void put_whenValidInput_thenReturns2xx() throws Exception {
        var request = createGameRequest();
        var game = createGame();
        var gameResponse = createGameResponse();

        when(gameService.updateGame(request)).thenReturn(game);
        when(gameMapper.gameToGameResponse(game)).thenReturn(gameResponse);

        var requestBuilder = put(GAMES_BASE_PATH)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(createGameBody(request));

        performAndAssert(request, requestBuilder);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("badRequests")
    void put_whenInvalidInput_thenReturns4xx(String scenario, String value, String expectedMessage) throws Exception {
        var request = new GameRequest(value, Instant.now(), null);

        var requestBuilder = put(GAMES_BASE_PATH)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString((request)));

        performAndAssertErrors(expectedMessage, requestBuilder);
    }

    @Test
    void put_whenNotFound_thenReturns4xx() throws Exception {
        var request = createGameRequest();

        doThrow(new GameNotFoundException(request.name())).when(gameService).updateGame(request);

        var requestBuilder = put(GAMES_BASE_PATH)
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString((request)));

        performAndAssertErrors("Cannot find any game with Name [%s]".formatted(request.name()), requestBuilder);
    }

    @Test
    void delete_whenValidInput_thenReturns2xx() throws Exception {
        var request = createGameRequest();

        var requestBuilder = delete(GAME_ID_BASE_PATH.formatted(request.name()))
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
                .andExpect(status()
                        .isNoContent());
    }

    @Test
    void delete_whenGameNameIsNotFound_thenReturns4xx() throws Exception {
        var request = createGameRequest();

        doThrow(new GameNotFoundException(request.name())).when(gameService).deleteGame(request.name());

        var requestBuilder = delete(GAME_ID_BASE_PATH.formatted(request.name()))
                .accept(APPLICATION_JSON_VALUE)
                .contentType(APPLICATION_JSON_VALUE);

        performAndAssertErrors("Cannot find any game with Name [%s]".formatted(request.name()), requestBuilder);
    }

    private static String createGameBody(GameRequest gameRequest) {
        return """
                {
                	"name": "%s",
                	"dateOfCreation": "%s",
                	"active": %b
                }
                 """.formatted(gameRequest.name(), gameRequest.dateOfCreation(), gameRequest.active());
    }

    private static Stream<Arguments> badRequests() {
        return Stream.of(
                Arguments.of("Null name", null, "'name' has an invalid value 'must not be blank'"),
                Arguments.of("Empty name", "", "'name' has an invalid value 'must not be blank'")
        );
    }

    private void performAndAssert(GameRequest request, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        var mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status()
                        .is2xxSuccessful())
                .andReturn();

        var actualResponseAsJSON = mvcResult.getResponse().getContentAsString();
        assertThatJson(actualResponseAsJSON).isObject()
                .containsEntry("name", request.name())
                .containsEntry("active", request.active())
                .containsEntry("dateOfCreation", request.dateOfCreation().toString());
    }

    private void performAndAssertErrors(String expectedMessage, MockHttpServletRequestBuilder requestBuilder) throws Exception {
        var mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status()
                        .is4xxClientError())
                .andReturn();

        var actualResponseAsJSON = mvcResult.getResponse().getContentAsString();
        assertThatJson(actualResponseAsJSON)
                .isObject()
                .hasEntrySatisfying("errors", violations -> assertThatJson(violations)
                        .isArray()
                        .hasSize(1)
                        .extracting("errorMessage")
                        .asString()
                        .contains(expectedMessage));
    }

}


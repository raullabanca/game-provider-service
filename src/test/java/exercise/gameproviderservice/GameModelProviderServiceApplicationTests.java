package exercise.gameproviderservice;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@Execution(ExecutionMode.CONCURRENT)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class GameModelProviderServiceApplicationTests {

    private static final String NAME = UUID.randomUUID().toString();
    private static final boolean ACTIVE = false;
    private static final Instant DATE_OF_CREATION = Instant.now();
    private static final String GAME_BODY = createGameBody(NAME, DATE_OF_CREATION, ACTIVE);
    private static final String NAME_KEY = "name";
    private static final String ACTIVE_KEY = "active";
    private static final String DATE_OF_CREATION_KEY = "dateOfCreation";
    private static final String GAMES_BASE_PATH = "/v1/games";
    private static final String GAME_ID_BASE_PATH = "/v1/games/{gameName}";
    private static final String GAME_NAME_PARAM = "gameName";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void applicationTest_allCrudOperations_success() {
        createAndAssertGame(NAME, DATE_OF_CREATION, ACTIVE);

        var actual = getAllGames();

        assertThatJson(actual).isArray()
                .hasSize(1)
                .anySatisfy(game -> assertThatJson(game).isEqualTo(GAME_BODY));

        getGameAndAssert(NAME);

        updateGameAndAssert(NAME, DATE_OF_CREATION);
        updateGameAndAssert(NAME, DATE_OF_CREATION.plus(1, ChronoUnit.DAYS));
        updateGameAndAssert(NAME, DATE_OF_CREATION.plus(10, ChronoUnit.DAYS));

        deleteAndAssert(NAME);
    }

    private void deleteAndAssert(String gameName) {
        given()
                .log().all()
                .basePath(GAME_ID_BASE_PATH)
                .pathParam(GAME_NAME_PARAM, gameName)
                .contentType(JSON)
                .delete()
                .then()
                .assertThat()
                .statusCode(204)
                .extract()
                .body()
                .asString();

        var actual = getAllGames();

        assertThatJson(actual).isArray()
                .hasSize(0);
    }

    private void updateGameAndAssert(String gameName, Instant dateOfCreation) {
        var updateGameRequest = createGameBody(gameName, dateOfCreation, true);
        var actual = given()
                .log().all()
                .basePath(GAMES_BASE_PATH)
                .contentType(JSON)
                .body(updateGameRequest)
                .put()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertThatJson(actual).isObject()
                .containsEntry(NAME_KEY, gameName)
                .containsEntry(ACTIVE_KEY, true)
                .containsEntry(DATE_OF_CREATION_KEY, dateOfCreation.toString());

        actual = getAllGames();

        assertThatJson(actual).isArray()
                .hasSize(1)
                .anySatisfy(game ->
                        assertThatJson(game).isEqualTo(updateGameRequest));
    }

    private void getGameAndAssert(String gameName) {

        var actual = given()
                .log().all()
                .basePath(GAME_ID_BASE_PATH)
                .pathParam(GAME_NAME_PARAM, gameName)
                .contentType(JSON)
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertThatJson(actual).isEqualTo(GAME_BODY);
    }

    private String getAllGames() {
        return given()
                .log().all()
                .basePath(GAMES_BASE_PATH)
                .contentType(JSON)
                .get()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .asString();
    }

    private void createAndAssertGame(String gameName, Instant dateOfCreation, boolean active) {
        var actual = given()
                .log().all()
                .basePath(GAMES_BASE_PATH)
                .contentType(JSON)
                .body(GAME_BODY)
                .post()
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .body()
                .asString();

        assertThatJson(actual).isObject()
                .containsEntry(NAME_KEY, gameName)
                .containsEntry(ACTIVE_KEY, active)
                .containsEntry(DATE_OF_CREATION_KEY, dateOfCreation.toString());
    }

    private static String createGameBody(String name, Instant dateOfCreation, boolean active) {
        return """
                {
                	"name": "%s",
                	"dateOfCreation": "%s",
                	"active": %b
                }
                 """.formatted(name, dateOfCreation.toString(), active);
    }
}

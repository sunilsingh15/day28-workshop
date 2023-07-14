package sg.edu.nus.iss.day28workshop.controller;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.day28workshop.service.GameService;

@RestController
@RequestMapping
public class GameController {

    @Autowired
    private GameService service;

    @GetMapping(path = "/game/{id}/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> viewGameAndCommentsByID(@PathVariable String id) {

        Integer gameID = 0;

        try {
            gameID = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "Game ID is invalid. It must be a whole number.")
                    .build();

            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (!service.checkIfGameExistsByID(gameID)) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "No game found with ID: " + gameID)
                    .build();

            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<String>(service.getGameWithCommentsByID(gameID), HttpStatus.OK);
    }

    @GetMapping(path = "/games/{ranking}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGamesByRanking(@PathVariable String ranking) {

        if (ranking.equals("highest")) {

            List<Document> game = service.getGamesWithHighestRatedComment();

            JsonArrayBuilder gamesArray = Json.createArrayBuilder();

            for (Document g : game) {
                JsonObject gameToAdd = Json.createObjectBuilder()
                        .add("_id", g.getInteger("gid"))
                        .add("name", g.getString("name"))
                        .add("rating", g.getInteger("rating"))
                        .add("user", g.getString("user"))
                        .add("comment", g.getString("comment"))
                        .add("review_id", g.getString("c_id"))
                        .build();

                gamesArray.add(gameToAdd);
            }

            JsonObject highestJson = Json.createObjectBuilder()
                    .add("rating", "highest")
                    .add("games", gamesArray.build())
                    .add("timestamp", new Date().toString())
                    .build();

            return new ResponseEntity<String>(highestJson.toString(), HttpStatus.OK);

        } else if (ranking.equals("lowest")) {

            List<Document> game = service.getGamesWithLowestRatedComment();

            JsonArrayBuilder gamesArray = Json.createArrayBuilder();

            for (Document g : game) {
                JsonObject gameToAdd = Json.createObjectBuilder()
                        .add("_id", g.getInteger("gid"))
                        .add("name", g.getString("name"))
                        .add("rating", g.getInteger("rating"))
                        .add("user", g.getString("user"))
                        .add("comment", g.getString("comment"))
                        .add("review_id", g.getString("c_id"))
                        .build();

                gamesArray.add(gameToAdd);
            }

            JsonObject lowestJson = Json.createObjectBuilder()
                    .add("rating", "lowest")
                    .add("games", gamesArray.build())
                    .add("timestamp", new Date().toString())
                    .build();

            return new ResponseEntity<String>(lowestJson.toString(), HttpStatus.OK);

        } else {

            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "Invalid input. Only 'highest' and 'lowest' are supported.")
                    .build();

            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);

        }

    }

}

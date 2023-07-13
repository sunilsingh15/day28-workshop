package sg.edu.nus.iss.day28workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
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

}

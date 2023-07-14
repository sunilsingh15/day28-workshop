package sg.edu.nus.iss.day28workshop.service;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.day28workshop.repository.GameRepository;

@Service
public class GameService {

    @Autowired
    private GameRepository repository;

    public Boolean checkIfGameExistsByID(Integer gameID) {
        return repository.checkIfGameExistsByID(gameID);
    }

    public String getGameWithCommentsByID(Integer gameID) {
        Document game = repository.getGameByID(gameID);
        List<Document> comments = repository.getCommentsForGameByID(gameID);

        JsonObject gameJson = Json.createObjectBuilder()
                .add("game_id", game.getInteger("gid"))
                .add("name", game.getString("name"))
                .add("year", game.getInteger("year"))
                .add("rank", game.getInteger("ranking"))
                .add("average", calculateAverageRating(comments))
                .add("users_rated", game.getInteger("users_rated"))
                .add("url", game.getString("url"))
                .add("thumbnail", game.getString("image"))
                .add("reviews", createArrayFromComments(comments))
                .add("timestamp", new Date().toString())
                .build();

        return gameJson.toString();

    }

    public Double calculateAverageRating(List<Document> comments) {
        Integer sum = 0;

        for (Document document : comments) {
            Integer rating = document.getInteger("rating");
            sum += rating;
        }

        return (double) sum / comments.size();
    }

    public JsonArray createArrayFromComments(List<Document> comments) {
        JsonArrayBuilder commentsArray = Json.createArrayBuilder();

        for (Document document : comments) {
            commentsArray.add("/review/" + document.getString("c_id"));
        }

        return commentsArray.build();
    }

    public List<Document> getGamesWithHighestRatedComment() {
        return repository.getGamesWithHighestRatedComment();
    }

    public List<Document> getGamesWithLowestRatedComment() {
        return repository.getGamesWithLowestRatedComment();
    }

}

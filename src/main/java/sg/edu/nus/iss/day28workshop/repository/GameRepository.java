package sg.edu.nus.iss.day28workshop.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class GameRepository {

    @Autowired
    private MongoTemplate template;

    public Boolean checkIfGameExistsByID(Integer gameID) {
        return template.exists(Query.query(Criteria.where("gid").is(gameID)), "games");
    }

    public Document getGameByID(Integer gameID) {
        return template.findOne(Query.query(Criteria.where("gid").is(gameID)), Document.class, "games");
    }

    public List<Document> getCommentsForGameByID(Integer gameID) {
        return template.find(Query.query(Criteria.where("gid").is(gameID)), Document.class, "comments");
    }
    
}

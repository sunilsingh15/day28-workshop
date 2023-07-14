package sg.edu.nus.iss.day28workshop.repository;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
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

    public Document getGameWithCommentsByID(Integer gameID) {
        MatchOperation match = Aggregation.match(Criteria.where("gid").is(gameID));
        LookupOperation lookup = Aggregation.lookup("comments", "gid", "gid", "reviews");

        Aggregation pipeline = Aggregation.newAggregation(match, lookup);

        return template.aggregate(pipeline, "games", Document.class).getMappedResults().get(0);
    }

    public List<Document> getGamesWithLimit() {
        return template.find(Query.query(Criteria.where("")).limit(10), Document.class, "games");
    }

    public List<Document> getGamesWithHighestRatedComment() {
        LimitOperation limit = Aggregation.limit(10);
        LookupOperation lookup = Aggregation.lookup("comments", "gid", "gid", "comment");
        UnwindOperation unwind = Aggregation.unwind("comment");
        SortOperation sort = Aggregation.sort(Sort.Direction.DESC, "comment.rating");
        GroupOperation group = Aggregation.group("gid")
                .first("gid").as("gid")
                .first("name").as("name")
                .first("comment.rating").as("rating")
                .first("comment.user").as("user")
                .first("comment.c_text").as("comment")
                .first("comment.c_id").as("c_id");
        SortOperation sortByID = Aggregation.sort(Sort.Direction.ASC, "gid");
        ProjectionOperation project = Aggregation.project("gid", "name", "rating", "user", "comment", "c_id")
                .andExclude("_id");

        Aggregation pipeline = Aggregation.newAggregation(limit, lookup, unwind, sort, group, sortByID, project);

        return template.aggregate(pipeline, "games", Document.class).getMappedResults();
    }

    public List<Document> getGamesWithLowestRatedComment() {
        LimitOperation limit = Aggregation.limit(10);
        LookupOperation lookup = Aggregation.lookup("comments", "gid", "gid", "comment");
        UnwindOperation unwind = Aggregation.unwind("comment");
        SortOperation sort = Aggregation.sort(Sort.Direction.ASC, "comment.rating");
        GroupOperation group = Aggregation.group("gid")
                .first("gid").as("gid")
                .first("name").as("name")
                .first("comment.rating").as("rating")
                .first("comment.user").as("user")
                .first("comment.c_text").as("comment")
                .first("comment.c_id").as("c_id");
        SortOperation sortByID = Aggregation.sort(Sort.Direction.ASC, "gid");
        ProjectionOperation project = Aggregation.project("gid", "name", "rating", "user", "comment", "c_id")
                .andExclude("_id");

        Aggregation pipeline = Aggregation.newAggregation(limit, lookup, unwind, sort, group, sortByID, project);

        return template.aggregate(pipeline, "games", Document.class).getMappedResults();
    }

}

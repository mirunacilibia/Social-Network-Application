package repository.file;

import domain.Friendship;
import domain.Status;
import domain.User;
import domain.validators.FriendshipValidator;
import domain.validators.Validator;
import utils.Tuple;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class FriendshipRepository extends AbstractFileRepository<Tuple<String, String>, Friendship>{

    /**
     * Constructor
     * @param validator - the validator used for validating the data
     * @param fileName - the file where we store the data
     */
    public FriendshipRepository(Validator<Friendship> validator, String fileName){
        super(validator, fileName);
    }

    /**
     * Function that creates an entity from a list of attributes
     * @param attributes - list of strings, the entity's attributes
     * @return friendship - a Friendship entity with the attributes of the list
     */
    @Override
    public Friendship extractEntity(List<String> attributes) {
        if(attributes.size() != 4)
            throw new IllegalArgumentException("There is incorrect data in the file!\n");
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(attributes.get(0), attributes.get(1)));
        friendship.setStatus(Status.valueOf(attributes.get(2)));
        return friendship;
    }

    /**
     * Function that creates a string that contains an entity's attributes
     * @param friendship - the entity from where we take the data
     * @return a string that contains the attributes
     */
    @Override
    protected String createEntityAsString(Friendship friendship) {
        return friendship.getId().first + ";" + friendship.getId().second + ";" + friendship.getStatus().toString();
    }
}
package repository.file;

import domain.Entity;
import domain.User;
import domain.validators.Validator;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

public class UserRepository extends AbstractFileRepository<String, User>{

    /**
     * Constructor
     * @param validator - the validator used for validating the data
     * @param fileName - the file where we store the data
     */
    public UserRepository(Validator<User> validator, String fileName){
        super(validator, fileName);
    }

    /**
     * Function that creates an entity from a list of attributes
     * @param attributes - list of strings, the entity's attributes
     * @return user - a User entity with the attributes of the list
     */
    @Override
    public User extractEntity(List<String> attributes) {
        if(attributes.size() != 6)
            throw new IllegalArgumentException("There is incorrect data in the file!\n");
        User user = new User(attributes.get(1), attributes.get(2), attributes.get(3), attributes.get(4), attributes.get(5));
        user.setId(attributes.get(0));
        return user;
    }

    /**
     * Function that creates a string that contains an entity's attributes
     * @param entity - the entity from where we take the data
     * @return a string that contains the attributes
     */
    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId().toString() + ";" + entity.getFirstName() + ";" + entity.getLastName()
               + ";" + entity.getPhoneNumber() + ";" + entity.getTimestamp().toString() + ";" + entity.getPassword();
    }
}

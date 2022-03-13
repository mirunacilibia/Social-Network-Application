package repository.database;

import domain.User;
import domain.validators.ValidationException;
import domain.validators.Validator;

import java.sql.*;

public class UserDBRepository extends AbstractDBRepository<String, User> {

    /**
     * Constructor
     * @param url - the data required for connecting to the database
     * @param username - the data required for connecting to the database
     * @param password - the data required for connecting to the database
     * @param validator - the validator of the data
     */
    public UserDBRepository(String url, String username, String password, Validator<User> validator, String database) {
        super(url, username, password, validator, database);
    }

    /**
     * Function that sets in the sql statement the id of the entity
     * @param ps - the statement
     * @param number - the position the id takes in the statement
     * @param s - the id
     * @throws SQLException - if the command can't be executed
     */
    @Override
    protected void setID(PreparedStatement ps, int number, String s) throws SQLException {
        ps.setString(number, s);
    }

    /**
     * Function that creates a User from a resultSet
     * @param resultSet - the Set from where we get the data of the User
     * @return - the User with the given data
     * @throws SQLException - if the User could not be created
     */
    protected User getEntity(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("email");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String phoneNumber = resultSet.getString("phone_number");
        String timestamp = resultSet.getString("timestamp");
        String password = resultSet.getString("password");
        String path = resultSet.getString("path");

        User user = new User(firstName, lastName, phoneNumber, timestamp, password);
        user.setId(id);
        user.setPathToPicture(path);
        return user;
    }

    /**
     * Function that sets the data for running an sql command
     * @param entity - the entity from which we take the data
     * @param ps - the statement that we want to set
     * @throws SQLException - if the statement could not be updated
     */
    protected void setSqlCommand(User entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getId());
        ps.setString(2, entity.getFirstName());
        ps.setString(3, entity.getLastName());
        ps.setString(4, entity.getPhoneNumber());
        ps.setString(5, entity.getTimestamp());
        ps.setString(6, entity.getPassword());
        ps.setString(7, entity.getPathToPicture());
    }

    /**
     * @param string -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     */
    @Override
    public User findOne(String string) {
        String sql = "SELECT * from users where email = '" + string + "'";
        return findOneAbstract(sql);
    }

    /**
     * @return all entities
     */
    @Override
    public Iterable<User> findAll() {
        String sql = "SELECT * from users";
        return findAllAbstract(sql);
    }


    public Iterable<User> findAll(int offset, int number) {
        String sql = "SELECT * from users" +
                " ORDER BY email" +
                " OFFSET " + offset + " ROWS FETCH NEXT " + number + " ROWS ONLY";
        return findAllAbstract(sql);
    }

    /**
     * @param entity
     *         entity must be not null
     * @return null- if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws ValidationException
     *            if the entity is not valid
     */
    @Override
    public User save(User entity) {
        String sql = "insert into users (email, first_name, last_name, phone_number, timestamp, password, path)" +
                " values (?, ?, ?, ?, ?, ?, ?)";
        return saveAbstract(sql, entity);
    }

    /**
     *  removes the entity with the specified id
     * @param string
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     */
    @Override
    public User delete(String string) {
        String sql = "delete from users " +
                     "where email = ? ";
        return deleteAbstract(sql, string, 1);
    }

    /**
     * @param entity
     *          entity must not be null
     * @return null - if the entity is updated,
     *                otherwise  returns the entity  - (e.g id does not exist).
     * @throws ValidationException
     *             if the entity is not valid.
     */
    @Override
    public User update(User entity) {
        String sql = "update users " +
                "set email = ?, " +
                "first_name = ?, " +
                "last_name = ?, " +
                "phone_number = ?, " +
                "timestamp = ?, " +
                "password = ?, " +
                "path = ?" +
                "where email = ?";
        return updateAbstract(sql, entity, 8);
    }
}


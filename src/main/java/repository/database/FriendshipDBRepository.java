package repository.database;

import domain.Friendship;
import domain.Status;
import domain.validators.ValidationException;
import domain.validators.Validator;
import utils.Tuple;

import java.sql.*;
import java.time.LocalDate;

public class FriendshipDBRepository extends AbstractDBRepository<Tuple<String, String>, Friendship> {

    /**
     * Constructor
     * @param url - the data required for connecting to the database
     * @param username - the data required for connecting to the database
     * @param password - the data required for connecting to the database
     * @param validator - the validator of the data
     */
    public FriendshipDBRepository(String url, String username, String password, Validator<Friendship> validator, String database) {
        super(url, username, password, validator, database);
    }


    /**
     * Function that sets in the sql statement the id of the entity
     * @param ps - the statement
     * @param number - the position the id takes in the statement
     * @param stringStringTuple - the id
     * @throws SQLException - if the command can't be executed
     */
    @Override
    protected void setID(PreparedStatement ps, int number, Tuple<String, String> stringStringTuple) throws SQLException {
        ps.setString(number, stringStringTuple.first);
        ps.setString(number + 1, stringStringTuple.second);
    }

    /**
     * Function that creates a Friendship from a resultSet
     * @param resultSet - the Set from where we get the data of the Friendship
     * @return - the Friendship with the given data
     * @throws SQLException - if the Friendship could not be created
     */
    protected Friendship getEntity(ResultSet resultSet) throws SQLException {
        String email1 = resultSet.getString("first_email");
        String email2 = resultSet.getString("second_email");
        String status = resultSet.getString("status");
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(email1, email2));
        friendship.setStatus(Status.valueOf(status));
        String date_string = resultSet.getString("date");
        if(date_string != null) {
            LocalDate date = LocalDate.parse(date_string);
            friendship.setDate(date);
        }
        return friendship;
    }

    /**
     * Function that sets the data for running an sql command
     * @param entity - the entity from which we take the data
     * @param ps - the statement that we want to set
     * @throws SQLException - if the statement could not be updated
     */
    protected void setSqlCommand(Friendship entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getId().first);
        ps.setString(2, entity.getId().second);
        ps.setString(3, entity.getStatus().toString());
        if(entity.getDate() != null)
            ps.setDate(4, Date.valueOf(entity.getDate()));
        else
            ps.setNull(4, Types.DATE);
    }

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     */
    @Override
    public Friendship findOne(Tuple<String, String> id) {
        String sql = "SELECT * from friendships" +
                " where first_email = '" + id.first + "' and second_email = '" + id.second + "'";
        return findOneAbstract(sql);
    }

    /**
     * @return all entities
     */
    @Override
    public Iterable<Friendship> findAll() {
        String sql = "SELECT * from friendships";
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
    public Friendship save(Friendship entity) {
        if(findOne(entity.getId()) != null)
            return entity;
        if(findOne(new Tuple<>(entity.getId().second, entity.getId().first)) != null)
            return entity;
        String sql = "insert into friendships (first_email, second_email, status, date)" +
                " values (?, ?, ?, ?)";
        return saveAbstract(sql, entity);
    }


    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     */
    @Override
    public Friendship delete(Tuple<String, String> id) {
        Friendship friendship = findOne(id);
        if(friendship == null)
            friendship = findOne(new Tuple<>(id.second, id.first));
        if(friendship == null)
            return null;
        String sql = "delete from friendships " +
                "where first_email = ? and second_email = ?";
        return deleteAbstract(sql, friendship.getId(), 1);
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
    public Friendship update(Friendship entity) {

        String sql = "update friendships " +
                "set first_email = ?, " +
                "second_email = ?, " +
                "status = ?," +
                "date = ? " +
                "where first_email = ? and second_email = ?";
        return updateAbstract(sql, entity, 5);
    }
}


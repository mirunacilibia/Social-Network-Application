package repository.database;

import domain.Message;
import domain.User;
import domain.validators.ValidationException;
import domain.validators.Validator;
import utils.Tuple;
//import org.postgresql.util.PSQLException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;


public class MessageDBRepository extends AbstractDBRepository<Integer, Message> {

    HashMap<String, User> users;
    List<Tuple<String, String>> receivers;

    /**
     * Constructor
     * @param url - the data required for connecting to the database
     * @param username - the data required for connecting to the database
     * @param password - the data required for connecting to the database
     * @param validator - the validator of the data
     */
    public MessageDBRepository(String url, String username, String password, Validator<Message> validator, String database) {
        super(url, username, password, validator, database);
    }

    @Override
    protected void initializeRepo() {
        super.initializeRepo();
        setUsers();
        setReceivers();
    }

    private void setUsers(){
        String sql = "SELECT * from users";
        Set<User> entities = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                User entity = createUser(resultSet);
                entities.add(entity);
            }
            connection.close();
            users = new HashMap<>();
            entities.forEach(x -> users.put(x.getId(), x));
        } catch (SQLException e) {
            e.printStackTrace();
            users = new HashMap<>();
        }
    }

    private void setReceivers(){
        String sql = "SELECT * from receivers";
        List<Tuple<String, String>> emails = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                emails.add(new Tuple<>(resultSet.getString("id_message"), resultSet.getString("id_user")));
            }
            connection.close();
            receivers = emails;
        } catch (SQLException e) {
            e.printStackTrace();
            receivers = emails;
        }
    }

    /**
     * Function that sets in the sql statement the id of the entity
     * @param ps - the statement
     * @param number - the position the id takes in the statement
     * @param integer - the id
     * @throws SQLException - if the command can't be executed
     */
    @Override
    protected void setID(PreparedStatement ps, int number, Integer integer) throws SQLException {
        ps.setInt(number, integer);
    }

    /**
     * Function that creates a User from a given id
     * @param resultSet - the Set from where we get the data of the Message
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     */
    private User createUser(ResultSet resultSet) throws SQLException {
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
     * Function that creates a Message from a resultSet
     * @param resultSet - the Set from where we get the data of the Message
     * @return - the Message with the given data
     * @throws SQLException - if the Message could not be created
     */
    @Override
    protected Message getEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String from = resultSet.getString("from");
        String text = resultSet.getString("message");
        LocalDateTime date = resultSet.getTimestamp("date").toLocalDateTime();

        User userFrom = users.get(from);
        List<User> usersTo = new ArrayList<>();
        receivers.forEach(x -> {
            if(Integer.parseInt(x.first) == id){
                usersTo.add(users.get(x.second));
            }
        });

        String replyMessageID = resultSet.getString("reply_message");
        Message replyMessage;
        if(replyMessageID != null){
            replyMessage = findOne(Integer.parseInt(replyMessageID));
        } else
            replyMessage = null;

        Message message = new Message(userFrom, usersTo, text, date, replyMessage);
        message.setId(id);
        message.setReplyMessage(replyMessage);
        return message;
    }

    /**
     * Function that sets the data for running an sql command
     * @param entity - the entity from which we take the data
     * @param ps - the statement that we want to set
     * @throws SQLException - if the statement could not be updated
     */
    @Override
    protected void setSqlCommand(Message entity, PreparedStatement ps) throws SQLException {
        ps.setString(1, entity.getFrom().getId());
        ps.setString(2, entity.getMessage());
        ps.setTimestamp(3, Timestamp.valueOf(entity.getDate()));
        if(entity.getReplyMessage() == null)
            ps.setNull(4, Types.INTEGER);
        else
            ps.setInt(4, entity.getReplyMessage().getId());
    }

    /**
     * @param integer -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     */
    @Override
    public Message findOne(Integer integer) {
        setUsers();
        setReceivers();
        String sql = "SELECT * from messages where id = " + integer;
        return findOneAbstract(sql);
    }

    /**
     * @return all entities
     */
    @Override
    public Iterable<Message> findAll() {
        setUsers();
        setReceivers();
        String sql = "SELECT * from messages";
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
    public Message save(Message entity) {
        String sql =    "insert into messages (\"from\", message, date, reply_message)" +
                " values (?, ?, ?, ?)";
        if(saveAbstract(sql, entity) != null)
            return entity;

        String sqlReceivers = "insert into receivers (id_message, id_user) values ";
        int numberOfReceivers = entity.getTo().size();
        sqlReceivers += "(?, ?), ".repeat(numberOfReceivers-1);
        sqlReceivers += "(?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sqlReceivers)) {
            int i = 1;
            int id = getMessageID(entity);
            for(User user: entity.getTo()){
                statement.setInt(i, id);
                statement.setString(i + 1, user.getId());
                i += 2;
            }
            statement.executeUpdate();
            connection.close();
       // }catch (PSQLException e){
     //       return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function that returns the ID of a Message
     * @param entity - the Message that we want the id of
     * @return - integer, the id
     */
    private int getMessageID(Message entity){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select id from messages " +
                "where \"from\" = '" + entity.getFrom().getId() +
                "' and message = '" + entity.getMessage() +
                "' and date = '" + entity.getDate().toString() + "'");
             ResultSet resultSet = statement.executeQuery()) {
            if(!resultSet.next()){
                connection.close();
                return -1;
            }
            else {
                connection.close();
                int id  = resultSet.getInt("id");
                entity.setId(id);
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     *  removes the entity with the specified id
     * @param integer
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     */
    @Override
    public Message delete(Integer integer) {
        String sql =    "delete from messages " +
                        "where id = ? ";
        return deleteAbstract(sql, integer, 1);
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
    public Message update(Message entity) {
        String sql =    "update users " +
                        "set \"from\" = ?, " +
                        "message = ?, " +
                        "date = ?, " +
                        "reply_message = ? " +
                        "where id = ?";
        return updateAbstract(sql, entity, 5);
    }
}
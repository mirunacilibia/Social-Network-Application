package repository.database;

import domain.Event;
import domain.Message;
import domain.User;
import domain.validators.ValidationException;
import domain.validators.Validator;
import utils.Tuple;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public class EventsDBRepository extends AbstractDBRepository<Integer, Event> {

    HashMap<String, User> users;

    /**
     * Constructor
     *
     * @param url       - the data required for connecting to the database
     * @param username  - the data required for connecting to the database
     * @param password  - the data required for connecting to the database
     * @param validator - the validator of the data
     */
    public EventsDBRepository(String url, String username, String password, Validator<Event> validator, String database) {
        super(url, username, password, validator, database);
    }

    @Override
    protected void initializeRepo() {
        super.initializeRepo();
        setUsers();
    }

    private void setUsers() {
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

    public List<Tuple<User, Tuple<Event, Boolean>>> getParticipantsToEvent() {
        setUsers();
        HashMap<Integer, Event> list = new HashMap<>();
        for (Event event : findAll())
            list.put(event.getId(), event);
        String sql = "SELECT * from participants";
        List<Tuple<User, Tuple<Event, Boolean>>> participants = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                participants.add(new Tuple<User, Tuple<Event, Boolean>>(users.get(resultSet.getString("id_user")), new Tuple<>(list.get(resultSet.getInt("id_event")), resultSet.getBoolean("is_notified"))));
            }
            connection.close();
            return participants;
        } catch (SQLException e) {
            e.printStackTrace();
            return participants;
        }
    }

    public Tuple<String, Tuple<Integer, Boolean>> getParticipant(int idEvent, String emailUser) {
        setUsers();
        String sql = "SELECT * from participants where id_event = " + idEvent + " and id_user = '" + emailUser + "'";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                connection.close();
                return null;
            }
            return new Tuple<>(resultSet.getString("id_user"), new Tuple<>(resultSet.getInt("id_event"), resultSet.getBoolean("is_notified")));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function that creates a User from a given id
     *
     * @param resultSet - the Set from where we get the data of the Message
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
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
     * @param integer -the id of the entity to be returned
     *                id must not be null
     * @return the entity with the specified id
     * or null - if there is no entity with the given id
     */
    @Override
    public Event findOne(Integer integer) {
        setUsers();
        String sql = "SELECT * from events where id = " + integer;
        return findOneAbstract(sql);
    }

    /**
     * @return all entities
     */
    @Override
    public Iterable<Event> findAll() {
        setUsers();
        String sql = "SELECT * from events";
        return findAllAbstract(sql);
    }

    public Iterable<Event> findAll(int from, int to) {
        setUsers();
        String sql = "SELECT * from events"+
                " OFFSET " + from + " ROWS FETCH NEXT " + to + " ROWS ONLY";
        return findAllAbstract(sql);
    }

    /**
     * @param entity entity must be not null
     * @return null- if the given entity is saved
     * otherwise returns the entity (id already exists)
     * @throws ValidationException if the entity is not valid
     */
    @Override
    public Event save(Event entity) {
        String sql = "insert into events (id, creator, description, start_date, end_date, location, path_to_picture, name)" +
                " values (?, ?, ?, ?, ?, ?, ?, ?)";
        return saveAbstract(sql, entity);
    }

    public User saveParticipant(int idEvent, String emailUser, boolean isNotified) {
        setUsers();
        String sql = "insert into participants (id_event, id_user, is_notified)" +
                " values (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEvent);
            statement.setString(2, emailUser);
            statement.setBoolean(3, isNotified);
            statement.executeUpdate();
            connection.close();
        } catch (SQLException e) {
            return users.get(emailUser);
        }
        return null;
    }

    /**
     * removes the entity with the specified id
     *
     * @param integer id must be not null
     * @return the removed entity or null if there is no entity with the given id
     */
    @Override
    public Event delete(Integer integer) {
        String sql = "delete from events " +
                "where id = ? ";
        return deleteAbstract(sql, integer, 1);
    }

    public User deleteParticipant(int idEvent, String emailUser) {
        setUsers();
        String sql = "delete from participants " +
                "where id_event = ? and id_user = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEvent);
            statement.setString(2, emailUser);
            int successful = statement.executeUpdate();
            if (successful == 0) {
                connection.close();
                return null;
            }
            connection.close();
            return users.get(emailUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     * otherwise  returns the entity  - (e.g id does not exist).
     * @throws ValidationException if the entity is not valid.
     */
    @Override
    public Event update(Event entity) {
        String sql = "update events " +
                "set id = ?, " +
                "creator = ?, " +
                "description = ?, " +
                "start_date = ?, " +
                "end_date = ?, " +
                "location = ?, " +
                "path_to_picture = ?, " +
                "name = ? " +
                "where id = ?";
        return updateAbstract(sql, entity, 9);
    }

    public User updateParticipant(int idEvent, String emailUser, boolean isNotified) {
        setUsers();
        String sql = "update participants " +
                "set id_event = ?, id_user = ?, is_notified = ? where id_event = ? and id_user = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, idEvent);
            statement.setString(2, emailUser);
            statement.setBoolean(3, isNotified);
            statement.setInt(4, idEvent);
            statement.setString(5, emailUser);
            int successful = statement.executeUpdate();
            if (successful == 0) {
                connection.close();
                return null;
            }
            connection.close();
            return users.get(emailUser);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function that sets in the sql statement the id of the entity
     *
     * @param ps      - the statement
     * @param number  - the position the id takes in the statement
     * @param integer - the id
     * @throws SQLException - if the command can't be executed
     */
    @Override
    protected void setID(PreparedStatement ps, int number, Integer integer) throws SQLException {
        ps.setInt(number, integer);
    }

    /**
     * Function that creates an Event from a resultSet
     *
     * @param resultSet - the Set from where we get the data of the Message
     * @return - the Message with the given data
     * @throws SQLException - if the Message could not be created
     */
    @Override
    protected Event getEntity(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String creator = resultSet.getString("creator");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate startDate = resultSet.getDate("start_date").toLocalDate();
        LocalDate endDate = resultSet.getDate("end_date").toLocalDate();
        String location = resultSet.getString("location");
        String path = resultSet.getString("path_to_picture");

        User user = users.get(creator);
        Event event = new Event(user, name, description, startDate, endDate, location);
        event.setId(id);
        event.setPathToPicture(path);

        return event;
    }


    /**
     * Function that sets the data for running an sql command
     *
     * @param entity - the entity from which we take the data
     * @param ps     - the statement that we want to set
     * @throws SQLException - if the statement could not be updated
     */
    @Override
    protected void setSqlCommand(Event entity, PreparedStatement ps) throws SQLException {
        ps.setInt(1, entity.getId());
        ps.setString(2, entity.getCreator().getId());
        ps.setString(3, entity.getDescription());
        ps.setDate(4, Date.valueOf(entity.getStartDate()));
        ps.setDate(5, Date.valueOf(entity.getEndDate()));
        ps.setString(6, entity.getLocation());
        ps.setString(7, entity.getPathToPicture());
        ps.setString(8, entity.getName());
    }

    /**
     * Function that returns the ID of an Event
     *
     * @return - integer, the id
     */
    public int getLastEventID() {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select max(id) from events");
             ResultSet resultSet = statement.executeQuery()) {
            if (!resultSet.next()) {
                connection.close();
                return -1;
            } else {
                connection.close();
                int id = resultSet.getInt("max");
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

}

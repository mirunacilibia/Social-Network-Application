package repository.database;
import domain.Entity;
import domain.validators.ValidationException;
import domain.validators.Validator;
import repository.Repository;
//import org.postgresql.util.PSQLException;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
/**
 * Abstract class - because otherwise there would be a lot of duplicated code
 * @param <ID> - the id of the entity
 * @param <E> - the entity
 */
public abstract class AbstractDBRepository<ID, E extends Entity<ID>> implements Repository<ID, E> {
    protected String url;
    protected String username;
    protected String password;
    protected Validator<E> validator;
    protected int offset = 0;

    public AbstractDBRepository(String url, String username, String password, Validator<E> validator, String database) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
        initializeRepo();
        validateData(database);
    }

    protected void initializeRepo() {
    }

    /**
     * Function that validates the data already existing in the database
     */
    protected void validateData(String database){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * from " + database);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                E entity = getEntity(resultSet);
                try {
                    validator.validate(entity);
                } catch (ValidationException e){
                    this.delete(entity.getId());
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that executes the part of the findOne function that is the same in all classes
     * @param sql - the query that needs to be executed
     * @return  - the entity found
     *          - null, if there is no entity with the id specified in the sql query
     */
    protected E findOneAbstract(String sql){
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            if(!resultSet.next()){
                connection.close();
                return null;
            }
            E entity = getEntity(resultSet);
            connection.close();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function that executes the part of the findAll function that is the same in all classes
     * @param sql - the query that needs to be executed
     * @return  - all the entities in the database
     */
    protected Iterable<E> findAllAbstract(String sql){
        Set<E> entities = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
             ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                E entity = getEntity(resultSet);
                entities.add(entity);
            }
            connection.close();
            return entities;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }
    /**
     * Function that executes the part of the findAll function that is the same in all classes
     * @param sql - the query that needs to be executed
     * @return  - all the entities in the database
     */
    protected Iterable<E> findAllAbstract(String sql,int offset,int number){
        Set<E> entities = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)){
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                E entity = getEntity(resultSet);
                entities.add(entity);
            }
            connection.close();
            return entities;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entities;
    }

    /**
     * Function that executes the part of the save function that is the same in all classes
     * @param sql - the query that needs to be executed
     * @param entity - the entity that needs to be saved
     * @return null- if the given entity is saved
     *         otherwise returns the entity (id already exists)
     */
    protected E saveAbstract(String sql, E entity){
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSqlCommand(entity, statement);
            statement.executeUpdate();
            connection.close();
      //  }catch (PSQLException e){
      //      return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function that sets the data for running an sql command
     * @param ps - the statement that we want to set
     * @throws SQLException - if the statement could not be updated
     */
    protected void setSqlCommandForPaging(Integer offset,Integer fetchNext, PreparedStatement ps) throws SQLException {
        ps.setInt(1, offset);
        ps.setInt(2, fetchNext);
    }

    /**
     * Function that executes the part of the delete function that is the same in all classes
     * @param sql - the query that needs to be executed
     * @param id - the id of the entity that has to be deleted
     * @param number - the position of the id (for the setID function)
     * @return the removed entity or null if there is no entity with the given id
     */
    protected E deleteAbstract(String sql, ID id, int number){
        E entity = findOne(id);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            //ps.setString(1, id);
            setID(statement, number, id);
            int successful = statement.executeUpdate();
            if(successful == 0) {
                connection.close();
                return null;
            }
            connection.close();
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function that executes the part of the delete function that is the same in all classes
     * @param sql - the query that needs to be executed
     * @param entity - the entity that has to be updates
     * @param number - the position of the id (for the setID function)
     * @return null - if the entity is updated,
     *                otherwise  returns the entity  - (e.g id does not exist).
     */
    protected E updateAbstract(String sql, E entity, int number){
        validator.validate(entity);
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setSqlCommand(entity, statement);
            //ps.setInt(7, entity.getId());
            setID(statement, number, entity.getId());
            int successful = statement.executeUpdate();
            if(successful == 0) {
                connection.close();
                return entity;
            }
            connection.close();
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Function that sets in the sql statement the id of the entity
     * @param ps - the statement
     * @param number - the position the id takes in the statement
     * @param id - the id
     * @throws SQLException - if the command can't be executed
     */
    protected abstract void setID(PreparedStatement ps, int number, ID id) throws SQLException;

    /**
     * Function that creates an entity from a resultSet
     * @param resultSet - the Set from where we get the data of the entity
     * @return - the entity with the given data
     * @throws SQLException - if the entity could not be created
     */
    protected abstract E getEntity(ResultSet resultSet) throws SQLException;

    /**
     * Function that sets the data for running an sql command
     * @param entity - the entity from which we take the data
     * @param ps - the statement that we want to set
     * @throws SQLException - if the statement could not be updated
     */
    protected abstract void setSqlCommand(E entity, PreparedStatement ps) throws SQLException;

}

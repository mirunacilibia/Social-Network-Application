package repository.file;

import domain.Entity;
import domain.validators.ValidationException;
import domain.validators.Validator;
import repository.memory.InMemoryRepository;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {

    String fileName;

    /**
     * Constructor
     * @param validator - the validator used for validating the data
     * @param fileName - the file where we store the data
     */
    public AbstractFileRepository(Validator<E> validator, String fileName){
        super(validator);
        this.fileName = fileName;
        LoadData();
    }

    /**
     * Private function that loads the data from the file into the memory
     * @throws FileNotFoundException - if there is no file with that name
     */
    private void LoadData(){
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String linie;
            while ((linie = br.readLine()) != null && !linie.equals("")) {
                List<String> attributes = Arrays.asList(linie.split(";"));
                E e = extractEntity(attributes);
                super.save(e);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes - the attributes of the entity
     * @return an entity of type E
     */
    protected abstract E extractEntity(List<String> attributes);

    /**
     * Creates a string of attributes that can be stored in the file
     * @param entity - the entity from where we take the data
     * @return - a string that contains the attributes of the object
     */
    protected abstract String createEntityAsString(E entity);

    /**
     * Saves an entity in the repository and in the file
     * @param entity
     *         entity must be not null
     * @return null- if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     */
    @Override
    public E save(E entity){
        if (super.save(entity) != null)
            return entity;
        writeToFile(entity);
        return null;
    }

    /**
     * Updates the entity given as parameter from the memory and the file
     * @param entity
     *          entity must not be null
     * @return null - if the entity is updated,
     *                otherwise  returns the entity  - (e.g id does not exist).
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws ValidationException
     *             if the entity is not valid.
     */
    @Override
    public E update(E entity){
        if (super.update(entity) != null)
            return entity;
        writeEntities();
        return null;
    }

    /**
     *  Removes the entity with the specified id from the memory and the file
     * @param id
     *      id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException
     *                   if the given id is null.
     */
    @Override
    public E delete(ID id){
        E entity = super.delete(id);
        if(entity == null)
            return null;
        writeEntities();
        return entity;
    }

    /**
     * Function that writes to the file all the entities
     */
    private void writeEntities(){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false))) {
            for (E e : findAll()) {
                bw.write(createEntityAsString(e));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that writes to the file an entity gives as parameter
     * @param entity - type E, the entity that is writen to the file
     */
    public void writeToFile(E entity){

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(createEntityAsString(entity));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

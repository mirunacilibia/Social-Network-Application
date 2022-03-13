package service;

import domain.Entity;
import domain.User;
import domain.validators.RepositoryException;
import domain.validators.ValidationException;

public interface Service<ID, E extends Entity<ID>>{

    /**
     * @param id - the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    E findOneEntity(ID id);

    /**
     * @return all entities
     */
    Iterable<E> findAllEntities();

    /**
     * @param entity - the entity that will be stored
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if the user has already been saved in the memory
     */
    void saveEntity(E entity);


    /**
     *  removes the entity with the specified id
     * @param id
     *      id must be not null
     * @throws IllegalArgumentException
     *             if the given id is null.
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    E deleteEntity(ID id);

    /**
     * @param entity - the entity that will be stored
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    void updateEntity(E entity);

}

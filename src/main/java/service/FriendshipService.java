package service;

import domain.Friendship;
import domain.validators.RepositoryException;
import domain.validators.ValidationException;
import repository.Repository;
import utils.Tuple;
import utils.events.EventChangeEvent;
import utils.events.FriendshipChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class FriendshipService implements Service<Tuple<String, String>, Friendship>{

    Repository<Tuple<String, String>, Friendship> friendshipRepository;

    /**
     * Constructor
     * @param friendshipRepository - the repository of the service
     */
    public FriendshipService(Repository<Tuple<String, String>, Friendship> friendshipRepository) {
        this.friendshipRepository = friendshipRepository;
    }

    /**
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    @Override
    public Friendship findOneEntity(Tuple<String, String> id) {
        Friendship friendship = friendshipRepository.findOne(id);
        if(friendship == null)
            friendship = friendshipRepository.findOne(new Tuple<>(id.second, id.first));
        return friendship;
    }

    /**
     * @return all entities
     */
    @Override
    public Iterable<Friendship> findAllEntities() {
        return friendshipRepository.findAll();
    }

    /**
     * @param friendship - the friendship that will be stored
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if the users do not exist
     *             if the friendship has already been saved in the memory
     */
    @Override
    public void saveEntity(Friendship friendship) {
        if(friendshipRepository.save(friendship) != null)
            throw new RepositoryException("This friendship is already saved!");
    }

    /**
     *  removes the friendship with the specified id
     * @param id
     *      id must be not null
     * @throws IllegalArgumentException
     *             if the given id is null.
     * @throws RepositoryException
     *             if the friendship does not exist
     */
    @Override
    public Friendship deleteEntity(Tuple<String, String> id) {
        Friendship friendship = friendshipRepository.delete(id);
        if( friendship == null)
            throw new RepositoryException("There is no friendship between these users!");
        return friendship;
    }

    /**
     * @param friendship - the friendship that will be stored
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if the users do not exist
     *             if the friendship has already been saved in the memory
     */
    @Override
    public void updateEntity(Friendship friendship) {
        if(friendshipRepository.update(friendship) != null) {
            friendship.setId(new Tuple<>(friendship.getId().second, friendship.getId().first));
            if(friendshipRepository.update(friendship) != null)
                throw new RepositoryException("There is no friendship between these users!");
        }
    }
}

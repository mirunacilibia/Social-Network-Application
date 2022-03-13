package service;

import domain.User;
import domain.validators.RepositoryException;
import domain.validators.ValidationException;
import repository.Repository;
import repository.database.UserDBRepository;
import utils.events.ChangeEventType;
import utils.events.UserChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class UserService implements Service<String, User>, Observable<UserChangeEvent> {

     UserDBRepository userRepository;

    /**
     * Cosntructor
     * @param userRepository - the repository of the users
     */
    public UserService(UserDBRepository userRepository) {
        this.userRepository = userRepository;
    }


    /**
     * @param s - the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     * @throws IllegalArgumentException
     *                  if id is null.
     */
    @Override
    public User findOneEntity(String s) {
        return userRepository.findOne(s);
    }

    /**
     * @return all entities
     */
    @Override
    public Iterable<User> findAllEntities() {
        return userRepository.findAll();
    }

    /**
     * @param user - the entity that will be stored
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if the user has already been saved in the memory
     */
    @Override
    public void saveEntity(User user) {
        if(userRepository.save(user) != null)
            throw new RepositoryException("This user is already saved!");
        notifyObservers(new UserChangeEvent(ChangeEventType.ADD, user));
    }

    /**
     *  removes the entity with the specified id
     * @param s
     *      id must be not null
     * @throws IllegalArgumentException
     *             if the given id is null.
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    @Override
    public User deleteEntity(String s) {
        User user = userRepository.delete(s);
        if(user == null)
            throw new RepositoryException("There is no user with this email!");
        notifyObservers(new UserChangeEvent(ChangeEventType.DELETE, user));
        return user;
    }

    /**
     * @param user - the entity that will be stored
     * @throws ValidationException
     *            if the entity is not valid
     * @throws IllegalArgumentException
     *             if the given entity is null.
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    @Override
    public void updateEntity(User user) {
        User oldUser = userRepository.findOne(user.getId());
        if(userRepository.update(user) != null)
            throw new RepositoryException("There is no user with this email!");
        notifyObservers(new UserChangeEvent(ChangeEventType.UPDATE, user, oldUser));
    }

    public Iterable<User> findAllEntities(int from, int to) {
        return userRepository.findAll(from,to);
    }

//-------------------------------------------------Observer-------------------------------------------------------------

    private final List<Observer<UserChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<UserChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

}

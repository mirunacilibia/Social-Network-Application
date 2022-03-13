package service;

import controller.MessagesController;
import domain.Message;
import domain.validators.RepositoryException;
import domain.validators.ValidationException;
import repository.Repository;
import utils.events.ChangeEventType;
import utils.events.MessageChangeEvent;
import utils.events.UserChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MessageService implements Service<Integer, Message>, Observable<MessageChangeEvent> {

    Repository<Integer, Message> messageRepository;
    HashMap<String, Observer<MessageChangeEvent>> observersList = new HashMap<>();
    List<String> obs = new ArrayList<>();

    /**
     * Cosntructor
     * @param messageRepository - the repository of the messages
     */
    public MessageService(Repository<Integer, Message> messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * @param integer - the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id
     *          or null - if there is no entity with the given id
     */
    @Override
    public Message findOneEntity(Integer integer) {
        return messageRepository.findOne(integer);
    }

    /**
     * @return all entities
     */
    @Override
    public Iterable<Message> findAllEntities() {
        return messageRepository.findAll();
    }

    /**
     * @param message - the message that will be stored
     * @throws ValidationException
     *            if the entity is not valid
     * @throws RepositoryException
     *             if the user has already been saved in the memory
     */
    @Override
    public void saveEntity(Message message) {
        if(messageRepository.save(message) != null)
            throw new RepositoryException("This message is already saved!");
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, message));
    }

    /**
     *  removes the entity with the specified id
     * @param integer
     *      id must be not null
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    @Override
    public Message deleteEntity(Integer integer) {
        Message message = messageRepository.delete(integer);
        if(message == null)
            throw new RepositoryException("There is no message with this id!");
        notifyObservers(new MessageChangeEvent(ChangeEventType.ADD, message));
        return message;
    }

    /**
     * @param message - the message that will be stored
     * @throws ValidationException
     *            if the entity is not valid
     * @throws RepositoryException
     *             if there is no user with the email that we want to update
     */
    @Override
    public void updateEntity(Message message) {
        Message oldMessage = messageRepository.findOne(message.getId());
        if(messageRepository.save(message) != null)
            throw new RepositoryException("There is no message with this id!");
        notifyObservers(new MessageChangeEvent(ChangeEventType.UPDATE, message, oldMessage));
    }

//-------------------------------------------------Observer-------------------------------------------------------------

    private final List<Observer<MessageChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<MessageChangeEvent> e) {
        observers.add(e);
        System.out.println(observers.size());
    }

    @Override
    public void removeObserver(Observer<MessageChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageChangeEvent t) {
        //observers.forEach(x->x.update(t));
        for(int i = observers.size() - 1; i >= 0; i--)
            observers.get(i).update(t);
    }

    public void addObserver(Observer<MessageChangeEvent> e, String className) {
        Observer<MessageChangeEvent> event = observersList.get(className);
        if(event != null){
            removeObserver(event);
        }
        observersList.put(className, e);
//        if(obs.contains(className))
//            return;
//        obs.add(className);
        observers.add(e);
    }
}

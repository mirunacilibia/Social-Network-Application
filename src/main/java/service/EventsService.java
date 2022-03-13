package service;

import domain.Event;
import domain.Message;
import domain.User;
import domain.validators.RepositoryException;
import repository.Repository;
import repository.database.EventsDBRepository;
import utils.Tuple;
import utils.events.ChangeEventType;
import utils.events.EventChangeEvent;
import utils.events.MessageChangeEvent;
import utils.events.UserChangeEvent;
import utils.observer.Observable;
import utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class EventsService implements Service<Integer, Event>, Observable<EventChangeEvent> {

    EventsDBRepository eventsRepository;


    public EventsService(EventsDBRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    @Override
    public Event findOneEntity(Integer integer) {
        return eventsRepository.findOne(integer);
    }

    @Override
    public Iterable<Event> findAllEntities() {
        return eventsRepository.findAll();
    }

    public Iterable<Event> findAllEntities(int from, int to) {
        return eventsRepository.findAll(from,to);
    }

    @Override
    public void saveEntity(Event entity) {
        if(eventsRepository.save(entity) != null)
            throw new RepositoryException("This event is already saved!");
    }

    @Override
    public Event deleteEntity(Integer integer) {
        Event event = eventsRepository.delete(integer);
        if(event == null)
            throw new RepositoryException("There is no event with this id!");
        return event;
    }

    @Override
    public void updateEntity(Event entity) {
        Event oldEvent = eventsRepository.findOne(entity.getId());
        if(eventsRepository.update(entity) != null)
            throw new RepositoryException("There is no event with this id!");
    }

    public void saveParticipant(int idEvent, String emailUser, boolean isNotified){
        if(eventsRepository.saveParticipant(idEvent, emailUser, isNotified) != null)
            throw new RepositoryException("This participant is already subscribed!");
    }

    public void deleteParticipant(int idEvent, String emailUser) {
        User user = eventsRepository.deleteParticipant(idEvent, emailUser);
        if(user == null)
            throw new RepositoryException("This participant cannot be removed!");
    }

    public void updateParticipant(int idEvent, String emailUser, boolean isNotified){
        eventsRepository.updateParticipant(idEvent, emailUser, isNotified);
    }

    public List<Tuple<User, Tuple <Event, Boolean>>> getParticipantsToEvent(){
        return eventsRepository.getParticipantsToEvent();
    }

    public Tuple<String, Tuple <Integer, Boolean>> getParticipant(int idEvent, String emailUser){
        return eventsRepository.getParticipant(idEvent, emailUser);
    }

    public int getLastEventID(){
        return eventsRepository.getLastEventID();
    }

//-------------------------------------------------Observer-------------------------------------------------------------

    private final List<Observer<EventChangeEvent>> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer<EventChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<EventChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(EventChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }

}

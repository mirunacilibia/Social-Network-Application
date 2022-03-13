package utils.observer;

import utils.events.Event;

public interface Observable<E extends Event> {

    /**
     * Function that adds an Observer to the observers list
     * @param e - the Observer that is added
     */
    void addObserver(Observer<E> e);

    /**
     * Function that removes an Observer from the observers list
     * @param e - the Observer that is removed
     */
    void removeObserver(Observer<E> e);

    /**
     * Function that notifies the observers that data has been changed
     * @param t - the event that made the change
     */
    void notifyObservers(E t);
}

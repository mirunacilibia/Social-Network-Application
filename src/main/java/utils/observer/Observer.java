package utils.observer;

import utils.events.Event;

public interface Observer<E extends Event> {

    /**
     * Function that updates the Observer
     * @param e - the event that made the change
     */
    void update(E e);
}
package utils.events;

import domain.Friendship;

public class EventChangeEvent implements Event{
    private ChangeEventType type;
    private domain.Event data, oldData;

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the data that is changed
     */
    public EventChangeEvent(ChangeEventType type, domain.Event data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the new data
     * @param oldData - the data that is changed
     */
    public EventChangeEvent(ChangeEventType type, domain.Event data, domain.Event oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    /**
     * Getter
     * @return the type of change
     */
    public ChangeEventType getType() {
        return type;
    }

    /**
     * Getter
     * @return the data that is changed
     */
    public domain.Event getData() {
        return data;
    }

    /**
     * Getter
     * @return the old data
     */
    public domain.Event getOldData() {
        return oldData;
    }
}

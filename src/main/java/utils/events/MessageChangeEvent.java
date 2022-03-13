package utils.events;

import domain.Message;

public class MessageChangeEvent implements Event {

    private ChangeEventType type;
    private Message data, oldData;

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the data that is changed
     */
    public MessageChangeEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the new data
     * @param oldData - the data that is changed
     */
    public MessageChangeEvent(ChangeEventType type, Message data, Message oldData) {
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
    public Message getData() {
        return data;
    }

    /**
     * Getter
     * @return the old data
     */
    public Message getOldData() {
        return oldData;
    }
}

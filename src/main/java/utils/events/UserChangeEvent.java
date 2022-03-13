package utils.events;

import domain.User;

public class UserChangeEvent implements Event {

    private ChangeEventType type;
    private User data, oldData;

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the data that is changed
     */
    public UserChangeEvent(ChangeEventType type, User data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the new data
     * @param oldData - the data that is changed
     */
    public UserChangeEvent(ChangeEventType type, User data, User oldData) {
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
    public User getData() {
        return data;
    }

    /**
     * Getter
     * @return the old data
     */
    public User getOldData() {
        return oldData;
    }
}

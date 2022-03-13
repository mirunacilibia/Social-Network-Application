package utils.events;

import domain.Friendship;
import domain.User;

public class FriendshipChangeEvent implements Event{

    private ChangeEventType type;
    private Friendship data, oldData;

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the data that is changed
     */
    public FriendshipChangeEvent(ChangeEventType type, Friendship data) {
        this.type = type;
        this.data = data;
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the new data
     * @param oldData - the data that is changed
     */
    public FriendshipChangeEvent(ChangeEventType type, Friendship data, Friendship oldData) {
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
    public Friendship getData() {
        return data;
    }

    /**
     * Getter
     * @return the old data
     */
    public Friendship getOldData() {
        return oldData;
    }
}

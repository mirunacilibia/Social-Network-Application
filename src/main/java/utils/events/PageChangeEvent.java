package utils.events;

import domain.Friendship;
import domain.Message;
import domain.User;

public class PageChangeEvent implements Event{
    private ChangeEventType type;
    private User dataUser, oldDataUser;
    private Friendship dataFriendship, oldDataFriendship;
    private Message dataMessage, oldDataMessage;
    private domain.Event dataEvent, oldDataEvent;
    private String className;

    public String getClassName() {
        return className;
    }

    /**
     * Getter
     * @return the type of change
     */
    public ChangeEventType getType() {
        return type;
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the data that is changed
     */
    public PageChangeEvent(ChangeEventType type, domain.Event data) {
        this.type = type;
        this.dataEvent = data;
        className = "Event";
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the new data
     * @param oldData - the data that is changed
     */
    public PageChangeEvent(ChangeEventType type, domain.Event data, domain.Event oldData) {
        this.type = type;
        this.dataEvent = data;
        this.oldDataEvent=oldData;
        className = "Event";
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the data that is changed
     */
    public PageChangeEvent(ChangeEventType type, Friendship data) {
        this.type = type;
        this.dataFriendship = data;
        className = "Friendship";
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the new data
     * @param oldData - the data that is changed
     */
    public PageChangeEvent(ChangeEventType type, Friendship data, Friendship oldData) {
        this.type = type;
        this.dataFriendship = data;
        this.oldDataFriendship=oldData;
        className = "Friendship";
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the data that is changed
     */
    public PageChangeEvent(ChangeEventType type, User data) {
        this.type = type;
        this.dataUser = data;
        className = "User";
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param dataUser - the new data
     * @param oldDataUser - the data that is changed
     */
    public PageChangeEvent(ChangeEventType type, User dataUser, User oldDataUser) {
        this.type = type;
        this.dataUser = dataUser;
        this.oldDataUser=oldDataUser;
        className = "User";
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the data that is changed
     */
    public PageChangeEvent(ChangeEventType type, Message data) {
        this.type = type;
        this.dataMessage = data;
        className = "Message";
    }

    /**
     * Constructor
     * @param type - the type of change
     * @param data - the new data
     * @param oldData - the data that is changed
     */
    public PageChangeEvent(ChangeEventType type, Message data, Message oldData) {
        this.type = type;
        this.dataMessage = data;
        this.oldDataMessage=oldData;
        className = "Message";
    }

    /**
     * Getter
     * @return the data that is changed
     */
    public Message getDataMessage() {
        return dataMessage;
    }

    /**
     * Getter
     * @return the old data
     */
    public Message getOldDataMessage() {
        return oldDataMessage;
    }

    /**
     * Getter
     * @return the data that is changed
     */
    public User getDataUser() {
        return dataUser;
    }

    /**
     * Getter
     * @return the old data
     */
    public User getOldDataUser() {
        return oldDataUser;
    }

    /**
     * Getter
     * @return the data that is changed
     */
    public Friendship getDataFriendship() {
        return dataFriendship;
    }

    /**
     * Getter
     * @return the old data
     */
    public Friendship getOldDataFriendship() {
        return oldDataFriendship;
    }

    /**
     * Getter
     * @return the data that is changed
     */
    public domain.Event getDataEvent() {
        return dataEvent;
    }

    /**
     * Getter
     * @return the old data
     */
    public domain.Event getOldDataEvent() {
        return oldDataEvent;
    }

}

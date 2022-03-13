package domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Integer>{

    private User from;
    private List<User> to;
    private String message;
    private LocalDateTime date;
    Message replyMessage;

    /**
     * Constructor
     * @param from          - the User that sends the message
     * @param to            - the list of Users that receive the message
     * @param message       - the text of the message
     * @param date          - the date the message was sent
     * @param replyMessage  - the Message that this object is the reply of
     *                      - null, if this message is not a reply
     */
    public Message(User from, List<User> to, String message, LocalDateTime date, Message replyMessage) {
        this.setId(-1);
        this.from = from;
        this.to = to;
        this.message = message;
        this.date = date;
        this.replyMessage = replyMessage;
    }

    /**
     * Getter
     * @return - the User that sends the message
     */
    public User getFrom() {
        return from;
    }

    /**
     * Setter
     * @param from the User that sends the message
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * Getter
     * @return - the list of Users that receive the message
     */
    public List<User> getTo() {
        return to;
    }

    /**
     * Setter
     * @param to - the list of Users that receive the message
     */
    public void setTo(List<User> to) {
        this.to = to;
    }

    /**
     * Getter
     * @return - the text of the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter
     * @param message - the text of the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter
     * @return - the date the message was sent
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Setter
     * @param date - the date the message was sent
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Getter
     * @return  - the Message that this object is the reply of
     *          - null, if this message is not a reply
     */
    public Message getReplyMessage() {
        return replyMessage;
    }

    /**
     * Setter
     * @param replyMessage  - the Message that this object is the reply of
     *                      - null, if this message is not a reply
     */
    public void setReplyMessage(Message replyMessage) {
        this.replyMessage = replyMessage;
    }

    /**
     * Function that overrides toString
     * @return - String - the message
     */
    @Override
    public String toString() {
        String string = "ID: " + getId().toString() +
                "\nFrom: " + from.getId() + " - " + from.getFirstName() + " " +  from.getLastName() +
                "\nTo: ";
        for(User u: to)
            string += u.getId() + " ";
        string += "\nText: " + message +
                  "\nDate: " + date.toString();
        if(replyMessage != null) {
            String mess = replyMessage.getMessage();
            if (mess.length() > 30)
                mess = mess.substring(0, 30);
            string += "\nResponse of: " + mess;
        }
        string += "\n";
        return string;
    }
}

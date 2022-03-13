package domain;

import utils.Tuple;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LastConversation  extends Entity<Tuple<Message, User>>{
    String chatGroup;
    Message message;
    String messageViewed;
    String user;
    String pathToPicture;
    LocalDateTime data;
    private List<User> to = new ArrayList<>();
    private List<Message> messages;

    /**
     * Function returns the path to picture
     * @return string- path to picture
     */
    public String getPathToPicture() {
        return pathToPicture;
    }

    /**
     * Function sets all the message from chat
     * @param messages list of all messages from 2 users
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    /**
     * Function returns all the message from chat
     * @return messages list of all messages from 2 users
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     *
     * @return the messageView
     */
    public String getMessageViewed() {
        return messageViewed;
    }

    /**
     *
     * @return String
     */
    public String getChatGroup() {
        return chatGroup;
    }

    /**
     * Funtion returns the id of user
     * @return String
     */
    public String getUser() {
        return user;
    }

    public LastConversation(Message message,String user) {
        this.message = message;
        this.user = user;
        this.pathToPicture = "icons/participants.png";
        setting();
    }
    public LastConversation(List<User> to, String user){
        this.to  = to;
        this.user = user;
        if(to.size() == 1)
            pathToPicture = to.get(0).getPathToPicture();
        else this.pathToPicture = "icons/participants.png";
    }
    private void setting() {
            setData();
            setGroup();
            setMessage();
    }
    public void setMessage() {
        this.messageViewed = message.getFrom().firstName+" "+message.getFrom().getLastName()+": "+ message.getMessage();
    }

    public List<User> getTo() {
        return to;
    }

    private void setGroup() {
        if(!Objects.equals(message.getFrom().getId(), user))
            to.add(message.getFrom());
        if(message.getTo().size()>1){
            chatGroup=message.getFrom().firstName+" "+message.getFrom().getLastName()+", ";
            for(int i=0;i<message.getTo().size();i++){
                if(i==message.getTo().size()-1)
                    chatGroup += message.getTo().get(i).firstName+" "+message.getTo().get(i).getLastName();
                else
                    chatGroup += message.getTo().get(i).firstName+" "+message.getTo().get(i).getLastName()+", ";
                if(!Objects.equals(message.getTo().get(i).getId(), user))
                    to.add(message.getTo().get(i));
            }
        }else {
            if (Objects.equals(user, message.getFrom().getId()))
                chatGroup = message.getTo().get(0).firstName + " " + message.getTo().get(0).lastName;
            else
                chatGroup = message.getFrom().firstName + " " + message.getFrom().lastName;
            if(!Objects.equals(message.getTo().get(0).getId(), user))
                to.add(message.getTo().get(0));
        }
        if(to.size() == 1)
            pathToPicture = to.get(0).getPathToPicture();
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData() {
        this.data = message.getDate();
    }

    public Message getMessage() {
        return message;
    }

    public void setMessageId(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message data) {
        if(messages == null)
            messages = new ArrayList<>();
        messages.add(data);
    }
}

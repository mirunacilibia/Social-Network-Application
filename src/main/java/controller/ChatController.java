package controller;

import domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import service.SocialNetworkService;
import utils.events.MessageChangeEvent;
import utils.observer.Observer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class ChatController implements Observer<MessageChangeEvent> {

    private SocialNetworkService service;
    private String user;
    public TextField textMessage;
    public Button buttonSendMessage;
    public Label replyLabel;
    public Button replyButton;
    public ListView<String> List1;
    public ListView<String> List2;
    ObservableList observableList1 = FXCollections.observableArrayList();
    ObservableList observableList2 = FXCollections.observableArrayList();
    private List<User> usersChat;
    private List<Integer> myMessagesIndex = new ArrayList<>();
    LastConversation selected;
    private Integer LastIndex = -1;
    private int ReplyIndexMessage = -1;
    private int LastInfoRowList1 = -1;
    private int LastInfoRowList2 = -1;
    User userObject = null;

    /**
     * Function set all data
     * @param user User
     * @param service SocialNetworkService
     * @param selected LastConversation
     */
    public void settings(String user, SocialNetworkService service,LastConversation selected) {
        this.user = user;
        this.service = service;
        this.selected = selected;
        userObject = service.findOneEntity(user);
        initializeData();
        service.addObserverMessages(this,user + getClass().toString());
    }

    /**
     * Function initialize data
     */
    public void initializeData(){
        replyButton.setVisible(false);
        replyLabel.setVisible(false);
        loadData(selected.getMessages());
        setPhoto();
    }

    /**
     * Function set photo to all messages
     */
    private void setPhoto() {
        List2.setCellFactory(param -> new ListCell<>() {
            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || Objects.equals(name, "") || Objects.equals(name, " " + "\n" + " ")) {
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    Label label = new Label(" " + "\n" + " ");
                    label.setAlignment(Pos.CENTER_RIGHT);
                    hBox.getChildren().add(label);
                    setGraphic(hBox);
                    setText(null);
                    setStyle("-fx-background-color: transparent;\n" +
                            "  -fx-border-width: 0px");
                } else {
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    Label label = new Label();
                    if (!name.contains("\n")) {
                        label.setText(name+"   " + "\n" + " ");
                        label.setAlignment(Pos.CENTER_RIGHT);
                        hBox.getChildren().add(label);
                    } else {
                        label.setText(name);
                        label.setAlignment(Pos.CENTER_RIGHT);
                        hBox.getChildren().add(label);
                    }
                    setGraphic(hBox);
                    setOnMouseClicked(ev -> moreInfo());
                    setStyle("-fx-min-width: 50px;\n" +
                            "  -fx-background-color: #ffffff;\n" +
                            "  -fx-background-radius: 30px;\n" +
                            "  -fx-border-radius: 20px;\n" +
                            "  -fx-border-width: 2px;\n" +
                            "  -fx-border-style: solid;\n" +
                            "  -fx-border-color: #88bdbc");
                }
            }

            /**
             * Function gives more info about a message
             */
            private void moreInfo() {
                if (LastInfoRowList2 != -1) {
                    Message last = selected.getMessages().get(LastInfoRowList2);
                    if( last.getReplyMessage()==null)
                        observableList2.set(LastInfoRowList2, last.getMessage());
                    else {
                        addMessage(observableList2,last,LastInfoRowList2);
                    }
                    List2.setItems(observableList2);
                }
                if (myMessagesIndex.contains(getIndex())) {
                    Message message = selected.getMessages().get(getIndex());
                    observableList2.set(getIndex(), selected.getMessages().get(getIndex()).getMessage() + " \n" + setDate(message.getDate()));
                    List2.setItems(observableList2);
                    LastInfoRowList2 = getIndex();
                } else
                    LastInfoRowList2 = -1;
            }
        });
        List1.setCellFactory(param -> new ListCell<>() {
            private Circle circle = new Circle(20, 20, 20);

            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty || Objects.equals(name, "") || Objects.equals(name, " " + "\n" + " ")) {
                    setText(" " + "\n" + " ");
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;\n" +
                            "  -fx-border-width: 2px");
                } else {
                    setText(name);
                    circle.setStroke(Color.SEAGREEN);
                    User u = selected.getMessages().get(getIndex()).getFrom();
                    circle.setFill(new ImagePattern(new Image(u.getPathToPicture())));
                    circle.setEffect(new DropShadow(+5d, 0.1d, +0.1d, Color.DARKSEAGREEN));
                    circle.setCenterX(17.0f);
                    circle.setCenterY(17.0f);
                    circle.setRadius(17.0f);
                    setGraphic(circle);
                    setStyle("-fx-min-width: 50px;\n" +
                            "  -fx-background-color: #ffffff;\n" +
                            "  -fx-background-radius: 30px;\n" +
                            "  -fx-border-radius: 20px;\n" +
                            "  -fx-border-width: 2px;\n" +
                            "  -fx-border-style: solid;\n" +
                            "  -fx-border-color: #88bdbc");
                    setOnMouseClicked(event -> replyMessage());
                }
            }

            /**
             * Function reply to a message
             */
            private void replyMessage() {
                if(LastInfoRowList1 != -1){
                    observableList1.set(LastInfoRowList1, selected.getMessages().get(LastInfoRowList1).getMessage());
                    List1.setItems(observableList1);
                }
                if(!myMessagesIndex.contains(getIndex())) {
                    Message message = selected.getMessages().get(getIndex());
                    observableList1.set(getIndex(), message.getMessage() + " \n" + setDate(message.getDate()));
                    List1.setItems(observableList1);
                    LastInfoRowList1 = getIndex();
                    if (myMessagesIndex.contains(getIndex()))
                        return;
                    replyButton.setVisible(true);
                    replyLabel.setVisible(true);
                    replyLabel.setText("Reply to " + message.getFrom().getFirstName() +
                            message.getFrom().getLastName() + ": " + message.getMessage());
                    ReplyIndexMessage = getIndex();
                }
                else{
                    ReplyIndexMessage = getIndex();
                }
            }
        });

    }

    /**
     * Function build a nice view for data
     * @param date Local date
     * @return the date with a classic view
     */
    private String setDate(LocalDateTime date) {
        String d = "";

        if( date.toLocalDate().isBefore(LocalDate.now()))
            d += date.getDayOfMonth()+"-"+ date.getMonth()+"-"+ date.getYear();
        else
            d += date.getHour()+":"+date.getMinute();
        return d;
    }

    /**
     * Function load list with messages
     * @param l1 ObservableLis
     * @param l2 ObservableLis
     * @param messages All the messages from conversation
     */
    public void loadList(ObservableList l1, ObservableList l2, List<Message> messages){
        if(messages==null)
            return;
        for(int i = 0;i<messages.size();i++){
            Message message = messages.get(i);
            if(Objects.equals(messages.get(i).getFrom().getId(), user)){
                if(message.getReplyMessage()==null) {
                    addMessage(l2, message,-1);
                    l1.add("");
                }
                else {
                    addMessage(l2, message,-1);
                    l1.add(" "+ "\n"+ " ");
                }
                myMessagesIndex.add(i);
                LastIndex = i;
            }
            else{
                if(message.getReplyMessage()==null) {
                    //l1.add(messages.get(i).getFrom().getFirstName() + " " + messages.get(i).getFrom().getLastName() + ": " + messages.get(i).getMessage());
                    addMessage(l1, message,-1);
                    l2.add("");
                }
                else {
                    addMessage(l1, message,-1);
                    l2.add(" " + "\n"+ " ");
                }
                LastIndex = i;
            }
        }
    }

    /**
     * Function add a message to list
     * @param l2 ObservableLis
     * @param message Message
     * @param index  where we change the value of message
     */
    private void addMessage(ObservableList l2, Message message, Integer index) {
        Message reply = message.getReplyMessage();
        StringBuilder replyMessage = new StringBuilder();
        int j = 0;
        if(reply != null) {
            replyMessage.append("Reply to " +reply.getFrom().getFirstName()+" "+
                    reply.getFrom().getLastName()+ ": ");
            while (j < reply.getMessage().length()) {
                replyMessage.append(reply.getMessage().charAt(j));
                if (j == 10) {
                    replyMessage.append("..");
                    break;
                }
                j++;
            }
            replyMessage.append("\n");
        }
        StringBuilder myMessage = new StringBuilder();
        for(int i=0;i<message.getMessage().length();i++){
            myMessage.append(message.getMessage().charAt(i));
            if(i % 40 == 0 && i != 0) {
                myMessage.append("\n");
            }
            j++;
        }
        replyMessage.append(myMessage);
        if(index == -1)
            l2.add(replyMessage.toString());
        else
            l2.set(index, replyMessage.toString());
    }

    /**
     * Function set the messages to chat
     * @param messages list with all the messages
     */
    public void loadData(List<Message> messages){
        observableList1.removeAll();
        observableList2.removeAll();
        loadList(observableList1,observableList2,messages);
        List1.setItems(observableList1);
        List2.setItems(observableList2);
    }

    /**
     * Function set scrolling from Listview simultaneous
     */
    public void setScrolling() {
        List1.applyCss();
        Node n1 =  List1.lookup(".scroll-bar");
        if (n1 instanceof final ScrollBar bar1) {
            Node n2 = List2.lookup(".scroll-bar");
            if (n2 instanceof final ScrollBar bar2) {
                bar1.valueProperty().bindBidirectional(bar2.valueProperty());
            }
        }
        List2.scrollTo(observableList2.size());
    }

    /**
     * Function send a message
     */
    public void onSendButtonAction(){
        if(textMessage.getText().isEmpty())
            return;
        if(ReplyIndexMessage!=-1) {
            SendReplyMessage();
            return;
        }
        StringBuilder sendEmails = new StringBuilder();
        usersChat = selected.getTo();
        if(usersChat.size() > 1)
            for(User u : usersChat){
                sendEmails.append(u.getId());
                sendEmails.append(";");
            }
        else{
            sendEmails.append(usersChat.get(0).getId());
        }
        service.saveMessage(Arrays.asList(user, sendEmails.toString(), textMessage.getText(), LocalDateTime.now().toString(), "-1"));
        observableList2.add(textMessage.getText());
        observableList1.add("");
        myMessagesIndex.add(LastIndex+1);
        LastIndex+=1;
        textMessage.clear();
        List2.setItems(observableList2);
        List1.setItems(observableList1);
    }

    /**
     * Function send a reply message
     */
    private void SendReplyMessage() {
        Message message = selected.getMessages().get(ReplyIndexMessage);
        String sendEmails = "";
        sendEmails = service.getListOfSenders(service.findOneMessage(message.getId()), user);
        sendEmails += service.findOneMessage(message.getId()).getFrom().getId();
        service.saveMessage(Arrays.asList(user, sendEmails, textMessage.getText(), LocalDateTime.now().toString(), message.getId().toString()));
    }

    /**
     * Function stop option for reply
     * @param actionEvent buttonClicked
     */
    public void onStopReplyButtonAction(ActionEvent actionEvent) {
        stopReply();
    }

    /**
     * Function stop option for reply
     */
    private void stopReply() {
        replyLabel.setText("");
        textMessage.setText("");
        replyButton.setVisible(false);
        replyLabel.setVisible(false);
        ReplyIndexMessage=-1;
    }

    /**
     * Function is called when a message is received from a user
     * @param messageChangeEvent for observable
     */
    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        if(messageChangeEvent.getData().getTo().contains(userObject))
            loadData(selected.getMessages());
    }
}

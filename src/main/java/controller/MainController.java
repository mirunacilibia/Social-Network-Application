package controller;

import domain.Event;
import domain.Status;
import domain.User;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import service.SocialNetworkService;
import utils.Tuple;
import utils.events.ChangeEventType;
import utils.events.PageChangeEvent;
import utils.observer.Observer;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MainController implements Observer<PageChangeEvent>{

    public AnchorPane mainPane;
    public Button logOutButton;
    public ImageView profilePicture;
    public ComboBox<String> notifications;
    public ImageView newNotifications;

    private SocialNetworkService service;
    private String user;
    private User userLoggedIn;
    private ScheduledExecutorService exec;

//--------------------------------------- Initialization and settings --------------------------------------------------

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service) throws IOException {
        this.user = user;
        this.service = service;
        this.userLoggedIn = service.findOneEntity(user);
        setPicture();
        service.addObserver(this);
        initializeData();
        setComboBox();
    }

    /**
     * Function that sets the comboBox for the notifications
     */
    private void setComboBox() {
        notifications.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
                if (arg2 != null) {
                    if(arg2.contains("friend") && arg2.contains("new")) {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("requests-view.fxml"));
                        Parent dashboard = null;
                        try {
                            dashboard = fxmlLoader.load();
                            Node oldRegion = mainPane.getChildren().set(0, dashboard);
                            Map<Object, Object> properties = dashboard.getProperties();
                            oldRegion.getProperties().forEach(properties::putIfAbsent);
                            FriendRequestsController controller = fxmlLoader.getController();
                            controller.settings(user, service);
                            mainPane.getChildren().setAll(dashboard);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(arg2.contains("friend")){
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("friends-view.fxml"));
                        Parent dashboard = null;
                        try {
                            dashboard = fxmlLoader.load();
                            Node oldRegion = mainPane.getChildren().set(0, dashboard);
                            Map<Object, Object> properties = dashboard.getProperties();
                            oldRegion.getProperties().forEach(properties::putIfAbsent);
                            MyFriendsController controller = fxmlLoader.getController();
                            controller.settings(user, service);
                            mainPane.getChildren().setAll(dashboard);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(arg2.contains("message")){
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainMessages-view.fxml"));
                        Parent dashboard = null;
                        try {
                            dashboard = fxmlLoader.load();
                            Node oldRegion = mainPane.getChildren().set(0, dashboard);
                            Map<Object, Object> properties = dashboard.getProperties();
                            oldRegion.getProperties().forEach(properties::putIfAbsent);
                            MainMessagesController controller = fxmlLoader.getController();
                            controller.settings(user, service);
                            mainPane.getChildren().setAll(dashboard);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if(arg2.contains("event")){
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-page-events-view.fxml"));
                        Parent dashboard = null;
                        try {
                            dashboard = fxmlLoader.load();
                            Node oldRegion = mainPane.getChildren().set(0, dashboard);
                            Map<Object, Object> properties = dashboard.getProperties();
                            oldRegion.getProperties().forEach(properties::putIfAbsent);
                            MainPageEventsController controller = fxmlLoader.getController();
                            controller.settings(user, service);
                            mainPane.getChildren().setAll(dashboard);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        notifications.setCellFactory(param -> new ComboBoxListCell<String>() {{
            setTextFill(Color.valueOf("#112D32"));
            setBackground(new Background(new BackgroundFill(Color.valueOf("#88BDBC"), null, null)));
            setFont(new Font(14));
            setPrefHeight(30);
            setAlignment(Pos.CENTER);
            setOnMouseEntered(event -> {
                setBackground(new Background(new BackgroundFill(Color.valueOf("#E0E0E0"), null, null)));
            });
        }});
    }

    /**
     * Function that initializes the window
     * @throws IOException if the pane cannot be loaded
     */
    private void initializeData() throws IOException {
        newNotifications.setVisible(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("search-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        SearchController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            LocalDate now = LocalDate.now();
            for(Event e: service.findAllEvents()) {
                Tuple<String, Tuple<Integer, Boolean>> participant = service.getParticipant(e.getId(), user);
                if(participant != null && e.getStartDate().equals(now) && participant.second.second){
                    newNotifications.setVisible(true);
                    ObservableList<String> items = notifications.getItems();
                    FXCollections.reverse(items);
                    items.add("You have an event today: " + e.getName());
                    FXCollections.reverse(items);
                    notifications.setItems(items);
                }
                if(e.getEndDate().isBefore(now)){
                    service.deleteEvent(e.getId());
                }
            }

        }, 5, 86400, TimeUnit.SECONDS);
    }

    /**
     * Function that sets the profile picure of the user
     */
    public void setPicture(){
        profilePicture.setImage(new Image(userLoggedIn.getPathToPicture()));
    }


    /**
     * Function that makes the "new notification" icon disappear
     * @param mouseEvent the action
     */
    public void seenNotifications(javafx.scene.input.MouseEvent mouseEvent) {
        newNotifications.setVisible(false);
    }

//------------------------------------------- Buttons ------------------------------------------------------------------

    /**
     * Function that loads the MyFriendsController
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onMyFriendsButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("friends-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        MyFriendsController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the FriendRequestsController
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onFriendRequestsButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("requests-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        FriendRequestsController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the MyMessagesController
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onMyMessagesButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainMessages-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        MainMessagesController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the MyAccountController
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onMyAccountButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("page-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        UserPageController controller = fxmlLoader.getController();
        controller.settings(user, service, true);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the SearchController
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onSearchButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("search-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        SearchController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the EventsController
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onEventsButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-page-events-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        MainPageEventsController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the Reports
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onReportsButtonClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("activity-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        ActivityController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that logs out the user
     * @param actionEvent the action
     */
    public void logOut(ActionEvent actionEvent) {
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        stage.close();
    }


//-------------------------------------------- Observer ----------------------------------------------------------------

    /**
     * Function that updates the Observer
     * @param pageChangeEvent - the event that made the change
     */
    @Override
    public void update(PageChangeEvent pageChangeEvent) {
       switch (pageChangeEvent.getClassName()){
           case "User":
               this.userLoggedIn = service.findOneEntity(user);
               setPicture();
               break;
           case "Friendship":
               if(pageChangeEvent.getDataFriendship().getId().second.equals(user) &&
                       pageChangeEvent.getType().equals(ChangeEventType.ADD)) {
                   newNotifications.setVisible(true);
                   User friend = service.findOneEntity(pageChangeEvent.getDataFriendship().getId().first);
                   String friendName = friend.getFirstName() + " " + friend.getLastName();
                   ObservableList<String> items = notifications.getItems();
                   FXCollections.reverse(items);
                   items.add("You have a new friend request from: " + friendName);
                   FXCollections.reverse(items);
                   notifications.setItems(items);
               } else if(pageChangeEvent.getDataFriendship().getId().first.equals(user) &&
                       pageChangeEvent.getType().equals(ChangeEventType.UPDATE)) {
                   newNotifications.setVisible(true);
                   User friend = service.findOneEntity(pageChangeEvent.getDataFriendship().getId().second);
                   String friendName = friend.getFirstName() + " " + friend.getLastName();
                   ObservableList<String> items = notifications.getItems();
                   FXCollections.reverse(items);
                   items.add(friendName + " accepted your friend request!");
                   FXCollections.reverse(items);
                   notifications.setItems(items);
               }
               break;
           case "Message":
               if(pageChangeEvent.getDataMessage().getTo().contains(service.findOneEntity(user))
                    && pageChangeEvent.getType().equals(ChangeEventType.ADD)) {
                   newNotifications.setVisible(true);
                   String messageName = pageChangeEvent.getDataMessage().getFrom().getFirstName() +
                                        pageChangeEvent.getDataMessage().getFrom().getLastName();
                   ObservableList<String> items = notifications.getItems();
                   FXCollections.reverse(items);
                   items.add("You have a new message from: " + messageName);
                   FXCollections.reverse(items);
                   notifications.setItems(items);
               }
               break;
           case "Event":
               if(pageChangeEvent.getDataEvent().getCreator().getId().equals(user)
                    && pageChangeEvent.getType().equals(ChangeEventType.ADD)) {
                   newNotifications.setVisible(true);
                   ObservableList<String> items = notifications.getItems();
                   FXCollections.reverse(items);
                   items.add("You have a new participant to your event!");
                   FXCollections.reverse(items);
                   notifications.setItems(items);
               }
       }
    }
}

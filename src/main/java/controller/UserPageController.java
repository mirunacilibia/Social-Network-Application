package controller;

import domain.Event;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import service.SocialNetworkService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserPageController {

    public ImageView profilePicture;
    public Label labelName;
    public Label labelFriends;
    public Button updateData;
    public AnchorPane pane;
    public AnchorPane mainPane;

    private String user;
    private User userClass;
    private SocialNetworkService service;
    private boolean loggedInUser;

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service, boolean loggedInUser) throws IOException {
        this.user = user;
        this.service = service;
        this.loggedInUser = loggedInUser;
        this.userClass = service.findOneEntity(user);
        initializeData();
    }

    /**
     * Function the initializes the data
     */
    private void initializeData() throws IOException {
        if(!loggedInUser)
            updateData.setVisible(false);
        profilePicture.setImage(new Image(service.findOneEntity(user).getPathToPicture()));
        labelName.setText(userClass.getFirstName() + " " + userClass.getLastName());
        int noOfFriends = (int) service.getFriendsForUser(user).count();
        if(noOfFriends == 1)
            labelFriends.setText(noOfFriends + " Friend");
        else labelFriends.setText(noOfFriends + " Friends");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("page-details-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = pane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        PageDetailsController controller = fxmlLoader.getController();
        controller.settings(user, service, "Details");
        pane.getChildren().setAll(dashboard);;
    }

    /**
     * Function that loads the events page
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void eventsPage(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("page-details-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = pane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        PageDetailsController controller = fxmlLoader.getController();
        controller.settings(user, service, "Events");
        pane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the friends page
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void friendsPage(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("page-details-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = pane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        PageDetailsController controller = fxmlLoader.getController();
        controller.settings(user, service, "Friends");
        pane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the about page
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void aboutPage(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("page-details-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = pane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        PageDetailsController controller = fxmlLoader.getController();
        controller.settings(user, service, "Details");
        pane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the update data window
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onUpdateData(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("account-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = pane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        UpdateAccountController controller = fxmlLoader.getController();
        controller.settings(service.findOneEntity(user), service);
        mainPane.getChildren().setAll(dashboard);
    }
}

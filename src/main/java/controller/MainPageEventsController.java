package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import service.SocialNetworkService;

import java.io.IOException;
import java.util.Map;

public class MainPageEventsController {

    public AnchorPane mainPane;
    private String user;
    private SocialNetworkService service;

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service) throws IOException {
        this.user = user;
        this.service = service;
        initializePane();
    }

    private void initializePane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("your-events-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        YourEventsController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    public void addEventOnClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-event-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        NewEventController controller = fxmlLoader.getController();
        controller.settings(user, service, null);
        mainPane.getChildren().setAll(dashboard);
    }

    public void yourEventsOnClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("your-events-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        YourEventsController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    public void openAllEvents() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("all-events-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        AllEventsController controller = fxmlLoader.getController();
        controller.settings(user, service, false);
        mainPane.getChildren().setAll(dashboard);
    }

    public void allEventsOnClick(ActionEvent actionEvent) throws IOException {
        openAllEvents();
    }

    public void createdEventsOnClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("all-events-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        AllEventsController controller = fxmlLoader.getController();
        controller.settings(user, service, true);
        mainPane.getChildren().setAll(dashboard);
    }
}

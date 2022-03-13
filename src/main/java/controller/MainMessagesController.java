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

public class MainMessagesController {
    public AnchorPane mainPane;
    private String user;
    private SocialNetworkService service;

    public void settings(String user, SocialNetworkService service) throws IOException {
        this.user = user;
        this.service = service;
        service.setGroups(user);
        initializeMainPane();
    }

    /**
     * Function that initializes the window
     * @throws IOException if the pane cannot be loaded
     */
    private void initializeMainPane() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("allMessagesFinal-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        MessagesController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }


    public void SendMessageButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("sendMessage-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        SendMessageController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }

    public void AllMessagesButton(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("allMessagesFinal-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        MessagesController controller = fxmlLoader.getController();
        controller.settings(user, service);
        mainPane.getChildren().setAll(dashboard);
    }
}

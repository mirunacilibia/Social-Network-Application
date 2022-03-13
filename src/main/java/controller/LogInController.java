package controller;

import domain.User;
import domain.validators.RepositoryException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.SocialNetworkService;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class LogInController {

    public TextField username;
    public AnchorPane mainPane;
    public PasswordField password;
    SocialNetworkService service;

    /**
     * Setter
     * @param service - the service
     */
    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    /**
     * Function that loads the MainController of the credentials are correct
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onLogButtonClick(ActionEvent actionEvent) throws IOException {
        try {
            service.logIn(username.getText(), password.getText());
            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();
            MainController controller = fxmlLoader.getController();
            controller.settings(username.getText(),service);
            Scene scene = new Scene(root, 1077, 690);
            primaryStage.setTitle("ADMIR!");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (RepositoryException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    /**
     * Function that loads the previous window
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void goBack(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("first-window-view.fxml"));
        AnchorPane dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        FirstWindowController controller = fxmlLoader.getController();
        controller.setService(service);
        mainPane.getChildren().setAll(dashboard);
    }
}

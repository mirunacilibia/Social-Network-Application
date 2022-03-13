package controller;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import service.SocialNetworkService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class FirstWindowController extends Application {

    public AnchorPane mainPane;
    public Button registerButton;
    public Button logInButton;
    SocialNetworkService service;

    @Override
    public void start(Stage primaryStage) throws Exception {
    }

    /**
     * Setter
     * @param service - the service
     */
    public void setService(SocialNetworkService service) {
        this.service = service;
    }

    /**
     * Function that loads the logIn window
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onClickLogIn(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("log-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        LogInController controller = fxmlLoader.getController();
        controller.setService(service);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that loads the register window
     * @param mouseEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void onClickRegister(ActionEvent mouseEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register-view.fxml"));
        AnchorPane dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        RegisterController controller = fxmlLoader.getController();
        controller.setService(service);
        mainPane.getChildren().setAll(dashboard);
    }
}

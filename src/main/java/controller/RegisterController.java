package controller;

import domain.validators.RepositoryException;
import domain.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import service.SocialNetworkService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class RegisterController {
    public AnchorPane mainPane;
    public PasswordField passwordAgain;
    public TextField email;
    public TextField lastName;
    public TextField phoneNumber;
    public TextField firstName;
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
     * Function that loads the previous window
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void goBack(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("first-window-view.fxml"));
        Parent dashboard = fxmlLoader.load();
        Node oldRegion = mainPane.getChildren().set(0, dashboard);
        Map<Object, Object> properties = dashboard.getProperties();
        oldRegion.getProperties().forEach(properties::putIfAbsent);
        FirstWindowController controller = fxmlLoader.getController();
        controller.setService(service);
        mainPane.getChildren().setAll(dashboard);
    }

    /**
     * Function that creates the account and loads the main app
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void createAccount(ActionEvent actionEvent) throws IOException{
        try {
            if(password.getText().length() < 12)
                throw new ValidationException("Password must be at least 12 characters!");
            if(!password.getText().equals(passwordAgain.getText()))
                throw new IllegalArgumentException("Passwords don't match!");
            service.saveEntity(Arrays.asList(email.getText(), firstName.getText(),
                    lastName.getText(), phoneNumber.getText(), password.getText()));
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "The account has been created!");

            Stage primaryStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            AnchorPane root = fxmlLoader.load();
            MainController controller = fxmlLoader.getController();
            controller.settings(email.getText(), service);
            Scene scene = new Scene(root, 1077, 690);
            primaryStage.setTitle("ADMIR!");
            primaryStage.setScene(scene);
            primaryStage.show();
            //primaryStage.setOnCloseRequest(event -> exec.shutdown());

        } catch (ValidationException | RepositoryException | IllegalArgumentException e){
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Error", e.getMessage());
        }
    }
}
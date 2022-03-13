package controller;

import domain.Event;
import domain.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.RowConstraints;
import service.SocialNetworkService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OneEventController {
    public RowConstraints backgroundPicture;
    public Button button;
    public AnchorPane mainPane;
    public Label nameLabel;
    public Label participantsLabel;
    public Label descriptionLabel;
    public Label locationLabel;
    public Label dateLabel;
    public ImageView picture;
    public Button goBackButton;
    private String user;
    private SocialNetworkService service;
    private Event event;
    private List<User> participants;
    String window;

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service, Event event, String window) {
        this.user = user;
        this.service = service;
        this.event = event;
        this.window = window;
        setParticipants();
        setButton();
        setData();
    }

    private void setData() {
        if(window.equals("AllEvents"))
            goBackButton.setVisible(false);
        nameLabel.setText(event.getName());
        locationLabel.setText(event.getLocation());
        dateLabel.setText(event.getStartDate().toString() + " - " + event.getEndDate().toString());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setText(event.getDescription());
        picture.setImage(new Image(event.getPathToPicture()));

    }

    private void setButton() {
        for(User user1: participants){
            if(user1.getId().equals(user)){
                button.setText("Can't participate");
                return;
            }
        }
        button.setText("Participate");
    }

    private void setParticipants() {
        this.participants = service.getParticipants().stream()
                .filter(x -> x.second.first.getId() == event.getId())
                .map(x -> x.first)
                .collect(Collectors.toList());
        participantsLabel.setText(participants.size() + " people responded");
    }

    /**
     * Function that either save a participant ar removes it
     * @param actionEvent the action
     */
    public void onButtonClick(ActionEvent actionEvent) {
        if(button.getText().equals("Can't participate")){
            button.setText("Participate");
            service.deleteParticipant(event.getId(), user);
            setParticipants();
        } else if(button.getText().equals("Participate")) {
            button.setText("Can't participate");
            service.saveParticipant(event.getId(), user, true);
            setParticipants();
        } else {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Error", "Error!");
        }
    }

    /**
     * Function that loads the previous window
     * @param actionEvent the action
     * @throws IOException if the pane cannot be loaded
     */
    public void goBack(ActionEvent actionEvent) throws IOException {
        if(window.equals("YourEvents")){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("your-events-view.fxml"));
            Parent dashboard = fxmlLoader.load();
            Node oldRegion = mainPane.getChildren().set(0, dashboard);
            Map<Object, Object> properties = dashboard.getProperties();
            oldRegion.getProperties().forEach(properties::putIfAbsent);
            YourEventsController controller = fxmlLoader.getController();
            controller.settings(user, service);
            mainPane.getChildren().setAll(dashboard);
        } else if(window.equals("AllEvents")){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("all-events-view.fxml"));
            Parent dashboard = fxmlLoader.load();
            Node oldRegion = mainPane.getChildren().set(0, dashboard);
            Map<Object, Object> properties = dashboard.getProperties();
            oldRegion.getProperties().forEach(properties::putIfAbsent);
            AllEventsController controller = fxmlLoader.getController();
            controller.settings(user, service, false);
            mainPane.getChildren().setAll(dashboard);
        }
    }
}

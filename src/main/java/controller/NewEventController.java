package controller;

import domain.Event;
import domain.validators.RepositoryException;
import domain.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import service.SocialNetworkService;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

public class NewEventController {
    public Button addEventButton;
    public TextField TextFieldName;
    public TextArea TextFieldDescription;
    public TextField TextFieldLocation;
    public DatePicker TextFieldStartDate;
    public DatePicker TextFieldEndDate;
    public AnchorPane anchorPane;
    public ImageView image;
    private String user;
    private SocialNetworkService service;
    private Event event;
    private boolean addedImage = false;

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service, Event event) {
        this.user = user;
        this.service = service;
        this.event = event;
        initializeData();
    }

    /**
     * Function that initializes the data
     */
    private void initializeData() {
        if(event == null){
            addEventButton.setText("Add event");
        }else {
            addEventButton.setText("Modify event");
            TextFieldName.setText(event.getName());
            TextFieldDescription.setText(event.getDescription());
            TextFieldStartDate.setValue(event.getStartDate());
            TextFieldEndDate.setValue(event.getEndDate());
            TextFieldLocation.setText(event.getLocation());
            image.setImage(new Image(event.getPathToPicture()));
        }
    }

    /**
     * Function that adds a photo for the event
     * @param actionEvent the action
     */
    public void addPhotoOnClick(MouseEvent actionEvent) {
        String imageURL;
        final FileChooser f = new FileChooser();
        File file = f.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            imageURL = file.toURI().toString();
            Image img = new Image(imageURL);
            image.setImage(img);
            addedImage = true;
        }
    }

    /**
     * Function that saves the event
     * @param actionEvent the action
     */
    public void addOnClick(ActionEvent actionEvent) {
        if(event == null){
            try {
                if(!addedImage) {
                    service.saveEvent(Arrays.asList(user, TextFieldName.getText(), TextFieldDescription.getText(),
                            TextFieldStartDate.getValue().toString(), TextFieldEndDate.getValue().toString(), TextFieldLocation.getText()));
                } else{
                    service.saveEvent(Arrays.asList(user, TextFieldName.getText(), TextFieldDescription.getText(),
                            TextFieldStartDate.getValue().toString(), TextFieldEndDate.getValue().toString(), TextFieldLocation.getText(), image.getImage().getUrl()));
                }
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "You created a new event!");
                TextFieldName.clear();
                TextFieldDescription.clear();
                TextFieldStartDate.getEditor().clear();
                TextFieldEndDate.getEditor().clear();
                TextFieldLocation.clear();
                image.setImage(null);
            } catch (ValidationException | RepositoryException e){
                MessageAlert.showErrorMessage(null, e.getMessage());
            } catch (NullPointerException e){
                MessageAlert.showErrorMessage(null, "You must enter all the data!");
            }
        } else {
            try {
                service.updateEvent(Arrays.asList(event.getId().toString(), user, TextFieldName.getText(), TextFieldDescription.getText(),
                        TextFieldStartDate.getValue().toString(), TextFieldEndDate.getValue().toString(), TextFieldLocation.getText(), image.getImage().getUrl()));
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Success", "You created a new event!");
            } catch (ValidationException | RepositoryException e){
                MessageAlert.showErrorMessage(null, e.getMessage());
            }
        }

    }
}

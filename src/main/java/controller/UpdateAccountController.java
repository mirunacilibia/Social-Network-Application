package controller;

import domain.User;
import domain.validators.RepositoryException;
import domain.validators.ValidationException;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import service.SocialNetworkService;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class UpdateAccountController {
    public AnchorPane anchorPane;
    public ImageView image;
    public TextField password;
    public TextField textFieldFirstName;
    public TextField textFieldLastName;
    public TextField textFieldPhoneNumber;
    public PasswordField password1;
    private User user;
    private SocialNetworkService service;
    private String imageURL;

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(User user, SocialNetworkService service) {
        this.user = user;
        this.service = service;
        initializeData();
    }

    /**
     * Function the initializes the data
     */
    private void initializeData() {
        textFieldFirstName.setText(user.getFirstName());
        textFieldLastName.setText(user.getLastName());
        textFieldPhoneNumber.setText(user.getPhoneNumber());
        imageURL = user.getPathToPicture();
        image.setImage(new Image(user.getPathToPicture()));
    }

    /**
     * Function that loads a new profile picture
     * @param actionEvent the action
     */
    public void onBrowse(ActionEvent actionEvent) {

        final FileChooser f = new FileChooser();
        File file = f.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            imageURL = file.toURI().toString();
            Image img = new Image(imageURL);
            image.setImage(img);
        }
    }

    /**
     * Function that saves the update of the user
     * @param actionEvent the action
     */
    public void saveUpdate(ActionEvent actionEvent) {
        try {
            service.logIn(user.getId(), password.getText());
            if(password1.getText().length() > 0)
                service.updateEntity(Arrays.asList(user.getId(), textFieldFirstName.getText(),
                    textFieldLastName.getText(), textFieldPhoneNumber.getText(), password1.getText(), imageURL));
            else
                service.updateEntity(Arrays.asList(user.getId(), textFieldFirstName.getText(),
                        textFieldLastName.getText(), textFieldPhoneNumber.getText(), password.getText(), imageURL));
        } catch (ValidationException | RepositoryException e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }
}

package controller;

import domain.Event;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import service.SocialNetworkService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class PageDetailsController {

    public TableColumn tableColumnProfilePhoto;
    public TableColumn tableColumnFirstName;
    public TableColumn tableColumnLastName;
    public TableColumn tableColumnEmail;
    public TableColumn tableColumnPhoto;
    public TableColumn tableColumnName;
    public TableColumn tableColumnStartDate;
    public TableColumn tableColumnEndDate;
    public TableView<Event> tableViewEvents;
    public TableView<User> tableViewFriends;

    public Label labelNameDetails;
    public Label labelEmail;
    public Label labelPhone;
    public VBox detailsPane;

    private ObservableList<User> friends = FXCollections.observableArrayList();
    private ObservableList<Event> events = FXCollections.observableArrayList();

    private String user;
    private SocialNetworkService service;
    private String type;
    private User userClass;


    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service, String type) throws IOException {
        this.user = user;
        this.service = service;
        this.type = type;
        this.userClass = service.findOneEntity(user);
        if(type.equals("Friends"))
            setFriends();
        else if(type.equals("Events"))
            setEvents();
        else
            setDetails();
    }

    /**
     * Sets the details page
     */
    private void setDetails() {
        tableViewEvents.setVisible(false);
        tableViewFriends.setVisible(false);
        detailsPane.setVisible(true);
        labelEmail.setText(userClass.getId());
        labelNameDetails.setText(userClass.getFirstName() + " " + userClass.getLastName());
        labelPhone.setText(userClass.getPhoneNumber());
    }

    /**
     * Sets the friends page
     */
    private void setFriends() {
        tableViewEvents.setVisible(false);
        tableViewFriends.setVisible(true);
        detailsPane.setVisible(false);
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<User, String>("id"));
        addPhotoToTable(tableColumnProfilePhoto);
        friends.setAll(service.getFriendsForUser(user).collect(Collectors.toList()));
        tableViewFriends.setItems(friends);
    }

    /**
     * Sets the events page
     */
    private void setEvents() {
        tableViewEvents.setVisible(true);
        tableViewFriends.setVisible(false);
        detailsPane.setVisible(false);
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        tableColumnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        addPhotoToTable(tableColumnPhoto);
        events.setAll(getEvents());
        tableViewEvents.setItems(events);
    }

    /**
     *
     * @return the list of events the user is attending
     */
    private List<Event> getEvents(){
        return service.getParticipants().stream()
                .filter(x -> x.first.getId().equals(user))
                .map(x -> x.second.first)
                .collect(Collectors.toList());
    }

    /**
     * Function that adds a photo to the tableView
     */
    private void addPhotoToTable(TableColumn tableColumn) {
        tableColumn.setCellFactory(param -> {
            final ImageView imageview = new ImageView();
            imageview.setFitHeight(50);
            imageview.setFitWidth(50);

            TableCell<User, String> cell = new TableCell<User, String>() {
                public void updateItem(String item, boolean empty) {
                    if (item != null) {
                        imageview.setImage(new Image(item));
                    }
                }
            };
            cell.setGraphic(imageview);
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        tableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("pathToPicture"));
    }
}

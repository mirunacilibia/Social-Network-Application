package controller;

import domain.TableViewClass;
import domain.User;
import domain.validators.RepositoryException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import service.SocialNetworkService;
import utils.Tuple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MyFriendsController {

    public AnchorPane mainPane;
    public TableColumn tableColumnFirstName;
    public TableColumn tableColumnPhoto;
    public TableColumn tableColumnLastName;
    public TableColumn tableColumnButton;
    public TextField searchTextField;
    ObservableList<User> modelUser = FXCollections.observableArrayList();
    boolean addFriends = false;

    private SocialNetworkService service;
    private String user;

    @FXML
    TableView<User> tableViewUsers;

//--------------------------------------- Initialization and settings --------------------------------------------------

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service) {
        this.user = user;
        this.service = service;
        initializeTableView();
        setTableSelection();
    }


    /**
     * Function that adds a photo to the tableView
     */
    private void addPhotoToTable(){
        tableColumnPhoto.setCellFactory(param -> {
            final Circle circle = new Circle(250, 250, 120);
            TableCell<User, String> cell = new TableCell<User, String>() {
                public void updateItem(String item, boolean empty) {
                    if (item != null) {
                        circle.setStroke(Color.SEAGREEN);
                        circle.setFill(new ImagePattern(new Image(item)));
                        circle.setEffect(new DropShadow(+5d, 0.1d, +0.1d, Color.DARKSEAGREEN));
                        circle.setCenterX(17.0f);
                        circle.setCenterY(17.0f);
                        circle.setRadius(35.0f);
                        setGraphic(circle);
                    }
                }
            };
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        tableColumnPhoto.setCellValueFactory(new PropertyValueFactory<User, String>("pathToPicture"));
    }

    /**
     * Function that initializes the tableView
     */
    private void initializeTableView() {
        setData();
        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                tableViewUsers.getSelectionModel().clearSelection();
                if(addFriends)
                    setDataAddFriend();
                else
                    setData();
            }
        });

        tableViewUsers.setItems(modelUser);
    }

    /**
     * Function that loads a user's friends
     */
    public void setData() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        addPhotoToTable();
        addButtonToTable();

        List<User> list = getUserFriendsList();

        Predicate<User> p1 = n -> n.getFirstName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());
        Predicate<User> p2 = n -> n.getLastName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());

        if (searchTextField.getText().length() > 0) {
            modelUser.setAll(list.stream()
                    .filter(p1.or(p2))
                    .collect(Collectors.toList()));
        } else {
            modelUser.setAll(list);
        }
    }


//------------------------------------------- Buttons ------------------------------------------------------------------

    /**
     * Function that returns the list for the tableView
     * @return - a list of users
     */
    private List<User> getUserFriendsList() {
        List<User> u = service.getFriendsForUser(user).toList();
        return service.getFriendsForUser(user).toList();
    }


    private List<User> getUsers(){
        List<User> list = new ArrayList<>();
        for(User u: service.findAllEntities()){
            list.add(u);
        }
        return list.stream()
                .filter(x -> service.findOneRelation(new Tuple<>(user, x.getId())) == null)
                .filter(x -> !x.getId().equals(user))
                .collect(Collectors.toList());
    }

    public void setDataAddFriend() {
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        addPhotoToTable();
        addAddButtonToTable();

        List<User> list = getUsers();

        Predicate<User> p1 = n -> n.getFirstName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());
        Predicate<User> p2 = n -> n.getLastName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());

        if (searchTextField.getText().length() > 0) {
            modelUser.setAll(list.stream()
                    .filter(p1.or(p2))
                    .collect(Collectors.toList()));
        } else {
            modelUser.setAll(list);
        }
    }

    /**
     * Function that adds the delete request button to the tableview
     */
    private void addAddButtonToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory
                = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                final TableCell<User, Void> cell = new TableCell<User, Void>() {

                    private final Button btn = new Button("Add");
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        btn.getStyleClass().add("buttons");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction((ActionEvent event) -> {
                                try {
                                    User user1 = getTableView().getItems().get(getIndex());
                                    service.saveRelation(Arrays.asList(user, user1.getId(), "PENDING"));
                                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Add", "Friendship request sent!");
                                    modelUser.setAll(getUsers());
                                    setDataAddFriend();
                                    tableViewUsers.setItems(modelUser);
                                } catch (RepositoryException e){
                                    MessageAlert.showErrorMessage(null, e.getMessage());
                                }
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        tableColumnButton.setCellFactory(cellFactory);
    }

    /**
     * Function that adds the delete request button to the tableview
     */
    private void addButtonToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory
                = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                final TableCell<User, Void> cell = new TableCell<User, Void>() {

                    private final Button btn = new Button("Delete");
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        btn.getStyleClass().add("buttons");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction((ActionEvent event) -> {
                                try {
                                    User user1 = getTableView().getItems().get(getIndex());
                                    service.deleteRelation(new Tuple<>(user,user1.getId()));
                                    MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Friendship deleted!");
                                    modelUser.setAll(getUserFriendsList());
                                    setData();
                                    tableViewUsers.setItems(modelUser);
                                } catch (RepositoryException e){
                                    MessageAlert.showErrorMessage(null, e.getMessage());
                                }
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        tableColumnButton.setCellFactory(cellFactory);
    }

    /**
     * Function that sets the onClick action of tableView cells
     */
    private void setTableSelection(){
        tableViewUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("page-view.fxml"));
                Parent dashboard = null;
                try {
                    dashboard = fxmlLoader.load();
                    UserPageController controller = fxmlLoader.getController();
                    controller.settings(newSelection.getId(), service, false);
                    mainPane.getChildren().setAll(dashboard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void addFriends(ActionEvent actionEvent) {
        addFriends = true;
        setDataAddFriend();
        tableViewUsers.setItems(modelUser);
    }

    public void myFriends(ActionEvent actionEvent) {
        addFriends = false;
        setData();
        tableViewUsers.setItems(modelUser);
    }
}

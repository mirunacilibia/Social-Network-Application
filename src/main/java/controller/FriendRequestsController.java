package controller;

import domain.Friendship;
import domain.TableViewClass;
import domain.User;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FriendRequestsController {

    public TableView<TableViewClass> tableViewRequests;

    public TableColumn<TableViewClass, String> tableColumnFirstName;
    public TableColumn<TableViewClass, String> tableColumnLastName1;
    public TableColumn<TableViewClass, String> tableColumnStatus;
    public TableColumn<TableViewClass, String> tableColumnDate;
    public TableColumn<TableViewClass, Void> tableColumnButtons;
    public TableColumn<TableViewClass, Void> tableColumnButtons1;
    public TableColumn<TableViewClass, Image> tableColumnPhoto;
    public Button sentButton;
    public Button receivedButton;
    public AnchorPane mainPane;

    ObservableList<TableViewClass> modelFriendRequests = FXCollections.observableArrayList();
    private SocialNetworkService service;
    private String user;

//--------------------------------------- Initialization and settings --------------------------------------------------

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service) {
        this.user = user;
        this.service = service;
        setTableViewReceived();
        setTableSelection();
    }

    /**
     * Function that returns the list for the tableView
     * @return - a list of TableViewClass
     */
    private List<TableViewClass> getReceivedFriendRequests() {
        return service.getFriendRequest(user)
                .map(x -> {
                    User user = service.findOneEntity(x.getId().first);
                    return new TableViewClass(x.getId(), user.getFirstName(), user.getLastName(), x.getStatus(), x.getDate(), user.getPathToPicture());
                })
                .collect(Collectors.toList());
    }

    /**
     * Function that returns the list for the tableView
     * @return - a list of TableViewClass
     */
    private List<TableViewClass> getSentFriendRequests() {
        return service.getSentFriendRequests(user)
                .map(x -> {
                    User user = service.findOneEntity(x.getId().second);
                    return new TableViewClass(x.getId(), user.getFirstName(), user.getLastName(), x.getStatus(), x.getDate(), user.getPathToPicture());
                })
                .collect(Collectors.toList());
    }

//----------------------------------- Add photo / button to tableView --------------------------------------------------

    /**
     * Function that sets the onClick action of tableView cells
     */
    private void setTableSelection(){
        tableViewRequests.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("page-view.fxml"));
                Parent dashboard = null;
                try {
                    dashboard = fxmlLoader.load();
                    UserPageController controller = fxmlLoader.getController();
                    String pageOfUser;
                    if(newSelection.getId().first.equals(user))
                        pageOfUser = newSelection.getId().second;
                    else pageOfUser = newSelection.getId().first;
                    controller.settings(pageOfUser, service, false);
                    mainPane.getChildren().setAll(dashboard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Function that adds the accept request button to the tableview
     */
    private void addAcceptButtonToTable() {
        Callback<TableColumn<TableViewClass, Void>, TableCell<TableViewClass, Void>> cellFactory
                = new Callback<TableColumn<TableViewClass, Void>, TableCell<TableViewClass, Void>>() {
            @Override
            public TableCell<TableViewClass, Void> call(final TableColumn<TableViewClass, Void> param) {
                final TableCell<TableViewClass, Void> cell = new TableCell<TableViewClass, Void>() {

                    private final Button btn = new Button("Accept");
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        btn.getStyleClass().add("buttons");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction((ActionEvent event) -> {
                                TableViewClass tableViewClass = getTableView().getItems().get(getIndex());
                                service.acceptFriendRequest(Arrays.asList(user, tableViewClass.getId().first, "ACCEPTED"));
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Accept", "Friendship accepted!");
                                setTableViewReceived();
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        tableColumnButtons.setCellFactory(cellFactory);
    }

    /**
     * Function that adds the reject friendship button to the tableview
     */
    private void addRejectButtonToTable() {
        Callback<TableColumn<TableViewClass, Void>, TableCell<TableViewClass, Void>> cellFactory
                = new Callback<TableColumn<TableViewClass, Void>, TableCell<TableViewClass, Void>>() {
            @Override
            public TableCell<TableViewClass, Void> call(final TableColumn<TableViewClass, Void> param) {
                final TableCell<TableViewClass, Void> cell = new TableCell<TableViewClass, Void>() {

                    private final Button btn = new Button("Reject");
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        btn.getStyleClass().add("buttons");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction((ActionEvent event) -> {
                                TableViewClass tableViewClass = getTableView().getItems().get(getIndex());
                                service.acceptFriendRequest(Arrays.asList(user, tableViewClass.getId().first, "REJECTED"));
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Reject", "Friendship rejected!");
                                setTableViewReceived();
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        tableColumnButtons1.setCellFactory(cellFactory);
    }

    /**
     * Function that adds the delete request button to the tableview
     */
    private void addDeleteButtonToTable() {
        Callback<TableColumn<TableViewClass, Void>, TableCell<TableViewClass, Void>> cellFactory
                = new Callback<TableColumn<TableViewClass, Void>, TableCell<TableViewClass, Void>>() {
            @Override
            public TableCell<TableViewClass, Void> call(final TableColumn<TableViewClass, Void> param) {
                final TableCell<TableViewClass, Void> cell = new TableCell<TableViewClass, Void>() {

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
                                TableViewClass tableViewClass = getTableView().getItems().get(getIndex());
                                service.deleteRelation(tableViewClass.getId());
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Delete", "Friendship request deleted!");
                                setTableViewSent();
                            });
                            setGraphic(btn);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        };
        tableColumnButtons.setCellFactory(cellFactory);
    }

    /**
     * Function that adds a photo to the tableView
     */
    private void addPhotoToTable() {
        tableColumnPhoto.setCellFactory(param -> {
            final Circle circle = new Circle(250, 250, 120);
            TableCell<TableViewClass, Image> cell = new TableCell<TableViewClass, Image>() {
                public void updateItem(Image item, boolean empty) {
                    if (item != null) {
                        circle.setStroke(Color.SEAGREEN);
                        circle.setFill(new ImagePattern(item));
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
        tableColumnPhoto.setCellValueFactory(new PropertyValueFactory<TableViewClass, Image>("image"));
    }

//------------------------------------------- Buttons ------------------------------------------------------------------

    /**
     * Function that shows the received friend requests
     * @param actionEvent the action
     */
    public void onReceivedButtonClick(ActionEvent actionEvent) {
        setTableViewReceived();
    }

    private void setTableViewReceived(){
        tableColumnButtons1.setVisible(true);
        modelFriendRequests.setAll(getReceivedFriendRequests());
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName1.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        addAcceptButtonToTable();
        addRejectButtonToTable();
        addPhotoToTable();
        tableViewRequests.setItems(modelFriendRequests);
    }

    /**
     * Function that shows the sent friend requests
     * @param actionEvent the action
     */
    public void onSentButtonClick(ActionEvent actionEvent) {
        setTableViewSent();
    }

    private void setTableViewSent(){
        modelFriendRequests.setAll(getSentFriendRequests());
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnLastName1.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        tableColumnDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        addDeleteButtonToTable();
        addPhotoToTable();
        tableColumnButtons1.setVisible(false);
        tableViewRequests.setItems(modelFriendRequests);
    }
}

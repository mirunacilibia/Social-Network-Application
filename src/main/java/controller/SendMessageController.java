package controller;

import domain.*;
import domain.Event;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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

import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SendMessageController {
    public TextField searchTextField;
    public TableColumn tableColumnPhoto;
    public TableColumn tableColumnPhotoSmall;
    public TableView tableViewSmall;
    public TableColumn tableColumnFirstNameSmall;
    public TableColumn tableColumnLastNameSmall;
    public TableColumn tableColumnFirstName;
    public TableColumn tableColumnLastName;
    public TableView tableViewUsers;
    public TableColumn tableColumnButton;
    public Label labelSend;
    public AnchorPane mainPane;
    public Button leftButton;
    public Button rightButton;
    private String user;
    private SocialNetworkService service;
    private ObservableList<User> filteredUsers = FXCollections.observableArrayList();
    private ObservableList<User> sendToUsers = FXCollections.observableArrayList();
    private int indexNextPage = 4;
    private int page = 0;
    private int maxUser = 0;
    private int to = 4;

    public void settings(String user, SocialNetworkService service) {
        this.user = user;
        this.service = service;
        initializeTableView();
    }

    private void setMax() {
        List<User> list =   new ArrayList<>();
        for(User u: service.findAllEntities()){
            list.add(u);
        }
        maxUser = list.size();
    }
    /**
     * Function that adds a photo to the tableView
     */
    private void addPhotoToTable(TableColumn tableColumn){
        tableColumn.setCellFactory(param -> {
            final Circle circle = new Circle(250, 250, 120);
            TableCell<User, String> cell = new TableCell<User, String>() {
                public void updateItem(String item, boolean empty) {
                    if (item != null) {
                        circle.setStroke(javafx.scene.paint.Color.SEAGREEN);
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
        tableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("pathToPicture"));
    }

    /**
     * Function that adds a button to the tableview
     */
    private void addButtonToTable() {
        Callback<TableColumn<User, Void>, TableCell<User, Void>> cellFactory
                = new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                final TableCell<User, Void> cell = new TableCell<User, Void>() {

                    private final Button btn = new Button("X");
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction((ActionEvent event) -> {
                                sendToUsers.remove(getTableView().getItems().get(getIndex()));
                                if(sendToUsers.isEmpty())
                                    labelSend.setVisible(false);
                                initializeTableViewSmall();
                                tableViewSmall.setItems(sendToUsers);
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

    @FXML
    private void initialize() {
        labelSend.setVisible(false);
        tableViewSmall.getStyleClass().add("no-header");
        tableViewSmall.setPlaceholder(new Label(""));

        tableViewUsers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                //Check whether item is selected and set value of selected item to Label
                if(tableViewUsers.getSelectionModel().getSelectedItem() != null)
                {
                    initializeTableViewSmall();
                    User user = (User) tableViewUsers.getSelectionModel().getSelectedItem();
                    if(!sendToUsers.contains(user)){
                        sendToUsers.add(user);
                        labelSend.setVisible(true);
                    }
                    tableViewSmall.setItems(sendToUsers);
                    indexNextPage = 4;
                }
            }
        });
    }

    private void initializeTableViewSmall(){
        tableColumnFirstNameSmall.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnLastNameSmall.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        addPhotoToTable(tableColumnPhotoSmall);
        addButtonToTable();
    }

    private void initializeTableView(){
        setMax();
        leftButton.setVisible(false);
        updateData(0);
        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                tableViewUsers.getSelectionModel().clearSelection();
                leftButton.setVisible(false);
                page = 0;
                updateData(0);
            }
        });
        tableViewUsers.setItems(filteredUsers);
    }

    private boolean updateData(int from){
            if(from < 0)  return  false;
            rightButton.setVisible(false);
            leftButton.setVisible(page!=0);
            tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
            tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
            addPhotoToTable(tableColumnPhoto);

            List<User> list = new ArrayList<>();

        for (User f : service.findAllEntities(from, to)) {list.add(f);}
            if(list.size()==0)
                return false;

            Predicate<User> p1 = n -> n.getFirstName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());
            Predicate<User> p2 = n -> n.getLastName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());
            Predicate<User> p3 = n -> !n.getId().equals(user);

            indexNextPage = from + to;

            if (searchTextField.getText().length() > 0) {
                rightButton.setVisible(false);
                list = list.stream()
                        .filter((p1.or(p2)).and(p3))
                        .collect(Collectors.toList());
                if(list.size()!=0) {
                    filteredUsers.setAll(list);
                    rightButton.setVisible(true);
                }
                else if(page == 0)
                    filteredUsers.setAll(list);
                while (indexNextPage<maxUser) {
                    if (filteredUsers.size() != to) {
                        for (User f : service.findAllEntities(indexNextPage, to -filteredUsers.size())) {
                            list.add(f);
                        }
                        indexNextPage += to - filteredUsers.size();
                        list = list.stream()
                                .filter((p1.or(p2)).and(p3))
                                .collect(Collectors.toList());
                        if(list.size()!=0)
                            filteredUsers.setAll(list);
                    }
                    else {
                        rightButton.setVisible(true);
                        return true;
                    }
                }
                if(page==0)
                    filteredUsers.setAll(list);
                rightButton.setVisible(false);
                return list.size()!=0;
            } else {
                filteredUsers.setAll(list.stream()
                        .filter(p3)
                        .collect(Collectors.toList()));
                if(filteredUsers.size()!= to) {
                    for (User f : service.findAllEntities(indexNextPage, 1)) {
                        list.add(f);
                        indexNextPage ++;
                    }
                    filteredUsers.setAll(list.stream()
                            .filter((p1.or(p2)).and(p3))
                            .collect(Collectors.toList()));
                }
                rightButton.setVisible(indexNextPage < maxUser);
            }
            return true;
    }

    public void sendButtonOnClick(ActionEvent actionEvent) throws IOException {
        List<User> users = sendToUsers.stream().toList();
        if(users.isEmpty())
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Error", "You have to choose at least one user!");
        else {
            User me = service.findOneEntity(user);
            sendToUsers.add(me);
            List<User> users_group  = sendToUsers.stream().toList();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
            Parent dashboard = fxmlLoader.load();

            users_group = users_group.stream().sorted(Comparator.comparing(Entity::getId)).toList();
            ChatController controller = fxmlLoader.getController();
            LastConversation last;
            if(service.getGroups().get(users_group)!=null) {
                List<Message> messages = service.getGroups().get(users_group);
                last = new LastConversation(messages.get(messages.size() - 1), user);
                last.setMessageId(messages);
            }
            else
                last = new LastConversation(users, user);
            controller.settings(user, service,last);
            mainPane.getChildren().setAll(dashboard);
            controller.setScrolling();
        }
    }

    public void leftButtonAction(ActionEvent actionEvent) {
        page--;
        if(!updateData(page * to))
            page++;
        leftButton.setVisible(page!=0);
    }

    public void rightButtonAction(ActionEvent actionEvent) {
        page++;
        if(!updateData(indexNextPage))
            page--;
        leftButton.setVisible(page!=0);
    }
}

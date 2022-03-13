package controller;

import domain.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SearchController {

    public TextField searchTextField;
    public TableColumn tableColumnPhoto;
    public TableColumn tableColumnFirstName;
    public TableColumn tableColumnLastName;
    public CheckBox showUsers;
    public CheckBox showEvents;
    public TableView<User> tableViewUsers;
    public TableView<Event> tableViewEvents;
    public TableColumn tableColumnPhoto1;
    public TableColumn tableColumnName;
    public TableColumn tableColumnLocation;
    public AnchorPane mainPane;
    public Button leftButton;
    public Button rightButton;
    private String user;
    private SocialNetworkService service;
    private ObservableList<User> users = FXCollections.observableArrayList();
    private ObservableList<Event> events = FXCollections.observableArrayList();
    private int indexNextPage = 10;
    int maxUser = 0;
    int maxEvent = 0;
    int page = 0;
    int to = 5;

    public void settings(String user, SocialNetworkService service) {
        this.user = user;
        this.service = service;
        showUsers.setSelected(true);
        setMax();
        initializeTableView();
        setCheckBox();
        setTableSelection();
    }

    private void setMax() {
        List<User> list =   new ArrayList<>();
        for(User u: service.findAllEntities()){
            list.add(u);
        }
        maxUser = list.size();
        List<Event> listE =   new ArrayList<>();
        for(Event u: service.findAllEvents()){
            listE.add(u);
        }
        maxEvent = listE.size();
    }

    private void setCheckBox(){
        showUsers.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
                if(showEvents.isSelected() && showUsers.isSelected()){
                    showEvents.setSelected(false);
                }
                leftButton.setVisible(false);
                updateData(0);
                tableViewUsers.setVisible(true);
                tableViewEvents.setVisible(false);
                tableViewUsers.setItems(users);
            }
        });

        showEvents.selectedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> ov,
                                Boolean old_val, Boolean new_val) {
                if(showUsers.isSelected() && showEvents.isSelected()){
                    showUsers.setSelected(false);
                }
                leftButton.setVisible(false);
                rightButton.setVisible(false);
                updateData(0);
                tableViewUsers.setVisible(false);
                tableViewEvents.setVisible(true);
                tableViewEvents.setItems(events);
            }
        });
    }

    /**
     * Function that adds a photo to the tableView
     */
    private void addPhotoToTable(TableColumn tableColumn) {
        tableColumn.setCellFactory(param -> {
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
        tableColumn.setCellValueFactory(new PropertyValueFactory<User, String>("pathToPicture"));
    }

    private void initializeTableView() {
        updateData(0);
        tableViewUsers.setVisible(true);
        tableViewEvents.setVisible(false);
        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                tableViewUsers.getSelectionModel().clearSelection();
                page = 0;
                updateData(0);
            }
        });

        tableViewUsers.setItems(users);
    }

    private boolean updateData(int  from) {
        if(from < 0)  return  false;
        leftButton.setVisible(page!=0);
        rightButton.setVisible(false);
        if (showUsers.isSelected()) {
            tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
            tableColumnLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
            addPhotoToTable(tableColumnPhoto);

            List<User> list = new ArrayList<>();
            for (User f : service.findAllEntities(from,to)) {list.add(f);}
            if(list.size()==0)
                return false;

            Predicate<User> p1 = n -> n.getFirstName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());
            Predicate<User> p2 = n -> n.getLastName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());
            Predicate<User> p3 = n -> !n.getId().equals(user);

            indexNextPage = from + 5;

            if (searchTextField.getText().length() > 0) {
                rightButton.setVisible(false);
                list = list.stream()
                        .filter((p1.or(p2)).and(p3))
                        .collect(Collectors.toList());
                if(list.size() != 0 ) {
                    users.setAll(list);
                    rightButton.setVisible(true);
                }
                else if(page == 0)
                    users.setAll(list);
                while (indexNextPage<maxUser) {
                    if (users.size() != to) {
                        for (User f : service.findAllEntities(indexNextPage, to - users.size())) {
                            list.add(f);
                        }
                        indexNextPage += to - users.size();
                        list = list.stream()
                                .filter((p1.or(p2)).and(p3))
                                .collect(Collectors.toList());
                        if (list.size() != 0 || page == 0)
                            users.setAll(list);
                    } else {
                        rightButton.setVisible(true);
                        return true;
                    }
                }
                if(page==0)
                    users.setAll(list);
                rightButton.setVisible(false);
                return list.size() != 0;
            } else {
                users.setAll(list.stream()
                        .filter(p3)
                        .collect(Collectors.toList()));
                if(users.size()!=to) {
                    for (User f : service.findAllEntities(indexNextPage, 1)) {
                        list.add(f);
                        indexNextPage ++;
                    }
                    users.setAll(list.stream()
                            .filter((p1.or(p2)).and(p3))
                            .collect(Collectors.toList()));
                }
                rightButton.setVisible(indexNextPage < maxUser);
            }
        } else if(showEvents.isSelected()){
            tableColumnName.setCellValueFactory(new PropertyValueFactory<Event, String>("name"));
            tableColumnLocation.setCellValueFactory(new PropertyValueFactory<Event, String>("location"));
            addPhotoToTable(tableColumnPhoto1);

            List<Event> list = new ArrayList<>();
            for (Event f : service.findAllEvents(from,to)) {
                list.add(f);
            }
            if(list.size()==0)
                return false;
            Predicate<Event> p1 = n -> n.getName().toLowerCase().startsWith(searchTextField.getText().toLowerCase());
            indexNextPage = from + 5;
            if (searchTextField.getText().length() > 0) {
                rightButton.setVisible(false);
                list = list.stream()
                        .filter(p1)
                        .collect(Collectors.toList());
                if(list.size()!=0) {
                    rightButton.setVisible(true);
                    events.setAll(list);
                }
                else if(page == 0)
                    events.setAll(list);
                while(indexNextPage < maxEvent){
                        if(events.size()!=to){
                            for (Event f : service.findAllEvents(indexNextPage, to-events.size()) ) {
                                list.add(f);
                            }
                            indexNextPage += to -  events.size();
                            list = list.stream()
                                    .filter(p1)
                                    .collect(Collectors.toList());
                            if(list.size() !=0)
                                events.setAll(list);
                        }
                        else {
                            rightButton.setVisible(true);
                            return true;
                        }
                }
                if(page==0)
                    events.setAll(list);
                rightButton.setVisible(false);
                return list.size()!=0;
            }
            else {
                rightButton.setVisible(indexNextPage < maxEvent);
                events.setAll(list);
            }
        }
        return true;
    }

    /**
     * Function that sets the onClick action of tableView cells
     */
    private void setTableSelection(){
        tableViewEvents.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("one-event-view.fxml"));
                    Parent dashboard = null;
                    try {
                        dashboard = fxmlLoader.load();
                        Node oldRegion = mainPane.getChildren().set(0, dashboard);
                        Map<Object, Object> properties = dashboard.getProperties();
                        oldRegion.getProperties().forEach(properties::putIfAbsent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    OneEventController controller = fxmlLoader.getController();
                    controller.settings(user, service, newSelection, "AllEvents");
                    mainPane.getChildren().setAll(dashboard);
            }
        });
        tableViewUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("page-view.fxml"));
                Parent dashboard = null;
                try {
                    dashboard = fxmlLoader.load();
                    Node oldRegion = mainPane.getChildren().set(0, dashboard);
                    Map<Object, Object> properties = dashboard.getProperties();
                    oldRegion.getProperties().forEach(properties::putIfAbsent);
                    UserPageController controller = fxmlLoader.getController();
                    controller.settings(newSelection.getId(), service, false);
                    mainPane.getChildren().setAll(dashboard);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void leftButtonAction(ActionEvent actionEvent) {
        page--;
        if(!updateData(page * 5))
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

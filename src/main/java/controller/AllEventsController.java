package controller;

import domain.Event;
import domain.TableViewClass;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import service.SocialNetworkService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AllEventsController {

    public TableView<Event> tableViewEvents;
    public TableColumn tableColumnPicture;
    public TableColumn tableColumnName;
    public TableColumn tableColumnStartDate;
    public TableColumn tableColumnEndDate;
    public AnchorPane mainPane;

    ObservableList<Event> modelEvents = FXCollections.observableArrayList();
    private String user;
    private SocialNetworkService service;
    private boolean createdEvents;

    /**
     * Function that makes the settings required before opening the window
     * @param user - the logged user
     * @param service - the service that we use
     */
    public void settings(String user, SocialNetworkService service, boolean created) {
        this.user = user;
        this.service = service;
        this.createdEvents = created;
        initializeTableView();
        setTableSelection();
    }

    private void initializeTableView(){
        List<Event> list = new ArrayList<>();
        for(Event event: service.findAllEvents())
            list.add(event);
        if(createdEvents){
            list = list.stream()
                    .filter(x -> x.getCreator().getId().equals(user))
                    .collect(Collectors.toList());
        }
        modelEvents.setAll(list);
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        tableColumnEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        addPhotoToTable();
        tableViewEvents.setItems(modelEvents);

    }

    private void setTableSelection(){
        tableViewEvents.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if(createdEvents){
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("add-event-view.fxml"));
                    Parent dashboard = null;
                    try {
                        dashboard = fxmlLoader.load();
                        Node oldRegion = mainPane.getChildren().set(0, dashboard);
                        Map<Object, Object> properties = dashboard.getProperties();
                        oldRegion.getProperties().forEach(properties::putIfAbsent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    NewEventController controller = fxmlLoader.getController();
                    controller.settings(user, service, newSelection);
                    mainPane.getChildren().setAll(dashboard);
                } else {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("one-event-view.fxml"));
                    Parent dashboard = null;
                    try {
                        dashboard = fxmlLoader.load();
                        Node oldRegion = mainPane.getChildren().set(0, dashboard);
                        Map<Object, Object> properties = dashboard.getProperties();
                        oldRegion.getProperties().forEach(properties::putIfAbsent);
                        OneEventController controller = fxmlLoader.getController();
                        controller.settings(user, service, newSelection, "AllEvents");
                        mainPane.getChildren().setAll(dashboard);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * Function that adds a photo to the tableView
     */
    private void addPhotoToTable() {
        tableColumnPicture.setCellFactory(param -> {
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
        tableColumnPicture.setCellValueFactory(new PropertyValueFactory<User, String>("pathToPicture"));
    }
}

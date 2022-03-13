package controller;

import domain.Event;
import domain.TableViewClass;
import domain.User;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
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
import java.util.Map;
import java.util.stream.Collectors;

public class YourEventsController {
    public TableView<Event> tableViewEvents;
    public TableColumn tableColumnButton;
    public TableColumn tableColumnPicture;
    public TableColumn tableColumnName;
    public TableColumn tableColumnNotified;
    public AnchorPane mainPane;

    ObservableList<Event> modelEvents = FXCollections.observableArrayList();
    private String user;
    private SocialNetworkService service;

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

    private List<Event> getEvents(){
        return service.getParticipants().stream()
                .filter(x -> x.first.getId().equals(user))
                .map(x -> x.second.first)
                .collect(Collectors.toList());
    }

    private void initializeTableView(){
        modelEvents.setAll(getEvents());
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        addPhotoToTable();
        addButtonToTable();
        addCheckBoxToTable();
        tableViewEvents.setItems(modelEvents);

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

    /**
     * Function that adds button to the tableview
     */
    private void addButtonToTable() {
        Callback<TableColumn<Event, Void>, TableCell<Event, Void>> cellFactory
                = new Callback<TableColumn<Event, Void>, TableCell<Event, Void>>() {
            @Override
            public TableCell<Event, Void> call(final TableColumn<Event, Void> param) {
                final TableCell<Event, Void> cell = new TableCell<Event, Void>() {

                    private final Button btn = new Button("Unsubscribe");
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        btn.getStyleClass().add("buttons");
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            btn.setOnAction((ActionEvent event) -> {
                                Event event1 =  getTableView().getItems().get(getIndex());
                                service.deleteParticipant(event1.getId(), user);
                                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Deleted", "Event deleted!");
                                initializeTableView();
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
                controller.settings(user, service, newSelection, "YourEvents");
                mainPane.getChildren().setAll(dashboard);
            }
        });
    }

    /**
     * Function that adds the accept request button to the tableview
     */
    private void addCheckBoxToTable() {
        tableColumnNotified.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Event, CheckBox>, ObservableValue<CheckBox>>() {
            @Override
            public ObservableValue<CheckBox> call(
                    TableColumn.CellDataFeatures<Event, CheckBox> arg0) {
                Event event = arg0.getValue();
                CheckBox checkBox = new CheckBox();
                checkBox.selectedProperty().setValue(service.getParticipant(event.getId(), user).second.second);

                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    public void changed(ObservableValue<? extends Boolean> ov,
                                        Boolean old_val, Boolean new_val) {
                        service.updateParticipant(event.getId(), user, new_val);
                    }
                });
                return new SimpleObjectProperty<CheckBox>(checkBox);
            }
        });
    }
}

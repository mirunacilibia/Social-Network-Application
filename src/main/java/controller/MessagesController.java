package controller;
import domain.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import service.SocialNetworkService;
import utils.events.MessageChangeEvent;
import utils.observer.Observer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MessagesController implements Observer<MessageChangeEvent> {
    @FXML
    public TableColumn tableColumnPhoto;
    @FXML
    AnchorPane mainPane;
    @FXML
    public TableColumn tableColumnName;
    @FXML
    public TableColumn tableColumnMessage;
    @FXML
    public TableView<LastConversation> tableViewMessages;
    ObservableList<LastConversation> model = FXCollections.observableArrayList();
    private SocialNetworkService service;
    private String user;
    Group group = null;
    private List<LastConversation> messageList = null;

    /**
     * Function set all data
     * @param user User
     * @param service SocialNetworkService
     */
    public void settings(String user, SocialNetworkService service) throws IOException {
        this.user = user;
        this.service = service;
        initializeData();
        setTableSelection();
        System.out.println(getClass());
        service.addObserverMessages(this,user + getClass().toString());
    }

    /**
     * Function that adds a photo to the tableView
     */
    private void addPhotoToTable(){
        tableColumnPhoto.setCellFactory(param -> {
            final Circle circle = new Circle(250, 250, 120);
            TableCell<LastConversation, String> cell = new TableCell<LastConversation, String>() {
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
        tableColumnPhoto.setCellValueFactory(new PropertyValueFactory<>("pathToPicture"));
    }

    /**
     * Function sets what is happened when we select a item from table
     */
    private void setTableSelection() {
        tableViewMessages.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("chat-view.fxml"));
                Parent dashboard = null;
                try {
                    dashboard = fxmlLoader.load();
                    Node oldRegion = mainPane.getChildren().set(0, dashboard);
                    Map<Object, Object> properties = dashboard.getProperties();
                    oldRegion.getProperties().forEach(properties::putIfAbsent);
                    ChatController controller = fxmlLoader.getController();
                    controller.settings(user, service, newSelection);
                    mainPane.getChildren().setAll(dashboard);
                    controller.setScrolling();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Function initialize data to table
     */
    public void initializeData(){
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("chatGroup"));
        tableColumnMessage.setCellValueFactory(new PropertyValueFactory<>("messageViewed"));
        addPhotoToTable();
        getMessageList();
        model.setAll(messageList);
        tableViewMessages.setItems(model);
    }

    /**
     * Function set the groups
     */
    private void getMessageList() {
        if(group!=null) {
            return ;
        }
        service.setGroups(user);
        group = service.getGroups();
        List<LastConversation> l = new ArrayList<>();
        for(List<User> u: group.keySet()){
            List<Message> messages = group.get(u);
            LastConversation last = new LastConversation(messages.get(messages.size()-1),user);
            last.setMessageId(messages);
            l.add(last);
        }
        messageList =  l.stream()
                .sorted(Comparator.comparing(LastConversation::getData).reversed())
                .toList();
    }

    /**
     * Function is called when a message is received from a user
     * @param messageChangeEvent for observable
     */
    @Override
    public void update(MessageChangeEvent messageChangeEvent) {
        group = null;
        initializeData();
        //TODO
    }
}

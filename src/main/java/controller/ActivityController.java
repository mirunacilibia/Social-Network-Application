package controller;

import domain.*;
import domain.validators.ValidatorDates;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import service.SocialNetworkService;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ActivityController {
    public Button buttonGeneratePDFReport;
    public DatePicker StartDate;
    public DatePicker EndDate;
    public Label reportLabel;
    public PieChart pieChartFriendships;
    public PieChart pieChartMessages;
    public TextField searchTextField;
    public TableColumn tableColumnPhoto;
    public TableColumn tableColumnFirstName;
    public TableColumn tableColumnLastName;
    public TableView tableViewUsers;
    public AnchorPane mainPane;
    public Button leftButton;
    public Button rightButton;
    private ObservableList<User> filteredUsers = FXCollections.observableArrayList();
    private List<Message> messageList = new ArrayList<>();
    private List<Friendship> friendshipList = new ArrayList<>();
    private String user;
    private User friend;
    private SocialNetworkService service;
    private LocalDate startDate;
    private LocalDate endDate;
    private int indexNextPage = 3;
    private int page = 0;
    private int maxUser = 0;
    private int to = 3;

    /**
     * Function initialize data
     * @param user User
     * @param service  SocialNetworkService
     */
    public void settings(String user, SocialNetworkService service) {
        this.user = user;
        this.service = service;
        setMax();
        initializeTableView();
    }
    /**
     * Function that adds a photo to the tableView
     */
    private void addPhotoToTable(TableColumn tableColumn){
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

    @FXML
    /**
     * Function initialize tableProperty
     */
    private void initialize() {
        reportLabel.setVisible(false);
        tableViewUsers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                reportLabel.setVisible(true);
                friend = (User) tableViewUsers.getSelectionModel().getSelectedItem();
                reportLabel.setText("       Report for "+ " "+  friend.getFirstName()+ " "+ friend.getLastName());
                ImageView myImageView = new ImageView(friend.getPathToPicture());
                myImageView.setFitHeight(60);
                myImageView.setFitWidth(100);
                myImageView.setPreserveRatio(true);
                pieChartFriendships.setVisible(false);
                pieChartMessages.setVisible(false);
                reportLabel.setGraphic(myImageView);
            }
        });
        tableViewUsers.setRowFactory(new Callback<TableView<User>, TableRow<User>>() {
            @Override
            public TableRow<User> call(TableView<User> tableView2) {
                final TableRow<User> row = new TableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (index >= 0 && index < tableViewUsers.getItems().size() && tableViewUsers.getSelectionModel().isSelected(index)  ) {
                            tableViewUsers.getSelectionModel().clearSelection();
                            friend=null;
                            reportLabel.setText("");
                            reportLabel.setGraphic(null);
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });
    }

    /**
     * Function set de number of all users
     */
    private void setMax() {
        List<User> list =   new ArrayList<>();
        for(User u: service.findAllEntities()){
            list.add(u);
        }
        maxUser = list.size();
    }

    /**
     * Function initialize tableView data
     */
    private void initializeTableView(){
        updateData(0);
        leftButton.setVisible(false);
        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable,
                                String oldValue, String newValue) {
                tableViewUsers.getSelectionModel().clearSelection();
                leftButton.setVisible(false);
                page = 0;
                indexNextPage = 3;
                updateData(0);
            }
        });

        tableViewUsers.setItems(filteredUsers);
    }

    /**
     * Function load data from dataBase to tableView
     * @param from position from where we extract the users
     * @return true if we extract more than one users, or false otherwise
     */
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

    /**
     * Function generate de PDF
     * @param actionEvent ActionEvent - ButtonClicked
     */
    public void generatePDF(ActionEvent actionEvent) {
        reportLabel.setVisible(true);
        startDate = StartDate.getValue();
        endDate = EndDate.getValue();
        if (!ValidatorDates.validateDates(startDate, endDate)) {
            MessageAlert.showErrorMessage(null,"Invalide dates");
            return;
        }
        if(reportLabel.getText().isEmpty())
            fullReport(startDate,endDate,actionEvent);
        else
            friendReport(startDate,endDate,actionEvent);
    }

    /**
     * Functioc create de report for a friend
     * @param dateStart LocalDate
     * @param dateEnd LocalDate
     * @param actionEvent ActionEvent - ButtonClicked
     */
    private void friendReport(LocalDate dateStart, LocalDate dateEnd, ActionEvent actionEvent) {
        messageList = service.getListAllMessagesForUsersTimeInterval(user,friend.getId(), dateStart, dateEnd);
        friendshipList = new ArrayList<>();

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Save location");
        File selectedDirectory = chooser.showDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        String path = selectedDirectory.getAbsolutePath();
        path = path.concat("\\"+ "FullReport"+friend.getFirstName()+ friend.getLastName()+ ".pdf");

        try {
            createPdf("Full Report for "+friend.getFirstName()+" " + friend.getLastName()+ " from period  ",path,true);

        } catch (IOException e) {
            MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Report",e.getMessage());
        }
    }

    /**
     * Functioc create a report full report for period specified
     * @param dateStart LocalDate
     * @param dateEnd LocalDate
     * @param actionEvent ActionEvent - ButtonClicked
     */
    private void fullReport(LocalDate dateStart, LocalDate dateEnd, ActionEvent actionEvent) {
        messageList = service.getListAllMessagesToUserTimeInterval(user, dateStart, dateEnd);
        friendshipList = service.getListAllFriendshipsUserTimeInterval(user, dateStart, dateEnd);

        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Save location");
        File selectedDirectory = chooser.showDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        if(selectedDirectory==null)
            return;
        String path = selectedDirectory.getAbsolutePath();
        path = path.concat("\\"+ "FullReport"+ ".pdf");

        try {
            createPdf("Your full activity from ",path,false);

        } catch (IOException e) {
            MessageAlert.showMessage(null,Alert.AlertType.INFORMATION,"Report",e.getMessage());
        }
    }


    /**
     * Function builds the PDF
     * @param mess Title
     * @param path path where we save the PDF
     * @param forFriend represent if the PDF is for a friend or a fullReport
     * @throws IOException if is a problem
     */
    private void createPdf( String mess,String path,Boolean forFriend) throws IOException {
        PDFont font = PDType1Font.HELVETICA;
        float fontSize = 18;
        float fontHeight = 18;
        float leading = 18;

        PDDocument newPdf = new PDDocument();
        PDPage page = new PDPage();
        newPdf.addPage(page);

        PDPageContentStream contentStream = new PDPageContentStream(newPdf, page);
        contentStream.setFont(font, fontSize);

        float yCordinate = page.getCropBox().getUpperRightY() - 30;
        float startX = page.getCropBox().getLowerLeftX() + 30;
        float endX = page.getCropBox().getUpperRightX() - 30;

        contentStream.beginText();
        contentStream.newLineAtOffset(startX, yCordinate);
        contentStream.showText(mess +  startDate + " : " + endDate );
        yCordinate -= fontHeight;

        contentStream.newLineAtOffset(0, yCordinate);
        yCordinate -= leading;
        contentStream.showText("Messages");
        yCordinate -= fontHeight;
        contentStream.endText();
        contentStream.moveTo(startX, yCordinate);
        contentStream.lineTo(endX, yCordinate);
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, yCordinate);
        contentStream.showText("Messages");
        yCordinate -= leading;
        yCordinate -= leading;
        contentStream.endText();
        contentStream.beginText();
        contentStream.newLineAtOffset(startX, yCordinate);
        for(Message message: messageList) {
            String messageDay = message.getDate().getDayOfYear()+":"+ message.getDate().getMonthValue()+":"+ message.getDate().getYear();
            contentStream.showText(message.getFrom().getFirstName()+" "+message.getFrom().getLastName()+": "+ message.getMessage()+ " from "+ messageDay);
            yCordinate -= fontHeight;
            contentStream.endText();
            contentStream.moveTo(startX, yCordinate);
            contentStream.lineTo(endX, yCordinate);
            yCordinate -= leading;
            contentStream.beginText();
            contentStream.newLineAtOffset(startX, yCordinate);
        }
        yCordinate -= fontHeight;
        if(!forFriend) {
            contentStream.showText("Friendships");
            yCordinate -= fontHeight;
            contentStream.endText();

            contentStream.moveTo(startX, yCordinate);
            contentStream.lineTo(endX, yCordinate);

            contentStream.beginText();
            contentStream.newLineAtOffset(startX, yCordinate);

            for (Friendship friendship : friendshipList) {
                String Day = friendship.getDate().getDayOfMonth() + ":" + friendship.getDate().getMonthValue() + ":" + friendship.getDate().getYear();
                User user_1 = service.findOneEntity(friendship.getId().first);
                User user_2 = service.findOneEntity(friendship.getId().second);
                contentStream.showText(user_1.getFirstName()+" "+user_1.getLastName() + " - " + user_2.getFirstName()+" "+user_2.getLastName() + " from " + Day);
                yCordinate -= fontHeight;
                contentStream.endText();
                contentStream.moveTo(startX, yCordinate);
                contentStream.lineTo(endX, yCordinate);
                yCordinate -= leading;

                contentStream.beginText();
                contentStream.newLineAtOffset(startX, yCordinate);
            }
        }
        contentStream.endText();
        contentStream.close();
        newPdf.save(path);
        newPdf.close();
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Report Export to ptf");
        alert.show();
    }

    /**
     * Function set the pieCharts if dates are valid
     */
    public void eventShowGraphs() {

        startDate = StartDate.getValue();
        endDate = EndDate.getValue();
        pieChartFriendships.setVisible(true);
        pieChartMessages.setVisible(true);
        if(!ValidatorDates.validateDates(startDate,endDate)){
            pieChartMessages.setTitle("");
            pieChartMessages.setData(FXCollections.emptyObservableList());
            pieChartFriendships.setTitle("");
            pieChartFriendships.setData(FXCollections.emptyObservableList());
            MessageAlert.showErrorMessage(null,"Introduce a startDate and endDate");
            return;
        }
        if(friend==null) {
            reportLabel.setVisible(true);
            reportLabel.setText("                                 Preview full report");
            List<Message> messages = service.getListAllMessagesToUserTimeInterval(user, startDate, endDate);
            Map<String, Integer> friendshipList = service.getFriendshipToUserYear(user,startDate,endDate);
            if(messages.size()==0 ){
                for(String key : friendshipList.keySet())
                    if(friendshipList.get(key)!=0){
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "No messages in the selected period");
                        alert.show();
                        setPieChart(pieChartFriendships,friendshipList, "friendships");
                        return;
                    }
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No messages and new friendships in the selected period");
                alert.show();
                return;
            }
            else if(friendshipList.size()==0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No new friendship in the selected period");
                alert.show();
                setPieChart(pieChartMessages, service.getMessagesMap(messages),"messages");
            }
            setPieChart(pieChartMessages, service.getMessagesMap(messages),"messages");
            setPieChart(pieChartFriendships,friendshipList, "friendships");
        }
        else{
            reportLabel.setVisible(true);
            reportLabel.setText ("       Preview for " + friend.getFirstName()+" " + friend.getLastName());
            List<Message> messages = service.getListAllMessagesForUsersTimeInterval(user,friend.getId(), startDate, endDate);
            if(messages.size()==0 ){
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "No messages with " + friend.getFirstName()+
                        " "+ friend.getLastName() + " in the selected period");
                alert.show();
                return;
            }
            setPieChart(pieChartMessages, service.getMessagesMap(messages), "messages");
            pieChartFriendships.setVisible(false);
        }

    }
    /**
     * Function set the pieCharts
     */
    private void setPieChart(PieChart pieChart, Map<String, Integer> mapData, String entitiesString) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        mapData.entrySet().removeIf(entry -> entry.getValue() == 0); // Remove the months with 0 entities
        mapData.keySet().forEach(key -> {
            int number = mapData.get(key);
            String text = key + ": " + number + " " + entitiesString;
            pieChartData.add(new PieChart.Data(text, number));
        });
        if (mapData.keySet().size() == 0) {
            pieChart.setTitle("");
            pieChart.setData(FXCollections.emptyObservableList());
        } else {
            pieChart.setData(pieChartData);
            pieChart.setClockwise(true);
            pieChart.setStartAngle(180);
            pieChart.setLabelLineLength(20);
            pieChart.setTitle(entitiesString.substring(0, 1).toUpperCase() + entitiesString.substring(1)   );
        }
    }

    /**
     * Function create pieChart is the dates are valid
     * @param actionEvent buttonClicked
     */
    public void PreviewButton(ActionEvent actionEvent) {
        eventShowGraphs();
    }

    /**
     * Function move the tableView to next page
     * @param actionEvent buttonClicked
     */
    public void leftButtonAction(ActionEvent actionEvent) {
        page--;
        if(!updateData(page * to))
            page++;
        leftButton.setVisible(page!=0);
    }

    /**
     * Function move the tableView to previous page
     * @param actionEvent buttonClicked
     */
    public void rightButtonAction(ActionEvent actionEvent) {
        page++;
        if(!updateData(indexNextPage))
            page--;
        leftButton.setVisible(page!=0);
    }
}
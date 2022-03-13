package domain;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Tuple;

import java.time.LocalDate;

public class TableViewClass {

    String firstName;
    String lastName;
    Status status;
    LocalDate date;
    Image image;
    Tuple<String, String> id;

    public TableViewClass(Tuple<String, String> id, String firstName, String lastName, Status status, LocalDate date, String path) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.date = date;
        this.image = new Image(path);
    }

    public Image getImage() {
        return image;
    }

    public Tuple<String, String> getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDate getDate() {
        return date;
    }
}

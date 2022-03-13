package domain;

import utils.Tuple;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class Friendship extends Entity<Tuple<String, String>>{
    Status status;
    LocalDate date;
    /**
     * Constructor
     * status has the initial value of PENDING
     */
    public Friendship() {
        status = Status.PENDING;
        date = null;
    }

    /**
     * Getter - status
     * @return - the status of the friendship
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Setter - status
     * @param status - the new status of the friendship
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Getter - date
     * @return - the date of the friendship
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Setter - date
     * @param date - the new date of the friendship
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Override of the toString function
     * @return - a String that contains the friendship
     */
    @Override
    public String toString() {
        String string = "";
        if(getStatus() == Status.ACCEPTED)
            string = getDate().toString() + " -> ";
        string+= this.getStatus().toString() + " friendship between " + this.getId().first + " and " + this.getId().second;
        return string;
    }
}

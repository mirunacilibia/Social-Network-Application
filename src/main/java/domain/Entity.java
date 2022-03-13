package domain;

import java.io.Serializable;
import java.util.Objects;

public class Entity<ID> implements Serializable {

    private ID id;

    /**
     * Getter for the id
     * @return the id, of generic type ID
     */
    public ID getId() {
        return id;
    }

    /**
     * Setter for the id
     * @param id - generic type ID, the new id
     */
    public void setId(ID id) {
        this.id = id;
    }

    /**
     * Function that implements the == operation
     * @param o - Object
     * @return - true - o is equal to the instance of the class
     *         - false - o is not equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

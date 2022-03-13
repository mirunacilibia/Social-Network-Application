package utils;

import java.util.Objects;

public class Tuple<X, Y> {

    public final X first;
    public final Y second;

    /**
     * Constructor
     * @param x - generic type X
     * @param y - generic type Y
     */
    public Tuple(X x, Y y) {
        this.first = x;
        this.second = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        if(Objects.equals(first, tuple.second) && Objects.equals(second, tuple.first))
            return true;
        if(Objects.equals(first, tuple.first) && Objects.equals(second, tuple.second))
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(first) + Objects.hash(second);
    }
}

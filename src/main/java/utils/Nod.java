package utils;
import service.Color;
import java.util.HashMap;

public class Nod<E> {

    public E parent;
    public E index;
    public int distance;
    public HashMap<E, Nod<E>> nodes = new HashMap<>();
    public Color color;

    /**
     * Constructor
     * @param index - generic type E
     * @param parent - generic type E
     * @param distance - integer, the distance from the root
     * @param color - enum Color, color that we use for bfs
     */
    public Nod( E index, E parent, int distance, Color color) {
        this.parent = parent;
        this.index = index;
        this.distance = distance;
        //this.nodes = nodes;
        this.color = color;
    }

    /**
     * Cosntructor
     * @param i - generic type E, the index
     */
    public Nod(E i){
        parent = null;
        distance = -1;
        nodes.put(i, this);
        color = Color.WHITE;
        index = i;
    }
}

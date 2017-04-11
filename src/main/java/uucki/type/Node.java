package uucki.type;

public class Node<T> {
    public volatile int score = 0;
    public volatile int plays = 0;
    public T item = null;
    public FieldValue color = null;

    public Node(T item, FieldValue color) {
        this.item = item;
        this.color = color;
    }
}

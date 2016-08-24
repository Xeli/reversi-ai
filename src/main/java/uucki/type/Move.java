package uucki.type;

public class Move {

    public int row = 0;
    public int column = 0;
    public FieldValue value;

    public Move(int row, int column, FieldValue value) {
        this.row = row;
        this.column = column;
        this.value = value;
    }

    public Move(Position position, FieldValue value) {
        this(position.row, position.column, value);
    }

    public String toString() {
        return "(" + row + ", " + column + ")";
    }
}

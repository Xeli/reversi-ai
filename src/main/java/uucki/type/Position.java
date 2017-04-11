package uucki.type;

import uucki.type.Move;

public class Position {
    public int row    = 0;
    public int column = 0;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public Position(Move move) {
        this.row = move.row;
        this.column = move.column;
    }

    public String toString() {
        return "(" + row + ", " + column + ")";
    }

    public boolean equals(Object o) {
        return o instanceof Position && hashCode() == o.hashCode();
    }

    public int hashCode() {
        return row + column * 10;
    }

    public Object clone() {
        return new Position(row, column);
    }

    public boolean isCorner() {
        return (row == 0 && column == 0) ||
               (row == 7 && column == 0) ||
               (row == 0 && column == 7) ||
               (row == 7 && column == 7);
    }
}

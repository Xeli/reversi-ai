package uucki.type;

public enum FieldValue {
    EMPTY, BLACK, WHITE;

    public String toString() {
        switch(this) {
            default:
            case EMPTY:
                return " ";
            case BLACK:
                return "B";
            case WHITE:
                return "W";
        }
    }
}

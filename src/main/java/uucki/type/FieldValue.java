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

    public FieldValue getOpponent() {
        switch(this) {
            case BLACK:
                return FieldValue.WHITE;
            case WHITE:
                return FieldValue.BLACK;
            default:
            case EMPTY:
                return null;
        }
    }
}

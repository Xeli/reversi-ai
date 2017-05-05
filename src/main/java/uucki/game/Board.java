package uucki.game;

import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;

import java.util.*;

public abstract class Board {
    public static int ROW_COUNT = 0;
    public static int COLUMN_COUNT = 0;
    public FieldValue[] board = null;

    public int whites = 0;
    public int blacks = 0;

    protected List<Position> possibleWhitePositions = null;
    protected List<Position> possibleBlackPositions = null;

    public abstract Board makeMove(Move move);
    public abstract List<Position> getPossiblePositions(FieldValue color);
    public abstract void setFieldValue(Position position, FieldValue color);

    public abstract boolean isFinished();
    public abstract FieldValue getWinner();

    public void setFieldValue(int row, int column, FieldValue color) {
        possibleBlackPositions = null;
        possibleWhitePositions = null;
        FieldValue oldColor = board[row * COLUMN_COUNT + column];
        if(oldColor == color) {
            return;
        }
        if(color == FieldValue.WHITE) {
            whites++;
            if(oldColor == FieldValue.BLACK) {
                blacks--;
            }
        } else {
            blacks++;
            if(oldColor == FieldValue.WHITE) {
                whites--;
            }
        }
        board[row * COLUMN_COUNT + column] = color;
    }

    public void print() {
        System.out.print("=");
        for(int column = 0; column < COLUMN_COUNT; column++) {
            System.out.print(column);
        }
        for(int i = 0; i < board.length; i++) {
            if(i % COLUMN_COUNT == 0) {
                System.out.print("=\n=");
            }

            System.out.print(board[i] == null ? " " : board[i]);
        }
        System.out.println("=");
        System.out.println(new String(new char[COLUMN_COUNT + 2]).replace("\0", "="));
    }

    public int emptyFields() {
        return (ROW_COUNT * COLUMN_COUNT) - (whites + blacks);
    }


    public FieldValue getFieldValue(int row, int column) {
        return board[row * ROW_COUNT + column];
    }

    public FieldValue getFieldValue(Position position) {
        return board[position.row * 8 + position.column];
    }

    protected boolean validPosition(Position position) {
        return position.row >= 0 &&
               position.column >= 0 &&
               position.row < ROW_COUNT &&
               position.column < COLUMN_COUNT;

    }

    public int hashCode() {
        return Arrays.hashCode(board);
    }

    public boolean equals(Object o) {
        if(!(o instanceof Board)) {
            return false;
        }
        return o.hashCode() == hashCode();
    }
}

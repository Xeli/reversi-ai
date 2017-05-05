package uucki.game.fourinarow;

import uucki.game.fourinarow.Board;
import uucki.type.FieldValue;
import uucki.type.Position;

import java.util.*;

class PossibleMoves{

    public static void printBoard(Board board, FieldValue color) {
        List<Position> validPositions = getValidPositions(board, color);

        System.out.print("=");
        for(int column = 0; column < 8; column++) {
            System.out.print(column);
        }
        System.out.println();
        for(int row = 0; row < 8; row++) {
            System.out.print(row);
            for(int column = 0; column < 8; column++) {
                Position pos = new Position(row, column);
                if(validPositions.contains(pos)) {
                    System.out.print("o");
                } else {
                    System.out.print(board.getFieldValue(pos));
                }
            }
            System.out.println("=");
        }
        System.out.println(new String(new char[10]).replace("\0", "="));
    }

    public static List<Position> getValidPositions(Board board, FieldValue color) {
        List<Position> validPositions = new ArrayList<Position>();
        for(int column = 0; column < Board.COLUMN_COUNT; column++) {
            Position position = null;
            int row = Board.ROW_COUNT-1;
            while(position == null && row >= 0) {
                FieldValue currentValue = board.getFieldValue(row, column);
                if(currentValue == FieldValue.EMPTY || currentValue == null) {
                    position = new Position(row, column);
                }
                row--;
            }
            if(position != null) {
                validPositions.add(position);
            }
        }

        return validPositions;
    }
}

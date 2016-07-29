package uucki.game.reversi;

import uucki.game.reversi.Board;
import uucki.type.FieldValue;
import uucki.type.Position;

import java.util.*;

public class PossibleMoves{

    public static void printBoard(Board board, FieldValue color) {
        List<Position> validPositions = getValidPositions(board, color);

        System.out.print("=");
        for(int column = 0; column < board.board[0].length; column++) {
            System.out.print(column);
        }
        System.out.println();
        for(int row = 0; row < board.board.length; row++) {
            System.out.print(row);
            for(int column = 0; column < board.board[row].length; column++) {
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
        for(int row = 0; row < 8; row++) {
            for(int column = 0; column < 8; column++) {
                Position position = new Position(row, column);
                if(board.getFieldValue(position) != FieldValue.EMPTY) {
                    continue;
                }
                int swapCount = board.swapFields(position, color, true);
                if(swapCount > 0) {
                    validPositions.add(position);
                }
            }
        }

        return validPositions;
    }
}

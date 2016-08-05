package uucki.game.reversi;

import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;

import java.util.*;

public class Board{

    public FieldValue[] board = new FieldValue[8*8];
    public int whites = 0;
    public int blacks = 0;

    public Board makeMove(Move move) {
        Board newBoard = (Board)clone();

        Position position = new Position(move);

        newBoard.setFieldValue(position, move.value);
        if(move.value == FieldValue.WHITE) {
            newBoard.whites++;
        } else {
            newBoard.blacks++;
        }

        newBoard.swapFields(position, move.value, false);

        return newBoard;
    }

    public int swapFields(Position position, FieldValue color, boolean countOnly) {
        int totalSwaps = 0;
        totalSwaps += swapFieldsInDirection(-1, -1, position, color, countOnly);
        totalSwaps += swapFieldsInDirection( 0, -1, position, color, countOnly);
        totalSwaps += swapFieldsInDirection( 1, -1, position, color, countOnly);
        totalSwaps += swapFieldsInDirection(-1,  0, position, color, countOnly);
        totalSwaps += swapFieldsInDirection( 1,  0, position, color, countOnly);
        totalSwaps += swapFieldsInDirection(-1,  1, position, color, countOnly);
        totalSwaps += swapFieldsInDirection( 0,  1, position, color, countOnly);
        totalSwaps += swapFieldsInDirection( 1,  1, position, color, countOnly);

        return totalSwaps;
    }

    private int swapFieldsInDirection(int rowDirection, int columnDirection, Position position, FieldValue color, boolean countOnly) {
        int swaps = 0;

        FieldValue opponentColor = FieldValue.WHITE == color ? FieldValue.BLACK : FieldValue.WHITE;
        Position currentPosition = (Position)position.clone();
        currentPosition.row += rowDirection;
        currentPosition.column += columnDirection;

        //there needs to be atleast one opponent stone to be swapped
        if(!Board.validPosition(currentPosition) || getFieldValue(currentPosition) != opponentColor) {
            return swaps;
        }

        //skip all other opponent stones
        while(Board.validPosition(currentPosition) && getFieldValue(currentPosition) == opponentColor) {
            currentPosition.row += rowDirection;
            currentPosition.column += columnDirection;
        }
        if(!Board.validPosition(currentPosition) || getFieldValue(currentPosition) != color) {
            return swaps;
        }

        currentPosition = (Position)position.clone();
        currentPosition.row += rowDirection;
        currentPosition.column += columnDirection;
        while(Board.validPosition(currentPosition) && getFieldValue(currentPosition) == opponentColor) {
            swaps++;
            if(!countOnly) {
                setFieldValue(currentPosition, color);
                if(color == FieldValue.WHITE) {
                    whites++;
                    blacks--;
                } else {
                    whites--;
                    blacks++;
                }
            }
            currentPosition.row += rowDirection;
            currentPosition.column += columnDirection;
        }

        return swaps;
    }

    public void setFieldValue(int row, int column, FieldValue color) {
        board[row * 8 + column] = color;
    }

    public void setFieldValue(Position position, FieldValue color) {
        board[position.row * 8 + position.column] = color;
    }

    public FieldValue getFieldValue(int row, int column) {
        return board[row * 8 + column];
    }

    public FieldValue getFieldValue(Position position) {
        return board[position.row * 8 + position.column];
    }

    public boolean isFinished() {
        if(emptyFields() == 0) {
            return true;
        }

        return PossibleMoves.getValidPositions(this, FieldValue.WHITE).size() + PossibleMoves.getValidPositions(this, FieldValue.BLACK).size() == 0;
    }

    public FieldValue getWinner() {
        return whites > blacks ? FieldValue.WHITE : FieldValue.BLACK;
    }

    public int emptyFields() {
        return 8*8 - (whites + blacks);
    }

    public void print() {
        System.out.print("=");
        for(int column = 0; column < 8; column++) {
            System.out.print(column);
        }
        System.out.println("=");
        for(int i = 0; i < board.length; i++) {
            if(i % 8 == 0) {
                System.out.print("=");
            }

            System.out.print(board[i] == null ? " " : board[i]);

            if(i % 8 == 0) {
                System.out.println("=");
            }
        }
        System.out.println(new String(new char[10]).replace("\0", "="));
    }

    public Object clone() {
        Board newBoard = new Board();
        newBoard.board = Arrays.copyOf(board, board.length);
        newBoard.whites = whites;
        newBoard.blacks = blacks;
        return newBoard;
    }

    public static boolean validPosition(Position position) {
        return position.row >= 0 &&
               position.column >= 0 &&
               position.row < 8 &&
               position.column < 8;

    }
}

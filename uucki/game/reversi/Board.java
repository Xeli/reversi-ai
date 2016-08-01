package uucki.game.reversi;

import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;

import java.util.*;

public class Board{

    public FieldValue[][] board = new FieldValue[8][8];
    public List<Position> whites = new ArrayList<Position>();
    public List<Position> blacks = new ArrayList<Position>();

    public Board(){
        for(int row = 0; row < board.length; row++) {
            for(int col = 0; col < board[row].length; col++) {
                board[row][col] = FieldValue.EMPTY;
            }
        }
    }

    public Board makeMove(Move move) {
        Board newBoard = (Board)clone();

        newBoard.board[move.row][move.column] = move.value;

        Position position = new Position(move);
        if(move.value == FieldValue.BLACK) {
            newBoard.blacks.add(position);
        } else if (move.value == FieldValue.WHITE) {
            newBoard.whites.add(position);
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
                board[currentPosition.row][currentPosition.column] = color;
                if(color == FieldValue.BLACK) {
                    whites.remove(currentPosition);
                    blacks.add(currentPosition);
                } else {
                    blacks.remove(currentPosition);
                    whites.add(currentPosition);
                }
            }
            currentPosition.row += rowDirection;
            currentPosition.column += columnDirection;
        }

        return swaps;
    }

    public FieldValue getFieldValue(int row, int column) {
        return board[row][column];
    }

    public FieldValue getFieldValue(Position position) {
        return board[position.row][position.column];
    }

    public boolean isFinished() {
        return whites.size() + blacks.size() == 8*8;
    }

    public FieldValue getWinner() {
        return whites.size() > blacks.size() ? FieldValue.WHITE : FieldValue.BLACK;
    }

    public void print() {
        System.out.print("=");
        for(int column = 0; column < board[0].length; column++) {
            System.out.print(column);
        }
        System.out.println("=");
        for(FieldValue[] row : board) {
            System.out.print("=");
            for(FieldValue field : row) {
                System.out.print(field);
            }
            System.out.println("=");
        }
        System.out.println(new String(new char[10]).replace("\0", "="));
    }

    public Object clone() {
        Board newBoard = new Board();
        for(int row = 0; row < board.length; row++) {
            newBoard.board[row] = Arrays.copyOf(board[row], board[row].length);
        }
        newBoard.whites = new ArrayList<Position>(whites);
        newBoard.blacks = new ArrayList<Position>(blacks);
        return newBoard;
    }

    public static boolean validPosition(Position position) {
        return position.row >= 0 &&
               position.column >= 0 &&
               position.row < 8 &&
               position.column < 8;

    }
}

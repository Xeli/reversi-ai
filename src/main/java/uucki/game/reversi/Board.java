package uucki.game.reversi;

import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;

import java.util.*;

public class Board extends uucki.game.Board {
    public boolean negativeWinner = false;

    public static double[][] weights = new double[][]{
        new double[]{ 6.21,  1.88,  12.4,  0.37,  0.37, 12.40,  1.88, 6.21 },
        new double[]{ 1.88, -1.00, -5.45, -1.40, -1.40, -5.45, -1.00, 1.88 },
        new double[]{ 12.4, -5.45,  0.03,  0.07,  0.07,  0.03, -5.45, 12.4 },
        new double[]{ 0.37, -1.40,  0.07, -1.32, -1.32,  0.07, -1.40, 0.37 },
        new double[]{ 0.37, -1.40,  0.07, -1.32, -1.32,  0.07, -1.40, 0.37 },
        new double[]{ 12.4, -5.45,  0.03,  0.07,  0.07,  0.03, -5.45, 12.4 },
        new double[]{ 1.88, -1.00, -5.45, -1.40, -1.40, -5.45, -1.00, 1.88 },
        new double[]{ 6.21,  1.88,  12.4,  0.37,  0.37, 12.40,  1.88, 6.21 },
    };

    public Board() {
        ROW_COUNT = 8;
        COLUMN_COUNT = 8;
        board = new FieldValue[ROW_COUNT * COLUMN_COUNT];
    }

    public Board makeMove(Move move) {
        Board newBoard = (Board)clone();
        Position position = new Position(move);
        newBoard.setFieldValue(position, move.value);
        newBoard.swapFields(position, move.value, false);

        newBoard.possibleWhitePositions = null;
        newBoard.possibleBlackPositions = null;
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

    public List<Position> getPossiblePositions(FieldValue color) {
        if(color == FieldValue.WHITE) {
            if(possibleWhitePositions == null) {
                possibleWhitePositions = PossibleMoves.getValidPositions(this, color);
            }
            return possibleWhitePositions;
        } else {
            if(possibleBlackPositions == null) {
                possibleBlackPositions = PossibleMoves.getValidPositions(this, color);
            }
            return possibleBlackPositions;
        }
    }

    private int swapFieldsInDirection(int rowDirection, int columnDirection, Position position, FieldValue color, boolean countOnly) {
        int swaps = 0;

        FieldValue opponentColor = FieldValue.WHITE == color ? FieldValue.BLACK : FieldValue.WHITE;
        Position currentPosition = (Position)position.clone();
        currentPosition.row += rowDirection;
        currentPosition.column += columnDirection;

        //there needs to be atleast one opponent stone to be swapped
        if(!validPosition(currentPosition) || getFieldValue(currentPosition) != opponentColor) {
            return swaps;
        }

        //skip all other opponent stones
        while(validPosition(currentPosition) && getFieldValue(currentPosition) == opponentColor) {
            currentPosition.row += rowDirection;
            currentPosition.column += columnDirection;
        }
        if(!validPosition(currentPosition) || getFieldValue(currentPosition) != color) {
            return swaps;
        }

        currentPosition = (Position)position.clone();
        currentPosition.row += rowDirection;
        currentPosition.column += columnDirection;
        while(validPosition(currentPosition) && getFieldValue(currentPosition) == opponentColor) {
            swaps++;
            if(!countOnly) {
                setFieldValue(currentPosition, color);
            }
            currentPosition.row += rowDirection;
            currentPosition.column += columnDirection;
        }

        return swaps;
    }

    public void setFieldValue(Position position, FieldValue color) {
        setFieldValue(position.row, position.column, color);
    }

    public boolean isFinished() {
        if(emptyFields() == 0) {
            return true;
        }

        return getPossiblePositions(FieldValue.WHITE).size() + getPossiblePositions(FieldValue.BLACK).size() == 0;
    }

    public FieldValue getWinner() {
        boolean whiteWins = whites > blacks;
        if (negativeWinner) {
            whiteWins = blacks > whites;
        }
        return whiteWins ? FieldValue.WHITE : FieldValue.BLACK;
    }

    public static Board initialBoard(boolean blackTopLeft) {
        Board board = new Board();

        if(blackTopLeft) {
            board = board.makeMove(new Move(3,3,FieldValue.BLACK));
            board = board.makeMove(new Move(4,4,FieldValue.BLACK));
            board = board.makeMove(new Move(3,4,FieldValue.WHITE));
            board = board.makeMove(new Move(4,3,FieldValue.WHITE));
        } else {
            board = board.makeMove(new Move(3,3,FieldValue.WHITE));
            board = board.makeMove(new Move(4,4,FieldValue.WHITE));
            board = board.makeMove(new Move(3,4,FieldValue.BLACK));
            board = board.makeMove(new Move(4,3,FieldValue.BLACK));
        }
        return board;
    }

    public static double getWeight(Position position) {
        return weights[position.row][position.column] + 5.45;
    }

    public Object clone() {
        Board newBoard = new Board();
        newBoard.board = Arrays.copyOf(board, board.length);
        newBoard.whites = whites;
        newBoard.blacks = blacks;
        return newBoard;
    }

}

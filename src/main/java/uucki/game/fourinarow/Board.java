package uucki.game.fourinarow;

import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;

import java.util.*;

public class Board extends uucki.game.Board {
    public boolean negativeWinner = false;

    public Move lastMove = null;

    public Board() {
        ROW_COUNT = 6;
        COLUMN_COUNT = 7;
        board = new FieldValue[ROW_COUNT * COLUMN_COUNT];
    }

    public Board makeMove(Move move) {
        Board newBoard = (Board)clone();
        Position position = new Position(move);
        newBoard.setFieldValue(position, move.value);

        newBoard.possibleWhitePositions = null;
        newBoard.possibleBlackPositions = null;
        newBoard.lastMove = move;
        return newBoard;
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

    public void setFieldValue(Position position, FieldValue color) {
        setFieldValue(position.row, position.column, color);
    }

    public FieldValue getFieldValue(int row, int column) {
        return board[row * COLUMN_COUNT + column];
    }

    public FieldValue getFieldValue(Position position) {
        return getFieldValue(position.row, position.column);
    }

    public boolean isFinished() {
        if(emptyFields() == 0) {
            return true;
        }

        return getWinner() != FieldValue.EMPTY;
    }

    public FieldValue getWinner() {
        if(lastMove == null) {
            return FieldValue.EMPTY;
        }

        boolean partOfFour = partOfFour(new Position(lastMove.row, lastMove.column));
        if(partOfFour) {
            return lastMove.value;
        }
        return FieldValue.EMPTY;
    }

    private boolean partOfFour(Position position) {
        FieldValue color = getFieldValue(position);

        //horizontal: -
        int countLeft = countSimilarNeighbours(position, color, 0, -1);
        int countRight = countSimilarNeighbours(position, color, 0, 1);
        if(countLeft + countRight + 1 == 4) {
            return true;
        }

        //vertical: |
        int countTop = countSimilarNeighbours(position, color, -1, 0);
        int countBottom = countSimilarNeighbours(position, color, 1, 0);
        if(countTop + countBottom + 1 == 4) {
            return true;
        }

        //diagonal left right: \
        int countTopLeft = countSimilarNeighbours(position, color, -1, -1);
        int countBottomRight = countSimilarNeighbours(position, color, 1, 1);
        if(countTopLeft + countBottomRight + 1 == 4) {
            return true;
        }

        //diagonal right left: /
        int countTopRight = countSimilarNeighbours(position, color, -1, 1);
        int countBottomLeft = countSimilarNeighbours(position, color, 1, -1);
        if(countTopRight + countBottomLeft + 1 == 4) {
            return true;
        }

        return false;
    }

    private int countSimilarNeighbours(Position startingPosition, FieldValue color, int deltaRow, int deltaCol) {
        int count = -1;
        Position position = startingPosition;
        while(validPosition(position) && getFieldValue(position) == color) {
            count++;
            position = new Position(position.row + deltaRow, position.column + deltaCol);
        }

        return count;
    }

    public static Board initialBoard(boolean blackTopLeft) {
        return new Board();
    }

    public Object clone() {
        Board newBoard = new Board();
        newBoard.board = Arrays.copyOf(board, board.length);
        newBoard.whites = whites;
        newBoard.blacks = blacks;
        newBoard.lastMove = lastMove;
        return newBoard;
    }

}

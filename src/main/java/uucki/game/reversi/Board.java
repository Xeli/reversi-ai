package uucki.game.reversi;

import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;

import java.util.*;

public class Board{

    public FieldValue[] board = new FieldValue[8*8];
    public int whites = 0;
    public int blacks = 0;

    private List<Position> possibleWhitePositions = null;
    private List<Position> possibleBlackPositions = null;

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
            }
            currentPosition.row += rowDirection;
            currentPosition.column += columnDirection;
        }

        return swaps;
    }

    public void setFieldValue(int row, int column, FieldValue color) {
        possibleBlackPositions = null;
        possibleWhitePositions = null;
        FieldValue oldColor = board[row * 8 + column];
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
        board[row * 8 + column] = color;
    }

    public void setFieldValue(Position position, FieldValue color) {
        setFieldValue(position.row, position.column, color);
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

        return getPossiblePositions(FieldValue.WHITE).size() + getPossiblePositions(FieldValue.BLACK).size() == 0;
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

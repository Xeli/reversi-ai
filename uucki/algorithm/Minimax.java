package uucki.algorithm;

import uucki.game.reversi.Board;
import uucki.game.reversi.PossibleMoves;
import uucki.type.Position;
import uucki.type.Move;
import uucki.type.FieldValue;
import uucki.heuristic.reversi.Basic;

import java.util.*;

public class Minimax extends Algorithm {

    public Move run(Board board, FieldValue color) {

        List<Position> possiblePositions = PossibleMoves.getValidPositions(board, color);
        System.out.println(possiblePositions.size());
        double value = Double.NEGATIVE_INFINITY;
        Move resultMove = null;
        for(Position position : possiblePositions) {
            Move move = new Move(position, color);
            double newValue = minValue(5, board.makeMove(move), otherPlayer(color));
            if(newValue > value) {
                resultMove = move;
                value = newValue;
            }
        }

        return resultMove;
    }

    public double minValue(int depth, Board board, FieldValue color) {
        if(depth == 0) {
            return Basic.getValue(board, color);
        }
        List<Position> possiblePositions = PossibleMoves.getValidPositions(board, color);
        double value = Double.POSITIVE_INFINITY;

        for(Position position : possiblePositions) {
            Move move = new Move(position, color);
            value = Math.min(value, maxValue(depth-1, board.makeMove(move), otherPlayer(color)));
        }
        return value;
    }

    public double maxValue(int depth, Board board, FieldValue color) {
        if(depth == 0) {
            return Basic.getValue(board, color);
        }
        List<Position> possiblePositions = PossibleMoves.getValidPositions(board, color);
        double value = Double.NEGATIVE_INFINITY;

        for(Position position : possiblePositions) {
            Move move = new Move(position, color);
            value = Math.max(value, minValue(depth-1, board.makeMove(move), otherPlayer(color)));
        }
        return value;
    }

    private FieldValue otherPlayer(FieldValue color) {
        if(color == FieldValue.WHITE) {
            return FieldValue.BLACK;
        }

        return FieldValue.WHITE;
    }

}

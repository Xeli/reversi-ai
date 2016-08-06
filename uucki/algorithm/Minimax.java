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
        long startingTime = System.currentTimeMillis();
        Move bestMove = null;
        int depth = 1;
        long cutOffTime = startingTime + 1000 * 5;
        System.out.println(board.emptyFields());
        while((System.currentTimeMillis() < cutOffTime || bestMove == null) && depth <= board.emptyFields()) {
            Result result = maxValue(depth++, board, color, cutOffTime);
            System.out.println(Arrays.toString(result.moves.toArray()));
            Move move = result.moves.get(result.moves.size() - 1);
            System.out.println(move);
            System.out.println("Depth: " + depth);
            if(System.currentTimeMillis() < cutOffTime) {
                bestMove = move;
            }
        }
        return bestMove;
    }

    public Result minValue(int depth, Board board, FieldValue color, long cutOffTime) {
        if(depth == 0 || System.currentTimeMillis() > cutOffTime) {
            return new Result((int)Basic.getValue(board,otherPlayer(color)));
        }

        if(board.isFinished()) {
            return new Result(board.getWinner() == otherPlayer(color) ? 1000 : -1000);
        }

        List<Position> possiblePositions = PossibleMoves.getValidPositions(board,color);

        //if there are no possible moves, the opponent can go again
        if(possiblePositions.size() == 0) {
            return maxValue(depth, board, otherPlayer(color), cutOffTime);
        }

        Result result = null;

        for(Position position : possiblePositions) {
            Move move = new Move(position, color);
            result = newResultMin(result, maxValue(depth-1, board.makeMove(move), otherPlayer(color), cutOffTime).add(move));
        }
        return result;
    }

    public Result maxValue(int depth, Board board, FieldValue color, long cutOffTime) {
        if(depth == 0 || System.currentTimeMillis() > cutOffTime) {
            return new Result((int)Basic.getValue(board,color));
        }

        if(board.isFinished()) {
            return new Result(board.getWinner() == color ? 1000 : -1000);
        }
        List<Position> possiblePositions = PossibleMoves.getValidPositions(board, color);

        //if there are no possible moves, the opponent can go again
        if(possiblePositions.size() == 0) {
            return minValue(depth, board, otherPlayer(color), cutOffTime);
        }
        Result result = null;

        for(Position position : possiblePositions) {
            Move move = new Move(position, color);
            result = newResultMax(result, minValue(depth-1, board.makeMove(move), otherPlayer(color), cutOffTime).add(move));
        }
        return result;
    }

    private FieldValue otherPlayer(FieldValue color) {
        if(color == FieldValue.WHITE) {
            return FieldValue.BLACK;
        }

        return FieldValue.WHITE;
    }

    private Result newResultMin(Result result, Result newResult) {
        if(result == null || newResult.score < result.score) {
            return newResult;
        }

        return result;
    }
    private Result newResultMax(Result result, Result newResult) {
        if(result == null || newResult.score > result.score) {
            return newResult;
        }

        return result;
    }

}

class Result {
    List<Move> moves = new ArrayList<Move>();
    int score = 0;

    public Result(int score) {
        this.score = score;
    }

    public Result add(Move move) {
        moves.add(move);
        return this;
    }
}

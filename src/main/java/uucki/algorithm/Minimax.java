package uucki.algorithm;

import uucki.game.reversi.Board;
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
        long cutOffTime = startingTime + 1000 * 2;
        System.out.println(board.emptyFields());
        while(!board.isFinished() && (System.currentTimeMillis() < cutOffTime || bestMove == null) && depth <= board.emptyFields()) {
            Result result = maxValue(depth++, board, color, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, cutOffTime);
            if(System.currentTimeMillis() < cutOffTime) {
                Collections.reverse(result.moves);
                System.out.print(Arrays.toString(result.moves.toArray()));
                System.out.println(" " + result.score);
                System.out.println("Depth: " + depth);
                Move move = result.moves.get(0);
                bestMove = move;
            }
        }
        return bestMove;
    }

    public Result minValue(int depth, Board board, FieldValue color, double alpha, double beta, long cutOffTime) {
        if(board.isFinished()) {
            return new Result(board.getWinner() == otherPlayer(color) ? 1000 : -1000);
        }
        if(depth == 0 || System.currentTimeMillis() > cutOffTime) {
            return new Result((int)Basic.getValue(board,otherPlayer(color)));
        }


        List<Position> possiblePositions = board.getPossiblePositions(color);

        //if there are no possible moves, the opponent can go again
        if(possiblePositions.size() == 0) {
            return maxValue(depth, board, otherPlayer(color), alpha, beta, cutOffTime);
        }

        Result result = null;

        for(Position position : possiblePositions) {
            Move move = new Move(position, color);
            result = newResultMin(result, maxValue(depth-1, board.makeMove(move), otherPlayer(color), alpha, beta, cutOffTime).add(move));
            beta = Math.min(beta, result.score);
            if(beta <= alpha) {
                return result;
            }
        }
        return result;
    }

    public Result maxValue(int depth, Board board, FieldValue color, double alpha, double beta, long cutOffTime) {
        if(board.isFinished()) {
            return new Result(board.getWinner() == color ? 1000 : -1000);
        }
        if(depth == 0 || System.currentTimeMillis() > cutOffTime) {
            return new Result((int)Basic.getValue(board,color));
        }

        List<Position> possiblePositions = board.getPossiblePositions(color);

        //if there are no possible moves, the opponent can go again
        if(possiblePositions.size() == 0) {
            return minValue(depth, board, otherPlayer(color), alpha, beta, cutOffTime);
        }
        Result result = null;

        for(Position position : possiblePositions) {
            Move move = new Move(position, color);
            result = newResultMax(result, minValue(depth-1, board.makeMove(move), otherPlayer(color), alpha, beta, cutOffTime).add(move));
            alpha = Math.max(alpha, result.score);
            if(beta <= alpha) {
                return result;
            }
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

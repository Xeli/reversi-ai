package uucki;

import uucki.game.reversi.Board;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.algorithm.MonteCarloTreeSearch;
import uucki.algorithm.MonteCarloTreeSearch.Node;
import uucki.modes.AIvsAI;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.List;

public class AnalyseSingleMove{

    public static void main(String[] args) {
        Board board = Board.initialBoard(false);
        ExecutorService executor = Executors.newCachedThreadPool();
        MonteCarloTreeSearch ai = new MonteCarloTreeSearch(0.25, false, false, executor);

        Move move = ai.run(board, FieldValue.WHITE);
        board = board.makeMove(move);
        move = ai.run(board, FieldValue.BLACK);
        board = board.makeMove(move);
        move = ai.run(board, FieldValue.WHITE);
        board = board.makeMove(move);
        move = ai.run(board, FieldValue.BLACK);
        board = board.makeMove(move);
        move = ai.run(board, FieldValue.WHITE);
        board = board.makeMove(move);
        move = ai.run(board, FieldValue.BLACK);
        board = board.makeMove(move);
        move = ai.run(board, FieldValue.WHITE, false);

        board.print();
        board = board.makeMove(move);
        board.print();

        List<Node<Board>> children = ai.getChildren(ai.rootNode);

        for(Node<Board> node : children) {
            System.out.println(node.score + "/" + node.plays);
        }

        executor.shutdown();
    }
}

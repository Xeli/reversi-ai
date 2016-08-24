package uucki.modes;

import uucki.game.reversi.Board;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;
import uucki.algorithm.Algorithm;
import uucki.algorithm.MonteCarloTreeSearch;
import uucki.graphics.reversi.Window;
import uucki.graphics.reversi.MonteCarlo;

import java.util.*;

public class VersusAI implements Runnable {

    private Board board = null;
    private boolean aiFirst = true;
    private FieldValue aiColor = null;
    private Algorithm algorithm = null;
    private MonteCarlo mtPainter = null;
    private Window window = null;

    public VersusAI(Board board, boolean aiFirst, FieldValue aiColor, Algorithm algorithm) {
        this.board = board;
        this.aiFirst = aiFirst;
        this.aiColor = aiColor;
        this.algorithm = algorithm;
    }

    public Board game() {
        FieldValue opponentColor = FieldValue.WHITE == aiColor ? FieldValue.BLACK : FieldValue.WHITE;

        if(algorithm instanceof MonteCarloTreeSearch) {
            mtPainter = new MonteCarlo();
        }
        window = new Window(mtPainter);
        window.update(board);
        if(algorithm instanceof MonteCarloTreeSearch) {
            new Thread(this).start();
        }


        if(aiFirst) {
            Move move = algorithm.run(board, aiColor);
            board = board.makeMove(move);
            window.update(board);
        }
        while(!board.isFinished()) {
            if(board.getPossiblePositions(opponentColor).size() > 0) {
                List<Position> validPositions = board.getPossiblePositions(opponentColor);
                Position newPosition = null;
                while(newPosition == null) {
                    System.out.println("Make your move");
                    newPosition = window.getPosition();
                    if(!validPositions.contains(newPosition)) {
                        newPosition = null;
                    }
                }
                board = board.makeMove(new Move(newPosition, opponentColor));
                window.update(board);
            }

            //Computer move
            Move move = algorithm.run(board, aiColor);
            if(move != null) {
                board = board.makeMove(move);
                window.update(board);
            }
        }
        return board;
    }

    public void run() {
        MonteCarloTreeSearch mcts = (MonteCarloTreeSearch)algorithm;
        try {
            while(true) {
                Thread.sleep(100);
                mtPainter.update(mcts.getMoveProbability());
                window.repaint();
            }
        } catch (InterruptedException e) {

        }
    }
}

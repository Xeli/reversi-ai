package uucki.modes;

import uucki.game.reversi.Board;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.algorithm.Algorithm;

public class AIvsAI {

    private Board board = null;
    private Algorithm ai1 = null;
    private Algorithm ai2 = null;

    public AIvsAI(Board board, Algorithm ai1, Algorithm ai2) {
        this.board = board;
        this.ai1= ai1;
        this.ai2= ai2;
    }

    public Board game() {
        while(!board.isFinished()) {
            Move move = ai1.run(board, FieldValue.WHITE);
            if(move != null) {
                board = board.makeMove(move);
            }

            //Computer move
            move = ai2.run(board, FieldValue.BLACK);
            if(move != null) {
                board = board.makeMove(move);
            }
        }
        return board;
    }
}

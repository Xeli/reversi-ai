package uucki.modes;

import uucki.game.Board;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.algorithm.Algorithm;
import uucki.graphics.Window;

public class AIvsAI {

    private Board board = null;
    private Algorithm ai1 = null;
    private Algorithm ai2 = null;

    private boolean showBoard = true;
    private Window window = null;

    public AIvsAI(Board board, Algorithm ai1, Algorithm ai2, boolean showBoard) {
        this.board = board;
        this.ai1= ai1;
        this.ai2= ai2;
        this.showBoard = showBoard;
        if(showBoard) {
            this.window = new Window(board, null);
        }
    }

    public Board game() {
        while(!board.isFinished()) {
            updateBoard(board);

            Move move = ai1.run(board, FieldValue.WHITE);
            if(move != null) {
                board = board.makeMove(move);
            }

            updateBoard(board);

            if(!board.isFinished()) {
                //Computer move
                move = ai2.run(board, FieldValue.BLACK);
                if(move != null) {
                    board = board.makeMove(move);
                }
            }
        }

        closeWindow();
        return board;
    }

    private void updateBoard(Board board) {
        if(showBoard) {
            window.update(board);
        }
    }

    private void closeWindow() {
        if(showBoard) {
            window.hide();
        }
    }
}

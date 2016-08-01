import uucki.game.reversi.Board;
import uucki.game.reversi.PossibleMoves;
import uucki.algorithm.Algorithm;
import uucki.algorithm.Minimax;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;
import uucki.heuristic.reversi.Basic;
import uucki.graphics.reversi.Window;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.println("Board position, is black top left? (y/n)");
            boolean blackTopLeft = br.readLine().equals("y");

            System.out.println("Am I starting? (y/n)");
            boolean imStarting = br.readLine().equals("y");

            System.out.println("Am I black? (y/n)");
            FieldValue myColor = br.readLine().equals("y") ? FieldValue.BLACK : FieldValue.WHITE;

            Board board = initialBoard(blackTopLeft);
            game(board, imStarting, myColor);
        } catch (IOException e) {
            System.out.println("IO error, start again");
        }
    }

    public static void game(Board board, boolean imStarting, FieldValue color) {
        FieldValue opponentColor = FieldValue.WHITE == color ? FieldValue.BLACK : FieldValue.WHITE;
        board.print();

        Window window = new Window();
        window.update(board);

        Algorithm minimax = new Minimax();

        if(imStarting) {
            Move move = minimax.run(board, color);
            board = board.makeMove(move);
            window.update(board);
        }
        while(true) {
            if(PossibleMoves.getValidPositions(board, opponentColor).size() > 0) {
                List<Position> validPositions = PossibleMoves.getValidPositions(board, opponentColor);
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
            Move move = minimax.run(board, color);
            board = board.makeMove(move);
            window.update(board);
        }
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
}

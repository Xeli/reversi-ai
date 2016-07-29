import uucki.game.reversi.Board;
import uucki.game.reversi.PossibleMoves;
import uucki.algorithm.Algorithm;
import uucki.algorithm.Minimax;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.heuristic.reversi.Basic;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        if(args[0].equals("black")) {
            game(FieldValue.BLACK);
        } else {
            game(FieldValue.WHITE);
        }
    }

    public static void game(FieldValue color) {
        FieldValue opponentColor = FieldValue.WHITE == color ? FieldValue.BLACK : FieldValue.WHITE;
        Board board = new Board();

        board = board.makeMove(new Move(3,3,FieldValue.WHITE));
        board = board.makeMove(new Move(4,4,FieldValue.WHITE));
        board = board.makeMove(new Move(3,4,FieldValue.BLACK));
        board = board.makeMove(new Move(4,3,FieldValue.BLACK));
        board.print();

        Algorithm minimax = new Minimax();

        if(color == FieldValue.BLACK) {
            Move move = minimax.run(board, color);
            System.out.println("(" + (move.row+1) +","+(char)(move.column+'a') + ")");
            board = board.makeMove(move);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            PossibleMoves.printBoard(board, opponentColor);
            try {
                Move move = null;
                boolean keepAsking = true;
                while(keepAsking) {
                    System.out.println("Make your move");
                    String input = br.readLine();
                    int row = Integer.parseInt(input)-1;
                    input = br.readLine();
                    int column = Character.getNumericValue(input.charAt(0)) - 10;
                    move = new Move(row, column, opponentColor);
                    System.out.print("Do you want a different move? ");
                    System.out.println(move);
                    keepAsking = br.readLine().equals("y");
                }

                board = board.makeMove(move);
            } catch (IOException e) {
                System.out.println("IO Error");
            }

            Move move = minimax.run(board, color);
            System.out.println("(" + (move.row+1) +","+(char)(move.column+'a') + ")");
            board = board.makeMove(move);
        }
    }
}

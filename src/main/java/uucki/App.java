package uucki;

import uucki.game.reversi.Board;
import uucki.algorithm.Algorithm;
import uucki.algorithm.MonteCarloTreeSearch;
import uucki.algorithm.Minimax;
import uucki.type.FieldValue;
import uucki.modes.VersusAI;
import uucki.modes.AIvsAI;
import uucki.modes.CustomBoard;

import java.io.*;

public class App {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        boolean vsAI = false;
        try {
            System.out.println("Play versus ai? (y/N)");
            vsAI = br.readLine().toLowerCase().equals("y");
        } catch (IOException e) {}

        if(!vsAI) {
            Algorithm ai1 = new MonteCarloTreeSearch();
            Algorithm ai2 = new Minimax();
            Board board = Board.initialBoard(false);
            AIvsAI mode = new AIvsAI(board, ai2, ai1);
            board = mode.game();
            if(board.getWinner() == FieldValue.WHITE) {
                System.out.println("White won.");
            } else {
                System.out.println("Black won");
            }
        } else {
            versusGame(br);
        }
    }

    public static void versusGame(BufferedReader br) {
        try {
            Board board = askBoard(br);

            System.currentTimeMillis();
            System.out.println("Am I starting? (y/n)");
            boolean imStarting = br.readLine().equals("y");

            System.out.println("Am I black? (y/n)");
            FieldValue myColor = br.readLine().equals("y") ? FieldValue.BLACK : FieldValue.WHITE;

            Algorithm minimax = new MonteCarloTreeSearch();
            VersusAI mode = new VersusAI(board, imStarting, myColor, minimax);
            board = mode.game();
            if(board.getWinner() == myColor) {
                System.out.println("I won!");
            } else {
                System.out.println("I lost :(");
            }
        } catch (IOException e) {
            System.out.println("IO error, start again");
        }
    }

    public static Board askBoard(BufferedReader br) throws IOException {
        System.out.println("Do you want a custom board? (y/N)");
        String answer = br.readLine();
        Board board = null;
        if(answer.equals("y")) {
            board = CustomBoard.get(br);
        } else {
            System.out.println("Board position, is black top left? (y/n)");
            board = Board.initialBoard(br.readLine().equals("y"));
        }

        return board;
    }
}

package uucki;


import uucki.game.fourinarow.Board;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;
import uucki.algorithm.Algorithm;
import uucki.algorithm.MonteCarloTreeSearch;
import uucki.modes.VersusAI;

import java.util.*;

public class FourInARow {

    public static void main(String[] args) {
        Board board = new Board();
        System.out.println("hi");
        System.out.println(board.getPossiblePositions(FieldValue.WHITE));
        System.out.println("hi");
        Algorithm mcts = new MonteCarloTreeSearch(2, MonteCarloTreeSearch.RANDOM, false);
        VersusAI mode = new VersusAI(board, true, FieldValue.WHITE, mcts);
        board = (Board)mode.game();
        if(board.getWinner() == FieldValue.WHITE) {
            System.out.println("I won!");
        } else {
            System.out.println("I lost :(");
        }
    }
}

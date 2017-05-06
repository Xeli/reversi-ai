package uucki;

import uucki.game.Board;
import uucki.type.FieldValue;
import uucki.type.Move;
import uucki.type.Position;
import uucki.type.Node;
import uucki.algorithm.MonteCarloTreeSearch;
import uucki.modes.AIvsAI;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.*;

public class AnalyseSingleMove{

    public static void main(String[] args) {
        Board board = getBoardWithCornerMove(new uucki.game.reversi.Board());
        MonteCarloTreeSearch ai = new MonteCarloTreeSearch(0.2, MonteCarloTreeSearch.RANDOM, false);
        ai.MAX_TIME = 1500;

        board.print();
        ai.run(board, FieldValue.BLACK);
        printCornerMoves(ai);

        ai.MAX_TIME = 0;
        while(ai.MAX_TIME < 3000) {
            if(ai != null) {
            return;
            }
            ai.MAX_TIME += 100;
            double ave = 0;
            double ave2 = 0;
            for(int i = 0; i < 10; i++) {
                ai.run(board, FieldValue.BLACK);
                ave += ai.simulationCount.get() / 10.0;
                ave2 += printCornerMoves(ai) / 10.0;
            }
            System.out.println(ai.MAX_TIME + "," + ave + "," + ave2);
        }
    }

    public static int printCornerMoves(MonteCarloTreeSearch ai) {
        if(!ai.hasCornerMove(ai.rootNode)) {
            System.out.println("hi");
        }

        Node<Board> root = ai.rootNode;

        List<Position> positions = root.item.getPossiblePositions(root.color);
        FieldValue opponentColor = root.color == FieldValue.WHITE ? FieldValue.BLACK : FieldValue.WHITE;

        Map<Double, String> sortedOutput = new TreeMap<Double, String>();
        int cornerPlays = 0;
        int nonCornerPlays = 0;
        for(Position p : positions) {
            Move move = new Move(p, root.color);
            Board b = root.item.makeMove(move);
            Node<Board> node = new Node<Board>(b, opponentColor);
            if(ai.getNodes(node.color).containsKey(node.item)) {
                node = ai.getNodes(node.color).get(node.item);
                String output = p.toString() + " - " + node.score + "/" + node.plays + " = " + ((double)node.score / node.plays) + ": " + p.isCorner();
                sortedOutput.put(1 - ((double)node.score / node.plays), output);
                System.out.println(output);
                if(p.isCorner()) {
                    cornerPlays += node.plays;
                } else {
                    nonCornerPlays += node.plays;
                }
            }
        }
        System.out.println(cornerPlays);
        System.out.println(nonCornerPlays);
        System.out.println(cornerPlays / (double)(cornerPlays + nonCornerPlays));

        for(Map.Entry<Double, String> entry : sortedOutput.entrySet()) {
            System.out.println(entry.getValue());
        }
        return 0;
    }

    public static Board getBoardWithCornerMove(Board board) {
        board.setFieldValue(0,1,FieldValue.BLACK);
        board.setFieldValue(0,2,FieldValue.BLACK);
        board.setFieldValue(0,3,FieldValue.BLACK);
        board.setFieldValue(0,4,FieldValue.BLACK);
        board.setFieldValue(0,5,FieldValue.BLACK);
        board.setFieldValue(0,6,FieldValue.BLACK);
        board.setFieldValue(0,7,FieldValue.BLACK);


        board.setFieldValue(1,0,FieldValue.BLACK);
        board.setFieldValue(1,1,FieldValue.WHITE);
        board.setFieldValue(1,2,FieldValue.BLACK);
        board.setFieldValue(1,3,FieldValue.WHITE);
        board.setFieldValue(1,4,FieldValue.WHITE);
        board.setFieldValue(1,5,FieldValue.BLACK);
        board.setFieldValue(1,6,FieldValue.BLACK);
        board.setFieldValue(1,7,FieldValue.BLACK);

        board.setFieldValue(2,0,FieldValue.WHITE);
        board.setFieldValue(2,1,FieldValue.WHITE);
        board.setFieldValue(2,2,FieldValue.WHITE);
        board.setFieldValue(2,3,FieldValue.WHITE);
        board.setFieldValue(2,4,FieldValue.WHITE);
        board.setFieldValue(2,5,FieldValue.BLACK);
        board.setFieldValue(2,6,FieldValue.BLACK);
        board.setFieldValue(2,7,FieldValue.BLACK);

        board.setFieldValue(3,0,FieldValue.WHITE);
        board.setFieldValue(3,1,FieldValue.WHITE);
        board.setFieldValue(3,2,FieldValue.WHITE);
        board.setFieldValue(3,3,FieldValue.WHITE);
        board.setFieldValue(3,4,FieldValue.BLACK);
        board.setFieldValue(3,5,FieldValue.BLACK);
        board.setFieldValue(3,6,FieldValue.BLACK);
        board.setFieldValue(3,7,FieldValue.BLACK);

        board.setFieldValue(4,0,FieldValue.WHITE);
        board.setFieldValue(4,1,FieldValue.WHITE);
        board.setFieldValue(4,2,FieldValue.BLACK);
        board.setFieldValue(4,3,FieldValue.WHITE);
        board.setFieldValue(4,4,FieldValue.BLACK);
        board.setFieldValue(4,5,FieldValue.BLACK);
        board.setFieldValue(4,6,FieldValue.WHITE);
        board.setFieldValue(4,7,FieldValue.BLACK);


        board.setFieldValue(5,0,FieldValue.BLACK);
        board.setFieldValue(5,1,FieldValue.WHITE);
        board.setFieldValue(5,2,FieldValue.BLACK);
        board.setFieldValue(5,3,FieldValue.BLACK);
        board.setFieldValue(5,4,FieldValue.WHITE);
        board.setFieldValue(5,5,FieldValue.WHITE);
        board.setFieldValue(5,6,FieldValue.WHITE);
        board.setFieldValue(5,7,FieldValue.WHITE);

        board.setFieldValue(6,2,FieldValue.BLACK);
        board.setFieldValue(6,3,FieldValue.WHITE);
        board.setFieldValue(6,4,FieldValue.WHITE);

        board.setFieldValue(7,5,FieldValue.WHITE);

        return board;
    }

}

package uucki.heuristic.reversi;

import uucki.game.reversi.Board;
import uucki.type.FieldValue;
import uucki.type.Position;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Util{

    public static Board getRandomBoard() {
        ThreadLocalRandom tlr = ThreadLocalRandom.current();

        int amountOfPieces = tlr.nextInt(65);

        Board board = new Board();

        for(int i = 0; i < amountOfPieces; i++) {
            Position position = Util.getEmptyField(tlr, board);
            if(position == null) {
                continue;
            }
            if (tlr.nextBoolean()) {
                board.setFieldValue(position, FieldValue.WHITE);
            } else {
                board.setFieldValue(position, FieldValue.BLACK);
            }
        }
        return board;
    }

    private static Position getEmptyField(ThreadLocalRandom tlr, Board board) {
        List<Position> positions = new ArrayList<Position>();
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                if(board.getFieldValue(x,y) == FieldValue.EMPTY || board.getFieldValue(x,y) == null) {
                    positions.add(new Position(x,y));
                }
            }
        }
        if(positions.size() == 0) {
            return null;
        }
        return positions.get(tlr.nextInt(positions.size()));
    }
}

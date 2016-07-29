package uucki.heuristic.reversi;

import uucki.game.reversi.PossibleMoves;
import uucki.game.reversi.Board;
import uucki.type.FieldValue;
import uucki.type.Position;

public class Basic {

    public static double getValue(Board board, FieldValue color) {
        int score = 0;

        Position[] corners = new Position[]{
            new Position(0,0),
        };
        score += countColors(board, corners, color) * 100;

        Position[] nearCorners = new Position[]{
            new Position(0,1),
            new Position(1,0),
        };
        score += countColors(board, nearCorners, color) * -20;

        Position[] cornerCorner = new Position[]{
            new Position(1,1),
        };
        score += countColors(board,cornerCorner, color) * -50;

        Position[] cornerEdge = new Position[]{
            new Position(0,2),
            new Position(2,0),
        };
        score += countColors(board, cornerEdge, color) * 10;

        Position[] edge = new Position[]{
            new Position(3,0),
            new Position(0,3),
        };
        score += countColors(board, edge, color) * 5;

        Position[] mid = new Position[]{
            new Position(2,1),
            new Position(3,1),
            new Position(1,2),
            new Position(1,3),
        };
        score += countColors(board, mid, color) * -2;

        Position[] bot = new Position[]{
            new Position(2,2),
            new Position(2,3),
            new Position(3,3),
            new Position(3,2),
        };
        score += countColors(board, bot, color) * -1;

        return score;
    }

    private static int countColors(Board board, Position[] positions, FieldValue color) {
        int hits = 0;

        for(Position position : positions) {
            if(board.getFieldValue(position) == color) {
                hits++;
            }
        }
        for(Position position : rotate(positions, 1)) {
            if(board.getFieldValue(position) == color) {
                hits++;
            }
        }
        for(Position position : rotate(positions, 2)) {
            if(board.getFieldValue(position) == color) {
                hits++;
            }
        }
        for(Position position : rotate(positions, 3)) {
            if(board.getFieldValue(position) == color) {
                hits++;
            }
        }

        return hits;
    }

    private static Position[] rotate(Position[] positions, int rotate) {
        Position[] newPositions = new Position[positions.length];
        for(int i = 0; i < positions.length; i++) {
            switch(rotate) {
                case 1:
                    newPositions[i] = new Position(positions[i].row, 7 - positions[i].column);
                    break;
                case 2:
                    newPositions[i] = new Position(7 - positions[i].row, positions[i].column);
                    break;
                case 3:
                    newPositions[i] = new Position(7 - positions[i].row, 7 - positions[i].column);
                    break;
            }
        }
        return newPositions;
    }
}

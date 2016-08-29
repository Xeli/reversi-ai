package uucki;

import java.io.*;
import java.util.*;

import uucki.game.reversi.Board;
import uucki.heuristic.reversi.Basic;
import uucki.heuristic.reversi.Util;
import uucki.type.FieldValue;
import uucki.type.Position;

public class CreateData{

    public static void main(String[] args) {
        PrintWriter out = null;
        try {
            FileWriter fw = new FileWriter(args[0], true);
            BufferedWriter bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);
        } catch (IOException e) {
            System.out.println("Could not open file");
            return;
        }

        for(int i = 0; i < 200000; i++) {
            Board b = Util.getRandomBoard();
            double scoreWhite = Basic.getValue(b, FieldValue.WHITE);
            double scoreBlack = Basic.getValue(b, FieldValue.BLACK);
            printData(out, b, scoreWhite, b.getPossiblePositions(FieldValue.WHITE), false);

            printData(out, b, scoreBlack, b.getPossiblePositions(FieldValue.BLACK), true);
        }

        out.close();
    }

    public static void printData(PrintWriter out, Board b, double score, List<Position> possiblePositions, boolean swapColors) {
        for(int x = 0; x < 8; x++) {
            for(int y = 0; y < 8; y++) {
                Position position = new Position(x,y);
                if(possiblePositions.contains(position)) {
                    out.print("3,");
                    continue;
                }
                FieldValue value = b.getFieldValue(position);
                if(value == null) {
                    value = FieldValue.EMPTY;
                }
                if(value != FieldValue.EMPTY && swapColors) {
                    value = value.getOpponent();
                }
                switch(value) {
                    case EMPTY:
                        out.print("0,");
                        break;
                    case BLACK:
                        out.print("2,");
                        break;
                    case WHITE:
                        out.print("1,");
                }
            }
        }
        out.println(score);
    }
}

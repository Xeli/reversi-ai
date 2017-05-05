package uucki.modes;

import uucki.game.reversi.Board;
import uucki.type.*;
import uucki.graphics.Window;

import java.io.*;

public class CustomBoard {

    public static Board get(BufferedReader br) throws IOException {
        Board board = new Board();
        Window window = new Window(board, null);
        do {
            System.out.println("How many whites do you want?");
            int count = Integer.parseInt(br.readLine());
            for(int i = 0; i < count; i++) {
                board.setFieldValue(window.getPosition(), FieldValue.WHITE);
                window.update(board);
            }
        } while(CustomBoard.askForMore(br));

        do {
            System.out.println("How many blacks do you want?");
            int count = Integer.parseInt(br.readLine());
            for(int i = 0; i < count; i++) {
                board.setFieldValue(window.getPosition(), FieldValue.BLACK);
                window.update(board);
            }
        } while(CustomBoard.askForMore(br));

        window.hide();
        return board;
    }

    public static boolean askForMore(BufferedReader br) throws IOException {
        System.out.print("Want to fill more fields in this color? (y/N) ");
        return br.readLine().equals("y");
    }
}

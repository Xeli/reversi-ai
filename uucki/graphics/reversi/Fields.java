package uucki.graphics.reversi;

import uucki.type.FieldValue;
import uucki.game.reversi.Board;
import uucki.type.Position;

import java.awt.event.*;
import java.awt.*;

public class Fields extends Canvas implements MouseListener{
    private FieldValue[][] values = null;

    private int fieldWidth = 0;
    private int fieldHeight = 0;

    private Position lastClick = null;

    public Fields() {
        super();
        setBackground(Color.GRAY);

        values = new FieldValue[8][8];
        for(int row = 0; row < values.length; row++) {
            for(int column = 0; column < values[row].length; column++) {
                values[row][column] = FieldValue.EMPTY;
            }
        }

        addMouseListener(this);
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    public void updateValues(Board board) {
        for(int row = 0; row < values.length; row++) {
            for(int column = 0; column < values[row].length; column++) {
                values[row][column] = board.getFieldValue(row, column);
            }
        }
    }

    public void paint(Graphics g) {
        fieldWidth = (int)Math.round(getWidth() / 8.0);
        fieldHeight = (int)Math.round(getHeight() / 8.0);

        for(int row = 0; row < values.length; row++) {
            for(int column = 0; column < values[row].length; column++) {
                switch(values[row][column]) {
                    case EMPTY:
                        g.setColor(Color.GRAY);
                        g.fillRect(row * fieldWidth, column * fieldHeight, fieldWidth, fieldHeight);
                        break;
                    case WHITE:
                        g.setColor(Color.WHITE);
                        g.fillOval(row * fieldWidth, column * fieldHeight, fieldWidth, fieldHeight);
                        break;
                    case BLACK:
                        g.setColor(Color.BLACK);
                        g.fillOval(row * fieldWidth, column * fieldHeight, fieldWidth, fieldHeight);
                        break;
                }
            }
        }


    }

    public void acceptingClick() {
        lastClick = null;
    }

    public Position consumeClick() {
        return lastClick;
    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        fieldWidth = (int)Math.round(getWidth() / 8.0);
        fieldHeight = (int)Math.round(getHeight() / 8.0);

        lastClick = new Position(x / fieldWidth, y / fieldHeight);
        System.out.println(lastClick);
    }
    public void mousePressed(MouseEvent e) {

    }
    public void mouseReleased(MouseEvent e) {

    }
    public void mouseEntered(MouseEvent e) {

    }
    public void mouseExited(MouseEvent e) {

    }
}

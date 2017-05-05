package uucki.graphics;

import uucki.type.FieldValue;
import uucki.game.Board;
import uucki.type.Position;
import uucki.graphics.reversi.MonteCarlo;

import java.awt.event.*;
import java.awt.*;

public class Fields extends Canvas implements MouseListener{
    private FieldValue[][] values = null;

    private MonteCarlo mtPainter = null;

    private int rows = 0;
    private int cols = 0;
    private int fieldWidth = 0;
    private int fieldHeight = 0;

    private Position lastClick = null;

    public Fields(Board board, MonteCarlo mtPainter) {
        super();
        setBackground(Color.GRAY);

        rows = board.ROW_COUNT;
        cols = board.COLUMN_COUNT;

        values = new FieldValue[board.ROW_COUNT][board.COLUMN_COUNT];
        for(int row = 0; row < values.length; row++) {
            for(int column = 0; column < values[row].length; column++) {
                values[row][column] = FieldValue.EMPTY;
            }
        }

        addMouseListener(this);
        this.mtPainter = mtPainter;
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
    }

    public void updateValues(Board board) {
        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < cols; column++) {
                values[row][column] = board.getFieldValue(row, column);
            }
        }
    }

    public void paint(Graphics g) {
        fieldWidth = (int)Math.round(getWidth() / (double)cols);
        fieldHeight = (int)Math.round(getHeight() / (double)rows);

        for(int row = 0; row < rows; row++) {
            for(int column = 0; column < cols; column++) {
                if(values[row][column] == null) {
                    g.setColor(Color.GRAY);
                    g.fillRect(column * fieldWidth, row * fieldHeight, fieldWidth, fieldHeight);
                } else {
                    switch(values[row][column]) {
                        case EMPTY:
                            g.setColor(Color.GRAY);
                            g.fillRect(column * fieldWidth, row * fieldHeight, fieldWidth, fieldHeight);
                            break;
                        case WHITE:
                            g.setColor(Color.WHITE);
                            g.fillOval(column * fieldWidth, row * fieldHeight, fieldWidth, fieldHeight);
                            break;
                        case BLACK:
                            g.setColor(Color.BLACK);
                            g.fillOval(column * fieldWidth, row * fieldHeight, fieldWidth, fieldHeight);
                            break;
                    }
                }
            }
        }

        g.setColor(Color.GREEN);
        for(int i = 0; i < cols; i++) {
            g.drawLine(i * fieldWidth, 0, i * fieldWidth, getHeight());
        }
        for(int i = 0; i < rows; i++) {
            g.drawLine(0, i * fieldHeight, getWidth(), i * fieldHeight);
        }

        if(mtPainter != null) {
            mtPainter.paint(fieldWidth, fieldHeight, g);
        }

        Toolkit.getDefaultToolkit().sync();
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
        fieldWidth = (int)Math.round(getWidth() / (double)cols);
        fieldHeight = (int)Math.round(getHeight() / (double)rows);

        lastClick = new Position(y / fieldHeight, x / fieldWidth);
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

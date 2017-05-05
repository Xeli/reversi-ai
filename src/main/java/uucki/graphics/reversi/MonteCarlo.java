package uucki.graphics.reversi;

import java.util.Map;
import java.util.Set;
import java.awt.Graphics;
import java.awt.Color;

import uucki.type.Position;

public class MonteCarlo {

    private Set<Map.Entry<Position, Double>> probabilities = null;

    public void paint(int fieldWidth, int fieldHeight, Graphics g) {
        if(probabilities == null) {
            return;
        }

        for(Map.Entry<Position, Double> entry : probabilities) {
            Position position = entry.getKey();
            Double probability = entry.getValue();
            int normalized = (int)Math.round(probability * 128) + 128;
            g.setColor(new Color(normalized, 0, 0));
            g.fillRect(position.column * fieldWidth, position.row * fieldHeight, fieldWidth, fieldHeight);
        }
    }

    public void update(Map<Position, Double> probabilities) {
        this.probabilities = probabilities.entrySet();
    }
}

package uucki.algorithm;

import uucki.game.reversi.Board;
import uucki.type.Move;
import uucki.type.FieldValue;

public abstract class Algorithm{
    public abstract Move run(Board board, FieldValue turn);
}

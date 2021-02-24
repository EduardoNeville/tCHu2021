package ch.epfl.tchu.game;

import java.util.List;

/**
 * Class meant to enumerate all possible colors of cards
 * @author eduardoneville
 */
public enum Color {
    BLACK(1),
    VIOLET(2),
    BLUE(3),
    GREEN(4),
    YELLOW(5),
    ORANGE(6),
    RED(7),
    WHITE(8);

    public final int value;
    Color (final int valueArg){
        this.value = valueArg;
    }
}
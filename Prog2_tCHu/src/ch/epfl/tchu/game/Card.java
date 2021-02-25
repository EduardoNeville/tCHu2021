package ch.epfl.tchu.game;
import java.util.List;

/**
 * Card Class
 *
 * @author Eduardo Neville
 */
public enum Card {
        BLACK,
        VIOLET,
        BLUE,
        GREEN,
        YELLOW,
        ORANGE,
        RED,
        WHITE;

    public static final List<Card> ALL = List.of(values());

    public static final int COUNT = ALL.size();

}
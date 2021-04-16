package ch.epfl.tchu.game;

import java.util.List;

/**
 * Card Enumerated Class of nine different cards, eight colored and one with no color.
 *
 * @author Eduardo Neville (314667)
 * @author Martin Sanchez Lopez (313238)
 */
public enum Card {

    BLACK(Color.BLACK),
    VIOLET(Color.VIOLET),
    BLUE(Color.BLUE),
    GREEN(Color.GREEN),
    YELLOW(Color.YELLOW),
    ORANGE(Color.ORANGE),
    RED(Color.RED),
    WHITE(Color.WHITE),
    LOCOMOTIVE(null);

    private final Color cardColor;

    Card(Color cardColor) {
        this.cardColor = cardColor;
    }

    public static final List<Card> ALL = List.of(values());

    public static final int COUNT = ALL.size();

    public static final List<Card> CARS = List.of(
            BLACK,
            VIOLET,
            BLUE,
            GREEN,
            YELLOW,
            ORANGE,
            RED,
            WHITE);

    /**
     * Returns the card associated with the given color.
     *
     * @param color color of the card to be returned
     * @return the card that has <code>color</code> as its color
     */
    public static Card of(Color color) {
        return Card.valueOf(color.toString());
    }

    /**
     * Returns the color of this card null if it is a locomotive card.
     *
     * @return the color of this card or null if it is a locomotive card
     */
    public Color color() {
        return this.cardColor;
    }

}

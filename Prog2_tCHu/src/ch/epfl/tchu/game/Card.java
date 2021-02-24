package ch.epfl.tchu.game;
import java.util.List;

public class Card {
    private enum Cards {
        BLACK (1),
        VIOLET(2),
        BLUE(3),
        GREEN(4),
        YELLOW(5),
        ORANGE(6),
        RED(7),
        WHITE(8);

        private final int values;

        Cards(final int COUNT) {
            this.values = COUNT;
        }
    }
    /**
    public final String[] CardArray = new String[]{
            "printemps", "été", "automne", "hiver"
    };
    List<String> seasons = List.of(CardArray);
     */
}
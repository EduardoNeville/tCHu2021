package ch.epfl.tchu.game;
import java.util.List;

public enum Card {

    BLACK(Color.BLACK), VIOLET(Color.VIOLET), BLUE(Color.BLUE), GREEN(Color.GREEN), YELLOW(Color.YELLOW), ORANGE(Color.ORANGE), RED(Color.RED), WHITE(Color.WHITE), LOCOMOTIVE(null);

    private final Color cardColor;

    Card(Color cardColor) {
        this.cardColor = cardColor;
    }

    public static final List<Card> ALL = List.of(values());

    public static final int COUNT = ALL.size();

    public static final List<Card> CARS = List.of(BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE);

    public static Card of(Color color){

        return Card.valueOf(color.toString());
    }

    public Color color(){
        return this.cardColor;
    }

    }

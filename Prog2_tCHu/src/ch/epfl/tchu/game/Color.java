package ch.epfl.tchu.game;

    //The enumerated type Color represents the eight colors used in the game to color railcar cards and roads.

import java.util.List;

public enum Color {

    BLACK, VIOLET, BLUE, GREEN, YELLOW, ORANGE, RED, WHITE;

    public static final List<Color> ALL = List.of(values());

    public static final int COUNT = ALL.size();

}

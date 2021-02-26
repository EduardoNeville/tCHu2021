package ch.epfl.tchu.game;


/**
 * Route Class
 *
 * @author Eduardo Neville
 */
public final class Routes {
    public enum Level{
    OVERGROUND,
    UNDERGROUND;
    }

    private final String IDRoute;
    private final Station Station1;
    private final Station Station2;
    private final int length;
    private final Color color;


    public Routes(String IDRoute, Station station1, Station station2, int length,  Color color) {
        this.IDRoute = IDRoute;
        Station1 = station1;
        Station2 = station2;
        this.length = length;
        this.color = color;
    }

    /**
     * Getter for ID of the route
     * @return ID of route
     */
    public String getId() {
        return IDRoute;
    }

    /**
     * Getter for the 1st Station of the route
     * @return 1st Station of the route
     */
    public Station getStation1() {
        return Station1;
    }

    /**
     * Getter for the Last Station of the route
     * @return Last Station of the route
     */
    public Station getStation2() {
        return Station2;
    }

    /**
     * Getter for the length of a route
     * @return The length of the route
     */
    public int getLength() {
        return length;
    }

    /**
     * Getter for the color of a route
     * @return Color of the route
     */
    public Color getColor() {
        // return null if color neutral?
        return color;
    }
}

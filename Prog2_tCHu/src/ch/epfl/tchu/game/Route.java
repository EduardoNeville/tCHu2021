package ch.epfl.tchu.game;


/**
 * Route Class
 *
 * @author Eduardo Neville
 */
public final class Route {

    public enum Level{
    OVERGROUND,
    UNDERGROUND
    }

    private final String id;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final Color color;
    private final Level level;


    public Route(String id, Station station1, Station station2, int length, Level level,  Color color) {
        this.id = id;
        this.station1 = station1;
        this.station2 = station2;
        this.level = level;
        this.length = length;
        this.color = color;
    }

    /**
     * Getter for ID of the route
     * @return ID of route
     */
    public String id() {
        return id;
    } //name change

    /**
     * Getter for the departing Station of the route
     * @return departing Station of the route
     */
    public Station station1() {
        return station1;
    } //name change

    /**
     * Getter for the arriving Station of the route
     * @return arriving Station of the route
     */
    public Station station2() {
        return station2;
    } //name change

    /**
     * Getter for the length of a route
     * @return The length of the route
     */
    public int length() {
        return length;
    } //name change

    /**
     * Getter for the level of a route
     * @return The level of the route
     */
    public Level level() {return level;} //name change

    /**
     * Getter for the color of a route
     * @return Color of the route
     */
    public Color color() { //name change
        // return null if color neutral?
        return color;}
}

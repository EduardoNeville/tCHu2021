package ch.epfl.tchu.game;

/**
 * Trail Class
 *
 * @author Eduardo Neville
 */
public final class Trail {

    private final Station Station1;
    private final Station Station2;
    private final int length;

    public Trail(Station station1, Station station2, int length) {
        Station1 = station1;
        Station2 = station2;
        this.length = length;
    }

    //TODO Create Trail longest(List<Route> routes) method its supposed to be statique

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


}

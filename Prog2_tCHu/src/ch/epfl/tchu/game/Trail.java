package ch.epfl.tchu.game;

import java.util.List;

/**
 * Trail Class
 *
 * @author Eduardo Neville
 */
public final class Trail {

    private final Station station1;
    private final Station station2;
    private final int length;

    private Trail(Station station1, Station station2, int length) {
        this.station1 = station1; //maybe change as well
        this.station2 = station2;
        this.length = length;
    }

    //TODO Create Trail longest(List<Route> routes) method its supposed to be statique

    public static Trail longest(List<Route> routes){
        Trail longestRoute = null;
        //posible for loop here;
        return longestRoute;
    }
    }

    /**
     * Getter for the 1st Station of the route
     * @return 1st Station of the route
     */
    public Station station1() {
        if (length<=0){return null;}
        return Station1;
    }

    /**
     * Getter for the Last Station of the route
     * @return Last Station of the route
     */
    public Station station2() {
        if (length<=0){return null;}
        return Station2;
    }

    /**
     * Getter for the length of a route
     * @return The length of the route
     */
    public int length() { //name changed
        return length;
    }


}

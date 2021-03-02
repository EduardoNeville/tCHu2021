package ch.epfl.tchu.game;
import java.util.*;

/**
 * Trip Class
 *LIST<TRIP> METHOD IS MISSING
 *Trio.POINTS Is messed up i think
 *
 * @author Eduardo Neville
 */
public final class Trip {
    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Trip method brings all the info of a given trip
     * @param from Departing station
     * @param to Arriving station
     * @param points Points of the trip
     */
    public Trip(Station from, Station to, int points){
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Getter for the Departing station
     * @return Departing station
     */
    final Station from(){
        return from;
    }

    /**
     * Getter for the arriving station
     * @return Arriving station
     */
    final Station to(){
        return to;
    }

    /**
     * Getter for the # of points the trip has
     * @return Points of the trip
     */
    final int points(){ 
        return points; //ARE YOU SURE?
    }

    /**
     * Getter for the # of points of the trips connectivity
     * @param connectivity
     * @return Points of the trips connectivity
     */
    final int points(StationConnectivity connectivity){
        return points(connectivity);
    }
}

package ch.epfl.tchu.game;
import ch.epfl.tchu.Preconditions;

import java.util.*;

/**
 * Trip Class
 *LIST<TRIP> METHOD IS MISSING
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

    //created this method
    /**
     * Gives all possible trips that can be take from a given station (from)
     * @param from List of all possible starting station
     * @param to List of all possible finishing station
     * @param points The points that you can obtain from said trip
     * @return List of all possible train trips you can take.
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){

        List<Trip> PossibleTrips = new ArrayList<>();
        //loop through all possible routes and if connectivity cool
        for (Station station: from) {
            for (Station station1 : to) {
                if () { //connectivity is cool then add to list
                    PossibleTrips.add(new Trip(station, station1,)); //points could be received with a method
                }
            }
        }
        Preconditions.checkArgument(!PossibleTrips.isEmpty());
        return PossibleTrips;
    }

    /**
     * Getter for the Departing station
     * @return Departing station
     */
    public Station from(){
        return from;
    }

    /**
     * Getter for the arriving station
     * @return Arriving station
     */
    public Station to(){
        return to;
    }

    /**
     * Getter for the # of points the trip has
     * @return Points of the trip
     */
    public int points(){
        return points; //ARE YOU SURE? This is just a getter
    }

    //re did this method
    //is this a fix?
    /**
     * The # of points of the trips connectivity
     * @param connectivity
     * @return Points of the trips connectivity
     */
    public int points(StationConnectivity connectivity) {
        if (connectivity.connected(from, to)) {
            return points;
        }
        else {
            return 0;
        }
    }
}

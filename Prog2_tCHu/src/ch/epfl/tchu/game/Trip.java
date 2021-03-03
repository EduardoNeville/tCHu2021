package ch.epfl.tchu.game;
import ch.epfl.tchu.Preconditions;

import java.util.*;

//Trip CLass

public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Trip method brings all the info of a given trip
     *
     * @param from   Departing station must be non null
     * @param to     Arriving station must be non null
     * @param points Points of the trip must be >0
     */

    public Trip(Station from, Station to, int points) {
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Gives all possible trips that can be take from a given station (from)
     *
     * @param from   List of all possible starting station
     * @param to     List of all possible finishing station
     * @param points The points that you can obtain from said trip
     * @return List of all possible train trips you can take.
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points) {

        //THROW UNE ILLEGAL EXCEPTION SI UN ELEMENT DE FROM = UN ELEMENT DE TO AVANT D'AJOUTER AU TABLEAU DES TRAJETS
        List<Trip> PossibleTrips = new ArrayList<>();
        //loop through all possible routes and if connectivity cool
        for (Station station : from) {
            for (Station station1 : to) {
                if (from != to) { //connectivity is cool then add to list
                    PossibleTrips.add(new Trip(station, station1, points)); //points could be received with a method
                }
            }
        }
        Preconditions.checkArgument(!PossibleTrips.isEmpty() || !(points > 0));
        return PossibleTrips;
    }

    /**
     * Getter for the Departing station
     *
     * @return Departing station
     */

    public Station from() {
        return from;
    } //ADDED METHOD

    /**
     * Getter for the arriving station
     *
     * @return Arriving station
     */
    public Station to() {
        return to;
    }

    /**
     * Getter for the # of points the trip has
     *
     * @return Points of the trip
     */

    public int points() { return points; }

    /**
     * Getter for the # of points of the trips connectivity
     *
     * @param connectivity
     * @return Points of the trips connectivity
     */

    public int points(StationConnectivity connectivity) {
        if (connectivity.connected(from, to)) {
            return points;
        } else {
            return -points;
        }
    }
}

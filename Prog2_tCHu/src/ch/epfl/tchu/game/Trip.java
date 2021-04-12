package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Trip Class
 *
 * Constructor: Trip
 * Getters: from, to, points
 * Methods: all, points
 *
 * @author Martin Sanchez Lopez (313238)
 */
public final class Trip {

    private final Station from;
    private final Station to;
    private final int points;

    /**
     * Construct a Trip with a 'from' Station, a 'to' Station and an atributed number of points.
     *
     * @param from
     *          from Station
     * @param to
     *          to Station
     * @param points
     *          points worth of completing the Trip
     * @throws IllegalArgumentException
     *          if the number of points is lower or equal to 0
     * @throws NullPointerException
     *          if from is null
     * @throws NullPointerException
     *          if to is null
     */
    public Trip(Station from, Station to, int points){
        Preconditions.checkArgument(points > 0);
        this.from = Objects.requireNonNull(from);
        this.to = Objects.requireNonNull(to);
        this.points = points;
    }

    /**
     * Return list of trip, for each 'from' station to every 'to' station, as long as they are not the same.
     *
     * @param
     *      from From Station for the trip
     * @param
     *      to To Station for the trip
     * @param
     *      points Points that the trip rewards
     * @return ArrayList of all Trips
     */
    public static List<Trip> all(List<Station> from, List<Station> to, int points){
        ArrayList<Trip> allTrips = new ArrayList<>();

        for (Station f: from) {
            for (Station t : to) {
                //no trip from a station to that same station
                if ( !f.name().equals(t.name()) ) {
                    allTrips.add(new Trip(f, t, points));
                }
            }
        }
        return allTrips;
    }

    /**
     * Returns the 'from' Station of this Trip.
     * @return the 'from' Station of this Trip
     */
    public Station from(){
        return from;
    }

    /**
     * Returns the 'to' Station of this Trip.
     * @return the 'to' Station of this Trip
     */
    public Station to(){
        return to;
    }

    /**
     * Returns the points this Trip rewards.
     * @return the points this Trip rewards
     */
    public int points(){
        return points;
    }

    /**
     * Returns the points of this Trip for a given connectivity.
     *
     * @param connectivity
     *              connectivity
     * @return the points of this Trip for a given connectivity
     */
    public int points(StationConnectivity connectivity){
        if(connectivity.connected(from, to)){
            return points;
        }
        else{
            return -points;
        }
    }
}

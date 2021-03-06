package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Trail Class
 *
 * Constructor: Trail
 * Getters: toString, station1, station2, length
 * Methods: longest
 *
 * @author Eduardo Neville
 */
public final class Trail {

    private final Station station1;
    private final Station station2;
    private final int length;
    private final List<Route> routes;

    private Trail(Station station1, Station station2, List<Route> routes, int length) {
        this.station1 = station1; //maybe change as well
        this.station2 = station2; //this(List.of(... doesn't work as we would have to add an id
        this.length = length;
        this.routes = routes;
    }

    /**
     * Method used to find the longest possible trail from Station1 to Station2 given
     * the routes possible to take.
     * @param routes possible routes that the trail can take
     * @return Longest trail possible
     */
    public static Trail longest(List<Route> routes) {
        Trail longestTrail = new Trail(null, null, new ArrayList<>(), 0);

        List<Trail> PossibleRoutes = new ArrayList<>();

            for (Route route : routes){
                    PossibleRoutes.add(new Trail(route.station1(), route.station2(), List.of(route), route.length()));
                    PossibleRoutes.add(new Trail(route.station2(), route.station1(), List.of(route), route.length()));
            }

        while(!PossibleRoutes.isEmpty()){
            List<Trail> PossibleTrails = new ArrayList<>(); //Possible trails that we will eventually have routes added to them till max length of each trail
            for (Trail trail : PossibleRoutes){ //loop trails that are in PossibleRoutes
                List<Route> PlayerRoutes = new ArrayList<>(routes); //that belong to player,don't belong to PossibleRoutes,
                PlayerRoutes.removeAll(trail.routes);
                for (Route route1 : PlayerRoutes){ //For all routes in List<Route> of prev line
                    for (Station station1:route1.stations()) {
                        if (station1.equals(trail.station2)) {
                            List<Route> extended = new ArrayList<>(routes);
                            extended.add(route1);
                            PossibleTrails.add(new Trail(trail.station1, route1.stationOpposite(station1),extended, extended.size()));
                        }
                    }
                }
            }

            PossibleRoutes = PossibleTrails;
            //The route with max length is equal to longestTrail
            for (Trail loopingTrails : PossibleRoutes) {
                if (loopingTrails.length >= longestTrail.length) {
                    longestTrail = loopingTrails;
                }
            }
        }
        return longestTrail;
    }

    //To be perfected
    public String toString() {
        return station1 + " - " + station2 + " (" + length + ") ";
    }

    /**
     * Getter for the 1st Station of the trail
     * @return 1st Station of the trail
     */
    public Station station1() {
        if (length==0){return null;}
        else {return station1;}
    }

    /**
     * Getter for the Last Station of the trail
     * @return Last Station of the trail
     */
    public Station station2() {
        if (length==0){return null;}
        else {return station2;}
    }

    /**
     * Getter for the length of a trail
     * @return The length of the trail
     */
    public int length() { //name changed
        return length;
    }
}

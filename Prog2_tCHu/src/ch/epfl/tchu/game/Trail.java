package ch.epfl.tchu.game;

import ch.epfl.tchu.Preconditions;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
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

    private static Trail EMPTYTRAIL;
    private final Station station1;
    private final Station station2;
    private final int length;
    private final List<Route> routes;

    private Trail(Station station1, Station station2, List<Route> routes, int length) {
        this.station1 = station1;
        this.station2 = station2;
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

        List<Trail> trailList = new ArrayList<>();
        for (Route route : routes){
            trailList.add(new Trail(route.station1(), route.station2(), List.of(route), route.length()));
            trailList.add(new Trail(route.station2(), route.station1(), List.of(route), route.length()));
        }

        Trail longestTrail = new Trail(null, null, new ArrayList<>(), 0);

        while(!trailList.isEmpty()){
            List<Trail> PossibleTrails = new ArrayList<>(); //Possible trails that we will eventually have routes added to them till max length of each trail

            for (Trail forTrail : trailList){ //loop trails that are in trailList
                List<Route> PlayerRoutes = new ArrayList<>(routes); //that belong to player,don't belong to trailList,
                PlayerRoutes.removeAll(forTrail.routes);

                for (Route forRoute : PlayerRoutes){ //For all routes in List<Route> of prev line
                    for (Station station1:forRoute.stations()) {

                        if (station1.equals(forTrail.station2)) {
                            List<Route> extended = new ArrayList<>(forTrail.routes);
                            extended.add(forRoute);

                            Trail newTrail = new Trail(forTrail.station1(),  forRoute.stationOpposite(forTrail.station2()),extended, forTrail.length() + forRoute.length());
                            PossibleTrails.add(newTrail);


                        }
                    }
                }

                if (forTrail.length() > longestTrail.length()){
                    longestTrail = forTrail;
                }

            }
            trailList = new ArrayList<>(PossibleTrails);
        }
        return longestTrail;
    }

    /**
     * Getter for the Display name of a trail
     * @param trail Trail we want to get a name
     * @return Display name for the trail
     */
    public String toString(Trail trail) {
        StringBuilder TrailText= new StringBuilder();
        for (Route route: routes) {
            TrailText.append(" - ").append(route.toString());
        }
       return TrailText + " (" + length +") ";
    }

    /**
     * Getter for the 1st Station of the trail
     * @return 1st Station of the trail
     */
    public Station station1() {
        if (length==0){return null;}
        else {return station1;}
    }

    public List<Route> route(){
        if (routes.size() == 0){
            return null;
        }
        return routes;
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
    public int length() {
        return length;
    }
}

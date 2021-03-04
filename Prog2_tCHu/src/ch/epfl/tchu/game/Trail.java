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

    public Trail(Station station1, Station station2, int length) {
        this.station1 = station1; //maybe change as well
        this.station2 = station2;
        this.length = length;
    }

   /**
     * Method used to find the longest possible route from Station1 to Station2 given
     * the routes possible to take.
     * @param routes
     * @return Longest route possible
     */
    public static Trail longest(List<Route> routes){
        Trail longestRoute = null;

        List<Trail> PossibleRoutes = new ArrayList<Trail>(List.of()); //elts that the route can take
        List<Trail> PossibleRoutes1 = new ArrayList<>();

        for (Route route: ){ //loop trails that are in PossibleRoutes
            // List<Route> that bellong to player,don't bellong to PossibleRoutes, (and can prolong routes in Possible Routes)?
            //
            for (){ //For all routes in List<Route> of prev line
                //add the prolonged version of PossibleRoutes by Routes in List<Route> to PossibleRoutes1
            }
        }
        //check PossibleRoutes not empty
        Preconditions.checkArgument(!PossibleRoutes1.isEmpty());

        //The route with max points is equal to longestRoute
        return longestRoute;
}

    /**
     * Getter for the 1st Station of the route
     * @return 1st Station of the route
     */
    public Station station1() {
        if (length<=0){return null;}
        return station1;
    }

    /**
     * Getter for the Last Station of the route
     * @return Last Station of the route
     */
    public Station station2() {
        if (length<=0){return null;}
        return station2;
    }

    /**
     * Getter for the length of a route
     * @return The length of the route
     */
    public int length() { //name changed
        return length;
    }


}

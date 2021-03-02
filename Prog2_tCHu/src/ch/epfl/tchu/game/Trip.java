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
        return points; //ARE YOU SURE?
    }

    /**
     * Getter for the # of points of the trips connectivity
     * @param connectivity
     * @return Points of the trips connectivity
     */
    public int points(StationConnectivity connectivity){
        return points(connectivity);        // AND NEGATIVE POINTS?
                                            // int points(StationConnectivity connectivity), qui retourne le nombre de points du trajet pour la connectivité donnée.
                                            //La seconde variante de la méthode points retourne le nombre de points du trajet si la méthode connected de la connectivité 
                                            //qu'on lui passe retourne vrai lorsqu'on l'applique aux deux gares du trajet — ce qui signifie qu'elles sont bien connectées —, 
                                            //et la négation de ce nombre de points sinon.
    }
}

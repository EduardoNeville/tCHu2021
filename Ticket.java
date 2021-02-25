package ch.epfl.tchu.game;

import java.util.ArrayList;
import java.util.List;

/**
 * Ticket Class
 *
 * @author Eduardo Neville
 */
public final class Ticket implements Comparable<Ticket>{
    private final String text;
    private final int points;


    public Ticket(String text, int points){
        this.text = text;
        this.points = points;
    }

    /**
     * Getter that gives us the Text of a ticket to later display
     * @param from Departing Station
     * @param to Arriving Station
     * @param points Points of the trip
     * @return Departing Station - to - Arriving Station (# of points)
     */
     final String text(Station from, Station to, int points){
        return from + " - " + to + " (" + points + ") ";
     }



   // List<String> Stations = new ArrayList<String>( 56);

    /**
     * Getter for the # of points of the ticket connectivity
     * @param connectivity
     * @return Points of the trips connectivity
     */
    final int points(StationConnectivity connectivity){
        return points(connectivity);
    }

    /**
     * Compare the current Ticket with another ticket??????
     * @param that The current ticket
     * @return Result if they are the same ticket or not
     */
    public int compareTo(Ticket that) {
     return this.compareTo(that);
}
}
